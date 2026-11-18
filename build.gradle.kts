// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.3.20" apply false
    id("org.jetbrains.kotlin.jvm") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.20" apply false
    // Compose Multiplatform 1.12.2 pairs with the Kotlin 2.3 toolchain and the
    // Skiko milestone used by both desktop targets. Haze 1.7.3 is the matching
    // blur library; older Haze 1.7.2 was pinned to the pre-1.12 shader ABI.
    id("org.jetbrains.compose") version "1.12.2" apply false
}
