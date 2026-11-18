package com.music.bitchord.desktop

import com.music.bitchord.data.lyrics.LyricLine
import com.music.bitchord.data.lyrics.LyricWord
import kotlin.test.Test
import kotlin.test.assertEquals

class DesktopIslandLyricsTest {

    @Test
    fun `line-synced lyrics sweep across the line`() {
        val lines = listOf(
            LyricLine(timeMs = 1_000L, text = "first"),
            LyricLine(timeMs = 5_000L, text = "second"),
        )
        val now = IslandLines(activeIndex = 0, raw = lines, fallback = "")

        assertEquals(0f, islandLineReveal(now, 1_000L))
        assertEquals(0.5f, islandLineReveal(now, 3_000L))
        assertEquals(1f, islandLineReveal(now, 5_000L))
    }

    @Test
    fun `a stamped end wins over the gap to the next line`() {
        val lines = listOf(
            LyricLine(timeMs = 1_000L, text = "held", sungUntilMs = 2_000L),
            LyricLine(timeMs = 9_000L, text = "later"),
        )
        val now = IslandLines(activeIndex = 0, raw = lines, fallback = "")

        assertEquals(0.5f, islandLineReveal(now, 1_500L))
        assertEquals(1f, islandLineReveal(now, 2_000L))
    }

    @Test
    fun `word-synced lyrics use their own timings`() {
        val word = LyricWord(startMs = 1_000L, endMs = 2_000L, text = "sing")
        val line = LyricLine(timeMs = 1_000L, text = "sing", words = listOf(word))
        val now = IslandLines(activeIndex = 0, raw = listOf(line), fallback = "")

        assertEquals(0f, islandLineReveal(now, 1_000L))
        assertEquals(0.5f, islandLineReveal(now, 1_500L))
        assertEquals(1f, islandLineReveal(now, 2_000L))
    }
}
