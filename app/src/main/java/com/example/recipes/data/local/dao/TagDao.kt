package com.example.recipes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag

@Dao
interface TagDao {

    @Query("SELECT * FROM tags")
    suspend fun getAll() : List<Tag>

    @Insert
    suspend fun insertTag(tag: Tag)

    @Insert
    suspend fun insertRecipeTag(crossRef: RecipeTagCross)

}