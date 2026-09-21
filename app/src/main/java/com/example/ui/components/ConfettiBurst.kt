package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.AmberStreak
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val angle: Float,
    val speed: Float,
    val color: Color,
    val isSquare: Boolean,
    val size: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiBurst(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {}
) {
    if (!trigger) return

    val particles = remember {
        val colors = listOf(DeepIndigo, ElectricTeal, LimeGreen, AmberStreak, Color.White)
        List(40) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 450f + 250f
            Particle(
                angle = angle,
                speed = speed,
                color = colors[Random.nextInt(colors.size)],
                isSquare = Random.nextBoolean(),
                size = Random.nextFloat() * 10f + 8f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f
            )
        }
    }

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
        onComplete()
    }

    val progress = animProgress.value
    if (progress < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height * 0.45f
            val alpha = (1f - progress).coerceIn(0f, 1f)

            particles.forEach { p ->
                val distance = p.speed * progress
                // gravity pull
                val gravity = progress * progress * 300f
                val px = cx + cos(p.angle) * distance
                val py = cy + sin(p.angle) * distance + gravity

                rotate(degrees = p.rotationSpeed * progress, pivot = Offset(px, py)) {
                    if (p.isSquare) {
                        // Small rounded square
                        drawRoundRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(px - p.size / 2, py - p.size / 2),
                            size = Size(p.size, p.size),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                    } else {
                        // Dash particle
                        drawRoundRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(px - p.size, py - p.size * 0.35f),
                            size = Size(p.size * 2f, p.size * 0.7f),
                            cornerRadius = CornerRadius(2.5f, 2.5f)
                        )
                    }
                }
            }
        }
    }
}
