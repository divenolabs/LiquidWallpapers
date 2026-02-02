package com.example.liquidwallpapers.data.repository

import com.example.liquidwallpapers.data.local.WallpaperDao
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.data.remote.UnsplashApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WallpaperRepository @Inject constructor(
    private val api: UnsplashApi,
    private val dao: WallpaperDao // <--- Added: Connects to the Database
) {

    // 1. Get Founder's Collection
    suspend fun getFoundersCollection(): List<Wallpaper> {
        return try {
            // We filter this too, just in case a low-res image slipped into your collection
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
                .filter { it.height >= 3000 } // <--- THE FILTER (Must be taller than 3000px)
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
                .filter { it.height >= 3000 } // <--- THE FILTER
                .map { it.toDomainModel() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
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