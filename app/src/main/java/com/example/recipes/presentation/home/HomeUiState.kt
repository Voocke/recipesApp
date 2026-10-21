package com.example.recipes.presentation.home

import androidx.compose.runtime.Immutable
import com.example.recipes.data.local.relations.RecipeFullDetails

@Immutable
data class HomeUiState(
    val recentlyOpened: List<RecipeFullDetails> = emptyList(),
    val favorites: List<RecipeFullDetails> = emptyList(),
    val recentlyAdded: List<RecipeFullDetails> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
