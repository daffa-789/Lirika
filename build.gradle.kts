// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        /*
         * Overrides the D8/R8 that AGP bundles. The override was introduced when
         * AGP 8.10.9's R8 miscompiled NowPlayingScreen (36 parameters, ~5,500 dex
         * instructions, registers past v287). R8 8.13.23 fixed that register
         * allocation bug and reads Kotlin 2.3 @Metadata; keep it while the phone
         * build is still in the tree, even though the desktop build never runs D8.
         */
        classpath("com.android.tools:r8:8.13.23")
    }
}

extra["bitchordVersionCode"] = 33
extra["bitchordVersionName"] = "1.8.33"
extra["bitchordBetaSuffix"] = ""

plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.3.20" apply false
    id("org.jetbrains.kotlin.jvm") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.20" apply false
    // Compose Multiplatform 1.12.2 pairs with the Kotlin 2.3 toolchain and the
    // Skiko milestone used by both desktop targets. Haze 1.7.3 is the matching
    // blur library; older Haze 1.7.2 was pinned to the pre-1.12 shader ABI.
    id("org.jetbrains.compose") version "1.12.2" apply false
}
