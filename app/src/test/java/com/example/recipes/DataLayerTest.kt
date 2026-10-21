package com.example.recipes

import com.example.recipes.data.local.DatabaseSeeder
import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.RecipeTagCross
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeFullDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataLayerTest {

    @Test
    fun testSeedDataContainsAllRequiredRecipes() {
        val seedRecipes = DatabaseSeeder.getSeedRecipes()
        assertEquals(7, seedRecipes.size)

        val titles = seedRecipes.map { it.recipe.title }
        val expectedTitles = listOf(
            "Сливочная паста от бабушки",
            "Лимонный тарт",
            "Тартин на закваске",
            "Бабушкин яблочный пирог",
            "Запеченная курица с травами",
            "Летний гаспачо",
            "Овсяная каша с кардамоном"
        )
        for (expected in expectedTitles) {
            assertTrue("Expected to find $expected in seed recipes", titles.contains(expected))
        }
    }

    @Test
    fun testNonnasCreamPastaDetails() {
        val seedRecipes = DatabaseSeeder.getSeedRecipes()
        val nonna = seedRecipes.first { it.recipe.title == "Сливочная паста от бабушки" }

        assertTrue(nonna.ingredients.isNotEmpty())
        assertTrue(nonna.steps.isNotEmpty())
        assertNotNull(nonna.recipe.notes)
        assertTrue(nonna.recipe.notes!!.isNotBlank())
        assertTrue(nonna.tags.any { it.name.equals("Итальянская", ignoreCase = true) })
        assertEquals(4, nonna.recipe.servings)
        assertEquals(25, nonna.recipe.cookingTime)
        assertFalse(nonna.recipe.isDraft)
        assertTrue(nonna.recipe.isFavorite)
    }

    @Test
    fun testRecipeEntityDefaults() {
        val recipe = Recipe(title = "Test Recipe")
        assertEquals(0L, recipe.rid)
        assertEquals(1, recipe.servings)
        assertEquals("Легко", recipe.difficulty)
        assertEquals("Ужин", recipe.category)
        assertFalse(recipe.isDraft)
        assertFalse(recipe.isFavorite)
        assertTrue(recipe.createdAt > 0L)
    }

    @Test
    fun testIngredientEntity() {
        val ingredient = Ingredient(
            recipeId = 1L,
            text = "1 cup milk",
            orderIndex = 1,
            isChecked = false
        )
        assertEquals(1L, ingredient.recipeId)
        assertEquals("1 cup milk", ingredient.text)
        assertEquals(1, ingredient.orderIndex)
        assertFalse(ingredient.isChecked)
    }

    @Test
    fun testRecipeStepEntity() {
        val step = RecipeStep(
            recipeId = 1L,
            stepNumber = 1,
            instruction = "Boil water"
        )
        assertEquals(1L, step.recipeId)
        assertEquals(1, step.stepNumber)
        assertEquals("Boil water", step.instruction)
    }

    @Test
    fun testRecipeTagCross() {
        val cross = RecipeTagCross(recipeId = 10L, tagId = 20L)
        assertEquals(10L, cross.recipeId)
        assertEquals(20L, cross.tagId)
    }

    @Test
    fun testRecipeFullDetails() {
        val recipe = Recipe(title = "Pizza")
        val ingredients = listOf(Ingredient(recipeId = 1L, text = "Flour"))
        val steps = listOf(RecipeStep(recipeId = 1L, stepNumber = 1, instruction = "Knead"))
        val tags = listOf(Tag(tid = 1L, name = "Italian"))

        val details = RecipeFullDetails(
            recipe = recipe,
            ingredients = ingredients,
            steps = steps,
            tags = tags
        )

        assertEquals("Pizza", details.recipe.title)
        assertEquals(1, details.ingredients.size)
        assertEquals(1, details.steps.size)
        assertEquals(1, details.tags.size)
    }
}
