package com.example.liquidwallpapers.data.repository

import com.example.liquidwallpapers.data.model.PexelsPhoto
import com.example.liquidwallpapers.data.model.Wallpaper

object WallpaperContentFilter {
    private val blockedPhotographers = listOf(
        "mr jordan singh",
        "jordan singh"
    )

    private val blockedPhotographerUrls = listOf(
        "pexels.com/@mr-jordan-singh-1336369015"
    )

    private val blockedTerms = listOf(
        "love",
        "couple",
        "romantic",
        "romance",
        "girl",
        "boy",
        "model",
        "fashion",
        "bedroom",
        "date",
        "dating",
        "kiss",
        "kissing",
        "body",
        "portrait",
        "people",
        "person",
        "human",
        "face",
        "man",
        "woman",
        "male",
        "female",
        "bride",
        "groom",
        "wedding",
        "sensual",
        "intimate",
        "sexy",
        "nude",
        "lingerie",
        "bikini",
        "underwear"
    )

    private val blockedPattern = Regex(
        pattern = "\\b(${blockedTerms.joinToString("|") { Regex.escape(it) }})\\b",
        option = RegexOption.IGNORE_CASE
    )

    private val blockedPhotographerPattern = Regex(
        pattern = "\\b(${blockedPhotographers.joinToString("|") { Regex.escape(it) }})\\b",
        option = RegexOption.IGNORE_CASE
    )

    fun safeSearchQuery(query: String): String {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return SAFE_DEFAULT_QUERY

        return if (blockedPattern.containsMatchIn(cleanQuery)) {
            SAFE_DEFAULT_QUERY
        } else {
            cleanQuery
        }
    }

    fun isAllowed(photo: PexelsPhoto): Boolean {
        val searchableText = listOfNotNull(
            photo.alt,
            photo.url,
            photo.photographer,
            photo.photographerUrl
        ).joinToString(" ")

        return !blockedPattern.containsMatchIn(searchableText) &&
            !blockedPhotographerPattern.containsMatchIn(photo.photographer) &&
            !isBlockedPhotographerUrl(photo.photographerUrl)
    }

    fun isAllowed(wallpaper: Wallpaper): Boolean {
        val searchableText = listOf(
            wallpaper.title,
            wallpaper.url,
            wallpaper.thumbUrl,
            wallpaper.photographer,
            wallpaper.photographerUrl
        ).joinToString(" ")

        return !blockedPattern.containsMatchIn(searchableText) &&
            !blockedPhotographerPattern.containsMatchIn(wallpaper.photographer) &&
            !isBlockedPhotographerUrl(wallpaper.photographerUrl)
    }

    private fun isBlockedPhotographerUrl(url: String): Boolean {
        return blockedPhotographerUrls.any { blockedUrl ->
            url.contains(blockedUrl, ignoreCase = true)
        }
    }

    private const val SAFE_DEFAULT_QUERY = "abstract nature mobile wallpaper"
}
