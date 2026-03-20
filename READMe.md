### Add the repository

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