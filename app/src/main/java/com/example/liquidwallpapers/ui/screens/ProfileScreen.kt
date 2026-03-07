package com.example.liquidwallpapers.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.imageLoader
import com.example.liquidwallpapers.ui.components.glassEffect
import com.example.liquidwallpapers.ui.screens.home.AnimatedBackground
import com.example.liquidwallpapers.ui.theme.LiquidOrange

// ─────────────────────────────────────
// AVATAR DATA
// ─────────────────────────────────────

data class DefaultAvatar(val emoji: String, val label: String)

val defaultAvatars = listOf(
    DefaultAvatar("🦊", "Fox"),
    DefaultAvatar("🐺", "Wolf"),
    DefaultAvatar("🐱", "Cat"),
    DefaultAvatar("🐼", "Panda"),
    DefaultAvatar("🦁", "Lion"),
    DefaultAvatar("🐉", "Dragon"),
    DefaultAvatar("🦋", "Butterfly"),
    DefaultAvatar("🌸", "Blossom"),
    DefaultAvatar("🔥", "Fire"),
    DefaultAvatar("🌊", "Wave"),
    DefaultAvatar("🎨", "Art"),
    DefaultAvatar("💎", "Diamond"),
    DefaultAvatar("🚀", "Rocket"),
    DefaultAvatar("⚡", "Bolt"),
    DefaultAvatar("🌙", "Moon"),
    DefaultAvatar("✨", "Sparkle"),
    DefaultAvatar("🎭", "Masks"),
    DefaultAvatar("🦄", "Unicorn"),
    DefaultAvatar("🐬", "Dolphin"),
    DefaultAvatar("🌺", "Hibiscus"),
    DefaultAvatar("🎸", "Guitar"),
    DefaultAvatar("🏔️", "Mountain"),
    DefaultAvatar("🌌", "Galaxy"),
    DefaultAvatar("🍀", "Clover"),
)

// ─────────────────────────────────────
// MAIN SCREEN
// ─────────────────────────────────────

