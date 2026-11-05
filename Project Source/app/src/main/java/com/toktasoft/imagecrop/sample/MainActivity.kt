package com.toktasoft.imagecrop.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.toktasoft.imagecrop.sample.common.functions.AppLocale
import com.toktasoft.imagecrop.sample.presentation.main.MainRoute
import com.toktasoft.imagecrop.sample.presentation.ui.ImageCropTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        AppLocale.initialize(this)
        enableEdgeToEdge()
        setContent {
            ImageCropTheme{
                MainRoute()
            }
        }
    }
}
