package com.example.recipes.presentation.add_recipe

data class AddRecipeUiState(
    val editingRecipeId: Long? = null,
    val title: String = "",
    val charCount: String = "0/80",
    val cookingTime: Int = 15,
    val servings: Int = 2,
    val difficulty: String = "Легко",
    val mealType: String = "",
    val category: String = "Горячее",
    val customCategories: List<String> = emptyList(),
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val notes: String = "",
    val imagePath: String? = null,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val isDeleted: Boolean = false
)
