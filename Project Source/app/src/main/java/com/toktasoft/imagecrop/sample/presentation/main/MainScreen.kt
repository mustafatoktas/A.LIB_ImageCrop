package com.toktasoft.imagecrop.sample.presentation.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toktasoft.imagecrop.CropOptions
import com.toktasoft.imagecrop.ImageCropScreen
import com.toktasoft.imagecrop.sample.common.extensions.openInBrowser
import com.toktasoft.imagecrop.sample.common.functions.AppLocale
import com.toktasoft.imagecrop.sample.common.functions.currentLanguage
import com.toktasoft.imagecrop.sample.common.extensions.uriToBitmap
import com.toktasoft.imagecrop.sample.common.functions.myImagePickerLauncher
import com.toktasoft.imagecrop.sample.common.functions.slideInFromBottom
import com.toktasoft.imagecrop.sample.common.functions.slideInFromLeft
import com.toktasoft.imagecrop.sample.common.functions.slideInFromRight
import com.toktasoft.imagecrop.sample.common.functions.slideInFromTop
import com.toktasoft.imagecrop.sample.common.functions.slideOutToBottom
import com.toktasoft.imagecrop.sample.domain.model.infoCards
import com.toktasoft.imagecrop.sample.domain.model.sharedKey
import com.toktasoft.imagecrop.sample.presentation.main.composables.MainTAB
import com.toktasoft.imagecrop.sample.presentation.main.MainContract.UiState
import com.toktasoft.imagecrop.sample.presentation.main.MainContract.UiAction
import com.toktasoft.imagecrop.sample.presentation.main.composables.CropSettingsCard
import com.toktasoft.imagecrop.sample.presentation.main.composables.DeleteAllDialog
import com.toktasoft.imagecrop.sample.presentation.main.composables.ImageGalleryCard
import com.toktasoft.imagecrop.sample.presentation.main.composables.MainFAB
import com.toktasoft.imagecrop.sample.presentation.main.composables.MakeBackgroundBlur
import com.toktasoft.imagecrop.sample.presentation.main.composables.PictureReviewSection
import com.toktasoft.imagecrop.sample.presentation.main.composables.SpecialRow
import com.toktasoft.imagecrop.sample.presentation.ui.bottomDpForFAB
import com.toktasoft.imagecrop.sample.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun MainRoute() {
    val viewModel: MainVM = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SharedTransitionLayout {
        MainScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.MainScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val language = currentLanguage()
    val scope = rememberCoroutineScope()
    var loadJob by remember { mutableStateOf<Job?>(null) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    val photoPicker = myImagePickerLauncher { uri ->
        uri?.let {
            loadJob?.cancel()
            loadJob = scope.launch {
                context.uriToBitmap(it)
                    .onSuccess { bitmap ->
                        onAction(UiAction.ChangeSelectedBitmap(bitmap))
                        onAction(UiAction.ChangeCropScreenVisibility(true))
                    }
                    .onFailure {
                        Toast.makeText(context, R.string.image_load_error, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    MakeBackgroundBlur(
        conditions = uiState.deleteAllDialogVisibility ||
            uiState.isReviewing,
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MainTAB(
                    imageCount = uiState.images.size,
                    language = language,
                    scrollBehavior = scrollBehavior,
                    onDeleteClick = {
                        onAction(UiAction.ChangeDeleteAllDialogVisibility(true))
                    },
                    onLanguageChange = AppLocale::set,
                )
            },
            floatingActionButton = {
                MainFAB (
                    onClick = {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                )
            }
        ) { pv ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(horizontal = 8.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                infoCards.indices.forEach { index ->
                    AnimatedVisibility(
                        visible = index < uiState.visibleCardCount,
                        enter = if (index % 2 == 0) slideInFromLeft(300) else slideInFromRight(300),
                    ) {
                        SpecialRow(
                            modifier = if (index == 0) Modifier.padding(top = 8.dp) else Modifier,
                            title = stringResource(infoCards[index].title),
                            summary = stringResource(infoCards[index].summary),
                            icon = infoCards[index].icon,
                            onClick = {
                                context.openInBrowser(infoCards[index].link)
                            },
                        )
                    }
                }
                AnimatedVisibility(
                    visible = uiState.imageGalleryCardVisibility,
                    enter = slideInFromTop(300),
                ) {
                    CropSettingsCard(
                        settings = uiState.cropSettings,
                        onSettingsChange = { settings ->
                            onAction(UiAction.ChangeCropSettings(settings))
                        },
                        onReset = {
                            onAction(UiAction.ResetCropSettings)
                        },
                    )
                }
                AnimatedVisibility(
                    visible = uiState.imageGalleryCardVisibility,
                    enter = slideInFromTop(300),
                ) {
                    ImageGalleryCard(
                        images = uiState.images,
                        reviewedImageId = if (uiState.isReviewing) uiState.selectedImage?.id else null,
                        onImageClick = { croppedImage ->
                            onAction(UiAction.ChangeReviewImageScreenVisibility(true, croppedImage))
                        },
                        onLongImageClick = { croppedImage ->
                            onAction(UiAction.DeleteImage(croppedImage))
                        },
                    )
                }
                Spacer(modifier = Modifier.height(bottomDpForFAB))
            }
        }
    }
    AnimatedVisibility(
        visible = uiState.isCropping,
        enter = slideInFromBottom(450),
        exit = slideOutToBottom(450),
    ) {
        val settings = uiState.cropSettings
        ImageCropScreen(
            bitmap = uiState.selectedBitmap,
            onResult = { croppedBitmap ->
                Toast.makeText(
                    context,
                    resources.getString(R.string.image_saved, croppedBitmap.width, croppedBitmap.height),
                    Toast.LENGTH_SHORT,
                ).show()
                onAction(UiAction.SaveBitmap(croppedBitmap))
                onAction(UiAction.ChangeCropScreenVisibility(false))
            },
            onDismiss = {
                onAction(UiAction.ChangeCropScreenVisibility(false))
            },
            cropShape = settings.cropShape,
            cropShapes = settings.cropShapes,
            showCropShapeCard = settings.showCropShapeCard,
            showGuideLines = settings.showGuideLines,
            showGuideLinesCard = settings.showGuideLinesCard,
            showUseFullImage = settings.showUseFullImage,
            options = CropOptions(
                maxWidth = settings.maxOutputSize,
                maxHeight = settings.maxOutputSize,
            ),
        )
    }
    BackHandler(enabled = uiState.isCropping) {
        onAction(UiAction.ChangeCropScreenVisibility(false))
    }
    if (uiState.deleteAllDialogVisibility) {
        DeleteAllDialog(
            onDismiss = {
                onAction(UiAction.ChangeDeleteAllDialogVisibility(false))
            },
            onDelete =  {
                onAction(UiAction.DeleteAllImages)
            },
        )
    }
    AnimatedVisibility(
        visible = uiState.isReviewing,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        uiState.selectedImage?.let { image ->
            PictureReviewSection(
                pictureURL = image.uri,
                imageModifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(image.sharedKey),
                    animatedVisibilityScope = this@AnimatedVisibility,
                ),
                onBack = {
                    onAction(UiAction.ChangeReviewImageScreenVisibility(false))
                },
            )
        }
    }
}
