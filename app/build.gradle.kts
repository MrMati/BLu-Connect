plugins {
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidApplicationComposeConventionPlugin.kt
    alias(libs.plugins.nordic.application.compose)
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/AndroidHiltConventionPlugin.kt
    alias(libs.plugins.nordic.hilt)
}

android {
    namespace = "pl.mati.blu"
    defaultConfig {
        applicationId = "pl.mati.blu.bluconnect"
        resourceConfigurations.add("en")
        minSdk = 33
    }
}

dependencies {
    implementation(project(":blu:spec"))
    implementation(project(":blu:ui"))
    implementation(project(":blu:ble"))

    implementation(libs.nordic.theme)
    implementation(libs.nordic.navigation)

    implementation(libs.timber)

    implementation(libs.androidx.activity.compose)
}