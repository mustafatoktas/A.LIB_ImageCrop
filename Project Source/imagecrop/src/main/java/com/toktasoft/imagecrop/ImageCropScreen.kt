package com.toktasoft.imagecrop

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.toktasoft.imagecrop.internal.SegmentedCard
import com.toktasoft.imagecrop.internal.SwitchCard
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Ready-to-use Material 3 crop screen. [bitmap] stays owned by the caller.
 *
 * The screen does not close itself: [onResult] receives the new bitmap and the host decides what
 * happens next. System back is not intercepted; handle it in the host if needed.
 *
 * @param onResult Called on the main thread with the exported bitmap.
 * @param onDismiss Called when the back button in the top bar is pressed.
 * @param cropShape Initial shape. Fixed when the shape card is hidden.
 * @param cropShapes Shapes offered in the shape card, in display order. Must contain [cropShape].
 * @param showCropShapeCard Shows the shape card. It is also hidden when only one shape is offered.
 * @param showGuideLines Initial guideline state. Fixed when the guideline card is hidden.
 * @param showGuideLinesCard Shows the guideline on/off card.
 * @param showUseFullImage Shows "Use full image", which returns the whole rotated image without a
 * selection or circle mask. Hide it when the result must match the shape.
 * @param options Output size limits.
 * @param onError Called when exporting fails; the screen also shows a snackBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropScreen(
    bitmap: Bitmap,
    onResult: (Bitmap) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    cropShape: CropShape = CropShape.SQUARE,
    cropShapes: List<CropShape> = CropShape.entries,
    showCropShapeCard: Boolean = true,
    showGuideLines: Boolean = true,
    showGuideLinesCard: Boolean = true,
    showUseFullImage: Boolean = true,
    options: CropOptions = CropOptions(),
    onError: (Throwable) -> Unit = {},
) {
    require(cropShapes.isNotEmpty() && cropShapes.distinct().size == cropShapes.size) {
        "cropShapes must be non-empty and contain no duplicates."
    }
    require(cropShape in cropShapes) {
        "cropShape ($cropShape) must be one of cropShapes ($cropShapes)."
    }
    val shapeCardVisible = showCropShapeCard && cropShapes.size > 1
    val state = rememberImageCropState(bitmap, cropShape)

    LaunchedEffect(state, shapeCardVisible, cropShape, cropShapes) {
        if (!shapeCardVisible || state.cropShape !in cropShapes) state.cropShape = cropShape
    }
    var guideLinesChecked by rememberSaveable { mutableStateOf(showGuideLines) }
    val guideLines = if (showGuideLinesCard) guideLinesChecked else showGuideLines
    var busy by remember(bitmap) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackBar = remember { SnackbarHostState() }
    val errorMessage = stringResource(R.string.image_crop_error)
    val export: (fullImage: Boolean) -> Unit = { fullImage ->
        if (!busy && (fullImage || state.canCrop)) {
            busy = true
            scope.launch {
                val result = try {
                    if (fullImage) state.fullImage(options) else state.crop(options)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    onError(error)
                    snackBar.showSnackbar(errorMessage)
                    null
                } finally {
                    busy = false
                }
                result?.let(onResult)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackBar) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.image_crop_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        enabled = !busy,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.image_crop_ic_arrow_back),
                            contentDescription = stringResource(R.string.image_crop_back),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = state::reset,
                        enabled = !busy,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.image_crop_ic_restart),
                            contentDescription = stringResource(R.string.image_crop_reset),
                        )
                    }
                },
            )
        },
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp),
        ) {
            val cropper: @Composable (Modifier) -> Unit = {
                ImageCropper(
                    modifier = it.padding(8.dp),
                    state = state,
                    enabled = !busy,
                    showGuideLines = guideLines,
                )
            }
            val controls: @Composable (Modifier) -> Unit = { modifier ->
                Column(
                    modifier = modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RotationRow(
                        enabled = !busy,
                        onRotateLeft = state::rotateCounterClockwise,
                        onRotateRight = state::rotateClockwise,
                    )
                    if (shapeCardVisible)
                        SegmentedCard(
                            title = stringResource(R.string.image_crop_shape),
                            options = cropShapes,
                            selected = state.cropShape,
                            label = { shape -> stringResource(shape.labelRes) },
                            onSelect = { shape -> state.cropShape = shape },
                            enabled = !busy,
                        )
                    if (showGuideLinesCard)
                        SwitchCard(
                            title = stringResource(R.string.image_crop_guide_lines),
                            icon = R.drawable.image_crop_ic_grid_on,
                            checked = guideLinesChecked,
                            onCheckedChange = { checked -> guideLinesChecked = checked },
                            enabled = !busy,
                        )
                }
            }
            val actions: @Composable () -> Unit = {
                ActionRow(
                    busy = busy,
                    canCrop = state.canCrop,
                    showUseFullImage = showUseFullImage,
                    onUseFullImage = { export(true) },
                    onCrop = { export(false) },
                )
            }
            if (maxWidth >= 640.dp && maxWidth > maxHeight) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    cropper(
                        Modifier.weight(1f).fillMaxHeight(),
                    )
                    Column(
                        modifier = Modifier
                            .width(300.dp)
                            .fillMaxHeight(),
                    ) {
                        controls(Modifier.weight(1f).fillMaxWidth())
                        actions()
                    }
                }
            } else {
                val controlsMaxHeight = maxHeight * 0.45f
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    cropper(Modifier.weight(1f).fillMaxWidth())
                    controls(Modifier.fillMaxWidth().heightIn(max = controlsMaxHeight))
                    actions()
                }
            }
        }
    }
}

