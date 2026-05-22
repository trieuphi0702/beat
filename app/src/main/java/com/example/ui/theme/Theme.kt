package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPink,
    tertiary = NeonLime,
    background = DarkBackground,
    surface = StudioCardColor,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = LightSlate
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Consistently enforce beautiful high-performance dark theme
    dynamicColor: Boolean = false, // Disable dynamic colors to protect the intentional custom neon DJ palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
