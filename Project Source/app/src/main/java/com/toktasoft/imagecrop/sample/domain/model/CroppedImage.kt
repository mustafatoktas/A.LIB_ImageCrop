package com.toktasoft.imagecrop.sample.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_images")
data class CroppedImage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String,
    val createdAt: Long,
)

// Key that links a gallery thumbnail to its full-screen review in the shared element transition.
val CroppedImage.sharedKey: String get() = "cropped-image-$id"
