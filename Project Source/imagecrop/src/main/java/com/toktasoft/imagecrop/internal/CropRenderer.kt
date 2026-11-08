package com.toktasoft.imagecrop.internal

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import com.toktasoft.imagecrop.CropOptions
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

internal fun fitSize(
    width: Int,
    height: Int,
    maxWidth: Int,
    maxHeight: Int,
    maxPixels: Int,
): IntSize {
    val scale = min(1.0, min(min(maxWidth.toDouble() / width, maxHeight.toDouble() / height),
        sqrt(maxPixels.toDouble() / (width.toDouble() * height))))
    var w = floor(width * scale).toInt().coerceAtLeast(1)
    var h = floor(height * scale).toInt().coerceAtLeast(1)
    if (w.toLong() * h > maxPixels) {
        if (w > h) w = (maxPixels / h).coerceAtLeast(1) else h = (maxPixels / w).coerceAtLeast(1)
    }
    return IntSize(w, h)
}

internal class CropRequest(
    private val source: Bitmap,
    val crop: PixelCrop,
    private val rotation: Int,
    private val circular: Boolean,
    private val options: CropOptions,
) {
    fun render(): Bitmap {
        check(!source.isRecycled) { "The source bitmap has been recycled." }
        val size = fitSize(crop.width, crop.height, options.maxWidth, options.maxHeight, options.maxPixels)
        val software = if (source.config == Bitmap.Config.HARDWARE) {
            checkNotNull(source.copy(Bitmap.Config.ARGB_8888, false)) { "Cannot copy the hardware bitmap." }
        } else source
        var output: Bitmap? = null
        try {
            val result = createBitmap(size.width, size.height).also { output = it }
            result.density = source.density
            result.setHasAlpha(circular || source.hasAlpha())
            val matrix = Matrix().apply { setRotate(rotation.toFloat()) }
            val rotated = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat()).also(matrix::mapRect)
            matrix.postTranslate(-rotated.left - crop.left, -rotated.top - crop.top)
            matrix.postScale(size.width.toFloat() / crop.width, size.height.toFloat() / crop.height)
            val canvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
            if (circular) {
                canvas.drawOval(0f, 0f, size.width.toFloat(), size.height.toFloat(), paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            }
            canvas.drawBitmap(software, matrix, paint)
            return result
        } catch (error: Throwable) {
            output?.recycle()
            throw error
        } finally {
            if (software !== source) software.recycle()
        }
    }
}
