package com.toktasoft.imagecrop.sample.presentation.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.toktasoft.imagecrop.sample.R
import com.toktasoft.imagecrop.sample.common.enums.AppLanguage
import com.toktasoft.imagecrop.sample.common.functions.slideInFromLeft
import com.toktasoft.imagecrop.sample.common.functions.slideInFromTop
import com.toktasoft.imagecrop.sample.common.functions.slideOutToLeft
import com.toktasoft.imagecrop.sample.common.functions.slideOutToTop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTAB(
    imageCount: Int,
    language: AppLanguage,
    scrollBehavior: TopAppBarScrollBehavior,
    onDeleteClick: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
) {
    var languageMenuVisibility by remember { mutableStateOf(false) }

    Column {
        LargeTopAppBar(
            scrollBehavior = scrollBehavior,
            title = {
                Column{
                    Text(
                        text = stringResource(R.string.app_name),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    AnimatedVisibility(
                        visible = imageCount > 0,
                        enter = slideInFromLeft(),
                        exit = slideOutToLeft(),
                    ) {
                        Text(
                            text = pluralStringResource(R.plurals.image_count, imageCount, imageCount),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            },
            actions = {
                AnimatedVisibility(
                    visible = imageCount > 0,
                    enter = slideInFromTop(),
                    exit = slideOutToTop(),
                ) {
                    IconButton(
                        onClick = onDeleteClick,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.delete_all),
                        )
                    }
                }
                Box {
                    IconButton(
                        onClick = { languageMenuVisibility = true },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = stringResource(R.string.language),
                        )
                    }
                    DropdownMenu(
                        expanded = languageMenuVisibility,
                        onDismissRequest = { languageMenuVisibility = false },
                    ) {
                        AppLanguage.entries.forEach { entry ->
                            DropdownMenuItem(
                                text = { Text(stringResource(entry.nameRes)) },
                                trailingIcon = {
                                    if (entry == language) {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                        )
                                    }
                                },
                                onClick = {
                                    languageMenuVisibility = false
                                    if (entry != language) onLanguageChange(entry)
                                },
                            )
                        }
                    }
                }
            }
        )
        HorizontalDivider()
    }
}
