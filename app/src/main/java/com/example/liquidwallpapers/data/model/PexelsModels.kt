package com.example.liquidwallpapers.data.model

import com.google.gson.annotations.SerializedName

// 1. The main response wrapper
data class PexelsResponse(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_results") val totalResults: Int,
    val photos: List<PexelsPhoto>
)

// 2. The photo details
data class PexelsPhoto(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerializedName("photographer_url") val photographerUrl: String,
    @SerializedName("avg_color") val avgColor: String?,
    val src: PexelsSrc,
    val alt: String?
)

// 3. The image URLs for different sizes
data class PexelsSrc(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String, // We will use this for the wallpaper
    val landscape: String,
    val tiny: String
)

// 4. Converter: Turns a Pexels Photo into your app's Wallpaper object
fun PexelsPhoto.toWallpaper(): Wallpaper {
    return Wallpaper(
        id = this.id.toString(),
        url = this.src.original, // Using original for maximum sharpness
        thumbUrl = this.src.medium, // Medium is good enough for thumbnails
        photographer = this.photographer,
        photographerUrl = this.photographerUrl,
        color = this.avgColor ?: "#000000",
        title = if (this.alt.isNullOrBlank()) "Pexels Wallpaper" else this.alt,
        downloadLocation = "" // <--- ADDED THIS (Pexels doesn't need tracking, so we leave it empty)
    )
}