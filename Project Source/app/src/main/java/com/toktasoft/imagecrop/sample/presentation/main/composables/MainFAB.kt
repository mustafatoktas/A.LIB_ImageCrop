package com.toktasoft.imagecrop.sample.presentation.main.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.toktasoft.imagecrop.sample.R

@Composable
fun MainFAB(
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.select_image),
        )
    }
}
