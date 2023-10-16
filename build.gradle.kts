// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        maven(url = "https://maven.google.com")
        google()
        mavenCentral()
    }
    dependencies {
        // Required manually provide Android's dependencies here that CodeQL's autobuild works correctly
        // https://github.com/github/codeql-action/issues/1417#issuecomment-1737158409
        classpath("com.android.tools.build:gradle:${libs.versions.gradleVersion.get()}")
        classpath(libs.kotlin.plugin)
        classpath(libs.navigation.safe.args.plugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("base")
    id("signing")
    id("maven-publish")
}

allprojects {
    repositories {
        mavenLocal() // look into local .m2 cache.
        google()
        mavenCentral()
    }
}

subprojects {

    // apply publishing plugins to all subprojects to global control of publishing repositories.
    apply(plugin = "base")
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    val isReleaseVersion = !version.toString().lowercase().endsWith("snapshot")

    tasks {
        // Always run tests as part of the `build` task.
        findByName("test")?.apply {
            named("check") {
                dependsOn(named("test"))
            }
        }
    }

    // Setup global publishing repository settings.
    signing {
        useGpgCmd()
        sign(publishing.publications)
    }

    publishing {
        repositories {
            maven {
                // Dynamically select either Maven Central or na Internal repository depending on the value of uploadcare.publish.type / UPLOADCARE_PUBLISH_TYPE
                name = "selected"

                // Allow deploying to a custom repository (for testing purposes)
                val publishInternally = project.findProperty("uploadcare.publish.type")?.toString() == "internal"
                val repositoryUrl = if (isReleaseVersion) {
                    val releaseMavenCentral = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val releaseInternal = (project.findProperty("uploadcare.publish.internal.release") ?: System.getenv("UPLOADCARE_PUBLISH_INTERNAL_RELEASE") ?: "") as String
                    if (releaseInternal != "" && publishInternally) releaseInternal else releaseMavenCentral
                } else {
                    val snapshotMavenCentral = "https://oss.sonatype.org/content/repositories/snapshots/"
                    val snapshotInternal = (project.findProperty("uploadcare.publish.internal.snapshot") ?: System.getenv("UPLOADCARE_PUBLISH_INTERNAL_SNAPSHOT") ?: "") as String
                    if (snapshotInternal != "" && publishInternally) snapshotInternal else snapshotMavenCentral
                }

                url = uri(repositoryUrl)

                credentials {
                    val mavenCentralUser = (project.findProperty("uploadcare.publish.sonatype.user") ?: System.getenv("UPLOADCARE_PUBLISH_SONATYPE_USER") ?: "") as String
                    val mavenCentralPass = (project.findProperty("uploadcare.publish.sonatype.pass") ?: System.getenv("UPLOADCARE_PUBLISH_SONATYPE_PASS") ?: "") as String
                    val internalUser = (project.findProperty("uploadcare.publish.internal.user") ?: System.getenv("UPLOADCARE_PUBLISH_INTERNAL_USER") ?: "") as String
                    val internalPass = (project.findProperty("uploadcare.publish.internal.pass") ?: System.getenv("UPLOADCARE_PUBLISH_INTERNAL_PASS") ?: "") as String
                    username = if (publishInternally) internalUser else mavenCentralUser
                    password = if (publishInternally) internalPass else mavenCentralPass
                }
            }
        }
    }
}
