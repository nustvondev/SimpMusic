package com.maxrave.simpmusic.ui.component

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kyant.backdrop.highlight.Highlight
import com.maxrave.domain.manager.DataStoreManager
import com.maxrave.logger.Logger
import com.maxrave.simpmusic.Platform
import com.maxrave.simpmusic.expect.isFullscreenPlayerOpen
import com.maxrave.simpmusic.expect.setMiniPlayerOpen
import com.maxrave.simpmusic.expect.toggleFullscreenPlayer
import com.maxrave.simpmusic.expect.toggleMiniPlayer
import com.maxrave.simpmusic.expect.ui.PlatformBackdrop
import com.maxrave.simpmusic.expect.ui.isLyricsBlurSupported
import com.maxrave.simpmusic.expect.ui.layerBackdrop
import com.maxrave.simpmusic.expect.ui.rememberBackdrop
import com.maxrave.simpmusic.expect.ui.toImageBitmap
import com.maxrave.simpmusic.extension.KeepScreenOn
import com.maxrave.simpmusic.extension.LeftToRight
import com.maxrave.simpmusic.extension.formatDuration
import com.maxrave.simpmusic.extension.lengthLabel
import com.maxrave.simpmusic.extension.getScreenSizeInfo
import com.maxrave.simpmusic.extension.hsvToColor
import com.maxrave.simpmusic.getPlatform
import com.maxrave.simpmusic.ui.component.lyrics.ShareLyricsSheet
import com.maxrave.simpmusic.ui.component.lyrics.toShareLyricsLines
import com.maxrave.simpmusic.ui.icon.Close
import com.maxrave.simpmusic.ui.icon.Fullscreen
import com.maxrave.simpmusic.ui.icon.FullscreenExit
import com.maxrave.simpmusic.ui.icon.Info
import com.maxrave.simpmusic.ui.icon.MoreVert
import com.maxrave.simpmusic.ui.icon.PictureInPictureAlt
import com.maxrave.simpmusic.ui.icon.QueueMusic
import com.maxrave.simpmusic.ui.icon.Share
import com.maxrave.simpmusic.ui.icon.AvTimer
import com.maxrave.simpmusic.ui.icon.SimpIcons
import com.maxrave.simpmusic.ui.icon.VolumeOff
import com.maxrave.simpmusic.ui.icon.VolumeUp
import com.maxrave.simpmusic.ui.navigation.destination.list.ArtistDestination
import com.maxrave.simpmusic.ui.screen.player.content.AppleMusicArtworkBackdrop
import com.maxrave.simpmusic.ui.screen.player.content.AppleMusicMainTitleRow
import com.maxrave.simpmusic.ui.screen.player.content.ExpressivePlaybackControls
import com.maxrave.simpmusic.ui.screen.player.content.ExpressiveTrackInfoRow
import com.maxrave.simpmusic.ui.screen.player.content.NowPlayingContentActions
import com.maxrave.simpmusic.ui.screen.player.content.NowPlayingContentState
import com.maxrave.simpmusic.ui.screen.player.content.NowPlayingExpressiveTheme
import com.maxrave.simpmusic.ui.screen.player.content.NowPlayingTrackInfoRow
import com.maxrave.simpmusic.ui.screen.player.content.SpotifyPlaybackControls
import com.maxrave.simpmusic.ui.screen.player.content.applemusic.AppleMusicPlaybackControls
import com.maxrave.simpmusic.ui.screen.player.content.applemusic.rememberAppleMusicTypography
import com.maxrave.simpmusic.ui.theme.LocalLiquidGlassEnabled
import com.maxrave.simpmusic.ui.theme.seed
import com.maxrave.simpmusic.ui.theme.typo
import com.maxrave.simpmusic.viewModel.NowPlayingScreenData
import com.maxrave.simpmusic.viewModel.SharedViewModel
import com.maxrave.simpmusic.viewModel.UIEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import simpmusic.composeapp.generated.resources.Res
import simpmusic.composeapp.generated.resources.crossfading
import simpmusic.composeapp.generated.resources.share_lyrics
import simpmusic.composeapp.generated.resources.unavailable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.TimeSource
import org.koin.compose.koinInject

// The gutter this sheet has always used for its lyrics column.
private val FULLSCREEN_LYRICS_GUTTER = 50.dp

// Landscape insets. Desktop's top leaves room for the chrome row drawn over the layout (a 48dp
// pill inside 12dp of padding); Android's sits below the status bar instead.
private val LANDSCAPE_DESKTOP_TOP_PADDING = 72.dp
private val LANDSCAPE_ANDROID_TOP_PADDING = 24.dp
private val LANDSCAPE_BOTTOM_PADDING = 32.dp

// Apple Music's desktop full-screen player caps its artwork well short of the column it sits in.
private val LANDSCAPE_ARTWORK_MAX_SIZE = 420.dp

private val CHROME_VOLUME_SLIDER_WIDTH = 120.dp

// Track-change transition tunable constants
internal const val ARTWORK_TRANSITION_ENABLED = true
internal const val HANDOFF_PARTICLES_ENABLED = true
internal const val COVER_TRANSITION_MIN_SCALE = 0.50f
internal const val COVER_TRANSITION_MAX_SCALE = 1.00f
internal const val COVER_TRANSITION_ALPHA_FADE_IN_END = 0.30f
internal const val COVER_TRANSITION_ALPHA_FADE_OUT_START = 0.70f
internal const val TRACK_TRANSITION_ROTATION_Y_DEG = 15f
internal const val TRACK_TRANSITION_CAMERA_DISTANCE = 16f
internal const val COVER_TRANSITION_PALETTE_TRIGGER_PROGRESS = 0.50f
internal val HANDOFF_MIDPOINT_GAP = 56.dp
internal const val DEFAULT_CROSSFADE_DURATION_MS = 5000
internal const val SETTLING_DURATION_MS = 250
internal const val CAROUSEL_DURATION_MS = 550
internal const val PARTICLE_MAX = 24
internal const val PARTICLE_LIFETIME_MIN_MS = 1200L
internal const val PARTICLE_LIFETIME_MAX_MS = 1800L
internal const val PARTICLE_SPAWN_PROGRESS_MIN = 0.08f
internal const val PARTICLE_SPAWN_PROGRESS_MAX = 0.82f
private val ArtworkShape = RoundedCornerShape(12.dp)

internal enum class TrackTransitionDirection { Forward, Backward }

internal enum class ArtworkTransitionState {
    IDLE,
    HANDOFF,
    SETTLING,
    CAROUSEL,
}

internal enum class CoverSlotTarget {
    CENTER,
    LEFT,
    RIGHT,
    EXIT_LEFT,
    EXIT_RIGHT,
}

private class CoverItem(
    val trackId: String,
    url: String,
    isCurrentTrack: Boolean,
) {
    var url by mutableStateOf(url)
    var isCurrentTrack by mutableStateOf(isCurrentTrack)
    var isLoaded by mutableStateOf(false)
    var bitmap by mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null)
    var isPaletteTriggered by mutableStateOf(false)

    // Layer transform values driven per frame with zero recomposition
    val translationX = mutableFloatStateOf(0f)
    val scale = mutableFloatStateOf(1f)
    val alpha = mutableFloatStateOf(1f)
    val rotationY = mutableFloatStateOf(0f)
    var targetSlot by mutableStateOf(CoverSlotTarget.CENTER)
}

internal class ArtworkLayoutMetrics(
    val slotOffsetPx: Float,
    val carouselSlidePx: Float,
)

private class ArtworkLayoutMetricsHolder {
    var metrics: ArtworkLayoutMetrics? = null
}

private data class CoverTransform(
    val translationX: Float,
    val scale: Float,
    val alpha: Float,
    val rotationY: Float,
)

