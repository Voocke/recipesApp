package com.example.recipes.data.local.relations

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag

@Immutable
data class RecipeFullDetails(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "rid",
        entityColumn = "recipeId"
    )
    val ingredients: List<Ingredient> = emptyList(),
    @Relation(
        parentColumn = "rid",
        entityColumn = "recipeId"
    )
    val steps: List<RecipeStep> = emptyList(),
    @Relation(
        parentColumn = "rid",
        entityColumn = "tid",
        associateBy = Junction(RecipeTagCross::class)
    )
    val tags: List<Tag> = emptyList()
)
