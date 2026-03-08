package com.example.liquidwallpapers.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.data.repository.WallpaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val wallpapers: List<Wallpaper> = emptyList(),
    val foundersWallpapers: List<Wallpaper> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WallpaperRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var defaultStartPage = 1

    init {
        // Shift the starting page based on the day of the year 
        // This gives users entirely completely new mixed wallpapers every single day
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        defaultStartPage = (dayOfYear % 100) + 1 
        currentPage = defaultStartPage

        loadFoundersCollection()
        loadNextPage()
    }

    private fun loadFoundersCollection() {
        viewModelScope.launch {
            try {
                val curated = repository.getFoundersCollection()
                _uiState.update { it.copy(foundersWallpapers = curated) }
            } catch (_: Exception) { }
        }
    }

    fun searchWallpapers(query: String) {
        _uiState.update { it.copy(searchQuery = query, isLoading = true) }

        // Start at page 1 for specific search, but resume the daily page for the mixed feed
        currentPage = if (query.isBlank()) defaultStartPage else 1

        viewModelScope.launch {
            val effectiveQuery = query.ifBlank { DEFAULT_QUERY }
            var newWallpapers = repository.searchPhotos(effectiveQuery, currentPage)
            
            // Randomly shuffle the mixed feed so it feels very random and un-sorted
            if (query.isBlank()) {
                newWallpapers = newWallpapers.shuffled()
            }

            _uiState.update {
                it.copy(
                    wallpapers = newWallpapers,
                    isLoading = false
                )
            }
            currentPage++
        }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val effectiveQuery = _uiState.value.searchQuery.ifBlank { DEFAULT_QUERY }
            var newWallpapers = repository.searchPhotos(effectiveQuery, currentPage)
            
            // Randomly shuffle the mixed feed 
            if (_uiState.value.searchQuery.isBlank()) {
                newWallpapers = newWallpapers.shuffled()
            }

            _uiState.update {
                it.copy(
                    wallpapers = it.wallpapers + newWallpapers,
                    isLoading = false
                )
            }
            currentPage++
        }
    }

    companion object {
        // A generic term that gives a massive variety of different styles mixed together
        private const val DEFAULT_QUERY = "wallpaper"
    }
}