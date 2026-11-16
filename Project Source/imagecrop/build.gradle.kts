plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)

    // Maven İçin
    alias(libs.plugins.vanniktech.maven.publish)
}

group = "com.toktasoft"
version = "1.0.1"               // her release'te artır

android {
    namespace = "com.toktasoft.imagecrop"
    // Every library resource starts with this prefix so it never clashes with the host app.
    resourcePrefix = "image_crop_"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(platform(libs.androidx.bom))
    implementation(libs.androidx.material3)
    api(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.core.ktx)
}
/* ****************************************   Maven İçin   ************************************ */
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "com.toktasoft",
        artifactId = "image-crop",
        version = version.toString(),
    )

    pom {
        name.set("Image Crop")
        description.set(
            "A Jetpack Compose image cropper with bounded geometry, circle output and asynchronous export."
        )
        inceptionYear.set("2026")
        url.set("https://github.com/mustafatoktas/A.LIB_ImageCrop")
        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("mustafatoktas")
                name.set("Mustafa TOKTAŞ")
                url.set("https://toktasoft.com")
            }
            developer {
                id.set("toktasoft")
                name.set("Toktasoft")
                url.set("https://github.com/Toktasoft")
            }
        }
        organization {
            name.set("Toktasoft")
            url.set("https://toktasoft.com")
        }
        scm {
            url.set("https://github.com/mustafatoktas/A.LIB_ImageCrop")
            connection.set("scm:git:https://github.com/mustafatoktas/A.LIB_ImageCrop.git")
            developerConnection.set("scm:git:ssh://git@github.com/mustafatoktas/A.LIB_ImageCrop.git")
        }
        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/mustafatoktas/A.LIB_ImageCrop/issues")
        }
    }
}
