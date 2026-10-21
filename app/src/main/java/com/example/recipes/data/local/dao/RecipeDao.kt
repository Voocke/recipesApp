package com.example.recipes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeFullDetails
import com.example.recipes.data.local.relations.RecipeWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getRecentlyAdded(): Flow<List<RecipeFullDetails>>

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    suspend fun getAllFullRecipes(): List<RecipeFullDetails>

    @Transaction
    @Query("SELECT * FROM recipes WHERE lastViewedAt IS NOT NULL ORDER BY lastViewedAt DESC LIMIT 10")
    fun getRecentlyOpened(): Flow<List<RecipeFullDetails>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<RecipeFullDetails>>

    @Transaction
    @Query("SELECT * FROM recipes WHERE rid = :rid")
    fun getRecipeById(rid: Long): Flow<RecipeFullDetails?>

    @Query("UPDATE recipes SET lastViewedAt = :timestamp WHERE rid = :rid")
    suspend fun updateLastViewed(rid: Long, timestamp: Long)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE rid = :rid")
    suspend fun toggleFavorite(rid: Long, isFavorite: Boolean)

    @Query("UPDATE ingredients SET isChecked = :isChecked WHERE id = :ingredientId")
    suspend fun setIngredientChecked(ingredientId: Long, isChecked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<Ingredient>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<RecipeStep>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeTags(crossRef: List<RecipeTagCross>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag): Long

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): Tag?

    @Transaction
    suspend fun insertFullRecipe(
        recipe: Recipe,
        ingredients: List<Ingredient> = emptyList(),
        steps: List<RecipeStep> = emptyList(),
        tags: List<Tag> = emptyList()
    ): Long {
        val recipeId = insertRecipe(recipe)
        if (ingredients.isNotEmpty()) {
            insertIngredients(ingredients.map { it.copy(recipeId = recipeId) })
        }
        if (steps.isNotEmpty()) {
            insertSteps(steps.map { it.copy(recipeId = recipeId) })
        }
        if (tags.isNotEmpty()) {
            val crossRefs = tags.map { tag ->
                val tagId = if (tag.tid != 0L) {
                    tag.tid
                } else {
                    val existing = getTagByName(tag.name)
                    existing?.tid ?: insertTag(tag)
                }
                RecipeTagCross(recipeId = recipeId, tagId = tagId)
            }
            insertRecipeTags(crossRefs)
        }
        return recipeId
    }

    @Transaction
    suspend fun insertFullRecipe(details: RecipeFullDetails): Long {
        return insertFullRecipe(
            recipe = details.recipe,
            ingredients = details.ingredients,
            steps = details.steps,
            tags = details.tags
        )
    }

    @Transaction
    suspend fun insertFullRecipes(recipes: List<RecipeFullDetails>) {
        for (details in recipes) {
            insertFullRecipe(details)
        }
    }

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun count(): Int

    @Query("SELECT * FROM recipes")
    suspend fun getAll(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE LOWER(title) LIKE LOWER('%' || :search || '%')")
    suspend fun findByTitle(search: String): List<Recipe>

    @Transaction
    @Query("SELECT * FROM recipes")
    fun getRecipesWithTags(): Flow<List<RecipeWithTags>>

    @Transaction
    suspend fun insertRecipeWithTags(recipe: Recipe, tags: List<Tag>) {
        val recipeId = insertRecipe(recipe)
        val crossRefs = tags.map { RecipeTagCross(recipeId, it.tid) }
        insertRecipeTags(crossRefs)
    }

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: Long)

    @Query("DELETE FROM recipe_steps WHERE recipeId = :recipeId")
    suspend fun deleteStepsByRecipeId(recipeId: Long)

    @Query("DELETE FROM recipe_tag_cross WHERE rid = :recipeId")
    suspend fun deleteRecipeTagsByRecipeId(recipeId: Long)

    @Transaction
    suspend fun updateFullRecipe(
        recipe: Recipe,
        ingredients: List<Ingredient> = emptyList(),
        steps: List<RecipeStep> = emptyList(),
        tags: List<Tag> = emptyList()
    ) {
        updateRecipe(recipe)
        deleteIngredientsByRecipeId(recipe.rid)
        if (ingredients.isNotEmpty()) {
            insertIngredients(ingredients.map { it.copy(recipeId = recipe.rid) })
        }
        deleteStepsByRecipeId(recipe.rid)
        if (steps.isNotEmpty()) {
            insertSteps(steps.map { it.copy(recipeId = recipe.rid) })
        }
        deleteRecipeTagsByRecipeId(recipe.rid)
        if (tags.isNotEmpty()) {
            val crossRefs = tags.map { tag ->
                val tagId = if (tag.tid != 0L) {
                    tag.tid
                } else {
                    val existing = getTagByName(tag.name)
                    existing?.tid ?: insertTag(tag)
                }
                RecipeTagCross(recipeId = recipe.rid, tagId = tagId)
            }
            insertRecipeTags(crossRefs)
        }
    }

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()

    @Query("DELETE FROM recipes WHERE rid = :recipeId")
    suspend fun deleteRecipeById(recipeId: Long)

    @Delete
    suspend fun deleteRecipe(vararg recipe: Recipe)

    @Update
    suspend fun updateRecipe(recipe: Recipe)
}
