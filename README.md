<div align="center">
  <img src="Logo.png" alt="Lirika logo" width="96" />

  # Lirika

  **A desktop music player centered on YouTube Music.**<br />
  Built for Windows and Linux.

  [Download](https://github.com/daffa-789/Lirika/releases/latest) · [Features](#features) · [Build from source](#build-from-source) · [Contributing](CONTRIBUTING.md)

  ![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue.svg)
  ![Windows](https://img.shields.io/badge/platform-Windows-0078D4.svg)
  ![Linux](https://img.shields.io/badge/platform-Linux-FCC624.svg)
</div>

> Lirika is an independent project and is not affiliated with or endorsed by Google or YouTube.

## About

Lirika is a desktop-first music player for people who use YouTube Music. Sign in with Google to access your YouTube Music account, search for music, and keep playback controls and lyrics close while you work. The desktop client targets Windows and Linux.

## Features

### YouTube Music

- Search, browse, and play music from YouTube Music.
- Use a single, straightforward Google sign-in flow for YouTube Music.
- Open supported YouTube video links directly, including videos outside the YouTube Music catalogue.
- Use available YouTube captions as a fallback lyric source for video playback.

### Listening experience

- Synchronized lyrics from supported lyric sources.
- A floating mini-player with playback controls and rolling lyrics.
- Animated artwork and album-art-based color themes.
- Local music library and offline downloads.
- Playback options including crossfade, playback speed, skip silence, sleep timer, and equalizer integration.

### Windows and Linux integration

- Windows media keys and system media controls.
- Linux MPRIS playback controls and system tray support.
- Resizable desktop interface with dedicated Home, Search, Explore, Library, and Now Playing views.

## Downloads

Check the [latest release](https://github.com/daffa-789/Lirika/releases/latest) for the packages available for that version.

| Platform | Available package formats |
| --- | --- |
| Windows x64 | MSI installer, EXE installer, portable ZIP |
| Linux x64 | DEB, RPM, AppImage |

## Build from source

A JDK 25 installation is required. From the repository root:

```bash
./gradlew :desktopApp:run
./gradlew :desktopApp:test
```

On Windows PowerShell, use the Gradle wrapper for Windows:

```powershell
.\gradlew.bat :desktopApp:run
.\gradlew.bat :desktopApp:packageMsi
.\gradlew.bat :desktopApp:windowsPortable
```

For Linux packaging and contributor guidance, see [Contributing](CONTRIBUTING.md).

## About this fork

Lirika is based on [BitChord](https://github.com/kushagrasinghx/BitChord) by Kushagra Singh. This fork focuses on a streamlined desktop experience for YouTube Music on Windows and Linux. It retains the upstream GPL-3.0 license and required project attributions.

See [Additional documentation](ADDITIONAL.md) for the lyrics translation guide and project references.

## License and disclaimer

Lirika is distributed under the [GNU General Public License v3.0](LICENSE). YouTube and YouTube Music are third-party services. Users are responsible for complying with applicable laws and service terms.
