package com.example.liquidwallpapers.data.repository

import android.util.Log
import com.example.liquidwallpapers.BuildConfig
import com.example.liquidwallpapers.data.local.WallpaperDao
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.data.model.toWallpaper // Pexels Mapper
import com.example.liquidwallpapers.data.remote.PexelsApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

class WallpaperRepository @Inject constructor(
    private val pexelsApi: PexelsApi,
    private val dao: WallpaperDao
) {

    // 1. Get Founder's Collection (FROM GITHUB NOW)
    suspend fun getFoundersCollection(): List<Wallpaper> {
        return withContext(Dispatchers.IO) {
            try {
                // Your Raw GitHub Link
                val url = URL("https://raw.githubusercontent.com/divenolabs/liquid-wallpapers-assets/refs/heads/main/founders.json")
                val jsonString = url.readText()

                // Parse safely using Gson
                val type = object : TypeToken<List<Map<String, String>>>() {}.type
                val rawList: List<Map<String, String>> = Gson().fromJson(jsonString, type)

                // Map to our Wallpaper object
                rawList.map { item ->
                    Wallpaper(
                        id = item["id"] ?: "0",
                        url = item["url"] ?: "",
                        thumbUrl = item["url"] ?: "", // Use main URL as thumb
                        photographer = item["photographer"] ?: "Diveno Labs",
                        photographerUrl = item["photographerUrl"] ?: "",
                        color = "#000000", // Default black background
                        title = item["title"] ?: "Founder's Choice",
                        downloadLocation = "" // No tracking for GitHub
                    )
                }
            } catch (e: Exception) {
                Log.e("Repo", "GitHub Load Failed: ${e.message}")
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // 2. Search Photos (Pexels Only)
    suspend fun searchPhotos(query: String, page: Int): List<Wallpaper> {
        return try {
            val pexelsResponse = pexelsApi.searchWallpapers(
                apiKey = BuildConfig.PEXELS_API_KEY,
                query = query,
                page = page,
                perPage = 30
            )

            pexelsResponse.photos
                .filter { it.height >= 3000 }
                .map { it.toWallpaper() }
        } catch (e: Exception) {
            Log.e("Repo", "Pexels Search failed: ${e.message}")
            emptyList()
        }
    }

    // --- FAVORITES SECTION ---
    fun getAllFavorites(): Flow<List<Wallpaper>> = dao.getAllFavorites()
    fun isFavorite(id: String): Flow<Boolean> = dao.isFavorite(id)
    suspend fun insertFavorite(wallpaper: Wallpaper) = dao.insertFavorite(wallpaper)
    suspend fun deleteFavorite(wallpaper: Wallpaper) = dao.deleteFavorite(wallpaper)
}