private fun mapCoverTransition(
    progress: Float,
    isOutgoing: Boolean,
    direction: TrackTransitionDirection,
    slotOffsetPx: Float,
): CoverTransform {
    val p = progress.coerceIn(0f, 1f)
    val eased = FastOutSlowInEasing.transform(p)
    val sign = if (direction == TrackTransitionDirection.Forward) 1f else -1f

    return if (isOutgoing) {
        val transX = -sign * slotOffsetPx * eased
        val s = 1.0f - (1.0f - COVER_TRANSITION_MIN_SCALE) * eased
        val a = if (eased <= COVER_TRANSITION_ALPHA_FADE_OUT_START) {
            1.0f
        } else {
            1.0f - ((eased - COVER_TRANSITION_ALPHA_FADE_OUT_START) / (1.0f - COVER_TRANSITION_ALPHA_FADE_OUT_START))
        }.coerceIn(0f, 1f)
        val rotY = -sign * TRACK_TRANSITION_ROTATION_Y_DEG * eased
        CoverTransform(transX, s, a, rotY)
    } else {
        val transX = sign * slotOffsetPx * (1.0f - eased)
        val s = COVER_TRANSITION_MIN_SCALE + (1.0f - COVER_TRANSITION_MIN_SCALE) * eased
        val a = if (eased >= COVER_TRANSITION_ALPHA_FADE_IN_END) {
            1.0f
        } else {
            eased / COVER_TRANSITION_ALPHA_FADE_IN_END
        }.coerceIn(0f, 1f)
        val rotY = sign * TRACK_TRANSITION_ROTATION_Y_DEG * (1.0f - eased)
        CoverTransform(transX, s, a, rotY)
    }
}

private fun applyCoverTransform(
    cover: CoverItem,
    transform: CoverTransform,
) {
    cover.translationX.floatValue = transform.translationX
    cover.scale.floatValue = transform.scale
    cover.alpha.floatValue = transform.alpha
    cover.rotationY.floatValue = transform.rotationY
}

private fun checkTriggerPalette(
    progress: Float,
    incomingCover: CoverItem,
    onArtworkBitmap: (androidx.compose.ui.graphics.ImageBitmap) -> Unit,
) {
    if (progress >= COVER_TRANSITION_PALETTE_TRIGGER_PROGRESS && !incomingCover.isPaletteTriggered) {
        incomingCover.bitmap?.let { bmp ->
            incomingCover.isPaletteTriggered = true
            onArtworkBitmap(bmp)
        }
    }
}


