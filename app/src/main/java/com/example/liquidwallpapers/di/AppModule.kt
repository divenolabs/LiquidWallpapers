package com.example.liquidwallpapers.di

import android.app.Application
import androidx.room.Room
import com.example.liquidwallpapers.data.local.WallpaperDao
import com.example.liquidwallpapers.data.local.WallpaperDatabase
import com.example.liquidwallpapers.data.remote.UnsplashApi
import com.example.liquidwallpapers.data.repository.WallpaperRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.unsplash.com/"

    @Provides
    @Singleton
    fun provideUnsplashApi(): UnsplashApi {
        // 1. Create the Logger
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. Add Logger to the Client
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        // 3. Build Retrofit with the Client
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UnsplashApi::class.java)
    }

    // --- Database Injection ---

    @Provides
    @Singleton
    fun provideDatabase(app: Application): WallpaperDatabase {
        return Room.databaseBuilder(
            app,
            WallpaperDatabase::class.java,
            "liquid_wallpapers_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWallpaperDao(db: WallpaperDatabase): WallpaperDao {
        return db.wallpaperDao()
    }

    // --- Repository Injection (FIXED) ---
    // Now we pass both API and DAO to the repository
    @Provides
    @Singleton
    fun provideRepository(api: UnsplashApi, dao: WallpaperDao): WallpaperRepository {
        return WallpaperRepository(api, dao)
    }
}