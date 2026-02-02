package com.example.liquidwallpapers.data.remote.dto

import com.example.liquidwallpapers.data.model.Wallpaper
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val id: String,
    val width: Int,   // <--- Added: To check Resolution
    val height: Int,  // <--- Added: To check Resolution
    val urls: UnsplashUrls,
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
            color = color ?: "#FA3B05"
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