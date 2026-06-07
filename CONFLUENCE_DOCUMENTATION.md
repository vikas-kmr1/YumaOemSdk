# 📘 Yuma OEM SDK - Product & Technical Documentation

## 1. Executive Summary

The **Yuma OEM SDK** is a plug-and-play Android library designed to let partner companies (OEMs and Franchises) seamlessly embed Yuma’s battery-swapping ecosystem directly into their own applications. 

Instead of building complex IoT integrations, maps, and payment gateways from scratch, partners simply drop this SDK into their app. With just a few lines of code, their users gain access to real-time station maps, Bluetooth-powered battery swaps, and integrated payments.

---

## 2. Why We Built It

Partners want to offer battery swapping to their users under their own brand. However, developing the hardware communication (BLE), mapping, and token logic is highly complex. 

**The Yuma OEM SDK solves this by providing:**
*   **Speed to Market:** Partners can integrate battery swapping in days, not months.
*   **Frictionless Experience:** Users are logged in silently (Silent Auth) behind the scenes, meaning they don't have to create a separate "Yuma" account.
*   **Unified UI/UX:** A polished, modern, Compose-driven interface that feels like a natural part of the partner's app.

---

## 3. 🌟 Key Features

The SDK is packed with out-of-the-box features:

*   🔐 **Silent Authentication:** A secure handshake between the partner app and Yuma. Users are authenticated automatically using a Client Key and Auth Code.
*   🛡️ **Smart Permission Handling:** The SDK handles complex Android permissions natively (Bluetooth, Location, and Notifications) before letting the user proceed.
*   🗺️ **Live Maps & Navigation:** Integrated Google Maps showing nearby swapping stations, real-time battery availability, and route navigation.
*   🔄 **Hardware BLE Swapping:** Communicates directly with Yuma swapping stations via Bluetooth to perform secure, offline-capable battery swaps.
*   📷 **QR Code Scanning:** Fast and reliable barcode scanning via MLKit to identify stations and batteries.
*   💳 **Integrated Payments:** Built-in Cashfree payment gateway for handling subscription plans and on-the-go payments.

---

## 4. 🏗️ Technical Architecture

The SDK is built using a state-of-the-art modern Android stack. It is modular, reactive, and highly performant.

### **The Tech Stack**
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (100% Declarative UI)
*   **Asynchronous Operations:** Kotlin Coroutines & Flows
*   **Networking:** Ktor & Retrofit
*   **Local Storage:** DataStore Preferences
*   **Hardware:** Android Bluetooth Low Energy (BLE) & CameraX

### **Module Breakdown**
The SDK is divided into focused, clean modules:
1.  `core_network`: Manages all API calls to the Yuma backend.
2.  `core_preference`: Handles secure local storage.
3.  `new_ble_sdk` & `sdk_core`: The brains behind the IoT hardware communication.
4.  `feature_home` & `onboarding`: Contains the visual screens, ViewModels, and UI logic for the map, user profile, and token booking.
5.  `core_payment`: Bridges the SDK with the Cashfree payment gateway.

---

## 5. 🚀 High-Level Integration Flow

Integrating the SDK is designed to be as simple as possible for partner developers. 

**Step 1: Import the Library**
Partners pull the SDK securely via JitPack, which automatically resolves all heavy-lifting dependencies (like Compose and Ktor).

**Step 2: Initialize the SDK**
The partner passes their unique credentials to the SDK when their app starts up.
```kotlin
val sdkConfig = YumaSdkConfiguration.Builder()
    .setClientId(12345)
    .setClientSecret("SECRET")
    .setAuthCode("AUTH_CODE")
    .setMapApiKey("MAP_API_KEY")
    .setEnvironment(Environment.PROD)
    // ...
    .build()

YumaSdk.init(applicationContext, sdkConfig)
```

**Step 3: Launch the Experience**
When the user taps "Swap Battery" in the partner app, a single method call takes over:
```kotlin
YumaSdk.launchSdk(context)
```
*Behind the scenes: The SDK grabs the required permissions, logs the user in silently, and drops them onto the interactive map!*

---

## 6. 🌍 Supported Environments

The SDK supports multiple backend environments to allow partners to test safely before going live.

*   **DEV / PREPROD:** Sandbox environments for partners to test the UI, mock battery swaps, and verify payment flows without spending real money.
*   **PROD:** The live production environment connected to real, physical Yuma swapping stations.

---

> **💡 Note for Developers:** For detailed, step-by-step code integration instructions, please refer to the `CLIENT_INTEGRATION_README.md` included in the SDK repository.
