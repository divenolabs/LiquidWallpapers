package com.example.liquidwallpapers.ui.screens

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

data class CategoryDetailUiState(
    val wallpapers: List<Wallpaper> = emptyList(),
    val isLoading: Boolean = false,
    val categoryName: String = ""
)

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val repository: WallpaperRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryDetailUiState())
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var currentCategoryQuery = ""

    fun loadCategory(categoryName: String) {
        // Avoid reloading if already loaded for this category
        if (_uiState.value.categoryName == categoryName && _uiState.value.wallpapers.isNotEmpty()) return

        currentPage = 1
        currentCategoryQuery = mapCategoryToQuery(categoryName)
        _uiState.update { CategoryDetailUiState(isLoading = true, categoryName = categoryName) }

        viewModelScope.launch {
            val wallpapers = repository.searchPhotos(currentCategoryQuery, currentPage)
            _uiState.update {
                it.copy(wallpapers = wallpapers, isLoading = false)
            }
            currentPage++
        }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val newWallpapers = repository.searchPhotos(currentCategoryQuery, currentPage)
            _uiState.update {
                it.copy(
                    wallpapers = it.wallpapers + newWallpapers,
                    isLoading = false
                )
            }
            currentPage++
        }
    }

    private fun mapCategoryToQuery(category: String): String {
        return when (category.trim().lowercase()) {
            "", "all", "wallpapers" -> "wallpaper"
            "abstract art" -> "abstract art"
            "amoled dark", "amoled pure" -> "amoled dark"
            "deep space" -> "space"
            "cyberpunk" -> "cyberpunk"
            "nature hd" -> "nature"
            "minimalist" -> "minimalist"
            "aesthetics" -> "aesthetic"
            "automotive" -> "supercars"
            "mountains" -> "mountains"
            else -> "$category wallpapers"
        }
    }
}
