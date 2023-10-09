plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.uploadcare.android.example"

    defaultConfig {
        applicationId = "com.uploadcare.android.example"
        minSdkPreview = libs.versions.minSdk.get()
        targetSdkPreview = libs.versions.targetSdk.get()
        versionCode = 10
        versionName = libs.versions.appVersion.get()
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources {
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE"
        }
    }

    lint {
        abortOnError = false
    }

    compileOptions {
        val javaVersion = JavaVersion.toVersion(libs.versions.jdk.get())
        sourceCompatibility(javaVersion)
        targetCompatibility(javaVersion)
    }

    kotlinOptions {
        jvmTarget = libs.versions.jdk.get()
    }
}

dependencies {
    implementation(project(":widget"))
    implementation(project(":library"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation (libs.androidx.core)

    implementation (libs.navigation.fragment)
    implementation (libs.navigation.ui)

    implementation (libs.fragment.ktx)
    implementation (libs.appcompat)
    implementation (libs.material)
    implementation (libs.constraintlayout)

    implementation (libs.lifecycle.common)
    implementation (libs.lifecycle.viewmodel)

    implementation(libs.picasso)

    androidTestImplementation (libs.test.runner)
    androidTestImplementation (libs.test.espresso.core)
}
