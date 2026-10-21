package com.example.recipes.data.local.entities

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val rid: Long = 0,
    val title: String,
    val description: String? = null,
    val imgPath: String? = null,
    val cookingTime: Int = 0,
    val servings: Int = 1,
    val difficulty: String = "Легко",
    val category: String = "Ужин",
    val source: String? = null,
    val notes: String? = null,
    val isDraft: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastViewedAt: Long? = null
)
