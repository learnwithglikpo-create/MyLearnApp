package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.AmberStreak

enum class MascotMood {
    HAPPY,
    CHEERING,
    THINKING,
    HINT,
    WAVING
}

/**
 * Byte - The original CodeCraft mascot.
 * A friendly rounded-cube bot with two bright dot eyes and a single glowing antenna LED.
 */
@Composable
fun ByteMascot(
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    mood: MascotMood = MascotMood.HAPPY
) {
    val infiniteTransition = rememberInfiniteTransition(label = "byte_anim")
    
    // Subtle breathing/bobbing
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    // Antenna LED pulsing glow
    val ledGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "led_glow"
    )

    // Eye blinking
    val eyeScaleY by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3500
                1f at 0
                1f at 3100
                0.1f at 3200
                1f at 3300
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cy = h * 0.52f + bobOffset

            // 1. Antenna Pole
            drawLine(
                color = Color(0xFF6B7280),
                start = Offset(w * 0.5f, cy - h * 0.28f),
                end = Offset(w * 0.5f, cy - h * 0.42f),
                strokeWidth = w * 0.045f
            )

            // 2. Antenna LED Node
            val ledColor = when (mood) {
                MascotMood.HINT -> AmberStreak
                MascotMood.CHEERING -> LimeGreen
                MascotMood.THINKING -> Color(0xFF8172FF)
                else -> ElectricTeal
            }
            // Glow halo
            drawCircle(
                color = ledColor.copy(alpha = 0.35f * ledGlow),
                radius = w * 0.12f * ledGlow,
                center = Offset(w * 0.5f, cy - h * 0.43f)
            )
            // Core LED bulb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, ledColor),
                    center = Offset(w * 0.5f, cy - h * 0.43f),
                    radius = w * 0.06f
                ),
                radius = w * 0.065f,
                center = Offset(w * 0.5f, cy - h * 0.43f)
            )

            // 3. Rounded Cube Head Body
            val headWidth = w * 0.76f
            val headHeight = h * 0.56f
            val headLeft = (w - headWidth) / 2f
            val headTop = cy - headHeight / 2f

            // Outer robot chassis gradient (Dark indigo-slate metal)
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF2C3246), Color(0xFF1E2232)),
                    start = Offset(headLeft, headTop),
                    end = Offset(headLeft + headWidth, headTop + headHeight)
                ),
                topLeft = Offset(headLeft, headTop),
                size = Size(headWidth, headHeight),
                cornerRadius = CornerRadius(w * 0.18f, w * 0.18f)
            )
            // Chassis rim accent
            drawRoundRect(
                color = ElectricTeal.copy(alpha = 0.3f),
                topLeft = Offset(headLeft, headTop),
                size = Size(headWidth, headHeight),
                cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
                style = Stroke(width = w * 0.02f)
            )

            // 4. Dark Visor Screen (inner rounded rectangle)
            val visorWidth = headWidth * 0.82f
            val visorHeight = headHeight * 0.65f
            val visorLeft = (w - visorWidth) / 2f
            val visorTop = headTop + headHeight * 0.18f

            drawRoundRect(
                color = Color(0xFF0F1118),
                topLeft = Offset(visorLeft, visorTop),
                size = Size(visorWidth, visorHeight),
                cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
            )

            // 5. Friendly Dot Eyes
            val eyeRadius = w * 0.065f
            val eyeSpacing = visorWidth * 0.28f
            val leftEyeCenter = Offset(w * 0.5f - eyeSpacing, visorTop + visorHeight * 0.48f)
            val rightEyeCenter = Offset(w * 0.5f + eyeSpacing, visorTop + visorHeight * 0.48f)

            val eyeColor = when (mood) {
                MascotMood.HINT -> AmberStreak
                MascotMood.CHEERING -> LimeGreen
                else -> ElectricTeal
            }

            if (mood == MascotMood.CHEERING) {
                // Happy arc eyes ^ ^
                val arcPathLeft = Path().apply {
                    moveTo(leftEyeCenter.x - eyeRadius, leftEyeCenter.y + eyeRadius * 0.4f)
                    quadraticTo(
                        leftEyeCenter.x, leftEyeCenter.y - eyeRadius * 1.2f,
                        leftEyeCenter.x + eyeRadius, leftEyeCenter.y + eyeRadius * 0.4f
                    )
                }
                drawPath(arcPathLeft, color = eyeColor, style = Stroke(width = w * 0.035f))

                val arcPathRight = Path().apply {
                    moveTo(rightEyeCenter.x - eyeRadius, rightEyeCenter.y + eyeRadius * 0.4f)
                    quadraticTo(
                        rightEyeCenter.x, rightEyeCenter.y - eyeRadius * 1.2f,
                        rightEyeCenter.x + eyeRadius, rightEyeCenter.y + eyeRadius * 0.4f
                    )
                }
                drawPath(arcPathRight, color = eyeColor, style = Stroke(width = w * 0.035f))
            } else {
                // Oval dot eyes with scale
                val actualEyeHeight = eyeRadius * 1.25f * eyeScaleY
                drawOval(
                    color = eyeColor,
                    topLeft = Offset(leftEyeCenter.x - eyeRadius, leftEyeCenter.y - actualEyeHeight / 2),
                    size = Size(eyeRadius * 2, actualEyeHeight)
                )
                drawOval(
                    color = eyeColor,
                    topLeft = Offset(rightEyeCenter.x - eyeRadius, rightEyeCenter.y - actualEyeHeight / 2),
                    size = Size(eyeRadius * 2, actualEyeHeight)
                )

                // White eye specular reflections
                if (eyeScaleY > 0.5f) {
                    drawCircle(
                        color = Color.White,
                        radius = eyeRadius * 0.35f,
                        center = Offset(leftEyeCenter.x + eyeRadius * 0.25f, leftEyeCenter.y - actualEyeHeight * 0.2f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = eyeRadius * 0.35f,
                        center = Offset(rightEyeCenter.x + eyeRadius * 0.25f, rightEyeCenter.y - actualEyeHeight * 0.2f)
                    )
                }
            }

            // 6. Cute Circuit Cheek Accents
            drawCircle(
                color = ElectricTeal.copy(alpha = 0.25f),
                radius = eyeRadius * 0.5f,
                center = Offset(leftEyeCenter.x - eyeRadius * 0.9f, visorTop + visorHeight * 0.78f)
            )
            drawCircle(
                color = ElectricTeal.copy(alpha = 0.25f),
                radius = eyeRadius * 0.5f,
                center = Offset(rightEyeCenter.x + eyeRadius * 0.9f, visorTop + visorHeight * 0.78f)
            )
        }
    }
}
