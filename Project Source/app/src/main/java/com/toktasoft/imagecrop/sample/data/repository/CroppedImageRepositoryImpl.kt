package com.toktasoft.imagecrop.sample.data.repository

import android.content.Context
import com.toktasoft.imagecrop.sample.data.dao.CroppedImageDAO
import com.toktasoft.imagecrop.sample.domain.model.CroppedImage
import com.toktasoft.imagecrop.sample.domain.repository.CroppedImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class CroppedImageRepositoryImpl(
    private val dao: CroppedImageDAO,
    private val context: Context,
) : CroppedImageRepository {
    private val mutations = Mutex()

    override suspend fun addImage(image: CroppedImage) {
        mutations.withLock {
            dao.addImage(image)
        }
    }

    override fun getAllImages(): Flow<List<CroppedImage>> = dao.getAllImages()

    override suspend fun deleteImage(image: CroppedImage) {
        withContext(Dispatchers.IO) {
            mutations.withLock {
                dao.deleteImage(image)
                deleteOwnedFile(image)
            }
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            mutations.withLock {
                val images = dao.getAllImages().first()
                dao.deleteAll()
                images.forEach(::deleteOwnedFile)
            }
        }
    }

    private fun deleteOwnedFile(image: CroppedImage) {
        val file = File(image.uri).canonicalFile
        val ownedDirectories = listOf(
            File(
                context.filesDir,
                "images",
            ).canonicalFile,
            File(
                context.cacheDir,
                "images",
            ).canonicalFile,
        )
        if (file.parentFile?.let { it in ownedDirectories } == true && file.exists()) {
            check(
                value = file.delete(),
                lazyMessage = {
                    "Cannot delete saved image: $file"
                },
            )
        }
    }
}
