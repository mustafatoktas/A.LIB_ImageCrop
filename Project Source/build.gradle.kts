plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // ksp
    alias(libs.plugins.ksp) apply false

    // hilt
    alias(libs.plugins.hilt.plugin) apply false

    // room
    alias(libs.plugins.room) apply false
}
