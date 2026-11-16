package com.toktasoft.imagecrop.sample.presentation.main

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import com.toktasoft.imagecrop.CropShape
import com.toktasoft.imagecrop.sample.domain.model.CroppedImage

object MainContract {

    data class UiState(
        val images: List<CroppedImage> = emptyList(),
        val isReviewing: Boolean = false,
        val isCropping: Boolean = false,
        val deleteAllDialogVisibility: Boolean = false,
        val selectedBitmap: Bitmap = createBitmap(1, 1),
        val selectedImage: CroppedImage? = null,
        val cropSettings: CropSettings = CropSettings(),
        // for animation sequence
        val visibleCardCount: Int = 0,
        val imageGalleryCardVisibility: Boolean = false,
    )

    // ImageCropScreen parameters that can be changed from the sample UI
    data class CropSettings(
        val cropShapes: List<CropShape> = CropShape.entries,
        val cropShape: CropShape = CropShape.SQUARE,
        val showCropShapeCard: Boolean = true,
        val showGuideLines: Boolean = true,
        val showGuideLinesCard: Boolean = true,
        val showUseFullImage: Boolean = true,
        val maxOutputSize: Int = 4096,
    ) {
        // At least one shape stays offered and the initial shape is always one of the offered shapes.
        fun toggleShape(shape: CropShape): CropSettings {
            val shapes = CropShape.entries.filter { (it in cropShapes) != (it == shape) }
            if (shapes.isEmpty()) return this
            return copy(
                cropShapes = shapes,
                cropShape = if (cropShape in shapes) cropShape else shapes.first(),
            )
        }
    }

    sealed interface UiAction{
        data class DeleteImage(val image : CroppedImage): UiAction
        data class ChangeCropScreenVisibility(val visibility: Boolean): UiAction
        data class ChangeReviewImageScreenVisibility(val visibility: Boolean, val image: CroppedImage? = null): UiAction
        data class ChangeDeleteAllDialogVisibility(val visibility: Boolean): UiAction
        data class SaveBitmap(val bitmap: Bitmap): UiAction
        data class ChangeSelectedBitmap(val bitmap: Bitmap): UiAction
        data class ChangeCropSettings(val settings: CropSettings): UiAction
        data object ResetCropSettings: UiAction
        data object DeleteAllImages: UiAction
    }
}
