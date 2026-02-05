package com.example.recipes.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag

data class RecipeWithTags(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "rid",
        entityColumn = "tid",
        associateBy = Junction(RecipeTagCross::class)
    )
    val tags: List<Tag>
)
