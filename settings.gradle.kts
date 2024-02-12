pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    versionCatalogs {
        create("libs") {
            from("no.nordicsemi.android.gradle:version-catalog:1.9.11")
        }
    }
}
rootProject.name = "BLu Connect"

include(":app")
include(":scanner")
include(":blu:spec")
include(":blu:ui")
include(":blu:ble")
include(":blu:serialization")

if (file("../Android-Common-Libraries").exists()) {
    includeBuild("../Android-Common-Libraries")
}
if (file("../Android-BLE-Library").exists()) {
    includeBuild("../Android-BLE-Library")
}
