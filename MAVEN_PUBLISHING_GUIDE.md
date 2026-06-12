# 📦 Yuma OEM SDK - Maven Publishing Guide

This guide provides step-by-step instructions and command references for building, signing, and publishing the **Yuma OEM SDK** to various Maven hosting options.

---

## 🗺️ Publishing Overview

Depending on your distribution and testing needs, you can publish the SDK using one of the three primary approaches:

| Method | Best For | Target Repository | Access Control |
| :--- | :--- | :--- | :--- |
| **Local Publishing** | Rapid local integration testing & development | Local machine’s `~/.m2/repository` | Local only |
| **JitPack Hosting** | Quick cloud publishing from Git repository | `jitpack.io` | Public or Private Git credentials |
| **Custom Remote Maven** | Formal enterprise releases & production deployments | Sonatype / Nexus, Artifactory, or GitHub Packages | Custom IAM / token-based |

---

## 💻 1. Publishing to Local Maven Repository (`mavenLocal`)

Publishing locally is the fastest way to test your SDK changes in a client application on the same machine without pushing any commits online.

### Step-by-Step Instructions

1. **Verify your Version Configuration**  
   Open `oemSdk/build.gradle.kts` and check the target coordinates under the `afterEvaluate` block:
   ```kotlin
   afterEvaluate {
       publishing {
           publications {
               create<MavenPublication>("release") {
                   from(components["release"])
                   groupId = "com.bitbucket.yumaenergy"
                   artifactId = "oemSdk"
                   version = "1.0.15-beta" // Update this for new local versions
               }
           }
       }
   }
   ```

2. **Run the Publishing Command**  
   Execute the following gradle command in the root folder of the project:
   ```bash
   ./gradlew :oemSdk:publishToMavenLocal
   ```
   *Note: If you want to build and publish only the release publication, use:*
   ```bash
   ./gradlew :oemSdk:publishReleasePublicationToMavenLocal
   ```

3. **Verify the Build Artifacts**  
   Check that the artifacts (`.aar`, `.pom`, and source jars) have been created and copied to your local Maven cache.
   - **Mac/Linux:** `~/.m2/repository/com/bitbucket/yumaenergy/oemSdk/`
   - **Windows:** `C:\Users\<Username>\.m2\repository\com\bitbucket\yumaenergy\oemSdk\`

### Consuming the Local Build in a Client App

To test this local artifact in your Android client app (e.g., `yuma-oem-customer-app`):

1. Add `mavenLocal()` as the **very first** repository in the client app's `settings.gradle.kts`:
   ```kotlin
   dependencyResolutionManagement {
       repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
       repositories {
           mavenLocal() // Look up local repository first
           google()
           mavenCentral()
           // ...
       }
   }
   ```

2. Add the dependency in your client app's `app/build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation("com.bitbucket.yumaenergy:oemSdk:1.0.15-beta")
   }
   ```

---

## 🚀 2. Cloud Publishing via JitPack (Private Bitbucket Repository)

Since the **Yuma OEM SDK** repository is private and hosted on Bitbucket under the workspace `yumaenergy`, publishing and consuming the library requires authorization settings for both JitPack and client applications.

### Step 2.1: Grant JitPack Access to the Private Bitbucket Repository

JitPack needs permissions to pull code from your private Bitbucket repository to build it. You can grant access in one of two ways:

#### Option A: Connect Bitbucket Account (Recommended)
1. Go to [JitPack.io](https://jitpack.io/) and click **Sign In** in the top right.
2. Choose **Bitbucket** and authenticate using your Bitbucket credentials.
3. Grant JitPack read access to your workspace and repositories.

#### Option B: Configure a Deploy Key (Access Key)
1. Log in to [JitPack.io](https://jitpack.io/) and navigate to your profile at `https://jitpack.io/private` to copy your **JitPack SSH Public Key**.
2. Go to your repository page in **Bitbucket** (`yumaenergy/yuma-oem-sdk`).
3. Click on **Repository settings** in the left sidebar.
4. Select **Access keys** (under the Security section).
5. Click **Add key**, name it `JitPack`, paste the copied SSH public key, and click **Save**.

---

### Step 2.2: Tag and Release the SDK Code

Once JitPack has read access, you can trigger builds by pushing new Git tags.

1. **Commit and Push Your Changes**
   Make sure all codebase changes are committed and pushed to the remote `main` branch:
   ```bash
   git add .
   git commit -m "Prepare version 1.0.15-beta"
   git push origin main
   ```

2. **Create and Push a Git Tag**
   Tag the commit matching the version you want to release:
   ```bash
   git tag -a 1.0.15-beta -m "Release version 1.0.15-beta"
   git push origin 1.0.15-beta
   ```

