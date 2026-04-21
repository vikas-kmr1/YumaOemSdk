plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
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

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.androidx.compose.material3)

    // ─── Ktor Network Layer (No OkHttp — avoids dependency conflicts in host apps) ───
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)           // Native Android engine (HttpURLConnection)
    implementation(libs.ktor.client.auth)              // Bearer token auto-refresh
    implementation(libs.ktor.client.content.negotiation) // JSON content type handling
    implementation(libs.ktor.client.logging)           // Network logging (debug only)
    implementation(libs.ktor.serialization.kotlinx.json) // JSON parsing via Ktor
    implementation(libs.kotlinx.serialization.json)   // Kotlinx JSON serialization
}


android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.vikas-kmr1" // Replace with your GitHub username
                artifactId = "oem_sdk_beta" // Replace with the name of your SDK
                version = "1.0.0" // Replace with the version you want to publish
            }
        }
    }
}