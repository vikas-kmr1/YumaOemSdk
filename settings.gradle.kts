
import java.util.Properties

//fun readProperties(propertiesFile: File) = Properties().apply {
//    propertiesFile.inputStream().use { fis ->
//        load(fis)
//    }
//}
//
//fun getAuthCode(): String {
//    if (File(rootProject.projectDir,"local.properties").canRead()) {
//        val localProperties = readProperties(File(rootProject.projectDir,"local.properties"))
//        val authCode: String =  localProperties.getProperty("jitpack-authToken")
//
//        return "\"$authCode\""
//    }
//    return ""
//}


pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
//            credentials {
//                username = getAuthCode()
//            }
        }
        flatDir {
            dirs("oemSdk/libs")
        }
    }
}

rootProject.name = "YumaOemSdk"
include(":app")
include(":oemSdk")

