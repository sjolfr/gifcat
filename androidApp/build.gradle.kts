plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "uk.co.gifcat.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "uk.co.gifcat.android"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=com.arkivanov.decompose.ExperimentalDecomposeApi",
            "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi"
        )
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    val jetpackComposeVersion = "1.2.1"
    val material3Version = "1.0.0-beta03"
    val accompanistVersion = "0.25.1"
    val landscapistVersion = "2.0.0"

    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:$jetpackComposeVersion")
    implementation("androidx.compose.ui:ui-tooling:$jetpackComposeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$jetpackComposeVersion")
    implementation("androidx.compose.foundation:foundation:$jetpackComposeVersion")
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("com.arkivanov.decompose:extensions-compose-jetpack:1.0.0-alpha-04")
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.github.skydoves:landscapist-coil:$landscapistVersion")
    implementation("com.github.skydoves:landscapist-palette:$landscapistVersion")
}
