package com.toktasoft.imagecrop.sample.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.toktasoft.imagecrop.sample.domain.model.CroppedImage
import kotlinx.coroutines.flow.Flow

@Dao
interface CroppedImageDAO {

    @Insert
    suspend fun addImage(image: CroppedImage)

    @Query("SELECT * FROM saved_images ORDER BY createdAt DESC")
    fun getAllImages(): Flow<List<CroppedImage>>

    @Query("DELETE FROM saved_images")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteImage(image: CroppedImage)
}