/**
 * The fullscreen lyrics page, hosted the way Now Playing is: [FullscreenLyricsSheet] puts it in a
 * ModalBottomSheet on Android, NowPlayingScreenContent puts it in a full-window Popup on Desktop.
 *
 * The orientation picks the layout. Portrait is the page as it has always been. Landscape sets the
 * artwork, with the current Now Playing style's own track row and playback controls under it, beside
 * the lyrics — the shape of Apple Music's desktop full-screen player.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FullscreenLyricsContent(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    color: Color,
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    nowPlayingStyle: String,
    onDismiss: () -> Unit,
) {
    // The Apple Music renderer applies its own gutter inside LyricsView — it has to, because the
    // blur needs margin of its own to spill into and a caller-side gutter leaves it sliced flat at
    // the edge. This sheet's own 50dp then stacked on top of it, insetting the text by 70dp.
    val fullscreenLyricsStyle by sharedViewModel
        .getLyricsStyle()
        .collectAsStateWithLifecycle(DataStoreManager.LYRICS_STYLE_CLASSIC)
    val fullscreenAppleLyrics =
        fullscreenLyricsStyle == DataStoreManager.LYRICS_STYLE_APPLE_MUSIC && isLyricsBlurSupported()
    val screenDataState by sharedViewModel.nowPlayingScreenData.collectAsStateWithLifecycle()
    val timelineState by sharedViewModel.timeline.collectAsStateWithLifecycle()
    // Same correction LyricsView applies to the sheet itself, so the line the share picker opens on
    // is the line the listener is actually hearing.
    val fullscreenLyricsOffsetMs by sharedViewModel.getLyricsOffsetMs().collectAsStateWithLifecycle(0)
    val controllerState by sharedViewModel.controllerState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val localDensity = LocalDensity.current
    val windowInsets = WindowInsets.systemBars

    var sliderValue by rememberSaveable {
        mutableFloatStateOf(0f)
    }

    // Auto-hide controls state - hidden when entering fullscreen, shown on mouse move / tap
    var showControlButtons by rememberSaveable {
        mutableStateOf(false)
    }
    var lastUserInteractionTime by remember {
        mutableLongStateOf(0L)
    }

    var showNowPlayingSheet by rememberSaveable {
        mutableStateOf(false)
    }

    // Reset auto-hide timer when controls are shown
    LaunchedEffect(key1 = lastUserInteractionTime, key2 = showControlButtons) {
        if (showControlButtons) {
            delay(3000) // Hide after 3 seconds of inactivity
            showControlButtons = false
        }
    }

    LaunchedEffect(key1 = timelineState) {
        sliderValue =
            if (timelineState.total > 0L) {
                timelineState.current.toFloat() * 100 / timelineState.total.toFloat()
            } else {
                0f
            }
    }

    if (screenDataState.lyricsData != null) {
        KeepScreenOn()
    }

    var showQueueBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var showInfoBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var showShareLyricsSheet by rememberSaveable {
        mutableStateOf(false)
    }

    // This sheet renders lyrics through LyricsView, which computes the sung line internally and
    // keeps it to itself. The share sheet needs the same number to open on that line, so it is
    // recomputed here from the same two inputs, using the same helper.
    val shareTimedLineIndexes =
        remember(screenDataState.lyricsData?.lyrics?.lines) {
            screenDataState.lyricsData
                ?.lyrics
                ?.lines
                .orEmpty()
                .mapIndexedNotNull { index, line ->
                    line.startTimeMs.toLongOrNull()?.let { TimedLineIndex(index, it) }
                }.sortedBy { it.startTimeMs }
        }

    val screenInfo = getScreenSizeInfo()
    val isDesktop = getPlatform() == Platform.Desktop
    val isLandscape = isDesktop && screenInfo.wDP > screenInfo.hDP
    // Glass source for the Desktop chrome. Each layout marks its own background with it, and the
    // pills are always drawn as siblings of that background — nesting them inside the source is
    // the render-feedback loop that crashes the RuntimeShader.
    val backdrop = rememberBackdrop(Color.Black)

    var lastPointerPosition by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                // The sheet host paints black under this page. The Popup host on Desktop paints
                // nothing, and the gradient's translucent stops would let the app show through.
                .background(Color.Black)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            when (event.type) {
                                PointerEventType.Move -> {
                                    val currentPos = event.changes.firstOrNull()?.position
                                    val lastPos = lastPointerPosition
                                    if (currentPos != null) {
                                        if (lastPos != null) {
                                            val dx = currentPos.x - lastPos.x
                                            val dy = currentPos.y - lastPos.y
                                            // Only trigger if pointer moved more than 2 pixels (ignore synthetic zero-move events)
                                            if (dx * dx + dy * dy > 4f) {
                                                if (!showControlButtons) {
                                                    showControlButtons = true
                                                }
                                                lastUserInteractionTime = Clock.System.now().toEpochMilliseconds()
                                            }
                                        }
                                        lastPointerPosition = currentPos
                                    }
                                }
                                PointerEventType.Press -> {
                                    if (!showControlButtons) {
                                        showControlButtons = true
                                    }
                                    lastUserInteractionTime = Clock.System.now().toEpochMilliseconds()
                                }
                            }
                        }
                    }
                }
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    // Toggle controls on tap/click
                    showControlButtons = !showControlButtons
                    if (showControlButtons) {
                        lastUserInteractionTime = Clock.System.now().toEpochMilliseconds()
                    }
                },
    ) {
        if (isLandscape) {
            FullscreenLyricsLandscape(
                sharedViewModel = sharedViewModel,
                color = color,
                state = state,
                actions = actions,
                nowPlayingStyle = nowPlayingStyle,
                backdrop = backdrop,
                isCrossfading = timelineState.isCrossfading,
                showControlButtons = showControlButtons,
            )
            if (isDesktop) {
                AnimatedVisibility(
                    visible = showControlButtons,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(350, easing = FastOutSlowInEasing)) { -it },
                    exit = fadeOut(tween(250)) + slideOutVertically(tween(350, easing = FastOutSlowInEasing)) { -it },
                    modifier = Modifier.align(Alignment.TopCenter),
                ) {
                    FullscreenLyricsDesktopChrome(
                        sharedViewModel = sharedViewModel,
                        backdrop = backdrop,
                        onDismiss = onDismiss,
                    )
                }
            }
        } else {
            // Crossfade: RGB rainbow color cycling when transitioning between tracks
            val infiniteTransition = rememberInfiniteTransition(label = "crossfadeRainbow")
            val rainbowHue by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
                label = "rainbowHue",
            )
            val rainbowColor = hsvToColor(rainbowHue, 1f, 1f)
            val sliderTrackColor by animateColorAsState(
                targetValue = if (timelineState.isCrossfading) rainbowColor else Color.White,
                animationSpec = tween(300),
                label = "sliderCrossfadeColor",
            )
            Box(modifier = Modifier.fillMaxSize()) {
                // Animated gradient background. The glass source whenever glass is on: the Desktop
                // chrome draws from it, and so does the lyrics timing button on both platforms.
                AnimatedLyricsGradientBackground(
                    color = color,
                    modifier = if (LocalLiquidGlassEnabled.current) Modifier.layerBackdrop(backdrop) else Modifier,
                )

                // ── Foreground content column ─────────────────────────────────────
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                bottom =
                                    with(localDensity) {
                                        windowInsets.getBottom(localDensity).toDp()
                                    },
                                top =
                                    with(localDensity) {
                                        windowInsets.getTop(localDensity).toDp()
                                    },
                            ),
                ) {
                    if (isDesktop) {
                        FullscreenLyricsDesktopChrome(
                            sharedViewModel = sharedViewModel,
                            backdrop = backdrop,
                            onDismiss = onDismiss,
                        )
                    }

                    // New Apple Music Style Header
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 36.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Song Poster (Small, Top Left)
                        AsyncImage(
                            model =
                                ImageRequest
                                    .Builder(LocalPlatformContext.current)
                                    .data(screenDataState.thumbnailURL)
                                    .crossfade(300)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .diskCacheKey(screenDataState.thumbnailURL)
                                    .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier =
                                Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Song Info Column
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            // Song Name
                            Text(
                                text = screenDataState.nowPlayingTitle,
                                style = typo().labelSmall,
                                color = Color.White,
                                maxLines = 1,
                                modifier =
                                    Modifier
                                        .basicMarquee(
                                            iterations = Int.MAX_VALUE,
                                            animationMode = MarqueeAnimationMode.Immediately,
                                        ).focusable(),
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Artist Name with Explicit Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.clickable {
                                        coroutineScope.launch {
                                            val song = sharedViewModel.nowPlayingState.value?.songEntity
                                            (
                                                song?.artistId?.firstOrNull()?.takeIf { it.isNotEmpty() }
                                                    ?: screenDataState.songInfoData?.authorId
                                            )?.let { channelId ->
                                                // The host animates its own exit: the sheet hides
                                                // before it leaves composition, the Popup just closes.
                                                onDismiss()
                                                navController.navigate(
                                                    ArtistDestination(
                                                        channelId = channelId,
                                                    ),
                                                )
                                            }
                                        }
                                    },
                            ) {
                                if (screenDataState.isExplicit) {
                                    ExplicitBadge(
                                        modifier =
                                            Modifier
                                                .size(16.dp)
                                                .padding(end = 4.dp),
                                    )
                                }
                                Text(
                                    text = screenDataState.artistName,
                                    style = typo().bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    modifier =
                                        Modifier
                                            .basicMarquee(
                                                iterations = Int.MAX_VALUE,
                                                animationMode = MarqueeAnimationMode.Immediately,
                                            ).focusable(),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Like Button (Heart)
                        HeartCheckBox(
                            checked = controllerState.isLiked,
                            size = 28,
                        ) {
                            sharedViewModel.onUIEvent(UIEvent.ToggleLike)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Share lyrics — only when there are lyrics to share.
                        if (screenDataState.lyricsData != null) {
                            IconButton(
                                onClick = { showShareLyricsSheet = true },
                            ) {
                                Icon(
                                    imageVector = SimpIcons.Share,
                                    contentDescription = stringResource(Res.string.share_lyrics),
                                    tint = Color.White,
                                )
                            }
                        }

                        // Three Dot Menu
                        IconButton(
                            onClick = { showNowPlayingSheet = true },
                        ) {
                            Icon(
                                imageVector = SimpIcons.MoreVert,
                                contentDescription = "",
                                tint = Color.White,
                            )
                        }
                    }

                    // Lyrics Content - Expands when controls are hidden
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                // Same TOTAL gutter either way, just split differently. The Apple
                                // renderer applies AppleMusicLyricPaddingX itself so its blur has
                                // margin to spill into, so the sheet contributes the remainder — the
                                // text still lines up with this screen's header and slider. Dropping
                                // the sheet's side to zero (which this did for one revision) left the
                                // lyrics sitting further out than everything else on the page.
                                .padding(
                                    horizontal =
                                        if (fullscreenAppleLyrics) {
                                            FULLSCREEN_LYRICS_GUTTER - AppleMusicLyricPaddingX
                                        } else {
                                            FULLSCREEN_LYRICS_GUTTER
                                        },
                                ),
                    ) {
                        FullscreenLyricsList(
                            lyricsData = screenDataState.lyricsData,
                            sharedViewModel = sharedViewModel,
                            color = color,
                            backdrop = backdrop,
                        )
                    }

                    // Progress Bar and Time - Always visible
                    LeftToRight {
                        Column {
                            // Real Slider
                            Box(
                                Modifier
                                    .padding(
                                        top = 15.dp,
                                    ).padding(horizontal = 40.dp),
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(24.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Crossfade(timelineState.loading) {
                                        if (it) {
                                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                                LinearProgressIndicator(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .height(4.dp)
                                                            .padding(
                                                                horizontal = 3.dp,
                                                            ).clip(
                                                                RoundedCornerShape(8.dp),
                                                            ),
                                                    color = Color.Gray,
                                                    trackColor = Color.DarkGray,
                                                    strokeCap = StrokeCap.Round,
                                                )
                                            }
                                        } else {
                                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                                LinearProgressIndicator(
                                                    progress = { timelineState.bufferedPercent.toFloat() / 100 },
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .height(4.dp)
                                                            .padding(
                                                                horizontal = 3.dp,
                                                            ).clip(
                                                                RoundedCornerShape(8.dp),
                                                            ),
                                                    color = Color.Gray,
                                                    trackColor = Color.DarkGray,
                                                    strokeCap = StrokeCap.Round,
                                                    drawStopIndicator = {},
                                                )
                                            }
                                        }
                                    }
                                }
                                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                    Slider(
                                        // Fraction, not 0..100 — see the note in NowPlayingScreen:
                                        // material3 alpha25 drops valueRange on its binary-compatibility
                                        // overload.
                                        value = sliderValue / 100f,
                                        onValueChange = {
                                            sharedViewModel.onUIEvent(
                                                UIEvent.UpdateProgress(it * 100f),
                                            )
                                        },
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(top = 3.dp)
                                                .align(
                                                    Alignment.TopCenter,
                                                ),
                                        track = { sliderState ->
                                            SliderDefaults.Track(
                                                modifier =
                                                    Modifier
                                                        .height(5.dp),
                                                enabled = true,
                                                sliderState = sliderState,
                                                colors =
                                                    SliderDefaults.colors().copy(
                                                        thumbColor = sliderTrackColor,
                                                        activeTrackColor = sliderTrackColor,
                                                        inactiveTrackColor = Color.Transparent,
                                                    ),
                                                thumbTrackGapSize = 0.dp,
                                                drawTick = { _, _ -> },
                                                drawStopIndicator = null,
                                            )
                                        },
                                        thumb = {
                                            SliderDefaults.Thumb(
                                                modifier =
                                                    Modifier
                                                        .height(18.dp)
                                                        .width(8.dp)
                                                        .padding(
                                                            vertical = 4.dp,
                                                        ),
                                                thumbSize = DpSize(8.dp, 8.dp),
                                                interactionSource =
                                                    remember {
                                                        MutableInteractionSource()
                                                    },
                                                colors =
                                                    SliderDefaults.colors().copy(
                                                        thumbColor = Color.White,
                                                        activeTrackColor = Color.White,
                                                        inactiveTrackColor = Color.Transparent,
                                                    ),
                                                enabled = true,
                                            )
                                        },
                                    )
                                }
                            }
                            LazyColumn {
                                item {
                                    // Time Layout
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 40.dp),
                                    ) {
                                        Text(
                                            text = if (timelineState.isLive) "" else formatDuration(timelineState.current),
                                            style = typo().bodyMedium,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Left,
                                        )
                                        AnimatedVisibility(
                                            enter = fadeIn(),
                                            exit = fadeOut(),
                                            visible = timelineState.isCrossfading,
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.crossfading),
                                                style = typo().bodyMedium,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                        Text(
                                            text = timelineState.lengthLabel(),
                                            style = typo().bodyMedium,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Right,
                                        )
                                    }

                                    Spacer(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(5.dp),
                                    )
                                }

                                item {
                                    // Control Buttons - Animated visibility
                                    AnimatedVisibility(
                                        visible = showControlButtons,
                                        enter =
                                            expandVertically(
                                                tween(300),
                                            ),
                                        exit =
                                            shrinkVertically(
                                                tween(300),
                                            ),
                                    ) {
                                        PlayerControlLayout(controllerState) {
                                            sharedViewModel.onUIEvent(it)
                                        }
                                    }
                                    AnimatedVisibility(
                                        visible = showControlButtons,
                                        enter =
                                            expandVertically(
                                                tween(300),
                                            ),
                                        exit =
                                            shrinkVertically(
                                                tween(300),
                                            ),
                                    ) {
                                        // List Bottom Buttons
                                        Box(
                                            modifier =
                                                Modifier
                                                    .height(32.dp)
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 40.dp),
                                        ) {
                                            IconButton(
                                                modifier =
                                                    Modifier
                                                        .size(24.dp)
                                                        .aspectRatio(1f)
                                                        .align(Alignment.CenterStart)
                                                        .clip(
                                                            CircleShape,
                                                        ),
                                                onClick = {
                                                    showInfoBottomSheet = true
                                                    showControlButtons = true
                                                },
                                            ) {
                                                Icon(imageVector = SimpIcons.Info, tint = Color.White, contentDescription = "")
                                            }
                                            Row(
                                                Modifier.align(Alignment.CenterEnd),
                                            ) {
                                                Spacer(modifier = Modifier.size(8.dp))
                                                IconButton(
                                                    modifier =
                                                        Modifier
                                                            .size(24.dp)
                                                            .aspectRatio(1f)
                                                            .clip(
                                                                CircleShape,
                                                            ),
                                                    onClick = {
                                                        showQueueBottomSheet = true
                                                        showControlButtons = true
                                                    },
                                                ) {
                                                    Icon(
                                                        imageVector = SimpIcons.QueueMusic,
                                                        tint = Color.White,
                                                        contentDescription = "",
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                            }
                        }
                    }

                    // When control buttons are hidden, add spacer to maintain proper spacing
                    if (!showControlButtons) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
    if (showQueueBottomSheet) {
        QueueBottomSheet(
            onDismiss = {
                showQueueBottomSheet = false
            },
            navController = navController,
            onNavigateToOtherScreen = {
                showQueueBottomSheet = false
                onDismiss()
            },
        )
    }
    if (showInfoBottomSheet) {
        InfoPlayerBottomSheet(
            onDismiss = {
                showInfoBottomSheet = false
            },
        )
    }
    if (showNowPlayingSheet) {
        NowPlayingBottomSheet(
            onDismiss = {
                showNowPlayingSheet = false
            },
            navController = navController,
            onNavigateToOtherScreen = {
                onDismiss()
            },
            song = null,
            setSleepTimerEnable = true,
            changeMainLyricsProviderEnable = true,
        )
    }

    screenDataState.lyricsData?.let { lyricsData ->
        if (showShareLyricsSheet) {
            ShareLyricsSheet(
                lines = lyricsData.toShareLyricsLines(),
                songTitle = screenDataState.nowPlayingTitle,
                artistName = screenDataState.artistName,
                artwork = screenDataState.bitmap,
                seedColor = color,
                initialLineIndex = shareTimedLineIndexes.activeIndexAt(timelineState.current - fullscreenLyricsOffsetMs),
                onDismiss = { showShareLyricsSheet = false },
            )
        }
    }
}

/**
 * Landscape: the artwork with the current Now Playing style's track row and playback controls under
 * it on the left, the lyrics on the right. Only the row and the controls come from the style — its
 * action row (info, cast, queue…) stays on the Now Playing page.
 */
