package com.example.recipes.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredients",
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
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long = 0,
    val text: String,
    val orderIndex: Int = 0,
    val isChecked: Boolean = false
)
