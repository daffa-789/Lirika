package com.music.bitchord.desktop

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Everything the Dynamic Island remembers between runs.
 *
 * One object rather than a dozen loose preference reads, because three places have to agree on
 * the same state: the island itself, the menu hanging off the toolbar, and the Settings page.
 *
 * There are only a few preferences, and that is the point. The island used to carry a layout
 * ladder, a switch for each part of the capsule, a transparency slider, a rule about when to
 * shrink, six places to put it and a way to drag it elsewhere — knobs that all said the same thing,
 * which is "how much of this is showing, and where". The island is part of the screen now: it
 * rests at the top centre, appears once a song plays, and opens when the pointer lands on it. What
 * is left is only what a person genuinely has an opinion about: whether it is on, how wide the
 * lyric pill should be, whether it hides when paused, and whether clicks go through it.
 */
internal object DesktopDynamicIslandSettings {

    private val persistence = DesktopPersistence()

    /**
     * Whether the floating capsule is drawn over the page.
     *
     * The top-bar island is gone, so a profile that had only that one switched on has its old
     * preference carried over to the capsule rather than ending up with no island at all.
     */
    val enabled: MutableStateFlow<Boolean> = MutableStateFlow(
        persistence.boolean(KEY_ENABLED, true) || persistence.boolean(KEY_LEGACY_TOP_BAR, false),
    )

    /** Stays out of the way while playback is paused. */
    val hideWhenPaused: MutableStateFlow<Boolean> =
        MutableStateFlow(persistence.boolean(KEY_HIDE_WHEN_PAUSED, false))

    /**
     * Lets mouse clicks fall through the floating capsule to the window underneath it.
     *
     * With it on, the capsule is a readout only — no menu, no transport — and it is switched back
     * from Settings or the toolbar's island button, both of which live in windows the capsule
     * does not cover.
     */
    val clickThrough: MutableStateFlow<Boolean> =
        MutableStateFlow(persistence.boolean(KEY_CLICK_THROUGH, false))

    /**
     * The resting capsule's width in dp.
     *
     * The pill is a lyric strip: 420 dp fits a short line comfortably, and a wider monitor can ask
     * for more. The ends belong to the geometry (`islandCollapsedWidth`), which clamps the value on
     * the way in and on every read, so the slider and the glass cannot disagree about what is legal.
     */
    val collapsedWidth: MutableStateFlow<Float> = MutableStateFlow(
        islandCollapsedWidth(persistence.float(KEY_COLLAPSED_WIDTH, ISLAND_COLLAPSED_WIDTH)),
    )

    /** Sweep word-timed lyrics across the current line in the island. */
    val karaokeSweepEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(persistence.boolean(KEY_KARAOKE_SWEEP, true))

    fun toggle() = setEnabled(!enabled.value)

    fun setEnabled(value: Boolean) {
        persistence.saveBoolean(KEY_ENABLED, value)
        enabled.value = value
    }

    fun setHideWhenPaused(value: Boolean) = writeBoolean(KEY_HIDE_WHEN_PAUSED, value, hideWhenPaused)

    fun setClickThrough(value: Boolean) = writeBoolean(KEY_CLICK_THROUGH, value, clickThrough)

    fun setCollapsedWidth(value: Float) {
        val clamped = islandCollapsedWidth(value)
        persistence.saveFloat(KEY_COLLAPSED_WIDTH, clamped)
        collapsedWidth.value = clamped
    }

    fun setKaraokeSweepEnabled(value: Boolean) =
        writeBoolean(KEY_KARAOKE_SWEEP, value, karaokeSweepEnabled)

    private fun writeBoolean(key: String, value: Boolean, into: MutableStateFlow<Boolean>) {
        persistence.saveBoolean(key, value)
        into.value = value
    }

    private const val KEY_ENABLED = "floating_lyrics_enabled"

    /** Written by the old top-bar capsule. Read once, as a migration, never written again. */
    private const val KEY_LEGACY_TOP_BAR = "island_top_bar"
    private const val KEY_HIDE_WHEN_PAUSED = "island_hide_when_paused"
    private const val KEY_CLICK_THROUGH = "island_click_through"
    private const val KEY_COLLAPSED_WIDTH = "island_collapsed_width"
    private const val KEY_KARAOKE_SWEEP = "island_karaoke_sweep"
}
