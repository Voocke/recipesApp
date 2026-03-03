package com.example.recipes.data.repository

import com.example.recipes.data.local.dao.RecipeDao
import com.example.recipes.data.local.dao.TagDao
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeWithTags
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class RecipeRepository @Inject constructor (private val recipeDao: RecipeDao, private val tagDao: TagDao) {


    fun getAllRecipes(): Flow<List<RecipeWithTags>> {
        return recipeDao.getRecipesWithTags()
    }

    suspend fun searchRecipes(query: String): List<Recipe> {
        return recipeDao.findByTitle(query)
    }

    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }

    suspend fun insertTag(tag: Tag) {
        tagDao.insertTag(tag)
    }

    suspend fun addTagToRecipe(recipeId: Long, tagId: Long) {
        tagDao.insertRecipeTag(RecipeTagCross(recipeId, tagId))
    }


}