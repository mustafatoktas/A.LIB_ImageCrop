package com.toktasoft.imagecrop.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.toktasoft.imagecrop.HandleStyle
import com.toktasoft.imagecrop.ImageCropperStyle

internal fun DrawScope.drawSelection(
    imageBounds: Rect,
    selection: Rect,
    circular: Boolean,
    showGuideLines: Boolean,
    style: ImageCropperStyle,
) {
    val outline = Path().apply { if (circular) addOval(selection) else addRect(selection) }
    clipRect(imageBounds.left, imageBounds.top, imageBounds.right, imageBounds.bottom) {
        clipPath(outline, ClipOp.Difference) { drawRect(style.overlayColor) }
    }
    if (showGuideLines) clipPath(outline) {
        drawGuideLines(selection, style.lineColor.copy(alpha = style.lineColor.alpha * 0.8f), style.guideLineWidth.toPx())
    }
    drawPath(outline, style.lineColor, style = Stroke(style.borderWidth.toPx()))
    when (style.handleStyle) {
        HandleStyle.BRACKETS -> drawBrackets(selection, style.lineColor, style.handleWidth.toPx())
        HandleStyle.DOTS -> drawDots(selection, style.lineColor, style.dotRadius.toPx())
    }
}

private fun DrawScope.drawGuideLines(rect: Rect, color: Color, width: Float) {
    for (i in 1..2) {
        val y = rect.top + rect.height * i / 3f
        val x = rect.left + rect.width * i / 3f
        drawLine(color, Offset(rect.left, y), Offset(rect.right, y), width)
        drawLine(color, Offset(x, rect.top), Offset(x, rect.bottom), width)
    }
}

private fun DrawScope.drawDots(rect: Rect, color: Color, radius: Float) {
    listOf(rect.topLeft, rect.topRight, rect.bottomLeft, rect.bottomRight)
        .forEach { drawCircle(color, radius, it) }
}

private fun DrawScope.drawBrackets(rect: Rect, color: Color, width: Float) {
    val inset = width / 2f
    val left = rect.left - inset
    val top = rect.top - inset
    val right = rect.right + inset
    val bottom = rect.bottom + inset
    val length = minOf(20.dp.toPx(), rect.width / 4f, rect.height / 4f)
    val half = length / 2f
    val center = rect.center
    val path = Path().apply {
        moveTo(left, top + length); lineTo(left, top); lineTo(left + length, top)
        moveTo(right - length, top); lineTo(right, top); lineTo(right, top + length)
        moveTo(right, bottom - length); lineTo(right, bottom); lineTo(right - length, bottom)
        moveTo(left + length, bottom); lineTo(left, bottom); lineTo(left, bottom - length)
        moveTo(center.x - half, top); lineTo(center.x + half, top)
        moveTo(center.x - half, bottom); lineTo(center.x + half, bottom)
        moveTo(left, center.y - half); lineTo(left, center.y + half)
        moveTo(right, center.y - half); lineTo(right, center.y + half)
    }
    drawPath(path, color, style = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}
