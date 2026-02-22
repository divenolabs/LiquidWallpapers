package com.example.liquidwallpapers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.ui.screens.TextEditorScreen
import com.example.liquidwallpapers.ui.screens.details.DetailScreen
import com.example.liquidwallpapers.ui.screens.favorites.FavoritesScreen
import com.example.liquidwallpapers.ui.screens.home.HomeScreen
import com.example.liquidwallpapers.ui.theme.LiquidWallpapersTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val UPDATE_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Set the App Theme (Fixes status bars/colors)
        setTheme(R.style.Theme_LiquidWallpapers)

        super.onCreate(savedInstanceState)

        // 2. Setup Updates
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdates()

        setContent {
            LiquidWallpapersTheme {
                // 3. Run the Seamless Splash
                SeamlessSplash()
            }
        }
    }

    // --- SEAMLESS SPLASH SCREEN ---
    // This keeps the image visible for 1.5 seconds so it doesn't "flash" away.
    @Composable
    fun SeamlessSplash() {
        var showSplash by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            delay(1500) // Adjust this number: 1500 = 1.5 seconds
            showSplash = false
        }

        if (showSplash) {
            // Shows your splash image again to bridge the gap
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_bg), // Ensure this matches your file name
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Show the App
            LiquidNavigation()
        }
    }

    // --- NAVIGATION GRAPH ---
    @Composable
    fun LiquidNavigation() {
        val navController = rememberNavController()
        val gson = Gson()

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    onWallpaperClick = { wallpaper ->
                        val json = Uri.encode(gson.toJson(wallpaper))
                        navController.navigate("detail/$json")
                    },
                    onFavoritesClick = { navController.navigate("favorites") }
                )
            }
            composable(
                route = "detail/{wallpaperJson}",
                arguments = listOf(navArgument("wallpaperJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("wallpaperJson")
                val wallpaper = gson.fromJson(json, Wallpaper::class.java)
                DetailScreen(wallpaper = wallpaper, navController = navController)
            }
            composable("favorites") {
                FavoritesScreen(
                    onNavigateToDetail = { wallpaper ->
                        val json = Uri.encode(gson.toJson(wallpaper))
                        navController.navigate("detail/$json")
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "text_editor/{imageUrl}",
                arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                TextEditorScreen(navController = navController, imageUrl = imageUrl)
            }
        }
    }

    // --- UPDATE LOGIC ---
    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE && resultCode != RESULT_OK) {
            Toast.makeText(this, "Update is required.", Toast.LENGTH_SHORT).show()
            checkForUpdates()
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE
                )
            }
        }
    }
}