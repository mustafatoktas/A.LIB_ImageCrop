package com.toktasoft.imagecrop.sample.app_di

import android.content.Context
import androidx.room.Room
import com.toktasoft.imagecrop.sample.data.AppDB
import com.toktasoft.imagecrop.sample.data.repository.CroppedImageRepositoryImpl
import com.toktasoft.imagecrop.sample.domain.repository.CroppedImageRepository
import com.toktasoft.imagecrop.sample.domain.uc.AddCroppedImage
import com.toktasoft.imagecrop.sample.domain.uc.CroppedImageUseCases
import com.toktasoft.imagecrop.sample.domain.uc.DeleteAllCroppedImages
import com.toktasoft.imagecrop.sample.domain.uc.DeleteCroppedImage
import com.toktasoft.imagecrop.sample.domain.uc.GetAllCroppedImages
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDB =
        Room.databaseBuilder(
            context = context,
            klass = AppDB::class.java,
            name = AppDB.ROOM_DATABASE_NAME,
        ).build()

    @Provides
    @Singleton
    fun provideCroppedImageRepository(
        appDB: AppDB,
        @ApplicationContext context: Context,
    ): CroppedImageRepository =
        CroppedImageRepositoryImpl(
            dao = appDB.croppedImageDao,
            context = context,
        )


    @Provides
    @Singleton
    fun provideCroppedImageUseCases(
        croppedImageRepository: CroppedImageRepository,
        @ApplicationContext context: Context,
    ) : CroppedImageUseCases =
        CroppedImageUseCases(
            addCroppedImage = AddCroppedImage(croppedImageRepository, context),
            deleteAllCroppedImages = DeleteAllCroppedImages(croppedImageRepository),
            deleteCroppedImage = DeleteCroppedImage(croppedImageRepository),
            getAllCroppedImages = GetAllCroppedImages(croppedImageRepository),
        )
}
