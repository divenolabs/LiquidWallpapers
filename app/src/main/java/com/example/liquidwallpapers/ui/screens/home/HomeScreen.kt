package com.example.liquidwallpapers.ui.screens.home

import android.graphics.Color.parseColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.ui.components.glassEffect
import com.example.liquidwallpapers.ui.screens.AboutDialog
import com.example.liquidwallpapers.ui.theme.DeepBlue
import com.example.liquidwallpapers.ui.theme.LiquidOrange

// --- UPDATED CATEGORIES LIST ---
val CATEGORIES = listOf(
    "All", // Required to reset the filter
    "Nature", "Minimal", "Dark Wallpapers", "Space", "Cities",
    "Sci-Fi", "Mountains", "Automotives", "Flowers", "Animals", "Neon"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onWallpaperClick: (Wallpaper) -> Unit,
    onFavoritesClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var isVisible by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    // --- ROOT CONTAINER ---
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. THE NEW ANIMATED BACKGROUND (Sits behind everything)
        AnimatedBackground()

        // 2. THE MAIN CONTENT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(600)) { 100 }
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // TOP HEADER (Your Buttons with Glass Effect)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Search Bar
                            Box(modifier = Modifier.weight(1f)) {
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

                            // Favorites
                            GlassIconButton(
                                icon = Icons.Rounded.Favorite,
                                tint = LiquidOrange,
                                onClick = onFavoritesClick
                            )

                            // About
                            GlassIconButton(
                                icon = Icons.Rounded.Info,
                                tint = Color.White.copy(alpha = 0.8f),
                                onClick = { showAbout = true }
                            )
                        }
                    }

                    // Founder's Choice
                    if (uiState.foundersWallpapers.isNotEmpty() && uiState.searchQuery.isBlank()) {
                        item {
                            Column {
                                Text(
                                    text = "Founder's Choice",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LiquidOrange,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                FoundersRow(
                                    wallpapers = uiState.foundersWallpapers,
                                    onClick = onWallpaperClick
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    // Sticky Categories
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            CategoryRow(
                                selectedCategory = uiState.searchQuery,
                                onCategoryClick = { category ->
                                    val query = if (category == "All") "" else category
                                    viewModel.searchWallpapers(query)
                                }
                            )
                        }
                    }

                    // Wallpapers Grid
                    val chunkedWallpapers = uiState.wallpapers.chunked(2)
                    items(chunkedWallpapers) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
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

                        if (uiState.wallpapers.isNotEmpty() && rowItems == chunkedWallpapers.last()) {
                            LaunchedEffect(Unit) {
                                viewModel.loadNextPage()
                            }
                        }
                    }
                }
            }

            // Loading Indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 200.dp)
                        .size(40.dp)
                        .glassEffect(RoundedCornerShape(100)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = LiquidOrange, strokeWidth = 3.dp, modifier = Modifier.size(20.dp))
                }
            }

            if (showAbout) {
                AboutDialog(onDismiss = { showAbout = false })
            }
        }
    }
}

// --- ANIMATED BACKGROUND COMPONENT ---
@Composable
fun AnimatedBackground() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp.value * 3 // Convert roughly to pixels
    val screenWidth = configuration.screenWidthDp.dp.value * 3

    val infiniteTransition = rememberInfiniteTransition(label = "background")

    // Blob 1: Orange (Top Left)
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "blob1"
    )

    // Blob 2: Purple (Bottom Right)
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "blob2"
    )

    // Blob 3: Cyan (Middle Left)
    val offset3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "blob3"
    )

    Canvas(modifier = Modifier.fillMaxSize().background(DeepBlue)) {
        // Blob 1: Orange
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(LiquidOrange.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(x = size.width * 0.2f + (100 * offset1), y = size.height * 0.2f + (50 * offset1)),
                radius = size.width * 0.8f
            ),
            radius = size.width * 0.8f,
            center = Offset(x = size.width * 0.2f, y = size.height * 0.2f)
        )

        // Blob 2: Purple/Blue
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF6200EA).copy(alpha = 0.4f), Color.Transparent),
                center = Offset(x = size.width * 0.8f - (100 * offset2), y = size.height * 0.8f - (100 * offset2)),
                radius = size.width * 0.9f
            ),
            radius = size.width * 0.9f,
            center = Offset(x = size.width * 0.8f, y = size.height * 0.8f)
        )

        // Blob 3: Cyan/Teal
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Cyan.copy(alpha = 0.25f), Color.Transparent),
                center = Offset(x = size.width * 0.1f + (200 * offset3), y = size.height * 0.5f + (100 * offset3)),
                radius = size.width * 0.7f
            ),
            radius = size.width * 0.7f,
            center = Offset(x = size.width * 0.1f, y = size.height * 0.5f)
        )
    }
}

// --- COMPONENTS ---

@Composable
fun GlassIconButton(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .glassEffect(RoundedCornerShape(100))
            .bounceClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
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
            .glassEffect(RoundedCornerShape(100))
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text("Search...", color = Color.White.copy(alpha = 0.5f)) },
            leadingIcon = {
                Crossfade(targetState = isCategoryActive, label = "icon") { isActive ->
                    if (isActive) {
                        IconButton(onClick = { text = ""; onBackClick() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = LiquidOrange)
                        }
                    } else {
                        Icon(Icons.Rounded.Search, "Search", tint = Color.White.copy(alpha = 0.7f))
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
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(wallpapers) { wallpaper ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(140.dp)
                    .height(200.dp)
                    .bounceClick { onClick(wallpaper) }
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(colors = listOf(LiquidOrange, Color.Transparent)),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = DeepBlue.copy(alpha = 0.5f)) // More transparent
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = wallpaper.thumbUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .glassEffect(RoundedCornerShape(100)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Star, null, tint = LiquidOrange, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(selectedCategory: String, onCategoryClick: (String) -> Unit) {
    // 1. Create a state for the list scroll position
    val listState = rememberLazyListState()

    // 2. Watch for changes in 'selectedCategory'
    LaunchedEffect(selectedCategory) {
        // Find which index matches the selected category name
        val index = if (selectedCategory.isBlank()) {
            0 // "All" is index 0
        } else {
            CATEGORIES.indexOfFirst { it.equals(selectedCategory, ignoreCase = true) }
        }

        // If found, scroll to it!
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    LazyRow(
        state = listState, // 3. Attach the state here
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(CATEGORIES) { category ->
            val isSelected = if (category == "All") selectedCategory.isBlank() else selectedCategory.equals(category, ignoreCase = true)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .bounceClick { onCategoryClick(category) }
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                brush = Brush.linearGradient(colors = listOf(LiquidOrange, Color(0xFFB02A02)))
                            )
                        } else {
                            Modifier.glassEffect(RoundedCornerShape(100))
                        }
                    )
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category,
                    color = Color.White,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun WallpaperCard(wallpaper: Wallpaper, onClick: (Wallpaper) -> Unit) {
    val glowColor = try { Color(parseColor(wallpaper.color)) } catch (e: Exception) { LiquidOrange }
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .bounceClick { onClick(wallpaper) }
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = DeepBlue.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = wallpaper.thumbUrl,
                contentDescription = wallpaper.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 300f
                    )
                )
            )
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp).size(8.dp).clip(CircleShape).background(glowColor)
            )
        }
    }
}

// --- ANIMATION MODIFIER ---
fun Modifier.bounceClick(scaleDown: Float = 0.95f, onClick: () -> Unit) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "bounce"
    )
    this.graphicsLayer { scaleX = scale; scaleY = scale }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                onTap = { onClick() }
            )
        }
}