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

private const val STATS_SAMPLE_MS = 5_000L

/** How often the playhead is offered to the history tracker. */
private const val HISTORY_SAMPLE_MS = 5_000L

/** How long the search field is left alone before its typeahead is asked for. */
private const val SUGGESTION_DEBOUNCE_MS = 220L

/** How many tracks a station is built with when one is started explicitly. */
/** A search costs more than a completion, so the live rows wait for a longer pause. */
private const val TYPEAHEAD_MEDIA_DEBOUNCE_MS = 400L

/** How many playable rows the dropdown shows, matching Android's own limit. */
private const val TYPEAHEAD_MEDIA_LIMIT = 8

private const val INITIAL_RADIO_TRACKS = 24

/** An artist's top songs, capped so the release shelves are not buried. */
internal const val MAX_ARTIST_SONGS = 20
internal const val ARTIST_SONGS_PER_COLUMN = 4

/** The artist photograph's height. */
internal val ARTIST_BANNER_HEIGHT = 340.dp

/** The library shelf the account's own playlists arrive on, and the one edits are spliced into. */
private const val PLAYLISTS_SHELF = "Playlists"
private const val STATS_MAX_DELTA_MS = 15_000L
private const val LISTENING_PLAY_THRESHOLD_MS = 180_000L

/** Used for tracking restart of the current song when previous button is pressed. */
private const val BACK_RESTARTS_AFTER_MS = 10_000L

/** How long a manual skip blends into the next track when crossfade-on-skip is on. */
private const val SKIP_MIX_ON_SKIP_SECONDS = 2

/** One-time marker: existing profiles get skip-mix on, but a later choice of theirs is kept. */
private const val KEY_SKIP_MIX_DEFAULT_ON = "crossfade_on_skip_default_on_v1"

/** How long the supplier waits for the server to echo an AutoPlay queue addition. */
private const val PARTY_AUTOPLAY_ECHO_TIMEOUT_MS = 5_000L

/** The phone gives one empty station response a delayed second chance. */
private const val AUTOPLAY_EMPTY_REFRESH_DELAY_MS = 2_000L

/** The phone's dark scheme, which the shared pages are drawn against. */
internal fun desktopColorScheme() = bcColorScheme()

@Composable
internal fun desktopTypography(): Typography = bcTypography()

internal enum class DesktopDestination(val id: String, val label: String) {
    LISTEN_NOW("listen-now", "Home"),
    EXPLORE("explore", "Explore"),
    LIBRARY("library", "Library"),
    SEARCH("search", "Search"),
    HISTORY("history", "History"),
    DOWNLOADS("downloads", "Downloads"),
    LOCAL_MUSIC("local-music", "Local Music"),
    SETTINGS("settings", "Settings"),
}

internal enum class DesktopRepeatMode {
    OFF,
    ALL,
    ONE,
    ;

    fun next(): DesktopRepeatMode = when (this) {
        OFF -> ALL
        ALL -> ONE
        ONE -> OFF
    }

    fun label(): String = when (this) {
        OFF -> "Off"
        ALL -> "All"
        ONE -> "One"
    }
}

