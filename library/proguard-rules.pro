# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn java.lang.invoke.StringConcatFactory

-keep class com.uploadcare.android.library.data.** { *; }
-keep class com.uploadcare.android.library.api.Project { *; }
-keep class com.uploadcare.android.library.api.UploadcareCopyFile { *; }
-keep class com.uploadcare.android.library.api.UploadcareFile { *; }
-keep class com.uploadcare.android.library.api.UploadcareGroup { *; }
-keep class com.uploadcare.android.library.api.UploadcareWebhook { *; }
