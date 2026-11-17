package com.music.bitchord.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.data.lyrics.LyricLine
import com.music.bitchord.data.model.Song
import com.music.bitchord.ui.icons.BitChordIcons

/**
 * The capsule's measurements, in dp, before the user's size multiplier.
 *
 * Two sizes, not three: the pill the island rests at, and the face it grows into. The island used
 * to carry a Full / Compact / Mini ladder and a switch saying which of them to shrink to — four
 * ways of asking "how much of the song is showing". A Dynamic Island has one shape that changes
 * with what is happening to it, and so does this one.
 */
internal const val ISLAND_COLLAPSED_WIDTH = 320f
internal const val ISLAND_COLLAPSED_HEIGHT = 62f
internal const val ISLAND_EXPANDED_WIDTH = 480f
internal const val ISLAND_EXPANDED_HEIGHT = 150f

/**
 * The three lines the hovering island can show, and the one line the resting island shows.
 *
 * Only one of the two faces is composed at a time, so carrying the neighbouring lines here costs
 * nothing while the island rests; it is what lets the open face roll from line to line with a
 * transition instead of hard-cutting the text.
 */
internal class IslandLines(
    val activeIndex: Int,
    private val raw: List<LyricLine>,
    private val fallback: String,
) {
    val previous: String get() = raw.getOrNull(activeIndex - 1)?.text?.takeIf { it.isNotBlank() } ?: "♪"
    val current: String
        get() = raw.getOrNull(activeIndex)?.text?.takeIf { it.isNotBlank() } ?: fallback
    val next: String get() = raw.getOrNull(activeIndex + 1)?.text?.takeIf { it.isNotBlank() } ?: "♪"

    /** The lyric at [index], or null when the track has no real line there. */
    fun lineAt(index: Int): String? = raw.getOrNull(index)?.text?.takeIf { it.isNotBlank() }

    fun at(index: Int): IslandLines = IslandLines(index, raw, fallback)
}

/**
 * The index of the line being sung at [positionMs], or -1 before the first one.
 *
 * A binary search rather than a walk: this runs on every playhead tick for a song that can carry
 * several hundred lines, and the lines are sorted by time by construction. The old linear scan
 * restarted from the top four times a second.
 */
internal fun activeLyricIndex(lines: List<LyricLine>, positionMs: Long): Int {
    var low = 0
    var high = lines.size - 1
    var found = -1
    while (low <= high) {
        val mid = (low + high) ushr 1
        if (lines[mid].timeMs <= positionMs) {
            found = mid
            low = mid + 1
        } else {
            high = mid - 1
        }
    }
    return found
}

private fun islandLines(lines: List<LyricLine>?, song: Song?, activeIndex: Int): IslandLines =
    IslandLines(
        activeIndex = activeIndex,
        raw = lines.orEmpty(),
        fallback = song?.let { "${it.title} — ${it.artist}" }.orEmpty(),
    )

/**
 * How far through the current song the playhead is.
 *
 * The player reports its duration alongside the position, so the hairline is the song's real
 * progress; the lyric's own last timestamp is only a fallback for a stream that never told us.
 */
