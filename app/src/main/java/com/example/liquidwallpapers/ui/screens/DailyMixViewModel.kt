package com.example.liquidwallpapers.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.data.repository.WallpaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DailyMixUiState(
    val cards: List<Wallpaper> = emptyList(),
    val isLoading: Boolean = false,
    val isDeckFinished: Boolean = false
)

@HiltViewModel
class DailyMixViewModel @Inject constructor(
    private val repository: WallpaperRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyMixUiState())
    val uiState: StateFlow<DailyMixUiState> = _uiState.asStateFlow()

    private val prefs = context.getSharedPreferences("daily_mix", Context.MODE_PRIVATE)
    private val todayKey = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    init {
        loadDailyMix()
    }

    private fun loadDailyMix() {
        // Check if today's deck was already completed
        val lastFinishedDate = prefs.getString("finished_date", null)
        if (lastFinishedDate == todayKey) {
            _uiState.update { it.copy(isDeckFinished = true) }
            return
        }

        // Use day-of-year as page number so each day gives different results
        val dayPage = LocalDate.now().dayOfYear

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Fetch wallpapers using day-based page for daily variety
            val wallpapers = repository.searchPhotos(
                "wallpaper",
                page = dayPage
            )

            // Filter out any previously seen IDs from this session
            val seenIds = prefs.getStringSet("seen_ids_$todayKey", emptySet()) ?: emptySet()
            val freshWallpapers = wallpapers
                .filter { it.id !in seenIds }
                .take(10)

            _uiState.update {
                it.copy(cards = freshWallpapers, isLoading = false)
            }
        }
    }

    fun onSwipeRight(wallpaper: Wallpaper) {
        // CRITICAL: Save to favorites FIRST, then remove from deck
        viewModelScope.launch {
            repository.insertFavorite(wallpaper)
            markSeenAndRemove(wallpaper)
        }
    }

    fun onSwipeLeft() {
        val topCard = _uiState.value.cards.firstOrNull() ?: return
        viewModelScope.launch {
            markSeenAndRemove(topCard)
        }
    }

    private fun markSeenAndRemove(wallpaper: Wallpaper) {
        // Persist this wallpaper as "seen" so it won't repeat
        val seenIds = prefs.getStringSet("seen_ids_$todayKey", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        seenIds.add(wallpaper.id)
        prefs.edit().putStringSet("seen_ids_$todayKey", seenIds).apply()

        // FIXED: Filter by ID, NOT drop(1). drop(1) caused race conditions
        // when multiple swipes happened in quick succession.
        val remaining = _uiState.value.cards.filter { it.id != wallpaper.id }

        if (remaining.isEmpty()) {
            prefs.edit().putString("finished_date", todayKey).apply()
            _uiState.update { it.copy(cards = emptyList(), isDeckFinished = true) }
        } else {
            _uiState.update { it.copy(cards = remaining) }
        }
    }
}
