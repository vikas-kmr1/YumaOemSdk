# ProGuard rules for Yuma OEM SDK consumers
# These rules ensure that the SDK classes required for integration are preserved

# Keep the main SDK entry point
-keep class com.yuma.oemsdk.YumaSdk { *; }
-keep class com.yuma.oemsdk.YumaSdkConfiguration { *; }
-keep class com.yuma.oemsdk.YumaSdkConfiguration$Builder { *; }
-keep enum com.yuma.oemsdk.SdkEnvironment { *; }

# Keep DTOs for serialization (Ktor/Kotlinx.serialization)
-keepattributes Signature, Exceptions, *Annotation*
-keep class com.yuma.oemsdk.data.dto.** { *; }

# Ktor-specific rules
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Compose rules are usually handled by the compose plugin, but we can add them here if needed
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
