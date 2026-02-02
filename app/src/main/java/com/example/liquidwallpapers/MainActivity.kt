package com.example.liquidwallpapers

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.ui.screens.details.DetailScreen
import com.example.liquidwallpapers.ui.screens.favorites.FavoritesScreen
import com.example.liquidwallpapers.ui.screens.home.HomeScreen
import com.example.liquidwallpapers.ui.theme.LiquidWallpapersTheme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiquidWallpapersTheme {
                LiquidNavigation()
            }
        }
    }
}

@Composable
fun LiquidNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // 1. Home Screen (UPDATED)
        composable("home") {
            HomeScreen(
                onWallpaperClick = { wallpaper ->
                    // Pass the wallpaper data to the next screen safely
                    val json = Uri.encode(Gson().toJson(wallpaper))
                    navController.navigate("detail/$json")
                },
                onFavoritesClick = { // <--- NEW: Navigate to Favorites
                    navController.navigate("favorites")
                }
            )
        }

        // 2. Detail Screen (Unchanged)
        composable(
            route = "detail/{wallpaperJson}",
            arguments = listOf(navArgument("wallpaperJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("wallpaperJson")
            val wallpaper = Gson().fromJson(json, Wallpaper::class.java)

            DetailScreen(
                wallpaper = wallpaper,
                onBack = { navController.popBackStack() }
            )
        }

        // 3. Favorites Screen (NEW)
        composable("favorites") {
            FavoritesScreen(
                onNavigateToDetail = { wallpaper ->
                    val json = Uri.encode(Gson().toJson(wallpaper))
                    navController.navigate("detail/$json")
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}