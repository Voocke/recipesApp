package com.example.recipes.data.repository

import com.example.recipes.data.local.dao.RecipeDao
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeWithTags
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class RecipeRepository @Inject constructor (private val recipeDao: RecipeDao) {


    fun getRecipesWithTags(): Flow<List<RecipeWithTags>> {
        return recipeDao.getRecipesWithTags()
    }
    suspend fun searchRecipesByTitle(query: String): List<Recipe> {
        return recipeDao.findByTitle(query)
    }
    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }
    suspend fun insertRecipeWithTags(recipe: Recipe, tags: List<Tag>){
        recipeDao.insertRecipeWithTags(recipe,tags)
    }
    suspend fun deleteRecipe(vararg recipe: Recipe){
        recipeDao.deleteRecipe(*recipe)
    }
    suspend fun updateRecipe(recipe: Recipe){
        recipeDao.updateRecipe(recipe)
    }


}