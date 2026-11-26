package com.music.bitchord.desktop

import androidx.compose.foundation.lazy.LazyListState
import com.music.bitchord.ui.components.LocalShelfRowChrome
import com.music.bitchord.ui.components.ShelfRowChrome
import com.music.bitchord.ui.components.ShelfRow
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.pointer.isBackPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.mutableStateListOf
import com.music.bitchord.ui.components.PAGE_GUTTER
import com.music.bitchord.ui.theme.BcColors
import com.music.bitchord.ui.theme.BcShapes
import com.music.bitchord.ui.theme.bcColorScheme
import com.music.bitchord.ui.theme.bcTypography
import com.music.bitchord.ui.components.SourceQualityBadge
import com.music.bitchord.ui.components.sourceQualityBadge
import com.music.bitchord.ui.components.contextClick
import com.music.bitchord.ui.components.trackColumnWidth
import com.music.bitchord.ui.replay.ReplayCreditCard
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.music.bitchord.data.YtMusicRepository
import com.music.bitchord.data.model.EntityType
import com.music.bitchord.data.model.SearchHistoryEntity
import com.music.bitchord.ui.LocalPullToRefreshEnabled
import com.music.bitchord.ui.screens.ExploreScreen
import com.music.bitchord.ui.screens.HomeScreen
import com.music.bitchord.ui.screens.CompactTrackRow
import com.music.bitchord.ui.screens.LibraryGridPage
import com.music.bitchord.ui.screens.LibraryLink
import com.music.bitchord.ui.screens.LibraryScreen
import com.music.bitchord.ui.screens.MoodGenrePlaylistsScreen
import com.music.bitchord.ui.screens.SearchScreen
import com.music.bitchord.data.NerdStats
import com.music.bitchord.playback.PlaybackPosition
import com.music.bitchord.playback.QueueSource
import com.music.bitchord.playback.QueueTimeline
import com.music.bitchord.playback.QueueTimeline.asQueueEntry
import com.music.bitchord.ui.LyricsProviderState
import com.music.bitchord.data.lyrics.LyricsSource
import com.music.bitchord.ui.player.LyricsRomanizationResult
import com.music.bitchord.ui.player.LyricsSidePanel
import com.music.bitchord.ui.player.QueueSidePanel
import com.music.bitchord.ui.player.RepeatModes
import androidx.compose.runtime.SideEffect
import com.music.bitchord.data.model.QueueTier
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bitchord.desktopapp.generated.resources.Res
import bitchord.desktopapp.generated.resources.logo
import bitchord.desktopapp.generated.resources.logo_mark
import com.music.bitchord.data.model.ArtistPage
import com.music.bitchord.data.model.BrowseItem
import com.music.bitchord.data.model.BrowseType
import com.music.bitchord.data.model.HEADER_ART_PX
import com.music.bitchord.data.model.HomeShelf
import com.music.bitchord.data.model.LibraryPage
import com.music.bitchord.data.model.LikeStatus
import com.music.bitchord.data.model.MoodGenre
import com.music.bitchord.data.model.MoodGenreSection
import com.music.bitchord.data.model.PlaylistPrivacy
import com.music.bitchord.data.model.ROW_ART_PX
import com.music.bitchord.data.model.SearchFilter
import com.music.bitchord.data.model.SearchResult
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.model.PlaybackSourceType
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.SubscriptionState
import com.music.bitchord.data.model.UiState
import com.music.bitchord.data.model.UserPlaylist
import com.music.bitchord.data.model.durationMillis
import com.music.bitchord.data.model.isSameTrackAs
import com.music.bitchord.data.model.playingRowMatcher
import com.music.bitchord.data.model.withoutRepeatsOf
import com.music.bitchord.ui.icons.BitChordIcons
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.hazeSource
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.system.exitProcess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DesktopShelf(
    shelf: HomeShelf,
    onItemClick: (ShelfItem, String?) -> Unit,
    gutter: Dp = DesktopPageGutter,
) {
    Column {
        SectionTitle(shelf.title, shelf.subtitle, gutter)
        DesktopScrollableRow(gutter = gutter) {
            items(shelf.items, key = { it.videoId ?: it.browseId ?: it.title }) { item ->
                DesktopShelfCard(item) { onItemClick(it, shelf.title) }
            }
        }
    }
}

