package com.toktasoft.imagecrop.sample.domain.repository

import com.toktasoft.imagecrop.sample.domain.model.CroppedImage
import kotlinx.coroutines.flow.Flow

interface CroppedImageRepository {
    suspend fun addImage(image: CroppedImage)
    fun getAllImages(): Flow<List<CroppedImage>>
    suspend fun deleteImage(image: CroppedImage)
    suspend fun deleteAll()
}
