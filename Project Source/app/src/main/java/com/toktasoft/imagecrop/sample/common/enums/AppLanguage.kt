package com.toktasoft.imagecrop.sample.common.enums

import androidx.annotation.StringRes
import com.toktasoft.imagecrop.sample.R

enum class AppLanguage(
    val tag: String,
    @StringRes val nameRes: Int,
) {
    ENGLISH("en", R.string.language_english),
    TURKISH("tr", R.string.language_turkish);

    companion object {
        val DEFAULT = ENGLISH

        fun fromTag(tag: String): AppLanguage = entries.firstOrNull { it.tag == tag } ?: DEFAULT
    }
}
