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

/**
 * An artist: their picture, the numbers under it, their top songs, and the carousels of what they
 * have released.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DesktopArtistPage(
    state: UiState<ArtistPage>,
    fallbackName: String,
    onRetry: () -> Unit,
    onPlaySongs: (List<Song>, Int) -> Unit,
    onShuffle: (List<Song>) -> Unit,
    /** The ⋮ on a top song, or a right-click on it. */
    onOpenMenu: (Song) -> Unit,
    onShelfItemClick: (ShelfItem, String?) -> Unit,
    /** Null for a guest: a channel subscription is the account's. */
    onToggleSubscription: ((SubscriptionState) -> Unit)?,
    contentPadding: PaddingValues,
) {
    DesktopPageScaffold(contentPadding) {
        when (state) {
            UiState.Loading -> Box(Modifier.fillMaxSize()) {
                DesktopLoadingPage("Loading $fallbackName…")
            }
            is UiState.Error -> Box(Modifier.fillMaxSize()) {
                DesktopErrorPage(state.message, onRetry)
            }
            is UiState.Success -> {
                val artist = state.data
                val name = artist.name?.takeIf(String::isNotBlank) ?: fallbackName
                val palette = rememberDesktopArtworkPalette(artist.thumbnailUrl)
                // Android caps the top-songs block and pages it sideways so the release shelves are
                // not buried under a hundred rows.
                val top = artist.songs.take(MAX_ARTIST_SONGS)
                Box(Modifier.fillMaxSize()) {
                    LazyColumn(
                        contentPadding = pagePadding(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        item(key = "artist-header") {
                            DesktopArtistBanner(artist.thumbnailUrl, name, palette)
                        }
                        if (artist.subscriberCountText != null || artist.monthlyListenerCount != null) {
                            item(key = "artist-stats") {
                                Row(
                                    Modifier.fillMaxWidth().padding(horizontal = DesktopPageGutter, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    // YouTube's own text already reads "1.2M subscribers" in full,
                                    // so only the number is kept and the label re-said in the app's
                                    // words.
                                    artist.subscriberCountText?.let {
                                        DesktopStatChip(Icons.Rounded.Person, "${it.substringBefore(' ')} subscribers")
                                    }
                                    artist.monthlyListenerCount?.let {
                                        DesktopStatChip(Icons.Rounded.GraphicEq, "${it.substringBefore(' ')} monthly listeners")
                                    }
                                }
                            }
                        }
                        item(key = "artist-actions") {
                            // The phone's artist controls: subscribing first, where saving sits on a
                            // release, then the labelled Play pill and Shuffle.
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = DesktopPageGutter, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val subscription = artist.subscription
                                if (subscription != null && onToggleSubscription != null) {
                                    DesktopCircleButton(
                                        if (subscription.subscribed) BitChordIcons.Check else BitChordIcons.Plus,
                                        if (subscription.subscribed) {
                                            DesktopStrings["unsubscribe", "Unsubscribe"]
                                        } else {
                                            DesktopStrings["subscribe", "Subscribe"]
                                        },
                                        onClick = { onToggleSubscription(subscription) },
                                    )
                                }
                                DesktopPlayPill(
                                    label = DesktopStrings["play", "Play"],
                                    onClick = { if (top.isNotEmpty()) onPlaySongs(top, 0) },
                                )
                                DesktopCircleButton(BitChordIcons.Shuffle, DesktopStrings["shuffle", "Shuffle"], onClick = { onShuffle(top) })
                            }
                        }
                        artist.description?.takeIf(String::isNotBlank)?.let { blurb ->
                            item(key = "artist-about") {
                                Column(Modifier.padding(top = 16.dp)) {
                                    SectionTitle(DesktopStrings["d_about_artist", "About artist"])
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        blurb,
                                        color = DesktopSecondary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(horizontal = DesktopPageGutter),
                                    )
                                }
                            }
                        }
                        if (top.isNotEmpty()) {
                            // Paged sideways four to a column, the way Home lists Recents.
                            item(key = "artist-top-songs") {
                                Column(Modifier.padding(top = 20.dp, bottom = 6.dp)) {
                                    SectionTitle(DesktopStrings["top_songs", "Top songs"])
                                    Spacer(Modifier.height(8.dp))
                                    BoxWithConstraints {
                                        val columnWidth = trackColumnWidth(maxWidth)
                                        ShelfRow(
                                            contentPadding = PaddingValues(horizontal = DesktopPageGutter),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            items(top.withIndex().chunked(ARTIST_SONGS_PER_COLUMN)) { column ->
                                                Column(Modifier.width(columnWidth)) {
                                                    column.forEach { (index, song) ->
                                                        CompactTrackRow(
                                                            title = song.title,
                                                            subtitle = song.artist,
                                                            thumbnailUrl = song.thumbnailUrl,
                                                            onClick = { onPlaySongs(top, index) },
                                                            onLongPress = { onOpenMenu(song) },
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        artist.sections.forEach { shelf ->
                            item(key = "artist-shelf-${shelf.title}") {
                                Box(Modifier.padding(top = 18.dp)) {
                                    DesktopShelf(shelf, onItemClick = onShelfItemClick)
                                }
                            }
                        }
                        if (top.isEmpty() && artist.sections.isEmpty()) {
                            item {
                                DesktopEmptyPage(
                                    Icons.Rounded.Person,
                                    "Nothing to show for $name",
                                    "YouTube Music has no songs or releases on this page.",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** The photograph, and the name across the foot of it. */
@Composable
internal fun DesktopArtistBanner(url: String?, name: String, palette: DesktopArtworkPalette) {
    Box(Modifier.fillMaxWidth().height(ARTIST_BANNER_HEIGHT)) {
        DesktopArtwork(url, Modifier.matchParentSize(), px = HEADER_ART_PX)
        // Two washes rather than one: the artwork's own colour, then the fade
        // into the page so the banner has no hard edge.
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.55f to palette.primary.copy(alpha = 0.16f),
                    1f to DesktopBackground,
                ),
            ),
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.35f),
                    0.4f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.45f),
                ),
            ),
        )
        Text(
            name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = DesktopPageGutter, end = DesktopPageGutter, bottom = 18.dp),
        )
    }
}

@Composable
internal fun DesktopStatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        Modifier
            .clip(CircleShape)
            .background(DesktopGlass.copy(alpha = 0.55f))
            .border(0.5.dp, Color.White.copy(alpha = 0.10f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = DesktopSecondary, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(7.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.White)
    }
}

/** The artist page that is open, and the name to bill it under until it loads. */
/** The page an artist's tracks were started from. */
internal fun artistSource(target: DesktopArtistTarget) =
    DesktopQueueSource(target.name, PlaybackSourceType.BROWSE, target.browseId)

/** The album or playlist a queue was started from. */
internal fun collectionSource(collection: DesktopCollection) =
    DesktopQueueSource(collection.title, PlaybackSourceType.BROWSE, collection.browseId)

/** Where a queue was started from, for the player's "Playing from" caption. */
internal data class DesktopQueueSource(
    val title: String,
    val type: PlaybackSourceType,
    val id: String? = null,
)

/**
 * Where this row says it was played from, as the shared queue rules take it — the phone's
 * `play()` makes the same one from the first row of what it was handed.
 */
internal fun Song.queueSource(): QueueSource = QueueSource(
    title = playbackSource ?: albumName ?: "Queue",
    type = playbackSourceType ?: PlaybackSourceType.QUEUE,
    id = playbackSourceId,
)

/** Stamps [source] onto a row, leaving one that already names its origin alone. */
internal fun Song.withSource(source: DesktopQueueSource?): Song = when {
    source == null || playbackSource != null -> this
    else -> copy(
        playbackSource = source.title,
        playbackSourceType = source.type,
        playbackSourceId = source.id,
    )
}

internal data class DesktopArtistTarget(val browseId: String, val name: String)

/** One place the window has been: which page, and whatever was open on it. */
internal data class DesktopNavEntry(
    val destination: DesktopDestination,
    val artist: DesktopArtistTarget?,
    val collection: DesktopCollection?,
    val mood: MoodGenre?,
    val showAll: HomeShelf?,
    val replay: Boolean,
    val settings: DesktopSettingsPage?,
) {
    /** What makes two entries the same place: an album that paged in more tracks has not moved. */
    val key: List<Any?>
        get() = listOf(destination, artist?.browseId, collection?.browseId, mood?.browseId, mood?.params, showAll?.title, replay, settings)
}

/** Deep enough for any way back anyone takes; the oldest fall off. */
internal const val NAV_HISTORY_LIMIT = 50

/** The strip at the head of the sidebar that holds the window's buttons and can be dragged by. */


@Composable
internal fun DesktopHistoryPage(
    history: List<Song>,
    onSongClick: (Song) -> Unit,
    onDownload: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    downloadedIds: Set<String>,
    downloadInProgress: Set<String>,
    contentPadding: PaddingValues,
    menu: (@Composable (Song) -> Unit)? = null,
) {
    DesktopPageScaffold(contentPadding) {
        val isCurrentRow = rememberPlayingRowMatcher(history)
        LazyColumn(
            contentPadding = pagePadding(start = DesktopPageGutter, end = DesktopPageGutter, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (history.isEmpty()) item { DesktopEmptyPage(BitChordIcons.Clock, "Nothing played yet", "Songs you play will show up here.") }
            else items(history, key = Song::videoId) {
                DesktopSongRow(
                    song = it,
                    isCurrent = isCurrentRow(it),
                    liked = false,
                    onClick = onSongClick,
                    onToggleLike = null,
                    onDownload = onDownload,
                    onAddToPlaylist = onAddToPlaylist,
                    downloaded = it.videoId in downloadedIds,
                    downloadInProgress = it.videoId in downloadInProgress,
                    menu = menu,
                )
            }
        }
    }
}

@Composable
internal fun DesktopDownloadsPage(
    downloads: List<Song>,
    onSongClick: (Song) -> Unit,
    contentPadding: PaddingValues,
    menu: (@Composable (Song) -> Unit)? = null,
) {
    DesktopLocalMusicPage(
        songs = downloads,
        onSongClick = onSongClick,
        contentPadding = contentPadding,
        menu = menu,
        title = DesktopStrings["downloads", "Downloads"],
        subtitle = DesktopStrings["d_available_offline", "Available offline"],
        emptyIcon = BitChordIcons.Download,
        emptyTitle = DesktopStrings["d_no_downloads", "No downloads"],
        emptyDescription = DesktopStrings["d_downloaded_songs_will_appear_here", "Downloaded songs will appear here."],
        persistenceKey = "downloaded",
    )
}

@Composable
internal fun DesktopLocalMusicPage(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    contentPadding: PaddingValues,
    title: String = "Local Music",
    subtitle: String = "Audio files found in Music and Downloads",
    emptyIcon: androidx.compose.ui.graphics.vector.ImageVector = BitChordIcons.MusicNote,
    emptyTitle: String = "No local music",
    emptyDescription: String = "Put audio files in your Music folder and reopen this page.",
    persistenceKey: String = "local",
    menu: (@Composable (Song) -> Unit)? = null,
) {
    val persistence = remember { DesktopPersistence() }
    var selectedTab by remember(persistenceKey) { mutableStateOf(0) }
    var searchQuery by remember(persistenceKey) { mutableStateOf("") }
    var view by remember(persistenceKey) {
        mutableStateOf(
            if (persistence.string("${persistenceKey}_view", "LIST") == "GRID") {
                DesktopLibraryView.GRID
            } else {
                DesktopLibraryView.LIST
            },
        )
    }
    var sort by remember(persistenceKey) {
        mutableStateOf(
            runCatching {
                DesktopLibrarySort.valueOf(persistence.string("${persistenceKey}_sort", "TITLE_ASC"))
            }.getOrDefault(DesktopLibrarySort.TITLE_ASC),
        )
    }
    var drillDown by remember(persistenceKey) { mutableStateOf<Pair<String, List<Song>>?>(null) }
    val filteredSongs = remember(songs, sort, searchQuery) {
        songs.sortedForDesktopLibrary(sort).filter { song ->
            searchQuery.isBlank() || song.title.contains(searchQuery, true) || song.artist.contains(searchQuery, true) ||
                song.albumName.orEmpty().contains(searchQuery, true)
        }
    }
    val artists = remember(filteredSongs) {
        filteredSongs.groupBy { it.artist.ifBlank { "On This Computer" } }
            .entries.sortedBy { it.key.lowercase() }
    }
    val albums = remember(filteredSongs) {
        filteredSongs.groupBy { it.albumName?.takeIf(String::isNotBlank) ?: "Unknown Album" }
            .entries.sortedBy { it.key.lowercase() }
    }
    DesktopPageScaffold(contentPadding) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = DesktopPageGutter),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DesktopSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
                    modifier = Modifier.weight(1f),
                    placeholder = DesktopStrings["d_search_your_music", "Search your music"],
                )
                Spacer(Modifier.width(10.dp))
                TextButton(onClick = {
                    sort = when (sort) {
                        DesktopLibrarySort.TITLE_ASC -> DesktopLibrarySort.TITLE_DESC
                        DesktopLibrarySort.TITLE_DESC -> DesktopLibrarySort.DATE_ADDED
                        DesktopLibrarySort.DATE_ADDED -> DesktopLibrarySort.DATE_MODIFIED
                        DesktopLibrarySort.DATE_MODIFIED -> DesktopLibrarySort.TITLE_ASC
                    }
                    persistence.saveString("${persistenceKey}_sort", sort.name)
                }) {
                    Text(sort.label())
                }
                IconButton(onClick = {
                    view = if (view == DesktopLibraryView.LIST) DesktopLibraryView.GRID else DesktopLibraryView.LIST
                    persistence.saveString("${persistenceKey}_view", view.name)
                }) {
                    Icon(if (view == DesktopLibraryView.LIST) BitChordIcons.GridView else BitChordIcons.ListView, "Change view")
                }
            }
            Spacer(Modifier.height(10.dp))
            PrimaryTabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
                listOf("Songs", "Artists", "Albums").forEachIndexed { index, label ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(label) })
                }
            }
            Spacer(Modifier.height(8.dp))
            if (songs.isEmpty()) {
                DesktopEmptyPage(emptyIcon, emptyTitle, emptyDescription)
            } else if (drillDown != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { drillDown = null }) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, DesktopStrings["back", "Back"]) }
                    Text(drillDown!!.first, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(6.dp))
                DesktopLibrarySongContent(drillDown!!.second, view, onSongClick, menu)
            } else when (selectedTab) {
                0 -> DesktopLibrarySongContent(filteredSongs, view, onSongClick, menu)
                1 -> DesktopLibraryGroupingContent(artists, view, onGroupClick = { drillDown = it })
                else -> DesktopLibraryGroupingContent(albums, view, onGroupClick = { drillDown = it })
            }
        }
    }
}

