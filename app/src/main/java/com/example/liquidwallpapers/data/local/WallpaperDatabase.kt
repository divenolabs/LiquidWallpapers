package com.example.liquidwallpapers.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.liquidwallpapers.data.model.Wallpaper

@Database(entities = [Wallpaper::class], version = 1, exportSchema = false)
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun wallpaperDao(): WallpaperDao
}