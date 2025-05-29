# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep the BuildConfig class
-keep class com.example.bestbikeday.BuildConfig { *; }

# Remove all logging calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Keep required classes for API communication
-keep class com.example.bestbikeday.data.** { *; }
-keepclassmembers class com.example.bestbikeday.data.** { *; }

# Keep Retrofit service methods
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep Moshi classes
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keepclassmembers class ** {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}