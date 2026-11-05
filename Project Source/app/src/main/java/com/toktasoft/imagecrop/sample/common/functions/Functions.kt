package com.toktasoft.imagecrop.sample.common.functions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.Composable

@Composable
fun myImagePickerLauncher(onResult: (uri: Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri ->
        onResult(uri)
    }
)
