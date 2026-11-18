/*
 * The desktop's own Compose UI — the player, lyrics, queue and the Home /
 * Explore / Library / Search pages.
 *
 * It began as a copy of :sharedUi, which the phone, tablet and TV keep drawing
 * from. The two now move separately: a desktop redesign lands here and never
 * reaches the phone, and the phone's player never moves the desktop's. Packages
 * and the resource class are unchanged from :sharedUi, so the desktop's imports
 * did not move; that is safe only because no module depends on both.
 */
/** Versions shared with the desktop modules; see the block in gradle.properties. */
fun property(name: String): String = providers.gradleProperty("bitchord.lib.$name").get()

/**
 * Which platform this build is *for*, which is the host unless `-Pbitchord.target` says otherwise.
 * The same rule desktopApp/build.gradle.kts uses, so a cross build cannot compile this module
 * against the other platform's pairing.
 */
val hostIsWindows = System.getProperty("os.name").contains("Windows", ignoreCase = true)
val targetOs: String = (providers.gradleProperty("bitchord.target").orNull ?: when {
    hostIsWindows -> "windows"
    else -> "linux"
}).lowercase()

/**
 * Both desktop targets now resolve Compose Multiplatform 1.12.2 / Skiko milestone
 * 150. Keeping this module compiled against the shipped version avoids the old
 * Matrix33 ABI split: bytecode built against Skiko 0.9 called an object-returning
 * `makeTranslate` that no longer exists on 0.150 and died on the first grown lyric.
 */
val cmp = when (targetOs) {
    "windows" -> property("compose.windows")
    else -> property("compose.linux")
}

// Haze 1.7.3 is the build that matches Compose 1.12's ShaderBrush ABI; Haze 1.7.2
// was pinned to the pre-1.12 createShader return type and crashed every blur with
// NoSuchMethodError. Both desktop platforms now use the same pair.
val haze = when (targetOs) {
    "windows" -> property("haze.windows")
    else -> property("haze.linux")
}

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }
    }

    applyDefaultHierarchyTemplate()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        // Kept from :sharedUi so the two trees line up file for file; with one
        // target it is simply the body of jvmMain.
        val jvmSharedMain by creating {
            dependsOn(commonMain.get())
        }
        jvmMain.get().dependsOn(jvmSharedMain)

        commonMain.dependencies {
            api(project(":shared"))
            api("org.jetbrains.compose.runtime:runtime:$cmp")
            api("org.jetbrains.compose.foundation:foundation:$cmp")
            api("org.jetbrains.compose.animation:animation:$cmp")
            api("org.jetbrains.compose.ui:ui:$cmp")
            // Both of these were pinned a full generation behind what the desktop packages actually
            // carry (material3 1.7.3 against a resolved 1.9.0, haze 1.3.1 against 1.7.x), which is
            // the drift `cmp` above exists to prevent. These are the shipped versions.
            api("org.jetbrains.compose.material3:material3:${property("material3")}")
            api("org.jetbrains.compose.material:material-icons-extended:${property("materialIcons")}")
            api("org.jetbrains.compose.components:components-resources:$cmp")
            // 2.9.6, not the 2.8.4 this used to name: same reason as material3.
            api("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.9.6")
            api("io.coil-kt.coil3:coil-compose:${property("coil")}")
            api("dev.chrisbanes.haze:haze:$haze")
            api("dev.chrisbanes.haze:haze-materials:$haze")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("coroutines")}")
            // collectAsStateWithLifecycle hops onto Dispatchers.Main, and Compose Desktop bundles no
            // Main dispatcher: without this every lifecycle collector threw on startup.
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${property("coroutines")}")
        }
        // See `cmp` above.
        jvmMain.dependencies {
            api("org.jetbrains.compose.ui:ui-desktop:$cmp")
        }
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation("junit:junit:4.13.2")
        }
    }
}

compose.resources {
    publicResClass = true
    // The same class :sharedUi generates, so the desktop's `Res` imports did not move.
    packageOfResClass = "com.music.bitchord.sharedui.resources"
    generateResClass = always
}
