pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // thank you stack overflow for this: https://stackoverflow.com/questions/71879563/how-to-add-https-jitpack-io-library-to-android-studio-version-bumble-bee
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

rootProject.name = "Splash"
include(":app")
 