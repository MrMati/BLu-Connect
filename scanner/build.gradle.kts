plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidFeatureConventionPlugin.kt
    alias(libs.plugins.nordic.feature)
    // https://developer.android.com/kotlin/parcelize
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "pl.mati.blu.scanner"
}

dependencies {
    implementation(libs.nordic.core)
    implementation(libs.nordic.theme)
    implementation(libs.nordic.scanner)
    implementation(libs.nordic.navigation)
    implementation(libs.nordic.permissions.ble)

    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
}