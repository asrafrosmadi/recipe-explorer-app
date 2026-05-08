# Keep Activities
-keep class com.asrafrosmadi.recipeexplorer.ui.**Activity { *; }

# Keep ViewModels
-keep class androidx.lifecycle.** { *; }

# Keep model classes
-keep class com.asrafrosmadi.recipeexplorer.data.model.** { *; }

# Keep org.json
-keep class org.json.** { *; }

# Keep Material Components
-keep class com.google.android.material.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Fix R8 missing classes error
-dontwarn javax.annotation.Nullable