package com.example.recipes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.recipes.data.local.entities.Recipe

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    suspend fun getAll(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE LOWER(title) LIKE LOWER('%' || :search || '%')")
    suspend fun findByTitle(search: String) : List<Recipe>

    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(vararg recipe: Recipe)

    @Update
    suspend fun updateRecipe(recipe: Recipe)
}