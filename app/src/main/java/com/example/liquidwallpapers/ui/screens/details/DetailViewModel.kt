package com.example.liquidwallpapers.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liquidwallpapers.data.model.Wallpaper
import com.example.liquidwallpapers.data.repository.WallpaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: WallpaperRepository
) : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Check if this specific wallpaper is in the database
    fun checkFavoriteStatus(id: String) {
        viewModelScope.launch {
            repository.isFavorite(id).collect { status ->
                _isFavorite.value = status
            }
        }
    }

    // Toggle logic: If liked -> delete. If not liked -> insert.
    fun toggleFavorite(wallpaper: Wallpaper) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.deleteFavorite(wallpaper)
            } else {
                repository.insertFavorite(wallpaper)
            }
        }
    }
}