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

Initialize the SDK in your `Application` class's `onCreate()` method:

```kotlin
import android.app.Application
import com.yuma.oemsdk.Environment
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.YumaSdkConfiguration

class ClientApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 1. Build the configuration
        val sdkConfig = YumaSdkConfiguration.Builder()
            .setClientKey("YOUR_CLIENT_API_KEY") // Issued by Yuma
            .setMapApiKey("YOUR_GOOGLE_MAPS_API_KEY") // Required for maps
            .setEnvironment(Environment.PROD) // DEV, PREPROD, or PROD
            .build()
            
        // 2. Initialize the SDK
        YumaSdk.init(this, sdkConfig)
    }
}
```

##### Step 2: Launch the SDK
You can launch the Yuma SDK from any Activity or Fragment:

```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yuma.oemsdk.YumaSdk

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set your content view, etc.

        // Launch the Yuma SDK full experience
        YumaSdk.launchSdk(this)
    }
}
```
