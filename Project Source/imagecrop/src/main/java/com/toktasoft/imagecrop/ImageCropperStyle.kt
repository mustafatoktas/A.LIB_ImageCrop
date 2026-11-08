package com.toktasoft.imagecrop

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class HandleStyle {
    BRACKETS,
    DOTS,
}
/**
 * Visual style of an [ImageCropper].
 *
 * @param lineColor Color of the border, guidelines and handles.
 * @param overlayColor Color drawn over the image outside the selection.
 * @param borderWidth Width of the selection border.
 * @param guideLineWidth Width of the 3×3 guidelines inside the selection.
 * @param handleStyle Shape of the resize handles.
 * @param handleWidth Stroke width of [HandleStyle.BRACKETS].
 * @param dotRadius Radius of [HandleStyle.DOTS].
 */
@Immutable
data class ImageCropperStyle(
    val lineColor: Color = Color(0xFFD1CBE2),
    val overlayColor: Color = Color.Black.copy(alpha = 0.55f),
    val borderWidth: Dp = 1.5.dp,
    val guideLineWidth: Dp = 1.dp,
    val handleStyle: HandleStyle = HandleStyle.BRACKETS,
    val handleWidth: Dp = 2.dp,
    val dotRadius: Dp = 6.dp,
) {
    init {
        require(borderWidth.isPositive() && guideLineWidth.isPositive() && handleWidth.isPositive()) {
            "Line widths must be finite and positive."
        }
        require(dotRadius.value.isFinite() && dotRadius.value >= 0f) { "Dot radius must be finite and non-negative." }
    }

    private fun Dp.isPositive() = value.isFinite() && value > 0f
}
