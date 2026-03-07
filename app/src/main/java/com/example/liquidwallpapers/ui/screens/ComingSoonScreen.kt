package com.example.liquidwallpapers.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.liquidwallpapers.ui.theme.DeepBlue
import com.example.liquidwallpapers.ui.theme.LiquidOrange

@Composable
fun ComingSoonScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title Coming Soon",
            color = LiquidOrange,
            fontWeight = FontWeight.Bold
        )
    }
}
