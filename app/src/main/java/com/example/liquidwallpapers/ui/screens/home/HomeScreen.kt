package com.example.liquidwallpapers.ui.screens.home

import android.graphics.Color.parseColor
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.ui.components.glassEffect
import com.example.liquidwallpapers.ui.theme.DeepBlue
import com.example.liquidwallpapers.ui.theme.LiquidOrange
import java.util.Calendar

private data class MoodFilter(
    val label: String,
    val query: String,
    val accent: Color
)

private val HomeSidePadding = 12.dp
private val HomeSectionGap = 10.dp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onWallpaperClick: (Wallpaper) -> Unit,
    scrollToTopTrigger: Int = 0
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val navInsets = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val dailyDrop = remember(uiState.foundersWallpapers, uiState.wallpapers) {
        pickDailyDrop(
            foundersWallpapers = uiState.foundersWallpapers,
            feedWallpapers = uiState.wallpapers
        )
    }
    val visibleWallpapers = remember(uiState.wallpapers, dailyDrop) {
        dailyDrop?.let { drop -> uiState.wallpapers.filterNot { it.id == drop.id } } ?: uiState.wallpapers
    }
    val moods = remember {
        listOf(
            MoodFilter("AMOLED", "dark amoled abstract wallpaper", Color(0xFF00F5D4)),
            MoodFilter("Nature", "misty forest mountain wallpaper", Color(0xFF7BD88F)),
            MoodFilter("Neon", "neon city night wallpaper", Color(0xFFFF4FD8)),
            MoodFilter("Calm", "calm ocean minimal wallpaper", Color(0xFF78A8FF)),
            MoodFilter("Space", "space nebula wallpaper", Color(0xFFFFB703))
        )
    }

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            listState.animateScrollToItem(0)
        }
    }

    BackHandler(enabled = uiState.searchQuery.isNotBlank()) {
        viewModel.searchWallpapers("")
        focusManager.clearFocus()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        EditorialBackground()

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(450)) + slideInVertically(tween(650)) { 80 }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        val insetsPx = navInsets.toPx()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Black, Color.Transparent),
                                startY = size.height - 220.dp.toPx() - insetsPx,
                                endY = size.height - 72.dp.toPx() - insetsPx
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    },
                state = listState,
                contentPadding = PaddingValues(bottom = 144.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(start = HomeSidePadding, top = 18.dp, end = HomeSidePadding),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        HomeHeader()

                        LiquidSearchBar(
                            query = uiState.searchQuery,
                            isCategoryActive = uiState.searchQuery.isNotBlank(),
                            onSearch = { query ->
                                viewModel.searchWallpapers(query)
                                focusManager.clearFocus()
                            },
                            onBackClick = {
                                viewModel.searchWallpapers("")
                                focusManager.clearFocus()
                            }
                        )
                    }
                }

                if (uiState.searchQuery.isBlank()) {
                    dailyDrop?.let { feature ->
                        item {
                            TodayDropCard(
                                wallpaper = feature,
                                onClick = { onWallpaperClick(feature) }
                            )
                        }
                    }

                    item {
                        MoodRail(
                            moods = moods,
                            onMoodClick = { mood ->
                                viewModel.searchWallpapers(mood.query)
                                focusManager.clearFocus()
                            }
                        )
                    }

                    if (uiState.foundersWallpapers.isNotEmpty()) {
                        item {
                            HomeSection {
                                SectionTitle(
                                    title = "Diveno Picks",
                                    detail = "${uiState.foundersWallpapers.size} selected"
                                )
                                FoundersRow(
                                    wallpapers = uiState.foundersWallpapers,
                                    onClick = onWallpaperClick
                                )
                            }
                        }
                    }
                } else {
                    item {
                        SectionTitle(
                            title = uiState.searchQuery,
                            detail = "Search"
                        )
                    }
                }

                val chunkedWallpapers = visibleWallpapers.chunked(2)

                item {
                    HomeSection {
                        SectionTitle(
                            title = if (uiState.searchQuery.isBlank()) "Fresh Wall" else "Results",
                            detail = "${visibleWallpapers.size} shown"
                        )
                        chunkedWallpapers.firstOrNull()?.let { rowItems ->
                            WallpaperGridRow(
                                rowItems = rowItems,
                                onWallpaperClick = onWallpaperClick
                            )
                        }
                    }

                    if (chunkedWallpapers.size == 1) {
                        LaunchedEffect(uiState.searchQuery, visibleWallpapers.size) {
                            viewModel.loadNextPage()
                        }
                    }
                }

                itemsIndexed(chunkedWallpapers.drop(1)) { offset, rowItems ->
                    val index = offset + 1
                    WallpaperGridRow(
                        rowItems = rowItems,
                        onWallpaperClick = onWallpaperClick
                    )

                    if (index == chunkedWallpapers.lastIndex) {
                        LaunchedEffect(index, uiState.searchQuery, visibleWallpapers.size) {
                            viewModel.loadNextPage()
                        }
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 116.dp)
                    .size(42.dp)
                    .glassEffect(RoundedCornerShape(100)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = LiquidOrange,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeSection(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(HomeSectionGap),
        content = content
    )
}

@Composable
private fun WallpaperGridRow(
    rowItems: List<Wallpaper>,
    onWallpaperClick: (Wallpaper) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HomeSidePadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            WallpaperCard(rowItems[0], onWallpaperClick)
        }
        Box(modifier = Modifier.weight(1f)) {
            if (rowItems.size > 1) {
                WallpaperCard(rowItems[1], onWallpaperClick)
            }
        }
    }
}

