package com.example.recipes.data.sync

import com.example.recipes.data.local.entities.Ingredient
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.data.local.entities.RecipeStep
import com.example.recipes.data.local.entities.Tag
import com.example.recipes.data.local.relations.RecipeFullDetails
import com.example.recipes.data.repository.RecipeRepository
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonRecipeBackupService @Inject constructor(
    private val repository: RecipeRepository
) : RecipeBackupService {

    override suspend fun exportRecipesToJson(): String {
        val recipes = repository.getAllFullRecipes()
        return serializeRecipesToJson(recipes)
    }

    override suspend fun importRecipesFromJson(jsonString: String): Result<Int> {
        return runCatching {
            val recipes = parseRecipesFromJson(jsonString)
            if (recipes.isNotEmpty()) {
                repository.insertFullRecipes(recipes)
            }
            recipes.size
        }
    }

    override fun serializeRecipesToJson(recipes: List<RecipeFullDetails>): String {
        val rootArray = JSONArray()

        for (item in recipes) {
            val recipeObj = JSONObject().apply {
                put("title", item.recipe.title)
                if (!item.recipe.description.isNullOrBlank()) {
                    put("description", item.recipe.description)
                }
                if (!item.recipe.imgPath.isNullOrBlank()) {
                    put("imgPath", item.recipe.imgPath)
                }
                put("cookingTime", item.recipe.cookingTime)
                put("servings", item.recipe.servings)
                put("difficulty", item.recipe.difficulty)
                put("category", item.recipe.category)
                if (!item.recipe.source.isNullOrBlank()) {
                    put("source", item.recipe.source)
                }
                if (!item.recipe.notes.isNullOrBlank()) {
                    put("notes", item.recipe.notes)
                }
                put("isFavorite", item.recipe.isFavorite)

                // Ingredients
                val ingredientsArray = JSONArray()
                item.ingredients.sortedBy { it.orderIndex }.forEach { ing ->
                    val ingObj = JSONObject().apply {
                        put("text", ing.text)
                        put("orderIndex", ing.orderIndex)
                        put("isChecked", ing.isChecked)
                    }
                    ingredientsArray.put(ingObj)
                }
                put("ingredients", ingredientsArray)

                // Steps
                val stepsArray = JSONArray()
                item.steps.sortedBy { it.stepNumber }.forEach { step ->
                    val stepObj = JSONObject().apply {
                        put("stepNumber", step.stepNumber)
                        put("instruction", step.instruction)
                    }
                    stepsArray.put(stepObj)
                }
                put("steps", stepsArray)

                // Tags
                val tagsArray = JSONArray()
                item.tags.forEach { tag ->
                    tagsArray.put(tag.name)
                }
                put("tags", tagsArray)
            }
            rootArray.put(recipeObj)
        }

        return rootArray.toString(2)
    }

    override fun parseRecipesFromJson(jsonString: String): List<RecipeFullDetails> {
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) return emptyList()

        val jsonArray: JSONArray = when {
            trimmed.startsWith("[") -> JSONArray(trimmed)
            trimmed.startsWith("{") -> {
                val obj = JSONObject(trimmed)
                when {
                    obj.has("recipes") -> obj.getJSONArray("recipes")
                    obj.has("data") -> obj.getJSONArray("data")
                    else -> throw IllegalArgumentException("JSON must contain a 'recipes' array")
                }
            }
            else -> throw IllegalArgumentException("Invalid JSON format")
        }

        val result = mutableListOf<RecipeFullDetails>()
        val currentTime = System.currentTimeMillis()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.optJSONObject(i) ?: continue
            val title = obj.optString("title", "").trim()
            if (title.isEmpty()) continue

            val description = obj.optString("description", "").takeIf { it.isNotBlank() }
            val imgPath = obj.optString("imgPath", "").takeIf { it.isNotBlank() }
            val cookingTime = obj.optInt("cookingTime", 0)
            val servings = obj.optInt("servings", 1).coerceAtLeast(1)
            val difficulty = obj.optString("difficulty", "Easy").takeIf { it.isNotBlank() } ?: "Easy"
            val category = obj.optString("category", "Main").takeIf { it.isNotBlank() } ?: "Main"
            val source = obj.optString("source", "").takeIf { it.isNotBlank() }
            val notes = obj.optString("notes", "").takeIf { it.isNotBlank() }
            val isFavorite = obj.optBoolean("isFavorite", false)

            val recipe = Recipe(
                rid = 0L,
                title = title,
                description = description,
                imgPath = imgPath,
                cookingTime = cookingTime,
                servings = servings,
                difficulty = difficulty,
                category = category,
                source = source,
                notes = notes,
                isDraft = false,
                isFavorite = isFavorite,
                createdAt = currentTime - (i * 1000L),
                lastViewedAt = null
            )

            // Ingredients
            val ingredients = mutableListOf<Ingredient>()
            val ingArray = obj.optJSONArray("ingredients")
            if (ingArray != null) {
                for (j in 0 until ingArray.length()) {
                    val ingItem = ingArray.opt(j)
                    when (ingItem) {
                        is JSONObject -> {
                            val text = ingItem.optString("text", "").trim()
                            if (text.isNotEmpty()) {
                                val orderIndex = ingItem.optInt("orderIndex", j + 1)
                                val isChecked = ingItem.optBoolean("isChecked", false)
                                ingredients.add(
                                    Ingredient(
                                        id = 0L,
                                        recipeId = 0L,
                                        text = text,
                                        orderIndex = orderIndex,
                                        isChecked = isChecked
                                    )
                                )
                            }
                        }
                        is String -> {
                            val text = ingItem.trim()
                            if (text.isNotEmpty()) {
                                ingredients.add(
                                    Ingredient(
                                        id = 0L,
                                        recipeId = 0L,
                                        text = text,
                                        orderIndex = j + 1,
                                        isChecked = false
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Steps
            val steps = mutableListOf<RecipeStep>()
            val stepsArray = obj.optJSONArray("steps")
            if (stepsArray != null) {
                for (j in 0 until stepsArray.length()) {
                    val stepItem = stepsArray.opt(j)
                    when (stepItem) {
                        is JSONObject -> {
                            val instruction = stepItem.optString("instruction", "").trim()
                            if (instruction.isNotEmpty()) {
                                val stepNumber = stepItem.optInt("stepNumber", j + 1)
                                steps.add(
                                    RecipeStep(
                                        id = 0L,
                                        recipeId = 0L,
                                        stepNumber = stepNumber,
                                        instruction = instruction
                                    )
                                )
                            }
                        }
                        is String -> {
                            val instruction = stepItem.trim()
                            if (instruction.isNotEmpty()) {
                                steps.add(
                                    RecipeStep(
                                        id = 0L,
                                        recipeId = 0L,
                                        stepNumber = j + 1,
                                        instruction = instruction
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Tags
            val tags = mutableListOf<Tag>()
            val tagsArray = obj.optJSONArray("tags")
            if (tagsArray != null) {
                for (j in 0 until tagsArray.length()) {
                    val tagItem = tagsArray.opt(j)
                    val tagName = when (tagItem) {
                        is JSONObject -> tagItem.optString("name", "").trim()
                        is String -> tagItem.trim()
                        else -> ""
                    }
                    if (tagName.isNotEmpty()) {
                        tags.add(Tag(tid = 0L, name = tagName))
                    }
                }
            }

            result.add(
                RecipeFullDetails(
                    recipe = recipe,
                    ingredients = ingredients,
                    steps = steps,
                    tags = tags
                )
            )
        }

        return result
    }
}
