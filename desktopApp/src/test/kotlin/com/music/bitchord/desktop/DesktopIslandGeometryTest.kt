package com.music.bitchord.desktop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The island's geometry: where it rests, and which way it opens.
 *
 * The island is pinned to the top centre of the screen, so these are the rules that make it read
 * as part of the display rather than as a widget that happens to be floating there.
 */
class DesktopIslandGeometryTest {

    private val screen = IslandScreen(left = 0f, top = 0f, right = 1920f, bottom = 1040f)

    private val collapsedWidth = ISLAND_COLLAPSED_WIDTH
    private val expandedWidth = ISLAND_EXPANDED_WIDTH
    private val collapsedHeight = ISLAND_COLLAPSED_HEIGHT
    private val expandedHeight = ISLAND_EXPANDED_HEIGHT

    @Test
    fun `the capsule rests at the top centre of the screen`() {
        val rest = islandRestRect(screen, collapsedWidth, collapsedHeight)
        assertEquals((1920f - collapsedWidth) / 2f, rest.left, 0.01f)
        assertEquals(screen.top, rest.top, 0.01f)
        assertEquals(collapsedWidth, rest.width, 0.01f)
    }

    @Test
    fun `the capsule opens around itself, so its centre never moves`() {
        val rest = islandRestRect(screen, collapsedWidth, collapsedHeight)
        val grown = islandRestRect(screen, expandedWidth, expandedHeight)
        assertEquals(
            rest.left + rest.width / 2f,
            grown.left + grown.width / 2f,
            0.01f,
        )
        assertEquals(rest.top, grown.top, 0.01f, "the capsule should open downwards from the top")
    }

    @Test
    fun `the capsule touches the top edge with no gap`() {
        val rest = islandRestRect(screen, collapsedWidth, collapsedHeight)
        assertEquals(screen.top, rest.top, 0.01f)
        assertTrue(rest.left > screen.left, "the capsule should not touch the left edge")
        assertTrue(rest.right < screen.right, "the capsule should not touch the right edge")
    }

    @Test
    fun `a capsule too big for the screen starts at its edge instead of off it`() {
        // A capsule wider than the screen cannot fit, but it must still begin on the screen
        // rather than off the side of it.
        val wide = islandPlacedRect(screen, screen.right * 2f, collapsedHeight)
        assertTrue(wide.left >= screen.left, "the capsule should not start off the left of the screen")

        val tall = islandPlacedRect(screen, collapsedWidth, screen.bottom * 2f)
        assertTrue(tall.top >= screen.top, "the capsule should not start off the top of the screen")
    }

    @Test
    fun `a capsule that fits is held entirely inside the screen`() {
        val placed = islandPlacedRect(screen, expandedWidth, expandedHeight)
        assertTrue(placed.left >= screen.left)
        assertTrue(placed.right <= screen.right)
        assertTrue(placed.top >= screen.top)
        assertTrue(placed.bottom <= screen.bottom)
    }

    @Test
    fun `the capsule has one fixed size, wide enough to read and wider when it opens`() {
        assertEquals(420f, collapsedWidth, 0.01f)
        assertTrue(expandedWidth > collapsedWidth)
        assertTrue(expandedHeight > collapsedHeight)
    }

    @Test
    fun `the island stays off the screen until a song has played`() {
        assertTrue(
            !islandWindowVisible(hideWhenPaused = false, isPlaying = false, playbackStarted = false),
            "opening the app must not put a capsule over the display",
        )
        assertTrue(islandWindowVisible(
            hideWhenPaused = false, isPlaying = false, playbackStarted = true,
        ), "pausing after a song has played should not take the island away")
        assertTrue(islandWindowVisible(
            hideWhenPaused = false, isPlaying = true, playbackStarted = true,
        ))
    }

    @Test
    fun `only the explicit pause preference hides the island after playback`() {
        assertTrue(islandWindowVisible(
            hideWhenPaused = true, isPlaying = true, playbackStarted = true,
        ))
        assertTrue(!islandWindowVisible(
            hideWhenPaused = true, isPlaying = false, playbackStarted = true,
        ))
    }

    @Test
    fun `the capsule opens on the pointer, menu or scrub, never on playback alone`() {
        assertTrue(!islandWantsExpanded(
            clickThrough = false, hovered = false, menuOpen = false, seeking = false,
        ), "playing alone leaves a wide, resting pill")
        assertTrue(islandWantsExpanded(
            clickThrough = false, hovered = true, menuOpen = false, seeking = false,
        ))
        assertTrue(islandWantsExpanded(
            clickThrough = false, hovered = false, menuOpen = true, seeking = false,
        ))
        assertTrue(islandWantsExpanded(
            clickThrough = false, hovered = false, menuOpen = false, seeking = true,
        ))
    }

    @Test
    fun `click-through outranks every reason to open`() {
        assertTrue(!islandWantsExpanded(
            clickThrough = true, hovered = true, menuOpen = true, seeking = true,
        ))
    }

    @Test
    fun `the controls wait for the pointer`() {
        assertTrue(islandShowsControls(hovered = true, menuOpen = false, seeking = false))
        assertTrue(islandShowsControls(hovered = false, menuOpen = true, seeking = false))
        assertTrue(islandShowsControls(hovered = false, menuOpen = false, seeking = true))
        assertTrue(!islandShowsControls(hovered = false, menuOpen = false, seeking = false))
    }
}