internal fun islandProgress(positionMs: Long, durationMs: Long): Float {
    if (durationMs > 0L) return (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    val line = DesktopIslandState.lyricsLines.value.orEmpty()
    val last = line.lastOrNull()?.timeMs ?: return 0f
    if (last <= 0L) return 0f
    return (positionMs.toFloat() / last).coerceIn(0f, 1f)
}

/* ------------------------------------------------------------------------------------------------
 * The capsule itself
 * --------------------------------------------------------------------------------------------- */

/**
 * One capsule, drawn at whichever size the island's window gives it.
 *
 * The shell fills whatever it is handed, so the glass is always exactly as wide as the window that
 * holds it — there is no state in which the capsule is bigger than its own frame, which is what
 * used to happen while the window resized ahead of the drawing.
 *
 * Inside the shell sit two faces, drawn on top of each other: the pill the island rests at, and the
 * full face it grows into. How far along that growth is, 0 for the pill and 1 for the full face, is
 * measured from the capsule's real width rather than taken from the animation that resized the
 * window — so the drawing and the frame that holds it can never disagree.
 *
 * [positionMs] and [progress] are lambdas, not values, and that is the whole performance story of
 * this file. The engine publishes a playhead four times a second; passed as a value it recomposed
 * the entire capsule — artwork, transport row, animated lyric block — on every tick. Read inside
 * `derivedStateOf` and `drawBehind` instead, a tick costs one hairline redraw, and a full
 * recomposition happens only when the *line being sung* changes.
 */
@Composable
internal fun DesktopIslandCard(
    song: Song?,
    lines: List<LyricLine>?,
    positionMs: () -> Long,
    durationMs: () -> Long,
    isPlaying: Boolean,
    repeatMode: DesktopRepeatMode,
    scale: Float,
    hovered: Boolean,
    /** The window's own morph, 0 at rest and 1 open — the only source of truth for which face to draw. */
    expansion: Float,
    interactive: Boolean,
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onToggleRepeat: () -> Unit = {},
    /** Null leaves the expanded face without a scrub row, as a click-through capsule wants. */
    onSeek: ((Float) -> Unit)? = null,
    onSeekStarted: () -> Unit = {},
    onSeekFinished: () -> Unit = {},
    modifier: Modifier = Modifier,
    menu: (@Composable BoxScope.() -> Unit)? = null,
) {
    // Re-read only when the sung line changes, not on every playhead tick.
    val activeIndex by remember(lines, song) {
        derivedStateOf { activeLyricIndex(lines.orEmpty(), positionMs()) }
    }
    val now = remember(lines, song, activeIndex) { islandLines(lines, song, activeIndex) }
    // The same volume the dock and the OS media keys write; no second source of truth.
    val volume by DesktopPlayerHost.volumeLevel.collectAsState()

    // A flat, opaque glass: no artwork palette read, no gradient to paint, no shadow to blur.
    // Those were the expensive parts of a surface that is on screen for the whole song.
    val glass = if (hovered) Color(0xFF1B1B20) else Color(0xFF15151A)

    // The face is worked out from the width the capsule was actually given, so the drawing and the
    // window that holds it are always the same size, to the pixel.
    val density = LocalDensity.current
    val measured = remember { mutableFloatStateOf(0f) }
    val collapsed = ISLAND_COLLAPSED_WIDTH * scale
    val span = (ISLAND_EXPANDED_WIDTH - ISLAND_COLLAPSED_WIDTH) * scale

    Box(
        modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                val width = with(density) { size.width.toDp() }.value
                measured.floatValue = if (span <= 0f) {
                    0f
                } else {
                    ((width - collapsed) / span).coerceIn(0f, 1f)
                }
            }
            .clip(RectangleShape)
            .background(glass)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (hovered) 0.26f else 0.12f),
                shape = RectangleShape,
            ),
    ) {
        // Switch on the same expansion that sized the window. Measuring the box here disagreed
        // with AWT's bounds on scaled displays, so the open face was drawn inside a closed frame.
        val expandedFace = expansion >= 0.5f
        // Fill the window, don't hand the face a fixed 480.dp slab. The window is sized with the
        // user's scale multiplier, so a fixed slab is narrower than its frame whenever scale > 1:
        // anchored top-start it leaves the slack on the right, and the lyrics, seek row and
        // controls all read as shifted left of the capsule. Filling the window makes the face and
        // the glass the same box, so the centred content is centred on the capsule itself.
        if (expandedFace) {
            ExpandedIslandFace(
                song = song,
                now = now,
                positionMs = positionMs,
                durationMs = durationMs,
                isPlaying = isPlaying,
                repeatMode = repeatMode,
                volume = volume,
                onVolumeChange = { DesktopPlayerHost.onVolumeChange(it) },
                face = measured,
                interactive = interactive,
                onSeek = onSeek,
                onSeekStarted = onSeekStarted,
                onSeekFinished = onSeekFinished,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onToggleRepeat = onToggleRepeat,
            )
        } else {
            CollapsedIslandFace(song = song, now = now)
        }

        if (menu != null) {
            Box(Modifier.align(Alignment.TopEnd)) {
                menu(this)
            }
        }
    }
}

/**
 * The hovered face: a bigger cover and the rolling lyric lines on the left; the transport,
 * volume and seek row appear only while the pointer is on the capsule. The resting pill stays
 * artwork + lyrics alone.
 */
