pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
}
rootProject.name = "library";
include(":app");