package com.music.bitchord.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * Settings for the Dynamic Island.
 *
 * A short set of rows, and that is all there is to say about it. The island used to fill a page of its own:
 * a live preview, a ladder of layouts, a switch for the cover, one for the transport, one for the
 * progress line, a rule about when to shrink, a transparency slider, six places to put the
 * capsule and a way to drag it somewhere else. Almost all of that was asking the same question
 * twice.
 *
 * The island is part of the screen now: it rests at the top centre at a fixed, readable width,
 * appears once a song starts playing, and opens when the pointer lands on it. There is nothing to
 * place, drag, resize or keep open by hand — what remains is whether it is on, whether it hides
 * when paused, and whether clicks go through it.
 */
@Composable
internal fun DynamicIslandSettingsGroup() {
    val floating by DesktopDynamicIslandSettings.enabled.collectAsState()
    val hideWhenPaused by DesktopDynamicIslandSettings.hideWhenPaused.collectAsState()
    val clickThrough by DesktopDynamicIslandSettings.clickThrough.collectAsState()
    val karaokeSweep by DesktopDynamicIslandSettings.karaokeSweepEnabled.collectAsState()

    SettingsToggle(
        title = DesktopStrings["d_island_floating", "Floating island"],
        subtitle = DesktopStrings[
            "d_island_floating_subtitle",
            "A capsule that stays at the top of the screen once a song plays, and opens into the player when the pointer lands on it",
        ],
        checked = floating,
        onCheckedChange = DesktopDynamicIslandSettings::setEnabled,
    )
    if (!floating) return

    SettingsToggle(
        title = DesktopStrings["d_island_karaoke_sweep", "Karaoke word highlight"],
        subtitle = DesktopStrings[
            "d_island_karaoke_sweep_subtitle",
            "Highlights each timed word in the floating island while it is sung",
        ],
        checked = karaokeSweep,
        onCheckedChange = DesktopDynamicIslandSettings::setKaraokeSweepEnabled,
    )

    SettingsToggle(
        title = DesktopStrings["d_island_hide_paused", "Hide while paused"],
        subtitle = DesktopStrings[
            "d_island_hide_paused_subtitle",
            "Takes the capsule off the screen whenever playback is paused",
        ],
        checked = hideWhenPaused,
        onCheckedChange = DesktopDynamicIslandSettings::setHideWhenPaused,
    )

    if (DesktopPlatform.isWindows) {
        SettingsToggle(
            title = DesktopStrings["d_island_click_through", "Click-through"],
            subtitle = DesktopStrings[
                "d_island_click_through_subtitle",
                "Clicks over the capsule go to the window underneath. Switch it off here to grab the capsule again.",
            ],
            checked = clickThrough,
            onCheckedChange = DesktopDynamicIslandSettings::setClickThrough,
        )
    }
}
