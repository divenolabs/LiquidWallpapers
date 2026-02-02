package com.example.liquidwallpapers.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color // <--- This was missing
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// We define ONLY a Dark Scheme. The app never turns white.
private val DarkColorScheme = darkColorScheme(
    primary = LiquidOrange,
    background = DeepBlue,
    surface = DeepBlue, // Using DeepBlue for surface to avoid gray cards
    onPrimary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun LiquidWallpapersTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Make the status bar (battery/time area) transparent
            window.statusBarColor = Color.Transparent.toArgb()
            // Ensure icons are white
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}