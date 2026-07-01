plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

// Move Toolchain to top-level to ensure Gradle uses a proper JDK with jlink
kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.yuma.oemsdk"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
val retrofit = "2.9.0"
val retrofitGsonConvertor = "2.9.0"
val okhttp3 = "4.9.0"


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.icons.extended)


    implementation("com.squareup.retrofit2:converter-gson:${retrofitGsonConvertor}")
    implementation ("com.segment.analytics.kotlin:android:1.16.3")
    implementation("com.squareup.retrofit2:retrofit:${retrofit}")
    implementation("com.squareup.retrofit2:converter-gson:${retrofitGsonConvertor}")
    implementation("com.squareup.retrofit2:adapter-rxjava2:${retrofit}")
    implementation("com.squareup.okhttp3:logging-interceptor:${okhttp3}")
    implementation("com.squareup.okhttp3:okhttp:${okhttp3}")
    compileOnly(files("libs/yuma-ble-sdk-v2.8.17.aar"))

    // CameraX for camera preview & lifecycle
    implementation(libs.bundles.camerax)
    // ML Kit barcode scanning (bundled model — works offline)
    implementation(libs.mlkit.barcode.scanning)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // QR Code Scanner and Generator
    implementation(libs.qr.kit)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // DataStore — SDK-internal session preferences (no 3rd party DI)
    implementation(libs.androidx.datastore.preferences)

    // Maps & Location
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)

    // Image loading
    implementation(libs.coil.compose)

    // ─── Ktor Network Layer ───
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)           // Native Android engine
    implementation(libs.ktor.client.auth)              // Bearer token auto-refresh
    implementation(libs.ktor.client.content.negotiation) // JSON content type handling
    implementation(libs.ktor.client.logging)           // Network logging
    implementation(libs.ktor.serialization.kotlinx.json) // JSON parsing via Ktor
    implementation(libs.touchlab.kermit)//logging

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coroutines.core)
    implementation(libs.lottie.compose)

    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation("com.segment.analytics.kotlin:android:1.16.3")

    api(libs.moko.permissions)
    api(libs.moko.permissions.compose)
    api(libs.permissions.bluetooth)
    api(libs.permissions.location)
    api(libs.permissions.notifications)

    implementation(libs.cashfree)

    // Network inspection (Available everywhere now so we can control it via SDK)
    implementation(libs.inspektify.ktor3)


}

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.yuma-energy"
                artifactId = "oem_sdk_beta"
                version = "1.0.27-beta"
            }
        }
    }
}