@Composable
fun BitChordDesktopApp() {
    val scope = rememberCoroutineScope()
    // The island is the player while it is on. The full sheet is not a second view of the same
    // song — it is a different surface, and showing both is what duplicated the cover and title.
    val islandEnabled by DesktopDynamicIslandSettings.enabled.collectAsState()

    val persistence = remember { DesktopPersistence() }
    // In-app update notifications disabled for Lirika Windows Edition
    var destination by remember { mutableStateOf(DesktopDestination.LISTEN_NOW) }
    var query by remember { mutableStateOf("") }
    var searchFilter by remember { mutableStateOf(SearchFilter.ALL) }
    var searchRows by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    // What has been searched before, and what YouTube thinks is being typed.
    val searchHistory by DesktopSearchHistory.recent.collectAsState()
    // Whether anything has been searched for since the field was last emptied: until then the
    // page shows recent searches rather than a result set.
    var searchCommitted by remember { mutableStateOf(false) }
    var searchScrollReset by remember { mutableStateOf(0) }
    var searchFocusRequested by remember { mutableStateOf(false) }
    var searchSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    // Playable rows for the half-typed query, shown under the text completions.
    var searchTypeahead by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    // False while the query is being set *by* the app.
    var searchTyping by remember { mutableStateOf(false) }
    var searchLoading by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var homeState by remember { mutableStateOf<UiState<List<HomeShelf>>>(UiState.Loading) }
    // The home feed is paged: `FEmusic_home` answers with a couple of shelves and a token, and
    // everything else arrives by following it.
    var homeContinuation by remember { mutableStateOf<String?>(null) }
    var homeLoadingMore by remember { mutableStateOf(false) }
    // Recently Played has its own request and its own skeleton at the head of the page, as on the
    // phone; it is only asked for with an account to have played on.
    var homeRecentsLoading by remember { mutableStateOf(false) }
    // Held out here so a page keeps its place when another is visited and it is come back to.
    val homeListState = rememberLazyListState()
    val exploreListState = rememberLazyListState()
    val moodGenreListState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val libraryListState = rememberLazyListState()
    val libraryGridState = rememberLazyGridState()
    /** A Library shelf's "Show all", open as a grid in place of the page. */
    var libraryShowAll by remember { mutableStateOf<HomeShelf?>(null) }
    /** Where the one back button in the top bar goes: every place visited, oldest first. */
    val navHistory = remember { mutableStateListOf<DesktopNavEntry>() }
    /** Set while [goBack] puts a place back, so that move is not itself recorded as a visit. */
    val navRestoring = remember { booleanArrayOf(false) }
    /** Opens a song's menu at the pointer; set by the pages' menu host once it is in place. */
    var pageMenu by remember { mutableStateOf<(Song) -> Unit>({}) }
    val homeSeenTitles = remember { HashSet<String>() }
    var homeGeneration by remember { mutableStateOf(0) }
    var homeIdentity by remember { mutableStateOf<String?>(null) }
    var exploreState by remember { mutableStateOf<UiState<List<MoodGenreSection>>>(UiState.Loading) }
    var selectedMoodGenre by remember { mutableStateOf<MoodGenre?>(null) }
    // Bumped by a retry so the loaders below re-run without the state they are keyed on having to
    // change to something and back again.
    var exploreReloads by remember { mutableStateOf(0) }
    var moodGenreReloads by remember { mutableStateOf(0) }
    var moodGenreShelves by remember { mutableStateOf<UiState<List<HomeShelf>>>(UiState.Loading) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    // The live queue: what is playing, what played before it, what is next.
    var liveQueue by remember {
        mutableStateOf(
            DesktopQueue.restored(
                persistence.queue(),
                persistence.string("queue_index", "0").toIntOrNull() ?: 0,
            ),
        )
    }
    // The order the queue was in before shuffle rearranged it, so the toggle can be undone.
    var preShuffleOrder by remember { mutableStateOf<List<String>>(emptyList()) }
    val queue = liveQueue.songs
    // What was played on this computer. The account's own history replaces it
    // while signed in — see [remoteHistory] — because that is what Android's
    // History screen is and what makes a phone and a desktop agree.
    var history by remember { mutableStateOf(persistence.history()) }
    var remoteHistory by remember { mutableStateOf<List<Song>>(emptyList()) }
    var likedIds by remember { mutableStateOf(persistence.likedIds()) }
    // In-flight tail of the Liked Music chain; replaced whenever the library is fetched again.
    var likedSyncJob by remember { mutableStateOf<Job?>(null) }
    var dislikedIds by remember { mutableStateOf(persistence.dislikedIds()) }
    val overlays = remember { DesktopOverlays() }
    var downloads by remember { mutableStateOf(persistence.downloads()) }
    var localSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    val downloadQueue by DesktopDownloadQueue.active.collectAsState()
    val downloadInProgress = downloadQueue.keys
    var playlists by remember { mutableStateOf(persistence.playlists()) }
    var statsRevision by remember { mutableStateOf(0L) }
    var replayPeriod by remember { mutableStateOf(DesktopReplayPeriod.ALL_TIME) }
    val replayHolder = remember { DesktopAccounts.active()?.name.orEmpty() }
    var replaySummary by remember { mutableStateOf(DesktopListeningStats.summary()) }
    var statsCountedSongs by remember { mutableStateOf<Set<String>>(emptySet()) }
    var playlistTarget by remember { mutableStateOf<Song?>(null) }
    // One busy flag and one message for every playlist edit: only one of them can be in flight,
    // because each is driven by a dialog that is modal.
    var playlistBusy by remember { mutableStateOf(false) }
    var playlistError by remember { mutableStateOf<String?>(null) }
    // Settings is a modal over whatever page is open, the way Music puts its own preferences in a
    // sheet rather than a navigation destination.
    var autoplay by remember { mutableStateOf(persistence.boolean("autoplay", true)) }
    var dontRepeatSuggestions by remember {
        mutableStateOf(persistence.boolean(KEY_DONT_REPEAT_SUGGESTIONS, false))
    }
    /** Everything AutoPlay has played or offered this run — see [KEY_DONT_REPEAT_SUGGESTIONS]. */
    val sessionSongHistory = remember { mutableListOf<Song>() }
    var filterNonMusicAudio by remember {
        mutableStateOf(persistence.boolean(DesktopLocalMusic.KEY_FILTER_NON_MUSIC_AUDIO, false))
    }
    var localMusicRevision by remember { mutableStateOf(0) }
    // The track the queued station was built around, so a top-up is asked for once per song rather
    // than once per recomposition.
    var autoplaySeed by remember { mutableStateOf<String?>(null) }
    var autoplayJob by remember { mutableStateOf<Job?>(null) }
    // A manual skip blends into the already-prepared next track instead of cutting. The stored
    // key stays "automix_on_skip"; a one-time marker turns it on for profiles that never chose,
    // while anyone who later switches it off keeps that choice.
    var crossfadeOnSkip by remember {
        mutableStateOf(
            if (!persistence.boolean(KEY_SKIP_MIX_DEFAULT_ON, false)) {
                persistence.saveBoolean("automix_on_skip", true)
                persistence.saveBoolean(KEY_SKIP_MIX_DEFAULT_ON, true)
                true
            } else {
                persistence.boolean("automix_on_skip", false)
            },
        )
    }
    var shuffle by remember { mutableStateOf(persistence.boolean("shuffle", false)) }
    var repeatMode by remember {
        mutableStateOf(
            runCatching { DesktopRepeatMode.valueOf(persistence.string("repeat_mode", "OFF")) }
                .getOrDefault(DesktopRepeatMode.OFF),
        )
    }
    var playbackSpeed by remember { mutableStateOf(persistence.string("playback_speed", "1.0").toFloatOrNull() ?: 1.0f) }
    var volume by remember { mutableStateOf(persistence.string("volume", "1.0").toFloatOrNull()?.coerceIn(0.0f, 1.0f) ?: 1.0f) }
    var crossfadeSeconds by remember { mutableStateOf(persistence.string("crossfade_seconds", "0").toIntOrNull()?.coerceIn(0, 12) ?: 0) }
    var downloadQuality by remember { mutableStateOf(persistence.string("download_quality", "LOSSLESS")) }
    var trayIconEnabled by remember { mutableStateOf(persistence.boolean("tray_icon", true)) }
    var closeToTray by remember { mutableStateOf(persistence.boolean("close_to_tray", true)) }
    // The three the FFmpeg engine unlocked: none of them could exist while JavaFX owned the decode,
    // because none of them can be done without the samples themselves.
    var spatialAudio by remember { mutableStateOf(persistence.boolean("spatial_audio", false)) }
    var dolbyAtmos by remember { mutableStateOf(persistence.boolean("dolby_atmos", true)) }
    var skipSilence by remember { mutableStateOf(persistence.boolean("skip_silence", false)) }
    var outputPrecision by remember { mutableStateOf(persistence.string("output_precision", "PCM_16")) }
    var showNerdStats by remember { mutableStateOf(persistence.boolean("show_nerd_stats", false)) }
    var syncedLyrics by remember {
        mutableStateOf(persistence.boolean(DesktopLyricsClient.KEY_SYNCED_LYRICS, true))
    }
    var lyricsBlur by remember {
        mutableStateOf(persistence.boolean(DesktopLyricsClient.KEY_LYRICS_BLUR, true))
    }
    var prioritizeSyllables by remember {
        mutableStateOf(persistence.boolean(DesktopLyricsClient.KEY_PRIORITIZE_SYLLABLES, false))
    }
    var lyricsOrder by remember { mutableStateOf(persistence.lyricsSourceOrder()) }
    var lyricsOn by remember { mutableStateOf(persistence.lyricsEnabledSources()) }
    var accounts by remember { mutableStateOf(DesktopAccounts.accounts()) }
    var activeAccountId by remember { mutableStateOf(DesktopAccounts.activeAccountId()) }
    var activeProfileId by remember { mutableStateOf(DesktopAccounts.activeProfileId()) }
    var signInBusy by remember { mutableStateOf<String?>(null) }
    var signInError by remember { mutableStateOf<String?>(null) }
    var webSignInJob by remember { mutableStateOf<Job?>(null) }
    val activeAccount = accounts.firstOrNull { it.accountId == activeAccountId } ?: accounts.firstOrNull()
    // The account's own library, fetched once a session is in force.
    var libraryState by remember { mutableStateOf<UiState<LibraryPage>>(UiState.Loading) }
    // Whether YouTube is actually accepting the session, which is not the same as having an account
    // saved.
    var youtubeSignedIn by remember { mutableStateOf(false) }
    // Set by an edit that the library feed has not caught up with yet.
    var libraryStale by remember { mutableStateOf(false) }
    // Bumped when the session changes underneath, so the loader below re-runs without anything it
    // is keyed on having to change to something and back.
    var sessionRevision by remember { mutableStateOf(0) }
    // Read off the library rather than fetched again: the Playlists shelf is the same feed the
    // picker wants, minus the entries no edit can be aimed at.
    val accountPlaylists = remember(libraryState) {
        (libraryState as? UiState.Success)?.data?.shelves
            ?.firstOrNull { it.title == PLAYLISTS_SHELF }
            ?.items
            ?.let(DesktopSearchClient::userPlaylists)
            .orEmpty()
    }
    // The add-to-playlist dialog intentionally keeps only playlists owned by the account. The
    // sidebar is a library browser instead, so it shows the complete paged shelf: owned, saved,
    // and YouTube's built-in playlists such as Liked Music.
    val sidebarAccountPlaylists = remember(libraryState) {
        (libraryState as? UiState.Success)?.data?.shelves
            ?.firstOrNull { it.title == PLAYLISTS_SHELF }
            ?.items
            ?.filter { it.browseId != null }
            .orEmpty()
    }

    fun reloadLibrary() {
        libraryState = UiState.Loading
        sessionRevision++
    }

    /** Edits the Playlists shelf in place, without re-fetching it. */
    fun editPlaylistShelf(edit: (List<ShelfItem>) -> List<ShelfItem>) {
        val page = (libraryState as? UiState.Success)?.data ?: return
        val index = page.shelves.indexOfFirst { it.title == PLAYLISTS_SHELF }
        val shelves = if (index >= 0) {
            page.shelves.toMutableList().apply {
                this[index] = this[index].copy(items = edit(this[index].items))
            }
        } else {
            // A fresh account has no Playlists shelf at all, and it is exactly the account most
            // likely to be making its first one.
            val items = edit(emptyList())
            if (items.isEmpty()) return
            listOf(HomeShelf(PLAYLISTS_SHELF, items)) + page.shelves
        }
        libraryState = UiState.Success(page.copy(shelves = shelves.toList()))
    }

    // Whoever was signed in last is signed in again, before anything asks YouTube for something
    // that depends on it.
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) { DesktopAccounts.activate() }
        accounts = withContext(Dispatchers.IO) { DesktopAccounts.accounts() }
        activeAccountId = DesktopAccounts.activeAccountId()
        activeProfileId = DesktopAccounts.activeProfileId()
        youtubeSignedIn = DesktopYouTubeAuth.isSignedIn
        sessionRevision++
    }
    var audioQuality by remember { mutableStateOf(persistence.audioQuality()) }
    var sourceConfigs by remember { mutableStateOf(persistence.sourceConfigs()) }
    var sourceStatus by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val youtubeSourceEnabled = sourceConfigs.any {
        it.kind == DesktopSourceKind.YOUTUBE && it.enabled
    }
    val sleepTimerMinutes by DesktopSleepTimer.minutes.collectAsState()
    val sleepAfterTrack by DesktopSleepTimer.afterTrack.collectAsState()
    var sleepTimerTick by remember { mutableStateOf(0L) }
    LaunchedEffect(sleepTimerMinutes) {
        while (sleepTimerMinutes != null && currentCoroutineContext().isActive) {
            delay(1_000L)
            sleepTimerTick++
        }
    }
    val sleepRemainingMs = remember(sleepTimerMinutes, sleepTimerTick) {
        DesktopSleepTimer.remainingMs()
    }
    var openedCollection by remember { mutableStateOf<DesktopCollection?>(null) }
    // While the island carries a song, no song/album detail page stays open under it. This is the
    // screen that otherwise reads as a second player even after the sheet itself was retired.
    LaunchedEffect(islandEnabled, selectedSong?.videoId) {
        if (islandEnabled && selectedSong != null) openedCollection = null
    }
    var collectionLoadingMore by remember { mutableStateOf(false) }
    var collectionError by remember { mutableStateOf<String?>(null) }
    var collectionReloads by remember { mutableStateOf(0) }
    // The artist page is its own destination rather than a collection with a different header.
    var openedArtist by remember { mutableStateOf<DesktopArtistTarget?>(null) }
    var artistState by remember { mutableStateOf<UiState<ArtistPage>>(UiState.Loading) }
    var artistReloads by remember { mutableStateOf(0) }

    fun canonicalSong(song: Song): Song {
        val homeSong = (homeState as? UiState.Success)?.data.orEmpty()
            .asSequence()
            .flatMap { it.items.asSequence() }
            .filter { it.videoId == song.videoId }
            .map(ShelfItem::toSong)
            .firstOrNull()
        return if (homeSong != null && song.artist.isUnknownArtist()) {
            song.copy(
                artist = homeSong.artist,
                durationText = song.durationText ?: homeSong.durationText,
                thumbnailUrl = song.thumbnailUrl ?: homeSong.thumbnailUrl,
            )
        } else {
            song
        }
    }

    lateinit var startSong: (Song, Boolean) -> Unit
    lateinit var playbackEngine: DesktopPlaybackEngine

    fun saveQueue() {
        persistence.saveQueue(liveQueue.songs)
        persistence.saveString("queue_index", liveQueue.index.toString())
    }

    /**
     * Mirrors the song into the island the moment it changes.
     *
     * The island used to learn about a new song only from a [LaunchedEffect] in this composable.
     * That breaks in two places: with the main window hidden the island is the only surface left,
     * and a crossfade hand-off announces its new song from the audio thread. Writing the flows
     * here — thread-safe by way of MutableStateFlow — keeps the capsule's title and cover in step
     * with what is actually playing, whichever route got there.
     */
    fun mirrorIslandSong(song: Song?, queue: DesktopQueue) {
        DesktopIslandState.currentSong.value = song
        DesktopIslandState.queueSongs.value = queue.songs
        DesktopIslandState.queueIndex.value = queue.index
    }

    /** Opens whatever the queue is currently pointing at. */
    fun playCurrent(startPlaying: Boolean = true) {
        val song = liveQueue.current ?: return
        selectedSong = song
        mirrorIslandSong(song, liveQueue)
        history = (listOf(song) + history.filterNot { it.videoId == song.videoId }).take(50)
        saveQueue()
        persistence.saveHistory(history)
        playbackEngine.load(song, startPlaying)
    }

    /** A song played on its own — from a search row, a shelf card, history. */
    fun playSong(song: Song, startPlaying: Boolean = true, source: DesktopQueueSource? = null) {
        val tapped = canonicalSong(song).withSource(source)
        // The phone's one-off queue: the song, then whatever the listener had queued by hand.
        val songs = QueueTimeline.buildOneOffQueue(liveQueue.songs, liveQueue.index, tapped, tapped.queueSource())
        liveQueue = DesktopQueue(songs, index = 0)
        preShuffleOrder = emptyList()
        playCurrent(startPlaying)
    }

    /** Takes a captured cookie all the way to a saved account. */
    suspend fun signIn(cookie: String, label: String, sourceProfile: String? = null) {
        signInError = null
        signInBusy = label
        try {
            val scope = withContext(Dispatchers.IO) { DesktopYouTubeSession.adoptSessionScope(cookie) }
            if (scope == null) {
                signInError = "$label is not signed in to YouTube Music."
                return
            }
            DesktopYouTubeAuth.adopt(scope)
            // Asked with the session in force, so they describe the account that was just captured
            // rather than nobody in particular.
            val details = DesktopSearchClient.accountMenu().getOrNull()?.let(DesktopAccountParser::account)
            val channels = DesktopSearchClient.accountsList().getOrNull()
                ?.let(DesktopAccountParser::channels)
                .orEmpty()
            // Off the UI thread: saving reaches the platform keyring over D-Bus, and a blocking
            // call to another process is not something to do on the thread that is drawing.
            withContext(Dispatchers.IO) { DesktopAccounts.save(cookie, scope, details, channels, sourceProfile) }
            accounts = withContext(Dispatchers.IO) { DesktopAccounts.accounts() }
            activeAccountId = DesktopAccounts.activeAccountId()
            activeProfileId = DesktopAccounts.activeProfileId()
            overlays.signIn = false
            overlays.accounts = false
        } finally {
            signInBusy = null
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0, source: DesktopQueueSource? = null) {
        if (songs.isEmpty()) return
        val playable = songs.map { canonicalSong(it).withSource(source) }
        // Past the end still means the last row, as the clamp here always did.
        val at = startIndex.coerceIn(0, playable.lastIndex)
        // The phone's context queue: the album up to the pick, the pick, what the listener had
        // queued by hand, then the rest of the album.
        val built = QueueTimeline.buildContextQueue(
            currentTimeline = liveQueue.songs,
            currentIndex = liveQueue.index,
            newContextSongs = playable,
            selectedIndex = at,
            contextSource = playable[at].queueSource(),
        )
        liveQueue = if (shuffle) {
            DesktopQueue.shuffledStartingAt(built.timeline, built.startIndex)
        } else {
            // Keep the complete queue so Previous can navigate to the tracks before the selected one.
            DesktopQueue.startingAt(built.timeline, built.startIndex)
        }
        preShuffleOrder = if (shuffle) built.timeline.map(DesktopQueue::orderKey) else emptyList()
        // Once music starts the island is the surface. A song or album detail page left on screen
        // under it is the second panel the eye reads as a duplicate player, so close that page.
        openedCollection = null
        playCurrent()
    }

    /** Moves within the queue, dropping whatever a forward jump passed over. */
    fun playQueueIndex(target: Int) {
        if (target !in liveQueue.songs.indices) return
        liveQueue = liveQueue.jumpTo(target)
        playCurrent()
    }

    fun cycleSleepTimer() {
        when {
            sleepAfterTrack -> DesktopSleepTimer.cancel()
            sleepTimerMinutes == null -> DesktopSleepTimer.start(DesktopSleepTimer.presets.first())
            sleepTimerMinutes == DesktopSleepTimer.presets.last() -> DesktopSleepTimer.startAfterTrack()
            else -> {
                val current = DesktopSleepTimer.presets.indexOf(sleepTimerMinutes)
                DesktopSleepTimer.start(DesktopSleepTimer.presets.getOrElse(current + 1) { DesktopSleepTimer.presets.last() })
            }
        }
    }
    startSong = ::playSong

    /** Applies a rating locally and carries it to the account. */
    fun rate(song: Song, status: LikeStatus) {
        likedIds = if (status == LikeStatus.LIKE) likedIds + song.videoId else likedIds - song.videoId
        dislikedIds = if (status == LikeStatus.DISLIKE) {
            dislikedIds + song.videoId
        } else {
            dislikedIds - song.videoId
        }
        persistence.saveLikedIds(likedIds)
        persistence.saveDislikedIds(dislikedIds)
        scope.launch {
            DesktopSearchClient.rate(song.videoId, status)
                .onFailure { DesktopTrackLog.log("youtube: rating ${song.videoId} failed: ${it.message}") }
        }
    }

    fun toggleLike(song: Song) =
        rate(song, if (song.videoId in likedIds) LikeStatus.INDIFFERENT else LikeStatus.LIKE)

    fun toggleDislike(song: Song) =
        rate(song, if (song.videoId in dislikedIds) LikeStatus.INDIFFERENT else LikeStatus.DISLIKE)

    /** The origin the live queue is carrying, for anything appended to it. */
    fun currentQueueSource(): DesktopQueueSource? = liveQueue.current?.playbackSource?.let {
        DesktopQueueSource(
            it,
            liveQueue.current?.playbackSourceType ?: PlaybackSourceType.QUEUE,
            liveQueue.current?.playbackSourceId,
        )
    }

    /**
     * Queues a track by hand, into the listener's own section — "Play next" at its head, "Add to
     * queue" at its end, above the album and AutoPlay either way.
     */
    fun enqueue(song: Song, next: Boolean) {
        val queued = canonicalSong(song).copy(radioName = liveQueue.current?.radioName)
            .withSource(currentQueueSource())
        liveQueue = liveQueue.enqueue(queued, playNext = next)
        saveQueue()
    }

    /** Slots a track in right after the one playing. */
    fun playNext(song: Song) = enqueue(song, next = true)

    /** Puts a track at the end of what the listener queued — not the end of the queue. */
    fun addToQueue(song: Song) = enqueue(song, next = false)

    /** Starts the station YouTube Music builds around one track. */
    fun startRadio(song: Song) {
        val seed = canonicalSong(song).copy(radioName = song.title)
        scope.launch {
            // AutoPlay's section behind the seed, as the phone queues a station.
            val related = DesktopAutoplay.tracksFor(listOf(seed), seed, INITIAL_RADIO_TRACKS)
                .getOrNull()
                .orEmpty()
            if (related.isEmpty()) {
                DesktopTrackLog.log("radio: nothing to build a station on for '${song.title}'")
                return@launch
            }
            if (liveQueue.current?.videoId == seed.videoId) {
                liveQueue = DesktopQueue(listOf(liveQueue.current!!) + related, index = 0)
                saveQueue()
            } else {
                val station = listOf(seed.asQueueEntry(QueueTier.CONTEXT)) + related
                liveQueue = if (shuffle) DesktopQueue.shuffledStartingAt(station, 0) else DesktopQueue(station, 0)
                preShuffleOrder = if (shuffle) station.map(DesktopQueue::orderKey) else emptyList()
                playCurrent()
            }
            DesktopTrackLog.log("radio: started a station on '${song.title}' with ${related.size} tracks")
        }
    }

    /**
     * Opens an album or playlist in place of whatever page was open. The page it replaces is kept
     * in the history, not underneath: an artist page left standing under an album would be drawn
     * over it, which is how an album opened from an artist once went nowhere.
     */
    fun showCollection(collection: DesktopCollection) {
        openedArtist = null
        overlays.replay = false
        overlays.settingsPage = null
        collectionError = null
        openedCollection = collection
    }

    /**
     * Opens an album or playlist by id straight away, with a loader where the tracks go, the way an
     * artist's page opens — rather than leaving the click with nothing to show until YouTube answers.
     * [fallback] is what the card that was clicked already knew.
     */
    fun openCollection(browseId: String, fallback: BrowseItem? = null) {
        val current = openedCollection
        if (current?.browseId == browseId && !current.loading) {
            showCollection(current)
            return
        }
        showCollection(
            DesktopCollection(
                browseId = browseId,
                title = fallback?.title.orEmpty(),
                subtitle = fallback?.subtitle.orEmpty(),
                thumbnailUrl = fallback?.thumbnailUrl,
                type = fallback?.type?.takeIf { it != BrowseType.OTHER } ?: browseTypeOf(browseId),
                songs = emptyList(),
                loading = true,
            ),
        )
        collectionReloads++
    }

    // Keyed on the id, so a second page opened before the first answered cancels the first rather
    // than racing it; and a loading page restored by Back fetches again.
    LaunchedEffect(openedCollection?.browseId, openedCollection?.loading, collectionReloads) {
        val pending = openedCollection
            ?.takeIf { it.loading }
            ?: return@LaunchedEffect
        collectionError = null
        DesktopSearchClient.browse(
            browseId = pending.browseId,
            fallback = BrowseItem(
                browseId = pending.browseId,
                title = pending.title,
                subtitle = pending.subtitle,
                thumbnailUrl = pending.thumbnailUrl,
                type = pending.type,
            ),
        ).fold(
            onSuccess = { loaded ->
                if (openedCollection?.browseId == pending.browseId) openedCollection = loaded
            },
            onFailure = { collectionError = it.message ?: "Could not open ${pending.title.ifBlank { "this page" }}" },
        )
    }

    fun openAlbum(browseId: String) = openCollection(browseId)

    /** Copies the track's YouTube Music link. */
    fun shareSong(song: Song) {
        DesktopExternalLinks.copy("https://music.youtube.com/watch?v=${song.videoId}")
        DesktopTrackLog.log("copied a link to '${song.title}'")
    }

    /**
     * The link YouTube Music's own overflow shares for a release, built from the browse id rather
     * than fetched — nothing about it depends on the tracks or the account. Albums and playlists
     * only: an artist's is a channel link, not a release.
     */
    fun shareCollection(collection: DesktopCollection) {
        val id = collection.browseId
        val url = when (collection.type) {
            BrowseType.PLAYLIST -> "https://music.youtube.com/playlist?list=${id.removePrefix("VL")}"
            BrowseType.ALBUM -> "https://music.youtube.com/browse/$id"
            else -> return
        }
        DesktopExternalLinks.copy(url)
        DesktopTrackLog.log("copied a link to '${collection.title}'")
    }

    fun downloadSong(song: Song) {
        // A second tap on something already queued or running calls it off.
        if (song.videoId in downloadInProgress) {
            DesktopDownloadQueue.cancel(song.videoId)
            return
        }
        if (song.videoId in downloads.map(Song::videoId) || song.localPath != null) return
        DesktopDownloadQueue.enqueue(song)
    }

    /** Deletes the file a download saved and forgets the record. */
    fun removeDownload(song: Song) {
        scope.launch {
            withContext(Dispatchers.IO) { DesktopDownloadManager.delete(song) }
            downloads = downloads.filterNot { it.videoId == song.videoId }
            persistence.saveDownloads(downloads)
        }
    }

    /** Everything on a page, in the order it is listed. */
    fun downloadAll(songs: List<Song>) {
        val have = downloads.map(Song::videoId).toSet()
        DesktopDownloadQueue.enqueueAll(
            songs.filterNot { it.videoId in have || it.localPath != null },
        )
    }

    /** Keeps a station queued ahead of whatever is playing. */

    fun loadAutoplaySongs(playFirst: Boolean = false) {
        val current = selectedSong ?: return
        if (!autoplay || repeatMode == DesktopRepeatMode.ALL) return

        if (dontRepeatSuggestions) sessionSongHistory += current

        val needed = MAX_QUEUED_AUTOPLAY - liveQueue.songs.drop(liveQueue.index + 1).count { it.fromAutoplay }
        if (needed <= 0 && !playFirst) return
        // One request per seed.
        if (!playFirst && autoplaySeed == current.videoId) return
        autoplaySeed = current.videoId

        autoplayJob?.cancel()
        autoplayJob = scope.launch {
            var remaining = needed.coerceAtLeast(1)
            var emptyRefreshesRemaining = 1
            while (isActive) {
                DesktopTrackLog.log("autoplay: building a station from '${current.title}'")
                val at = liveQueue.songs.size
                // What the queue holds plus everything this session has already offered.
                val suggestions = DesktopAutoplay.tracksFor(
                    existing = if (dontRepeatSuggestions) {
                        liveQueue.songs + sessionSongHistory
                    } else {
                        liveQueue.songs
                    },
                    seedSong = current,
                    limit = remaining,
                ).getOrElse { failure ->
                    DesktopTrackLog.log("autoplay: could not build a station: ${failure.message}")
                    emptyList()
                }
                // The listener may have moved on while the station was being fetched; appending
                // then would attach it to the wrong seed.
                if (selectedSong?.videoId != current.videoId) return@launch
                if (suggestions.isEmpty()) {
                    DesktopTrackLog.log("autoplay: no station came back for '${current.title}'")
                    // Match the phone's bounded recovery: retry once after a short delay, but only
                    // while the current track genuinely has no tail to play next.
                    if (liveQueue.index < 0 || liveQueue.index != liveQueue.songs.lastIndex) return@launch
                    if (emptyRefreshesRemaining-- <= 0) return@launch
                    delay(AUTOPLAY_EMPTY_REFRESH_DELAY_MS)
                    remaining = MAX_QUEUED_AUTOPLAY
                    continue
                }
                // The station carries on from what was playing, so it keeps that queue's origin —
                // Android holds the source on every queue item, not just the ones picked by hand.
                val inherited = current.playbackSource?.let {
                    DesktopQueueSource(it, current.playbackSourceType ?: PlaybackSourceType.QUEUE, current.playbackSourceId)
                }
                val queuedSuggestions = suggestions.map { it.withSource(inherited) }
                liveQueue = liveQueue.append(queuedSuggestions)
                saveQueue()
                // The mix continues from where it was added rather than starting a queue of
                // its own.
                if (playFirst) playQueueIndex(at)
                if (dontRepeatSuggestions) sessionSongHistory += suggestions
                DesktopTrackLog.log(
                    "autoplay: queued ${suggestions.size} after '${current.title}'",
                )
                return@launch
            }
        }
    }

    /**
     * Turning AutoPlay on or off, and what that does to the queue.
     *
     * Android runs one path for this — `toggleAutoplayFromNotification` — whether the switch was
     * thrown in the player or anywhere else, and it does more than set a flag. Off drops the
     * suggestions it had already queued, because switching it off is the listener saying they do
     * not want them. On clears the seed first, so the station is fetched again for the track
     * already playing rather than being refused as one this seed has been loaded for.
     */
    fun setAutoplay(enabled: Boolean) {
        autoplay = enabled
        persistence.saveBoolean("autoplay", enabled)
        autoplayJob?.cancel()
        autoplayJob = null
        autoplaySeed = null
        if (enabled) {
            loadAutoplaySongs()
        } else {
            val trimmed = liveQueue.withoutAutoplay()
            if (trimmed !== liveQueue) {
                liveQueue = trimmed
                saveQueue()
            }
        }
    }

    fun playNext() {
        // Crossfade on skip: when enabled and the next deck is already prepared, blend into it over
        // a short crossfade instead of cutting. The blend's own hand-off advances the queue, so a
        // successful mix returns here without touching liveQueue; anything else falls through to the
        // ordinary skip below.
        if (crossfadeOnSkip && repeatMode != DesktopRepeatMode.ONE) {
            val currentId = selectedSong?.videoId
            val upcoming = currentId?.let {
                liveQueue.followingFor(it, repeatAll = repeatMode == DesktopRepeatMode.ALL)
            }
            if (upcoming != null && playbackEngine.beginSkipMix(upcoming.videoId, SKIP_MIX_ON_SKIP_SECONDS)) {
                return
            }
        }
        when {
            liveQueue.hasNext -> {
                liveQueue = liveQueue.next()
                playCurrent()
            }
            repeatMode == DesktopRepeatMode.ALL && liveQueue.songs.isNotEmpty() -> {
                liveQueue = liveQueue.copy(index = 0)
                playCurrent()
            }
            autoplay ->
                loadAutoplaySongs(playFirst = true)
        }
    }

    fun playPrevious() {
        val positionMs = playbackEngine.state.value.positionMs

        // After 10 seconds, Previous restarts the current song.
        if (positionMs > BACK_RESTARTS_AFTER_MS) {
            playbackEngine.seekTo(0L)
            return
        }

        // Within 10 seconds, Previous goes to the previous song in queue.
        if (!liveQueue.hasPrevious) return
        liveQueue = liveQueue.previous()
        playCurrent()
    }

    /**
     * Shuffle rearranges the queue rather than switching playback to a hidden random order, so what
     * the queue shows stays what plays.
     */
    fun setShuffle(enabled: Boolean) {
        shuffle = enabled
        persistence.saveBoolean("shuffle", enabled)
        liveQueue = if (enabled) {
            preShuffleOrder = liveQueue.orderKeys()
            liveQueue.shuffledAhead()
        } else {
            liveQueue.inOrderOf(preShuffleOrder).also { preShuffleOrder = emptyList() }
        }
        saveQueue()
    }

    fun closePlaylistDialogs() {
        overlays.playlistDialog = false
        playlistTarget = null
        overlays.rename = false
        overlays.delete = false
        overlays.reorder = false
        playlistError = null
        playlistBusy = false
    }

    /** Creates a playlist — on the account when there is one, otherwise here. */
    fun createPlaylist(title: String, privacy: PlaylistPrivacy) {
        val name = title.trim()
        if (name.isBlank()) return
        val seed = playlistTarget
        // A track from another source cannot go in an account playlist, so the playlist made to
        // hold it is one kept here.
        if (!DesktopYouTubeAuth.isSignedIn || (seed != null && !DesktopSearchClient.isVideoId(seed.videoId))) {
            playlists = playlists + DesktopPlaylist(title = name, songs = listOfNotNull(seed))
            persistence.savePlaylists(playlists)
            closePlaylistDialogs()
            return
        }
        playlistBusy = true
        playlistError = null
        scope.launch {
            DesktopSearchClient.createPlaylist(name, privacy, listOfNotNull(seed?.videoId))
                .onSuccess { playlistId ->
                    closePlaylistDialogs()
                    editPlaylistShelf { items ->
                        val card = ShelfItem(
                            title = name,
                            // Only what this request itself establishes: an unseeded playlist gets
                            // a card of just its name rather than a guess at what the feed will
                            // call it.
                            subtitle = if (seed != null) "1 song" else "",
                            thumbnailUrl = seed?.thumbnailUrl,
                            videoId = null,
                            browseId = "VL$playlistId",
                        )
                        // Leads the shelf because it is the newest, which is the order the feed
                        // itself comes in.
                        listOf(card) + items.filterNot { it.browseId == card.browseId }
                    }
                    libraryStale = true
                }
                .onFailure {
                    playlistBusy = false
                    playlistError = it.message ?: "Could not create the playlist"
                }
        }
    }

    fun openPlaylist(playlist: DesktopPlaylist) {
        showCollection(
            DesktopCollection(
                browseId = playlist.id,
                title = playlist.title,
                subtitle = "Playlist • ${playlist.songs.size} songs",
                thumbnailUrl = playlist.songs.firstOrNull()?.thumbnailUrl,
                type = BrowseType.PLAYLIST,
                songs = playlist.songs,
            ),
        )
    }

    fun addToPlaylist(playlist: DesktopPlaylist) {
        val song = playlistTarget ?: return
        playlists = playlists.map { current ->
            if (current.id == playlist.id) current.copy(songs = (current.songs + song).distinctBy(Song::videoId)) else current
        }
        persistence.savePlaylists(playlists)
        playlistTarget = null
    }

    fun addToAccountPlaylist(playlist: UserPlaylist) {
        val song = playlistTarget ?: return
        playlistBusy = true
        playlistError = null
        scope.launch {
            DesktopSearchClient.addToPlaylist(playlist.playlistId, listOf(song.videoId))
                .onSuccess { closePlaylistDialogs() }
                .onFailure {
                    DesktopTrackLog.log("playlist: could not add ${song.videoId}: ${it.message}")
                    playlistBusy = false
                    playlistError = it.message ?: "Could not add to ${playlist.title}"
                }
        }
    }

    playbackEngine = remember {
        DesktopPlaybackEngine(
            onEnded = {
                if (DesktopSleepTimer.afterTrack.value) {
                    DesktopSleepTimer.cancel()
                } else if (repeatMode == DesktopRepeatMode.ONE) {
                    // Replayed in place. [startSong] makes the track a queue of one, which threw
                    // the rest of the queue away the first time it repeated.
                    val song = selectedSong
                    if (song != null && liveQueue.current?.videoId == song.videoId) {
                        playCurrent()
                    } else {
                        song?.let { startSong(it, true) }
                    }
                } else {
                    playNext()
                }
            },
            onCrossfaded = { song ->
                // Announced from the audio thread. Publish the island's song first — it must not
                // wait for a main-window effect that may not be running while the window is hidden.
                // The queue still moves on the main thread below; the capsule's title and cover
                // are what have to change with the audio, immediately.
                DesktopIslandState.currentSong.value = song
                scope.launch {
                    // The queue moves with the audio. Only the selection used to, which left the
                    // queue on the track that had just finished — so Next "advanced" onto the song
                    // already playing, and every later skip landed one behind.
                    liveQueue = liveQueue.afterHandoffTo(song.videoId)
                    saveQueue()
                    selectedSong = song
                    history = (listOf(song) + history.filterNot { it.videoId == song.videoId }).take(50)
                    persistence.saveHistory(history)
                }
            },
        )
    }
    fun togglePlayPauseFromUser() {
        playbackEngine.togglePlayPause()
    }
    fun playFromUser() {
        playbackEngine.play()
    }
    fun pauseFromUser() {
        playbackEngine.pause()
    }
    val mprisController = remember(playbackEngine) {
        DesktopMprisController(
            onPlay = ::playFromUser,
            onPause = ::pauseFromUser,
            onPlayPause = ::togglePlayPauseFromUser,
            onNext = ::playNext,
            onPrevious = ::playPrevious,
            onShuffleChanged = { enabled ->
                shuffle = enabled
                persistence.saveBoolean("shuffle", enabled)
            },
            onLoopStatusChanged = { status ->
                repeatMode = when (status) {
                    "Track" -> DesktopRepeatMode.ONE
                    "Playlist" -> DesktopRepeatMode.ALL
                    else -> DesktopRepeatMode.OFF
                }
                persistence.saveString("repeat_mode", repeatMode.name)
            },
            onRateChanged = { value ->
                playbackSpeed = value.toFloat().coerceIn(0.25f, 3.0f)
                persistence.saveString("playback_speed", playbackSpeed.toString())
            },
            onVolumeChanged = { value ->
                volume = value.toFloat().coerceIn(0.0f, 1.0f)
                persistence.saveString("volume", volume.toString())
            },
            onSeek = { positionMs ->
                playbackEngine.seekTo(positionMs)
            },
        )
    }
    DisposableEffect(mprisController) {
        mprisController.start()
        onDispose { mprisController.stop() }
    }
    // The Windows half of the same idea. Linux has MPRIS above; on Windows this
    // is what the media keys and the volume flyout's card talk to.
    DisposableEffect(playbackEngine) {
        DesktopWindowsMedia.start(
            DesktopWindowsMedia.Controller(
                onPlay = ::playFromUser,
                onPause = ::pauseFromUser,
                onNext = ::playNext,
                onPrevious = ::playPrevious,
                onStop = ::pauseFromUser,
                onSeek = { positionMs ->
                    playbackEngine.seekTo(positionMs)
                },
            ),
        )
        onDispose { DesktopWindowsMedia.stop() }
    }

    LaunchedEffect(playbackSpeed) { playbackEngine.setPlaybackSpeed(playbackSpeed) }
    LaunchedEffect(volume) { playbackEngine.setVolume(volume) }
    LaunchedEffect(audioQuality) { playbackEngine.setAudioQuality(audioQuality.name) }
    // Every one of the seven settings renders the same curve, so they are collected together
    // rather than destructured — seven sources of one tuning is seven chances to read them in the
    // wrong order.
    val eqEnabled by DesktopEqualizerSettings.enabled.collectAsState()
    val eqMode by DesktopEqualizerSettings.mode.collectAsState()
    val eqToneX by DesktopEqualizerSettings.toneX.collectAsState()
    val eqToneY by DesktopEqualizerSettings.toneY.collectAsState()
    val eqFocused by DesktopEqualizerSettings.focused.collectAsState()
    val eqBalance by DesktopEqualizerSettings.balance.collectAsState()
    val eqBands by DesktopEqualizerSettings.bands.collectAsState()
    LaunchedEffect(eqEnabled, eqMode, eqToneX, eqToneY, eqFocused, eqBalance, eqBands) {
        playbackEngine.setEqualizer(eqEnabled, DesktopEqualizerSettings.curve(), eqBalance)
    }
    LaunchedEffect(spatialAudio, skipSilence, outputPrecision) {
        playbackEngine.setSpatialAudio(spatialAudio)
        playbackEngine.setSkipSilence(skipSilence)
        playbackEngine.setOutputPrecision(outputPrecision)
    }
    LaunchedEffect(crossfadeOnSkip, crossfadeSeconds, audioQuality, selectedSong?.videoId, queue, liveQueue.index, shuffle, repeatMode) {
        playbackEngine.setCrossfadeSeconds(crossfadeSeconds)
        playbackEngine.setSkipMixEnabled(crossfadeOnSkip)
        // Exactly what Next would play, so a crossfade can never blend into anything else. Shuffle
        // is already the queue's order; picking "the first track that is not this one" under it
        // blended into a song from history whenever the current one was not at the top.
        // Repeat-one prepares nothing: the track ends and [onEnded] starts it again, rather than
        // blending into the next one.
        val next = selectedSong
            ?.takeIf { repeatMode != DesktopRepeatMode.ONE }
            ?.let { liveQueue.followingFor(it.videoId, repeatAll = repeatMode == DesktopRepeatMode.ALL) }
        playbackEngine.prepareNext(next)
    }
    // Topped up on every track change, the way Android does it, rather than only once the queue has
    // run dry.
    LaunchedEffect(selectedSong?.videoId, autoplay, repeatMode) {
        loadAutoplaySongs()
    }
    LaunchedEffect(liveQueue.songs.size, liveQueue.index) {
        val currentId = selectedSong?.videoId
        val shouldRefresh = DesktopAutoplay.queueNeedsRefresh(
            enabled = autoplay,
            repeatAll = repeatMode == DesktopRepeatMode.ALL,
            currentIndex = liveQueue.index,
            itemCount = liveQueue.songs.size,
            loadInProgress = autoplayJob?.isActive == true,
        )
        // Only re-arm a seed that has already been attempted. The track-change effect above owns
        // the initial load; this effect owns later local or server queue edits that expose a tail.
        if (shouldRefresh && currentId != null && autoplaySeed == currentId) {
            autoplayJob = null
            autoplaySeed = null
            loadAutoplaySongs()
        }
    }
    // No timer armed: no ticking at all. "Stop after this track" is handled at the track end,
    // not by a wall-clock poll, so it does not need a loop either.
    LaunchedEffect(sleepTimerMinutes) {
        if (sleepTimerMinutes == null) return@LaunchedEffect
        while (currentCoroutineContext().isActive) {
            delay(1_000L)
            if (DesktopSleepTimer.isExpired()) {
                DesktopSleepTimer.cancel()
                playbackEngine.pause()
            }
        }
    }
    val playback by playbackEngine.state.collectAsState()

    // The shared player reads the playhead off this one object, and only where it
    // draws it — see PlaybackPosition — so a tick never recomposes the player.
    val playerPosition = remember { PlaybackPosition() }
    LaunchedEffect(playbackEngine) {
        // Keyed only on the engine, never on a screen state: this collector keeps running while
        // the main window is hidden. The island is the only surface left then, so its lyrics and
        // playhead are fed here rather than from an effect the window may stop recomposing.
        var islandSongId: String? = null
        playbackEngine.state.collect { state ->
            // The audio thread's own timestamp, not this collector's: it runs on the UI thread and
            // gets to a reading as late as the UI is busy.
            playerPosition.report(state.positionMs, state.positionSampledAtNanos)
            playerPosition.seeks = state.seeks
            playerPosition.advancing = !state.awaitingAudio
            DesktopIslandState.currentPositionMs.value = state.positionMs
            DesktopIslandState.durationMs.value = state.durationMs
            DesktopIslandState.isPlaying.value = state.isPlaying

            val playing = state.song
            if (playing != null && DesktopIslandState.currentSong.value?.videoId != playing.videoId) {
                DesktopIslandState.currentSong.value = playing
            }
            if (playing == null || state.durationMs <= 0L) return@collect
            if (playing.videoId == islandSongId) return@collect
            islandSongId = playing.videoId
            // Drop the last track's words before the lookup, so the capsule never shows a line
            // belonging to a song that has already finished.
            DesktopIslandState.lyricsLines.value = null
            // The window's own lookup effect still runs when the window is on screen and owns the
            // player UI. This one only keeps the capsule's lines current when that effect cannot —
            // DesktopLyricsClient caches by track, so the duplication costs no second network race.
            DesktopLyricsClient.lookup(playing, state.durationMs).onSuccess { found ->
                if (DesktopIslandState.currentSong.value?.videoId == playing.videoId) {
                    DesktopIslandState.lyricsLines.value = found.lines
                }
            }.onFailure { error ->
                // A silent miss was indistinguishable from a broken provider.
                DesktopTrackLog.log(
                    "island lyrics: no lines for ${playing.title} — ${error.message ?: "unknown error"}",
                )
            }
        }
    }

    // The island draws in its own window, so the lyric panel's romanized lines — kept in that
    // panel's Compose state — are out of reach, especially while the main window is hidden. Give
    // the capsule its own reading of the same sheet whenever the shared setting asks for one.
    // Keyed on the preference too, so switching it on mid-song does not wait for the next track.
    val islandRomanization by DesktopPlayerSettings.romanizedLyricsByDefault.collectAsState()
    LaunchedEffect(islandRomanization) {
        if (!islandRomanization) DesktopIslandState.romanizedLines.value = null
    }
    LaunchedEffect(islandRomanization) {
        if (!islandRomanization) return@LaunchedEffect
        DesktopIslandState.lyricsLines.collect { source ->
            val song = DesktopIslandState.currentSong.value
            DesktopIslandState.romanizedLines.value = null
            if (song == null || source.isNullOrEmpty()) return@collect
            when (
                val result = DesktopPlayerHost.romanizeLyrics(
                    trackId = song.videoId,
                    lines = source,
                    // Romanization always ends in Latin script; the target is only what the
                    // provider's endpoint requires, and is deliberately outside its cache key.
                    targetLanguageTag = "en",
                )
            ) {
                is LyricsRomanizationResult.Romanized -> {
                    // A slow answer must not put an earlier track's reading back over the new one.
                    if (
                        DesktopIslandState.currentSong.value?.videoId == song.videoId &&
                        DesktopIslandState.lyricsLines.value === source
                    ) {
                        DesktopIslandState.romanizedLines.value = result.lines
                    }
                }
                else -> Unit
            }
        }
    }

    // The old position/duration effect is folded into the collector above: two writers to the same
    // two snapshot values let a hidden window's stopped collector leave the island progress frozen.
    // Lyrics and motion artwork follow whatever is *playing*, not whatever page happens to be open.
    // This is where Android keeps it — `MainActivity`, keyed on the player's own track — so the
    // lookup starts the moment a track does and the panel is already populated by the time anyone
    // opens the player. Run from the player page instead, nothing was fetched until it was opened,
    // and a track half a minute in then showed a skeleton and arrived mid-song.
    var lyrics by remember { mutableStateOf<DesktopLyrics?>(null) }
    // The provider picked for this track in the player's lyrics drawer, if any.
    var lyricsOnly by remember(selectedSong?.videoId) { mutableStateOf<String?>(null) }
    // A title and artist typed into the song menu's lyrics search, overriding the
    // track's own credits for the lookup — for a cover or a mislabelled upload.
    var lyricsQuery by remember(selectedSong?.videoId) { mutableStateOf<Pair<String, String>?>(null) }
    // Bumped on every search so re-running the same query still refetches.
    var lyricsSearchTick by remember(selectedSong?.videoId) { mutableStateOf(0) }
    // What each provider's most recent race turned up, so the song menu's
    // provider drawer can say "Found" or "Not found" per source, not just name
    // the winner. It is keyed on the track and is only ever added to, never
    // cleared mid-track: a repeat search a cache answers straight away fires no
    // callbacks, so clearing on start would blank the very states it should hold.
    val lyricsProviderResults = remember(selectedSong?.videoId) {
        mutableStateOf<Map<LyricsSource, LyricsProviderState>>(emptyMap())
    }
    var lyricsLoading by remember { mutableStateOf(false) }
    var lyricsError by remember { mutableStateOf<String?>(null) }

    // The length the lyrics are matched on, taken once per track. A better rendition swapped in
    // mid-song reports its own, slightly different length — or none, for a manifest — and keyed
    // on the live duration, every such upgrade threw away the lyrics on screen and fetched them
    // all over again. Only the playing track's own report counts: right after a skip, [playback]
    // still describes the track before.
    var lyricsLengthMs by remember(selectedSong?.videoId) { mutableStateOf(0L) }
    val playingLengthMs = playback.durationMs.takeIf { playback.song?.videoId == selectedSong?.videoId } ?: 0L
    LaunchedEffect(selectedSong?.videoId, playingLengthMs > 0L) {
        if (lyricsLengthMs <= 0L && playingLengthMs > 0L) lyricsLengthMs = playingLengthMs
    }

    // Keyed on the duration too: it lands a beat after the track, and a database match needs it,
    // so looking up against a length of zero would settle on the wrong recording.
    LaunchedEffect(
        selectedSong?.videoId,
        lyricsLengthMs,
        syncedLyrics,
        prioritizeSyllables,
        lyricsOn,
        lyricsOrder,
        lyricsOnly,
        lyricsQuery,
        lyricsSearchTick,
    ) {
        val current = selectedSong
        lyrics = null
        lyricsError = null
        if (current == null) {
            lyricsLoading = false
            return@LaunchedEffect
        }
        if (!syncedLyrics) {
            lyricsError = "Synced lyrics are switched off"
            lyricsLoading = false
            return@LaunchedEffect
        }
        // A length is what a database match is made on, and it lands a beat after the track. Looking
        // up without one would settle on whichever recording shares the name — so this waits, the
        // same way Android's `loadLyrics` turns the call away until a duration exists.
        val length = lyricsLengthMs.takeIf { it > 0L } ?: current.durationMillis()
        if (length <= 0L) {
            lyricsLoading = true
            return@LaunchedEffect
        }
        lyricsLoading = true
        // The manual search asks the same providers under the typed words rather
        // than the track's own credits; a provider switch keeps that query.
        val probe = lyricsQuery?.let { (title, artist) -> current.copy(title = title, artist = artist) } ?: current
        DesktopLyricsClient.lookup(
            probe,
            length,
            only = lyricsOnly,
            // Each provider reports itself as the race runs; the callback is on
            // the IO thread, so the map is updated back on the composition's own.
            onSourceResult = { source, found ->
                scope.launch {
                    lyricsProviderResults.value += source to
                        if (found) LyricsProviderState.FOUND else LyricsProviderState.NOT_FOUND
                }
            },
        ).fold(
            onSuccess = { lyrics = it },
            onFailure = { lyricsError = it.message ?: "Lyrics unavailable" },
        )
        lyricsLoading = false
    }

    // A manual ask from the song menu's lyrics drawer, for a track whose automatic
    // lookup came up empty and whose real title or artist the listener knows
    // better than the tag. It just sets the query and nudges the effect above,
    // which owns the loading state and the result — so the drawer's provider
    // list updates through the same path as the automatic lookup.
    fun searchLyrics(title: String, artist: String) {
        lyricsQuery = title to artist
        lyricsSearchTick++
    }

    // The song menu's lyrics offset nudges the whole synced lyric against the clock. It is a
    // per-track adjustment, so a new track starts back at zero; the desktop owns the reset so the
    // phone's sheet, which carries the offset across tracks, is left alone.
    val lyricsOffsetMs by DesktopPlayerSettings.lyricsOffsetMs.collectAsState()
    LaunchedEffect(selectedSong?.videoId) {
        DesktopPlayerSettings.setLyricsOffsetMs(0)
    }

    LaunchedEffect(
        playback.song?.videoId,
        playback.isPlaying,
        playback.durationMs,
        playback.volume,
        playback.error,
        shuffle,
        repeatMode,
        playbackSpeed,
    ) {
        mprisController.update(
            playback = playback,
            shuffle = shuffle,
            loopStatus = when (repeatMode) {
                DesktopRepeatMode.OFF -> "None"
                DesktopRepeatMode.ALL -> "Playlist"
                DesktopRepeatMode.ONE -> "Track"
            },
            rate = playbackSpeed.toDouble(),
        )
    }
    LaunchedEffect(playback.song?.videoId, playback.isPlaying, playback.durationMs) {
        DesktopWindowsMedia.publish(playback)
    }
    // Refetched on opening the page rather than cached for the session: the
    // point of it is that something played elsewhere shows up here.
    LaunchedEffect(destination, youtubeSignedIn, sessionRevision) {
        if (destination != DesktopDestination.HISTORY || !youtubeSignedIn) return@LaunchedEffect
        DesktopSearchClient.history().onSuccess { remoteHistory = it }
    }
    // The account's own history on YouTube Music, which is what the home feed is built out of.
    DisposableEffect(playback.song?.videoId) {
        val previous = playbackEngine.state.value.positionMs / 1_000
        onDispose { DesktopPlaybackTracker.onTrackChanged(previous) }
    }
    LaunchedEffect(playback.song?.videoId, playback.isPlaying) {
        val songId = playback.song?.videoId ?: return@LaunchedEffect
        if (!playback.isPlaying) return@LaunchedEffect
        DesktopPlaybackTracker.onPlaying(songId)
        while (currentCoroutineContext().isActive) {
            delay(HISTORY_SAMPLE_MS)
            val snapshot = playbackEngine.state.value
            if (snapshot.song?.videoId != songId) break
            DesktopPlaybackTracker.onProgress(songId, snapshot.positionMs / 1_000)
        }
    }
    LaunchedEffect(playback.song?.videoId, playback.isPlaying) {
        val songId = playback.song?.videoId ?: return@LaunchedEffect
        if (!playback.isPlaying) return@LaunchedEffect
        var previousPosition = playback.positionMs
        var countedAsPlay = songId in statsCountedSongs
        while (currentCoroutineContext().isActive) {
            delay(STATS_SAMPLE_MS)
            val snapshot = playbackEngine.state.value
            if (!snapshot.isPlaying || snapshot.song?.videoId != songId) break
            val position = snapshot.positionMs
            val delta = (position - previousPosition).takeIf { it in 1L..STATS_MAX_DELTA_MS } ?: 0L
            if (delta > 0L) {
                val threshold = snapshot.durationMs.takeIf { it > 0L }
                    ?.let { minOf(it / 2L, LISTENING_PLAY_THRESHOLD_MS) }
                    ?: LISTENING_PLAY_THRESHOLD_MS
                val reachedThreshold = !countedAsPlay && position >= threshold
                DesktopListeningStats.record(snapshot.song, delta, reachedThreshold)
                if (reachedThreshold) {
                    countedAsPlay = true
                    statsCountedSongs = statsCountedSongs + songId
                }
                statsRevision++
            }
            previousPosition = position
        }
    }
    LaunchedEffect(statsRevision) {
        replaySummary = withContext(Dispatchers.IO) { DesktopListeningStats.summary(replayPeriod) }
    }
    DisposableEffect(playbackEngine) {
        onDispose { playbackEngine.release() }
    }

    /** Opens an artist's page, from a card, a search row or the player's credit. */
    fun openArtist(browseId: String, name: String) {
        val target = DesktopArtistTarget(browseId, name)
        if (openedArtist == target && artistState is UiState.Success) return
        openedCollection = null
        overlays.replay = false
        overlays.settingsPage = null
        openedArtist = target
        artistState = UiState.Loading
        artistReloads++
    }

    /**
     * Puts [song] on the Dynamic Island: the capsule is switched on, and the track starts when it
     * is not the one already loaded — the menu row reads "Play", so it should play.
     */
    fun playInDynamicIsland(song: Song) {
        DesktopDynamicIslandSettings.setEnabled(true)
        if (selectedSong?.videoId == song.videoId) {
            if (!playback.isPlaying) playFromUser()
        } else {
            playSong(song)
        }
    }

    /**
     * The "…" menu for one row, wherever it is drawn.
     *
     * Built once so a row on Downloads or Local Music offers what the player's own "…" offers —
     * Android opens the same `SongActionsSheet` from every one of them.
     */
    fun songActionsFor(song: Song) = DesktopSongActions(
        signedIn = youtubeSignedIn,
        disliked = song.videoId in dislikedIds,
        downloaded = downloads.any { it.videoId == song.videoId },
        downloadInProgress = song.videoId in downloadInProgress,
        sleepTimerMinutes = sleepTimerMinutes,
        sleepAfterTrack = sleepAfterTrack,
        onToggleDislike = ::toggleDislike,
        onAddToPlaylist = { playlistTarget = it },
        onDownload = ::downloadSong,
        onRemoveDownload = ::removeDownload,
        onStartRadio = ::startRadio,
        onPlayNext = ::playNext,
        onAddToQueue = ::addToQueue,
        onPlayInIsland = ::playInDynamicIsland,
        onOpenAlbum = ::openAlbum,
        onOpenArtist = { id -> openArtist(id, song.artist) },
        onSleepTimer = { minutes ->
            if (minutes == null) DesktopSleepTimer.cancel() else DesktopSleepTimer.start(minutes)
        },
        onSleepAfterTrack = { DesktopSleepTimer.startAfterTrack() },
        onShare = ::shareSong,
    )

    /** The "…" a song row hangs off, built the same way on every page that offers one. */
    @Composable
    fun songMenu(song: Song) = DesktopSongMenuAnchor(
        song = song,
        liked = song.videoId in likedIds,
        actions = songActionsFor(song),
        onToggleLike = { toggleLike(song) },
        onRevertToOriginal = null,
        onUpgradeQuality = null,
    )

    /** Replay counts artists by name, so the page has to be found from the name alone. */
    fun openArtistByName(name: String) {
        scope.launch {
            val match = DesktopSearchClient.search(name, SearchFilter.ARTISTS)
                .getOrDefault(emptyList())
                .filterIsInstance<SearchResult.Browse>()
                .map { it.item }
                .firstOrNull { it.browseId.isNotBlank() }
                ?: return@launch
            openArtist(match.browseId, match.title)
        }
    }

    LaunchedEffect(openedArtist, artistReloads) {
        val target = openedArtist ?: return@LaunchedEffect
        if (artistState !is UiState.Loading) return@LaunchedEffect
        DesktopSearchClient.artistPage(target.browseId).fold(
            onSuccess = { artistState = UiState.Success(it) },
            onFailure = { artistState = UiState.Error(it.message ?: "Could not open ${target.name}") },
        )
    }

    fun toggleSubscription(subscription: SubscriptionState) {
        val target = openedArtist ?: return
        val next = !subscription.subscribed
        // Painted before the network answers and put back if it refuses.
        artistState = (artistState as? UiState.Success)?.let { current ->
            UiState.Success(current.data.copy(subscription = subscription.copy(subscribed = next)))
        } ?: artistState
        scope.launch {
            DesktopSearchClient.setSubscribed(subscription.channelId, next).onFailure { failure ->
                DesktopTrackLog.log("subscription: ${target.name} failed: ${failure.message}")
                artistState = (artistState as? UiState.Success)?.let { current ->
                    UiState.Success(current.data.copy(subscription = subscription))
                } ?: artistState
            }
        }
    }

    /** A search hit or a recent search that is a page: an artist's own, or a collection's. */
    fun openBrowseItem(item: BrowseItem) {
        if (item.type == BrowseType.ARTIST) {
            openArtist(item.browseId, item.title)
        } else {
            openCollection(item.browseId, item)
        }
    }

    fun openShelfItem(item: ShelfItem, shelfTitle: String? = null) {
        val videoId = item.videoId
        val browseId = item.browseId
        when {
            videoId != null -> {
                val source = shelfTitle?.let { DesktopQueueSource(it, PlaybackSourceType.HOME) }
                playSong(item.toSong(), source = source)
            }
            // An artist is a page of its own, not a list of tracks with a photograph on top.
            browseTypeOf(browseId.orEmpty()) == BrowseType.ARTIST ->
                openArtist(browseId!!, item.title)
            browseId != null -> openCollection(
                browseId = browseId,
                fallback = BrowseItem(
                    browseId = browseId,
                    title = item.title,
                    subtitle = item.subtitle,
                    thumbnailUrl = item.thumbnailUrl,
                    type = BrowseType.OTHER,
                ),
            )
        }
    }

    fun openMenu(song: Song) = pageMenu(song)

    /** What the phone records for a search hit: the thing picked, not the words typed to find it. */
    fun recordSongSearch(song: Song) = DesktopSearchHistory.record(
        SearchHistoryEntity(
            id = song.videoId,
            title = song.title,
            subtitle = song.artist,
            artworkUrl = song.thumbnailUrl,
            entityType = EntityType.TRACK,
        ),
    )

    fun recordBrowseSearch(item: BrowseItem) = DesktopSearchHistory.record(
        SearchHistoryEntity(
            id = item.browseId,
            title = item.title,
            subtitle = item.subtitle,
            artworkUrl = item.thumbnailUrl,
            entityType = when (item.type) {
                BrowseType.ALBUM -> EntityType.ALBUM
                BrowseType.ARTIST -> EntityType.ARTIST
                BrowseType.PLAYLIST -> EntityType.PLAYLIST
                else -> EntityType.TRACK
            },
        ),
    )

    /** Typing, from the page's field or the sidebar's: the typeahead follows, and an empty field goes back to recents. */
    fun editQuery(text: String) {
        query = text
        searchTyping = true
        if (text.isBlank()) {
            searchCommitted = false
            searchRows = emptyList()
            searchError = null
        }
    }

    fun search() {
        if (query.isBlank() || searchLoading) return
        overlays.settingsPage = null
        destination = DesktopDestination.SEARCH
        searchTyping = false
        searchSuggestions = emptyList()
        // Submitting is picking the first track the typeahead offered, as on the phone — or, with
        // none, the first the results bring back.
        val directVideoId = DesktopYouTubeVideo.idFromSearchInput(query)
        val typedHit = if (directVideoId == null) searchTypeahead.firstNotNullOfOrNull { row ->
            when (row) {
                is SearchResult.TopTrack -> row.song
                is SearchResult.Track -> row.song
                else -> null
            }
        } else null
        typedHit?.let(::recordSongSearch)
        searchCommitted = true
        searchScrollReset++
        searchLoading = true
        searchError = null
        if (directVideoId != null) {
            searchFilter = SearchFilter.VIDEOS
            searchRows = emptyList()
        }
        scope.launch {
            try {
                if (directVideoId != null) {
                    DesktopSearchClient.youtubeVideo(directVideoId).fold(
                        onSuccess = { song ->
                            searchRows = listOf(SearchResult.Track(song))
                            recordSongSearch(song)
                            playSong(song, source = DesktopQueueSource(
                                DesktopStrings["search", "Search"],
                                PlaybackSourceType.SEARCH,
                            ))
                        },
                        onFailure = { searchError = it.message ?: "Could not load this YouTube video" },
                    )
                    return@launch
                }
                DesktopMusicSources.search(query, searchFilter).fold(
                    onSuccess = { rows ->
                        searchRows = rows
                        if (typedHit == null) {
                            rows.firstNotNullOfOrNull { row ->
                                when (row) {
                                    is SearchResult.TopTrack -> row.song
                                    is SearchResult.Track -> row.song
                                    else -> null
                                }
                            }?.let(::recordSongSearch)
                        }
                    },
                    onFailure = { searchError = it.message ?: "Search failed" },
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Throwable) {
                searchError = failure.message ?: "Search failed"
            } finally {
                // OBS and other desktop capture tools can briefly interrupt focus/coroutines.
                searchLoading = false
            }
        }
    }

    /** The typeahead, one request behind the keystrokes rather than one per. */
    LaunchedEffect(query, searchTyping) {
        if (!searchTyping || query.isBlank()) {
            searchSuggestions = emptyList()
            return@LaunchedEffect
        }
        if (DesktopYouTubeVideo.looksLikeVideoInput(query)) {
            searchSuggestions = emptyList()
            return@LaunchedEffect
        }
        delay(SUGGESTION_DEBOUNCE_MS)
        DesktopSearchClient.searchSuggestions(query)
            .onSuccess { if (searchTyping) searchSuggestions = it }
            .onFailure { searchSuggestions = emptyList() }
    }

    // Its own pass, on its own delay: a search costs far more than a completion, so it waits for a
    // longer pause in the typing rather than riding the same one.
    LaunchedEffect(query, searchTyping) {
        if (!searchTyping || query.isBlank()) {
            searchTypeahead = emptyList()
            return@LaunchedEffect
        }
        if (DesktopYouTubeVideo.looksLikeVideoInput(query)) {
            searchTypeahead = emptyList()
            return@LaunchedEffect
        }
        delay(TYPEAHEAD_MEDIA_DEBOUNCE_MS)
        DesktopSearchClient.searchTypeahead(query)
            .onSuccess { if (searchTyping) searchTypeahead = it.take(TYPEAHEAD_MEDIA_LIMIT) }
            .onFailure { searchTypeahead = emptyList() }
    }

    fun runSearch(term: String) {
        query = term
        searchTyping = false
        searchSuggestions = emptyList()
        searchTypeahead = emptyList()
        search()
    }

    fun selectDestination(next: DesktopDestination) {
        if (next == DesktopDestination.LIBRARY && libraryStale) {
            libraryStale = false
            reloadLibrary()
        }
        openedArtist = null
        openedCollection = null
        overlays.replay = false
        overlays.settingsPage = null
        selectedMoodGenre = null
        libraryShowAll = null
        if (next == DesktopDestination.SEARCH) searchFocusRequested = true
        destination = next
    }

    // Settings' search and scroll outlive a visit to one of its pages, and start over each time
    // Settings itself is opened.
    var settingsSession by remember { mutableStateOf(0) }
    var settingsQuery by remember(settingsSession) { mutableStateOf("") }
    val settingsListState = remember(settingsSession) { LazyListState() }

    /** Settings in place of the page — from the sidebar, the account switcher or the tray. */
    fun openSettings() {
        if (overlays.settingsPage == null) settingsSession++
        overlays.settingsPage = DesktopSettingsPage.MAIN
    }

    /** The Library's two local folders; account playlists live in the YouTube Music shelves. */
    val libraryLinks = remember {
        listOf(
            LibraryLink(
                item = ShelfItem(
                    title = DesktopStrings["downloads", "Downloads"],
                    subtitle = DesktopStrings["downloaded_songs", "Downloaded songs"],
                    thumbnailUrl = null,
                    videoId = null,
                    browseId = LOCAL_DOWNLOADS_ID,
                ),
                icon = Icons.Rounded.Download,
            ),
            LibraryLink(
                item = ShelfItem(
                    title = DesktopStrings["local_music", "Local Music"],
                    subtitle = DesktopStrings["d_audio_files_on_this_computer", "Audio files on this computer"],
                    thumbnailUrl = null,
                    videoId = null,
                    browseId = LOCAL_MUSIC_ID,
                ),
                icon = Icons.Rounded.Folder,
            ),
        )
    }

    /**
     * The Library's "On device" shelf: the playlists kept on this computer rather than on the
     * account — the desktop's counterpart of the phone's downloaded releases, and like them
     * reachable from nowhere else.
     */
    val libraryDeviceItems = remember(playlists) {
        playlists.map { playlist ->
            ShelfItem(
                title = playlist.title,
                subtitle = "Playlist • ${playlist.songs.size} songs",
                thumbnailUrl = playlist.songs.firstOrNull()?.thumbnailUrl,
                videoId = null,
                browseId = LOCAL_PLAYLIST_PREFIX + playlist.id,
            )
        }
    }

    /** A Library card: a folder is a page of this app's, a local playlist is opened from here. */
    fun openLibraryItem(item: ShelfItem) {
        val browseId = item.browseId.orEmpty()
        when {
            browseId == LOCAL_DOWNLOADS_ID -> selectDestination(DesktopDestination.DOWNLOADS)
            browseId == LOCAL_MUSIC_ID -> selectDestination(DesktopDestination.LOCAL_MUSIC)
            browseId.startsWith(LOCAL_PLAYLIST_PREFIX) ->
                playlists.firstOrNull { it.id == browseId.removePrefix(LOCAL_PLAYLIST_PREFIX) }?.let(::openPlaylist)
            else -> openShelfItem(item)
        }
    }

    /**
     * Adds shelves to the page as they arrive, skipping any heading already on it and any shelf
     * that repeats one's releases — the phone's `publishHomeShelves`. Recently Played is
     * [prepend]ed and replaces any stale copy the core feed carried.
     */
    fun publishHomeShelves(shelves: List<HomeShelf>, prepend: Boolean = false) {
        val existing = (homeState as? UiState.Success)?.data.orEmpty()
        if (prepend) {
            val replacing = shelves.map { it.title.lowercase() }.toSet()
            homeSeenTitles.addAll(replacing)
            homeState = UiState.Success(shelves + existing.filterNot { it.title.lowercase() in replacing })
            return
        }
        val added = shelves.withoutRepeatsOf(existing)
            .filter { it.items.isNotEmpty() && homeSeenTitles.add(it.title.lowercase()) }
        if (added.isNotEmpty()) homeState = UiState.Success(existing + added)
    }

    /**
     * The phone's Home: the core feed, the account's own Recently Played at its head, and the
     * supplementary browse feeds, all asked for at once and laid down as each lands. Signed out
     * there is no Recently Played to ask for, which is also how the phone has it.
     */
    fun loadHome() {
        if (!youtubeSourceEnabled) {
            homeState = UiState.Error("YouTube Music is disabled")
            return
        }
        val generation = ++homeGeneration
        val signedIn = DesktopYouTubeAuth.isSignedIn
        homeIdentity = "$signedIn/${DesktopAccounts.activeAccountId()}"
        homeState = UiState.Loading
        homeContinuation = null
        homeSeenTitles.clear()
        homeLoadingMore = false
        homeRecentsLoading = signedIn
        scope.launch {
            launch {
                DesktopSearchClient.home().fold(
                    onSuccess = { feed ->
                        if (generation != homeGeneration) return@fold
                        homeContinuation = feed.continuation
                        publishHomeShelves(feed.shelves)
                    },
                    onFailure = {
                        if (generation == homeGeneration && homeState !is UiState.Success) {
                            homeState = UiState.Error(it.message ?: "Could not load Home")
                        }
                    },
                )
            }
            if (signedIn) {
                launch {
                    YtMusicRepository.homeRecentlyPlayed()
                        .onSuccess { shelf ->
                            if (generation != homeGeneration) return@onSuccess
                            homeRecentsLoading = false
                            shelf?.let { publishHomeShelves(listOf(it), prepend = true) }
                        }
                        .onFailure { if (generation == homeGeneration) homeRecentsLoading = false }
                }
            }
            DesktopSearchClient.HOME_SUPPLEMENT_BROWSE_IDS.forEach { browseId ->
                launch {
                    DesktopSearchClient.homeSupplement(browseId).onSuccess { shelves ->
                        if (generation == homeGeneration) publishHomeShelves(shelves)
                    }
                }
            }
        }
    }

    /** The next page of the core feed, as the list nears its end; a no-op once it has run dry. */
    fun loadMoreHome() {
        val token = homeContinuation ?: return
        if (homeLoadingMore || homeState !is UiState.Success) return
        val generation = homeGeneration
        homeLoadingMore = true
        scope.launch {
            DesktopSearchClient.moreHome(token).fold(
                onSuccess = { feed ->
                    if (generation != homeGeneration) return@fold
                    val before = homeSeenTitles.size
                    publishHomeShelves(feed.shelves)
                    // A page with nothing new is the feed looping back on itself, not running dry.
                    homeContinuation = feed.continuation.takeIf { homeSeenTitles.size > before }
                },
                onFailure = { homeContinuation = null },
            )
            homeLoadingMore = false
        }
    }

    // The tray icon speaks StatusNotifierItem rather than going through AWT, which only implements
    // the XEmbed tray a Wayland session does not have.
    val tray = remember {
        DesktopStatusNotifierController(
            // Clicking the icon is how the window comes back once it has been closed to the tray,
            // so it has to raise the window before it does anything inside it.
            onActivate = {
                DesktopWindowVisibility.show()
                DesktopDynamicIslandSettings.setEnabled(true)
            },
            onPlayPause = { if (selectedSong != null) togglePlayPauseFromUser() },
        )
    }
    DisposableEffect(tray, trayIconEnabled) {
        if (trayIconEnabled) tray.start()
        onDispose { tray.stop() }
    }
    // Deliberately not a key on the effect above.
    DisposableEffect(trayIconEnabled, closeToTray) {
        // The listener's choice, with the tray as its precondition: hiding with no tray to come
        // back from leaves an audible process and no window.
        DesktopWindowVisibility.keepRunningWhenClosed = trayIconEnabled && closeToTray
        onDispose { DesktopWindowVisibility.keepRunningWhenClosed = false }
    }
    LaunchedEffect(tray, trayIconEnabled) {
        if (!trayIconEnabled) return@LaunchedEffect
        // The logo travels to the tray as pixels: a themed icon name would be whatever the user's
        // theme draws for a generic music player.
        runCatching { Res.readBytes("drawable/logo.svg") }.onSuccess(tray::setIcon)
    }
    LaunchedEffect(tray) {
        DesktopTrayMenu.bind(
            onPlayPause = { if (selectedSong != null) togglePlayPauseFromUser() },
            onNext = ::playNext,
            onPrevious = ::playPrevious,
            onOpenPlayer = {
                DesktopWindowVisibility.show()
                DesktopDynamicIslandSettings.setEnabled(true)
            },
            onOpenSettings = {
                DesktopWindowVisibility.show()
                openSettings()
            },
            onToggleIsland = { DesktopIslandState.toggle() },
            onQuit = {
                // Native work first: the audio thread and the media controls are inside FFmpeg,
                // WASAPI and WinRT, and exitProcess would unload those libraries under them.
                playbackEngine.shutdown()
                DesktopWindowsMedia.stop()
                exitProcess(0)
            },
        )
    }
    LaunchedEffect(islandEnabled) {
        // The tray row is a verb, not a noun: re-label it whenever the island is switched,
        // including from the island's own menu or Settings. tray.update() rebuilds the AWT
        // popup from the current entries, so the new label reaches both tray backends.
        DesktopTrayMenu.publishIsland(islandEnabled)
        tray.update(title = selectedSong?.let { "${it.title} — ${it.artist}" }, isPlaying = playback.isPlaying)
    }

    LaunchedEffect(selectedSong, playback.isPlaying) {
        val title = selectedSong?.let { "${it.title} — ${it.artist}" }
        tray.update(title = title, isPlaying = playback.isPlaying)
        DesktopTrayMenu.publish(title = title, playing = playback.isPlaying)
    }

    LaunchedEffect(Unit) {
        DesktopHorizontalScroll.install()
        localSongs = withContext(Dispatchers.IO) { DesktopLocalMusic.scan() }
        // A download record is a claim about a folder this app does not own, so it can outlive the
        // file it names. Checked once at startup rather than trusted: a stale entry showed the
        // track as downloaded, refused to fetch it again, and then would not play.
        val onDisk = withContext(Dispatchers.IO) { DesktopDownloadManager.verified(downloads) }
        if (onDisk.size != downloads.size) {
            DesktopTrackLog.log(
                "downloads: ${downloads.size - onDisk.size} recorded file(s) are gone; forgetting them",
            )
            downloads = onDisk
            persistence.saveDownloads(onDisk)
        }
        loadHome()
    }

    // The feed is the account's: signing in, out or across to another channel is a different Home,
    // and Recently Played only exists with an account behind it.
    LaunchedEffect(youtubeSignedIn, activeAccountId) {
        val identity = "${DesktopYouTubeAuth.isSignedIn}/${DesktopAccounts.activeAccountId()}"
        if (homeIdentity != null && identity != homeIdentity) loadHome()
    }

    // The mark on a downloaded row, for the shared pages.
    LaunchedEffect(downloads) {
        DesktopAppUiHost.downloaded.value = downloads.mapTo(HashSet(), Song::videoId)
    }

    // What reached disk goes into the library's own record, which is what the Downloads page and
    // every row's tick read from.
    LaunchedEffect(Unit) {
        DesktopDownloadQueue.finished.collect { saved ->
            downloads = (listOf(saved) + downloads.filterNot { it.videoId == saved.videoId }).take(200)
            persistence.saveDownloads(downloads)
        }
    }

    // The charts are period-filtered at read time, so a different period is a different read.
    LaunchedEffect(replayPeriod) {
        replaySummary = withContext(Dispatchers.IO) { DesktopListeningStats.summary(replayPeriod) }
    }

    // Re-scanned when the filter or the folder changes, never on first composition.
    LaunchedEffect(localMusicRevision) {
        if (localMusicRevision == 0) return@LaunchedEffect
        localSongs = withContext(Dispatchers.IO) { DesktopLocalMusic.scan() }
        DesktopLocalMusicWatcher.restart()
    }

    // A track dropped into the folder appears without a relaunch.
    LaunchedEffect(Unit) {
        DesktopLocalMusicWatcher.restart()
        DesktopLocalMusicWatcher.changes.collect {
            localSongs = withContext(Dispatchers.IO) { DesktopLocalMusic.scan() }
        }
    }

    suspend fun loadExplore() {
        if (!youtubeSourceEnabled) {
            exploreState = UiState.Error("YouTube Music is disabled")
            return
        }
        exploreState = UiState.Loading
        DesktopSearchClient.moodAndGenres().fold(
            onSuccess = { sections ->
                exploreState = if (sections.isEmpty()) {
                    UiState.Error("Nothing to explore right now")
                } else {
                    UiState.Success(sections)
                }
            },
            onFailure = { exploreState = UiState.Error(it.message ?: "Could not load Explore") },
        )
    }

    LaunchedEffect(destination, exploreReloads) {
        if (destination == DesktopDestination.EXPLORE &&
            (exploreState is UiState.Loading || exploreReloads > 0)
        ) {
            loadExplore()
        }
    }

    // Category buttons carry no artwork of their own, so a few are resolved at a time from the
    // shelves they open.
    LaunchedEffect(exploreState) {
        val sections = (exploreState as? UiState.Success)?.data ?: return@LaunchedEffect
        if (sections.all { section -> section.items.all { it.thumbnailUrl != null } }) return@LaunchedEffect
        val limiter = Semaphore(4)
        coroutineScope {
            sections.flatMap(MoodGenreSection::items)
                .filter { it.thumbnailUrl == null }
                .forEach { item ->
                    launch {
                        val artwork = limiter.withPermit {
                            DesktopSearchClient.moodGenreArtwork(item.browseId, item.params).getOrNull()
                        } ?: return@launch
                        val current = (exploreState as? UiState.Success)?.data ?: return@launch
                        exploreState = UiState.Success(
                            current.map { section ->
                                section.copy(
                                    items = section.items.map { entry ->
                                        if (entry.browseId == item.browseId && entry.params == item.params) {
                                            entry.copy(thumbnailUrl = artwork)
                                        } else {
                                            entry
                                        }
                                    },
                                )
                            },
                        )
                    }
                }
        }
    }

    /** The next page of the open collection, appended in place. */
    fun loadMoreCollectionSongs() {
        val collection = openedCollection ?: return
        val token = collection.continuation ?: return
        if (collectionLoadingMore) return
        collectionLoadingMore = true
        scope.launch {
            DesktopSearchClient.moreCollectionSongs(token, collection.songs.firstOrNull()?.artist)
                .onSuccess { (songs, next) ->
                    val current = openedCollection
                    if (current != null && current.browseId == collection.browseId) {
                        openedCollection = current.copy(
                            songs = (current.songs + songs).distinctBy(Song::videoId),
                            continuation = next,
                        )
                    }
                }
                .onFailure {
                    DesktopTrackLog.log("collection: no more rows for ${collection.browseId}: ${it.message}")
                    val current = openedCollection
                    if (current != null && current.browseId == collection.browseId) {
                        // Dropped rather than retried on scroll for ever: the trailing row is what
                        // asks, and leaving the token in place would have it ask again on every
                        // frame.
                        openedCollection = current.copy(continuation = null)
                    }
                }
            collectionLoadingMore = false
        }
    }

    fun renameOpenPlaylist(title: String) {
        val collection = openedCollection ?: return
        playlistBusy = true
        playlistError = null
        scope.launch {
            DesktopSearchClient.renamePlaylist(collection.playlistId, title)
                .onSuccess {
                    // Renamed in place as well as re-fetched: the page that ordered it is still on
                    // screen showing the old name, and the library reload behind it takes a moment.
                    openedCollection = openedCollection
                        ?.takeIf { it.browseId == collection.browseId }
                        ?.copy(title = title)
                        ?: openedCollection
                    editPlaylistShelf { items ->
                        items.map { if (it.browseId == collection.browseId) it.copy(title = title) else it }
                    }
                    closePlaylistDialogs()
                    libraryStale = true
                }
                .onFailure {
                    playlistBusy = false
                    playlistError = it.message ?: "Could not rename the playlist"
                }
        }
    }

    fun deleteOpenPlaylist() {
        val collection = openedCollection ?: return
        playlistBusy = true
        playlistError = null
        scope.launch {
            DesktopSearchClient.deletePlaylist(collection.playlistId)
                .onSuccess {
                    closePlaylistDialogs()
                    // Its page is the one open, and a deleted playlist has nothing left to show.
                    if (openedCollection?.browseId == collection.browseId) openedCollection = null
                    editPlaylistShelf { items -> items.filterNot { it.browseId == collection.browseId } }
                    libraryStale = true
                }
                .onFailure {
                    playlistBusy = false
                    playlistError = it.message ?: "Could not delete the playlist"
                }
        }
    }

    /** Takes one row back out of the playlist it is being read in. */
    fun removeFromOpenPlaylist(song: Song) {
        val collection = openedCollection ?: return
        val setVideoId = song.setVideoId ?: return
        scope.launch {
            DesktopSearchClient.removeFromPlaylist(
                collection.playlistId,
                listOf(setVideoId to song.videoId),
            ).onSuccess {
                val current = openedCollection
                if (current != null && current.browseId == collection.browseId) {
                    openedCollection = current.copy(
                        songs = current.songs.filterNot { it.setVideoId == setVideoId },
                    )
                }
            }.onFailure {
                DesktopTrackLog.log("playlist: could not remove ${song.videoId}: ${it.message}")
            }
        }
    }

    /**
     * Sends a new order for the playlist being read. [original] and [reordered] are the same whole
     * list of entries — as it was and as the user left it — since a reorder is sent as moves relative
     * to neighbours and needs every entry to move against.
     */
    fun reorderOpenPlaylist(original: List<Song>, reordered: List<Song>) {
        val collection = openedCollection ?: return
        playlistBusy = true
        playlistError = null
        scope.launch {
            DesktopSearchClient.reorderPlaylist(
                collection.playlistId,
                current = original.mapNotNull { it.setVideoId },
                target = reordered.mapNotNull { it.setVideoId },
            ).onSuccess {
                val current = openedCollection
                if (current != null && current.browseId == collection.browseId) {
                    // The whole list arrived to be reordered, so the page now shows all of it and has
                    // no further page to ask for.
                    openedCollection = current.copy(songs = reordered, continuation = null)
                }
                closePlaylistDialogs()
                libraryStale = true
            }.onFailure {
                playlistBusy = false
                playlistError = it.message ?: "Could not reorder the playlist"
            }
        }
    }

    /**
     * The account's library, re-read whenever the session behind it changes — signing in, switching
     * channel, signing out.
     */
    LaunchedEffect(activeAccountId, activeProfileId, sessionRevision) {
        youtubeSignedIn = DesktopYouTubeAuth.isSignedIn
        if (!DesktopYouTubeAuth.isSignedIn) {
            libraryState = UiState.Success(LibraryPage(emptyList(), emptyList(), emptyList()))
            return@LaunchedEffect
        }
        libraryState = UiState.Loading
        DesktopSearchClient.library().fold(
            onSuccess = { page ->
                // The continuation is a job to run, not page state — it is consumed below.
                libraryState = UiState.Success(page.copy(likedContinuation = null))
                DesktopTrackLog.log(
                    "library: ${page.likedSongs.size} liked, ${page.librarySongs.size} added, " +
                        "shelves ${page.shelves.joinToString { "${it.title}=${it.items.size}" }}",
                )
                val fromAccount = page.likedSongs.mapTo(HashSet(), Song::videoId)
                if (!likedIds.containsAll(fromAccount)) {
                    likedIds = likedIds + fromAccount
                    // Off the drawing thread: an account with a long Liked Music writes a few tens
                    // of kilobytes through the preference store, and that is a disk flush.
                    withContext(Dispatchers.IO) { persistence.saveLikedIds(likedIds) }
                }
                // Liked Music is published to the tab a page budget deep; the rest is followed here
                // for its ids alone, so a liked track past the budget still reads as liked.
                // Tied to the account it was fetched for: the liked set is one shared map, and a
                // sync still running after a switch would seed it with a stranger's likes.
                val syncingFor = activeAccountId
                page.likedContinuation?.let { token ->
                    likedSyncJob?.cancel()
                    likedSyncJob = scope.launch {
                        val extra = HashSet<String>()
                        DesktopSearchClient.syncLikedIds(token, onIds = { ids -> extra += ids })
                        if (syncingFor != DesktopAccounts.activeAccountId()) return@launch
                        if (!likedIds.containsAll(extra)) {
                            likedIds = likedIds + extra
                            withContext(Dispatchers.IO) { persistence.saveLikedIds(likedIds) }
                            DesktopTrackLog.log("library: liked sync added ${extra.size} ids past the first pages")
                        }
                    }
                }
            },
            onFailure = { libraryState = UiState.Error(it.message ?: "Could not load your library") },
        )
        // A session refused mid-flight is dropped by the request itself, so the answer to "are we
        // signed in" is only settled once the call is over.
        youtubeSignedIn = DesktopYouTubeAuth.isSignedIn
    }

    fun here() = DesktopNavEntry(
        destination = destination,
        artist = openedArtist,
        collection = openedCollection,
        mood = selectedMoodGenre,
        showAll = libraryShowAll,
        replay = overlays.replay,
        settings = overlays.settingsPage,
    )

    // Every move between places is a visit, however it was made — the sidebar, a card, a search
    // hit, the player's credits. The place left is what back returns to.
    LaunchedEffect(Unit) {
        var last: DesktopNavEntry? = null
        snapshotFlow { here() }.collect { now ->
            val before = last
            if (before != null && before.key != now.key) {
                if (navRestoring[0]) {
                    navRestoring[0] = false
                } else {
                    navHistory.add(before)
                    if (navHistory.size > NAV_HISTORY_LIMIT) navHistory.removeAt(0)
                }
            }
            // The newest copy of the same place, so an album that has paged in more tracks comes
            // back with them.
            last = now
        }
    }

    /** Back to the place before this one — the top bar's arrow, the mouse's back button, Alt+Left. */
    fun goBack() {
        val entry = navHistory.removeLastOrNull() ?: return
        if (entry.key == here().key) return
        navRestoring[0] = true
        destination = entry.destination
        overlays.replay = entry.replay
        overlays.settingsPage = entry.settings
        collectionError = null
        openedCollection = entry.collection
        libraryShowAll = entry.showAll
        if (entry.mood != selectedMoodGenre) {
            selectedMoodGenre = entry.mood
            moodGenreShelves = UiState.Loading
        }
        if (entry.artist != openedArtist) {
            openedArtist = entry.artist
            if (entry.artist != null) {
                artistState = UiState.Loading
                artistReloads++
            }
        }
    }

    fun openMoodGenre(item: MoodGenre) {
        selectedMoodGenre = item
        moodGenreShelves = UiState.Loading
    }

    LaunchedEffect(selectedMoodGenre, moodGenreReloads) {
        val item = selectedMoodGenre ?: return@LaunchedEffect
        if (moodGenreShelves !is UiState.Loading) return@LaunchedEffect
        DesktopSearchClient.moodGenreShelves(item.browseId, item.params).fold(
            onSuccess = { shelves ->
                moodGenreShelves = if (shelves.isEmpty()) {
                    UiState.Error("Nothing to explore here yet")
                } else {
                    UiState.Success(shelves)
                }
            },
            onFailure = { moodGenreShelves = UiState.Error(it.message ?: "Could not load this category") },
        )
    }

    // The menu the shared player's "…" opens.
    var playerMenuOpen by remember(selectedSong?.videoId) { mutableStateOf(false) }
    // The player's own overflow (volume, the lyrics/output/queue row, the lyric
    // footer), raised from the song menu's "More…" and drawn by the shared
    // player. Held here so the desktop song menu can open it.
    val playerOverflowOpen = remember(selectedSong?.videoId) { mutableStateOf(false) }
    // Requests the song menu sends the player (switch lyrics/queue, translate),
    // flipped here and consumed by it, and the player's state mirrored back so the
    // menu's rows say what is on screen. Not keyed to the track: the player's own
    // panel and translation state outlive a skip, so copies recreated per track
    // would contradict it until it next changed.
    val playerQueueToggle = remember { mutableStateOf(false) }
    val playerQueueShowing = remember { mutableStateOf(false) }
    val playerTranslateToggle = remember { mutableStateOf(false) }
    val playerShowingTranslation = remember { mutableStateOf(false) }
    // Where the player's "…" is, in window coordinates, so its menu hangs off it.
    val playerMenuAnchor = remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }

    /** The song menu's verbs for [song], as the player sheet offers them. */
    fun playerSongActions(song: Song) = DesktopSongActions(
        signedIn = youtubeSignedIn,
        disliked = song.videoId in dislikedIds,
        downloaded = downloads.any { it.videoId == song.videoId },
        downloadInProgress = song.videoId in downloadInProgress,
        sleepTimerMinutes = sleepTimerMinutes,
        sleepAfterTrack = sleepAfterTrack,
        onToggleDislike = ::toggleDislike,
        onAddToPlaylist = { playlistTarget = it },
        onDownload = ::downloadSong,
        onRemoveDownload = ::removeDownload,
        onStartRadio = ::startRadio,
        onPlayNext = ::playNext,
        onAddToQueue = ::addToQueue,
        onPlayInIsland = ::playInDynamicIsland,
        onOpenAlbum = ::openAlbum,
        onOpenArtist = { id -> openArtist(id, song.artist) },
        onSleepTimer = { minutes ->
            if (minutes == null) DesktopSleepTimer.cancel() else DesktopSleepTimer.start(minutes)
        },
        onSleepAfterTrack = { DesktopSleepTimer.startAfterTrack() },
        onShare = ::shareSong,
    )

    // What the shared player reads as settings, kept in step with the window's own.
    SideEffect {
        DesktopPlayerSettings.lyricsBlur.value = lyricsBlur
        DesktopPlayerSettings.syncedLyrics.value = syncedLyrics
        DesktopPlayerSettings.showNerdStats.value = showNerdStats
        DesktopPlayerSettings.mixInProgress.value = playback.mixing
        DesktopPlayerSettings.transitionWindow.value = playback.transitionWindow
        DesktopPlayerSettings.lyricsSourceOrder.value =
            DesktopLyricsClient.enabledSources(lyricsOrder, lyricsOn).mapNotNull(::lyricsSourceNamed)
        DesktopPlayerHost.volumeLevel.value = volume
        DesktopPlayerHost.onVolumeChange = {
            volume = it
            persistence.saveString("volume", it.toString())
        }
        // The island's own menus reach the app through here: Settings and the lyrics column live
        // in the main window. Playback never opens the full player from the capsule — one song,
        // one screen.
        DesktopIslandState.onOpenSettings = { openSettings() }
        DesktopIslandState.onOpenLyrics = { overlays.toggleSidePanel(DesktopSidePanel.LYRICS) }
    }

    // The engine's measured stream, as the player's quality badge and stats line read it.
    LaunchedEffect(playback.streamFormat, playback.streamSourceId) {
        NerdStats.current.value = playback.streamFormat?.nerdSnapshot(playback.streamSourceId)
    }
    LaunchedEffect(playback.searchingBetter, selectedSong?.videoId) {
        NerdStats.racingLossless.value = selectedSong?.videoId
            ?.takeIf { playback.searchingBetter }
            ?.let(::setOf)
            .orEmpty()
    }
    LaunchedEffect(playback.streamFormat, playback.isPlaying) {
        DesktopPlayerHost.pipeline.value = playbackEngine.pipeline()
    }
    DesktopPlayerHost.pipelineDialog = { onDismiss ->
        DesktopAudioPipelineDialog(
            format = playback.streamFormat,
            sourceName = playback.streamSourceId?.let { id ->
                sourceConfigs.firstOrNull { it.id == id }?.displayName
            },
            pipeline = playbackEngine.pipeline(),
            onDismiss = onDismiss,
        )
    }

    // What the player and the lyrics column beside the page both read, worked out once for the two.
    val sharedLyrics = lyrics?.lines

    fun seekPlayer(target: Long) {
        val duration = playback.durationMs
        playbackEngine.seekTo(
            if (duration > 0) target.coerceIn(0L, duration) else target.coerceAtLeast(0L),
        )
    }

    /** Every island press means "the island is the player now" — retire any page under it. */
    fun dismissPlayerPages() {
        overlays.sidePanel = null
        openedCollection = null
    }

    LaunchedEffect(selectedSong, playback.isPlaying, repeatMode, liveQueue) {
        // Song, lyrics and playhead are fed by paths that keep running with the window hidden —
        // mirrorIslandSong(), the lookup effect and the engine collector above. Only the callbacks
        // and repeat mode are window-owned; writing the others here would overwrite a newer value
        // the audio thread had already put on the island.
        DesktopIslandState.isPlaying.value = playback.isPlaying
        DesktopIslandState.repeatMode.value = repeatMode
        // The queue rides along, so the capsule's menu can start any row of it — the same jump
        // the queue sheet's tap makes — with the main window closed.
        DesktopIslandState.queueSongs.value = liveQueue.songs
        DesktopIslandState.queueIndex.value = liveQueue.index
        DesktopIslandState.onPlayPause = {
            dismissPlayerPages()
            if (selectedSong != null) togglePlayPauseFromUser()
        }
        DesktopIslandState.onNext = {
            dismissPlayerPages()
            playNext()
        }
        DesktopIslandState.onPrevious = {
            dismissPlayerPages()
            playPrevious()
        }
        DesktopIslandState.onPlayQueueIndex = {
            dismissPlayerPages()
            playQueueIndex(it)
        }
        // The island's scrub row speaks in fractions of the song; the player takes milliseconds.
        DesktopIslandState.onSeek = { fraction ->
            dismissPlayerPages()
            val duration = playback.durationMs
            if (duration > 0L) seekPlayer((fraction.coerceIn(0f, 1f) * duration).toLong())
        }
        DesktopIslandState.onToggleRepeat = {
            dismissPlayerPages()
            repeatMode = repeatMode.next()
            persistence.saveString("repeat_mode", repeatMode.name)
        }
    }
    val sharedLyricsSource = remember(lyrics) { lyrics?.source?.let(::lyricsSourceNamed) }
    val sharedLyricsUnavailable = !lyricsLoading && sharedLyrics.isNullOrEmpty()
    val providerStates = remember(lyricsOrder, lyricsOn, sharedLyricsSource, lyricsLoading) {
        DesktopLyricsClient.enabledSources(lyricsOrder, lyricsOn)
            .mapNotNull(::lyricsSourceNamed)
            .associateWith { source ->
                when {
                    source == sharedLyricsSource -> LyricsProviderState.FOUND
                    lyricsProviderResults.value[source] != null -> lyricsProviderResults.value.getValue(source)
                    lyricsLoading -> LyricsProviderState.FETCHING
                    else -> LyricsProviderState.NOT_FETCHED
                }
            }
    }

    // Every provider the song menu's lyrics drawer lists — all of them, in the
    // saved order, not just the enabled ones — each carrying what its last race
    // turned up so the row can read "Found", "Not found" or "Searching…". The
    // winner is the one the sheet shows now.
    val menuLyricsProviders = remember(
        lyricsOrder,
        lyricsOn,
        lyricsOnly,
        lyricsLoading,
        lyrics?.source,
        lyricsProviderResults.value,
    ) {
        val currentLabel = lyrics?.source
        val asked = lyricsOnly?.let { listOf(it) } ?: DesktopLyricsClient.enabledSources(lyricsOrder, lyricsOn)
        lyricsOrder.map { label ->
            val source = lyricsSourceNamed(label)
            val state = when {
                label == currentLabel -> LyricsProviderState.FOUND
                source != null && lyricsProviderResults.value[source] != null -> lyricsProviderResults.value.getValue(source)
                lyricsLoading && label in asked -> LyricsProviderState.FETCHING
                else -> LyricsProviderState.NOT_FETCHED
            }
            DesktopLyricsProviderRow(label, state, label == currentLabel)
        }
    }

    // The queue's edits, for the player's queue and the queue column alike.
    fun removeFromQueue(at: Int) {
        val edited = liveQueue.removeAt(at)
        if (edited !== liveQueue) {
            liveQueue = edited
            saveQueue()
        }
    }

    fun moveInQueue(from: Int, to: Int) {
        val edited = liveQueue.move(from, to)
        if (edited !== liveQueue) {
            liveQueue = edited
            saveQueue()
        }
    }

    // Clears what the listener queued by hand, as the phone's Clear does; the album and AutoPlay
    // stay where they are.
    fun clearQueue() {
        val edited = liveQueue.withoutUserQueue()
        if (edited === liveQueue) return
        liveQueue = edited
        saveQueue()
    }

    val pageTitle = when {
        overlays.settingsPage != null -> DesktopStrings["settings", "Settings"]
        openedCollection != null -> openedCollection?.title.orEmpty()
        openedArtist != null -> openedArtist?.name.orEmpty()
        else -> destination.label
    }

    MaterialTheme(
        colorScheme = desktopColorScheme(),
        typography = desktopTypography(),
    ) {
        val ambientBackdrop by DesktopAppearanceSettings.ambientBackdrop.collectAsState()
        val windowActions = LocalDesktopWindowActions.current
        CompositionLocalProvider(
            LocalContentColor provides Color.White,
            LocalNowPlaying provides selectedSong,
        ) {
            DesktopFrame(
                // The phone's page is black, not the near-black of its cards.
                containerColor = DesktopBackground,
                // Only Replay dresses itself unless ambient backdrop is on; everywhere else the chrome sits on the plain surface.
                backdrop = { transparentBase ->
                    val ambientArtworkUrl = replaySummary.songs.firstOrNull()?.song?.thumbnailUrl
                        .takeIf { overlays.replay }
                        ?: selectedSong?.thumbnailUrl.takeIf { ambientBackdrop }
                    DesktopPageBackdrop(
                        artworkUrl = ambientArtworkUrl,
                        transparentBase = transparentBase,
                    )
                },
                modifier = Modifier.onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyUp) return@onPreviewKeyEvent false
                    when {
                        event.key == Key.MediaPlayPause -> {
                            if (selectedSong != null) togglePlayPauseFromUser()
                            true
                        }
                        event.key == Key.MediaNext -> {
                            playNext()
                            true
                        }
                        event.key == Key.MediaPrevious -> {
                            playPrevious()
                            true
                        }
                        event.key == Key.F11 -> {
                            DesktopWindowMode.toggle()
                            true
                        }
                        // The hint in the sidebar's field, and the way back to it from anywhere.
                        event.isCtrlPressed && !event.isAltPressed && event.key == Key.K -> {
                            if (overlays.settingsPage != null) overlays.settingsPage = null
                            selectDestination(DesktopDestination.SEARCH)
                            true
                        }
                        // Windows' own shortcut for the island, so it is reachable without the
                        // mouse while the user is working in something else.
                        event.isCtrlPressed && event.isAltPressed && event.key == Key.D -> {
                            DesktopIslandState.toggle()
                            true
                        }
                        event.key == Key.DirectionLeft && event.isAltPressed -> {
                            goBack()
                            true
                        }




                        // One layer at a time, innermost first: the lyrics or queue column,
                        // then a page of Settings.
                        event.key == Key.Escape -> when {
                            overlays.sidePanel != null -> {
                                overlays.sidePanel = null
                                true
                            }
                            // A page of Settings steps back the way the back button does, unless
                            // one of its prompts is up over it.
                            overlays.settingsPage != null -> {
                                goBack()
                                true
                            }
                            else -> false
                        }
                        else -> false
                    }
                },
                topBar = { compact ->
                    DesktopTopBar(
                        compact = compact,
                        title = pageTitle,
                        // A second click on the same button puts the column away, as in Apple Music.
                        onOpenLyrics = { overlays.toggleSidePanel(DesktopSidePanel.LYRICS) },
                        onOpenQueue = { overlays.toggleSidePanel(DesktopSidePanel.QUEUE) },
                        sidePanel = overlays.sidePanel,
                        accountAvatar = activeAccount?.profiles?.firstOrNull { it.profileId == activeProfileId }?.avatar?.takeIf { it.isNotBlank() }
                            ?: activeAccount?.avatar?.takeIf { it.isNotBlank() }
                            ?: activeAccount?.profiles?.firstOrNull()?.avatar?.takeIf { it.isNotBlank() },
                        onOpenAccounts = { DesktopTrackLog.log("accounts: opening the switcher"); overlays.accounts = true },
                        canGoBack = navHistory.isNotEmpty(),
                        onBack = ::goBack,
                    )
                },
                sidebar = {
                    DesktopSidebar(
                        destination = destination,
                        settingsOpen = overlays.settingsPage != null,
                        accountPlaylists = sidebarAccountPlaylists,
                        localPlaylists = playlists,
                        openedCollectionId = openedCollection?.browseId,
                        query = query,
                        onQueryChange = { text ->
                            editQuery(text)
                            if (destination != DesktopDestination.SEARCH || overlays.settingsPage != null) {
                                selectDestination(DesktopDestination.SEARCH)
                            }
                        },
                        onSearch = ::search,
                        onDestinationSelected = ::selectDestination,
                        onOpenAccountPlaylist = { openShelfItem(it, PLAYLISTS_SHELF) },
                        onOpenLocalPlaylist = ::openPlaylist,
                        onOpenSettings = ::openSettings,
                        focusSearch = searchFocusRequested,
                        onSearchFocused = { searchFocusRequested = false },
                        signedIn = youtubeSignedIn,
                        onSignIn = { overlays.accounts = true },
                    )
                },
                bottomBar = { compact ->
                    DesktopBottomChrome(
                        compact = compact,
                        destination = destination,
                        onDestinationSelected = ::selectDestination,
                    )
                },
                // The island is the one player surface. The old in-window dock grew into a second
                // full player on hover — the panel that duplicated the island — so it is gone.
                // Playback still answers to the island, the media keys and the song rows.
                dock = {},
                trailing = {
                    val current = selectedSong
                    DesktopSidePanelColumn(
                        panel = overlays.sidePanel.takeIf { current != null },
                        onClose = { overlays.sidePanel = null },
                    ) { which ->
                        if (current == null) return@DesktopSidePanelColumn
                        when (which) {
                            DesktopSidePanel.LYRICS -> LyricsSidePanel(
                                song = current,
                                isPlaying = playback.isPlaying,
                                position = playerPosition,
                                lyrics = sharedLyrics,
                                lyricsSource = sharedLyricsSource,
                                lyricsProviderStates = providerStates,
                                onSelectLyricsProvider = { source -> lyricsOnly = source.label },
                                lyricsUnavailable = sharedLyricsUnavailable,
                                onSeek = ::seekPlayer,
                                modifier = Modifier.fillMaxSize(),
                            )
                            DesktopSidePanel.QUEUE -> QueueSidePanel(
                                queue = liveQueue.songs,
                                queueIndex = liveQueue.index,
                                autoplayEnabled = autoplay,
                                onJumpTo = ::playQueueIndex,
                                onRemove = ::removeFromQueue,
                                onMove = ::moveInQueue,
                                onClear = ::clearQueue,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                },
                overlay = {
                    // The floating capsule is a window of its own now — see DesktopIslandWindow —
                    // so that hiding this window leaves it on screen.

                    if (overlays.accounts) {
                        DesktopAccountSelector(
                            accounts = accounts,
                            activeAccountId = activeAccountId,
                            activeProfileId = activeProfileId,
                            busy = signInBusy != null,
                            onSelect = { account, profile ->
                                DesktopAccounts.select(account.accountId, profile.profileId)
                                activeAccountId = account.accountId
                                activeProfileId = profile.profileId
                            },
                            onAddAccount = { overlays.accounts = false; overlays.signIn = true },
                            onSignOut = { account ->
                                scope.launch {
                                    accounts = withContext(Dispatchers.IO) {
                                        DesktopAccounts.remove(account.accountId)
                                        DesktopAccounts.accounts()
                                    }
                                    activeAccountId = DesktopAccounts.activeAccountId()
                                    activeProfileId = DesktopAccounts.activeProfileId()
                                    DesktopTrackLog.log(
                                        "accounts: signed out of " +
                                            account.email.ifBlank { account.name.ifBlank { "an account" } },
                                    )
                                }
                            },
                            onOpenSettings = { overlays.accounts = false; openSettings() },
                            onDismiss = { overlays.accounts = false },
                        )
                    }

                    if (overlays.signIn) {
                        DesktopSignInDialog(
                            busy = signInBusy,
                            error = signInError,
                            webSignIn = DesktopWebSignIn.available,
                            onWebSignIn = {
                                webSignInJob?.cancel()
                                webSignInJob = scope.launch {
                                    signInError = null
                                    signInBusy = WEB_SIGN_IN
                                    try {
                                        val cookie = DesktopWebSignIn.captureYouTube()
                                        signIn(cookie, WEB_SIGN_IN)
                                    } catch (cancelled: kotlinx.coroutines.CancellationException) {
                                        throw cancelled
                                    } catch (failure: Exception) {
                                        DesktopTrackLog.log("sign-in: YouTube Music failed: ${failure.message}")
                                        signInError = failure.message ?: "Could not complete the sign-in."
                                    } finally {
                                        signInBusy = null
                                        webSignInJob = null
                                    }
                                }
                            },
                            onDismiss = {
                                webSignInJob?.cancel()
                                overlays.signIn = false
                                signInError = null
                            },
                        )
                    }
                    if (overlays.playlistDialog || playlistTarget != null) {
                        DesktopPlaylistDialog(
                            song = playlistTarget,
                            accountPlaylists = accountPlaylists,
                            localPlaylists = playlists,
                            signedIn = youtubeSignedIn,
                            canUseAccount = playlistTarget
                                ?.let { DesktopSearchClient.isVideoId(it.videoId) } ?: true,
                            busy = playlistBusy,
                            error = playlistError,
                            onPickAccount = ::addToAccountPlaylist,
                            onPickLocal = ::addToPlaylist,
                            onCreate = ::createPlaylist,
                            onDismiss = ::closePlaylistDialogs,
                        )
                    }
                    if (overlays.rename) {
                        DesktopRenamePlaylistDialog(
                            current = openedCollection?.title.orEmpty(),
                            busy = playlistBusy,
                            error = playlistError,
                            onRename = ::renameOpenPlaylist,
                            onDismiss = ::closePlaylistDialogs,
                        )
                    }
                    if (overlays.delete) {
                        DesktopDeletePlaylistDialog(
                            title = openedCollection?.title.orEmpty(),
                            busy = playlistBusy,
                            error = playlistError,
                            onDelete = ::deleteOpenPlaylist,
                            onDismiss = ::closePlaylistDialogs,
                        )
                    }
                    val reorderCollection = openedCollection?.takeIf { overlays.reorder }
                    if (reorderCollection != null) {
                        DesktopReorderPlaylistDialog(
                            browseId = reorderCollection.browseId,
                            busy = playlistBusy,
                            error = playlistError,
                            onSave = ::reorderOpenPlaylist,
                            onDismiss = ::closePlaylistDialogs,
                            songContextMenu = { song, onDismiss ->
                                DesktopSongMenuFor(
                                    song = song,
                                    liked = song.videoId in likedIds,
                                    actions = songActionsFor(song),
                                    onToggleLike = { toggleLike(song) },
                                    onRevertToOriginal = null,
                                    onUpgradeQuality = null,
                                    onDismiss = onDismiss,
                                )
                            },
                        )
                    }
                    if (overlays.downloadManager) {
                        DesktopDownloadManagerDialog(onDismiss = { overlays.downloadManager = false })
                    }
                    if (overlays.audioOutput) {
                        DesktopAudioOutputDialog(onDismiss = { overlays.audioOutput = false })
                    }
                    if (overlays.pipeline) {
                        DesktopAudioPipelineDialog(
                            format = playback.streamFormat,
                            sourceName = playback.streamSourceId?.let { id ->
                                sourceConfigs.firstOrNull { it.id == id }?.displayName
                            },
                            pipeline = playbackEngine.pipeline(),
                            onDismiss = { overlays.pipeline = false },
                        )
                    }
                },
            ) { contentPadding ->
                // Desktop pages start at the same top edge as Library. Only preserve space for the
                // floating player/navigation bar at the bottom in compact windows.
                val sharedPagePadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())
                CompositionLocalProvider(
                    LocalPullToRefreshEnabled provides false,
                    LocalShelfRowChrome provides DesktopShelfRowChrome,
                ) {
                DesktopPointerMenuHost<Song>(
                    menu = { song, onDismiss ->
                        DesktopSongMenuFor(
                            song = song,
                            liked = song.videoId in likedIds,
                            actions = songActionsFor(song),
                            onToggleLike = { toggleLike(song) },
                            onRevertToOriginal = null,
                            onUpgradeQuality = null,
                            onDismiss = onDismiss,
                        )
                    },
                ) { openMenu ->
                pageMenu = openMenu
                CompositionLocalProvider(LocalDesktopContextMenu provides openMenu) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    if (event.type == PointerEventType.Press && event.buttons.isBackPressed) goBack()
                                }
                            }
                        },
                ) {
                    when {
                        // Settings and its pages stand in for the page, held to the width the
                        // Settings card had. Its prompts are still cards, over the page.
                        overlays.settingsPage != null -> Box(
                            Modifier.fillMaxSize().padding(bottom = contentPadding.calculateBottomPadding()),
                        ) {
                            CompositionLocalProvider(LocalDesktopPanelIsPage provides true) {
                            when (overlays.settingsPage!!) {
                                DesktopSettingsPage.MAIN -> DesktopSettingsScreen(
                                    query = settingsQuery,
                                    onQueryChange = { settingsQuery = it },
                                    listState = settingsListState,
                                    autoplay = autoplay,
                                    onAutoplayChange = ::setAutoplay,
                                    crossfadeOnSkip = crossfadeOnSkip,
                                    onCrossfadeOnSkipChange = {
                                        crossfadeOnSkip = it
                                        persistence.saveBoolean("automix_on_skip", it)
                                    },
                                    shuffle = shuffle,
                                    onShuffleChange = ::setShuffle,
                                    repeatMode = repeatMode,
                                    onRepeatModeChange = {
                                        repeatMode = it
                                        persistence.saveString("repeat_mode", it.name)
                                    },
                                    playbackSpeed = playbackSpeed,
                                    onPlaybackSpeedChange = {
                                        playbackSpeed = it
                                        persistence.saveString("playback_speed", it.toString())
                                    },
                                    crossfadeSeconds = crossfadeSeconds,
                                    onCrossfadeSecondsChange = {
                                        crossfadeSeconds = it
                                        persistence.saveString("crossfade_seconds", it.toString())
                                    },
                                    showNerdStats = showNerdStats,
                                    dontRepeatSuggestions = dontRepeatSuggestions,
                                    onDontRepeatSuggestionsChange = {
                                        dontRepeatSuggestions = it
                                        persistence.saveBoolean(KEY_DONT_REPEAT_SUGGESTIONS, it)
                                    },
                                    onChooseLocalMusicFolder = { onChosen ->
                                        DesktopLocalMusic.chooseFolder()?.let { chosen ->
                                            DesktopLocalMusic.setFolder(chosen)
                                            onChosen()
                                            localMusicRevision++
                                        }
                                    },
                                    onLocalMusicFolderChanged = { localMusicRevision++ },
                                    filterNonMusicAudio = filterNonMusicAudio,
                                    onFilterNonMusicAudioChange = {
                                        filterNonMusicAudio = it
                                        persistence.saveBoolean(DesktopLocalMusic.KEY_FILTER_NON_MUSIC_AUDIO, it)
                                        // The scan's result changes with it, so it has to be taken again.
                                        localMusicRevision++
                                    },
                                    syncedLyrics = syncedLyrics,
                                    onSyncedLyricsChange = {
                                        syncedLyrics = it
                                        persistence.saveBoolean(DesktopLyricsClient.KEY_SYNCED_LYRICS, it)
                                    },
                                    lyricsBlur = lyricsBlur,
                                    onLyricsBlurChange = {
                                        lyricsBlur = it
                                        persistence.saveBoolean(DesktopLyricsClient.KEY_LYRICS_BLUR, it)
                                    },
                                    enabledLyricsSources = DesktopLyricsClient.enabledSources(lyricsOrder, lyricsOn),
                                    onOpenLyricsSources = { overlays.settingsPage = DesktopSettingsPage.LYRICS_SOURCES },
                                    onOpenTranslationLanguage = { overlays.settingsPage = DesktopSettingsPage.TRANSLATION_LANGUAGE },
                                    onOpenEqualizer = { overlays.settingsPage = DesktopSettingsPage.EQUALIZER },
                                    onOpenAudioOutput = { overlays.settingsPage = DesktopSettingsPage.AUDIO_OUTPUT },
                                    onShowNerdStatsChange = {
                                        showNerdStats = it
                                        persistence.saveBoolean("show_nerd_stats", it)
                                    },
                                    spatialAudio = spatialAudio,
                                    onSpatialAudioChange = {
                                        spatialAudio = it
                                        persistence.saveBoolean("spatial_audio", it)
                                    },
                                    dolbyAtmos = dolbyAtmos,
                                    onDolbyAtmosChange = {
                                        dolbyAtmos = it
                                        DesktopAddonSettings.dolbyAtmosEnabled = it
                                        persistence.saveBoolean("dolby_atmos", it)
                                        DesktopAddonSource.clearCompletedTrackCalls()
                                    },
                                    skipSilence = skipSilence,
                                    onSkipSilenceChange = {
                                        skipSilence = it
                                        persistence.saveBoolean("skip_silence", it)
                                    },
                                    outputPrecision = outputPrecision,
                                    onOutputPrecisionChange = {
                                        outputPrecision = it
                                        persistence.saveString("output_precision", it)
                                    },
                                    outputSummary = playbackEngine.outputSummary(),
                                    trayIconEnabled = trayIconEnabled,
                                    closeToTray = closeToTray,
                                    onCloseToTrayChange = {
                                        closeToTray = it
                                        persistence.saveBoolean("close_to_tray", it)
                                    },
                                    onTrayIconChange = {
                                        trayIconEnabled = it
                                        persistence.saveBoolean("tray_icon", it)
                                    },
                                    downloadQuality = downloadQuality,
                                    onDownloadQualityChange = {
                                        downloadQuality = it
                                        persistence.saveString("download_quality", it)
                                    },
                                    audioQuality = audioQuality,
                                    onAudioQualityChange = {
                                        audioQuality = it
                                        persistence.saveAudioQuality(it)
                                    },
                                    sleepTimerMinutes = sleepTimerMinutes,
                                    sleepAfterTrack = sleepAfterTrack,
                                    sleepRemainingMs = sleepRemainingMs,
                                    onSleepTimerCycle = ::cycleSleepTimer,
                                    sourceConfigs = sourceConfigs,
                                    sourceStatus = sourceStatus,
                                    onSourceEnabledChange = { config, enabled ->
                                        val next = sourceConfigs.map {
                                            if (it.id == config.id && it.kind != DesktopSourceKind.YOUTUBE) {
                                                it.copy(enabled = enabled)
                                            } else {
                                                it
                                            }
                                        }
                                        persistence.saveSourceConfigs(next)
                                        sourceConfigs = persistence.sourceConfigs()
                                    },
                                    onSaveSource = { saved ->
                                        // Replaced by id, so any number of addons can be configured.
                                        val without = sourceConfigs.filterNot { it.id == saved.id }
                                        persistence.saveSourceConfigs((without + saved).inSourceOrder())
                                        sourceConfigs = persistence.sourceConfigs()
                                        // Whatever was held for this entry describes a server that may no
                                        // longer be the one selected.
                                        DesktopAddonSource.forget(saved.id)
                                    },
                                    onRemoveSource = { config ->
                                        if (config.isUserAdded) {
                                            persistence.saveSourceConfigs(sourceConfigs.filterNot { it.id == config.id })
                                            sourceConfigs = persistence.sourceConfigs()
                                            DesktopAddonSource.forget(config.id)
                                        }
                                    },
                                    onMoveSource = { config, delta ->
                                        val next = moveUserSource(sourceConfigs, config.id, delta)
                                        persistence.saveSourceConfigs(next)
                                        sourceConfigs = persistence.sourceConfigs()
                                    },
                                    onTestSource = { candidate ->
                                        sourceStatus = sourceStatus + (candidate.id to "Checking source…")
                                        scope.launch {
                                            val health = DesktopAddonSource.health(candidate)
                                            health.fold(
                                                onSuccess = { sourceStatus = sourceStatus + (candidate.id to it) },
                                                onFailure = {
                                                    sourceStatus = sourceStatus +
                                                        (candidate.id to (it.message ?: "Source unavailable"))
                                                },
                                            )
                                        }
                                    },
                                    onOpenLicenses = { overlays.settingsPage = DesktopSettingsPage.LICENSES },
                                )
                                DesktopSettingsPage.EQUALIZER -> DesktopEqualizerDialog(onDismiss = ::goBack)
                                DesktopSettingsPage.AUDIO_OUTPUT -> DesktopAudioOutputDialog(onDismiss = ::goBack)

                                DesktopSettingsPage.LYRICS_SOURCES -> DesktopLyricsSourcesDialog(
                                    order = lyricsOrder,
                                    enabled = lyricsOn,
                                    prioritizeSyllables = prioritizeSyllables,
                                    onReorder = {
                                        lyricsOrder = it
                                        persistence.saveLyricsSourceOrder(it)
                                    },
                                    onToggle = { name ->
                                        lyricsOn = if (name in lyricsOn) lyricsOn - name else lyricsOn + name
                                        persistence.saveLyricsEnabledSources(lyricsOn)
                                    },
                                    onPrioritizeSyllables = {
                                        prioritizeSyllables = it
                                        persistence.saveBoolean(DesktopLyricsClient.KEY_PRIORITIZE_SYLLABLES, it)
                                    },
                                    onReset = {
                                        lyricsOrder = DesktopLyricsClient.sources.map { it.name }
                                        lyricsOn = lyricsOrder.toSet()
                                        prioritizeSyllables = false
                                        persistence.saveLyricsSourceOrder(lyricsOrder)
                                        persistence.saveLyricsEnabledSources(lyricsOn)
                                        persistence.saveBoolean(DesktopLyricsClient.KEY_PRIORITIZE_SYLLABLES, false)
                                    },
                                    onDismiss = ::goBack,
                                )
                                DesktopSettingsPage.TRANSLATION_LANGUAGE -> DesktopTranslationLanguageDialog(onDismiss = ::goBack)
                                DesktopSettingsPage.LICENSES -> DesktopLicensesPage(onDismiss = ::goBack)
                            }
                            }
                        }
                        overlays.replay -> DesktopReplayPage(
                            summary = replaySummary,
                            period = replayPeriod,
                            holder = replayHolder,
                            onPeriodChange = { replayPeriod = it },
                            onPlaySong = { playSong(it) },
                            onOpenArtist = ::openArtistByName,
                            contentPadding = contentPadding,
                        )
                        openedArtist != null -> DesktopArtistPage(
                            state = artistState,
                            fallbackName = openedArtist!!.name,
                            onRetry = {
                                artistState = UiState.Loading
                                artistReloads++
                            },
                            onPlaySongs = { songs, index ->
                                playSongs(songs, index, openedArtist?.let { artistSource(it) })
                            },
                            onShuffle = { songs ->
                                playSongs(songs, 0, openedArtist?.let { artistSource(it) })
                            },
                            onOpenMenu = { song -> openMenu(song) },
                            onShelfItemClick = ::openShelfItem,
                            // A channel subscription is the account's, so a guest is never shown
                            // the button.
                            onToggleSubscription = if (youtubeSignedIn) ::toggleSubscription else null,
                            contentPadding = contentPadding,
                        )
                        openedCollection?.loading == true -> DesktopPageScaffold(contentPadding) {
                            Box(Modifier.fillMaxSize()) {
                                val error = collectionError
                                if (error != null) {
                                    DesktopErrorPage(error) { collectionReloads++ }
                                } else {
                                    val title = openedCollection?.title.orEmpty()
                                    DesktopLoadingPage(if (title.isBlank()) "Loading…" else "Loading $title…")
                                }
                            }
                        }
                        openedCollection != null -> DesktopCollectionPage(
                            collection = openedCollection!!,
                            loadingMore = collectionLoadingMore,
                            onLoadMore = ::loadMoreCollectionSongs,
                            onRename = { overlays.rename = true }.takeIf { openedCollection?.owned == true },
                            onShare = openedCollection
                                ?.takeIf { it.type == BrowseType.ALBUM || it.type == BrowseType.PLAYLIST }
                                ?.let { collection -> { shareCollection(collection) } },
                            onDelete = { overlays.delete = true }.takeIf { openedCollection?.owned == true },
                            onReorder = { overlays.reorder = true }.takeIf {
                                openedCollection?.owned == true && openedCollection?.type == BrowseType.PLAYLIST
                            },
                            onRemoveFromPlaylist = ::removeFromOpenPlaylist
                                .takeIf { openedCollection?.owned == true },
                            likedIds = likedIds,
                            onPlaySongs = { songs, index ->
                                playSongs(songs, index, openedCollection?.let { collectionSource(it) })
                            },
                            onShuffle = { songs ->
                                playSongs(songs.shuffled(), 0, openedCollection?.let { collectionSource(it) })
                            },
                            onDownloadAll = ::downloadAll,
                            onToggleLike = { song ->
                                toggleLike(song)
                            },
                            onAddToPlaylist = { playlistTarget = it },
                            onDownload = ::downloadSong,
                            downloadedIds = downloads.map(Song::videoId).toSet(),
                            downloadInProgress = downloadInProgress,
                            contentPadding = contentPadding,
                        )
                        destination == DesktopDestination.LISTEN_NOW -> HomeScreen(
                            state = homeState,
                            listState = homeListState,
                            onItemClick = { item, shelfTitle -> openShelfItem(item, shelfTitle) },
                            onRetry = ::loadHome,
                            refreshing = false,
                            onRefresh = ::loadHome,
                            pullState = rememberPullToRefreshState(),
                            contentPadding = sharedPagePadding,
                            title = null,
                            signedIn = youtubeSignedIn,
                            onSignIn = { overlays.accounts = true },
                            onItemLongPress = { item -> if (item.videoId != null) openMenu(item.toSong()) },
                            onLoadMore = ::loadMoreHome,
                            loadingMore = homeLoadingMore,
                            recentlyPlayedLoading = homeRecentsLoading,
                            leadHero = false,
                            leadingContent = {
                                val shelves = (homeState as? UiState.Success)?.data.orEmpty()
                                val recentsTitle = DesktopStrings["shelf_recents", "Recents"]
                                val picks = shelves
                                    .firstOrNull { shelf ->
                                        shelf.title.equals("Recents", ignoreCase = true) ||
                                            shelf.title.equals("Recently played", ignoreCase = true) ||
                                            shelf.title.equals(recentsTitle, ignoreCase = true)
                                    }
                                    ?.items
                                    .orEmpty()
                                    .filter { it.videoId != null }
                                    .take(8)
                                DesktopQuickPicks(
                                    items = picks,
                                    currentSong = playback.song,
                                    isPlaying = playback.isPlaying,
                                    onClick = { item -> openShelfItem(item, "Quick picks") },
                                    onOpenMenu = { item -> if (item.videoId != null) openMenu(item.toSong()) },
                                )
                            },
                        )
                        destination == DesktopDestination.EXPLORE && selectedMoodGenre != null -> Column(Modifier.fillMaxSize()) {
                            MoodGenrePlaylistsScreen(
                                title = selectedMoodGenre!!.title,
                                state = moodGenreShelves,
                                listState = moodGenreListState,
                                onItemClick = { item -> openShelfItem(item, selectedMoodGenre?.title) },
                                onRetry = {
                                    moodGenreShelves = UiState.Loading
                                    moodGenreReloads++
                                },
                                contentPadding = PaddingValues(bottom = sharedPagePadding.calculateBottomPadding()),
                                modifier = Modifier.weight(1f),
                            )
                        }
                        destination == DesktopDestination.EXPLORE -> ExploreScreen(
                            state = exploreState,
                            listState = exploreListState,
                            onCategoryClick = ::openMoodGenre,
                            onRetry = { exploreReloads++ },
                            refreshing = false,
                            onRefresh = { exploreReloads++ },
                            pullState = rememberPullToRefreshState(),
                            contentPadding = sharedPagePadding,
                            showTitle = false,
                        )
                        destination == DesktopDestination.SEARCH -> SearchScreen(
                            query = query,
                            onQueryChange = ::editQuery,
                            filter = searchFilter,
                            // The playing row's indicator, as the phone passes it: the engine's
                            // live state, not the selection a track change publishes behind it.
                            currentSong = playback.song,
                            isPlaying = playback.isPlaying,
                            onFilterChange = {
                                searchFilter = it
                                if (query.isNotBlank()) search()
                            },
                            results = when {
                                searchLoading -> UiState.Loading
                                searchError != null -> UiState.Error(searchError!!)
                                searchCommitted -> UiState.Success(searchRows)
                                else -> null
                            },
                            loadingMore = false,
                            onLoadMore = {},
                            listState = searchListState,
                            scrollResetTrigger = searchScrollReset,
                            focusRequested = searchFocusRequested,
                            onFocusHandled = { searchFocusRequested = false },
                            onSongClick = { songs, index ->
                                songs.getOrNull(index)?.let { song ->
                                    recordSongSearch(song)
                                    playSong(song, source = DesktopQueueSource(DesktopStrings["search", "Search"], PlaybackSourceType.SEARCH))
                                }
                            },
                            onSongLongPress = ::openMenu,
                            onSongSwipe = { song ->
                                if (DesktopAppUiHost.swipeToPlayNext.value) playNext(song) else addToQueue(song)
                            },
                            onTopResultPlay = { song ->
                                recordSongSearch(song)
                                playSong(song, source = DesktopQueueSource(DesktopStrings["search", "Search"], PlaybackSourceType.SEARCH))
                            },
                            onTopResultPlaylist = { song ->
                                recordSongSearch(song)
                                playlistTarget = song
                            },
                            onBrowseClick = { item ->
                                recordBrowseSearch(item)
                                openBrowseItem(item)
                            },
                            history = searchHistory,
                            // The typed text leads the list, put there by the keystroke rather than
                            // taken from the response — the page drops it, as the phone's does.
                            suggestions = if (searchTyping && query.isNotBlank()) {
                                listOf(query) +
                                    searchSuggestions.filterNot { it.equals(query, ignoreCase = true) }
                            } else {
                                emptyList()
                            },
                            typeaheadResults = if (searchTyping && query.isNotBlank()) searchTypeahead else emptyList(),
                            onSubmit = ::search,
                            onSuggestionClick = ::runSearch,
                            onHistoryClick = { entity ->
                                // Going back to a recent does not record it again, as on the phone.
                                when (entity.entityType) {
                                    EntityType.TRACK -> playSong(
                                        Song(
                                            videoId = entity.id,
                                            title = entity.title,
                                            artist = entity.subtitle,
                                            thumbnailUrl = entity.artworkUrl,
                                        ),
                                        source = DesktopQueueSource(entity.title, PlaybackSourceType.SEARCH),
                                    )
                                    EntityType.ARTIST -> openArtist(entity.id, entity.title)
                                    EntityType.ALBUM, EntityType.PLAYLIST -> openBrowseItem(
                                        BrowseItem(
                                            browseId = entity.id,
                                            title = entity.title,
                                            subtitle = entity.subtitle,
                                            thumbnailUrl = entity.artworkUrl,
                                            type = if (entity.entityType == EntityType.ALBUM) BrowseType.ALBUM else BrowseType.PLAYLIST,
                                        ),
                                    )
                                }
                            },
                            onHistoryRemove = DesktopSearchHistory::remove,
                            onHistoryClear = DesktopSearchHistory::clear,
                            onTypeaheadLongPress = ::openMenu,
                            contentPadding = sharedPagePadding,
                            topPadding = 0.dp,
                            showField = false,
                        )
                        destination == DesktopDestination.LIBRARY && libraryShowAll != null -> Column(Modifier.fillMaxSize()) {
                            // The phone keeps the grid's sort beside it, in the bar above; here it sits
                            // at the end of the row the way back is on.
                            Row(
                                Modifier.fillMaxWidth().padding(top = 8.dp, end = 12.dp),
                                horizontalArrangement = Arrangement.End,
                            ) { DesktopLibrarySortMenu() }
                            LibraryGridPage(
                                shelf = libraryShowAll!!,
                                gridState = libraryGridState,
                                onItemClick = ::openLibraryItem,
                                onItemLongPress = { item -> if (item.videoId != null) openMenu(item.toSong()) },
                                contentPadding = PaddingValues(bottom = sharedPagePadding.calculateBottomPadding()),
                                modifier = Modifier.weight(1f),
                                onNewPlaylist = { overlays.playlistDialog = true }
                                    .takeIf { libraryShowAll?.title == PLAYLISTS_SHELF },
                            )
                        }
                        destination == DesktopDestination.LIBRARY -> LibraryScreen(
                            signedIn = youtubeSignedIn,
                            state = libraryState,
                            listState = libraryListState,
                            onShelfItemClick = ::openLibraryItem,
                            onShelfItemLongPress = { item -> if (item.videoId != null) openMenu(item.toSong()) },
                            onNewPlaylist = { overlays.playlistDialog = true },
                            onShowAll = { shelf -> libraryShowAll = shelf },
                            replay = {
                                // The phone's wallet of Replay cards, or nothing until there is
                                // listening to deal them from.
                                val cards = remember(replaySummary) { replaySummary.heroCards() }
                                if (!replaySummary.isEmpty && cards.isNotEmpty()) {
                                    ShelfRow(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        contentPadding = PaddingValues(horizontal = PAGE_GUTTER),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        items(cards, key = { it.label }) { card ->
                                            ReplayCreditCard(
                                                label = card.label,
                                                value = card.value,
                                                detail = card.detail,
                                                artworkUrl = card.artworkUrl,
                                                holder = replayHolder,
                                                memberSince = replaySummary.memberSince(),
                                                onClick = { overlays.replay = true },
                                                modifier = Modifier.width(300.dp),
                                            )
                                        }
                                    }
                                }
                            },
                            onSignIn = { overlays.accounts = true },
                            onRetry = ::reloadLibrary,
                            refreshing = false,
                            onRefresh = ::reloadLibrary,
                            pullState = rememberPullToRefreshState(),
                            contentPadding = sharedPagePadding,
                            links = libraryLinks,
                            deviceItems = libraryDeviceItems,
                            showTitle = false,
                        )
                        destination == DesktopDestination.HISTORY -> DesktopHistoryPage(
                            // The account's history when there is one, and what
                            // this computer played when there is not.
                            history = remoteHistory.ifEmpty { history },
                            onSongClick = { playSong(it, source = DesktopQueueSource(DesktopStrings["history", "History"], PlaybackSourceType.HISTORY)) },
                            onDownload = ::downloadSong,
                            onAddToPlaylist = { playlistTarget = it },
                            downloadedIds = downloads.map(Song::videoId).toSet(),
                            downloadInProgress = downloadInProgress,
                            contentPadding = contentPadding,
                            menu = { song -> songMenu(song) },
                        )
                        destination == DesktopDestination.DOWNLOADS -> Box(Modifier.fillMaxSize()) {
                            DesktopDownloadsPage(
                                downloads = downloads,
                                onSongClick = { playSong(it, source = DesktopQueueSource(DesktopStrings["downloads", "Downloads"], PlaybackSourceType.BROWSE)) },
                                contentPadding = contentPadding,
                                menu = { song -> songMenu(song) },
                            )
                            // Only while there is a queue to look at.
                            if (downloadInProgress.isNotEmpty()) {
                                DesktopActionButton(
                                    "Queue · ${downloadInProgress.size}",
                                    BitChordIcons.Download,
                                    onClick = { overlays.downloadManager = true },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 28.dp, bottom = 28.dp + contentPadding.calculateBottomPadding()),
                                )
                            }
                        }
                        destination == DesktopDestination.LOCAL_MUSIC -> DesktopLocalMusicPage(
                            songs = localSongs,
                            onSongClick = { playSong(it, source = DesktopQueueSource(DesktopStrings["local_music", "Local Music"], PlaybackSourceType.BROWSE)) },
                            contentPadding = contentPadding,
                            menu = { song -> songMenu(song) },
                        )
                    }
                }
                }
                }

            }
        }
        }
    }

}


/** What the mesh is averaged from — coarse by design. */
/** Whether AutoPlay may offer a song this session has already played or suggested. */
internal const val KEY_DONT_REPEAT_SUGGESTIONS = "dont_repeat_suggestions"

internal const val KEY_LIBRARY_SORT = "library_sort"

private const val MESH_SOURCE_PX = 120



/** Enough to scroll through, short of turning the shelf into the history page. */
/** The Library's "On device" cards — the phone's ids for the two folders, so their cards draw alike. */
private const val LOCAL_DOWNLOADS_ID = "local:downloads"
private const val LOCAL_MUSIC_ID = "local:all"
private const val LOCAL_PLAYLIST_PREFIX = "desktop-playlist:"
