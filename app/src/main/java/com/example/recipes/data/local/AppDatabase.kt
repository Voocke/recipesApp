package com.example.recipes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recipes.data.local.dao.RecipeDao
import com.example.recipes.data.local.dao.TagDao
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag

@Database(entities = [Recipe::class, Tag::class, RecipeTagCross::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao() : RecipeDao
    abstract fun tagDao() : TagDao


}