private fun pickDailyDrop(
    foundersWallpapers: List<Wallpaper>,
    feedWallpapers: List<Wallpaper>
): Wallpaper? {
    val source = foundersWallpapers.ifEmpty { feedWallpapers }
    if (source.isEmpty()) return null

    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return source[dayOfYear % source.size]
}

@Composable
fun AnimatedBackground() {
    EditorialBackground()
}

@Composable
private fun EditorialBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "editorial-background")
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )
    val breath by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF05080D),
                        Color(0xFF071018),
                        Color(0xFF0A151B),
                        Color(0xFF10131F)
                    )
                )
            )
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(34.dp)
        ) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF0B2A24).copy(alpha = 0.28f + breath * 0.07f),
                        Color.Transparent
                    ),
                    startY = size.height * 0.08f,
                    endY = size.height * 0.92f
                )
            )

            drawPath(
                path = Path().apply {
                    moveTo(-size.width * 0.2f, size.height * (0.16f + drift * 0.04f))
                    cubicTo(
                        size.width * 0.18f,
                        size.height * (0.02f + breath * 0.06f),
                        size.width * 0.52f,
                        size.height * (0.38f - drift * 0.04f),
                        size.width * 1.22f,
                        size.height * (0.17f + breath * 0.06f)
                    )
                },
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0xFF00F5D4).copy(alpha = 0.08f),
                        Color(0xFF7BD88F).copy(alpha = 0.15f),
                        Color.Transparent
                    )
                ),
                style = Stroke(width = size.width * 0.56f)
            )

            drawPath(
                path = Path().apply {
                    moveTo(-size.width * 0.14f, size.height * (0.7f - breath * 0.04f))
                    cubicTo(
                        size.width * 0.24f,
                        size.height * (0.5f + drift * 0.05f),
                        size.width * 0.62f,
                        size.height * (0.92f - breath * 0.05f),
                        size.width * 1.16f,
                        size.height * (0.6f + drift * 0.04f)
                    )
                },
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        LiquidOrange.copy(alpha = 0.04f),
                        Color(0xFF78A8FF).copy(alpha = 0.11f),
                        Color.Transparent
                    )
                ),
                style = Stroke(width = size.width * 0.62f)
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.08f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.36f)
                    )
                )
            )
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Liquid Wallpapers",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.sp
            )
            Text(
                text = "Curated high quality wallpapers",
                color = Color.White.copy(alpha = 0.58f),
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 0.sp
            )
        }
    }
}

