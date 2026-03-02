package com.example.recipes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    suspend fun getAll(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE LOWER(title) LIKE LOWER('%' || :search || '%')")
    suspend fun findByTitle(search: String) : List<Recipe>

    @Transaction
    @Query("SELECT * FROM recipes")
    fun getRecipesWithTags(): Flow<List<RecipeWithTags>>

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert
    suspend fun insertRecipeTags(crossRef: List<RecipeTagCross>)

    @Transaction
    suspend fun insertRecipeWithTags(recipe: Recipe, tags: List<Tag>) {
        val recipeId = insertRecipe(recipe)
        val crossRefs = tags.map { RecipeTagCross(recipeId, it.tid) }
        insertRecipeTags(crossRefs)
    }

    @Delete
    suspend fun deleteRecipe(vararg recipe: Recipe)

    @Update
    suspend fun updateRecipe(recipe: Recipe)
}