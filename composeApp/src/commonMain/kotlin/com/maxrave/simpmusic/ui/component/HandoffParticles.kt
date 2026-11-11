package com.maxrave.simpmusic.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private const val FIELDS_PER_PARTICLE = 12
private const val OFF_ACTIVE = 0 // 1.0f if active, 0.0f if inactive
private const val OFF_PROGRESS = 1 // 0.0f .. 1.0f
private const val OFF_LIFETIME = 2 // lifetime in ms
private const val OFF_ELAPSED = 3 // elapsed ms
private const val OFF_START_X = 4 // start X px
private const val OFF_END_X = 5 // end X px
private const val OFF_START_Y = 6 // start Y px
private const val OFF_END_Y = 7 // end Y px
private const val OFF_SINE_AMP = 8 // sine arc amplitude (px)
private const val OFF_RADIUS = 9 // particle radius (px)
private const val OFF_MAX_ALPHA = 10 // peak alpha (0.6f .. 0.95f)
private const val OFF_TWINKLE_SPEED = 11 // shimmer rate

private const val SPAWN_INTERVAL_MS = 105f // ~9.5 spawns/sec -> ~14-16 steady active particles

/**
 * High-performance, zero-allocation particle state container for the Handoff transition.
 * Uses a single contiguous [FloatArray] to store all particle properties.
 */
internal class HandoffParticlePool(
    private val maxParticles: Int = PARTICLE_MAX,
) {
    val state = FloatArray(maxParticles * FIELDS_PER_PARTICLE)
    var activeCount by mutableIntStateOf(0)
        private set

    private var timeSinceLastSpawnMs = 0f

    /**
     * Updates particles by [dtMs] delta time. Spawns new particles if [isSpawningAllowed].
     */
    fun update(
        dtMs: Float,
        isSpawningAllowed: Boolean,
        metrics: ArtworkLayoutMetrics,
        boxWidth: Float,
        boxHeight: Float,
    ) {
        val boxCenterX = boxWidth / 2f
        val boxCenterY = boxHeight / 2f
        val effectiveScale = metrics.effectiveHandoffScale
        val coverWidth = boxWidth * effectiveScale
        val coverHeight = boxHeight * effectiveScale
        val gapPx = metrics.slotOffsetPx * 2f - coverWidth

        // Spawning: only when allowed and below max capacity
        if (isSpawningAllowed) {
            timeSinceLastSpawnMs += dtMs
            while (timeSinceLastSpawnMs >= SPAWN_INTERVAL_MS) {
                timeSinceLastSpawnMs -= SPAWN_INTERVAL_MS
                if (activeCount < maxParticles) {
                    spawnOne(
                        boxCenterX = boxCenterX,
                        boxCenterY = boxCenterY,
                        coverWidth = coverWidth,
                        coverHeight = coverHeight,
                        gapPx = gapPx,
                    )
                }
            }
        } else {
            timeSinceLastSpawnMs = 0f
        }

        // Update active particles
        var currentActive = 0
        for (i in 0 until maxParticles) {
            val base = i * FIELDS_PER_PARTICLE
            if (state[base + OFF_ACTIVE] == 1f) {
                val elapsed = state[base + OFF_ELAPSED] + dtMs
                state[base + OFF_ELAPSED] = elapsed
                val lifetime = state[base + OFF_LIFETIME]
                val progress = elapsed / lifetime

                if (progress >= 1f) {
                    // Particle died
                    state[base + OFF_ACTIVE] = 0f
                } else {
                    state[base + OFF_PROGRESS] = progress
                    currentActive++
                }
            }
        }
        activeCount = currentActive
    }

    private fun spawnOne(
        boxCenterX: Float,
        boxCenterY: Float,
        coverWidth: Float,
        coverHeight: Float,
        gapPx: Float,
    ) {
        // Find an inactive slot
        for (i in 0 until maxParticles) {
            val base = i * FIELDS_PER_PARTICLE
            if (state[base + OFF_ACTIVE] == 0f) {
                state[base + OFF_ACTIVE] = 1f
                state[base + OFF_ELAPSED] = 0f
                state[base + OFF_PROGRESS] = 0f
                state[base + OFF_LIFETIME] = (PARTICLE_LIFETIME_MIN_MS + Random.nextFloat() * (PARTICLE_LIFETIME_MAX_MS - PARTICLE_LIFETIME_MIN_MS)).toFloat()

                // Spawn near right rim of outgoing cover, flow toward left rim of incoming cover
                val rightRimX = boxCenterX - gapPx / 2f
                val leftRimX = boxCenterX + gapPx / 2f
                state[base + OFF_START_X] = rightRimX - Random.nextFloat() * (coverWidth * 0.12f)
                state[base + OFF_END_X] = leftRimX + Random.nextFloat() * (coverWidth * 0.12f)

                // Vertical distribution across the center 70% of the artwork height
                val startY = boxCenterY + (Random.nextFloat() - 0.5f) * (coverHeight * 0.70f)
                state[base + OFF_START_Y] = startY
                state[base + OFF_END_Y] = startY + (Random.nextFloat() - 0.5f) * (coverHeight * 0.20f)

                // Gentle sine arc (positive or negative curve)
                val ampDir = if (Random.nextBoolean()) 1f else -1f
                state[base + OFF_SINE_AMP] = ampDir * (10f + Random.nextFloat() * 24f)

                // Radius: 1.5px .. 3.2px
                state[base + OFF_RADIUS] = 1.5f + Random.nextFloat() * 1.7f

                // Max alpha: 0.65 .. 0.95
                state[base + OFF_MAX_ALPHA] = 0.65f + Random.nextFloat() * 0.30f

                // Shimmer rate
                state[base + OFF_TWINKLE_SPEED] = 0.007f + Random.nextFloat() * 0.012f

                activeCount++
                return
            }
        }
    }

    /**
     * Draws all active particles in a single draw phase. Zero heap allocations.
     */
    fun draw(drawScope: DrawScope) {
        val pi = PI.toFloat()
        for (i in 0 until maxParticles) {
            val base = i * FIELDS_PER_PARTICLE
            if (state[base + OFF_ACTIVE] == 1f) {
                val progress = state[base + OFF_PROGRESS]
                val startX = state[base + OFF_START_X]
                val endX = state[base + OFF_END_X]
                val startY = state[base + OFF_START_Y]
                val endY = state[base + OFF_END_Y]
                val sineAmp = state[base + OFF_SINE_AMP]
                val radius = state[base + OFF_RADIUS]
                val maxAlpha = state[base + OFF_MAX_ALPHA]
                val twinkleSpeed = state[base + OFF_TWINKLE_SPEED]
                val elapsed = state[base + OFF_ELAPSED]

                // Position interpolation with sine arc
                val currentX = startX + (endX - startX) * progress
                val arcY = sineAmp * sin(progress * pi)
                val currentY = startY + (endY - startY) * progress + arcY

                // Smooth alpha envelope: 20% fade-in, 60% sustained with twinkle, 20% fade-out
                val fadeIn = (progress / 0.20f).coerceIn(0f, 1f)
                val fadeOut = ((1f - progress) / 0.20f).coerceIn(0f, 1f)
                val envelope = minOf(fadeIn, fadeOut)
                val twinkle = 0.85f + 0.15f * sin(elapsed * twinkleSpeed)
                val alpha = (maxAlpha * envelope * twinkle).coerceIn(0f, 1f)

                if (alpha > 0.01f) {
                    val center = Offset(currentX, currentY)
                    // Contrasting dark halo for readability on bright/white backgrounds
                    drawScope.drawCircle(
                        color = Color.Black.copy(alpha = alpha * 0.25f),
                        radius = radius * 1.8f,
                        center = center,
                    )
                    // Luminous pearlescent white core
                    drawScope.drawCircle(
                        color = Color(0xFF, 0xFD, 0xF6).copy(alpha = alpha),
                        radius = radius,
                        center = center,
                    )
                }
            }
        }
    }

    fun reset() {
        state.fill(0f)
        activeCount = 0
        timeSinceLastSpawnMs = 0f
    }
}

