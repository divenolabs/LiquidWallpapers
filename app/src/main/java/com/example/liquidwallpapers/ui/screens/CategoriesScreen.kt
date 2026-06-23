package com.example.liquidwallpapers.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import kotlin.math.absoluteValue
import com.example.liquidwallpapers.ui.components.glassEffect
import com.example.liquidwallpapers.ui.screens.home.bounceClick
import com.example.liquidwallpapers.ui.theme.DeepBlue

data class CategoryPortal(
    val id: String,
    val name: String,
    val dominantColor: Color,
    val imageUrl: String
)

// High-impact, no-people category covers chosen to feel like wallpaper art.
val liquidCategories = listOf(
    CategoryPortal(
        "10", "Liquid Glass", Color(0xFF00E5FF),
        "https://images.pexels.com/photos/29355930/pexels-photo-29355930.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "11", "3D Shapes", Color(0xFF7C4DFF),
        "https://images.pexels.com/photos/33797646/pexels-photo-33797646.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "12", "Gradient Flow", Color(0xFFFF2E88),
        "https://images.pexels.com/photos/6985132/pexels-photo-6985132.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "13", "Flower Macro", Color(0xFF1E88FF),
        "https://images.pexels.com/photos/10621654/pexels-photo-10621654.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "14", "Sunset Glow", Color(0xFFFF6B35),
        "https://images.pexels.com/photos/13869913/pexels-photo-13869913.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "15", "Coastal Blue", Color(0xFF00C2D1),
        "https://images.pexels.com/photos/28607786/pexels-photo-28607786.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "1", "Abstract Art", Color(0xFF7C4DFF),
        "https://images.pexels.com/photos/7120460/pexels-photo-7120460.png?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "2", "Amoled Dark", Color(0xFF111111),
        "https://images.pexels.com/photos/20818845/pexels-photo-20818845.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "3", "Deep Space", Color(0xFF2F5BFF),
        "https://images.pexels.com/photos/998641/pexels-photo-998641.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "4", "Cyberpunk", Color(0xFFFF4D00),
        "https://images.pexels.com/photos/36521494/pexels-photo-36521494.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "5", "Nature HD", Color(0xFF00A86B),
        "https://images.pexels.com/photos/17640525/pexels-photo-17640525.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "6", "Minimalist", Color(0xFF4CB3FF),
        "https://images.pexels.com/photos/34133564/pexels-photo-34133564.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "7", "Aesthetics", Color(0xFFFFD166),
        "https://images.pexels.com/photos/29355930/pexels-photo-29355930.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "8", "Automotive", Color(0xFFFFC400),
        "https://images.pexels.com/photos/30849327/pexels-photo-30849327.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    ),
    CategoryPortal(
        "9", "Mountains", Color(0xFF00D084),
        "https://images.pexels.com/photos/35815154/pexels-photo-35815154.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen(
    onCategoryClick: (String) -> Unit = {}
) {
    val listState = rememberLazyListState()
    var centeredItemIndex by remember { mutableIntStateOf(0) }

    // Track centered item for background color interpolation
    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            if (info.visibleItemsInfo.isEmpty()) return@snapshotFlow 0

            val center = info.viewportStartOffset + (info.viewportEndOffset - info.viewportStartOffset) / 2
            var bestIndex = 0
            var minDist = Int.MAX_VALUE

            info.visibleItemsInfo.forEach { item ->
                val itemCenter = item.offset + item.size / 2
                val dist = (center - itemCenter).absoluteValue
                if (dist < minDist) {
                    minDist = dist
                    bestIndex = item.index
                }
            }
            bestIndex
        }.collect { centeredItemIndex = it }
    }

    val currentCategory = liquidCategories.getOrNull(centeredItemIndex) ?: liquidCategories.first()

    // Mix the dominant color with our DeepBlue root background for ambient lighting effect
    val targetBgColor = Color(
        red = (currentCategory.dominantColor.red * 0.2f + DeepBlue.red * 0.80f),
        green = (currentCategory.dominantColor.green * 0.2f + DeepBlue.green * 0.80f),
        blue = (currentCategory.dominantColor.blue * 0.2f + DeepBlue.blue * 0.80f),
        alpha = 1f
    )

    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(600),
        label = "ambient_bg"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBgColor)
    ) {
        val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        val navInsets = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(top = 180.dp, bottom = 200.dp, start = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
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
        ) {
            items(
                count = liquidCategories.size,
                key = { liquidCategories[it].id }
            ) { index ->
                CategoryLiquidCard(
                    category = liquidCategories[index],
                    index = index,
                    listState = listState,
                    onClick = { onCategoryClick(liquidCategories[index].name) }
                )
            }
        }

        // Header - Drawn last to be on top, with gradient to mask scrolling items behind it
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            animatedBgColor,
                            animatedBgColor,
                            animatedBgColor.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 12.dp)
                .padding(top = 20.dp, bottom = 32.dp)
        ) {
            Text("Categories", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Browse by mood and aesthetic", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
    }
}

@Composable
fun CategoryLiquidCard(
    category: CategoryPortal,
    index: Int,
    listState: LazyListState,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .bounceClick { onClick() }
            .graphicsLayer {
                val layoutInfo = listState.layoutInfo
                val viewportCenter =
                    layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
                val itemInfo =
                    layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }

                if (itemInfo != null) {
                    val itemCenter = itemInfo.offset + itemInfo.size / 2
                    val distance = (viewportCenter - itemCenter).toFloat()
                    val maxDistance = 600f
                    val fraction =
                        (distance.absoluteValue / maxDistance).coerceIn(0f, 1f)

                    // Scale: 1.0 -> 0.88
                    val s = 1f - (0.12f * fraction)
                    scaleX = s
                    scaleY = s

                    // Dim: 1.0 -> 0.45 alpha
                    alpha = 1f - (0.55f * fraction)
                }
            }
    ) {
        // Card body with clipping
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF1A1A2E))
        ) {
            // Parallax image via Coil with aggressive caching
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(category.imageUrl)
                    .crossfade(400)
                    .size(Size.ORIGINAL)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Overscale to hide parallax gaps
                        scaleX = 1.3f
                        scaleY = 1.3f

                        val layoutInfo = listState.layoutInfo
                        val viewportCenter =
                            layoutInfo.viewportStartOffset + (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
                        val itemInfo =
                            layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }

                        if (itemInfo != null) {
                            val itemCenter = itemInfo.offset + itemInfo.size / 2
                            val distance =
                                (viewportCenter - itemCenter).toFloat()
                            // Parallax shift
                            translationY = distance * 0.35f
                        }
                    }
            )

            // Bottom gradient for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Glassmorphic floating category label
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
                    .glassEffect(RoundedCornerShape(100))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
