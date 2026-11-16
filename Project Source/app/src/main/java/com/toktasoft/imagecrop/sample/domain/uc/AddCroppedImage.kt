package com.toktasoft.imagecrop.sample.domain.uc

import android.content.Context
import android.graphics.Bitmap
import com.toktasoft.imagecrop.sample.domain.model.CroppedImage
import com.toktasoft.imagecrop.sample.domain.repository.CroppedImageRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class AddCroppedImage @Inject constructor(
    private val repository: CroppedImageRepository,
    private val context: Context,
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<Unit> = withContext(Dispatchers.IO) {
        var output: File? = null
        try {
            val directory = File(context.filesDir, "images")
            check(directory.isDirectory || directory.mkdirs()) { "Cannot create image directory." }
            val format = if (bitmap.hasAlpha()) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            val extension = if (bitmap.hasAlpha()) "png" else "jpg"
            val file = File(directory, "img_${UUID.randomUUID()}.$extension")
            output = file
            file.outputStream().use { check(bitmap.compress(format, 90, it)) { "Cannot encode image." } }
            // Finish registration atomically with respect to coroutine cancellation.
            withContext(NonCancellable) {
                repository.addImage(
                    image = CroppedImage(
                        uri = file.absolutePath,
                        createdAt = System.currentTimeMillis(),
                    ),
                )
            }
            output = null
            Result.success(Unit)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Result.failure(error)
        } finally {
            output?.delete()
        }
    }
}
