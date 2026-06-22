package com.example.liquidwallpapers.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liquidwallpapers.data.repository.WallpaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ProfileStatsUiState(
    val savedCount: Int = 0,
    val isDailyMixReady: Boolean = true,
    val cacheLabel: String = "0 MB"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    repository: WallpaperRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val cacheLabel = MutableStateFlow("0 MB")

    val uiState: StateFlow<ProfileStatsUiState> = combine(
        repository.getAllFavorites(),
        cacheLabel
    ) { favorites, cache ->
        ProfileStatsUiState(
            savedCount = favorites.size,
            isDailyMixReady = isDailyMixReady(),
            cacheLabel = cache
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileStatsUiState()
    )

    suspend fun refreshCacheSize() {
        cacheLabel.value = withContext(Dispatchers.IO) {
            formatBytes(coilCacheDir().directorySize())
        }
    }

    private fun isDailyMixReady(): Boolean {
        val todayKey = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val prefs = context.getSharedPreferences("daily_mix", Context.MODE_PRIVATE)
        return prefs.getString("finished_date", null) != todayKey
    }

    private fun coilCacheDir(): File = File(context.cacheDir, "image_cache")

    private fun File.directorySize(): Long {
        if (!exists()) return 0L
        if (isFile) return length()
        return listFiles()?.sumOf { it.directorySize() } ?: 0L
    }

    private fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024f * 1024f)
        return if (mb < 1f) {
            "0 MB"
        } else {
            "${mb.toInt()} MB"
        }
    }
}
