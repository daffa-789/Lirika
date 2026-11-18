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

/*
 * This fork is desktop-first: the normal build configures only the JVM modules the
 * desktop client actually uses. The phone client is still in the tree as upstream
 * heritage, but AGP, its SDK and the mobile-only dependency graph are paid for only
 * when they are explicitly asked for:
 *
 *   ./gradlew :app:assembleDebug -Pbitchord.android=true
 *
 * Keeping the phone modules out of the default graph is the single biggest build-
 * time and configuration-time saving available here — the desktop never depended
 * on :sharedUi or :app in the first place.
 */
val includeAndroid = providers.gradleProperty("bitchord.android").orNull?.toBoolean() ?: false

include(":shared")
include(":desktopUi")
include(":desktopApp")

if (includeAndroid) {
    include(":sharedUi")

    val androidSdk: String? = file("local.properties")
        .takeIf { it.isFile }
        ?.let { f -> java.util.Properties().apply { f.inputStream().use(::load) } }
        ?.getProperty("sdk.dir")
        ?.trim()
        ?: System.getenv("ANDROID_HOME")
        ?: System.getenv("ANDROID_SDK_ROOT")

    if (androidSdk != null && file(androidSdk).isDirectory) {
        include(":app")
    } else {
        logger.lifecycle("bitchord.android=true but no Android SDK found - :app is not included.")
    }
} else {
    logger.lifecycle("Desktop-only build. Pass -Pbitchord.android=true to include the phone modules.")
}