@Composable
fun ProfileScreen(onFavoritesClick: () -> Unit = {}) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showAbout by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }

    // Persist avatar choice
    val prefs = remember { context.getSharedPreferences("profile", android.content.Context.MODE_PRIVATE) }
    var selectedAvatarIndex by remember { mutableIntStateOf(prefs.getInt("avatar_index", 0)) }

    // Entrance animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val navInsets = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    val insetsPx = navInsets.toPx()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Black, Color.Transparent),
                            startY = size.height - 200.dp.toPx() - insetsPx, // Starts fading higher up
                            endY = size.height - 80.dp.toPx() - insetsPx    // fully invisible right at top of navbar
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -40 }
            ) {
                Column {
                    Text("Profile", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Settings & preferences", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── ANIMATED AVATAR CARD ──
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 150)) + scaleIn(tween(500, delayMillis = 150), initialScale = 0.85f)
            ) {
                AnimatedProfileCard(
                    avatar = defaultAvatars[selectedAvatarIndex],
                    onAvatarClick = { showAvatarPicker = true }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section: Actions
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(tween(400, delayMillis = 300)) { 60 }
            ) {
                Column {
                    Text(
                        "ACTIONS", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                    )

                    // 1. Favorites
                    ProfileOption(Icons.Rounded.Favorite, "My Favorites", "View your saved wallpapers", LiquidOrange) {
                        onFavoritesClick()
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Clear Image Cache
                    ProfileOption(Icons.Rounded.CleaningServices, "Clear Image Cache", "Free up storage by clearing cached wallpapers", Color(0xFFFF6B35)) {
                        try {
                            context.imageLoader.memoryCache?.clear()
                            context.imageLoader.diskCache?.clear()
                            Toast.makeText(context, "Image cache cleared ✓", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to clear cache", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Share App
                    ProfileOption(Icons.Rounded.Share, "Share App", "Recommend Liquid Wallpapers to friends", Color(0xFF00BBF9)) {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Liquid Wallpapers")
                            putExtra(Intent.EXTRA_TEXT, "Check out Liquid Wallpapers — stunning 4K wallpapers with a glassmorphic design! 🎨✨")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Reset Daily Mix
                    ProfileOption(Icons.Rounded.Refresh, "Reset Daily Mix", "Re-shuffle today's wallpapers to swipe again", Color(0xFF00F5D4)) {
                        val dmPrefs = context.getSharedPreferences("daily_mix", android.content.Context.MODE_PRIVATE)
                        val todayKey = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
                        dmPrefs.edit().remove("finished_date").remove("seen_ids_$todayKey").commit()
                        Toast.makeText(context, "Daily Mix reset! Go swipe again ✓", Toast.LENGTH_SHORT).show()
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. About
                    ProfileOption(Icons.Rounded.Info, "About", "Liquid Wallpapers • Product of Diveno Labs", Color(0xFF9B5DE5)) {
                        showAbout = true
                    }

                    Spacer(modifier = Modifier.height(140.dp))
                }
            }
        }

        // ── DIALOGS ──
        if (showAbout) {
            AboutDialog(onDismiss = { showAbout = false })
        }
        if (showAvatarPicker) {
            AvatarPickerDialog(
                currentIndex = selectedAvatarIndex,
                onAvatarSelected = { index ->
                    selectedAvatarIndex = index
                    prefs.edit().putInt("avatar_index", index).apply()
                    showAvatarPicker = false
                },
                onDismiss = { showAvatarPicker = false }
            )
        }
    }
}

// ─────────────────────────────────────
// ANIMATED PROFILE CARD
// ─────────────────────────────────────

@Composable
fun AnimatedProfileCard(avatar: DefaultAvatar, onAvatarClick: () -> Unit) {
    // Rotating gradient ring
    val infiniteTransition = rememberInfiniteTransition(label = "profile_ring")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)), label = "ring_rot"
    )
    // Breathing scale
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "breath"
    )
    // Glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "glow"
    )

    val ringGradient = listOf(LiquidOrange, Color(0xFFFF006E), Color(0xFF8338EC), Color(0xFF00BBF9), LiquidOrange)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.02f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(28.dp))
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Avatar with rotating ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(breathScale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onAvatarClick() }
            ) {
                // Glow behind avatar
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(LiquidOrange.copy(alpha = glowAlpha), Color.Transparent)
                                ),
                                radius = size.width * 0.7f
                            )
                        }
                )

                // Rotating gradient ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .rotate(ringRotation)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.sweepGradient(ringGradient),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                )

                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatar.emoji,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Edit badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-2).dp, y = (-2).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(LiquidOrange)
                        .border(2.dp, Color(0xFF1A1A2E), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Edit, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Wallpaper Enthusiast",
                color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Tap avatar to customize",
                color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp
            )
        }
    }
}

// ─────────────────────────────────────
// AVATAR PICKER DIALOG
// ─────────────────────────────────────

@Composable
fun AvatarPickerDialog(
    currentIndex: Int,
    onAvatarSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1A1A2E), Color(0xFF0D0D1A))
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.15f), Color.Transparent, Color.White.copy(alpha = 0.05f))
                    ),
                    RoundedCornerShape(28.dp)
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Choose Avatar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Pick your vibe", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(20.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.heightIn(max = 360.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(defaultAvatars) { index, avatar ->
                        val isSelected = index == currentIndex
                        val borderColor = if (isSelected) LiquidOrange else Color.White.copy(alpha = 0.08f)
                        val bgColor = if (isSelected) LiquidOrange.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor)
                                .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onAvatarSelected(index) }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(avatar.emoji, fontSize = 28.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                avatar.label,
                                color = if (isSelected) LiquidOrange else Color.White.copy(alpha = 0.5f),
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────
// PROFILE OPTION ROW
// ─────────────────────────────────────

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassEffect(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        }
    }
}
