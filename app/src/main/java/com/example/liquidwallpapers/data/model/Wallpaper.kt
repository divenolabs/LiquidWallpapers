package com.example.liquidwallpapers.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Wallpaper(
    @PrimaryKey val id: String,
    val url: String,          // High quality image
    val thumbUrl: String,     // Thumbnail
    val title: String,
    val color: String,        // The glow color
    val category: String = "General"
)