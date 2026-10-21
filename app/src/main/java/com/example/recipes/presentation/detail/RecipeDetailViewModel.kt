package com.example.recipes.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private var loadRecipeJob: Job? = null

    /**
     * Loads full recipe details by [recipeId] and marks it as viewed with timestamp.
     */
    fun loadRecipe(recipeId: Long) {
        loadRecipeJob?.cancel()
        _uiState.update { it.copy(isLoading = true) }

        loadRecipeJob = viewModelScope.launch {
            repository.updateLastViewed(recipeId, System.currentTimeMillis())
            repository.getRecipeById(recipeId).collect { details ->
                val totalCount = details?.ingredients?.size ?: 0
                val checkedCount = details?.ingredients?.count { it.isChecked } ?: 0
                _uiState.update {
                    it.copy(
                        recipe = details,
                        checkedCount = checkedCount,
                        totalIngredientsCount = totalCount,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Toggles checked state for an ingredient checkbox.
     */
    fun toggleIngredient(ingredientId: Long, isChecked: Boolean) {
        viewModelScope.launch {
            repository.setIngredientChecked(ingredientId, isChecked)

            // Optimistic update in state
            _uiState.update { current ->
                val currentRecipe = current.recipe ?: return@update current
                val updatedIngredients = currentRecipe.ingredients.map { ing ->
                    if (ing.id == ingredientId) ing.copy(isChecked = isChecked) else ing
                }
                val newCheckedCount = updatedIngredients.count { it.isChecked }
                current.copy(
                    recipe = currentRecipe.copy(ingredients = updatedIngredients),
                    checkedCount = newCheckedCount,
                    totalIngredientsCount = updatedIngredients.size
                )
            }
        }
    }

    /**
     * Toggles favorite status of the currently opened recipe.
     */
    fun toggleFavorite() {
        val currentRecipe = _uiState.value.recipe ?: return
        val newFavoriteState = !currentRecipe.recipe.isFavorite

        viewModelScope.launch {
            repository.toggleFavorite(currentRecipe.recipe.rid, newFavoriteState)

            // Optimistic update
            _uiState.update { current ->
                val rec = current.recipe ?: return@update current
                current.copy(
                    recipe = rec.copy(recipe = rec.recipe.copy(isFavorite = newFavoriteState))
                )
            }
        }
    }

    /**
     * Formats clean, beautiful text representation of the recipe
     * with ingredients, cooking steps, and notes for system ShareSheet.
     */
    fun getShareText(): String {
        val details = _uiState.value.recipe ?: return ""
        val recipe = details.recipe
        val sb = StringBuilder()

        // Title
        sb.appendLine("🍽️ ${recipe.title}")
        sb.appendLine()

        // Description
        if (!recipe.description.isNullOrBlank()) {
            sb.appendLine(recipe.description)
            sb.appendLine()
        }

        // Meta pills
        val metadata = buildList {
            if (recipe.cookingTime > 0) add("⏱️ ${recipe.cookingTime} мин")
            if (recipe.servings > 0) add("👥 ${recipe.servings} порц.")
            if (recipe.difficulty.isNotBlank()) add("📊 ${recipe.difficulty}")
            if (recipe.category.isNotBlank()) add("🏷️ ${recipe.category}")
        }
        if (metadata.isNotEmpty()) {
            sb.appendLine(metadata.joinToString(" • "))
            sb.appendLine()
        }

        // Ingredients
        if (details.ingredients.isNotEmpty()) {
            sb.appendLine("🛒 Ингредиенты:")
            details.ingredients.sortedBy { it.orderIndex }.forEach { ingredient ->
                sb.appendLine(" • ${ingredient.text}")
            }
            sb.appendLine()
        }

        // Steps
        if (details.steps.isNotEmpty()) {
            sb.appendLine("👩‍🍳 Способ приготовления:")
            details.steps.sortedBy { it.stepNumber }.forEach { step ->
                sb.appendLine("${step.stepNumber}. ${step.instruction}")
            }
            sb.appendLine()
        }

        // Notes
        if (!recipe.notes.isNullOrBlank()) {
            sb.appendLine("💡 Заметки шефа: ${recipe.notes}")
            sb.appendLine()
        }

        sb.append("Отправлено из «Книги рецептов»")
        return sb.toString().trim()
    }
}
