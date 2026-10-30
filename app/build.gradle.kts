
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.kayara"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.kayara"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // ==============================
    // EXISTING ANDROID DEPENDENCIES
    // ==============================

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ==============================
    // FIREBASE BOM
    // ==============================


    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))


    // ==============================
    // EXISTING FIREBASE SERVICES
    // ==============================

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Cloud Firestore Database
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage")

    // ==============================
    // NEW: FIREBASE GEMINI AI
    // ==============================

    // Firebase AI Logic
    implementation("com.google.firebase:firebase-ai")

    // Kotlin coroutine lifecycle support
    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4"
    )

    // ==============================
    // NEW: FIREBASE APP CHECK
    // ==============================

    // For Android Studio debugging
    debugImplementation(
        "com.google.firebase:firebase-appcheck-debug"
    )

    // For release builds
    releaseImplementation(
        "com.google.firebase:firebase-appcheck-playintegrity"
    )

    // ==============================
    // EXISTING CLOUDINARY
    // ==============================

    implementation("com.cloudinary:cloudinary-android:3.1.2")

    // ==============================
    // EXISTING VIEWPAGER
    // ==============================

    implementation("androidx.viewpager2:viewpager2:1.1.0")
}
