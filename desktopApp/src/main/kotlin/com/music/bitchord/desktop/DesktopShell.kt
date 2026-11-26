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
internal fun DesktopTopBar(
    compact: Boolean,
    title: String,
    onOpenLyrics: () -> Unit,
    onOpenQueue: () -> Unit,
    /** Which of the two the column beside the page is showing, lit on its button. */
    sidePanel: DesktopSidePanel?,
    accountAvatar: String?,
    onOpenAccounts: () -> Unit,
    /** The app's one way back — every page's own arrow was folded into this. */
    canGoBack: Boolean,
    onBack: () -> Unit,
) {
    val titleBarEnabled by DesktopTitleBarSetting.enabled.collectAsState()
    val inlineCaption = DesktopPlatform.drawsOwnWindowFrame && !titleBarEnabled

    DesktopTitleBarDragArea(Modifier.fillMaxWidth().height(56.dp)) {
        Box(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxSize()
                    .desktopWindowGlass(DesktopChromeEdge.BOTTOM)
                    .padding(start = 14.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (compact) {
                    Image(
                        painter = painterResource(Res.drawable.logo_mark),
                        contentDescription = "Lirika",
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                }
                DesktopToolbarButton(onClick = onBack, enabled = canGoBack, size = 32.dp) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        DesktopStrings["back", "Back"],
                        tint = if (canGoBack) Color.White else DesktopSecondary.copy(alpha = 0.40f),
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DesktopIslandToolbarButton()
                    DesktopToolbarButton(onClick = onOpenLyrics, size = 32.dp) {
                        Icon(
                            BitChordIcons.LyricsQuote,
                            DesktopStrings["lyrics", "Lyrics"],
                            tint = if (sidePanel == DesktopSidePanel.LYRICS) Color.White else DesktopSecondary,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                    DesktopToolbarButton(onClick = onOpenQueue, size = 32.dp) {
                        Icon(
                            BitChordIcons.Queue,
                            DesktopStrings["queue", "Queue"],
                            tint = if (sidePanel == DesktopSidePanel.QUEUE) Color.White else DesktopSecondary,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                    DesktopAccountButton(avatar = accountAvatar, onClick = onOpenAccounts)
                    if (DesktopPlatform.isWindows && inlineCaption) {
                        Spacer(Modifier.width(8.dp))
                        WindowsCaptionButtons(Modifier.fillMaxHeight())
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                thickness = 1.dp,
                color = desktopChromeDivider(),
            )
        }
    }
}

@Composable
internal fun DesktopToolbarButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: Dp = 36.dp,
    content: @Composable () -> Unit,
) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .desktopHoverWash()
            .clickable(
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
internal fun DesktopSidebar(
    destination: DesktopDestination,
    settingsOpen: Boolean,
    accountPlaylists: List<ShelfItem>,
    localPlaylists: List<DesktopPlaylist>,
    openedCollectionId: String?,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onDestinationSelected: (DesktopDestination) -> Unit,
    onOpenAccountPlaylist: (ShelfItem) -> Unit,
    onOpenLocalPlaylist: (DesktopPlaylist) -> Unit,
    onOpenSettings: () -> Unit,
    /** Search was picked: the sidebar's box is the page's field, so it takes the focus. */
    focusSearch: Boolean,
    onSearchFocused: () -> Unit,
    /** Whether YouTube Music has an account, and the way in when it doesn't. */
    signedIn: Boolean,
    onSignIn: () -> Unit,
) {
    val searchFocusRequester = remember { FocusRequester() }
    LaunchedEffect(focusSearch) {
        if (focusSearch) {
            runCatching { searchFocusRequester.requestFocus() }
            onSearchFocused()
        }
    }
    val windowInfo = LocalWindowInfo.current
    var searchFocused by remember { mutableStateOf(false) }
    var restoreSearchFocus by remember { mutableStateOf(false) }

    LaunchedEffect(windowInfo.isWindowFocused) {
        if (!windowInfo.isWindowFocused) {
            restoreSearchFocus = searchFocused
        } else if (restoreSearchFocus) {
            // Let the desktop window finish its activation before asking Compose to put the native
            // text input target back in focus.
            yield()
            searchFocusRequester.requestFocus()
            restoreSearchFocus = false
        }
    }

    val titleBarEnabled by DesktopTitleBarSetting.enabled.collectAsState()
    val inlineCaption = DesktopPlatform.drawsOwnWindowFrame && !titleBarEnabled
    // Rows the listener has tucked away; keyed the same way the sidebar lists them.
    val hiddenSidebar by DesktopSidebarVisibility.hidden.collectAsState()
    Box(
        Modifier
            .width(236.dp)
            .fillMaxHeight()
            .desktopWindowGlass(DesktopChromeEdge.END, fade = 0.07f),
    ) {
        Column(Modifier.fillMaxSize()) {

        Column(
            Modifier.fillMaxSize().padding(start = 14.dp, end = 14.dp, top = if (inlineCaption) 10.dp else 18.dp, bottom = 16.dp),
        ) {
            // The mark names the window now: the caption is only the app's name and a drag surface,
            // so the identity lives where the eye starts.
            Row(
                Modifier.height(30.dp).padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_mark),
                    contentDescription = "Lirika",
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Lirika",
                    color = BcColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                )
            }
            Spacer(Modifier.height(16.dp))
            DesktopSearchField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                focusRequester = searchFocusRequester,
                onFocusChanged = { searchFocused = it },
                height = 40.dp,
                shortcutHint = "Ctrl K",
            )
            Spacer(Modifier.height(18.dp))
            DesktopSidebarItem(BitChordIcons.Home, DesktopStrings["home", "Home"], destination == DesktopDestination.LISTEN_NOW) {
                onDestinationSelected(DesktopDestination.LISTEN_NOW)
            }
            DesktopSidebarItem(BitChordIcons.Explore, "Explore", destination == DesktopDestination.EXPLORE) {
                onDestinationSelected(DesktopDestination.EXPLORE)
            }
            DesktopSidebarItem(BitChordIcons.Library, "Library", destination == DesktopDestination.LIBRARY) {
                onDestinationSelected(DesktopDestination.LIBRARY)
            }
            DesktopSidebarItem(BitChordIcons.Search, "Search", destination == DesktopDestination.SEARCH) {
                onDestinationSelected(DesktopDestination.SEARCH)
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = desktopChromeDivider())
            Spacer(Modifier.height(14.dp))
            if (HISTORY_KEY !in hiddenSidebar) {
                DesktopSidebarItem(
                    BitChordIcons.Clock,
                    "History",
                    destination == DesktopDestination.HISTORY,
                    onClick = { onDestinationSelected(DesktopDestination.HISTORY) },
                    onHide = { DesktopSidebarVisibility.hide(HISTORY_KEY, "History") },
                )
            }
            val queued by DesktopDownloadQueue.active.collectAsState()
            if (DOWNLOADS_KEY !in hiddenSidebar) {
                DesktopSidebarItem(
                    BitChordIcons.Download,
                    if (queued.isEmpty()) "Downloads" else "Downloads · ${queued.size}",
                    destination == DesktopDestination.DOWNLOADS,
                    onClick = { onDestinationSelected(DesktopDestination.DOWNLOADS) },
                    onHide = { DesktopSidebarVisibility.hide(DOWNLOADS_KEY, "Downloads") },
                )
            }
            if (LOCAL_MUSIC_KEY !in hiddenSidebar) {
                DesktopSidebarItem(
                    BitChordIcons.Library,
                    "Local Music",
                    destination == DesktopDestination.LOCAL_MUSIC,
                    onClick = { onDestinationSelected(DesktopDestination.LOCAL_MUSIC) },
                    onHide = { DesktopSidebarVisibility.hide(LOCAL_MUSIC_KEY, "Local Music") },
                )
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = desktopChromeDivider())
            Spacer(Modifier.height(12.dp))
            Text(
                "Your playlists",
                modifier = Modifier.padding(start = 9.dp, bottom = 6.dp),
                color = BcColors.TextFaint,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
            val visibleAccount = accountPlaylists.filterNot { "account:${it.browseId}" in hiddenSidebar }
            val visibleLocal = localPlaylists.filterNot { "local:${it.id}" in hiddenSidebar }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                items(visibleAccount, key = { "account:${it.browseId}" }) { playlist ->
                    DesktopSidebarItem(
                        Icons.AutoMirrored.Rounded.PlaylistPlay,
                        playlist.title,
                        openedCollectionId == playlist.browseId,
                        onClick = { onOpenAccountPlaylist(playlist) },
                        onHide = {
                            DesktopSidebarVisibility.hide("account:${playlist.browseId}", playlist.title)
                        },
                    )
                }
                items(visibleLocal, key = { "local:${it.id}" }) { playlist ->
                    DesktopSidebarItem(
                        Icons.AutoMirrored.Rounded.PlaylistPlay,
                        playlist.title,
                        openedCollectionId == playlist.id,
                        onClick = { onOpenLocalPlaylist(playlist) },
                        onHide = { DesktopSidebarVisibility.hide("local:${playlist.id}", playlist.title) },
                    )
                }
                if (visibleAccount.isEmpty() && visibleLocal.isEmpty()) {
                    item {
                        Text(
                            "Your playlists will appear here",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
                            color = DesktopSecondary.copy(alpha = 0.68f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            HorizontalDivider(color = desktopChromeDivider())
            Spacer(Modifier.height(8.dp))
            if (hiddenSidebar.isNotEmpty()) {
                DesktopSidebarRestoreRow(hiddenSidebar)
            }
            // The account lives here as well as in the top bar, the way it does
            // in YouTube Music: a guest can see whose library this would be.
            if (!signedIn) {
                DesktopSidebarItem(Icons.Rounded.Person, "Sign in to YouTube Music", false, onClick = onSignIn)
            }
            DesktopSidebarItem(Icons.Rounded.Settings, "Settings", settingsOpen) {
                onOpenSettings()
            }
        }
        }
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .width(1.dp)
                .fillMaxHeight()
                .background(desktopChromeDivider()),
        )
    }
}

@Composable
internal fun DesktopSidebarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    /** When set, a right-click offers to hide the row — see [DesktopSidebarVisibility]. */
    onHide: (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .height(36.dp)
                .desktopHoverFill(corner = BcShapes.artwork)
                .clip(RoundedCornerShape(BcShapes.artwork))
                .background(if (selected) Color.White.copy(alpha = 0.07f) else Color.Transparent)
                .drawBehind {
                    if (!selected) return@drawBehind
                    val lineHeight = size.height * 0.44f
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(0f, (size.height - lineHeight) / 2f),
                        size = androidx.compose.ui.geometry.Size(3.dp.toPx(), lineHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()),
                    )
                }
                .clickable(onClick = onClick)
                .then(
                    if (onHide == null) Modifier
                    else Modifier.pointerInput(onHide) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                                    menuOpen = true
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        }
                    },
                )
                .padding(horizontal = 9.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, label, tint = if (selected) BcColors.TextPrimary else BcColors.TextMuted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                label,
                color = if (selected) BcColors.TextPrimary else BcColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (onHide != null) {
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(DesktopStrings["hide_from_sidebar", "Hide from sidebar"]) },
                    onClick = {
                        menuOpen = false
                        onHide()
                    },
                )
            }
        }
    }
}

/** The secondary destinations that can be tucked away, by the key they hide under. */
internal const val HISTORY_KEY = "dest:HISTORY"
internal const val DOWNLOADS_KEY = "dest:DOWNLOADS"
internal const val LOCAL_MUSIC_KEY = "dest:LOCAL_MUSIC"

/**
 * The "N hidden" row that brings tucked-away rows back: a menu of everything
 * hidden, each restoring on its own, plus a "Show all". Only drawn while
 * something is hidden, so an ordinary sidebar never shows it.
 */
@Composable
internal fun DesktopSidebarRestoreRow(hidden: Map<String, String>) {
    var open by remember { mutableStateOf(false) }
    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .desktopHoverFill()
                .clip(RoundedCornerShape(7.dp))
                .clickable { open = true }
                .padding(horizontal = 9.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.RemoveCircleOutline,
                null,
                tint = DesktopSecondary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                DesktopStrings.format("n_hidden", hidden.size, fallback = "%1\$d hidden"),
                color = DesktopSecondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            hidden.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    onClick = {
                        DesktopSidebarVisibility.show(key)
                        if (DesktopSidebarVisibility.hidden.value.isEmpty()) open = false
                    },
                )
            }
            if (hidden.size > 1) {
                DropdownMenuItem(
                    text = { Text(DesktopStrings["show_all", "Show all"]) },
                    onClick = {
                        DesktopSidebarVisibility.showAll()
                        open = false
                    },
                )
            }
        }
    }
}

