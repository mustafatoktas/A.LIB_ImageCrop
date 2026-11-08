package com.toktasoft.imagecrop

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.toktasoft.imagecrop.internal.CropGeometry
import com.toktasoft.imagecrop.internal.CropHandle
import com.toktasoft.imagecrop.internal.CropRequest
import com.toktasoft.imagecrop.internal.PixelCrop
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Selection, shape and rotation of one [ImageCropper]. Create it with [rememberImageCropState].
 *
 * The state never modifies or recycles [bitmap]; the caller keeps ownership of it. Use one state
 * per [ImageCropper], and call its members from the main thread.
 */
@Stable
class ImageCropState(
    val bitmap: Bitmap,
    cropShape: CropShape = CropShape.FREE,
    aspectRatio: Float? = null,
) {
    init {
        require(!bitmap.isRecycled) { "The source bitmap has been recycled." }
        requireValidRatio(aspectRatio)
    }

    private var shape by mutableStateOf(cropShape)
    private var ratioOverride by mutableStateOf(aspectRatio)

    /** Selection shape. Changing it resets the selection. */
    var cropShape: CropShape
        get() = shape
        set(value) {
            if (value == shape) return
            shape = value
            reset()
        }

    /**
     * Fixed width / height ratio for [CropShape.FREE], or `null` for a free selection.
     * [CropShape.SQUARE] and [CropShape.CIRCLE] always use 1:1. Changing it resets the selection.
     */
    var aspectRatio: Float?
        get() = ratioOverride
        set(value) {
            requireValidRatio(value)
            if (value == ratioOverride) return
            ratioOverride = value
            reset()
        }

    /** Clockwise rotation of the image: 0, 90, 180 or 270. */
    var rotationDegrees: Int by mutableIntStateOf(0)
        private set

    /** `true` once the cropper has been laid out and [crop] can be called. */
    val canCrop: Boolean get() = !imageBounds.isEmpty && !selection.isEmpty && !bitmap.isRecycled

    /** Rotates the image 90° clockwise and resets the selection. */
    fun rotateClockwise() = rotate(1)

    /** Rotates the image 90° counter-clockwise and resets the selection. */
    fun rotateCounterClockwise() = rotate(-1)

    /** Restores the default selection: centered, 85% of the image. */
    fun reset() {
        pendingSelection = null
        selection = CropGeometry.initial(imageBounds, ratio)
        activeHandle = null
    }

    /**
     * Exports the selection as a new bitmap on [Dispatchers.Default]. [CropShape.CIRCLE] produces
     * transparent corners.
     *
     * @throws IllegalStateException if called before the cropper is laid out (see [canCrop]).
     */
    suspend fun crop(options: CropOptions = CropOptions()): Bitmap = render(request(options))

    /** Exports the whole image with the current rotation, without the selection or circle mask. */
    suspend fun fullImage(options: CropOptions = CropOptions()): Bitmap =
        render(CropRequest(bitmap, PixelCrop(0, 0, imageWidth, imageHeight), rotationDegrees, false, options))

    // region Internal: layout, gestures and export, used by ImageCropper.

    /** Image area inside the viewport, after rotation. */
    internal var imageBounds by mutableStateOf(Rect.Zero)
        private set

    /** Selection in viewport pixels, always inside [imageBounds]. */
    internal var selection by mutableStateOf(Rect.Zero)
        private set

    internal var touchRadius = 24f
    internal var minimumSize = 48f
    private var viewport = IntSize.Zero
    private var activeHandle: CropHandle? = null
    private var lastPoint = Offset.Zero

    /** Normalized selection waiting for the first layout, e.g. after process recreation. */
    private var pendingSelection: Rect? = null

    internal val imageWidth get() = if (rotationDegrees % 180 == 0) bitmap.width else bitmap.height
    internal val imageHeight get() = if (rotationDegrees % 180 == 0) bitmap.height else bitmap.width
    private val ratio get() = if (shape == CropShape.FREE) ratioOverride else 1f

    internal fun onViewportChanged(size: IntSize) {
        if (viewport == size) return
        val normalized = normalizedSelection()
        viewport = size
        imageBounds = CropGeometry.fit(imageWidth, imageHeight, viewport)
        if (imageBounds.isEmpty) {
            pendingSelection = normalized
            selection = Rect.Zero
        } else {
            pendingSelection = null
            selection = normalized?.let { CropGeometry.restore(it, imageBounds) }
                ?: CropGeometry.initial(imageBounds, ratio)
        }
        activeHandle = null
    }

    internal fun startDrag(point: Offset) {
        activeHandle = CropGeometry.hitTest(selection, point, touchRadius)
        lastPoint = point
    }

    internal fun drag(point: Offset) {
        activeHandle?.let { selection = CropGeometry.drag(selection, it, point - lastPoint, imageBounds, ratio, minimumSize) }
        lastPoint = point
    }

    internal fun endDrag() {
        activeHandle = null
    }

    internal fun moveBy(delta: Offset) {
        selection = CropGeometry.drag(selection, CropHandle.MOVE, delta, imageBounds, ratio, minimumSize)
    }

    internal fun resizeBy(delta: Offset) {
        selection = CropGeometry.drag(selection, CropHandle.BOTTOM_RIGHT, delta, imageBounds, ratio, minimumSize)
    }

    internal fun request(options: CropOptions): CropRequest {
        check(canCrop) { "The cropper must be laid out with a valid source bitmap before cropping." }
        val crop = CropGeometry.pixels(selection, imageBounds, imageWidth, imageHeight, square = shape != CropShape.FREE)
        return CropRequest(bitmap, crop, rotationDegrees, circular = shape == CropShape.CIRCLE, options)
    }

    private fun rotate(quarterTurns: Int) {
        rotationDegrees = Math.floorMod(rotationDegrees / 90 + quarterTurns, 4) * 90
        imageBounds = CropGeometry.fit(imageWidth, imageHeight, viewport)
        reset()
    }

    private fun normalizedSelection(): Rect? =
        if (imageBounds.isEmpty || selection.isEmpty) pendingSelection else CropGeometry.normalize(selection, imageBounds)

    private suspend fun render(request: CropRequest): Bitmap {
        var result: Bitmap? = null
        try {
            return withContext(Dispatchers.Default) { request.render().also { result = it } }
        } catch (cancelled: CancellationException) {
            result?.recycle()
            throw cancelled
        }
    }

    // endregion

    internal companion object {
        private const val KEY_SHAPE = "shape"
        private const val KEY_RATIO = "ratio"
        private const val KEY_ROTATION = "rotation"
        private const val KEY_SELECTION = "selection"

        private fun requireValidRatio(ratio: Float?) =
            require(ratio == null || (ratio.isFinite() && ratio > 0f)) { "Aspect ratio must be finite and positive." }

        /** Saves shape, ratio, rotation and the normalized selection; the bitmap is supplied again on restore. */
        fun saver(bitmap: Bitmap): Saver<ImageCropState, Any> = mapSaver(
            save = { state ->
                mapOf(
                    KEY_SHAPE to state.shape.name,
                    KEY_RATIO to state.ratioOverride,
                    KEY_ROTATION to state.rotationDegrees,
                    KEY_SELECTION to state.normalizedSelection()?.let { floatArrayOf(it.left, it.top, it.right, it.bottom) },
                )
            },
            restore = { saved ->
                ImageCropState(bitmap, CropShape.valueOf(saved[KEY_SHAPE] as String), saved[KEY_RATIO] as Float?).apply {
                    rotationDegrees = saved[KEY_ROTATION] as Int
                    pendingSelection = (saved[KEY_SELECTION] as FloatArray?)?.let { Rect(it[0], it[1], it[2], it[3]) }
                }
            },
        )
    }
}

/**
 * Creates an [ImageCropState] that survives configuration changes and process recreation.
 * A new state is created when [bitmap] changes. The initial [cropShape] and [aspectRatio] are used
 * only on creation; change them later through the state.
 */
@Composable
fun rememberImageCropState(
    bitmap: Bitmap,
    cropShape: CropShape = CropShape.FREE,
    aspectRatio: Float? = null,
): ImageCropState {
    val saver = remember(bitmap) { ImageCropState.saver(bitmap) }
    return rememberSaveable(bitmap, saver = saver) { ImageCropState(bitmap, cropShape, aspectRatio) }
}
