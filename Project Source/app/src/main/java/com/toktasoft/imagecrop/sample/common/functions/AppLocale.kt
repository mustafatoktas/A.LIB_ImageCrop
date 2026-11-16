package com.toktasoft.imagecrop.sample.common.functions

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import com.toktasoft.imagecrop.sample.common.enums.AppLanguage

/**
 * The app language lives in Android's per-app language setting, so it also shows in the system
 * settings. The activity handles the locale change itself, so switching restyles the screen in
 * place instead of restarting it.
 */
object AppLocale {
    private const val PREFERENCES = "app_locale"
    private const val KEY_INITIALIZED = "language_initialized"

    fun set(language: AppLanguage) =
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))

    /** English on the very first launch, unless a language was already picked in system settings. */
    fun initialize(context: Context) {
        val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        if (preferences.getBoolean(KEY_INITIALIZED, false)) return
        if (AppCompatDelegate.getApplicationLocales().isEmpty) set(AppLanguage.DEFAULT)
        preferences.edit { putBoolean(KEY_INITIALIZED, true) }
    }
}

@Composable
fun currentLanguage(): AppLanguage = AppLanguage.fromTag(LocalConfiguration.current.locales[0].language)
