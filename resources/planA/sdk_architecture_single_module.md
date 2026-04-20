# Final SDK Architecture: Single Module (Drop-In UI Only)

Based on the decision to **not provide custom UI headless options** and to require a **Map Key** during initialization, this document details the locked-in architecture and specific integration flow for external clients.

---

## 1. High-Level Architecture Flow

The entire SDK (Networking, Authentication, Databases, Jetpack Compose UI, and Map renderings) is bundled into one singular `.aar` file. The client has no direct access to underlying headless APIs; they merely plug the module in and launch the UI.

<img src="./user_flow-093020.png" width="2986" alt="">


## 2. Integration Sequence (Launch & Auth Flow)

Because the Custom UI / Headless option has been stripped out, integrating the SDK is completely linear and extremely simple for end-user developers.

### **The Initialization Logic**
During the host application's startup, the client must pass **three** required keys to configuration:
1. `ClientId`: Identifies the client partner.
2. `ClientKey`: Secure key exchanged for the JWT Session Token.
3. `MapKey`: Exposes the Google Maps API key (to be consumed internally by your Maps Compose UI).

<img src="./user_flow.png" width="5092" alt="">

## Abstracted Code Integration Example

What the client developer will actually write to start your SDK:

```kotlin
// Inside Client's Application Class
YumaSdk.init(
    context = this,
    config = SdkConfiguration.Builder()
        .setClientKey("SEC_KEY_XYZ")
        .setMapKey("MAP_API_") // New required parameter
        .setEnvironment(Environment.PROD)
        .build()
)

// Later on a button press inside their app
binding.btnOpenMap.setOnClickListener {
    YumaSdk.launchHome(this)
}
```
