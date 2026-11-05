plugins {
    alias(libs.plugins.android.application)

    // compose
    alias(libs.plugins.kotlin.compose)

    // ksp
    alias(libs.plugins.ksp)

    // hilt
    alias(libs.plugins.hilt.plugin)

    // room
    alias(libs.plugins.room)
}

android {
    namespace = "com.toktasoft.imagecrop.sample"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.toktasoft.imagecrop.sample"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
    buildFeatures {
        compose = true
    }
    androidResources {
        // Only English and Turkish ship. Library translations for other languages are dropped.
        localeFilters += listOf("en", "tr")
        generateLocaleConfig = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // modül bağımlılıkları
    implementation(project(":imagecrop"))

    implementation(platform(libs.androidx.bom))
    implementation(libs.androidx.activity)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    // hilt
    implementation(libs.hilt)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)

    //room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.common)
    ksp(libs.androidx.room.compiler)

    // icons
    implementation(libs.androidx.material.icons.extended)

    // appcompat (uygulama içi dil seçimi)
    implementation(libs.androidx.appcompat)

    // splash screen
    implementation(libs.androidx.core.splashscreen)

    // coil
    implementation(libs.coil.compose)
}