@Composable
private fun TodayDropCard(
    wallpaper: Wallpaper,
    onClick: () -> Unit
) {
    val glowColor = wallpaper.safeAccent()

    Box(
        modifier = Modifier
            .padding(horizontal = HomeSidePadding)
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.36f), glowColor.copy(alpha = 0.28f), Color.Transparent)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .bounceClick { onClick() }
    ) {
        AsyncImage(
            model = wallpaper.url,
            contentDescription = wallpaper.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.08f),
                            Color.Black.copy(alpha = 0.18f),
                            Color.Black.copy(alpha = 0.82f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .clip(RoundedCornerShape(100))
                .background(Color.Black.copy(alpha = 0.42f))
                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(100))
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = glowColor, modifier = Modifier.size(15.dp))
                Text(
                    text = "Today's Drop",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = wallpaper.title.ifBlank { "Featured Wallpaper" },
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 31.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 0.sp
            )
            Text(
                text = wallpaper.photographer,
                color = Color.White.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Rounded.AutoAwesome, null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text("Open", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MoodRail(
    moods: List<MoodFilter>,
    onMoodClick: (MoodFilter) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = HomeSidePadding),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(moods) { mood ->
            Row(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(100))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, mood.accent.copy(alpha = 0.38f), RoundedCornerShape(100))
                    .bounceClick(scaleDown = 0.97f) { onMoodClick(mood) }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(mood.accent)
                )
                Text(
                    text = mood.label,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    detail: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HomeSidePadding),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = detail,
            color = Color.White.copy(alpha = 0.48f),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1
        )
    }
}

@Composable
fun GlassIconButton(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .glassEffect(RoundedCornerShape(100))
            .bounceClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(23.dp)
        )
    }
}

@Composable
fun LiquidSearchBar(
    query: String,
    isCategoryActive: Boolean,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var text by remember { mutableStateOf(query) }
    LaunchedEffect(query) { text = query }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.09f))
            .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(8.dp))
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("Search wallpapers", color = Color.White.copy(alpha = 0.52f)) },
            leadingIcon = {
                Crossfade(targetState = isCategoryActive, label = "search-icon") { isActive ->
                    if (isActive) {
                        IconButton(onClick = { text = ""; onBackClick() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = LiquidOrange)
                        }
                    } else {
                        Icon(Icons.Rounded.Search, "Search", tint = Color.White.copy(alpha = 0.72f))
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = LiquidOrange,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(text) })
        )
    }
}

@Composable
fun FoundersRow(wallpapers: List<Wallpaper>, onClick: (Wallpaper) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = HomeSidePadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(wallpapers) { wallpaper ->
            val accent = wallpaper.safeAccent()
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(148.dp)
                    .height(210.dp)
                    .bounceClick { onClick(wallpaper) }
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(colors = listOf(accent.copy(alpha = 0.9f), Color.Transparent)),
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = DeepBlue.copy(alpha = 0.55f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = wallpaper.thumbUrl,
                        contentDescription = wallpaper.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )
                    IconBadge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(9.dp),
                        icon = Icons.Rounded.Star,
                        tint = LiquidOrange
                    )
                    Text(
                        text = wallpaper.photographer,
                        color = Color.White.copy(alpha = 0.76f),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WallpaperCard(wallpaper: Wallpaper, onClick: (Wallpaper) -> Unit) {
    val glowColor = wallpaper.safeAccent()
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.68f)
            .bounceClick { onClick(wallpaper) }
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.24f), Color.Transparent)),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = DeepBlue.copy(alpha = 0.52f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = wallpaper.thumbUrl,
                contentDescription = wallpaper.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.74f)),
                            startY = 260f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(11.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(glowColor)
            )
        }
    }
}

@Composable
private fun BoxScope.IconBadge(
    modifier: Modifier,
    icon: ImageVector,
    tint: Color
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.42f))
            .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(15.dp))
    }
}

private fun Wallpaper.safeAccent(): Color {
    return try {
        Color(parseColor(color))
    } catch (_: Exception) {
        LiquidOrange
    }
}

fun Modifier.bounceClick(scaleDown: Float = 0.95f, onClick: () -> Unit) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "bounce"
    )
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = { onClick() }
            )
        }
}
