package com.example.recipes.presentation.add_recipe

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.storage.InternalStorageManager
import com.example.recipes.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

val MealTypeOptions = listOf("Завтрак", "Обед", "Ужин")
val DefaultCategoryOptions = listOf("Супы", "Горячее", "Салаты", "Закуски", "Паста", "Выпечка", "Десерты", "Напитки")

fun translateCategoryToRussian(category: String): String {
    return when (category.lowercase().trim()) {
        "breakfast", "завтрак", "завтраки" -> "Завтрак"
        "lunch", "обед", "обеды" -> "Обед"
        "dinner", "ужин", "ужины" -> "Ужин"
        "soup", "soups", "суп", "супы" -> "Супы"
        "main", "hot", "горячее", "горячие блюда", "второе" -> "Горячее"
        "dessert", "desserts", "десерт", "десерты" -> "Десерты"
        "pasta", "паста", "макароны" -> "Паста"
        "baking", "bakery", "выпечка" -> "Выпечка"
        "salad", "salads", "салат", "салаты" -> "Салаты"
        "snack", "snacks", "appetizer", "appetizers", "закуска", "закуски" -> "Закуски"
        "drink", "drinks", "beverage", "beverages", "напиток", "напитки" -> "Напитки"
        "sauce", "sauces", "соус", "соусы" -> "Соусы"
        "bread", "хлеб" -> "Хлеб"
        "side", "sides", "side dish", "гарнир", "гарниры" -> "Гарниры"
        "meat", "мясо" -> "Мясо"
        "fish", "рыба" -> "Рыба"
        "poultry", "chicken", "птица", "курица" -> "Птица"
        "vegetarian", "vegan", "вегетарианское" -> "Вегетарианское"
        else -> category.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val internalStorageManager: InternalStorageManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecipeUiState())
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val allTags = repository.getAllTags().map { translateCategoryToRussian(it.name) }
                val knownMealTypes = listOf("Завтрак", "Обед", "Ужин")
                val custom = allTags.filter { tag ->
                    tag.isNotBlank() &&
                        knownMealTypes.none { it.equals(tag, ignoreCase = true) } &&
                        DefaultCategoryOptions.none { it.equals(tag, ignoreCase = true) }
                }.distinct()
                if (custom.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(customCategories = (state.customCategories + custom).distinct())
                    }
                }
            } catch (_: Exception) {
                // Ignore any failure during init
            }
        }
    }

    companion object {
        const val MAX_TITLE_LENGTH = 80
    }

    /**
     * Updates recipe title and enforces character limit of up to 80 characters.
     */
    fun updateTitle(value: String) {
        val sanitized = if (value.length > MAX_TITLE_LENGTH) {
            value.take(MAX_TITLE_LENGTH)
        } else {
            value
        }
        _uiState.update {
            it.copy(
                title = sanitized,
                charCount = "${sanitized.length}/$MAX_TITLE_LENGTH"
            )
        }
    }

    /**
     * Updates cooking time in minutes.
     */
    fun updateTime(minutes: Int) {
        _uiState.update { it.copy(cookingTime = minutes.coerceAtLeast(0)) }
    }

    /**
     * Updates number of servings.
     */
    fun updateServings(count: Int) {
        _uiState.update { it.copy(servings = count.coerceAtLeast(1)) }
    }

    /**
     * Updates difficulty level («Easy» / «Medium» / «Hard»).
     */
    fun updateDifficulty(level: String) {
        _uiState.update { it.copy(difficulty = level) }
    }

    /**
     * Selects meal type («Завтрак» / «Обед» / «Ужин»). Toggles off if clicked again.
     */
    fun selectMealType(mealType: String) {
        _uiState.update {
            it.copy(
                mealType = if (it.mealType.equals(mealType, ignoreCase = true)) "" else mealType
            )
        }
    }

    /**
     * Selects recipe category.
     */
    fun selectCategory(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    /**
     * Adds a custom category to the available list and selects it.
     */
    fun addCustomCategory(newCategory: String) {
        val trimmed = newCategory.trim()
        if (trimmed.isEmpty()) return
        _uiState.update { state ->
            val updatedCustom = if (state.customCategories.none { it.equals(trimmed, ignoreCase = true) }) {
                state.customCategories + trimmed
            } else {
                state.customCategories
            }
            state.copy(
                customCategories = updatedCustom,
                category = trimmed
            )
        }
    }

    /**
     * Adds a new empty ingredient row.
     */
    fun addIngredientLine() {
        _uiState.update { it.copy(ingredients = it.ingredients + "") }
    }

    /**
     * Updates ingredient at the given [index].
     */
    fun updateIngredient(index: Int, text: String) {
        _uiState.update { state ->
            if (index in state.ingredients.indices) {
                val updated = state.ingredients.toMutableList()
                updated[index] = text
                state.copy(ingredients = updated)
            } else {
                state
            }
        }
    }

    /**
     * Removes ingredient at the given [index].
     */
    fun removeIngredient(index: Int) {
        _uiState.update { state ->
            if (index in state.ingredients.indices) {
                val updated = state.ingredients.toMutableList()
                updated.removeAt(index)
                state.copy(ingredients = updated)
            } else {
                state
            }
        }
    }

    /**
     * Adds a new empty step instruction row.
     */
    fun addStepLine() {
        _uiState.update { it.copy(steps = it.steps + "") }
    }

    /**
     * Updates step instruction at the given [index].
     */
    fun updateStep(index: Int, text: String) {
        _uiState.update { state ->
            if (index in state.steps.indices) {
                val updated = state.steps.toMutableList()
                updated[index] = text
                state.copy(steps = updated)
            } else {
                state
            }
        }
    }

    /**
     * Removes step instruction at the given [index].
     */
    fun removeStep(index: Int) {
        _uiState.update { state ->
            if (index in state.steps.indices) {
                val updated = state.steps.toMutableList()
                updated.removeAt(index)
                state.copy(steps = updated)
            } else {
                state
            }
        }
    }

    /**
     * Updates notes / chef tips.
     */
    fun updateNotes(text: String) {
        _uiState.update { it.copy(notes = text) }
    }

    /**
     * Directly updates image path (e.g. for testing or pre-saved path).
     */
    fun updateImagePath(path: String?) {
        _uiState.update { it.copy(imagePath = path) }
    }

    /**
     * Saves chosen image via [InternalStorageManager] into private internal storage.
     */
    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            try {
                val path = internalStorageManager?.saveImageFromUri(uri) ?: uri.toString()
                _uiState.update { it.copy(imagePath = path) }
            } catch (e: Exception) {
                _uiState.update { it.copy(imagePath = it.imagePath ?: uri.toString()) }
            }
        }
    }

    /**
     * Loads existing recipe into editor for editing.
     */
    fun loadRecipe(recipeId: Long) {
        viewModelScope.launch {
            repository.getRecipeById(recipeId).firstOrNull()?.let { details ->
                val recipe = details.recipe
                val ingList = details.ingredients.sortedBy { it.orderIndex }.map { it.text }
                val stepList = details.steps.sortedBy { it.stepNumber }.map { it.instruction }

                val knownMealTypes = listOf("Завтрак", "Обед", "Ужин", "Breakfast", "Lunch", "Dinner")
                val foundMealTag = details.tags.firstOrNull { tag ->
                    knownMealTypes.any { it.equals(tag.name, ignoreCase = true) }
                }?.name
                val loadedMealType = foundMealTag
                    ?: if (knownMealTypes.any { it.equals(recipe.category, ignoreCase = true) }) recipe.category else ""

                val loadedCategory = if (recipe.category.isNotBlank() && !knownMealTypes.any { it.equals(recipe.category, ignoreCase = true) }) {
                    recipe.category
                } else {
                    details.tags.firstOrNull { tag ->
                        !knownMealTypes.any { it.equals(tag.name, ignoreCase = true) }
                    }?.name ?: recipe.category
                }

                val nonDefaultTags = details.tags.map { it.name.trim() }.filter { tag ->
                    tag.isNotBlank() &&
                        knownMealTypes.none { it.equals(tag, ignoreCase = true) } &&
                        DefaultCategoryOptions.none { it.equals(tag, ignoreCase = true) }
                }

                _uiState.update { current ->
                    current.copy(
                        editingRecipeId = recipe.rid,
                        title = recipe.title,
                        charCount = "${recipe.title.length}/$MAX_TITLE_LENGTH",
                        cookingTime = recipe.cookingTime,
                        servings = recipe.servings,
                        difficulty = recipe.difficulty,
                        mealType = loadedMealType,
                        category = loadedCategory,
                        customCategories = (current.customCategories + nonDefaultTags).distinct(),
                        ingredients = ingList,
                        steps = stepList,
                        notes = recipe.notes.orEmpty(),
                        imagePath = recipe.imgPath
                    )
                }
            }
        }
    }

    /**
     * Persists recipe into Room database via [RecipeRepository].
     *
     * @param isDraft If true, marks recipe as draft.
     */
    fun saveRecipe(isDraft: Boolean = false) {
        if (_uiState.value.isSaving) return
        val current = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val titleToSave = current.title.trim().ifEmpty {
                    if (isDraft) "Черновик" else "Новый рецепт"
                }

                val categoryToSave = when {
                    current.category.isNotBlank() -> current.category.trim()
                    current.mealType.isNotBlank() -> current.mealType.trim()
                    else -> "Основное"
                }

                val existingDetails = current.editingRecipeId?.let { rid ->
                    repository.getRecipeById(rid).firstOrNull()
                }

                val recipe = Recipe(
                    rid = current.editingRecipeId ?: 0L,
                    title = titleToSave,
                    description = existingDetails?.recipe?.description,
                    imgPath = current.imagePath,
                    cookingTime = current.cookingTime,
                    servings = current.servings,
                    difficulty = current.difficulty,
                    category = categoryToSave,
                    source = existingDetails?.recipe?.source,
                    notes = current.notes.ifBlank { null },
                    isDraft = isDraft,
                    isFavorite = existingDetails?.recipe?.isFavorite ?: false,
                    createdAt = existingDetails?.recipe?.createdAt ?: System.currentTimeMillis(),
                    lastViewedAt = existingDetails?.recipe?.lastViewedAt
                )

                val ingredientsList = current.ingredients
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .mapIndexed { index, text ->
                        Ingredient(
                            recipeId = recipe.rid,
                            text = text,
                            orderIndex = index + 1,
                            isChecked = false
                        )
                    }

                val stepsList = current.steps
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .mapIndexed { index, text ->
                        RecipeStep(
                            recipeId = recipe.rid,
                            stepNumber = index + 1,
                            instruction = text
                        )
                    }

                val tagsList = buildList {
                    if (current.mealType.isNotBlank()) {
                        add(Tag(name = current.mealType.trim()))
                    }
                    if (current.category.isNotBlank() && !current.category.equals(current.mealType, ignoreCase = true)) {
                        add(Tag(name = current.category.trim()))
                    }
                }

                if (current.editingRecipeId != null && current.editingRecipeId > 0L) {
                    repository.updateFullRecipe(
                        recipe = recipe,
                        ingredients = ingredientsList,
                        steps = stepsList,
                        tags = tagsList
                    )
                } else {
                    repository.insertFullRecipe(
                        recipe = recipe,
                        ingredients = ingredientsList,
                        steps = stepsList,
                        tags = tagsList
                    )
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    /**
     * Deletes the recipe currently being edited from the database.
     */
    fun deleteRecipe() {
        val recipeId = _uiState.value.editingRecipeId ?: return
        viewModelScope.launch {
            try {
                repository.deleteRecipeById(recipeId)
                _uiState.update { it.copy(isDeleted = true) }
            } catch (_: Exception) {
            }
        }
    }
}