@Composable
private fun RotationRow(
    enabled: Boolean,
    onRotateLeft: () -> Unit,
    onRotateRight: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RotateButton(
            icon = R.drawable.image_crop_ic_rotate_left,
            description = R.string.image_crop_rotate_left,
            enabled = enabled,
            onClick = onRotateLeft,
        )
        Text(
            text = "90°",
            style = MaterialTheme.typography.titleMedium,
        )
        RotateButton(
            icon = R.drawable.image_crop_ic_rotate_right,
            description = R.string.image_crop_rotate_right,
            enabled = enabled,
            onClick = onRotateRight,
        )
    }
}

@Composable
private fun RotateButton(
    @DrawableRes icon: Int,
    @StringRes description: Int,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    FilledTonalIconButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            painterResource(id = icon),
            stringResource(id = description)
        )
    }
}

/** "Use full image" and the floating Crop button, fixed below the scrollable controls. */
@Composable
private fun ActionRow(
    busy: Boolean,
    canCrop: Boolean,
    showUseFullImage: Boolean,
    onUseFullImage: () -> Unit,
    onCrop: () -> Unit,
) {
    val cropEnabled = canCrop && !busy
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
    ) {
        if (showUseFullImage) {
            OutlinedButton(
                onClick = onUseFullImage,
                enabled = !busy,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.image_crop_use_full_image),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Spacer(Modifier.weight(1f))
        }
        // FloatingActionButton has no enabled state, so it is dimmed and marked disabled for accessibility.
        ExtendedFloatingActionButton(
            onClick = {
                if (cropEnabled) onCrop()
            },
            modifier = Modifier
                .alpha(if (cropEnabled || busy) 1f else 0.5f)
                .semantics { if (!cropEnabled) disabled() },
        ) {
            if (busy) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = LocalContentColor.current,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.image_crop_ic_crop),
                    contentDescription = null,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(text = stringResource(R.string.image_crop_crop))
        }
    }
}

private val CropShape.labelRes: Int
    @StringRes get() = when (this) {
        CropShape.FREE -> R.string.image_crop_shape_free
        CropShape.SQUARE -> R.string.image_crop_shape_square
        CropShape.CIRCLE -> R.string.image_crop_shape_circle
    }
