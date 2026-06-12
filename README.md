# Yuma OEM SDK Client Integration Guide

This guide explains how to integrate the Yuma OEM SDK and its associated BLE SDK into your Android application. The core SDK is hosted on JitPack, which automatically handles all transitive dependencies, while the BLE SDK is provided as a local `.aar` file.

## Prerequisites
- Android Studio (Ladybug or later recommended)
- Minimum SDK Version: `24`
- Target SDK Version: `35` (or `36`)

---

## Step 1: Add the BLE SDK `.aar` File

1. In your Android Studio project, switch to the **Project** view in the top-left dropdown.
2. Navigate to your app module (usually named `app`).
3. If a `libs` directory does not exist inside `app`, right-click on `app` -> **New** -> **Directory** and name it `libs`.
4. Copy the provided `.aar` file into the `app/libs` directory:
   - `yuma-ble-sdk-<LATEST_VERSION>.aar`

*(Note: You do not need to add the `oemSdk-release.aar` locally as it will be fetched from JitPack.)*

---

## Step 2: Configure Repositories

Because the Yuma OEM SDK is hosted on JitPack, you must add the JitPack repository to your project. 

Open your `settings.gradle.kts` (or project-level `build.gradle.kts` for older projects) and add `maven { url = uri("https://jitpack.io") }` to the `repositories` block:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Add JitPack repository
    }
}
```

---

## Step 3: Configure `build.gradle.kts`

Open your app's **module-level** `build.gradle.kts` (e.g., `app/build.gradle.kts`) and make the following updates.

### 3.1 Enable Core Library Desugaring and Java 17
The SDK uses modern Java APIs, so desugaring is required. Ensure your `compileOptions` and `kotlinOptions` are set to Java 17 and enable desugaring:

```kotlin
android {
    // ...
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
```

### 3.2 Add Dependencies
Add the SDK dependencies and the desugaring library inside the `dependencies { ... }` block:

```kotlin
dependencies {
    // Enable Java 8+ API desugaring support, version 2.1.0 or higher required
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:<LATEST_VERSION>")

    // Core OEM SDK from JitPack (Replace version with the latest release)
    implementation("com.github.yuma-energy:YumaOemSdk:<LATEST_VERSION>")

    // The local BLE SDK dependency
    implementation(files("libs/yuma-ble-sdk-<LATEST_VERSION>.aar"))
}
```
*(By using JitPack, all required transitive dependencies like Jetpack Compose, Google Maps, and Ktor are resolved automatically. You don't need to manually add them!)*

---

## Step 4: Sync Project with Gradle
1. After updating the Gradle files, Android Studio will prompt you to sync.
2. Click **Sync Now** in the top-right banner.
3. Wait for the build to finish successfully.

---

## Step 5: Initialize the SDK

Before you can launch the SDK, you must initialize it. The best place to do this is in your `Application` class so that it is initialized globally when your app starts.

### 5.1 Initialize in Application Class
Create a Custom Application Class (if you don't have one) and initialize `YumaSdk`:

```kotlin
import android.app.Application
import com.yuma.oemsdk.Environment
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.YumaSdkConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Build the Configuration
        val sdkConfig = YumaSdkConfiguration.Builder()
            .setClientId(12345) // Replace with your Client ID
            .setClientSecret("YOUR_CLIENT_SECRET") // Replace with your Client Secret
            .setAuthCode("YOUR_AUTH_CODE") // Replace with your Auth Code
            .setMapApiKey("YOUR_GOOGLE_MAPS_API_KEY") // Replace with your Maps API Key
            .setEnvironment(Environment.PROD) // Available options: DEV, PREPROD, PROD
            .build()

        // 2. Initialize the SDK
        YumaSdk.init(this, sdkConfig)
    }
}
```

### 5.2 Register the Application Class
Open your `AndroidManifest.xml` and specify your custom `Application` class in the `<application>` tag:

```xml
<application
    android:name=".MyApplication"
    ... >
</application>
```

---

## Step 6: Launch the SDK

Once initialized, you can launch the SDK's full UI experience from any Activity, Fragment, or Composable (e.g., when a user clicks a button).

```kotlin
import com.yuma.oemsdk.YumaSdk

// Example inside a button click listener or Composable:
YumaSdk.launchSdk(context)
```

> **What happens next?**  
> `launchSdk()` will automatically open the SDK's main activity. It handles requesting all necessary permissions (Location, Bluetooth, Notifications), performs silent authentication in the background, and smoothly navigates the user to the Home/Map screen.

---