@Composable
@Suppress("UNUSED_PARAMETER")
private fun ExpandedIslandFace(
    song: Song?,
    now: IslandLines,
    positionMs: () -> Long,
    durationMs: () -> Long,
    isPlaying: Boolean,
    repeatMode: DesktopRepeatMode,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    face: FloatState,
    interactive: Boolean,
    onSeek: ((Float) -> Unit)?,
    onSeekStarted: () -> Unit,
    onSeekFinished: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleRepeat: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        // A full-width centred box holds the lyrics; the cover is overlaid at the start so it
        // cannot push the words off the face's centre line.
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            key(song?.videoId) {
                AnimatedContent(
                    targetState = now.activeIndex,
                    transitionSpec = {
                        (slideInVertically(animationSpec = tween(LYRIC_ROLL_MS)) { it / 3 } +
                            fadeIn(tween(LYRIC_ROLL_MS))).togetherWith(
                            slideOutVertically(animationSpec = tween(LYRIC_ROLL_MS)) { -it / 3 } +
                                fadeOut(tween(LYRIC_ROLL_MS)),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 62.dp),
                    label = "islandLinesExpanded",
                ) { index ->
                    val at = now.at(index)
                    Column(Modifier.fillMaxWidth()) {
                        IslandLine(text = at.previous, fontSize = 10.sp, active = false)
                        IslandLine(text = at.current, fontSize = 13.sp, active = true)
                        IslandLine(text = at.next, fontSize = 10.sp, active = false)
                    }
                }
            }
            DesktopArtwork(
                url = song?.thumbnailUrl,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
                    .size(52.dp)
                    .clip(RectangleShape)
                    .background(Color(0xFF222226)),
                contentDescription = "Cover",
                px = ISLAND_COVER_LARGE_PX,
            )
        }

        // Progress sits between the lyrics and the controls.
        if (onSeek != null) {
            Box(Modifier.graphicsLayer { alpha = ((face.floatValue - 0.5f) / 0.5f).coerceIn(0f, 1f) }) {
                IslandSeekRow(
                    positionMs = positionMs,
                    durationMs = durationMs,
                    onSeek = onSeek,
                    onSeekStarted = onSeekStarted,
                    onSeekFinished = onSeekFinished,
                )
            }
        }

        // Controls centred on the bottom row, volume at the end.
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IslandControlButton(
                icon = Icons.Rounded.FastRewind,
                tooltip = "Previous",
                size = 26.dp,
                iconSize = 14.dp,
                enabled = interactive,
                onClick = onPrevious,
            )
            IslandControlButton(
                icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                tooltip = if (isPlaying) "Pause" else "Play",
                size = 32.dp,
                iconSize = 18.dp,
                isPrimary = true,
                enabled = interactive,
                onClick = onPlayPause,
            )
            IslandControlButton(
                icon = Icons.Rounded.FastForward,
                tooltip = "Next",
                size = 26.dp,
                iconSize = 14.dp,
                enabled = interactive,
                onClick = onNext,
            )
            Icon(
                Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = "Volume",
                tint = Color.White.copy(alpha = 0.70f),
                modifier = Modifier.size(14.dp),
            )
            DesktopThinSlider(
                value = volume.coerceIn(0f, 1f),
                onValueChange = onVolumeChange,
                modifier = Modifier.width(46.dp),
                idleHeight = 3.dp,
                activeHeight = 5.dp,
                activeColor = Color.White.copy(alpha = 0.88f),
                inactiveColor = Color.White.copy(alpha = 0.20f),
            )
        }
    }
}

/**
 * The resting pill: the cover on the left, vertically centred, and the same three rolling lyric
 * lines the hovered face shows — context line, line being sung, next line. No title, no artist,
 * no controls; the island reads as a karaoke line at a glance.
 */
@Composable
private fun CollapsedIslandFace(
    song: Song?,
    now: IslandLines,
) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when {
            song == null -> Box(
                Modifier
                    .size(48.dp)
                    .clip(RectangleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    BitChordIcons.MusicNote,
                    null,
                    tint = Color.White.copy(alpha = 0.70f),
                    modifier = Modifier.size(20.dp),
                )
            }
            else -> DesktopArtwork(
                url = song.thumbnailUrl,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RectangleShape)
                    .background(Color(0xFF222226)),
                contentDescription = "Cover",
                px = ISLAND_COVER_PX,
            )
        }

        // The three-line lyric block, identical in shape to the hovered face's. Keyed on the song
        // so a track change rolls in with that song's own lines.
        key(song?.videoId) {
            AnimatedContent(
                targetState = now.activeIndex,
                transitionSpec = {
                    (slideInVertically(animationSpec = tween(LYRIC_ROLL_MS)) { it / 3 } +
                        fadeIn(tween(LYRIC_ROLL_MS))).togetherWith(
                        slideOutVertically(animationSpec = tween(LYRIC_ROLL_MS)) { -it / 3 } +
                            fadeOut(tween(LYRIC_ROLL_MS)),
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 6.dp),
                label = "islandLinesCollapsed",
            ) { index ->
                val at = now.at(index)
                Column(Modifier.fillMaxWidth()) {
                    IslandLine(text = at.previous, fontSize = 9.sp, active = false)
                    IslandLine(text = at.current, fontSize = 12.sp, active = true)
                    IslandLine(text = at.next, fontSize = 9.sp, active = false)
                }
            }
        }
    }
}

/**
 * One line of the island's lyric, drawn plainly: no text shadow to blur on every frame.
 */
@Composable
private fun IslandLine(
    text: String,
    fontSize: TextUnit,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.ifBlank { " " },
        color = if (active) Color.White else Color.White.copy(alpha = 0.36f),
        fontSize = fontSize,
        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth(),
    )
}