internal fun DesktopLibrarySort.label(): String = when (this) {
    DesktopLibrarySort.TITLE_ASC -> "A–Z"
    DesktopLibrarySort.TITLE_DESC -> "Z–A"
    DesktopLibrarySort.DATE_ADDED -> "Added"
    DesktopLibrarySort.DATE_MODIFIED -> "Modified"
}

@Composable
internal fun DesktopLibrarySongContent(
    songs: List<Song>,
    view: DesktopLibraryView,
    onSongClick: (Song) -> Unit,
    menu: (@Composable (Song) -> Unit)? = null,
) {
    if (songs.isEmpty()) {
        DesktopEmptyPage(BitChordIcons.Search, "No matching music", "Try a different search.")
    } else if (view == DesktopLibraryView.GRID) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = pagePadding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            gridItems(songs, key = Song::videoId) { song ->
                Column(Modifier.fillMaxWidth().clickable { onSongClick(song) }) {
                    DesktopArtwork(song.thumbnailUrl, Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)))
                    Spacer(Modifier.height(7.dp))
                    Text(song.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                    Text(song.artist, color = DesktopSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    } else {
        val isCurrentRow = rememberPlayingRowMatcher(songs)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = pagePadding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(songs, key = Song::videoId) { song ->
                DesktopSongRow(
                    song,
                    isCurrent = isCurrentRow(song),
                    liked = false,
                    onClick = onSongClick,
                    onToggleLike = null,
                    downloaded = song.localPath != null,
                    menu = menu,
                )
            }
        }
    }
}