@Composable
internal fun DesktopBottomChrome(
    compact: Boolean,
    destination: DesktopDestination,
    onDestinationSelected: (DesktopDestination) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DesktopFloatingNavigation(destination, onDestinationSelected)
    }
}

@Composable
internal fun DesktopFloatingNavigation(
    destination: DesktopDestination,
    onDestinationSelected: (DesktopDestination) -> Unit,
) {
    val tabs = listOf(
        DesktopDestination.LISTEN_NOW to (BitChordIcons.Play to "Play"),
        DesktopDestination.EXPLORE to (BitChordIcons.Explore to "Explore"),
        DesktopDestination.LIBRARY to (BitChordIcons.Library to "Library"),
        DesktopDestination.SEARCH to (BitChordIcons.Search to "Search"),
    )
    val selected = tabs.firstOrNull { it.first == destination }?.first ?: DesktopDestination.LIBRARY
    val shape = RoundedCornerShape(percent = 50)
    Surface(
        modifier = Modifier
            .widthIn(max = 440.dp)
            .fillMaxWidth()
            .clip(shape)
            .desktopBarGlass(shape),
        shape = shape,
        color = Color.Transparent,
        tonalElevation = 0.dp,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            tabs.forEach { (item, iconAndLabel) ->
                val isSelected = item == selected
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(if (isSelected) DesktopAccent.copy(alpha = 0.18f) else Color.Transparent)
                        .clickable { onDestinationSelected(item) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(iconAndLabel.first, iconAndLabel.second, tint = if (isSelected) DesktopAccent else DesktopSecondary, modifier = Modifier.size(20.dp))
                    Text(iconAndLabel.second, color = if (isSelected) Color.White else DesktopSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
internal fun DesktopSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onFocusChanged: (Boolean) -> Unit = {},
    placeholder: String = "Search",
    height: Dp = 36.dp,
    /** The keyboard shortcut that lands in this box, shown as a keycap while it is empty. */
    shortcutHint: String? = null,
) {
    val searchShape = RoundedCornerShape(BcShapes.artwork)
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(searchShape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), searchShape)
            .clickable { focusRequester.requestFocus() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                        onSearch()
                        true
                    } else {
                        false
                    }
                },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            cursorBrush = SolidColor(DesktopAccent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        BitChordIcons.Search,
                        contentDescription = null,
                        tint = DesktopSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isBlank()) {
                            Text(placeholder, color = DesktopSecondary, maxLines = 1)
                        }
                        innerTextField()
                    }
                    if (shortcutHint != null && query.isBlank()) {
                        val keyShape = RoundedCornerShape(BcShapes.chip)
                        Text(
                            shortcutHint,
                            color = BcColors.TextFaint,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(keyShape)
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), keyShape)
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                        )
                    }
                }
            },
        )
    }
}

