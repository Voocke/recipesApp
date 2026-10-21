package com.example.recipes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeFullDetails
import com.example.recipes.data.local.relations.RecipeWithTags
import com.example.recipes.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(private val repository: RecipeRepository) : ViewModel() {

    val allRecipes: StateFlow<List<RecipeWithTags>> = repository.getAllRecipesWithTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentlyAdded: StateFlow<List<RecipeFullDetails>> = repository.getRecentlyAdded()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentlyOpened: StateFlow<List<RecipeFullDetails>> = repository.getRecentlyOpened()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favorites: StateFlow<List<RecipeFullDetails>> = repository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun getAllTags(): List<Tag> {
        return repository.getAllTags()
    }

    suspend fun searchRecipes(query: String): List<Recipe> {
        return repository.searchRecipes(query)
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.deleteTag(tag)
        }
    }

    fun addTag(name: String) {
        viewModelScope.launch {
            val newTag = Tag(name = name)
            repository.insertTag(newTag)
        }
    }

    fun toggleFavorite(recipeId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId, isFavorite)
        }
    }

    fun updateLastViewed(recipeId: Long, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            repository.updateLastViewed(recipeId, timestamp)
        }
    }

    fun setIngredientChecked(ingredientId: Long, isChecked: Boolean) {
        viewModelScope.launch {
            repository.setIngredientChecked(ingredientId, isChecked)
        }
    }

    fun addFullRecipe(
        recipe: Recipe,
        ingredients: List<Ingredient> = emptyList(),
        steps: List<RecipeStep> = emptyList(),
        tags: List<Tag> = emptyList()
    ) {
        viewModelScope.launch {
            repository.insertFullRecipe(recipe, ingredients, steps, tags)
        }
    }

    fun addRecipe(
        title: String,
        description: String? = null,
        tags: List<Tag> = emptyList(),
        imgPath: String? = null,
        cookingTime: Int = 0,
        servings: Int = 1,
        difficulty: String = "Easy",
        category: String = "Main",
        source: String? = null,
        notes: String? = null,
        recipeText: String? = null
    ) {
        viewModelScope.launch {
            val newRecipe = Recipe(
                title = title,
                description = description,
                imgPath = imgPath,
                cookingTime = cookingTime,
                servings = servings,
                difficulty = difficulty,
                category = category,
                source = source,
                notes = notes
            )
            val steps = if (!recipeText.isNullOrBlank()) {
                listOf(RecipeStep(stepNumber = 1, instruction = recipeText))
            } else {
                emptyList()
            }
            repository.insertFullRecipe(newRecipe, emptyList(), steps, tags)
        }
    }
}
