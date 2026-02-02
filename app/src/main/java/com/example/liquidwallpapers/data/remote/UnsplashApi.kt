package com.example.liquidwallpapers.data.remote

import com.example.liquidwallpapers.data.remote.dto.SearchResponse
import com.example.liquidwallpapers.data.remote.dto.UnsplashPhoto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// ----------------------------------------------------------------
// YOUR ACCESS KEY
// ----------------------------------------------------------------
private const val CLIENT_ID = "lVYx6N5DJ9i3gcatrbMv6rVgHKk84uTXoK08q1iOo_Q"
// ----------------------------------------------------------------

interface UnsplashApi {

    @GET("photos")
    suspend fun getEditorialPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authHeader: String = "Client-ID $CLIENT_ID"
    ): List<UnsplashPhoto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("orientation") orientation: String = "portrait", // <--- Added: Forces Portrait Mode
        @Header("Authorization") authHeader: String = "Client-ID $CLIENT_ID"
    ): SearchResponse

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authHeader: String = "Client-ID $CLIENT_ID"
    ): List<UnsplashPhoto>
}