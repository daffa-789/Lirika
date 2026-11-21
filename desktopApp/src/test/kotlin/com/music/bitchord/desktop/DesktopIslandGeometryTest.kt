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
    fun `the capsule is wider when it opens than when it rests`() {
        assertTrue(expandedWidth > collapsedWidth)
        assertTrue(expandedHeight > collapsedHeight)
    }

    @Test
    fun `the resting width is held inside the range the slider offers`() {
        // The number comes from a preference file, which a hand-edit or an older build can fill
        // with anything; the capsule must still end up somewhere sane.
        assertEquals(ISLAND_MIN_COLLAPSED_WIDTH, islandCollapsedWidth(1f), 0.01f)
        assertEquals(ISLAND_MIN_COLLAPSED_WIDTH, islandCollapsedWidth(239f), 0.01f)
        assertEquals(420f, islandCollapsedWidth(420f), 0.01f)
        assertEquals(ISLAND_MAX_COLLAPSED_WIDTH, islandCollapsedWidth(5_000f), 0.01f)
    }

    @Test
    fun `widening the pill never makes the open face narrower`() {
        // The base face is 480 dp, so a pill set past it would otherwise shrink the moment the
        // pointer landed on it.
        assertEquals(ISLAND_EXPANDED_WIDTH, islandExpandedWidth(320f), 0.01f)
        assertEquals(700f, islandExpandedWidth(700f), 0.01f)
        assertTrue(islandExpandedWidth(600f) >= islandCollapsedWidth(600f))
        assertTrue(islandExpandedWidth(1_000f) >= islandCollapsedWidth(1_000f))
    }

    @Test
    fun `every width the slider offers stays centred and on screen`() {
        var width = ISLAND_MIN_COLLAPSED_WIDTH
        while (width <= ISLAND_MAX_COLLAPSED_WIDTH) {
            val rest = islandPlacedRect(screen, islandCollapsedWidth(width), collapsedHeight)
            val open = islandPlacedRect(screen, islandExpandedWidth(width), expandedHeight)
            assertEquals(rest.left + rest.width / 2f, open.left + open.width / 2f, 0.01f)
            assertTrue(rest.left >= screen.left && rest.right <= screen.right)
            assertTrue(open.left >= screen.left && open.right <= screen.right)
            width += 20f
        }
    }

    @Test
    fun `the island keeps its place before a song loads`() {
        // A one-pixel window cannot be pressed back into view, so "nothing playing" is a pill on
        // screen rather than a vanished capsule.
        assertTrue(islandWindowVisible(hideWhenPaused = false, isPlaying = false))
    }

    @Test
    fun `only the explicit pause preference hides the island`() {
        assertTrue(islandWindowVisible(hideWhenPaused = true, isPlaying = true))
        assertTrue(!islandWindowVisible(hideWhenPaused = true, isPlaying = false))
    }

    @Test
    fun `the capsule opens on the pointer and, when asked, on playback`() {
        val resting = islandWantsExpanded(
            clickThrough = false, hovered = false, menuOpen = false,
            seeking = false, isPlaying = true, expandWhenPlaying = false,
        )
        assertTrue(!resting, "a lyric pill rests closed")

        assertTrue(islandWantsExpanded(
            clickThrough = false, hovered = true, menuOpen = false,
            seeking = false, isPlaying = false, expandWhenPlaying = false,
        ))
        assertTrue(islandWantsExpanded(
            clickThrough = false, hovered = false, menuOpen = true,
            seeking = false, isPlaying = false, expandWhenPlaying = false,
        ))
        assertTrue(islandWantsExpanded(
            clickThrough = false, hovered = false, menuOpen = false,
            seeking = true, isPlaying = false, expandWhenPlaying = false,
        ))
        assertTrue(islandWantsExpanded(
            clickThrough = false, hovered = false, menuOpen = false,
            seeking = false, isPlaying = true, expandWhenPlaying = true,
        ), "playback opens it when the preference is on")
        assertTrue(!islandWantsExpanded(
            clickThrough = false, hovered = false, menuOpen = false,
            seeking = false, isPlaying = false, expandWhenPlaying = true,
        ), "a paused song closes it again")
    }

    @Test
    fun `click-through outranks every reason to open`() {
        assertTrue(!islandWantsExpanded(
            clickThrough = true, hovered = true, menuOpen = true,
            seeking = true, isPlaying = true, expandWhenPlaying = true,
        ))
    }

    @Test
    fun `the controls wait for the pointer even while playback holds the capsule open`() {
        // Being open is not the same as being used: a capsule open because a song is running shows
        // the cover and the lyric, and nothing over the window underneath until it is reached for.
        assertTrue(islandShowsControls(hovered = true, menuOpen = false, seeking = false))
        assertTrue(islandShowsControls(hovered = false, menuOpen = true, seeking = false))
        assertTrue(islandShowsControls(hovered = false, menuOpen = false, seeking = true))
        assertTrue(!islandShowsControls(hovered = false, menuOpen = false, seeking = false))
    }
}
