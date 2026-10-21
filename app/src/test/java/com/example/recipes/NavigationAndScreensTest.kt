package com.example.recipes

import com.example.recipes.navigation.Screen
import com.example.recipes.presentation.home.formatCookingTime
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationAndScreensTest {

    @Test
    fun testScreenRoutes() {
        assertEquals("home", Screen.Home.route)
        assertEquals("add_recipe", Screen.AddRecipe.route)
        assertEquals("edit_recipe/{recipeId}", Screen.EditRecipe.route)
        assertEquals("recipeId", Screen.EditRecipe.ARG_RECIPE_ID)
        assertEquals("recipe_detail/{recipeId}", Screen.RecipeDetail.route)
        assertEquals("recipeId", Screen.RecipeDetail.ARG_RECIPE_ID)
        assertEquals("settings", Screen.Settings.route)
    }

    @Test
    fun testEditRecipeRouteCreation() {
        val route = Screen.EditRecipe.createRoute(42L)
        assertEquals("edit_recipe/42", route)

        val routeZero = Screen.EditRecipe.createRoute(0L)
        assertEquals("edit_recipe/0", routeZero)
    }

    @Test
    fun testRecipeDetailRouteCreation() {
        val route = Screen.RecipeDetail.createRoute(42L)
        assertEquals("recipe_detail/42", route)

        val routeZero = Screen.RecipeDetail.createRoute(0L)
        assertEquals("recipe_detail/0", routeZero)

        val routeLarge = Screen.RecipeDetail.createRoute(999999L)
        assertEquals("recipe_detail/999999", routeLarge)
    }

    @Test
    fun testFormatCookingTime() {
        assertEquals("15 мин", formatCookingTime(0))
        assertEquals("15 мин", formatCookingTime(-5))
        assertEquals("20 мин", formatCookingTime(20))
        assertEquals("45 мин", formatCookingTime(45))
        assertEquals("1 ч", formatCookingTime(60))
        assertEquals("1 ч 10 мин", formatCookingTime(70))
        assertEquals("2 ч", formatCookingTime(120))
        assertEquals("2 ч 15 мин", formatCookingTime(135))
    }
}
