// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.4.21" apply false
    id("org.jetbrains.kotlin.jvm") version "2.4.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.21" apply false
    // Compose Multiplatform 1.12.2 is built with Kotlin 2.4.21 and the Skiko milestone used by
    // both desktop targets. Haze 2.1.0 is the matching blur library, compiled against the same
    // Kotlin and Compose pair.
    id("org.jetbrains.compose") version "1.12.2" apply false
}
