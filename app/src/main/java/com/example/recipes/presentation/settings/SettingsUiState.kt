package com.example.recipes.presentation.settings

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsUiState(
    val recipeCount: Int = 0,
    val isLoading: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)