/**
 * A sideways row with the affordances a desktop needs to move it.
 *
 * A carousel longer than the window has no way to be scrolled with a mouse — the wheel belongs to
 * the page underneath — so this adds the three that work: dragging it, the pointer's horizontal
 * ticks while it is hovered, and a paging arrow at whichever end still has more.
 *
 * Shared rather than repeated: every shelf-shaped row in the app wants the same three.
 */
@Composable
internal fun DesktopScrollableRow(
    gutter: Dp = DesktopPageGutter,
    spacing: Dp = 12.dp,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    val rowState = rememberLazyListState()
    DesktopShelfRowChrome.Wrap(rowState) {
        LazyRow(
            state = rowState,
            contentPadding = PaddingValues(horizontal = gutter),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            content = content,
        )
    }
}

/**
 * What every sideways shelf is wrapped in on the desktop — the shared pages' rows too, through
 * [LocalShelfRowChrome]. A mouse cannot swipe a row, so it gets the arrows it pages with, a drag
 * to pull it by, and the horizontal wheel while the pointer is over it.
 */
internal object DesktopShelfRowChrome : ShelfRowChrome {
    @Composable
    override fun Wrap(state: LazyListState, row: @Composable () -> Unit) {
        val scope = rememberCoroutineScope()
        val interaction = remember { MutableInteractionSource() }
        val hovered by interaction.collectIsHoveredAsState()

        /** One screenful, less a sliver so the card at the edge stays in view. */
        fun page(forward: Boolean) {
            val viewport = state.layoutInfo.let { it.viewportEndOffset - it.viewportStartOffset }
            val distance = (viewport - SHELF_PAGE_OVERLAP_PX).coerceAtLeast(SHELF_PAGE_OVERLAP_PX)
            scope.launch { state.animateScrollBy(if (forward) distance.toFloat() else -distance.toFloat()) }
        }

        // Whichever row the pointer is in takes the horizontal ticks; the page underneath keeps the
        // wheel, as it does everywhere else.
        LaunchedEffect(hovered) {
            if (!hovered) return@LaunchedEffect
            DesktopHorizontalScroll.ticks.collect { tick -> state.scrollBy(tick * SHELF_WHEEL_STEP) }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .hoverable(interaction)
                // A row can be taken hold of and pulled, which is the first thing anyone tries.
                .draggable(
                    state = rememberDraggableState { delta -> scope.launch { state.scrollBy(-delta) } },
                    orientation = Orientation.Horizontal,
                ),
        ) {
            row()
            DesktopShelfArrow(
                alignment = Alignment.CenterStart,
                icon = Icons.Rounded.ChevronLeft,
                description = DesktopStrings["d_scroll_left", "Scroll left"],
                visible = hovered && state.canScrollBackward,
                onClick = { page(forward = false) },
            )
            DesktopShelfArrow(
                alignment = Alignment.CenterEnd,
                icon = BitChordIcons.ChevronRight,
                description = DesktopStrings["d_scroll_right", "Scroll right"],
                visible = hovered && state.canScrollForward,
                onClick = { page(forward = true) },
            )
        }
    }
}

/** How much of the outgoing card stays visible after a paged scroll. */
internal const val SHELF_PAGE_OVERLAP_PX = 120

/** How far one notch of a horizontal scroll moves a shelf. */
internal const val SHELF_WHEEL_STEP = 90f

