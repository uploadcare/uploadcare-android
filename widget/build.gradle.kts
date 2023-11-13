plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("maven-publish")
}

group = "com.uploadcare.android.widget"

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.uploadcare.android.widget"

    defaultConfig {
        minSdkPreview = libs.versions.minSdk.get()
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "SOCIAL_API_ENDPOINT", "\"https://social.uploadcare.com/\"")
        }
        release {
            isMinifyEnabled = true
            buildConfigField("String", "SOCIAL_API_ENDPOINT", "\"https://social.uploadcare.com/\"")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            consumerProguardFile("proguard-rules.pro")
        }
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES.txt"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/dependencies.txt"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/LGPL2.1"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/notice.txt"
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
    implementation(project(":library"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.coroutines.android)
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.androidx.core)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.fragment.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.preference.ktx)

    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.viewmodel)

    implementation(libs.work.runtime)
    implementation(libs.work.gcm)

    implementation(libs.moshi.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)

    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.okhttp.mockwebserver)

    implementation(libs.retrofit.retrofit)
    implementation(libs.retrofit.converter.moshi)

    implementation(libs.picasso)

    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.espresso.core)
}

// Make sure unit tests always run when we are building the code.
tasks.named("build") {
    dependsOn(tasks.named("check"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = project.group.toString()
                artifactId = "uploadcare-android-widget"
                version = libs.versions.appVersion.get()

                //withBuildIdentifier(), available in newer gradle versions.

                pom {
                    name.set("uploadcare-android-widget")
                    url.set("https://github.com/uploadcare/uploadcare-android")
                    description.set("Android widget for the Uploadcare API.")
                    issueManagement {
                        url.set("https://github.com/uploadcare/uploadcare-android/issues")
                    }
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/uploadcare/uploadcare-android.git")
                        developerConnection.set("scm:git:ssh://github.com/uploadcare/uploadcare-android.git")
                        url.set("https://github.com/uploadcare/uploadcare-android")
                    }
                    developers {
                        developer {
                            name.set("raphaelnew")
                            email.set("iraphaele@gmail.com")
                            organization.set("Uploadcare")
                            url.set("https://github.com/raphaelnew")
                        }
                    }
                }
            }
        }
    }
}
