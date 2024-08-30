plugins {
    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("com.google.firebase.crashlytics")
//    id("kotlin-parcelize")
    kotlin("android")
    kotlin("plugin.compose")
    kotlin("plugin.parcelize")
    kotlin("plugin.serialization") version "2.0.20"
}

android {
    signingConfigs {
        getByName("debug") {
//            storeFile = file("/home/yamin_khan/Documents/keys/debugME1.keystore")
            storeFile = file("C:/debugME1.keystore")
        }
    }
    namespace = "com.mlab.knockme"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mlab.knockme"
        minSdk = 23
        targetSdk = 35
        versionCode = 14
        versionName = "2.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-splashscreen:1.2.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))
    implementation("androidx.fragment:fragment-ktx:1.8.2")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0-rc01")


    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.facebook.android:facebook-android-sdk:17.0.1")

    //gsm
//    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("androidx.credentials:credentials:1.5.0-alpha04")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0-alpha04")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.08.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.8.0-rc01")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.2")

    implementation("io.coil-kt:coil-compose:2.7.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14")

    // Chucker
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")

    // Bar chart
    implementation("com.himanshoe:charty:1.0.1")
    implementation("com.ibm.icu:icu4j:75.1") {
        exclude(group = "com.ibm.icu", module = "util")
        exclude(group = "com.ibm.icu", module = "impl")
        exclude(group = "com.ibm.icu", module = "lang")
        exclude(group = "com.ibm.icu", module = "math")
        exclude(group = "com.ibm.icu", module = "message2")
        exclude(group = "com.ibm.icu", module = "number")
    }
    // Play In-App Update
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Play In-App Review
     implementation("com.google.android.play:review-ktx:2.0.1")
}