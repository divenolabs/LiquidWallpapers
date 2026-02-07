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
    val category: String = "General",

    // --- NEW FIELDS REQUIRED FOR UNSPLASH COMPLIANCE ---
    val downloadLocation: String, // The tracking URL (Invisible)
    val photographer: String,     // "Photo by [Name]" (Visible)
    val photographerUrl: String   // Link to their profile (Visible)
)