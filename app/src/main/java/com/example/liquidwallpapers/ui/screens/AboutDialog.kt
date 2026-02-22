package com.example.liquidwallpapers.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.liquidwallpapers.ui.theme.LiquidOrange

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()
    val instaUrl = "https://www.instagram.com/rahulshekhawatb"

    Dialog(onDismissRequest = onDismiss) {
        // Main dialog box with glass effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Content Column
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- HEADER ---
                Text(
                    text = "DIVENO LABS",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                )
                Text(
                    text = "Psychology. Strategy. Design.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontStyle = FontStyle.Italic
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- ABOUT ---
                SectionHeader("About Liquid Wallpapers")
                Text(
                    text = "We believe your screen influences your mind. Liquid Wallpapers curates the finest wallpapers to bring clarity, fluidity, and focus to your daily digital experience.",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- PROJECTS ---
                SectionHeader("Our Projects")
                Text(
                    text = "Building the future of style & tech:",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Project 1: THE NAVRA (Hyperlink)
                val navraText = buildAnnotatedString {
                    append("• ")
                    pushStringAnnotation(tag = "URL", annotation = "https://www.thenavra.com")
                    withStyle(style = SpanStyle(color = LiquidOrange, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("THE NAVRA")
                    }
                    pop()
                    append(": Redefining Women's Ethnic Clothing")
                }

                ClickableText(
                    text = navraText,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)),
                    onClick = { offset ->
                        navraText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                uriHandler.openUri(annotation.item)
                            }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Project 2: ZeroCoBuild
                Text(
                    text = "• ZeroCoBuild: AI-Powered Virtual Photo shoot for brands and Wholesalers.",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- THE FUTURE ---
                SectionHeader("The Future")
                Text(
                    text = "\"Evolution is coming in Wallpapers App. Stay tuned for exciting features in coming updates\"",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // --- FOOTER ---
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                // --- SOCIALS (Instagram) ---
                Text(
                    text = "Connect with me",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // INSTAGRAM ICON BUTTON
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { uriHandler.openUri(instaUrl) }
                        .padding(8.dp)
                ) {
                    InstagramLogo()
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Solving my own problems.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "© 2026 Diveno Labs | Rahul Shekhawat",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Close Button moved to Top-Right
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = LiquidOrange,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// --- CUSTOM INSTAGRAM ICON ---
@Composable
fun InstagramLogo() {
    val instaGradient = listOf(
        Color(0xFF833AB4), // Purple
        Color(0xFFFD1D1D), // Red/Pink
        Color(0xFFF77737)  // Orange
    )
    val brush = Brush.linearGradient(colors = instaGradient)

    Canvas(modifier = Modifier.size(32.dp)) {
        // 1. Outer Rounded Rectangle
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
        // 2. Inner Circle
        drawCircle(
            brush = brush,
            radius = 5.dp.toPx(),
            style = Stroke(width = 3.dp.toPx())
        )
        // 3. The small dot
        drawCircle(
            brush = brush,
            radius = 1.5.dp.toPx(),
            center = Offset(size.width * 0.8f, size.height * 0.2f)
        )
    }
}