package com.example.recipes.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = "recipe_tag_cross",
    primaryKeys = ["rid", "tid"],
    indices = [
        Index(value = ["tid"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["rid"],
            childColumns = ["rid"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["tid"],
            childColumns = ["tid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecipeTagCross(
    @ColumnInfo(name = "rid") val recipeId: Long,
    @ColumnInfo(name = "tid") val tagId: Long
)
