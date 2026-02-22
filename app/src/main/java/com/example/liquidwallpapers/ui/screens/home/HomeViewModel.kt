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

    init {
        loadFoundersCollection()
        loadNextPage()
    }

    private fun loadFoundersCollection() {
        viewModelScope.launch {
            try {
                val curated = repository.getFoundersCollection()
                _uiState.update { it.copy(foundersWallpapers = curated) }
            } catch (e: Exception) { }
        }
    }

    fun searchWallpapers(query: String) {
        _uiState.update { it.copy(searchQuery = query, isLoading = true) }

        currentPage = 1

        viewModelScope.launch {
            // FIX: If query is blank, force "wallpapers" keyword instead of random photos
            val effectiveQuery = if (query.isBlank()) "wallpapers" else query

            // We now use searchPhotos for EVERYTHING to ensure strict filtering
            val newWallpapers = repository.searchPhotos(effectiveQuery, currentPage)

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
        if (_uiState.value.isLoading && currentPage > 1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // FIX: Determine query. If blank, default to "wallpapers"
            val currentQuery = _uiState.value.searchQuery
            val effectiveQuery = if (currentQuery.isBlank()) "wallpapers" else currentQuery

            // Always call searchPhotos to keep the feed clean
            val newWallpapers = repository.searchPhotos(effectiveQuery, currentPage)

            _uiState.update {
                it.copy(
                    wallpapers = it.wallpapers + newWallpapers,
                    isLoading = false
                )
            }
            currentPage++
        }
    }
}