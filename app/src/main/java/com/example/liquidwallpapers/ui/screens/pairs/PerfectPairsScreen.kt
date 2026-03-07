package com.example.liquidwallpapers.ui.screens.pairs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liquidwallpapers.ui.components.glassEffect
import com.example.liquidwallpapers.ui.theme.LiquidOrange
import kotlin.math.absoluteValue

// ─────────────────────────────────────
// DATA MODEL
// ─────────────────────────────────────

data class WallpaperPair(
    val id: String,
    val title: String,
    val subtitle: String,
    val lockColor: Color,
    val homeColor: Color,
    val accentColor: Color
)

val dummyPairs = listOf(
    WallpaperPair("1", "Midnight Ocean", "Deep blues for focus", Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF415A77)),
    WallpaperPair("2", "Solar Flare", "Warm energy burst", Color(0xFFFA3B05), Color(0xFF2E0E02), Color(0xFFFF6B35)),
    WallpaperPair("3", "Northern Lights", "Arctic glow", Color(0xFF00F5D4), Color(0xFF0B132B), Color(0xFF00BBF9)),
    WallpaperPair("4", "Cyber Bloom", "Neon garden", Color(0xFF9B5DE5), Color(0xFF10002B), Color(0xFFFF006E)),
    WallpaperPair("5", "Desert Sand", "Calm earth tones", Color(0xFFC9A87C), Color(0xFF3D2B1F), Color(0xFFD4B896))
)

// ─────────────────────────────────────
// MAIN SCREEN
// ─────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PerfectPairsScreen() {
    val pagerState = rememberPagerState(pageCount = { dummyPairs.size })
    val currentPair = dummyPairs.getOrElse(pagerState.currentPage) { dummyPairs[0] }

    // Smooth ambient background color transitions
    val ambientStart by animateColorAsState(
        currentPair.lockColor.copy(alpha = 0.3f), tween(600), label = "amb_s"
    )
    val ambientEnd by animateColorAsState(
        currentPair.homeColor.copy(alpha = 0.2f), tween(600), label = "amb_e"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(ambientStart, Color(0xFF050510), ambientEnd)
                )
            )
    ) {
        // Header
        PairsTopBar()

        // Vertical pager
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 100.dp),
            pageSpacing = 24.dp
        ) { page ->
            val pair = dummyPairs[page]
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

            PairPage(
                pair = pair,
                pageOffset = pageOffset
            )
        }

        // Page indicator dots
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            dummyPairs.forEachIndexed { index, _ ->
                val isActive = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(if (isActive) 20.dp else 8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isActive) LiquidOrange
                            else Color.White.copy(alpha = 0.2f)
                        )
                )
            }
        }

        // Bottom control hub
        PairsControlHub(pair = currentPair)
    }
}

// ─────────────────────────────────────
// TOP BAR
// ─────────────────────────────────────

@Composable
fun PairsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Perfect Pairs", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Lock + Home, curated together", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
        Icon(
            Icons.Rounded.PhoneAndroid, null,
            tint = LiquidOrange.copy(alpha = 0.8f),
            modifier = Modifier.size(28.dp)
        )
    }
}

// ─────────────────────────────────────
// PAIR PAGE — Contains Two Phone Mockups
// ─────────────────────────────────────

@Composable
fun PairPage(
    pair: WallpaperPair,
    pageOffset: Float
) {
    val scale = 1f - (pageOffset * 0.15f).coerceIn(0f, 0.3f)
    val alpha = 1f - (pageOffset * 0.4f).coerceIn(0f, 0.6f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lock Screen mockup
            PhoneMockup(
                backgroundColor = pair.lockColor,
                label = "Lock Screen"
            ) {
                LockScreenOverlay(accentColor = pair.accentColor)
            }

            // Home Screen mockup
            PhoneMockup(
                backgroundColor = pair.homeColor,
                label = "Home Screen"
            ) {
                HomeScreenOverlay(accentColor = pair.accentColor)
            }
        }
    }
}

// ─────────────────────────────────────
// PHONE MOCKUP — Reusable Device Frame
// ─────────────────────────────────────

@Composable
fun PhoneMockup(
    backgroundColor: Color,
    label: String,
    content: @Composable BoxScope.() -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(140.dp)
                .aspectRatio(9f / 19.5f)
                .clip(RoundedCornerShape(24.dp))
                .background(backgroundColor)
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
        ) {
            // Subtle inner gradient for depth
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.15f)
                            )
                        )
                    )
            )

            // Dynamic island / notch
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .width(36.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            content()
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            label,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────
// LOCK SCREEN OVERLAY
// ─────────────────────────────────────

@Composable
fun BoxScope.LockScreenOverlay(accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Lock icon
        Icon(
            Icons.Rounded.Lock, null,
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Digital clock
        Text(
            "9:41",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraLight,
            letterSpacing = 2.sp
        )

        // Date
        Text(
            "Friday, March 7",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom flashlight / camera circles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                )
            }

            // Home indicator bar
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.4f))
                    .align(Alignment.Bottom)
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                )
            }
        }
    }
}

// ─────────────────────────────────────
// HOME SCREEN OVERLAY
// ─────────────────────────────────────

@Composable
fun BoxScope.HomeScreenOverlay(accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Search widget pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Search, null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Search", color = Color.White.copy(alpha = 0.3f), fontSize = 8.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App grid (3x2 tiny circles)
        repeat(2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Dock — 4 app icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (it == 0) accentColor.copy(alpha = 0.5f)
                            else Color.White.copy(alpha = 0.15f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Home indicator
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.4f))
        )
    }
}

// ─────────────────────────────────────
// BOTTOM CONTROL HUB
// ─────────────────────────────────────

@Composable
fun BoxScope.PairsControlHub(pair: WallpaperPair) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val accentAnimated by animateColorAsState(pair.accentColor, tween(500), label = "btn_accent")
    var isApplied by remember { mutableStateOf(false) }
    val scope = kotlinx.coroutines.MainScope()

    // Auto-revert after showing confirmation
    LaunchedEffect(isApplied) {
        if (isApplied) {
            kotlinx.coroutines.delay(1500)
            isApplied = false
        }
    }

    val buttonBg by animateColorAsState(
        targetValue = if (isApplied) accentAnimated else Color.Transparent,
        animationSpec = tween(300), label = "btn_bg"
    )

    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 120.dp)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pair title
        Text(
            pair.title,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            pair.subtitle,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Premium "Apply Pair" button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .drawBehind {
                    drawRoundRect(
                        color = accentAnimated.copy(alpha = 0.25f),
                        cornerRadius = CornerRadius(28.dp.toPx()),
                        size = Size(size.width + 12.dp.toPx(), size.height + 12.dp.toPx()),
                        topLeft = Offset(-6.dp.toPx(), -6.dp.toPx())
                    )
                }
                .clip(RoundedCornerShape(28.dp))
                .background(buttonBg)
                .then(
                    if (!isApplied) Modifier.glassEffect(RoundedCornerShape(28.dp))
                    else Modifier
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (!isApplied) {
                        isApplied = true
                        android.widget.Toast.makeText(
                            context,
                            "\"${pair.title}\" pair applied!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isApplied) {
                    Text(
                        "✓  Applied!",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        Icons.Rounded.Wallpaper, null,
                        tint = accentAnimated,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Apply Pair",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
