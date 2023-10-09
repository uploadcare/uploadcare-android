plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("maven-publish")
}

group = "com.uploadcare.android.library"

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.uploadcare.android.library"

    defaultConfig {
        minSdkPreview = libs.versions.minSdk.get()
        buildConfigField("String", "VERSION_NAME", "\"${libs.versions.appVersion.get()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    packaging {
        resources {
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/DEPENDENCIES"
        }
    }

    buildFeatures {
        buildConfig = true
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
    testImplementation(libs.test.junit)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.coroutines.android)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.moshi.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.moshi.kotlin)
    implementation(libs.annotation)
    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.okhttp.mockwebserver)
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
                artifactId = "uploadcare-android"
                version = libs.versions.appVersion.get()

                //withBuildIdentifier(), available in newer gradle versions.

                pom {
                    name.set("uploadcare-android")
                    url.set("https://github.com/uploadcare/uploadcare-android")
                    description.set("Android client library for the Uploadcare API.")
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
