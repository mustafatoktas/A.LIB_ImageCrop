package com.toktasoft.imagecrop.sample.domain.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.toktasoft.imagecrop.sample.R

data class InfoCard(
    @param:StringRes val title: Int,
    @param:StringRes val summary: Int,
    val icon: ImageVector,
    val link: String,
)

val infoCards = listOf(
    InfoCard(
        title = R.string.info_developer_name,
        summary = R.string.info_developer_summary,
        icon = Icons.Default.Person,
        link = "https://github.com/mustafatoktas",
    ),
    InfoCard(
        title = R.string.info_repository_title,
        summary = R.string.info_repository_summary,
        icon = Icons.Default.Code,
        link = "https://github.com/mustafatoktas/A.LIB_ImageCrop",
    ),
)
