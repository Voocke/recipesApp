package com.example.recipes.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val rid: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "img_path") val imgPath: String?,
    @ColumnInfo(name = "cooking_time") val cookingTime: Int,
    @ColumnInfo(name = "recipe_text") val recipeText: String
)

