package com.music.bitchord.desktop

/**
 * The island's geometry, with no window manager in it.
 *
 * Everything here works in dp, because everything the island draws is in dp; the window layer
 * converts once, at the end. Keeping the arithmetic apart from AWT is what makes it checkable:
 * the rule that decides where the capsule rests and which way it grows is the one that makes it
 * feel stuck to the screen rather than like a widget that happens to be floating there.
 */

/** A rectangle in dp. */
internal class IslandRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
}

/** A screen's work area, in dp. */
internal class IslandScreen(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
)

/**
 * Where the capsule rests on a screen, for a given size.
 *
 * The island is pinned to the very top centre of the screen: it is part of the display, the way a
 * Dynamic Island is part of the phone, and not a window that can be pushed around. Its top edge
 * touches the screen's work area — no margin, no visible gap — and it opens downward from there.
 * Its centre stays put horizontally while it grows, so the capsule opens around itself.
 */
internal fun islandRestRect(
    screen: IslandScreen,
    width: Float,
    height: Float,
): IslandRect {
    val left = (screen.left + screen.right) / 2f - width / 2f
    val top = screen.top
    return IslandRect(left, top, left + width, top + height)
}

/**
 * The capsule's rect once it has been kept inside the screen.
 *
 * The clamp keeps a capsule wider than the screen itself from hanging off either side; the top
 * is left exactly at the work area's edge, with no margin to show as a gap.
 */
internal fun islandPlacedRect(
    screen: IslandScreen,
    width: Float,
    height: Float,
): IslandRect {
    val rest = islandRestRect(screen, width, height)
    val left = rest.left.coerceIn(
        screen.left,
        (screen.right - rest.width).coerceAtLeast(screen.left),
    )
    val top = rest.top.coerceIn(
        screen.top,
        (screen.bottom - rest.height).coerceAtLeast(screen.top),
    )
    return IslandRect(left, top, left + rest.width, top + rest.height)
}

