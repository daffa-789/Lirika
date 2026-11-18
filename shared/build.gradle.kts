import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/** JVM target for the desktop client. Android stays on 17; see androidTarget below. */
val desktopJvmTarget = JvmTarget.JVM_25

/** Versions shared with the desktop modules; see the block in gradle.properties. */
fun property(name: String): String = providers.gradleProperty("bitchord.lib.$name").get()

val ktor = property("ktor")
val coroutines = property("coroutines")
val serialization = property("serialization")

val includeAndroid = providers.gradleProperty("bitchord.android").orNull?.toBoolean() ?: false

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    // Declared here so the version comes from the root build; applied only in the
    // phone build below, because the plugins {} block cannot see local variables.
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.plugin.serialization")
}

if (includeAndroid) apply(plugin = "com.android.library")

kotlin {
    if (includeAndroid) {
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(desktopJvmTarget)
        }
    }

    // No Apple targets. An iOS build was cut from this tree: there was no iOS UI to build
    // (the phone's Compose UI lives in :app, which is Android-only, and :sharedUi had an empty
    // commonMain), so the XCFramework shipped a logic layer nobody could put a player on top of.
    applyDefaultHierarchyTemplate()

    sourceSets {
        val jvmSharedMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                // The network stack the lyrics providers and the YouTube Music client run on.
                api("io.ktor:ktor-client-okhttp:$ktor")
                api("io.ktor:ktor-client-websockets:$ktor")
                api("io.ktor:ktor-client-content-negotiation:$ktor")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktor")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
                api("org.jsoup:jsoup:${property("jsoup")}")
                // YouTube stream resolution: InnerTubeX's client catalog and cipher tiers, NewPipe as the fallback.
                api("com.github.MetrolistGroup.innertubex:innertubex:${property("innertubex")}")
                compileOnly("com.github.TeamNewPipe:NewPipeExtractor:${property("newpipe")}")
                // Compile-only: the application supplies the runtime artifact itself — the phone pulls
                // onnxruntime-android, and a desktop build that ever enables Automix brings its own.
                compileOnly("com.microsoft.onnxruntime:onnxruntime:1.20.0")
            }
        }
        if (includeAndroid) {
            androidMain.get().dependsOn(jvmSharedMain)
        }
        jvmMain.get().dependsOn(jvmSharedMain)

        commonMain.dependencies {
            api("io.ktor:ktor-client-core:$ktor")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmTest.dependencies {
            implementation("com.squareup.okhttp3:mockwebserver:5.3.2")
        }
    }
}

if (includeAndroid) {
    extensions.configure<com.android.build.gradle.LibraryExtension>("android") {
        namespace = "com.music.bitchord.shared"
        compileSdk = 36
    }
}