@Composable
internal fun DesktopLibraryGroupingContent(
    groups: List<Map.Entry<String, List<Song>>>,
    view: DesktopLibraryView,
    onGroupClick: (Pair<String, List<Song>>) -> Unit,
) {
    if (groups.isEmpty()) {
        DesktopEmptyPage(BitChordIcons.Search, "Nothing here yet", "Music will be grouped as it is added.")
    } else if (view == DesktopLibraryView.GRID) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = pagePadding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            gridItems(groups, key = { it.key }) { group ->
                Column(Modifier.fillMaxWidth().clickable { onGroupClick(group.key to group.value) }) {
                    DesktopArtwork(group.value.firstOrNull()?.thumbnailUrl, Modifier.fillMaxWidth().height(150.dp).clip(CircleShape))
                    Spacer(Modifier.height(7.dp))
                    Text(group.key, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                    Text("${group.value.size} songs", color = DesktopSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = pagePadding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(groups, key = { it.key }) { group ->
                Row(
                    Modifier.fillMaxWidth().clickable { onGroupClick(group.key to group.value) }.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DesktopArtwork(group.value.firstOrNull()?.thumbnailUrl, Modifier.size(54.dp).clip(CircleShape))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(group.key, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                        Text("${group.value.size} songs", color = DesktopSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(BitChordIcons.Play, DesktopStrings["open", "Open"], tint = DesktopSecondary)
                }
            }
        }
    }
}
