package com.example.liquidwallpapers.data.remote

import com.example.liquidwallpapers.data.model.PexelsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApi {

    // 1. Get "Curated" (Best/Trending) photos
    @GET("v1/curated")
    suspend fun getCuratedWallpapers(
        @Header("Authorization") apiKey: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): PexelsResponse

    // 2. Search for photos
    @GET("v1/search")
    suspend fun searchWallpapers(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): PexelsResponse
}