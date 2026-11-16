package com.toktasoft.imagecrop.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

internal enum class CropHandle {
    MOVE,
    TOP_LEFT,
    TOP,
    TOP_RIGHT,
    RIGHT,
    BOTTOM_RIGHT,
    BOTTOM,
    BOTTOM_LEFT,
    LEFT,
}

internal data class PixelCrop(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
)

internal object CropGeometry {

    fun fit(
        width: Int,
        height: Int,
        viewport: IntSize,
    ): Rect {
        if (width <= 0 || height <= 0 || viewport.width <= 0 || viewport.height <= 0) return Rect.Zero
        val scale = min(viewport.width.toFloat() / width, viewport.height.toFloat() / height)
        val w = width * scale
        val h = height * scale
        val x = (viewport.width - w) / 2f
        val y = (viewport.height - h) / 2f
        return Rect(x, y, x + w, y + h)
    }

    fun initial(
        bounds: Rect,
        ratio: Float?,
    ): Rect {
        if (bounds.isEmpty) return Rect.Zero
        var w = bounds.width * .85f
        var h = bounds.height * .85f
        if (ratio != null) { w = min(w, h * ratio); h = w / ratio }
        return Rect(bounds.center.x - w / 2, bounds.center.y - h / 2,
            bounds.center.x + w / 2, bounds.center.y + h / 2)
    }

    fun normalize(
        rect: Rect,
        bounds: Rect,
    ): Rect = Rect(
        (rect.left - bounds.left) / bounds.width, (rect.top - bounds.top) / bounds.height,
        (rect.right - bounds.left) / bounds.width, (rect.bottom - bounds.top) / bounds.height,
    )

    fun restore(
        rect: Rect,
        bounds: Rect,
    ): Rect = Rect(
        bounds.left + rect.left * bounds.width, bounds.top + rect.top * bounds.height,
        bounds.left + rect.right * bounds.width, bounds.top + rect.bottom * bounds.height,
    )

    fun hitTest(
        rect: Rect,
        point: Offset,
        radius: Float,
    ): CropHandle? {
        if (rect.isEmpty) return null
        if ((point - rect.center).getDistance() < min(rect.width, rect.height) * .2f) return CropHandle.MOVE
        val handles = listOf(
            CropHandle.TOP_LEFT to rect.topLeft, CropHandle.TOP_RIGHT to rect.topRight,
            CropHandle.BOTTOM_LEFT to rect.bottomLeft, CropHandle.BOTTOM_RIGHT to rect.bottomRight,
            CropHandle.TOP to Offset(rect.center.x, rect.top),
            CropHandle.RIGHT to Offset(rect.right, rect.center.y),
            CropHandle.BOTTOM to Offset(rect.center.x, rect.bottom),
            CropHandle.LEFT to Offset(rect.left, rect.center.y),
        )
        val nearest = handles.minBy { (_, position) -> (point - position).getDistanceSquared() }
        if ((point - nearest.second).getDistance() <= radius) return nearest.first
        return if (rect.contains(point)) CropHandle.MOVE else null
    }

    fun drag(
        rect: Rect,
        handle: CropHandle,
        delta: Offset,
        bounds: Rect,
        ratio: Float?,
        minSize: Float,
    ): Rect {
        if (rect.isEmpty || bounds.isEmpty || !delta.x.isFinite() || !delta.y.isFinite()) return rect
        if (handle == CropHandle.MOVE) {
            val x = (rect.left + delta.x).coerceIn(bounds.left, (bounds.right - rect.width).coerceAtLeast(bounds.left))
            val y = (rect.top + delta.y).coerceIn(bounds.top, (bounds.bottom - rect.height).coerceAtLeast(bounds.top))
            return Rect(x, y, x + rect.width, y + rect.height)
        }
        val left = handle in listOf(CropHandle.LEFT, CropHandle.TOP_LEFT, CropHandle.BOTTOM_LEFT)
        val right = handle in listOf(CropHandle.RIGHT, CropHandle.TOP_RIGHT, CropHandle.BOTTOM_RIGHT)
        val top = handle in listOf(CropHandle.TOP, CropHandle.TOP_LEFT, CropHandle.TOP_RIGHT)
        val bottom = handle in listOf(CropHandle.BOTTOM, CropHandle.BOTTOM_LEFT, CropHandle.BOTTOM_RIGHT)
        if (ratio == null) {
            val minW = min(minSize, min(bounds.width, rect.width))
            val minH = min(minSize, min(bounds.height, rect.height))
            return Rect(
                if (left) (rect.left + delta.x).coerceIn(bounds.left, rect.right - minW) else rect.left,
                if (top) (rect.top + delta.y).coerceIn(bounds.top, rect.bottom - minH) else rect.top,
                if (right) (rect.right + delta.x).coerceIn(rect.left + minW, bounds.right) else rect.right,
                if (bottom) (rect.bottom + delta.y).coerceIn(rect.top + minH, bounds.bottom) else rect.bottom,
            )
        }
        val sx = if (left) -1f else 1f
        val sy = if (top) -1f else 1f
        val horizontalOnly = !top && !bottom
        val verticalOnly = !left && !right
        val anchorX = when { left -> rect.right; right -> rect.left; else -> rect.center.x }
        val anchorY = when { top -> rect.bottom; bottom -> rect.top; else -> rect.center.y }
        val availableW = when {
            verticalOnly -> 2 * min(anchorX - bounds.left, bounds.right - anchorX)
            left -> anchorX - bounds.left
            else -> bounds.right - anchorX
        }
        val availableH = when {
            horizontalOnly -> 2 * min(anchorY - bounds.top, bounds.bottom - anchorY)
            top -> anchorY - bounds.top
            else -> bounds.bottom - anchorY
        }
        val maxW = min(availableW, availableH * ratio).coerceAtLeast(0f)
        val requestedW = when {
            horizontalOnly -> rect.width + delta.x * sx
            verticalOnly -> (rect.height + delta.y * sy) * ratio
            abs(delta.x) >= abs(delta.y * ratio) -> rect.width + delta.x * sx
            else -> (rect.height + delta.y * sy) * ratio
        }
        val w = requestedW.coerceIn(min(minSize * maxOf(1f, ratio), maxW), maxW)
        val h = w / ratio
        val x = when { verticalOnly -> anchorX - w / 2; left -> anchorX - w; else -> anchorX }
        val y = when { horizontalOnly -> anchorY - h / 2; top -> anchorY - h; else -> anchorY }
        return Rect(x, y, x + w, y + h)
    }

    fun pixels(
        rect: Rect,
        bounds: Rect,
        width: Int,
        height: Int,
        square: Boolean,
    ): PixelCrop {
        require(!rect.isEmpty && !bounds.isEmpty) { "The crop view must be laid out before cropping." }
        val n = normalize(rect, bounds)
        val x = (n.left * width).roundToInt().coerceIn(0, width - 1)
        val y = (n.top * height).roundToInt().coerceIn(0, height - 1)
        var w = ((n.right * width).roundToInt() - x).coerceIn(1, width - x)
        var h = ((n.bottom * height).roundToInt() - y).coerceIn(1, height - y)
        if (square) { val side = min(w, h); w = side; h = side }
        return PixelCrop(x, y, w, h)
    }
}
