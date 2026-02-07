package com.example.liquidwallpapers.data.remote

// Import the BuildConfig file so we can read the hidden key
import com.example.liquidwallpapers.BuildConfig
import com.example.liquidwallpapers.data.remote.dto.SearchResponse
import com.example.liquidwallpapers.data.remote.dto.UnsplashPhoto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url // <--- NEW IMPORT: Required for tracking

interface UnsplashApi {

    @GET("photos")
    suspend fun getEditorialPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authHeader: String = "Client-ID ${BuildConfig.UNSPLASH_KEY}"
    ): List<UnsplashPhoto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("orientation") orientation: String = "portrait",
        @Header("Authorization") authHeader: String = "Client-ID ${BuildConfig.UNSPLASH_KEY}"
    ): SearchResponse

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authHeader: String = "Client-ID ${BuildConfig.UNSPLASH_KEY}"
    ): List<UnsplashPhoto>

    // --- NEW: Required for Unsplash Production Approval ---
    // This hits the special "download_location" URL to count the download
    @GET
    suspend fun trackDownload(
        @Url url: String,
        @Header("Authorization") authHeader: String = "Client-ID ${BuildConfig.UNSPLASH_KEY}"
    )
}