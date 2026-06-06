# Yuma OEM SDK Client Integration Guide

This guide explains how to manually integrate the Yuma OEM SDK and its associated BLE SDK into your Android application using `.aar` files.

## Prerequisites
- Android Studio (Ladybug or later recommended)
- Minimum SDK Version: `24`
- Target SDK Version: `35` (or `36`)

---

## Step 1: Add the `.aar` Files

1. In your Android Studio project, switch to the **Project** view in the top-left dropdown.
2. Navigate to your app module (usually named `app`).
3. If a `libs` directory does not exist inside `app`, right-click on `app` -> **New** -> **Directory** and name it `libs`.
4. Copy the provided `.aar` files into the `app/libs` directory:
   - `oemSdk-release.aar`
   - `yuma-ble-sdk-v2.8.13.aar`

---

## Step 2: Configure `build.gradle.kts`

Because you are integrating `.aar` files manually, **transitive dependencies are not resolved automatically**. You must explicitly include all third-party libraries that the Yuma SDK depends on to prevent `ClassNotFoundException` at runtime.

Open your app's **module-level** `build.gradle.kts` (e.g., `app/build.gradle.kts`) and add the following inside the `dependencies { ... }` block.

```kotlin
dependencies {
    // 1. Core SDK Files
    implementation(files("libs/oemSdk-release.aar"))
    implementation(files("libs/yuma-ble-sdk-v2.8.13.aar"))

    // 2. Transitive Dependencies required by Yuma OEM SDK
    
    // AndroidX & Compose
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    
    // Compose UI & Material
    implementation(platform("androidx.compose:compose-bom:2026.03.00")) // Update BOM version if needed
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // CameraX
    val cameraxVersion = "1.4.2"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Navigation & Coroutines & DataStore
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("androidx.datastore:datastore-preferences:1.1.4")

    // Maps & Location
    implementation("com.google.maps.android:maps-compose:8.3.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Networking (Retrofit & Ktor)
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    val ktorVersion = "3.4.2"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Serialization & JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Firebase (BOM for versioning)
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-messaging")

    // Additional Utilities
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("network.chaintech:qr-kit:3.0.6")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.airbnb.android:lottie-compose:6.7.1")
    implementation("co.touchlab:kermit:2.0.4")
    implementation("com.segment.analytics.kotlin:android:1.16.3")
    implementation("com.cashfree.pg:api:2.2.8")

    // Moko Permissions
    val mokoVersion = "0.19.1"
    implementation("dev.icerock.moko:permissions-android:$mokoVersion")
    implementation("dev.icerock.moko:permissions-compose-android:$mokoVersion")
    implementation("dev.icerock.moko:permissions-bluetooth-android:$mokoVersion")
    implementation("dev.icerock.moko:permissions-location-android:$mokoVersion")
    implementation("dev.icerock.moko:permissions-notifications-android:$mokoVersion")
}
```

> **Note:** If your app already includes some of these dependencies (like Compose, Firebase, or Coroutines), you do not need to duplicate them. Ensure that your existing versions are reasonably close to avoid compatibility issues.

---

## Step 3: Sync Project with Gradle
1. After updating the `build.gradle.kts` file, Android Studio will prompt you to sync.
2. Click **Sync Now** in the top-right banner.
3. Wait for the build to finish successfully.

You have now successfully integrated the Yuma OEM SDK into your app and can begin initializing it!
