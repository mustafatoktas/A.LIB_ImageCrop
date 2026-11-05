package com.toktasoft.imagecrop.sample.domain.uc

import com.toktasoft.imagecrop.sample.domain.model.CroppedImage
import com.toktasoft.imagecrop.sample.domain.repository.CroppedImageRepository

class DeleteCroppedImage(
    private val repository: CroppedImageRepository,
) {
    suspend operator fun invoke(image: CroppedImage): Result<Unit> = runCatching {
        repository.deleteImage(image)
    }
}