@Composable
internal fun BoxScope.DesktopShelfArrow(
    alignment: Alignment,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    visible: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.align(alignment).padding(horizontal = 6.dp),
    ) {
        Box(
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, description, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
internal fun DesktopShelfCard(item: ShelfItem, onClick: (ShelfItem) -> Unit) {
    val width = 160.dp
    val shape = RoundedCornerShape(BcShapes.artwork)
    Column(
        Modifier
            .width(width)
            // The whole card answers the pointer, artwork and captions together, so the two do not
            // move independently of each other.
            .desktopHoverLift(shape)
            .clickable { onClick(item) },
    ) {
        // No plate behind the sleeve: on a YT Music shelf the artwork is the card.
        Box(
            Modifier
                .size(width)
                .clip(shape)
                .background(DesktopSurface)
                .border(1.dp, BcColors.Hairline, shape),
        ) {
            DesktopArtwork(item.thumbnailUrl, Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(8.dp))
        Text(
            item.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            item.subtitle,
            color = DesktopSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun DesktopSongRow(
    song: Song,
    /** From the list's [rememberPlayingRowMatcher]; null falls back to the name rule. */
    isCurrent: Boolean? = null,
    liked: Boolean,
    onClick: (Song) -> Unit,
    onToggleLike: ((Song) -> Unit)?,
    onDownload: ((Song) -> Unit)? = null,
    onAddToPlaylist: ((Song) -> Unit)? = null,
    downloaded: Boolean = false,
    downloadInProgress: Boolean = false,
    number: Int? = null,
    /** The row's own "…", when the page has a menu to give it. */
    menu: (@Composable (Song) -> Unit)? = null,
) {
    val openContextMenu = LocalDesktopContextMenu.current
    Row(
        Modifier
            .fillMaxWidth()
            .contextClick { openContextMenu(song) }
            .clip(RoundedCornerShape(BcShapes.artwork))
            .desktopHoverFill(corner = BcShapes.artwork)
            .clickable { onClick(song) }
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (number != null) {
            Text(
                "$number",
                Modifier.width(28.dp),
                color = DesktopSecondary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(Modifier.width(12.dp))
        }
        DesktopArtwork(
            song.thumbnailUrl,
            Modifier.size(48.dp).clip(RoundedCornerShape(BcShapes.artwork)),
            px = ROW_ART_PX,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = if (isCurrent ?: song.isNowPlaying()) BcColors.Action else BcColors.TextPrimary,
            )
            Text(listOfNotNull(song.artist.takeIf(String::isNotBlank), song.durationText).joinToString(" · "), color = DesktopSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        song.sourceQualityBadge()?.let { quality ->
            SourceQualityBadge(
                quality = quality,
                color = DesktopSecondary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
        if (onToggleLike != null) {
            IconButton(onClick = { onToggleLike(song) }) { Icon(if (liked) BitChordIcons.HeartFilled else BitChordIcons.Heart, "Favorite", tint = if (liked) DesktopAccent else DesktopSecondary) }
        }
        if (onDownload != null) {
            if (downloadInProgress) {
                IconButton(onClick = { onDownload(song) }) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = DesktopAccent,
                        strokeWidth = 2.dp,
                    )
                }
            } else {
                IconButton(onClick = { if (!downloaded) onDownload(song) }) {
                    Icon(
                        imageVector = if (downloaded) BitChordIcons.Download else BitChordIcons.Download,
                        contentDescription = if (downloaded) "Downloaded" else "Download",
                        tint = if (downloaded) DesktopAccent else DesktopSecondary,
                    )
                }
            }
        }
        if (onAddToPlaylist != null) {
            IconButton(onClick = { onAddToPlaylist(song) }) {
                Icon(BitChordIcons.Plus, DesktopStrings["add_to_playlist", "Add to playlist"], tint = DesktopSecondary)
            }
        }
        // The same menu every other surface opens, so a row on a list page offers what a row on
        // the player does; the row itself plays.
        menu?.invoke(song)
    }
}

/** A page's box, inset from the shell's chrome. */
