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
        // FIX: We do NOT clear 'wallpapers' here anymore.
        // This prevents the screen from flashing white/empty.
        _uiState.update { it.copy(searchQuery = query, isLoading = true) }

        currentPage = 1

        viewModelScope.launch {
            // Fetch new data first
            val newWallpapers = if (query.isBlank()) {
                repository.getEditorialPhotos(currentPage)
            } else {
                repository.searchPhotos(query, currentPage)
            }

            // THEN update the UI in one smooth frame
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

            val newWallpapers = if (_uiState.value.searchQuery.isBlank()) {
                repository.getEditorialPhotos(currentPage)
            } else {
                repository.searchPhotos(_uiState.value.searchQuery, currentPage)
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
}