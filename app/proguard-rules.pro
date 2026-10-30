# ProGuard & R8 optimization rules for publishing AetherStudio Elite

# Preserve JavascriptInterface methods for WebView bridge
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep publishing classes and helpers (Java + Kotlin)
-keep class com.example.publish.** { *; }
-keep class com.example.MainActivity** { *; }
-keep class com.example.AetherApplication { *; }

# Keep BuildConfig fields
-keep class com.example.BuildConfig { *; }

# Preserve line numbers and source attributes for production crash stack traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
