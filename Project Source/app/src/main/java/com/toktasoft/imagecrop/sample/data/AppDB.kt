package com.toktasoft.imagecrop.sample.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.toktasoft.imagecrop.sample.data.dao.CroppedImageDAO
import com.toktasoft.imagecrop.sample.domain.model.CroppedImage

@Database(
    entities = [CroppedImage::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDB : RoomDatabase() {

    companion object {
        const val ROOM_DATABASE_NAME = "app_database"
    }

    abstract val croppedImageDao: CroppedImageDAO
}