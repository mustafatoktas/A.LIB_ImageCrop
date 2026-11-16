package com.toktasoft.imagecrop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import com.toktasoft.imagecrop.internal.fitSize
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageCropLoader {
    suspend fun load(
        context: Context,
        uri: Uri,
        maxDimension: Int = 4096,
        maxPixels: Int = 16_777_216,
    ): Bitmap {
        require(maxDimension > 0 && maxPixels > 0) { "Decode limits must be positive." }
        var decoded: Bitmap? = null
        try {
            return withContext(Dispatchers.IO) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                    val target = fitSize(info.size.width, info.size.height, maxDimension, maxDimension, maxPixels)
                    decoder.setTargetSize(target.width, target.height)
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.setOnPartialImageListener { false }
                }.also { decoded = it }
            }
        } catch (cancelled: CancellationException) {
            decoded?.recycle()
            throw cancelled
        }
    }
}
