package com.example.liquidwallpapers.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.ui.components.glassEffect
import com.example.liquidwallpapers.ui.screens.home.AnimatedBackground
import com.example.liquidwallpapers.ui.screens.home.WallpaperCard
import com.example.liquidwallpapers.ui.screens.home.bounceClick
import com.example.liquidwallpapers.ui.theme.DeepBlue
import com.example.liquidwallpapers.ui.theme.LiquidOrange

@Composable
fun CategoryDetailScreen(
    categoryName: String,
    onWallpaperClick: (Wallpaper) -> Unit,
    onBack: () -> Unit,
    viewModel: CategoryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()

    // Trigger load for this category on first composition
    LaunchedEffect(categoryName) {
        viewModel.loadCategory(categoryName)
    }

    // Detect scroll near end to trigger pagination
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }.collect { info ->
            val totalItems = info.totalItemsCount
            val lastVisibleItem = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            if (totalItems > 0 && lastVisibleItem >= totalItems - 4) {
                viewModel.loadNextPage()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Spacer(modifier = Modifier.statusBarsPadding())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .glassEffect(RoundedCornerShape(100))
                        .bounceClick { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = LiquidOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = categoryName,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Wallpaper grid
            if (uiState.isLoading && uiState.wallpapers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = LiquidOrange)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 120.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.wallpapers) { wallpaper ->
                        WallpaperCard(
                            wallpaper = wallpaper,
                            onClick = { onWallpaperClick(it) }
                        )
                    }

                    // Bottom loading indicator
                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = LiquidOrange,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
