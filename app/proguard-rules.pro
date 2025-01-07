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

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}
# Allow full obfuscation of the Constants class and its fields
-dontwarn com.example.aprajitafoundation.utility.Constants

-dontwarn org.slf4j.impl.StaticLoggerBinder

# Gson serialization
-keep class com.google.gson.** { *; }

# Retrofit interface
-keep interface com.example.aprajitafoundation.api.UserAdminApi { *; }
-keep interface com.example.aprajitafoundation.api.AdminAuthApi { *; }

# Keep classes used by Retrofit
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Keep the generic types (for reflection purposes)
-keep class com.example.aprajitafoundation.model.** { *; }

# Ensure Retrofit doesn't obfuscate these
-keep class * extends retrofit2.Call { *; }
-keep class * extends retrofit2.Response { *; }