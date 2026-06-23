package com.example.liquidwallpapers.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.liquidwallpapers.R
import com.example.liquidwallpapers.ui.theme.LiquidOrange

private const val LIQUID_WALLPAPERS_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.divenolabs.liquidwall"
private const val LISTRO_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.divenolabs.listro"
private const val INSTAGRAM_URL = "https://www.instagram.com/rahulshekhawatb"

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .widthIn(max = 520.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF111923).copy(alpha = 0.98f),
                            Color(0xFF070A0F).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.2f),
                            LiquidOrange.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(18.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "Liquid Wallpapers logo",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Liquid Wallpapers",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.sp
                )
                Text(
                    text = "A Diveno Labs wallpaper experience",
                    color = Color.White.copy(alpha = 0.56f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                AboutPanel(
                    title = "Premium 4K wallpapers",
                    body = "Pexels-powered discovery, Diveno Favorites, Daily Mix, mood categories, saved favorites, Text Studio, icon blur, dark-mode dimming, and Liquid Studio in one calm workspace."
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinkPanel(
                    title = "Download Liquid Wallpapers",
                    body = "Open the current Play Store listing.",
                    action = "Open on Play Store",
                    emphasized = true,
                    onClick = { uriHandler.openUri(LIQUID_WALLPAPERS_PLAY_STORE_URL) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinkPanel(
                    title = "Listro",
                    body = "A focused AI to-do app from Diveno Labs.",
                    action = "Open Listro",
                    onClick = { uriHandler.openUri(LISTRO_PLAY_STORE_URL) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                AboutPanel(
                    title = "Content posture",
                    body = "Wallpapers come from Pexels-powered discovery and Diveno-hosted favorites. Third-party images remain owned by their photographers or rights holders."
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Connect with Rahul",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { uriHandler.openUri(INSTAGRAM_URL) }
                        .padding(8.dp)
                ) {
                    InstagramLogo()
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "2026 Diveno Labs | Rahul Shekhawat",
                    color = Color.White.copy(alpha = 0.42f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
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
private fun AboutPanel(
    title: String,
    body: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.065f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(title, color = LiquidOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = body,
            color = Color.White.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodySmall,
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun LinkPanel(
    title: String,
    body: String,
    action: String,
    emphasized: Boolean = false,
    onClick: () -> Unit
) {
    val containerColor = if (emphasized) LiquidOrange.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.065f)
    val borderColor = if (emphasized) LiquidOrange.copy(alpha = 0.36f) else Color.White.copy(alpha = 0.1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            Text(body, color = Color.White.copy(alpha = 0.68f), style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(action, color = LiquidOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = LiquidOrange,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun InstagramLogo() {
    val brush = Brush.linearGradient(
        listOf(
            Color(0xFF833AB4),
            Color(0xFFFD1D1D),
            Color(0xFFF77737)
        )
    )

    Canvas(modifier = Modifier.size(32.dp)) {
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
        drawCircle(
            brush = brush,
            radius = 5.dp.toPx(),
            style = Stroke(width = 3.dp.toPx())
        )
        drawCircle(
            brush = brush,
            radius = 1.5.dp.toPx(),
            center = Offset(size.width * 0.8f, size.height * 0.2f)
        )
    }
}
