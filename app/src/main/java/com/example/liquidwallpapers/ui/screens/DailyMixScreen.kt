package com.example.liquidwallpapers.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.ui.components.glassEffect
import com.example.liquidwallpapers.ui.screens.home.AnimatedBackground
import com.example.liquidwallpapers.ui.theme.LiquidOrange

/**
 * DAILY MIX SCREEN — Complete rewrite.
 *
 * Architecture: NO Animatable, NO coroutine-based animation.
 * - Drag offset tracked with plain mutableFloatStateOf
 * - On drag end past threshold → call ViewModel immediately (card just disappears)
 * - On button press → call ViewModel immediately (card just disappears)
 * - key(wallpaper.id) ensures fresh state per card
 *
 * This design has ZERO animation race conditions because there ARE no animations
 * that need to complete before state changes. Drag provides visual feedback,
 * dismissal is instant.
 */

@Composable
fun DailyMixScreen(
    onWallpaperClick: (Wallpaper) -> Unit = {},
    viewModel: DailyMixViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cards = uiState.cards

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text("Daily Mix", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Swipe right to favourite, left to skip", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = LiquidOrange)
                }

                cards.isEmpty() || uiState.isDeckFinished -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .glassEffect(RoundedCornerShape(32.dp))
                            .padding(40.dp)
                    ) {
                        Text(text = "🎉", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("You've seen today's mix!", color = Color.White, fontSize = 20.sp,
                            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Liked wallpapers have been saved to your favorites. Come back tomorrow for a fresh set!",
                            color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp,
                            textAlign = TextAlign.Center, lineHeight = 20.sp)
                    }
                }

                else -> {
                    // Render card stack: back cards first, top card last
                    val visibleCards = cards.take(3).reversed()
                    visibleCards.forEachIndexed { visualIndex, wallpaper ->
                        val stackIndex = visibleCards.size - 1 - visualIndex
                        val isTop = stackIndex == 0

                        key(wallpaper.id) {
                            if (isTop) {
                                TopSwipeCard(
                                    wallpaper = wallpaper,
                                    onSwipeRight = { viewModel.onSwipeRight(wallpaper) },
                                    onSwipeLeft = { viewModel.onSwipeLeft() },
                                    onTap = { onWallpaperClick(wallpaper) }
                                )
                            } else {
                                BackCard(wallpaper = wallpaper, stackIndex = stackIndex)
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 120.dp),
                        horizontalArrangement = Arrangement.spacedBy(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Skip button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .glassEffect(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.onSwipeLeft() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Close, "Skip", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(28.dp))
                        }

                        // Counter
                        Box(
                            modifier = Modifier
                                .glassEffect(RoundedCornerShape(100))
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${cards.size} left", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        // Like button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .glassEffect(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    val topCard = cards.firstOrNull()
                                    if (topCard != null) viewModel.onSwipeRight(topCard)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Favorite, "Like", tint = LiquidOrange, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────
// TOP CARD — Only this card handles gestures
// ─────────────────────────────────────

@Composable
fun TopSwipeCard(
    wallpaper: Wallpaper,
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit,
    onTap: () -> Unit
) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.toFloat()
    val swipeThreshold = screenWidth * 0.35f

    // Simple float state — no Animatable, no animation coroutines
    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }

    val dragFraction = (dragX / swipeThreshold).coerceIn(-1f, 1f)
    val likeAlpha = if (dragFraction > 0.1f) ((dragFraction - 0.1f) / 0.9f).coerceIn(0f, 1f) else 0f
    val skipAlpha = if (dragFraction < -0.1f) ((-dragFraction - 0.1f) / 0.9f).coerceIn(0f, 1f) else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .fillMaxHeight(0.55f)
            .graphicsLayer {
                translationX = dragX
                translationY = dragY
                rotationZ = dragX / 30f
                rotationY = dragFraction * -6f
                cameraDistance = 14f * density
            }
            .pointerInput(wallpaper.id) {
                detectDragGestures(
                    onDragEnd = {
                        when {
                            dragX > swipeThreshold -> onSwipeRight()
                            dragX < -swipeThreshold -> onSwipeLeft()
                            else -> { dragX = 0f; dragY = 0f }
                        }
                    },
                    onDragCancel = { dragX = 0f; dragY = 0f },
                    onDrag = { change, amount ->
                        change.consume()
                        dragX += amount.x
                        dragY += amount.y * 0.3f
                    }
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onTap() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF1A1A2E))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(wallpaper.url)
                    .crossfade(300)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = -dragX * 0.12f
                        translationY = -dragY * 0.2f
                        scaleX = 1.06f; scaleY = 1.06f
                    }
            )

            // Bottom gradient
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.85f)))))

            // LIKE label
            if (likeAlpha > 0f) {
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(20.dp)
                        .graphicsLayer { alpha = likeAlpha }
                        .background(Color(0xFF4CAF50).copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) { Text("♥ LIKE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }

            // SKIP label
            if (skipAlpha > 0f) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(20.dp)
                        .graphicsLayer { alpha = skipAlpha }
                        .background(Color(0xFFF44336).copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) { Text("SKIP ✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }

            Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Text(wallpaper.photographer, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Tap to preview • Swipe to decide", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }
        }
    }
}

// ─────────────────────────────────────
// BACK CARD — Static, no gestures
// ─────────────────────────────────────

@Composable
fun BackCard(wallpaper: Wallpaper, stackIndex: Int) {
    val context = LocalContext.current
    val s = 1f - (stackIndex * 0.05f)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .fillMaxHeight(0.55f)
            .graphicsLayer {
                scaleX = s; scaleY = s
                translationY = stackIndex * 30f
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF1A1A2E))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(wallpaper.url)
                    .crossfade(300)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
