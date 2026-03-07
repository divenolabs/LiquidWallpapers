package com.example.liquidwallpapers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// --- THE GLASS STYLE ENGINE ---
// Premium Real-Glass Frosted Look

@Composable
fun Modifier.glassEffect(shape: RoundedCornerShape = RoundedCornerShape(24.dp)): Modifier {
    return this
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.25f), // Top: Brighter Specular Highlight
                    Color.White.copy(alpha = 0.08f)  // Bottom: Deep frosted shadow
                )
            )
        )
        .border(
            width = 1.5.dp, // Thicker more prominent glass edge
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.6f), // Intense top-left light catch
                    Color.White.copy(alpha = 0.1f),
                    Color.Transparent,
                    Color.White.copy(alpha = 0.2f)  // Bottom-right reflection
                )
            ),
            shape = shape
        )
}