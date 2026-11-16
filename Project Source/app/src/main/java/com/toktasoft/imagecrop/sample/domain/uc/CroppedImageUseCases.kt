package com.toktasoft.imagecrop.sample.domain.uc

data class CroppedImageUseCases(
    val addCroppedImage: AddCroppedImage,
    val deleteAllCroppedImages: DeleteAllCroppedImages,
    val deleteCroppedImage: DeleteCroppedImage,
    val getAllCroppedImages: GetAllCroppedImages,
)
