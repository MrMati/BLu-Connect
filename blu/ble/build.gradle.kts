plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidLibraryConventionPlugin.kt
    alias(libs.plugins.nordic.library)
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidKotlinConventionPlugin.kt
    alias(libs.plugins.nordic.kotlin)
}

android {
    namespace = "pl.mati.blu.transport_ble"
}

dependencies {
    implementation(project(":blu:spec"))
    implementation(project(":blu:serialization"))

    // Import BLE Library
    implementation(libs.nordic.ble.ktx)
    // BLE events are logged using Timber
    implementation(libs.timber)
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.protobuf.kotlin.lite)
}