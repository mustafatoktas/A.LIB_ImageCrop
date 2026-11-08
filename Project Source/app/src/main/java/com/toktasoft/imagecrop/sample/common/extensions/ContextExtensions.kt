package com.toktasoft.imagecrop.sample.common.extensions

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.toktasoft.imagecrop.ImageCropLoader
import kotlinx.coroutines.CancellationException

fun Context.openInBrowser(url: String?): Result<Unit> = runCatching {
    startActivity(Intent(
        Intent.ACTION_VIEW, url.orEmpty().toUri()
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}
suspend fun Context.uriToBitmap(uri: Uri): Result<Bitmap> = try {
    Result.success(ImageCropLoader.load(this, uri))
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (error: Exception) {
    Result.failure(error)
}
