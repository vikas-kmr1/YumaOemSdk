# SDK Architecture: The "Two Modules" Approach (Visual Text)

Since Android Studio's Markdown preview does not render Mermaid diagrams, here are universal visual representations (using ASCII Art) that you can view directly within Android Studio without any plugins.

---

## High-Level Architecture Flow

This diagram illustrates how your SDK splits into two modules, and how the Client App can interact with both:

```text
 ┌────────────────────────────────────────────────────────┐
 │                Client Host Application                 │
 └────────────────────────────────────────────────────────┘
            │                                 │
     [ Option A: ]                     [ Option B: ]
  Client Wants Full UI             Client Wants Custom UI
            │                                 │
            ▼                                 │
  ┌──────────────────┐                        │
  │     sdk-ui       │                        │
  │  (Compose / XML) │                        │
  └──────────────────┘                        │
            │                                 │
     (Relies Upon)                            │
            │                                 │
            ▼                                 ▼
 ┌────────────────────────────────────────────────────────┐
 │                       sdk-core                         │
 │        (Headless Logic, Retrofit, Auth Logic)          │
 └────────────────────────────────────────────────────────┘
                              │
                      (Network Calls)
                              │
                              ▼
 ┌────────────────────────────────────────────────────────┐
 │              Your Backend Services / DB                │
 └────────────────────────────────────────────────────────┘
```

---

## SDK Integration Use Cases

Depending on the client's engineering capacity, they can choose between two distinct integration flows.

### Use Case A: The "Drop-In UI" Client (Plugin & Play)
**Target:** Fast Startups, Quick Integrations. 
*The client relies entirely on your pre-built design in the `sdk-ui` module.*

```text
CLIENT APP                           SDK-UI                           SDK-CORE
    │                                  │                                 │
    ├─── 1. YumaSdk.init(key) ─────────┼────────────────────────────────>│ (Silently Auths)
    │                                  │                                 │
    ├─── 2. launchHome() ─────────────>│                                 │
    │                                  ├─── 3. Fetch Data Internally ───>│
    │                                  │<── 4. Returns Data Models ──────┤
    │<── 5. Takes over Screen to ──────┤                                 │
    │       display beautiful UI       │                                 │
    │                                  │                                 │
```

### Use Case B: The "Headless API" Client (Custom UI)
**Target:** Giant Enterprises with strict custom design guidelines.
*The client completely skips `sdk-ui`, importing only `sdk-core`. They handle rendering the buttons and lists themselves.*

```text
CLIENT's Custom UI                                                    SDK-CORE
    │                                                                    │
    ├─── 1. YumaSdkCore.init(key) ──────────────────────────────────────>│ (Silently Auths)
    │                                                                    │
    ├─── 2. yumaCore.getNearestStations() ──────────────────────────────>│
    │<── 3. Returns Raw data: List<StationModel> ────────────────────────┤
    │                                                                    │
    ├─── 4. Client's Engineers map the raw data                          │
    │       onto their Company's specific styling                        │
    │                                                                    │
```

## Why this is the "Sweet Spot"
By splitting into two packages:
1. **App Size:** If a client builds their own UI, they don't have to download Jetpack Compose artifacts saving huge amounts of megabytes inside the final `.aar`.
2. **Isolation Guarantee:** Clients using pure headless logic won't be exposed to the internal Navigation crash issues deep inside your `sdk-ui`.
