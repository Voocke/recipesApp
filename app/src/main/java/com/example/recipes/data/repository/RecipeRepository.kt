package com.example.recipes.data.repository

import com.example.recipes.data.local.dao.RecipeDao
import com.example.recipes.data.local.dao.TagDao
import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeFullDetails
import com.example.recipes.data.local.relations.RecipeWithTags
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao,
    private val tagDao: TagDao
) {

    // Queries
    fun getRecentlyAdded(): Flow<List<RecipeFullDetails>> = recipeDao.getRecentlyAdded()

    suspend fun getAllFullRecipes(): List<RecipeFullDetails> = recipeDao.getAllFullRecipes()

    fun getRecentlyOpened(): Flow<List<RecipeFullDetails>> = recipeDao.getRecentlyOpened()

    fun getFavorites(): Flow<List<RecipeFullDetails>> = recipeDao.getFavorites()

    fun getRecipeById(rid: Long): Flow<RecipeFullDetails?> = recipeDao.getRecipeById(rid)

    fun getAllRecipesWithTags(): Flow<List<RecipeWithTags>> = recipeDao.getRecipesWithTags()

    suspend fun getAllTags(): List<Tag> = tagDao.getAll()

    suspend fun searchRecipes(query: String): List<Recipe> = recipeDao.findByTitle(query)

    suspend fun count(): Int = recipeDao.count()

    // Updates
    suspend fun updateLastViewed(rid: Long, timestamp: Long) = recipeDao.updateLastViewed(rid, timestamp)

    suspend fun toggleFavorite(rid: Long, isFavorite: Boolean) = recipeDao.toggleFavorite(rid, isFavorite)

    suspend fun setIngredientChecked(ingredientId: Long, isChecked: Boolean) =
        recipeDao.setIngredientChecked(ingredientId, isChecked)

    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)

    suspend fun updateFullRecipe(
        recipe: Recipe,
        ingredients: List<Ingredient> = emptyList(),
        steps: List<RecipeStep> = emptyList(),
        tags: List<Tag> = emptyList()
    ) {
        recipeDao.updateFullRecipe(recipe, ingredients, steps, tags)
    }

    // Inserts
    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)

    suspend fun insertTag(tag: Tag): Long = tagDao.insertTag(tag)

    suspend fun insertRecipeWithTags(recipe: Recipe, tags: List<Tag>) =
        recipeDao.insertRecipeWithTags(recipe, tags)

    suspend fun insertFullRecipe(
        recipe: Recipe,
        ingredients: List<Ingredient> = emptyList(),
        steps: List<RecipeStep> = emptyList(),
        tags: List<Tag> = emptyList()
    ): Long = recipeDao.insertFullRecipe(recipe, ingredients, steps, tags)

    suspend fun insertFullRecipe(details: RecipeFullDetails): Long =
        recipeDao.insertFullRecipe(details)

    suspend fun insertFullRecipes(recipes: List<RecipeFullDetails>) {
        recipeDao.insertFullRecipes(recipes)
    }

    suspend fun addTagToRecipe(recipeId: Long, tagId: Long) {
        tagDao.insertRecipeTag(RecipeTagCross(recipeId, tagId))
    }

    // Deletes
    suspend fun deleteRecipe(vararg recipe: Recipe) {
        recipeDao.deleteRecipe(*recipe)
    }

    suspend fun deleteRecipeById(recipeId: Long) {
        recipeDao.deleteRecipeById(recipeId)
    }

    suspend fun deleteRecipeTag(crossRef: RecipeTagCross) {
        tagDao.deleteRecipeTag(crossRef)
    }

    suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(tag)
    }
}
