package com.toktasoft.imagecrop.sample.presentation.main.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.toktasoft.imagecrop.sample.R
import com.toktasoft.imagecrop.sample.common.extensions.noRippleClickable
import java.io.File
import kotlin.ranges.coerceIn

@Composable
fun PictureReviewSection(
    modifier: Modifier = Modifier,
    pictureURL: String,
    imageModifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 6f,
    onBack: () -> Unit = {},
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    BackHandler(onBack = onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                onBack()
            },
    ) {
        Box(
            modifier = modifier
                .align(Alignment.Center)
                .onSizeChanged { containerSize = it }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(minScale, maxScale)
                        val newOffset = offset + pan

                        if (containerSize != IntSize.Zero) {
                            val maxX = ((scale * containerSize.width) - containerSize.width) / 2f
                            val maxY = ((scale * containerSize.height) - containerSize.height) / 2f
                            offset = Offset(
                                x = newOffset.x.coerceIn(-maxX, maxX),
                                y = newOffset.y.coerceIn(-maxY, maxY),
                            )
                        } else
                            offset = newOffset
                    }
                }.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                ).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {},
                )
        ) {
            // The gallery thumbnail is shown at once from memory while the full image loads,
            // so the shared element transition has an image to grow from.
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(pictureURL))
                    .placeholderMemoryCacheKey(pictureURL)
                    .build(),
                contentDescription = null,
                error = painterResource(R.drawable.img_error),
                modifier = imageModifier.fillMaxWidth(),
            )
        }
    }
}
