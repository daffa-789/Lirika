pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // JDK 25 is now the project toolchain. Without a resolver, Gradle only finds
        // JDKs installed in its known locations; the Temurin 25 used to verify this
        // tree lives under ~/.jdks and would otherwise be invisible unless JAVA_HOME
        // is manually changed. Foojay resolves the toolchain from the vendor API.
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // NewPipeExtractor is published via JitPack.
        maven("https://jitpack.io")
    }
}

rootProject.name = "Lirika"

// Desktop-only project: three JVM modules, no phone modules and no AGP.
include(":shared")
include(":desktopUi")
include(":desktopApp")
