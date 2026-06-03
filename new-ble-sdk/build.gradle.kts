plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    namespace = "com.yuma.oemsdk.new_ble_sdk"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }
}

val retrofit = "2.9.0"
val retrofitGsonConvertor = "2.9.0"
val okhttp3 = "4.9.0"

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.squareup.retrofit2:converter-gson:${retrofitGsonConvertor}")
    implementation ("com.segment.analytics.kotlin:android:1.16.3")
    implementation("com.squareup.retrofit2:retrofit:${retrofit}")
    implementation("com.squareup.retrofit2:converter-gson:${retrofitGsonConvertor}")
    implementation("com.squareup.retrofit2:adapter-rxjava2:${retrofit}")
    implementation("com.squareup.okhttp3:logging-interceptor:${okhttp3}")
    implementation("com.squareup.okhttp3:okhttp:${okhttp3}")
    compileOnly(files("libs/yuma-ble-sdk-v2.8.13.aar"))
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
                groupId = "com.github.vikas-kmr1.YumaOemSdk"
                artifactId = "new-ble-sdk"
                version = "1.0.7-beta"
                
                pom.withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", "com.github.vikas-kmr1.YumaOemSdk")
                    dependencyNode.appendNode("artifactId", "yuma-ble-sdk")
                    dependencyNode.appendNode("version", "1.0.7-beta")
                    dependencyNode.appendNode("scope", "runtime")
                }
            }
            create<MavenPublication>("bleAar") {
                groupId = "com.github.vikas-kmr1.YumaOemSdk"
                artifactId = "yuma-ble-sdk"
                version = "1.0.7-beta"
                artifact(file("libs/yuma-ble-sdk-v2.8.13.aar"))
            }
        }
    }
}