package com.example.recipes

import com.example.recipes.data.local.dao.RecipeDao
import com.example.recipes.data.local.dao.TagDao
import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeFullDetails
import com.example.recipes.data.local.relations.RecipeWithTags
import com.example.recipes.data.repository.RecipeRepository
import com.example.recipes.data.sync.JsonRecipeBackupService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeBackupTest {

    private val fakeDao = DummyRecipeDao()
    private val fakeTagDao = DummyTagDao()
    private val repository = RecipeRepository(fakeDao, fakeTagDao)
    private val backupService = JsonRecipeBackupService(repository)

    private fun createSampleRecipe(): RecipeFullDetails {
        return RecipeFullDetails(
            recipe = Recipe(
                rid = 1L,
                title = "Борщ украинский",
                description = "Традиционный наваристый борщ",
                cookingTime = 90,
                servings = 6,
                difficulty = "Средне",
                category = "Супы",
                source = "Книга рецептов",
                notes = "Подавать с пампушками и чесноком",
                isFavorite = true
            ),
            ingredients = listOf(
                Ingredient(id = 1L, recipeId = 1L, text = "Свекла 2 шт", orderIndex = 1, isChecked = false),
                Ingredient(id = 2L, recipeId = 1L, text = "Говядина 500г", orderIndex = 2, isChecked = true)
            ),
            steps = listOf(
                RecipeStep(id = 1L, recipeId = 1L, stepNumber = 1, instruction = "Сварить мясной бульон"),
                RecipeStep(id = 2L, recipeId = 1L, stepNumber = 2, instruction = "Нарезать и пассеровать овощи")
            ),
            tags = listOf(
                Tag(tid = 1L, name = "Супы"),
                Tag(tid = 2L, name = "Обед")
            )
        )
    }

    @Test
    fun testSerializationAndDeserializationRoundTrip() {
        val original = createSampleRecipe()
        val json = backupService.serializeRecipesToJson(listOf(original))

        assertTrue(json.contains("Борщ украинский"))
        assertTrue(json.contains("Свекла 2 шт"))
        assertTrue(json.contains("Сварить мясной бульон"))

        val parsed = backupService.parseRecipesFromJson(json)
        assertEquals(1, parsed.size)

        val item = parsed.first()
        assertEquals("Борщ украинский", item.recipe.title)
        assertEquals("Традиционный наваристый борщ", item.recipe.description)
        assertEquals(90, item.recipe.cookingTime)
        assertEquals(6, item.recipe.servings)
        assertEquals("Средне", item.recipe.difficulty)
        assertEquals("Супы", item.recipe.category)
        assertEquals("Книга рецептов", item.recipe.source)
        assertEquals("Подавать с пампушками и чесноком", item.recipe.notes)
        assertTrue(item.recipe.isFavorite)

        // Ingredients
        assertEquals(2, item.ingredients.size)
        assertEquals("Свекла 2 шт", item.ingredients[0].text)
        assertFalse(item.ingredients[0].isChecked)
        assertEquals("Говядина 500г", item.ingredients[1].text)
        assertTrue(item.ingredients[1].isChecked)

        // Steps
        assertEquals(2, item.steps.size)
        assertEquals(1, item.steps[0].stepNumber)
        assertEquals("Сварить мясной бульон", item.steps[0].instruction)
        assertEquals(2, item.steps[1].stepNumber)

        // Tags
        assertEquals(2, item.tags.size)
        assertEquals("Супы", item.tags[0].name)
        assertEquals("Обед", item.tags[1].name)
    }

    @Test
    fun testParseJsonObjectWithRecipesArray() {
        val jsonString = """
            {
              "recipes": [
                {
                  "title": "Панкейки",
                  "category": "Завтрак",
                  "cookingTime": 20,
                  "ingredients": ["Мука 200г", "Молоко 200мл", "Яйцо 1 шт"],
                  "steps": ["Смешать ингредиенты", "Жарить на сухой сковороде"],
                  "tags": ["Завтрак", "Выпечка"]
                }
              ]
            }
        """.trimIndent()

        val parsed = backupService.parseRecipesFromJson(jsonString)
        assertEquals(1, parsed.size)
        val pancake = parsed[0]
        assertEquals("Панкейки", pancake.recipe.title)
        assertEquals(3, pancake.ingredients.size)
        assertEquals("Мука 200г", pancake.ingredients[0].text)
        assertEquals(2, pancake.steps.size)
        assertEquals("Смешать ингредиенты", pancake.steps[0].instruction)
        assertEquals(2, pancake.tags.size)
    }

    @Test
    fun testParseEmptyOrBlankJsonReturnsEmptyList() {
        val emptyResult = backupService.parseRecipesFromJson("")
        assertTrue(emptyResult.isEmpty())

        val whitespaceResult = backupService.parseRecipesFromJson("   ")
        assertTrue(whitespaceResult.isEmpty())
    }

    @Test(expected = Exception::class)
    fun testParseInvalidJsonThrows() {
        backupService.parseRecipesFromJson("invalid json content")
    }

    private class DummyRecipeDao : RecipeDao {
        override fun getRecentlyAdded(): Flow<List<RecipeFullDetails>> = emptyFlow()
        override suspend fun getAllFullRecipes(): List<RecipeFullDetails> = emptyList()
        override fun getRecentlyOpened(): Flow<List<RecipeFullDetails>> = emptyFlow()
        override fun getFavorites(): Flow<List<RecipeFullDetails>> = emptyFlow()
        override fun getRecipeById(rid: Long): Flow<RecipeFullDetails?> = emptyFlow()
        override suspend fun updateLastViewed(rid: Long, timestamp: Long) {}
        override suspend fun toggleFavorite(rid: Long, isFavorite: Boolean) {}
        override suspend fun setIngredientChecked(ingredientId: Long, isChecked: Boolean) {}
        override suspend fun insertRecipe(recipe: Recipe): Long = 1L
        override suspend fun insertIngredients(ingredients: List<Ingredient>): List<Long> = emptyList()
        override suspend fun insertSteps(steps: List<RecipeStep>): List<Long> = emptyList()
        override suspend fun insertRecipeTags(crossRef: List<RecipeTagCross>) {}
        override suspend fun insertTag(tag: Tag): Long = 1L
        override suspend fun getTagByName(name: String): Tag? = null
        override suspend fun count(): Int = 0
        override suspend fun getAll(): List<Recipe> = emptyList()
        override suspend fun findByTitle(search: String): List<Recipe> = emptyList()
        override fun getRecipesWithTags(): Flow<List<RecipeWithTags>> = emptyFlow()
        override suspend fun deleteIngredientsByRecipeId(recipeId: Long) {}
        override suspend fun deleteStepsByRecipeId(recipeId: Long) {}
        override suspend fun deleteRecipeTagsByRecipeId(recipeId: Long) {}
        override suspend fun deleteRecipe(vararg recipe: Recipe) {}
        override suspend fun deleteRecipeById(recipeId: Long) {}
        override suspend fun deleteAllRecipes() {}
        override suspend fun updateRecipe(recipe: Recipe) {}
    }

    private class DummyTagDao : TagDao {
        override suspend fun getAll(): List<Tag> = emptyList()
        override suspend fun insertTag(tag: Tag): Long = 1L
        override suspend fun insertRecipeTag(crossRef: RecipeTagCross) {}
        override suspend fun deleteTag(vararg tag: Tag) {}
        override suspend fun deleteRecipeTag(crossRef: RecipeTagCross) {}
    }
}
