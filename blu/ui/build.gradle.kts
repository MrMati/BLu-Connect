plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidFeatureConventionPlugin.kt
    alias(libs.plugins.nordic.feature)
    // https://developer.android.com/kotlin/parcelize
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "pl.mati.blu.ui"
}

dependencies {
    implementation(project(":blu:spec"))
    implementation(project(":scanner"))

    implementation(libs.nordic.theme)
    implementation(libs.nordic.logger)
    implementation(libs.nordic.uilogger)
    implementation(libs.nordic.navigation)
    implementation(libs.nordic.permissions.ble)
    implementation(libs.nordic.log.timber)

    implementation(libs.androidx.compose.material.iconsExtended)

    implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.8")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.8")
    implementation("com.patrykandpatrick.vico:core:2.0.0-alpha.8")
}