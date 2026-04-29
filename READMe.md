### How to add SDK to your project

#### Groovy (`settings.gradle`)
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### KTS (`settings.gradle.kts`)
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Add the dependency

#### Groovy (`build.gradle`)
```groovy
dependencies {
    implementation 'com.github.vikas-kmr1:YumaOemSdk:Tag'
}
```

#### KTS (`build.gradle.kts`)
```kotlin
dependencies {
    implementation("com.github.vikas-kmr1:YumaOemSdk:Tag")
}
```

#### Application (`ClientApplication.kt`)
##### Step 1: Initialize Yuma OEM SDK
```kotlin
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Yuma OEM SDK
        val sdkConfig = YumaSdkConfiguration.Builder()
            .setClientKey("CLIENT_API_KEY") // Replace with real key
            .setMapApiKey("YOUR_API_KEY")
            .setEnvironment(Environment.DEV) // DEV, PREPROD, PROD
            .build()
            
        YumaSdk.init(this, sdkConfig)
    }
```

##### Step 2: Launch Sdk Home
```kotlin
    YumaSdk.launchSdk(this@MainActivity)
```
