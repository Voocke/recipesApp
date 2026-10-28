package com.example.recipes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipes.presentation.add_recipe.AddRecipeScreen
import com.example.recipes.presentation.detail.RecipeDetailScreen
import com.example.recipes.presentation.home.HomeScreen
import com.example.recipes.presentation.settings.SettingsScreen

/**
 * Destinations for the application.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddRecipe : Screen("add_recipe")
    data object EditRecipe : Screen("edit_recipe/{recipeId}") {
        const val ARG_RECIPE_ID = "recipeId"
        fun createRoute(recipeId: Long): String = "edit_recipe/$recipeId"
    }
    data object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        const val ARG_RECIPE_ID = "recipeId"
        fun createRoute(recipeId: Long): String = "recipe_detail/$recipeId"
    }
    data object Settings : Screen("settings")
}

/**
 * Main application navigation graph hosting Home, Add Recipe, and Recipe Detail screens.
 */

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onAddRecipeClick = {
                    navController.navigate(Screen.AddRecipe.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {

                    if (navController.currentDestination?.route != Screen.Home.route)
                        navController.popBackStack()
                }
            )
        }

        composable(route = Screen.AddRecipe.route) {
            AddRecipeScreen(
                onNavigateBack = {

                    if (navController.currentDestination?.route != Screen.Home.route)
                        navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditRecipe.route,
            arguments = listOf(
                navArgument(Screen.EditRecipe.ARG_RECIPE_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong(Screen.EditRecipe.ARG_RECIPE_ID) ?: 0L
            AddRecipeScreen(
                recipeId = recipeId,
                onNavigateBack = {
                    if (navController.currentDestination?.route != Screen.Home.route)
                        navController.popBackStack()
                },
                onRecipeDeleted = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument(Screen.RecipeDetail.ARG_RECIPE_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong(Screen.RecipeDetail.ARG_RECIPE_ID) ?: 0L
            RecipeDetailScreen(
                recipeId = recipeId,
                onNavigateBack = {
                    if (navController.currentDestination?.route != Screen.Home.route)
                        navController.popBackStack()
                },
                onEditRecipe = {
                    navController.navigate(Screen.EditRecipe.createRoute(recipeId))
                }
            )
        }
    }
}
