// build.gradle (Module: app)
// Ce fichier configure la compilation de votre application Android

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.notea"
    compileSdk = 36  // Version SDK Android utilisée pour compiler

    defaultConfig {
        applicationId = "com.example.notea"  // Identifiant unique de l'app
        minSdk = 24      // Version Android minimale supportée (Android 7.0)
        targetSdk = 36   // Version Android ciblée
        versionCode = 1  // Numéro de version interne
        versionName = "1.0"  // Nom de version visible par l'utilisateur

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false  // Ne pas minimiser le code en release
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    // Configuration pour supporter Kotlin
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

// Dépendances : bibliothèques externes utilisées par l'app
dependencies {

    // Bibliothèques AndroidX de base
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)

    // RecyclerView : pour afficher des listes optimisées
    implementation(libs.androidx.recyclerview)

    // CardView : pour les cartes arrondies des notes
    implementation(libs.androidx.cardview)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Bibliothèques de test (optionnelles pour débuter)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}