@Composable
internal fun DesktopFrame(
    containerColor: Color,
    modifier: Modifier = Modifier,
    /**
     * The open page's own colours, painted across the whole window rather than only the content
     * area, so the title bar, top bar and sidebar take their tint from the page they are framing
     * instead of staying a flat slab beside it.
     */
    /** Receives true when its solid base should be left clear for the system material. */
    backdrop: @Composable (transparentBase: Boolean) -> Unit,
    topBar: @Composable (Boolean) -> Unit,
    sidebar: @Composable () -> Unit,
    bottomBar: @Composable (Boolean) -> Unit,
    /** The player that floats over the page, inside its bottom edge. */
    dock: @Composable (Boolean) -> Unit = {},
    /** The column on the far side of the page — the lyrics or the queue, when one is open. */
    trailing: @Composable () -> Unit = {},
    overlay: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    // Held out here rather than inside the constraints box.
    val haze = remember { HazeState() }
    // With a system material active, the window stays clear under its chrome. The app-background
    // preference decides whether the page and side column also stay clear or retain their solid
    // gray ground.
    val material by DesktopWindowBackdrop.active.collectAsState()
    val appBackground by DesktopWindowBackdrop.appBackground.collectAsState()
    val glass = material != DesktopBackdrop.OFF
    val materialBehindApp = glass && appBackground

    CompositionLocalProvider(LocalDesktopHaze provides haze) {
        Box(
            modifier
                .fillMaxSize()
                .then(if (glass) Modifier else Modifier.background(containerColor)),
        ) {
            // Both sources of the same state: the chrome blurs the backdrop behind it, and the
            // floating bottom bar blurs the page scrolling under it.
            if (!glass) Box(Modifier.fillMaxSize().hazeSource(haze)) { backdrop(false) }
            Column(Modifier.fillMaxSize()) {
                // Above everything, and outside the box the rest of the window is drawn in, because
                // that is what a title bar is.
                DesktopTitleBar()
                Box(Modifier.fillMaxWidth().weight(1f)) {
                    BoxWithConstraints(Modifier.fillMaxSize()) {
                        val compact = maxWidth < 980.dp
                        // The sidebar runs the full height of the window, its head the window's
                        // caption; the player's bar and the page share the column beside it.
                        Row(Modifier.fillMaxSize()) {
                            if (!compact) sidebar()
                            Column(Modifier.weight(1f).fillMaxHeight()) {
                            topBar(compact)
                            Row(Modifier.fillMaxWidth().weight(1f)) {
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .then(
                                            if (glass && !materialBehindApp) {
                                                Modifier.background(containerColor)
                                            } else {
                                                Modifier
                                            },
                                        ),
                                ) {
                                    Box(Modifier.fillMaxSize().hazeSource(haze)) {
                                        if (glass) backdrop(materialBehindApp)
                                        content(
                                            PaddingValues(
                                                start = 0.dp,
                                                end = 0.dp,
                                                // The dock is gone; the island is the player. Only
                                                // the compact navigation pill needs clearance now.
                                                bottom = if (compact) 88.dp else 0.dp,
                                            ),
                                        )
                                    }
                                    if (compact) {
                                        Box(Modifier.align(Alignment.BottomCenter)) { bottomBar(true) }
                                    }
                                    Box(
                                        Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = if (compact) 84.dp else 14.dp),
                                    ) { dock(compact) }
                                }
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .then(
                                            if (glass && !materialBehindApp) {
                                                Modifier.background(containerColor)
                                            } else {
                                                Modifier
                                            },
                                        ),
                                ) { trailing() }
                            }
                            }
                        }
                    }
                    overlay()
                }
            }
        }
    }
}

/**
 * What [DesktopFrame] paints behind the window.
 *
 * [artworkUrl] is the page's lead artwork when it has one, and the mesh built from it is laid under
 * ink so a page of text stays readable over it.
 */
@Composable
internal fun DesktopPageBackdrop(artworkUrl: String?, transparentBase: Boolean) {
    Box(
        Modifier
            .fillMaxSize()
            .then(if (transparentBase) Modifier else Modifier.background(DesktopBackground)),
    ) {
        if (artworkUrl == null) return@Box
        DesktopMesh(artworkUrl)
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.30f),
                        Color.Black.copy(alpha = 0.72f),
                        Color.Black.copy(alpha = 0.88f),
                    ),
                ),
            ),
        )
    }
}
