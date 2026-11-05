package com.toktasoft.imagecrop.sample.presentation.main

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toktasoft.imagecrop.sample.domain.model.CroppedImage
import com.toktasoft.imagecrop.sample.domain.model.infoCards
import com.toktasoft.imagecrop.sample.domain.uc.CroppedImageUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.toktasoft.imagecrop.sample.presentation.main.MainContract.UiState
import com.toktasoft.imagecrop.sample.presentation.main.MainContract.UiAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainVM @Inject constructor(
    private val croppedImageUC: CroppedImageUseCases,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private fun updateUiState(copy: UiState.() -> UiState) = _uiState.update(copy)

    init {
        getAllImages()
        starterAnimations()
    }

    fun onAction(it: UiAction) = when (it) {
        is UiAction.DeleteAllImages -> deleteAllImages()
        is UiAction.SaveBitmap -> saveBitmap(it.bitmap)
        is UiAction.DeleteImage -> deleteImage(it.image)
        is UiAction.ChangeCropScreenVisibility ->
            updateUiState { copy(isCropping = it.visibility) }
        is UiAction.ChangeDeleteAllDialogVisibility ->
            updateUiState { copy(deleteAllDialogVisibility = it.visibility) }
        is UiAction.ChangeReviewImageScreenVisibility ->
            // The image stays selected after closing so the review can animate back into its grid cell.
            updateUiState { copy(isReviewing = it.visibility, selectedImage = if (it.visibility) it.image else selectedImage) }
        is UiAction.ChangeSelectedBitmap ->
            updateUiState { copy(selectedBitmap = it.bitmap) }
        is UiAction.ChangeCropSettings ->
            updateUiState { copy(cropSettings = it.settings) }
        is UiAction.ResetCropSettings ->
            updateUiState { copy(cropSettings = MainContract.CropSettings()) }
    }

    private fun getAllImages() {
        viewModelScope.launch {
            croppedImageUC.getAllCroppedImages()
                .flowOn(Dispatchers.IO)
                .collect { images ->
                updateUiState { copy(images = images) }
            }
        }
    }

    private fun starterAnimations() {
        viewModelScope.launch {
            delay(100.milliseconds)
            infoCards.indices.forEach { index ->
                updateUiState { copy(visibleCardCount = index + 1) }
                delay(300.milliseconds)
                if (index == 1)  updateUiState { copy(imageGalleryCardVisibility = true) }
            }
        }
    }

    private fun deleteAllImages() {
        viewModelScope.launch {
            croppedImageUC.deleteAllCroppedImages()
                .onSuccess { println("All Images Deleted Successfully") }
                .onFailure { println("All Images Deletion Failed ${it.message}") }
        }
    }

    private fun saveBitmap(
        bitmap: Bitmap,
    ) {
        viewModelScope.launch {
            croppedImageUC.addCroppedImage(bitmap)
                .onSuccess {
                    updateUiState { copy(selectedBitmap = createBitmap(1, 1)) }
                    println("Image Saved Successfully")
                }
                .onFailure {
                    println("Image Saving Failed ${it.message}")
                }
        }
    }

    private fun deleteImage(
        image: CroppedImage,
    ) {
        viewModelScope.launch {
            croppedImageUC.deleteCroppedImage(image)
                .onSuccess { println("Image Deleted Successfully") }
                .onFailure { println("Image Deletion Failed ${it.message}") }
        }
    }
}
