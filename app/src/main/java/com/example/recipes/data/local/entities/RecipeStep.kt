package com.example.recipes.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe_steps",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["rid"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["recipeId"])
    ]
)
data class RecipeStep(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long = 0,
    val stepNumber: Int,
    val instruction: String
)
