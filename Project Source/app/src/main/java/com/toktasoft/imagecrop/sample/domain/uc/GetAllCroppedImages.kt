package com.toktasoft.imagecrop.sample.domain.uc

import com.toktasoft.imagecrop.sample.domain.model.CroppedImage
import com.toktasoft.imagecrop.sample.domain.repository.CroppedImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class GetAllCroppedImages(
    private val repository: CroppedImageRepository,
) {
    operator fun invoke(): Flow<List<CroppedImage>> = repository.getAllImages().catch { e ->
        e.printStackTrace()
        emit(emptyList())
    }
}
