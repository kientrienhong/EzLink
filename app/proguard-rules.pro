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

# See: https://developer.android.com/studio/build/shrink-code

########################
# Common attribute keeps
########################
-keepattributes Signature, InnerClasses, EnclosingMethod,*Annotation*,Record
# Keep Kotlin metadata (needed for reflection / serialization)
-keep class kotlin.Metadata { *; }

########################
# Hilt / Dagger (usually auto-added, but these are safe guards)
########################
-dontwarn javax.inject.**
-dontwarn dagger.hilt.internal.**
-keep class dagger.hilt.internal.** { *; }
-keep class dagger.hilt.android.internal.managers.** { *; }
-keep @dagger.hilt.android.internal.managers.AggregatedRoot class *
-keep @dagger.hilt.internal.GeneratedEntryPoint class *
-keep @dagger.hilt.internal.UninstallModules class *

########################
# Room (preserve entities, daos, databases)
########################
-keep @androidx.room.Entity class * { *; }
-keep interface * extends androidx.room.Dao
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.**

########################
# Kotlin Serialization
########################
-keep class kotlinx.serialization.** { *; }
-keepclasseswithmembers class * implements kotlinx.serialization.KSerializer { *; }
-dontwarn kotlinx.serialization.**

########################
# Parcelize (keep generated CREATOR)
########################
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

########################
# Glide (including Compose integration)
########################
-dontwarn com.bumptech.glide.**
-keep public class com.bumptech.glide.Glide { *; }
-keep public class com.bumptech.glide.Registry { *; }
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule { *; }
-keep @com.bumptech.glide.annotation.GlideModule class * { *; }
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }

########################
# Jsoup
########################
-dontwarn org.jsoup.**

########################
# AndroidX / Compose (generally no special rules required)
########################
-dontwarn androidx.compose.**

########################
# Serialization / Reflection helpers (avoid stripping)
########################
-keepclassmembers class * {
    @kotlinx.serialization.SerialName *;
    @kotlinx.serialization.Required *;
}

########################
# Keep enum methods (safe when using reflection)
########################
-keepclassmembers enum * { **[] values(); ** valueOf(java.lang.String); }

########################
# Prevent stripping of generated adapters
########################
-keep class **$$Serializer { *; }

# (Add additional project-specific rules below)
