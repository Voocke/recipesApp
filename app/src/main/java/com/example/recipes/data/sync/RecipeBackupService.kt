package com.example.recipes.data.sync

import com.example.recipes.data.local.relations.RecipeFullDetails

/**
 * Service interface for recipe import and export.
 * Designed to be extensible for future sync services (cloud, WebDAV, REST, etc.).
 */
interface RecipeBackupService {
    suspend fun exportRecipesToJson(): String
    suspend fun importRecipesFromJson(jsonString: String): Result<Int>
    fun parseRecipesFromJson(jsonString: String): List<RecipeFullDetails>
    fun serializeRecipesToJson(recipes: List<RecipeFullDetails>): String
}
