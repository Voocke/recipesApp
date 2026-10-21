package com.example.recipes.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getRecentlyOpened().distinctUntilChanged(),
        repository.getFavorites().distinctUntilChanged(),
        repository.getRecentlyAdded().distinctUntilChanged(),
        _selectedCategory,
        _searchQuery
    ) { opened, favs, added, category, query ->
        val trimmedQuery = query.trim()
        val filteredRecentlyAdded = added.filter { details ->
            val matchesCategory = category.isNullOrBlank() ||
                details.recipe.category.equals(category, ignoreCase = true) ||
                details.tags.any { it.name.equals(category, ignoreCase = true) }
            val matchesSearch = trimmedQuery.isEmpty() ||
                details.recipe.title.contains(trimmedQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        val filteredOpened = if (trimmedQuery.isEmpty()) {
            opened
        } else {
            opened.filter { it.recipe.title.contains(trimmedQuery, ignoreCase = true) }
        }
        val filteredFavs = if (trimmedQuery.isEmpty()) {
            favs
        } else {
            favs.filter { it.recipe.title.contains(trimmedQuery, ignoreCase = true) }
        }
        HomeUiState(
            recentlyOpened = filteredOpened,
            favorites = filteredFavs,
            recentlyAdded = filteredRecentlyAdded,
            selectedCategory = category,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    /**
     * Updates the search query filter.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * Toggles category filter. If clicked again on the already selected category,
     * clears filter back to null.
     */
    fun onCategorySelected(category: String?) {
        _selectedCategory.value = if (category != null && _selectedCategory.value?.equals(category, ignoreCase = true) == true) {
            null
        } else {
            category
        }
    }

    /**
     * Toggles favorite status for a recipe by its ID.
     */
    fun onToggleFavorite(recipeId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId, isFavorite)
        }
    }
}
