package com.toktasoft.imagecrop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toktasoft.imagecrop.internal.drawSelection

@Composable
fun ImageCropper(
    state: ImageCropState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showGuideLines: Boolean = true,
    style: ImageCropperStyle = ImageCropperStyle(),
) {
    val image = remember(state.bitmap) { state.bitmap.asImageBitmap() }
    val density = LocalDensity.current
    val touchRadius = with(density) { 24.dp.toPx() }
    val minimumSize = with(density) { 48.dp.toPx() }
    SideEffect {
        state.touchRadius = touchRadius
        state.minimumSize = minimumSize
    }
    val description = stringResource(R.string.image_crop_selection)
    val moveLeft = stringResource(R.string.image_crop_move_left)
    val moveRight = stringResource(R.string.image_crop_move_right)
    val moveUp = stringResource(R.string.image_crop_move_up)
    val moveDown = stringResource(R.string.image_crop_move_down)
    val grow = stringResource(R.string.image_crop_grow)
    val shrink = stringResource(R.string.image_crop_shrink)
    val reset = stringResource(R.string.image_crop_reset)
    Canvas(
        modifier
            .onSizeChanged(state::onViewportChanged)
            .semantics {
                contentDescription = description
                if (enabled) customActions = listOf(
                    CustomAccessibilityAction(moveLeft) { state.moveBy(Offset(-touchRadius, 0f)); true },
                    CustomAccessibilityAction(moveRight) { state.moveBy(Offset(touchRadius, 0f)); true },
                    CustomAccessibilityAction(moveUp) { state.moveBy(Offset(0f, -touchRadius)); true },
                    CustomAccessibilityAction(moveDown) { state.moveBy(Offset(0f, touchRadius)); true },
                    CustomAccessibilityAction(grow) { state.resizeBy(Offset(touchRadius, touchRadius)); true },
                    CustomAccessibilityAction(shrink) { state.resizeBy(Offset(-touchRadius, -touchRadius)); true },
                    CustomAccessibilityAction(reset) { state.reset(); true },
                )
            }
            .pointerInput(state, enabled) {
                if (enabled) detectDragGestures(
                    onDragStart = state::startDrag,
                    onDrag = { change, _ ->
                        change.consume()
                        state.drag(change.position)
                    },
                    onDragEnd = state::endDrag,
                    onDragCancel = state::endDrag,
                )
            },
    ) {
        val bounds = state.imageBounds
        if (bounds.isEmpty) return@Canvas
        val scale = bounds.width / state.imageWidth
        withTransform({
            translate(bounds.center.x, bounds.center.y)
            rotate(state.rotationDegrees.toFloat(), Offset.Zero)
            scale(scale, scale, Offset.Zero)
        }) {
            drawImage(image, topLeft = Offset(-image.width / 2f, -image.height / 2f))
        }
        val selection = state.selection
        if (!selection.isEmpty) {
            drawSelection(bounds, selection, state.cropShape == CropShape.CIRCLE, showGuideLines, style)
        }
    }
}
