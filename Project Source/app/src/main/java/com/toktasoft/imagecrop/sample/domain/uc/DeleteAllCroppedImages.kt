package com.toktasoft.imagecrop.sample.domain.uc

import com.toktasoft.imagecrop.sample.domain.repository.CroppedImageRepository

class DeleteAllCroppedImages(
    private val repository: CroppedImageRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        repository.deleteAll()
    }
}
