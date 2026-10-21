package com.example.recipes.presentation.detail

import com.example.recipes.data.local.relations.RecipeFullDetails

data class RecipeDetailUiState(
    val recipe: RecipeFullDetails? = null,
    val checkedCount: Int = 0,
    val totalIngredientsCount: Int = 0,
    val isLoading: Boolean = false
)
