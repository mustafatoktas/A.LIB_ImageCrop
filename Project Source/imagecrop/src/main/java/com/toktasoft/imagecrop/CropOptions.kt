package com.toktasoft.imagecrop

data class CropOptions(
    val maxWidth: Int = 4096,
    val maxHeight: Int = 4096,
    val maxPixels: Int = 16_777_216,
) {
    init {
        require(maxWidth > 0 && maxHeight > 0 && maxPixels > 0) { "Output limits must be positive." }
    }
}
