plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.asrafrosmadi.recipeexplorer"
    compileSdk = 36

    flavorDimensions += "env"

    defaultConfig {
        minSdk = 23
        targetSdk = 36
    }

    productFlavors {
        create("development") {
            dimension = "env"
            applicationId = "com.asrafrosmadi.recipeexplorer.dev"
            versionCode = 1
            versionName = "0.0.1"
        }

        create("staging") {
            dimension = "env"
            applicationId = "com.asrafrosmadi.recipeexplorer.stg"
            versionCode = 1
            versionName = "0.0.1"
        }

        create("production") {
            dimension = "env"
            applicationId = "com.asrafrosmadi.recipeexplorer"
            versionCode = 1
            versionName = "1.0.0"
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
        checkDependencies = false
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug") // local testing only
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    //  AndroidX Dependencies
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //  Lottie Anim Dependencies
    implementation("com.airbnb.android:lottie:6.4.0")

    //  Google Dependencies
    implementation("com.google.android.material:material:1.12.0")

    //  Firebase Dependencies
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-analytics")
}
