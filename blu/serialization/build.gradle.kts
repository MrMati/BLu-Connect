import com.google.protobuf.gradle.proto

plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidLibraryConventionPlugin.kt
    alias(libs.plugins.nordic.library)
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidKotlinConventionPlugin.kt
    alias(libs.plugins.nordic.kotlin)

    alias(libs.plugins.protobuf)
}

android {
    namespace = "pl.mati.blu.serialization"

    sourceSets {
        named("main") {
            java {
                srcDirs("build/generated/source/proto/main/java")
            }
            kotlin {
                srcDirs("build/generated/source/proto/main/kotlin")
            }
            proto {
                srcDir("src/main/protos")
            }
        }
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}


dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.protobuf.protoc)

    testImplementation(libs.junit4)
}