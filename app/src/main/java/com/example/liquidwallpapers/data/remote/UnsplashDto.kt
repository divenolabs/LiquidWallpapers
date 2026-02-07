package com.example.liquidwallpapers.data.remote.dto

import com.example.liquidwallpapers.data.model.Wallpaper
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val id: String,
    val width: Int,
    val height: Int,
    val urls: UnsplashUrls,
    val links: UnsplashLinks, // <--- NEW: Contains the "download_location"
    val user: UnsplashUser,   // <--- NEW: Contains the photographer's name
    val color: String? = "#FA3B05",
    val description: String?,
    @SerializedName("alt_description") val altDescription: String?
) {
    fun toDomainModel(): Wallpaper {
        return Wallpaper(
            id = id,
            url = urls.regular, // Regular is fast & high enough quality for phones
            thumbUrl = urls.small,
            title = description ?: altDescription ?: "Liquid Wallpaper",
            color = color ?: "#FA3B05",

            // --- NEW MAPPINGS FOR UNSPLASH COMPLIANCE ---
            downloadLocation = links.downloadLocation, // The invisible tracking URL
            photographer = user.name,                  // The visible name
            photographerUrl = user.links.html          // The visible profile link
        )
    }
}

data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

// --- NEW DATA CLASSES ---

data class UnsplashLinks(
    @SerializedName("download_location") val downloadLocation: String // This is what we hit to count downloads
)

data class UnsplashUser(
    val name: String,
    val links: UserLinks
)

data class UserLinks(
    val html: String // This is the link to their profile (e.g., https://unsplash.com/@user)
)