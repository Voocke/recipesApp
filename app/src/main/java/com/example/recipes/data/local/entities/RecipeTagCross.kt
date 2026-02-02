package com.example.recipes.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "recipe_tag_cross", primaryKeys = ["recipe_id", "tag_id"])
data class RecipeTagCross(
    @ColumnInfo(name = "recipe_id") val recipeId: Long,
    @ColumnInfo(name = "tag_id") val tagId: Long
)