@Composable
private fun FullscreenLyricsLandscape(
    sharedViewModel: SharedViewModel,
    color: Color,
    state: NowPlayingContentState,
    actions: NowPlayingContentActions,
    nowPlayingStyle: String,
    backdrop: PlatformBackdrop,
    isCrossfading: Boolean = false,
    showControlButtons: Boolean = true,
) {
    val localDensity = LocalDensity.current
    val isDesktop = getPlatform() == Platform.Desktop
    val transitionEnabled = isDesktop && ARTWORK_TRANSITION_ENABLED

    val dataStoreManager: DataStoreManager = koinInject()
    val configuredCrossfadeDuration by dataStoreManager.crossfadeDuration.collectAsStateWithLifecycle(DEFAULT_CROSSFADE_DURATION_MS)
    val effectiveCrossfadeMs = if (configuredCrossfadeDuration > 0) configuredCrossfadeDuration else DEFAULT_CROSSFADE_DURATION_MS

    // Trigger key: stable track identity (videoId, with title+artist fallback).
    val currentTrackId = state.videoId?.takeIf { it.isNotBlank() }
        ?: (state.screenData.nowPlayingTitle + "_" + state.screenData.artistName).takeIf { it.isNotBlank() }
    val currentThumbnailUrl = state.screenData.thumbnailURL

    var transitionState by remember { mutableStateOf(ArtworkTransitionState.IDLE) }
    var transitionDirection by remember { mutableStateOf(TrackTransitionDirection.Forward) }
    var displayedTrackId by remember { mutableStateOf<String?>(null) }
    var displayedArtworkUrl by remember { mutableStateOf<String?>(null) }
    var isFirstComposition by remember { mutableStateOf(true) }
    var lastOrderIndex by remember { mutableIntStateOf(state.currentOrderIndex) }
    var lastIsCrossfading by remember { mutableStateOf(isCrossfading) }

    // Progress drives the continuous scale, translation, alpha, rotation (0f -> 1f)
    val transitionProgress = remember { Animatable(0f) }

    // Up to 2 active covers on screen
    var activeCovers by remember { mutableStateOf<List<CoverItem>>(emptyList()) }

    val transitionScope = rememberCoroutineScope()
    var transitionJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    val metricsHolder = remember { ArtworkLayoutMetricsHolder() }
    val particlePool = remember { HandoffParticlePool(PARTICLE_MAX) }

    fun completeTransition(winningCover: CoverItem) {
        activeCovers = listOf(winningCover)
        displayedArtworkUrl = winningCover.url
        displayedTrackId = winningCover.trackId
        transitionState = ArtworkTransitionState.IDLE
        particlePool.reset()
        if (!winningCover.isPaletteTriggered) {
            winningCover.bitmap?.let { bmp ->
                winningCover.isPaletteTriggered = true
                actions.onArtworkBitmap(bmp)
            }
        }
        transitionScope.launch {
            transitionProgress.snapTo(0f)
        }
    }

    // Trigger palette callback on incoming cover once transitionProgress reaches midpoint (0.50f)
    LaunchedEffect(activeCovers, transitionState) {
        if (activeCovers.size >= 2 && transitionState != ArtworkTransitionState.IDLE) {
            val incomingCover = activeCovers[1]
            snapshotFlow { transitionProgress.value }
                .collect { p ->
                    if (p >= COVER_TRANSITION_PALETTE_TRIGGER_PROGRESS && !incomingCover.isPaletteTriggered) {
                        incomingCover.bitmap?.let { bmp ->
                            incomingCover.isPaletteTriggered = true
                            Logger.d(
                                "FullscreenLyrics",
                                "[${Clock.System.now()}] [PALETTE] Dynamic color triggered at progress=$p for trackId=${incomingCover.trackId}"
                            )
                            actions.onArtworkBitmap(bmp)
                        }
                    }
                }
        }
    }

    // Observe isCrossfading changes
    LaunchedEffect(isCrossfading) {
        if (isCrossfading != lastIsCrossfading) {
            val old = lastIsCrossfading
            lastIsCrossfading = isCrossfading
            Logger.d(
                "FullscreenLyrics",
                "[${Clock.System.now()}] [INPUT] isCrossfading changed: old=$old, new=$isCrossfading (state=$transitionState, trackId=$currentTrackId)"
            )

            // If crossfade just ended (true -> false) and we are currently in HANDOFF
            if (!isCrossfading && transitionState == ArtworkTransitionState.HANDOFF) {
                val incoming = activeCovers.getOrNull(1)
                if (incoming != null) {
                    val fromState = transitionState
                    transitionState = ArtworkTransitionState.SETTLING
                    Logger.d(
                        "FullscreenLyrics",
                        "[${Clock.System.now()}] [STATE] State transition: from=$fromState, to=SETTLING, reason=Crossfade ended (isCrossfading -> false)"
                    )

                    transitionJob?.cancel()
                    transitionJob = transitionScope.launch {
                        val currentP = transitionProgress.value
                        if (currentP < 1f) {
                            transitionProgress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(
                                    durationMillis = SETTLING_DURATION_MS,
                                    easing = FastOutSlowInEasing,
                                ),
                            )
                        }
                        completeTransition(incoming)
                        Logger.d(
                            "FullscreenLyrics",
                            "[${Clock.System.now()}] [STATE] Settling completed (trackId=${incoming.trackId})"
                        )
                    }
                }
            }
        }
    }

    // Observe track changes (identity + thumbnailURL)
    LaunchedEffect(currentTrackId, currentThumbnailUrl) {
        Logger.d(
            "FullscreenLyrics",
            "[${Clock.System.now()}] [INPUT] Track emission: trackId=$currentTrackId, thumbnailURL=$currentThumbnailUrl, isCrossfading=$isCrossfading"
        )

        if (currentTrackId == null) return@LaunchedEffect

        // Decision 2: No transition on first composition
        if (isFirstComposition) {
            if (currentThumbnailUrl != null) {
                val initialCover = CoverItem(
                    trackId = currentTrackId,
                    url = currentThumbnailUrl,
                    isCurrentTrack = true,
                )
                activeCovers = listOf(initialCover)
                displayedTrackId = currentTrackId
                displayedArtworkUrl = currentThumbnailUrl
                lastOrderIndex = state.currentOrderIndex
                isFirstComposition = false
                Logger.d(
                    "FullscreenLyrics",
                    "[${Clock.System.now()}] [STATE] State transition: from=INITIAL, to=IDLE, reason=First composition (trackId=$currentTrackId)"
                )
            } else {
                displayedTrackId = currentTrackId
            }
            return@LaunchedEffect
        }

        // Decision 1: A URL change within the same track swaps in place with NO transition
        if (currentTrackId == displayedTrackId) {
            if (currentThumbnailUrl != null && currentThumbnailUrl != displayedArtworkUrl) {
                displayedArtworkUrl = currentThumbnailUrl
                activeCovers.find { it.trackId == currentTrackId }?.let {
                    it.url = currentThumbnailUrl
                }
            }
            return@LaunchedEffect
        }

        // New track arrived, but thumbnailURL has not arrived yet: wait for non-null URL
        if (currentThumbnailUrl == null) {
            return@LaunchedEffect
        }

        // Decision 3: If new artwork URL equals the displayed one (same album cover), keep 1 cover at center
        if (currentThumbnailUrl == displayedArtworkUrl) {
            Logger.d(
                "FullscreenLyrics",
                "[${Clock.System.now()}] [STATE] Skipping transition for trackId=$currentTrackId: same artwork URL ($currentThumbnailUrl)"
            )
            displayedTrackId = currentTrackId
            lastOrderIndex = state.currentOrderIndex
            activeCovers.firstOrNull()?.let {
                if (it.trackId != currentTrackId) {
                    activeCovers = listOf(CoverItem(currentTrackId, currentThumbnailUrl, isCurrentTrack = true))
                }
            }
            return@LaunchedEffect
        }

        val delta = state.currentOrderIndex - lastOrderIndex
        val direction = if (delta == -1) TrackTransitionDirection.Backward else TrackTransitionDirection.Forward
        transitionDirection = direction
        lastOrderIndex = state.currentOrderIndex

        // Check Case: Revert during HANDOFF
        val existingOutgoing = activeCovers.getOrNull(0)
        val existingIncoming = activeCovers.getOrNull(1)
        if (transitionState == ArtworkTransitionState.HANDOFF && existingOutgoing != null && existingOutgoing.trackId == currentTrackId) {
            val fromState = transitionState
            particlePool.reset()
            Logger.d(
                "FullscreenLyrics",
                "[${Clock.System.now()}] [STATE] State transition: from=$fromState, to=IDLE (revert), reason=Identity reverted to outgoing track ($currentTrackId)"
            )
            existingOutgoing.isCurrentTrack = true
            if (existingIncoming != null) {
                existingIncoming.isCurrentTrack = false
            }
            displayedTrackId = currentTrackId
            displayedArtworkUrl = existingOutgoing.url

            transitionJob?.cancel()
            transitionJob = transitionScope.launch {
                transitionProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = SETTLING_DURATION_MS, easing = FastOutSlowInEasing),
                )
                completeTransition(existingOutgoing)
            }
            return@LaunchedEffect
        }

        // Check Case: Third track skip during HANDOFF, SETTLING, or CAROUSEL
        if ((transitionState == ArtworkTransitionState.HANDOFF || transitionState == ArtworkTransitionState.SETTLING || transitionState == ArtworkTransitionState.CAROUSEL)
            && existingIncoming != null && currentTrackId != existingIncoming.trackId && currentTrackId != existingOutgoing?.trackId
        ) {
            val fromState = transitionState
            transitionState = ArtworkTransitionState.CAROUSEL
            particlePool.reset()
            Logger.d(
                "FullscreenLyrics",
                "[${Clock.System.now()}] [STATE] State transition: from=$fromState, to=CAROUSEL, reason=Third track skip during transition (incoming=${existingIncoming.trackId} -> new=$currentTrackId)"
            )

            // Current incoming becomes outgoing for 3rd track
            existingIncoming.isCurrentTrack = false

            val newCover = CoverItem(
                trackId = currentTrackId,
                url = currentThumbnailUrl,
                isCurrentTrack = true,
            )

            activeCovers = listOf(existingIncoming, newCover)
            displayedTrackId = currentTrackId

            transitionJob?.cancel()
            transitionJob = transitionScope.launch {
                transitionProgress.snapTo(0f)
                val waitStartTime = TimeSource.Monotonic.markNow()
                withTimeoutOrNull(300L) {
                    snapshotFlow { newCover.isLoaded }.first { it }
                }
                val waitedMs = waitStartTime.elapsedNow().inWholeMilliseconds
                Logger.d(
                    "FullscreenLyrics",
                    "[${Clock.System.now()}] [LOAD] Waited ${waitedMs}ms for 3rd-track cover (loaded=${newCover.isLoaded})"
                )

                transitionProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = CAROUSEL_DURATION_MS, easing = LinearEasing),
                )
                completeTransition(newCover)
            }
            return@LaunchedEffect
        }

        // Normal transition (HANDOFF or CAROUSEL)
        val outgoingCover = activeCovers.lastOrNull { it.isCurrentTrack } ?: activeCovers.lastOrNull()
        if (outgoingCover == null) {
            val singleCover = CoverItem(currentTrackId, currentThumbnailUrl, isCurrentTrack = true)
            activeCovers = listOf(singleCover)
            displayedTrackId = currentTrackId
            displayedArtworkUrl = currentThumbnailUrl
            transitionState = ArtworkTransitionState.IDLE
            Logger.d(
                "FullscreenLyrics",
                "[${Clock.System.now()}] [STATE] No outgoing cover known: staying IDLE (trackId=$currentTrackId)"
            )
            return@LaunchedEffect
        }

        outgoingCover.isCurrentTrack = false
        val incomingCover = CoverItem(
            trackId = currentTrackId,
            url = currentThumbnailUrl,
            isCurrentTrack = true,
        )

        if (isCrossfading) {
            // === HANDOFF ===
            val fromState = transitionState
            transitionState = ArtworkTransitionState.HANDOFF
            Logger.d(
                "FullscreenLyrics",
                "[${Clock.System.now()}] [STATE] State transition: from=$fromState, to=HANDOFF, duration=${effectiveCrossfadeMs}ms (outgoing=${outgoingCover.trackId}, incoming=$currentTrackId)"
            )

            activeCovers = listOf(outgoingCover, incomingCover)
            displayedTrackId = currentTrackId

            transitionJob?.cancel()
            transitionJob = transitionScope.launch {
                transitionProgress.snapTo(0f)
                val waitStartTime = TimeSource.Monotonic.markNow()
                withTimeoutOrNull(300L) {
                    snapshotFlow { incomingCover.isLoaded }.first { it }
                }
                val waitedMs = waitStartTime.elapsedNow().inWholeMilliseconds
                Logger.d(
                    "FullscreenLyrics",
                    "[${Clock.System.now()}] [LOAD] Waited ${waitedMs}ms for handoff incoming cover (loaded=${incomingCover.isLoaded})"
                )

                // UI-driven progress loop with pause/resume support
                while (transitionProgress.value < 1f && isActive) {
                    if (!state.controllerState.isPlaying) {
                        Logger.d("FullscreenLyrics", "[PAUSE] Freezing transition at progress=${transitionProgress.value}")
                        snapshotFlow { state.controllerState.isPlaying }.first { it }
                        Logger.d("FullscreenLyrics", "[RESUME] Resuming transition from progress=${transitionProgress.value}")
                    }
                    val remainingProgress = (1f - transitionProgress.value).coerceAtLeast(0f)
                    if (remainingProgress <= 0.001f) break
                    val remainingDurationMs = (effectiveCrossfadeMs * remainingProgress).roundToInt().coerceAtLeast(16)

                    coroutineScope {
                        val animJob = launch {
                            transitionProgress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = remainingDurationMs, easing = LinearEasing),
                            )
                        }
                        val pauseJob = launch {
                            snapshotFlow { state.controllerState.isPlaying }.first { !it }
                            animJob.cancel()
                        }
                        animJob.join()
                        pauseJob.cancel()
                    }
                }
                Logger.d(
                    "FullscreenLyrics",
                    "[${Clock.System.now()}] [ANIM] Handoff progress reached 1.0 (waiting for isCrossfading -> false)"
                )
            }
        } else {
            // === CAROUSEL ===
            val fromState = transitionState
            transitionState = ArtworkTransitionState.CAROUSEL
            particlePool.reset()
            Logger.d(
                "FullscreenLyrics",
                "[${Clock.System.now()}] [STATE] State transition: from=$fromState, to=CAROUSEL, duration=${CAROUSEL_DURATION_MS}ms (dir=$direction, outgoing=${outgoingCover.trackId}, incoming=$currentTrackId)"
            )

            activeCovers = listOf(outgoingCover, incomingCover)
            displayedTrackId = currentTrackId

            transitionJob?.cancel()
            transitionJob = transitionScope.launch {
                transitionProgress.snapTo(0f)
                val waitStartTime = TimeSource.Monotonic.markNow()
                withTimeoutOrNull(300L) {
                    snapshotFlow { incomingCover.isLoaded }.first { it }
                }
                val waitedMs = waitStartTime.elapsedNow().inWholeMilliseconds
                Logger.d(
                    "FullscreenLyrics",
                    "[${Clock.System.now()}] [LOAD] Waited ${waitedMs}ms for carousel incoming cover (loaded=${incomingCover.isLoaded})"
                )

                transitionProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = CAROUSEL_DURATION_MS, easing = LinearEasing),
                )
                completeTransition(incomingCover)
                Logger.d(
                    "FullscreenLyrics",
                    "[${Clock.System.now()}] [STATE] Carousel transition completed (trackId=$currentTrackId)"
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // The glass backdrop source. The Desktop chrome drawn over this layout is a sibling of this
        // whole Box, never a child of it.
        Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
            if (nowPlayingStyle == DataStoreManager.NOW_PLAYING_STYLE_APPLE_MUSIC) {
                // The same seed rule the Apple Music page applies: the shell's colour starts on black
                // and only turns into the palette colour once the palette resolves.
                val paletteColor = state.startColor.value
                AppleMusicArtworkBackdrop(
                    state = state,
                    actions = actions,
                    seedColor = if (paletteColor == Color.Black) seed else paletteColor,
                )
            } else {
                AnimatedLyricsGradientBackground(color = color)
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top =
                            if (getPlatform() == Platform.Desktop) {
                                LANDSCAPE_DESKTOP_TOP_PADDING
                            } else {
                                LANDSCAPE_ANDROID_TOP_PADDING +
                                    with(localDensity) { WindowInsets.statusBars.getTop(localDensity).toDp() }
                            },
                        bottom = LANDSCAPE_BOTTOM_PADDING,
                    ),
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(0.45f)
                        .fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                BoxWithConstraints {
                    val baseArtworkSize = minOf(maxWidth * 0.75f, maxHeight * 0.5f, LANDSCAPE_ARTWORK_MAX_SIZE)
                    val expandedArtworkSize = maxOf(baseArtworkSize, minOf(maxWidth * 0.85f, maxHeight * 0.72f, 560.dp))
                    val targetArtworkSize = if (showControlButtons) baseArtworkSize else expandedArtworkSize
                    val currentArtworkSize by animateDpAsState(
                        targetValue = targetArtworkSize,
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                        label = "artworkSizeAnimation",
                    )

                    val artworkWidthPx = with(localDensity) { currentArtworkSize.toPx() }
                    val slotOffsetPx = 0.75f * artworkWidthPx + with(localDensity) { HANDOFF_MIDPOINT_GAP.toPx() }
                    val carouselSlidePx = slotOffsetPx

                    metricsHolder.metrics = ArtworkLayoutMetrics(
                        slotOffsetPx = slotOffsetPx,
                        carouselSlidePx = carouselSlidePx,
                    )

                    Column(
                        modifier =
                            Modifier
                                // Every style row carries its own 20dp side gutters, so a column
                                // 40dp wider than the artwork lines the rows up with its edges.
                                .width(currentArtworkSize + 40.dp)
                                // A short landscape window — a phone on its side — cannot fit the
                                // artwork, the row and the transport at once. Scrolling keeps every
                                // control full size where the column would otherwise squash the last.
                                .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // 2D Mechanics for Artwork: Continuous Scale + Translation + Fade
                        Box(
                            modifier =
                                Modifier
                                    .padding(horizontal = 20.dp)
                                    .size(currentArtworkSize),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (!transitionEnabled) {
                                AsyncImage(
                                    model =
                                        ImageRequest
                                            .Builder(LocalPlatformContext.current)
                                            .data(state.screenData.thumbnailURL)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .diskCacheKey(state.screenData.thumbnailURL)
                                            .crossfade(false)
                                            .build(),
                                    placeholder = rememberHolderPainter(),
                                    error = rememberHolderPainter(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    onSuccess = {
                                        actions.onArtworkBitmap(it.result.image.toImageBitmap())
                                    },
                                    modifier =
                                        Modifier
                                            .size(currentArtworkSize)
                                            .clip(ArtworkShape),
                                )
                            } else {
                                @Composable
                                fun RenderArtworkCover(cover: CoverItem, isOutgoing: Boolean) {
                                    key(cover.trackId) {
                                        AsyncImage(
                                            model =
                                                ImageRequest
                                                    .Builder(LocalPlatformContext.current)
                                                    .data(cover.url)
                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                    .diskCacheKey(cover.url)
                                                    .crossfade(false)
                                                    .build(),
                                            placeholder = rememberHolderPainter(),
                                            error = rememberHolderPainter(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            onSuccess = {
                                                cover.isLoaded = true
                                                val bmp = it.result.image.toImageBitmap()
                                                cover.bitmap = bmp
                                                if (isFirstComposition || transitionState == ArtworkTransitionState.IDLE) {
                                                    actions.onArtworkBitmap(bmp)
                                                } else if (!isOutgoing && transitionProgress.value >= COVER_TRANSITION_PALETTE_TRIGGER_PROGRESS && !cover.isPaletteTriggered) {
                                                    cover.isPaletteTriggered = true
                                                    actions.onArtworkBitmap(bmp)
                                                }
                                            },
                                            onError = {
                                                cover.isLoaded = true
                                                val fallback = cover.url.replace("maxresdefault", "hqdefault")
                                                if (fallback != cover.url) {
                                                    cover.url = fallback
                                                }
                                            },
                                            modifier =
                                                Modifier
                                                    .size(currentArtworkSize)
                                                    .graphicsLayer {
                                                        if (activeCovers.size >= 2 && transitionState != ArtworkTransitionState.IDLE) {
                                                            val transform = mapCoverTransition(
                                                                progress = transitionProgress.value,
                                                                isOutgoing = isOutgoing,
                                                                direction = transitionDirection,
                                                                slotOffsetPx = slotOffsetPx,
                                                            )
                                                            translationX = transform.translationX
                                                            scaleX = transform.scale
                                                            scaleY = transform.scale
                                                            rotationY = transform.rotationY
                                                            cameraDistance = TRACK_TRANSITION_CAMERA_DISTANCE * density
                                                            alpha = transform.alpha.coerceIn(0f, 1f)
                                                        } else {
                                                            translationX = 0f
                                                            scaleX = 1f
                                                            scaleY = 1f
                                                            rotationY = 0f
                                                            cameraDistance = TRACK_TRANSITION_CAMERA_DISTANCE * density
                                                            alpha = 1f
                                                        }
                                                        shape = ArtworkShape
                                                        clip = true
                                                    }
                                                    .clip(ArtworkShape),
                                        )
                                    }
                                }

                                if (activeCovers.size >= 2) {
                                    // Layer 1 (bottom): Outgoing cover
                                    RenderArtworkCover(activeCovers[0], isOutgoing = true)

                                    // Layer 2 (middle): Handoff particle canvas between the covers
                                    val shouldRunParticleLoop = transitionEnabled && HANDOFF_PARTICLES_ENABLED &&
                                        (transitionState == ArtworkTransitionState.HANDOFF || (transitionState == ArtworkTransitionState.SETTLING && particlePool.activeCount > 0))

                                    if (shouldRunParticleLoop || particlePool.activeCount > 0) {
                                        HandoffParticleCanvas(
                                            pool = particlePool,
                                            boxSizePx = artworkWidthPx,
                                            outgoingRightRimX = {
                                                val p = transitionProgress.value
                                                val t = mapCoverTransition(p, isOutgoing = true, direction = transitionDirection, slotOffsetPx = slotOffsetPx)
                                                t.translationX + (artworkWidthPx * t.scale) / 2f
                                            },
                                            incomingLeftRimX = {
                                                val p = transitionProgress.value
                                                val t = mapCoverTransition(p, isOutgoing = false, direction = transitionDirection, slotOffsetPx = slotOffsetPx)
                                                t.translationX - (artworkWidthPx * t.scale) / 2f
                                            },
                                            isSpawningAllowed = {
                                                transitionState == ArtworkTransitionState.HANDOFF &&
                                                    transitionProgress.value in PARTICLE_SPAWN_PROGRESS_MIN..PARTICLE_SPAWN_PROGRESS_MAX
                                            },
                                            isRunning = shouldRunParticleLoop,
                                            modifier = Modifier.size(currentArtworkSize),
                                        )
                                    }

                                    // Layer 3 (top): Incoming cover
                                    RenderArtworkCover(activeCovers[1], isOutgoing = false)
                                } else if (activeCovers.isNotEmpty()) {
                                    RenderArtworkCover(activeCovers[0], isOutgoing = false)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        when (nowPlayingStyle) {
                            DataStoreManager.NOW_PLAYING_STYLE_M3_EXPRESSIVE ->
                                NowPlayingExpressiveTheme(state = state) {
                                    Column {
                                        ExpressiveTrackInfoRow(
                                            state = state,
                                            actions = actions,
                                            showCanvasThumbnail = false,
                                        )
                                        // Shuffle and repeat live in the connected group on Now
                                        // Playing, which this page leaves out — so the transport
                                        // carries them here.
                                        AnimatedVisibility(
                                            visible = showControlButtons,
                                            enter = fadeIn(tween(300)) + expandVertically(tween(400, easing = FastOutSlowInEasing)),
                                            exit = fadeOut(tween(250)) + shrinkVertically(tween(400, easing = FastOutSlowInEasing)),
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                ExpressivePlaybackControls(
                                                    state = state,
                                                    actions = actions,
                                                    showShuffleAndRepeat = true,
                                                )
                                            }
                                        }
                                    }
                                }

                            DataStoreManager.NOW_PLAYING_STYLE_APPLE_MUSIC -> {
                                val typography = rememberAppleMusicTypography()
                                AppleMusicMainTitleRow(state = state, actions = actions, typography = typography)
                                AnimatedVisibility(
                                    visible = showControlButtons,
                                    enter = fadeIn(tween(300)) + expandVertically(tween(400, easing = FastOutSlowInEasing)),
                                    exit = fadeOut(tween(250)) + shrinkVertically(tween(400, easing = FastOutSlowInEasing)),
                                ) {
                                    Column(
                                        modifier =
                                            Modifier
                                                .padding(horizontal = 20.dp)
                                                .padding(top = 16.dp),
                                    ) {
                                        // Apple's own desktop player puts shuffle and repeat at the
                                        // two ends of this row; Now Playing keeps them in the queue.
                                        AppleMusicPlaybackControls(
                                            state = state,
                                            actions = actions,
                                            typography = typography,
                                            showShuffleAndRepeat = true,
                                        )
                                    }
                                }
                            }

                            else -> {
                                NowPlayingTrackInfoRow(
                                    state = state,
                                    actions = actions,
                                    showCanvasThumbnail = false,
                                )
                                AnimatedVisibility(
                                    visible = showControlButtons,
                                    enter = fadeIn(tween(300)) + expandVertically(tween(400, easing = FastOutSlowInEasing)),
                                    exit = fadeOut(tween(250)) + shrinkVertically(tween(400, easing = FastOutSlowInEasing)),
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        SpotifyPlaybackControls(state = state, actions = actions)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier =
                    Modifier
                        .weight(0.55f)
                        .fillMaxHeight()
                        .padding(end = 32.dp),
            ) {
                key(currentTrackId) {
                    FullscreenLyricsList(
                        lyricsData = state.screenData.lyricsData,
                        sharedViewModel = sharedViewModel,
                        color = color,
                        backdrop = backdrop,
                    )
                }
            }
        }
    }
}

/**
 * Desktop's way out of the page, plus the two controls it covers up. There is no sheet to drag
 * away and no back gesture there, and the page sits over the capsule that normally carries the
 * mini-player switch and the volume.
 */
@Composable
private fun FullscreenLyricsDesktopChrome(
    sharedViewModel: SharedViewModel,
    backdrop: PlatformBackdrop,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val controllerState by sharedViewModel.controllerState.collectAsStateWithLifecycle()

    // The Desktop capsule's volume state machine (MiniPlayer), unchanged.
    var isVolumeSliding by rememberSaveable {
        mutableStateOf(false)
    }
    var volumeValue by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(key1 = controllerState, key2 = isVolumeSliding) {
        if (!isVolumeSliding) {
            volumeValue = controllerState.volume
        }
    }
    // Remembers the level to come back to when unmuting, so the button restores
    // what the user was listening at instead of jumping to full volume.
    // Starting muted leaves nothing to restore, so full volume stays the fallback.
    var previousVolumeValue by rememberSaveable {
        mutableFloatStateOf(controllerState.volume.takeIf { it > 0f } ?: 1f)
    }
    LaunchedEffect(controllerState.volume) {
        if (controllerState.volume > 0f) {
            previousVolumeValue = controllerState.volume
        }
    }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier =
                Modifier
                    .height(48.dp)
                    .liquidGlass(backdrop, RoundedCornerShape(24.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val isFullscreen = isFullscreenPlayerOpen()
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = SimpIcons.Close,
                    contentDescription = if (isFullscreen) "Exit Fullscreen" else "Close",
                    tint = Color.White,
                )
            }
            IconButton(
                onClick = {
                    if (isFullscreen) {
                        onDismiss()
                    } else {
                        toggleFullscreenPlayer()
                    }
                },
            ) {
                Icon(
                    imageVector = if (isFullscreen) SimpIcons.FullscreenExit else SimpIcons.Fullscreen,
                    contentDescription = if (isFullscreen) "Exit Fullscreen" else "Fullscreen Player",
                    tint = Color.White,
                )
            }
            IconButton(
                onClick = {
                    if (isFullscreen) {
                        onDismiss()
                        setMiniPlayerOpen(true)
                    } else {
                        toggleMiniPlayer()
                    }
                },
            ) {
                Icon(
                    imageVector = SimpIcons.PictureInPictureAlt,
                    contentDescription = "Mini Player",
                    tint = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier =
                Modifier
                    .height(48.dp)
                    // Static glass: the press swell scales the whole pill, which would slide the
                    // slider out from under a pointer that is dragging it.
                    .liquidGlass(backdrop, RoundedCornerShape(24.dp), interactive = false)
                    .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    // Toggle mute/unmute
                    if (controllerState.volume > 0f) {
                        sharedViewModel.onUIEvent(UIEvent.UpdateVolume(0f))
                    } else {
                        sharedViewModel.onUIEvent(
                            UIEvent.UpdateVolume(previousVolumeValue.coerceIn(0.1f, 1f)),
                        )
                    }
                },
            ) {
                Icon(
                    imageVector =
                        if (controllerState.volume > 0f) {
                            SimpIcons.VolumeUp
                        } else {
                            SimpIcons.VolumeOff
                        },
                    tint = Color.White,
                    contentDescription = if (controllerState.volume > 0f) "Mute" else "Unmute",
                )
            }
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                Slider(
                    value = volumeValue,
                    onValueChangeFinished = {
                        isVolumeSliding = false
                        sharedViewModel.onUIEvent(
                            UIEvent.UpdateVolume(volumeValue.coerceIn(0f, 1f)),
                        )
                    },
                    onValueChange = {
                        isVolumeSliding = true
                        volumeValue = it
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.width(CHROME_VOLUME_SLIDER_WIDTH),
                    track = { sliderState ->
                        SliderDefaults.Track(
                            modifier =
                                Modifier
                                    .height(4.dp),
                            enabled = true,
                            sliderState = sliderState,
                            colors =
                                SliderDefaults.colors().copy(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.White,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                                ),
                            thumbTrackGapSize = 0.dp,
                            drawTick = { _, _ -> },
                            drawStopIndicator = null,
                        )
                    },
                    thumb = {
                        // No thumb, as on the capsule's volume slider — the active/inactive split
                        // marks the level.
                        Spacer(Modifier.size(0.dp))
                    },
                )
            }
        }
    }
}

// The lyrics themselves, or the "unavailable" line when the track has none. The portrait and
// landscape layouts show exactly the same thing, only boxed differently.
@Composable
private fun FullscreenLyricsList(
    lyricsData: NowPlayingScreenData.LyricsData?,
    sharedViewModel: SharedViewModel,
    color: Color,
    backdrop: PlatformBackdrop,
) {
    val lyricsOffsetMs by sharedViewModel.getLyricsOffsetMs().collectAsStateWithLifecycle(0)
    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = lyricsData != null,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (it) {
                lyricsData?.let { lyrics ->
                    LyricsView(
                        lyricsData = lyrics,
                        timeLine = sharedViewModel.timeline,
                        onLineClick = { f ->
                            sharedViewModel.onUIEvent(UIEvent.UpdateProgress(f))
                        },
                        modifier = Modifier.fillMaxSize(),
                        showScrollShadows = true,
                        backgroundColor = color,
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.unavailable),
                        style = typo().bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        // Bottom-start, clear of the offset button at bottom-end; glass like the rest of this page's
        // floating chrome. Shows only what the remote config places under the lyrics, and only over
        // lyrics, as on the Apple Music player's Lyrics tab.
        if (lyricsData != null) {
            LyricsPromoLine(
                backdrop = backdrop,
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, end = 72.dp, bottom = 12.dp),
                sharedViewModel = sharedViewModel,
            )
        }
        // Here rather than in either layout, so portrait, landscape and Desktop all get it. Bottom-end,
        // over the dimmest lines. Glass, like the rest of this page's floating chrome.
        if (lyricsData.hasTiming()) {
            LyricsOffsetFloatingControl(
                offsetMs = lyricsOffsetMs,
                onOffsetChange = { sharedViewModel.setLyricsOffsetMs(it) },
                backdrop = backdrop,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp),
            ) { onClick ->
                LiquidGlassIconButton(
                    backdrop = backdrop,
                    imageVector = SimpIcons.AvTimer,
                    // A round button's rim, as on the Apple Music player's Desktop dismiss button.
                    highlight = Highlight(width = 1.dp),
                    onClick = onClick,
                )
            }
        }
    }
}

/**
 * The fullscreen lyrics page's background: a gradient from [color] down to black that drifts and
 * turns on a loop, and glides to a new [color] when the track changes.
 */
@Composable
internal fun AnimatedLyricsGradientBackground(
    color: Color,
    modifier: Modifier = Modifier,
) {
    // Animated gradient colors - SMOOTH ANIMATION
    val startColor = remember { Animatable(color) }
    val midColor1 = remember { Animatable(color.copy(alpha = 0.95f)) }
    val midColor2 = remember { Animatable(color.copy(alpha = 0.85f)) }
    val endColor = remember { Animatable(Color.Black) }

    // Dynamic gradient animation - MULTIPLE DIRECTIONS
    // Replaces the previous `while(true) { delay(16) }` loop with a Compose
    // infinite transition.
    val gradientTransition = rememberInfiniteTransition(label = "lyricsGradient")
    val animatedAngle by gradientTransition.animateFloat(
        initialValue = -45f,
        targetValue = 45f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 6000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "lyricsGradientAngle",
    )
    val animatedOffsetX by gradientTransition.animateFloat(
        initialValue = -1500f,
        targetValue = 1500f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 8000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "lyricsGradientOffsetX",
    )
    val animatedOffsetY by gradientTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 8000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "lyricsGradientOffsetY",
    )
    val gradientAngle = animatedAngle
    val gradientOffsetX = animatedOffsetX
    val gradientOffsetY = animatedOffsetY

    // Smooth color animation based on lyrics color
    LaunchedEffect(color) {
        launch {
            startColor.animateTo(
                targetValue = color,
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            )
        }
        launch {
            midColor1.animateTo(
                targetValue = color.copy(alpha = 0.95f),
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            )
        }
        launch {
            midColor2.animateTo(
                targetValue = color.copy(alpha = 0.85f),
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            )
        }
        launch {
            endColor.animateTo(
                targetValue = Color.Black,
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            )
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors =
                            listOf(
                                startColor.value,
                                midColor1.value,
                                midColor2.value,
                                endColor.value.copy(alpha = 0.9f),
                                endColor.value,
                            ),
                        start =
                            Offset(
                                x = gradientOffsetX + (cos(gradientAngle * PI.toFloat() / 180f) * 800f),
                                y = gradientOffsetY + (sin(gradientAngle * PI.toFloat() / 180f) * 800f),
                            ),
                        end =
                            Offset(
                                x = gradientOffsetX + 2500f + (cos((gradientAngle + 180f) * PI.toFloat() / 180f) * 800f),
                                y = gradientOffsetY + 2500f + (sin((gradientAngle + 180f) * PI.toFloat() / 180f) * 800f),
                            ),
                    ),
                ),
    )
}
