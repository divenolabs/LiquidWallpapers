package com.example.liquidwallpapers.di

import android.app.Application
import androidx.room.Room
import com.example.liquidwallpapers.data.local.WallpaperDao
import com.example.liquidwallpapers.data.local.WallpaperDatabase
import com.example.liquidwallpapers.data.remote.PexelsApi
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

    // --- Unsplash API ---
    @Provides
    @Singleton
    fun provideUnsplashApi(): UnsplashApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UnsplashApi::class.java)
    }

    // --- Pexels API ---
    @Provides
    @Singleton
    fun providePexelsApi(): PexelsApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PexelsApi::class.java)
    }

    // --- Database Injection ---
    @Provides
    @Singleton
    fun provideDatabase(app: Application): WallpaperDatabase {
        return Room.databaseBuilder(
            app,
            WallpaperDatabase::class.java,
            "liquid_wallpapers_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWallpaperDao(db: WallpaperDatabase): WallpaperDao {
        return db.wallpaperDao()
    }

    // --- Repository Injection (FIXED) ---
    @Provides
    @Singleton
    fun provideRepository(
        unsplashApi: UnsplashApi,
        pexelsApi: PexelsApi,      // <--- Added PexelsApi
        dao: WallpaperDao
    ): WallpaperRepository {
        // FIX: Must match the order: Unsplash, Pexels, Dao
        return WallpaperRepository(unsplashApi, pexelsApi, dao)
    }
}