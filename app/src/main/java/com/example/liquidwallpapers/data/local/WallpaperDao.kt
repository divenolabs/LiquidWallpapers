package com.example.liquidwallpapers.data.local

import androidx.room.*
import com.example.liquidwallpapers.data.model.Wallpaper
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {

    // Get all favorites (Flow updates UI automatically when data changes)
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<Wallpaper>>

    // Check if a specific wallpaper is favorited
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    // Add to favorites
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(wallpaper: Wallpaper)

    // Remove from favorites
    @Delete
    suspend fun deleteFavorite(wallpaper: Wallpaper)
}