package com.music.bitchord.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Settings for the Dynamic Island.
 *
 * A short set of rows, and that is all there is to say about it. The island used to fill a page of its own:
 * a live preview, a ladder of layouts, a switch for the cover, one for the transport, one for the
 * progress line, a rule about when to shrink, a transparency slider, six places to put the
 * capsule and a way to drag it somewhere else. Almost all of that was asking the same question
 * twice.
 *
 * The island is part of the screen now: it rests at the top centre and opens when the pointer
 * lands on it, so there is nothing to place and nothing to drag. What is left is what a person
 * genuinely has an opinion about: whether it is on, how big it is — overall, and how wide the
 * resting pill is, since that is what decides how much of a lyric line fits — and whether clicks
 * go through it.
 */
@Composable
internal fun DynamicIslandSettingsGroup() {
    val floating by DesktopDynamicIslandSettings.enabled.collectAsState()
    val scale by DesktopDynamicIslandSettings.scale.collectAsState()
    val collapsedWidth by DesktopDynamicIslandSettings.collapsedWidth.collectAsState()
    val hideWhenPaused by DesktopDynamicIslandSettings.hideWhenPaused.collectAsState()
    val clickThrough by DesktopDynamicIslandSettings.clickThrough.collectAsState()
    val expandWhenPlaying by DesktopDynamicIslandSettings.expandWhenPlaying.collectAsState()
    val karaokeSweep by DesktopDynamicIslandSettings.karaokeSweepEnabled.collectAsState()

    SettingsToggle(
        title = DesktopStrings["d_island_floating", "Floating island"],
        subtitle = DesktopStrings[
            "d_island_floating_subtitle",
            "A capsule that stays at the top of the screen while you work, and opens into the player when the pointer lands on it",
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

    IslandSliderRow(
        title = DesktopStrings["d_island_size", "Island size"],
        value = "${(scale * 100).toInt()}%",
        slider = scale,
        range = DesktopDynamicIslandSettings.MIN_SCALE..DesktopDynamicIslandSettings.MAX_SCALE,
        onValueChange = DesktopDynamicIslandSettings::setScale,
    )

    // Width on its own, rather than folding it into the size multiplier: a person with a wide
    // monitor usually wants more room for the words at the same height, and the two sliders
    // together say so without making one drag change both.
    IslandSliderRow(
        title = DesktopStrings["d_island_width", "Island width"],
        value = DesktopStrings.format(
            "d_island_width_value",
            collapsedWidth.toInt(),
            fallback = "%1\$d dp",
        ),
        slider = collapsedWidth,
        range = ISLAND_MIN_COLLAPSED_WIDTH..ISLAND_MAX_COLLAPSED_WIDTH,
        steps = islandWidthSteps(),
        onValueChange = DesktopDynamicIslandSettings::setCollapsedWidth,
    )

    SettingsToggle(
        title = DesktopStrings["d_island_expand_playing", "Open while playing"],
        subtitle = DesktopStrings[
            "d_island_expand_playing_subtitle",
            "Keeps the capsule open with the transport and scrub bar for the whole song. It then covers whatever sits at the top centre of the screen underneath — switch it off to keep the capsule as a lyric pill that opens under the pointer.",
        ],
        checked = expandWhenPlaying,
        onCheckedChange = DesktopDynamicIslandSettings::setExpandWhenPlaying,
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

/** A slider with its name on the left and its value on the right, as the crossfade row reads. */
@Composable
private fun IslandSliderRow(
    title: String,
    value: String,
    slider: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit,
) {
    if (!settingsRowVisible(title, value)) return
    Column(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text(value, color = DesktopSecondary, style = MaterialTheme.typography.bodySmall)
        }
        DesktopBareSlider(
            value = slider,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
        )
    }
}

/**
 * How far the width slider moves at a time, in dp. The stops are worked out from it and from the
 * slider's own ends, so the read-out lands on whole numbers and cannot drift out of step with the
 * range the geometry clamps to.
 */
private const val WIDTH_STEP_DP = 20f

/** The stops between the width slider's ends, at [WIDTH_STEP_DP] apart. */
private fun islandWidthSteps(): Int =
    ((ISLAND_MAX_COLLAPSED_WIDTH - ISLAND_MIN_COLLAPSED_WIDTH) / WIDTH_STEP_DP).toInt() - 1
