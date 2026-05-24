package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = UpcRed,
    secondary = GrayMedium,
    tertiary = UpcRedDark,
    background = DarkBackground,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextMajorDark,
    onSurface = TextMajorDark,
    surfaceVariant = GrayDark,
    onSurfaceVariant = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = UpcRed,
    secondary = GrayMedium,
    tertiary = UpcRedLight,
    background = LightBackground,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextMajorLight,
    onSurface = TextMajorLight,
    surfaceVariant = GrayLight,
    onSurfaceVariant = TextMajorLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to ensure UPC visual identity
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
