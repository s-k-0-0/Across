# Modern R8 & ProGuard Optimization Rules for Across The Internet

# Optimization Passes & Access Modifications
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''

# Preserve Source File & Line Numbers for Production Diagnostics
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod

# Jetpack Compose Optimization Rules
-keep class androidx.compose.** { *; }
-keepclassmembers class * extends androidx.compose.runtime.State { *; }

# Room Database & FTS Entities Preservation
-keep class com.kax.across.data.local.** { *; }
-keepclassmembers class com.kax.across.data.local.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# WorkManager Worker Classes
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.kax.across.worker.ScraperWorker { *; }

# Jsoup HTML Parser
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# Coil Image Loading
-keep class coil.** { *; }
-dontwarn coil.**

# Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
