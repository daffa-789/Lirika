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

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DesktopCollectionPage(
    collection: DesktopCollection,
    loadingMore: Boolean,
    onLoadMore: () -> Unit,
    /** The three edits only a playlist's owner is offered. */
    onRename: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    /** Opens the drag-into-order sheet; only a playlist the account owns can be rearranged. */
    onReorder: (() -> Unit)?,
    /** Copies the release's own YouTube Music link; null for anything with no link to share. */
    onShare: (() -> Unit)?,
    onRemoveFromPlaylist: ((Song) -> Unit)?,
    likedIds: Set<String>,
    onPlaySongs: (List<Song>, Int) -> Unit,
    onShuffle: (List<Song>) -> Unit,
    onDownloadAll: (List<Song>) -> Unit,
    onToggleLike: (Song) -> Unit,
    onDownload: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    downloadedIds: Set<String>,
    downloadInProgress: Set<String>,
    contentPadding: PaddingValues,
) {
    val songs = collection.songs
    // The phone's "search this list": a filter over the rows already loaded.
    var searching by remember(collection.browseId) { mutableStateOf(false) }
    var listQuery by remember(collection.browseId) { mutableStateOf("") }
    val shownSongs = remember(songs, listQuery) {
        val needle = listQuery.trim()
        songs.withIndex().filter { (_, song) ->
            needle.isBlank() || song.title.contains(needle, ignoreCase = true) ||
                song.artist.contains(needle, ignoreCase = true) ||
                song.albumName.orEmpty().contains(needle, ignoreCase = true)
        }
    }
    var moreOpen by remember(collection.browseId) { mutableStateOf(false) }
    val subtitleParts = collection.subtitle
        .split(" • ", " · ", " | ")
        .map(String::trim)
        .filter(String::isNotBlank)
    val credit = subtitleParts.firstOrNull {
        it.lowercase() !in setOf("album", "single", "ep", "playlist", "artist") &&
            !it.matches(Regex("\\d{4}")) &&
            !it.matches(Regex("\\d{1,2}:\\d{2}(?::\\d{2})?")) &&
            !it.matches(Regex("[0-9,.]+\\s*(?:songs?|tracks?)", RegexOption.IGNORE_CASE))
    }.orEmpty()
    val typeLabel = when (collection.type) {
        BrowseType.ALBUM -> "Album"
        BrowseType.PLAYLIST -> "Playlist"
        BrowseType.ARTIST -> "Artist"
        BrowseType.OTHER -> "Collection"
    }
    val metadata = buildList {
        add(typeLabel)
        subtitleParts.firstOrNull { it.matches(Regex("\\d{4}")) }?.let(::add)
        if (songs.isNotEmpty()) add("${songs.size} songs")
        val seconds = songs.sumOf { it.durationText.durationMillis() / 1_000L }
        if (seconds > 0) add(formatCollectionDuration(seconds))
    }.joinToString(" • ")
    DesktopPageScaffold(contentPadding) {
        val isCurrentRow = rememberPlayingRowMatcher(songs)
        LazyColumn(
            contentPadding = pagePadding(
                start = DesktopPageGutter,
                top = DesktopPageGutter,
                end = DesktopPageGutter,
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item {
                // The release's own colour, bled across the header and gone by
                // the buttons — the page reads as the album before the list does.
                val headerArt = collection.thumbnailUrl ?: songs.firstOrNull()?.thumbnailUrl
                val headerPalette = rememberDesktopArtworkPalette(headerArt)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 36.dp)
                        .drawBehind {
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    0f to headerPalette.primary.copy(alpha = 0.14f),
                                    0.85f to Color.Transparent,
                                ),
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(286.dp).clip(RoundedCornerShape(BcShapes.artwork)).border(1.dp, BcColors.Hairline, RoundedCornerShape(BcShapes.artwork))) {
                        DesktopArtwork(
                            headerArt,
                            Modifier.fillMaxSize(),
                        )
                    }
                    Spacer(Modifier.width(30.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            typeLabel,
                            color = DesktopSecondary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            collection.title,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (credit.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                credit,
                                color = DesktopAccent,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (metadata.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(metadata, color = DesktopSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.height(24.dp))
                        // The phone's release controls, in its order: Shuffle, Play, search this
                        // list, and everything else behind the overflow — downloading a release is
                        // done once, finding a track on it is done while reading the page.
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DesktopCircleButton(BitChordIcons.Shuffle, DesktopStrings["shuffle", "Shuffle"], onClick = { onShuffle(songs) })
                            DesktopPlayPill(
                                label = DesktopStrings["play", "Play"],
                                onClick = { if (songs.isNotEmpty()) onPlaySongs(songs, 0) },
                                iconOnly = true,
                            )
                            DesktopCircleButton(
                                if (searching) Icons.Rounded.Close else BitChordIcons.Search,
                                if (searching) DesktopStrings["close_search", "Close search"] else DesktopStrings["search_this_list", "Search this list"],
                                onClick = {
                                    searching = !searching
                                    if (!searching) listQuery = ""
                                },
                            )
                            Box {
                                DesktopCircleButton(Icons.Rounded.MoreHoriz, DesktopStrings["more", "More"], onClick = { moreOpen = true })
                                DropdownMenu(expanded = moreOpen, onDismissRequest = { moreOpen = false }) {
                                    DropdownMenuItem(
                                        text = { Text(DesktopStrings["download_all", "Download all"]) },
                                        leadingIcon = { Icon(BitChordIcons.Download, null) },
                                        onClick = {
                                            moreOpen = false
                                            onDownloadAll(songs)
                                        },
                                    )
                                    onShare?.let { share ->
                                        DropdownMenuItem(
                                            text = { Text(DesktopStrings["share", "Share"]) },
                                            leadingIcon = { Icon(Icons.Rounded.Share, null) },
                                            onClick = {
                                                moreOpen = false
                                                share()
                                            },
                                        )
                                    }
                                    onRename?.let { rename ->
                                        DropdownMenuItem(
                                            text = { Text(DesktopStrings["rename", "Rename"]) },
                                            leadingIcon = { Icon(Icons.Rounded.Edit, null) },
                                            onClick = {
                                                moreOpen = false
                                                rename()
                                            },
                                        )
                                    }
                                    onReorder?.let { reorder ->
                                        DropdownMenuItem(
                                            text = { Text(DesktopStrings["reorder_playlist", "Edit order"]) },
                                            leadingIcon = { Icon(Icons.Rounded.DragHandle, null) },
                                            onClick = {
                                                moreOpen = false
                                                reorder()
                                            },
                                        )
                                    }
                                    onDelete?.let { delete ->
                                        DropdownMenuItem(
                                            text = { Text(DesktopStrings["delete", "Delete"]) },
                                            leadingIcon = { Icon(Icons.Rounded.Delete, null) },
                                            onClick = {
                                                moreOpen = false
                                                delete()
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (searching) {
                item(key = "collection-search") {
                    DesktopSearchField(
                        query = listQuery,
                        onQueryChange = { listQuery = it },
                        onSearch = {},
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                        placeholder = DesktopStrings["search_this_list", "Search this list"],
                    )
                }
            }
            itemsIndexed(shownSongs, key = { _, (index, song) -> "${song.videoId}-$index" }) { position, (index, song) ->
                DesktopCollectionSongRow(
                    // Striped by place on screen, not track number, so a filtered list still
                    // alternates.
                    striped = position % 2 == 1,
                    song = song,
                    isCurrent = isCurrentRow(song),
                    collectionType = collection.type,
                    liked = song.videoId in likedIds,
                    onClick = { _ -> onPlaySongs(songs, index) },
                    onToggleLike = onToggleLike,
                    onDownload = onDownload,
                    onAddToPlaylist = onAddToPlaylist,
                    downloaded = song.videoId in downloadedIds,
                    downloadInProgress = song.videoId in downloadInProgress,
                    number = index + 1,
                    onRemove = onRemoveFromPlaylist?.takeIf { song.setVideoId != null },
                )
            }
            if (collection.continuation != null) {
                // Reaching this row is what asks for the next page, the way the home feed pages.
                item(key = "collection-more") {
                    LaunchedEffect(songs.size) { onLoadMore() }
                    Box(
                        Modifier.fillMaxWidth().height(72.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (loadingMore) {
                            CircularProgressIndicator(
                                color = DesktopAccent,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * The width of a collection row's action cluster, fixed so the duration column lines up down the
 * table whichever rows can be removed.
 */
internal fun collectionActionsWidth(hasRemove: Boolean): Dp =
    if (hasRemove) 136.dp else 102.dp

@Composable
internal fun DesktopCollectionSongRow(
    song: Song,
    /** From the list's [rememberPlayingRowMatcher]; null falls back to the name rule. */
    isCurrent: Boolean? = null,
    collectionType: BrowseType,
    liked: Boolean,
    onClick: (Song) -> Unit,
    onToggleLike: ((Song) -> Unit)?,
    onDownload: ((Song) -> Unit)?,
    onAddToPlaylist: ((Song) -> Unit)?,
    downloaded: Boolean,
    downloadInProgress: Boolean,
    number: Int,
    /** Every other row is shaded, so a wide row can be followed across to its buttons. */
    striped: Boolean,
    /** Takes this row out of the playlist being read. */
    onRemove: ((Song) -> Unit)? = null,
) {
    val openContextMenu = LocalDesktopContextMenu.current
    Row(
        Modifier
            .fillMaxWidth()
            .contextClick { openContextMenu(song) }
            .clip(RoundedCornerShape(BcShapes.artwork))
            .background(if (striped) Color.White.copy(alpha = 0.03f) else Color.Transparent)
            .desktopHoverFill(corner = BcShapes.artwork)
            .clickable { onClick(song) }
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val nowPlaying = isCurrent ?: song.isNowPlaying()
        Box(Modifier.width(28.dp), contentAlignment = Alignment.Center) {
            Text(
                number.toString(),
                color = DesktopSecondary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Spacer(Modifier.width(12.dp))
        // No sleeve on an album's rows.
        if (collectionType != BrowseType.ALBUM) {
            DesktopArtwork(song.thumbnailUrl, Modifier.size(44.dp).clip(RoundedCornerShape(BcShapes.artwork)), px = ROW_ART_PX)
            Spacer(Modifier.width(12.dp))
        }
        Text(
            song.title,
            Modifier.weight(0.42f),
            color = if (nowPlaying) BcColors.Action else BcColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            song.artist,
            Modifier.weight(0.20f).padding(horizontal = 8.dp),
            color = DesktopSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        // Every row of an album is on that album; the column would only repeat the page's title.
        if (collectionType != BrowseType.ALBUM) {
            Text(
                song.albumName ?: "—",
                Modifier.weight(0.25f).padding(horizontal = 8.dp),
                color = DesktopSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            song.durationText ?: "—",
            Modifier.width(58.dp),
            color = DesktopSecondary,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            Modifier.width(collectionActionsWidth(onRemove != null)),
            horizontalArrangement = Arrangement.End,
        ) {
            onRemove?.let { remove ->
                IconButton(onClick = { remove(song) }, modifier = Modifier.size(34.dp)) {
                    Icon(
                        Icons.Rounded.RemoveCircleOutline,
                        DesktopStrings["d_remove_from_playlist", "Remove from playlist"],
                        tint = DesktopSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            onToggleLike?.let { onLike ->
                IconButton(onClick = { onLike(song) }, modifier = Modifier.size(34.dp)) {
                    Icon(
                        if (liked) BitChordIcons.HeartFilled else BitChordIcons.Heart,
                        "Favorite",
                        tint = if (liked) DesktopAccent else DesktopSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            onDownload?.let { onSave ->
                IconButton(onClick = { if (!downloaded) onSave(song) }, modifier = Modifier.size(34.dp)) {
                    if (downloadInProgress) {
                        CircularProgressIndicator(Modifier.size(16.dp), color = DesktopAccent, strokeWidth = 2.dp)
                    } else {
                        Icon(
                            if (downloaded) BitChordIcons.Download else BitChordIcons.Download,
                            if (downloaded) "Downloaded" else "Download",
                            tint = if (downloaded) DesktopAccent else DesktopSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
            onAddToPlaylist?.let { onAdd ->
                IconButton(onClick = { onAdd(song) }, modifier = Modifier.size(34.dp)) {
                    Icon(BitChordIcons.Plus, DesktopStrings["add_to_playlist", "Add to playlist"], tint = DesktopSecondary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
