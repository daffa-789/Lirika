package com.music.bitchord.ui.components

import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur

/**
 * Keeps Haze's visual style while lowering its rendering fidelity for large blur areas.
 *
 * Haze 2 exposes this through [HazePerformanceMode] on [hazeBlur]. A fixed quality of a third
 * (rather than the default balanced profile) keeps the mini player and the nav bar from
 * sampling at full resolution on every frame of every scroll. The blur hides the upscale, so
 * the pixels being skipped could not be seen anyway. Call sites can override the mode.
 */
fun Modifier.optimizedHazeEffect(
    state: HazeState,
    style: HazeBlurStyle = HazeBlurStyle,
    performanceMode: HazePerformanceMode = HazePerformanceMode.Fixed(0.33f),
): Modifier = hazeBlur(
    input = HazeInput.Sources(state),
    style = style,
    performanceMode = performanceMode,
)
