package com.example.liquidwallpapers.data.repository

import com.example.liquidwallpapers.data.local.WallpaperDao
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.data.remote.UnsplashApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WallpaperRepository @Inject constructor(
    private val api: UnsplashApi,
    private val dao: WallpaperDao
) {

    // 1. Get Founder's Collection
    suspend fun getFoundersCollection(): List<Wallpaper> {
        return try {
            api.getCollectionPhotos("n20iOQXHKFU", 1, 10)
                .filter { it.height >= 3000 }
                .map { it.toDomainModel() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 2. Search Photos (With 4K Filter)
    suspend fun searchPhotos(query: String, page: Int): List<Wallpaper> {
        return try {
            api.searchPhotos(query, page, 30, "portrait").results
                .filter { it.height >= 3000 }
                .map { it.toDomainModel() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 3. Get Editorial/Popular Photos (With 4K Filter)
    suspend fun getEditorialPhotos(page: Int): List<Wallpaper> {
        return try {
            api.getEditorialPhotos(page, 30)
                .filter { it.height >= 3000 }
                .map { it.toDomainModel() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // --- NEW: UNSPLASH TRACKING (Required for Production) ---
    // This function fires the signal to Unsplash when a user downloads/sets a wallpaper
    suspend fun trackDownload(downloadLocation: String) {
        try {
            // We just hit the URL; we don't need the response data
            api.trackDownload(downloadLocation)
        } catch (e: Exception) {
            // If tracking fails (e.g., bad internet), don't crash the app
            e.printStackTrace()
        }
    }

    // --- NEW: FAVORITES SECTION (Database Logic) ---

    // Get all favorite wallpapers (Flow updates UI automatically)
    fun getAllFavorites(): Flow<List<Wallpaper>> {
        return dao.getAllFavorites()
    }

    // Check if a specific wallpaper is favorited
    fun isFavorite(id: String): Flow<Boolean> {
        return dao.isFavorite(id)
    }

    // Save a wallpaper
    suspend fun insertFavorite(wallpaper: Wallpaper) {
        dao.insertFavorite(wallpaper)
    }

    // Remove a wallpaper
    suspend fun deleteFavorite(wallpaper: Wallpaper) {
        dao.deleteFavorite(wallpaper)
    }
}