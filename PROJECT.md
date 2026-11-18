# Project: Lirika (desktop-only)

## Architecture

Lirika is a Kotlin Multiplatform project whose only remaining target is the JVM desktop client.
Three modules are configured:

- **`:shared`** — pure-Kotlin models, networking, lyrics, playback logic and transition planning.
  - `commonMain`: models, equalizer, peak limiter, `TransitionPlanner` and other platform-free code.
  - `jvmSharedMain`: YouTube/InnerTubeX and NewPipe parsing, HTTP stack, lyrics providers and DSP helpers.
  - JVM target: Java 25.
- **`:desktopUi`** — the desktop's own Compose UI: player, lyrics, queue, Home, Explore, Library and Search.
- **`:desktopApp`** — application entry point, tray/island windows, native integrations and packaging
  (MSI, EXE, portable zip, DEB, RPM, AppImage helper).

CI workflows build the Windows and Linux packages only. There are no AGP plugins, Android SDK/NDK
requirements, phone modules, or APK artefacts. An earlier phase of this project restored an Android
client and an iOS target; both were later removed. The Kotlin package name `com.music.bitchord`
remains as an attribution trail.

## Features kept on desktop

- Windows Fluent caption / DWM backdrop and SMTC media keys through small native libraries.
- Dynamic Island lyrics overlay with tray toggle on Windows and Linux.
- Tray playback controls, MPRIS on Linux and StatusNotifier integration.
- YouTube Music-inspired player with restyled Home, Explore, Library and Search, a landscape Now
  Playing view, and the island as the out-of-window player.
- Manual crossfade and blend-on-skip; the analyser-driven Automix, its ONNX models and native library
  are gone.
- Last.fm scrobbling with user-supplied API credentials.
- Release manifests (`SHA256SUMS.txt`) generated for every tagged Windows/Linux build.

## Interface contracts

### `:desktopApp` ↔ `:shared`

- `implementation(project(":shared"))`
- JVM variant resolution: `jvm` target (Java 25).
- Key packages: `com.music.bitchord.data.model.*`, `com.music.bitchord.data.*`,
  `com.music.bitchord.playback.smart.*`.

### `:desktopApp` ↔ `:desktopUi`

- `implementation(project(":desktopUi"))`
- The UI module keeps the historical resource package `com.music.bitchord.sharedui.resources` so
  existing `Res` imports stay stable; no other module depends on it.

## Code layout

- `shared/` — shared JVM library (`commonMain`, `jvmSharedMain`, tests)
- `desktopUi/` — Compose UI library
- `desktopApp/` — application entry point and packaging tasks
- `desktopApp/packaging/` — installer and icon assets
- `.github/workflows/` — `check.yml`, `release.yml` and community workflows
- `gradle.properties` — shared dependency versions and JVM flags
- `settings.gradle.kts` — the three JVM modules
- `build.gradle.kts` — root plugin versions

## Verification

- `./gradlew :desktopApp:test :desktopUi:jvmTest :shared:jvmTest`
- `./gradlew :desktopApp:windowsPortable` (with a JDK 25 toolchain)
- CI: `check.yml` compiles/tests on Windows and compiles the Linux target on Ubuntu; `release.yml`
  publishes Windows and Linux packages on a `v*` tag.
