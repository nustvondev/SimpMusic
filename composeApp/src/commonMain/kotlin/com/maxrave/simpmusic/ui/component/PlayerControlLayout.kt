package com.maxrave.simpmusic.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maxrave.domain.mediaservice.handler.ControlState
import com.maxrave.domain.mediaservice.handler.RepeatState
import com.maxrave.simpmusic.extension.LeftToRight
import com.maxrave.simpmusic.ui.icon.Pause
import com.maxrave.simpmusic.ui.icon.PauseCircle
import com.maxrave.simpmusic.ui.icon.PlayArrow
import com.maxrave.simpmusic.ui.icon.PlayCircle
import com.maxrave.simpmusic.ui.icon.Repeat
import com.maxrave.simpmusic.ui.icon.RepeatOne
import com.maxrave.simpmusic.ui.icon.Shuffle
import com.maxrave.simpmusic.ui.icon.SimpIcons
import com.maxrave.simpmusic.ui.icon.SkipNext
import com.maxrave.simpmusic.ui.icon.SkipPrevious
import com.maxrave.simpmusic.ui.theme.seed
import com.maxrave.simpmusic.viewModel.UIEvent

@Composable
fun PlayerControlLayout(
    controllerState: ControlState,
    isSmallSize: Boolean = false,
    // Bare ▶ / ⏸ glyphs instead of the disc-enclosed PlayCircle/PauseCircle pair.
    // The desktop capsule asks for these; Now Playing keeps the discs.
    plainPlayPause: Boolean = false,
    // The capsule already pads its own edges; stacking this 20dp on top of that
    // read as a hole at both ends of the transport cluster.
    horizontalPadding: Dp = 20.dp,
    // Tint for the ACTIVE shuffle/repeat state. The default keeps the raw seed (#8ECAE6) every
    // existing call site had; the capsule passes a theme-aware colour because pastel seed on a
    // light glass surface is nearly invisible.
    activeColor: Color = seed,
    contentColor: Color = Color.White,
    onUIEvent: (UIEvent) -> Unit,
) = LeftToRight {
    // Balanced visual hierarchy: Play/Pause is primary (1.3x ratio), other controls are uniformly sized
    val height = if (isSmallSize) 44.dp else 68.dp
    val smallIcon = if (isSmallSize) 20.dp to 32.dp else 26.dp to 44.dp
    val mediumIcon = if (isSmallSize) 22.dp to 34.dp else 28.dp to 46.dp
    val bigIcon = if (isSmallSize) 28.dp to 42.dp else 38.dp to 58.dp // ~1.3x scale
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = horizontalPadding),
    ) {
        // ── 1. Shuffle Button ──
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .size(smallIcon.second)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable {
                            onUIEvent(UIEvent.Shuffle)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = controllerState.isShuffle, label = "Shuffle Button") { isShuffle ->
                    Icon(
                        imageVector = SimpIcons.Shuffle,
                        tint = if (isShuffle) activeColor else contentColor.copy(alpha = 0.85f),
                        contentDescription = if (isShuffle) "Disable Shuffle" else "Enable Shuffle",
                        modifier = Modifier.size(smallIcon.first),
                    )
                }
            }
        }
        // ── 2. Previous Track Button ──
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .size(mediumIcon.second)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable(enabled = controllerState.isPreviousAvailable) {
                            onUIEvent(UIEvent.Previous)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = SimpIcons.SkipPrevious,
                    tint = if (controllerState.isPreviousAvailable) contentColor else contentColor.copy(alpha = 0.38f),
                    contentDescription = "Previous Track",
                    modifier = Modifier.size(mediumIcon.first),
                )
            }
        }
        // ── 3. Primary Play / Pause Button (~1.3x scale) ──
        Box(Modifier.weight(1.15f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .size(bigIcon.second)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable {
                            onUIEvent(UIEvent.PlayPause)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = controllerState.isPlaying, label = "PlayPause Button") { isPlaying ->
                    Icon(
                        imageVector =
                            if (plainPlayPause) {
                                if (isPlaying) SimpIcons.Pause else SimpIcons.PlayArrow
                            } else {
                                if (isPlaying) SimpIcons.PauseCircle else SimpIcons.PlayCircle
                            },
                        tint = contentColor,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(bigIcon.first),
                    )
                }
            }
        }
        // ── 4. Next Track Button ──
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .size(mediumIcon.second)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable(enabled = controllerState.isNextAvailable) {
                            onUIEvent(UIEvent.Next)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = SimpIcons.SkipNext,
                    tint = if (controllerState.isNextAvailable) contentColor else contentColor.copy(alpha = 0.38f),
                    contentDescription = "Next Track",
                    modifier = Modifier.size(mediumIcon.first),
                )
            }
        }
        // ── 5. Repeat Button ──
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier =
                    Modifier
                        .size(smallIcon.second)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable {
                            onUIEvent(UIEvent.Repeat)
                        },
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = controllerState.repeatState, label = "Repeat Button") { rs ->
                    when (rs) {
                        is RepeatState.None -> {
                            Icon(
                                imageVector = SimpIcons.Repeat,
                                tint = contentColor.copy(alpha = 0.85f),
                                contentDescription = "Enable Repeat All",
                                modifier = Modifier.size(smallIcon.first),
                            )
                        }

                        RepeatState.All -> {
                            Icon(
                                imageVector = SimpIcons.Repeat,
                                tint = activeColor,
                                contentDescription = "Repeat One Track",
                                modifier = Modifier.size(smallIcon.first),
                            )
                        }

                        RepeatState.One -> {
                            Icon(
                                imageVector = SimpIcons.RepeatOne,
                                tint = activeColor,
                                contentDescription = "Disable Repeat",
                                modifier = Modifier.size(smallIcon.first),
                            )
                        }
                    }
                }
            }
        }
    }
}