/**
 * Dedicated particle canvas composable.
 * Driven by [withFrameNanos] inside a single [LaunchedEffect] frame loop.
 * Runs ONLY when [isRunning] is true. In Idle or Carousel, the frame loop is suspended.
 */
@Composable
internal fun HandoffParticleCanvas(
    pool: HandoffParticlePool,
    metrics: ArtworkLayoutMetrics,
    boxSizePx: Float,
    isSpawningAllowed: Boolean,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
) {
    var frameClock by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isRunning, isSpawningAllowed) {
        if (!isRunning) return@LaunchedEffect

        var lastFrameNanos = 0L
        while (isRunning) {
            withFrameNanos { frameTimeNanos ->
                if (lastFrameNanos > 0L) {
                    val dtMs = ((frameTimeNanos - lastFrameNanos) / 1_000_000f).coerceIn(1f, 50f)
                    pool.update(
                        dtMs = dtMs,
                        isSpawningAllowed = isSpawningAllowed,
                        metrics = metrics,
                        boxWidth = boxSizePx,
                        boxHeight = boxSizePx,
                    )
                }
                lastFrameNanos = frameTimeNanos
                frameClock = frameTimeNanos
            }
        }
    }

    Canvas(modifier = modifier) {
        // Reading frameClock subscribes this DrawScope to per-frame draw invalidations without recomposing
        if (frameClock >= 0L) {
            pool.draw(this)
        }
    }
}
