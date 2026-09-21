package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.CoralRed

/**
 * Custom geometric flame shape with an original dual-tier silhouette and gentle flicker.
 */
@Composable
fun FlameStreakIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isActive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame_flicker")
    val flickerScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker"
    )

    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            if (!isActive) {
                // Outlined/dimmed flame
                val path = createFlamePath(w, h, 1f)
                drawPath(path, color = Color(0xFF6B7280).copy(alpha = 0.5f))
                return@Canvas
            }

            // Outer flame (Amber to Coral)
            val outerPath = createFlamePath(w, h, flickerScale)
            drawPath(
                path = outerPath,
                brush = Brush.verticalGradient(
                    colors = listOf(AmberStreak, CoralRed),
                    startY = 0f,
                    endY = h
                )
            )

            // Inner core flame (Warm Yellow/White)
            val innerPath = createFlamePath(w * 0.55f, h * 0.55f, 1f)
            drawPath(
                path = innerPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, AmberStreak),
                    startY = h * 0.35f,
                    endY = h * 0.85f
                ),
                alpha = 0.9f
            )
        }
    }
}

private fun createFlamePath(width: Float, height: Float, scale: Float): Path {
    val w = width * scale
    val h = height * scale
    val offsetX = (width - w) / 2f
    val offsetY = height - h

    return Path().apply {
        // Base point bottom center
        moveTo(offsetX + w * 0.5f, offsetY + h)
        // Left base curve
        cubicTo(
            offsetX + w * 0.15f, offsetY + h * 0.95f,
            offsetX + w * 0.05f, offsetY + h * 0.70f,
            offsetX + w * 0.15f, offsetY + h * 0.45f
        )
        // Left tip notch
        cubicTo(
            offsetX + w * 0.22f, offsetY + h * 0.35f,
            offsetX + w * 0.25f, offsetY + h * 0.32f,
            offsetX + w * 0.30f, offsetY + h * 0.38f
        )
        // Main high tip
        cubicTo(
            offsetX + w * 0.35f, offsetY + h * 0.20f,
            offsetX + w * 0.45f, offsetY + 0f,
            offsetX + w * 0.55f, offsetY + 0f
        )
        // Right slope
        cubicTo(
            offsetX + w * 0.55f, offsetY + h * 0.22f,
            offsetX + w * 0.72f, offsetY + h * 0.30f,
            offsetX + w * 0.85f, offsetY + h * 0.45f
        )
        // Right belly
        cubicTo(
            offsetX + w * 0.95f, offsetY + h * 0.65f,
            offsetX + w * 0.85f, offsetY + h * 0.95f,
            offsetX + w * 0.5f, offsetY + h
        )
        close()
    }
}
