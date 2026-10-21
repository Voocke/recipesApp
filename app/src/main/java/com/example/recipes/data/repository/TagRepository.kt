package com.example.recipes.data.repository

import com.example.recipes.data.local.dao.TagDao
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag
import javax.inject.Inject

class TagRepository @Inject constructor(private val tagDao: TagDao) {

    suspend fun insertTag(tag: Tag) {
        tagDao.insertTag(tag)
    }

    suspend fun addTagToRecipe(recipeId: Long, tagId: Long) {
        tagDao.insertRecipeTag(RecipeTagCross(recipeId, tagId))
    }

    suspend fun deleteTag(vararg tag: Tag){
        tagDao.deleteTag(*tag)
    }

    suspend fun deleteRecipeTag(crossRef: RecipeTagCross){
        tagDao.deleteRecipeTag(crossRef)
    }
}