/**
 * The hairline under the capsule: two rectangles, read straight from the playhead inside
 * `drawBehind`, so a tick redraws the bar and recomposes nothing.
 */
@Composable
private fun IslandProgress(progress: () -> Float, modifier: Modifier = Modifier) {
    Box(
        modifier.drawBehind {
            val base = progress().coerceIn(0f, 1f)
            drawRect(
                color = Color.White.copy(alpha = 0.12f),
                size = Size(size.width, size.height),
            )
            if (base > 0f) {
                drawRect(
                    color = Color.White.copy(alpha = 0.88f),
                    size = Size(size.width * base, size.height),
                )
            }
        },
    )
}

/**
 * The scrub row under the expanded face: what has been played, a bar, what is left.
 *
 * The bar only reports on release. While the pointer is down it previews the position locally, so
 * the lyrics keep reading from where the song actually is until the listener has chosen; asking
 * the engine to jump on every pixel of a drag would be dozens of decoder seeks per gesture.
 */
@Composable
private fun IslandSeekRow(
    positionMs: () -> Long,
    durationMs: () -> Long,
    onSeek: (Float) -> Unit,
    onSeekStarted: () -> Unit,
    onSeekFinished: () -> Unit,
) {
    var preview by remember { mutableStateOf<Float?>(null) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 26.dp, end = 26.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        IslandSeekTime(
            positionMs = positionMs,
            durationMs = durationMs,
            preview = preview,
            leading = true,
        )
        IslandSeekBar(
            positionMs = positionMs,
            durationMs = durationMs,
            preview = preview,
            onPreview = { preview = it },
            onSeekStarted = onSeekStarted,
            onSeekFinished = {
                val target = preview
                if (target != null) onSeek(target)
                preview = null
                onSeekFinished()
            },
            modifier = Modifier.weight(1f),
        )
        IslandSeekTime(
            positionMs = positionMs,
            durationMs = durationMs,
            preview = preview,
            leading = false,
        )
    }
}

@Composable
private fun IslandSeekBar(
    positionMs: () -> Long,
    durationMs: () -> Long,
    preview: Float?,
    onPreview: (Float) -> Unit,
    onSeekStarted: () -> Unit,
    onSeekFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Read through a derived state so the four playhead ticks a second recompose the bar, not the
    // capsule around it.
    val live by remember {
        derivedStateOf {
            val duration = durationMs()
            if (duration > 0L) (positionMs().toFloat() / duration).coerceIn(0f, 1f) else 0f
        }
    }
    DesktopThinSlider(
        value = preview ?: live,
        onValueChange = onPreview,
        modifier = modifier,
        onValueChangeStarted = onSeekStarted,
        onValueChangeFinished = onSeekFinished,
        idleHeight = 3.dp,
        activeHeight = 5.dp,
        activeColor = Color.White.copy(alpha = 0.92f),
        inactiveColor = Color.White.copy(alpha = 0.20f),
    )
}

/** Elapsed time on the left of the seek row, time left on the right. */
@Composable
private fun IslandSeekTime(
    positionMs: () -> Long,
    durationMs: () -> Long,
    preview: Float?,
    leading: Boolean,
) {
    val text by remember(preview) {
        derivedStateOf {
            val duration = durationMs()
            val position = when {
                preview != null && duration > 0L -> (preview * duration).toLong()
                else -> positionMs()
            }
            if (leading) {
                formatIslandTime(position)
            } else {
                "-" + formatIslandTime((duration - position).coerceAtLeast(0L))
            }
        }
    }
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.52f),
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
    )
}

@Composable
private fun IslandControlButton(
    icon: ImageVector,
    tooltip: String,
    size: Dp,
    iconSize: Dp,
    isPrimary: Boolean = false,
    tint: Color = Color.White,
    badgeText: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val isHovered by interaction.collectIsHoveredAsState()

    val background = when {
        isPrimary -> if (isHovered) Color.White else Color(0xEEFFFFFF)
        isHovered -> Color.White.copy(alpha = 0.22f)
        else -> Color.White.copy(alpha = 0.08f)
    }
    val iconColor = if (isPrimary) Color.Black else tint

    Box(
        Modifier
            .size(size)
            .clip(RectangleShape)
            .background(background)
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (badgeText != null) {
            Text(
                text = badgeText,
                color = iconColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = tooltip,
                tint = iconColor,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

/**
 * The artwork sizes the island asks the image host for: one for the resting capsule's small cover,
 * one for the open face's full-bleed square.
 */
private const val ISLAND_COVER_PX = 96
private const val ISLAND_COVER_LARGE_PX = 160

/** How long a lyric line takes to roll into place. */
private const val LYRIC_ROLL_MS = 240

private fun formatIslandTime(ms: Long): String {
    val totalSeconds = (ms / 1_000L).coerceAtLeast(0L)
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
