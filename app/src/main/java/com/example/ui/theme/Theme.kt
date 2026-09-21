package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = DeepIndigo,
    onPrimary = Color.White,
    primaryContainer = SurfaceVariantDark,
    onPrimaryContainer = ElectricTeal,
    secondary = ElectricTeal,
    onSecondary = CharcoalBg,
    secondaryContainer = Color(0xFF1E3839),
    onSecondaryContainer = ElectricTeal,
    tertiary = AmberStreak,
    onTertiary = CharcoalBg,
    background = CharcoalBg,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceBorderDark,
    outlineVariant = Color(0xFF384058),
    error = CoralRed,
    onError = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = DeepIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E5FF),
    onPrimaryContainer = DeepIndigo,
    secondary = ElectricTealDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F9F5),
    onSecondaryContainer = Color(0xFF00695C),
    tertiary = AmberStreak,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceBorderLight,
    outlineVariant = Color(0xFFCFD5E2),
    error = CoralRed,
    onError = Color.White
)

@Composable
fun CodeCraftTheme(
    darkTheme: Boolean = true, // Charcoal dark mode default as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CodeCraftTheme(darkTheme = darkTheme, content = content)
}

