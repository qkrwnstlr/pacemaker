plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ssafy.pacemaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ssafy.pacemaker"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.7"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.material3.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.wear)
    implementation(libs.androidx.wear.tooling.preview)

    // Add support for wearable specific inputs
    implementation(libs.androidx.wear.input)
    implementation(libs.androidx.wear.input.testing)

    // Use to implement wear ongoing activities
    implementation(libs.androidx.wear.ongoing)

    // Use to implement support for interactions from the Wearables to Phones
    implementation(libs.androidx.wear.phone.interactions)
    // Use to implement support for interactions between the Wearables and Phones
    implementation(libs.androidx.wear.remote.interactions)

    // Add for use health service
    implementation(libs.androidx.health.services.client)

    implementation(libs.androidx.work.runtime)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.lifecycle.service)
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)
    implementation(libs.horologist.health.composables)
    implementation(libs.horologist.health.service)
    implementation(libs.horologist.roboscreenshots)

    implementation(libs.androidx.material.icons.extended)
    implementation(kotlin("script-runtime"))

    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
}