3. **Check the JitPack Build Status**
   - Go to [JitPack.io](https://jitpack.io/).
   - Enter your private repository path: `yumaenergy/yuma-oem-sdk`.
   - Click **Look up**.
   - Your tag `1.0.15-beta` should appear in the list. Click **Get it** to trigger the build.
   - Wait for the status indicator to turn **green** (Success). You can click on the log icon to view compile progress.

---

### Step 2.3: Configure the Client App to Consume the Private SDK

Because the repository is private, any client application (like `yuma-oem-customer-app`) needs authorization credentials to fetch it from JitPack.

#### 1. Generate a JitPack API Token
- Go to `https://jitpack.io/private`.
- Copy your **API Token** (starts with `jp_`).

#### 2. Configure Credentials in the Client App
To avoid hardcoding tokens, configure them locally on the client developer's machine:

Add the token to the client app's `local.properties` file:
```properties
jitpack.token=jp_xxxxxxxxxxxxxxxxxxxxxxxxxx
```

#### 3. Update `settings.gradle.kts` in the Client App
Configure the JitPack Maven repository block to load the token dynamically:

```kotlin
import java.util.Properties
import java.io.FileInputStream

// Read local.properties securely
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.projectDir.resolve("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}
val jitpackToken = localProperties.getProperty("jitpack.token") ?: System.getenv("JITPACK_TOKEN")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        
        // Add Private JitPack Repository with Auth
        maven {
            url = uri("https://jitpack.io")
            credentials {
                username = "authToken" // This is a literal value required by JitPack
                password = jitpackToken
            }
        }
    }
}
```

#### 4. Add the Dependency in the Client App (`app/build.gradle.kts`)
Specify the private Bitbucket dependency coordinates. The group ID will always start with `com.bitbucket`:

```kotlin
dependencies {
    // Core OEM SDK from Private Bitbucket Repository
    // Format: com.bitbucket.<workspace>:<module_name_or_artifact_id>:<version>
    implementation("com.bitbucket.yumaenergy:oemSdk:1.0.15-beta")
}
```

> [!TIP]  
> If JitPack builds the repository but cannot resolve sub-modules correctly, verify that the module name matches the gradle build configuration (e.g. `:oemSdk`).

---

## 🏛️ 3. Publishing to a Remote Maven Repository (Nexus / Sonatype / Artifactory)

To release artifacts directly to a central repository (like Maven Central or a private enterprise Nexus), you need to configure the publishing repository block in the gradle scripts.

### Step-by-Step Instructions

1. **Configure Repository credentials in `oemSdk/build.gradle.kts`**  
   Add the target Maven repository repository endpoint under the `publishing` block:
   ```kotlin
   publishing {
       repositories {
           maven {
               name = "MyNexus"
               val releasesUrl = "https://your-nexus-host.com/repository/maven-releases/"
               val snapshotsUrl = "https://your-nexus-host.com/repository/maven-snapshots/"
               url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl)
               
               credentials {
                   username = project.findProperty("mavenUser")?.toString() ?: System.getenv("MAVEN_USER")
                   password = project.findProperty("mavenPassword")?.toString() ?: System.getenv("MAVEN_PASSWORD")
               }
           }
       }
   }
   ```

2. **Secure Credentials Locally**  
   To prevent committing credentials to source control, declare them in your global or local properties file (`~/.gradle/gradle.properties` or `local.properties`):
   ```properties
   mavenUser=your_repository_username
   mavenPassword=your_repository_password
   ```

3. **Deploy Artifacts to Remote Repository**  
   Run the following task to compile, pack, and upload the artifacts to your configured server:
   ```bash
   ./gradlew :oemSdk:publish
   ```
   *To build and upload specifically to the defined repository:*
   ```bash
   ./gradlew :oemSdk:publishReleasePublicationToMyNexusRepository
   ```

---

## 🔐 4. Artifact Signing (Required for Maven Central)

If releasing to Maven Central (Sonatype), artifacts must be cryptographically signed using GnuPG (GPG).

1. **Configure Signing Plugin**  
   Add the signing plugin in `oemSdk/build.gradle.kts`:
   ```kotlin
   plugins {
       id("signing")
   }
   ```

2. **Set Up GPG Configurations**  
   Add signing configuration block matching the release publication:
   ```kotlin
   signing {
       sign(publishing.publications["release"])
   }
   ```

3. **Provide GPG Key Info in `local.properties`**  
   ```properties
   signing.keyId=12345678
   signing.password=your_gpg_passphrase
   signing.secretKeyRingFile=/Users/username/.gnupg/secring.gpg
   ```

---

## 🛠️ Summary of Common Publishing Commands

| Command | Action |
| :--- | :--- |
| `./gradlew :oemSdk:clean` | Cleans up the previous build artifacts. |
| `./gradlew :oemSdk:assembleRelease` | Compiles the release configuration and generates the local `.aar` package. |
| `./gradlew :oemSdk:publishToMavenLocal` | Publishes the `.aar` and metadata directly to local machine's Maven Cache. |
| `./gradlew :oemSdk:publish` | Publishes the `.aar` to all configured remote Maven repositories. |
