plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.asrafrosmadi.recipeexplorer"
    compileSdk = 37

    flavorDimensions += "env"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 37
    }

    productFlavors {
        create("development") {
            dimension = "env"
            applicationId = "com.asrafrosmadi.recipeexplorer.dev"
            versionCode = 1
            versionName = "0.0.1"
            buildConfigField(
                "String",
                "ENVIRONMENT",
                "\"DEVELOPMENT\""
            )
        }

        create("staging") {
            dimension = "env"
            applicationId = "com.asrafrosmadi.recipeexplorer.stg"
            versionCode = 1
            versionName = "0.0.1"
            buildConfigField(
                "String",
                "ENVIRONMENT",
                "\"STAGING\""
            )
        }

        create("production") {
            dimension = "env"
            applicationId = "com.asrafrosmadi.recipeexplorer"
            versionCode = 101
            versionName = "1.0.1"
            buildConfigField(
                "String",
                "ENVIRONMENT",
                "\"PRODUCTION\""
            )
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
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")

    //  Lottie Anim Dependencies
    implementation("com.airbnb.android:lottie:6.7.1")

    //  Google Dependencies
    implementation("com.google.android.material:material:1.14.0")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    //  Firebase Dependencies
    implementation(platform("com.google.firebase:firebase-bom:34.13.0"))
    implementation("com.google.firebase:firebase-analytics")
}
