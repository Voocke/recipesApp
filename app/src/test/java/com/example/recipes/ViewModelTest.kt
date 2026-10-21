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
import com.example.recipes.presentation.add_recipe.AddRecipeUiState
import com.example.recipes.presentation.add_recipe.AddRecipeViewModel
import com.example.recipes.presentation.detail.RecipeDetailUiState
import com.example.recipes.presentation.detail.RecipeDetailViewModel
import com.example.recipes.presentation.home.HomeUiState
import com.example.recipes.presentation.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRecipeDao: FakeRecipeDao
    private lateinit var fakeTagDao: FakeTagDao
    private lateinit var repository: RecipeRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRecipeDao = FakeRecipeDao()
        fakeTagDao = FakeTagDao()
        repository = RecipeRepository(fakeRecipeDao, fakeTagDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // =========================================================================
    // 1. HomeViewModel Tests
    // =========================================================================

    @Test
    fun testHomeViewModelStateAndCategoryFiltering() = runTest {
        val pasta = createSampleRecipe(
            id = 1L,
            title = "Cream Pasta",
            category = "Pasta",
            isFavorite = true,
            isDraft = false,
            lastViewedAt = System.currentTimeMillis()
        )
        val soup = createSampleRecipe(
            id = 2L,
            title = "Gazpacho",
            category = "Soup",
            isFavorite = false,
            isDraft = false,
            lastViewedAt = null
        )
        fakeRecipeDao.addSample(pasta)
        fakeRecipeDao.addSample(soup)

        val homeViewModel = HomeViewModel(repository)
        backgroundScope.launch(testDispatcher) { homeViewModel.uiState.collect() }
        val state = homeViewModel.uiState.value

        assertEquals(1, state.recentlyOpened.size)
        assertEquals("Cream Pasta", state.recentlyOpened.first().recipe.title)
        assertEquals(1, state.favorites.size)
        assertEquals(2, state.recentlyAdded.size)
        assertNull(state.selectedCategory)
        assertFalse(state.isLoading)

        // Select category "Soup"
        homeViewModel.onCategorySelected("Soup")
        val filteredState = homeViewModel.uiState.value
        assertEquals("Soup", filteredState.selectedCategory)
        assertEquals(1, filteredState.recentlyAdded.size)
        assertEquals("Gazpacho", filteredState.recentlyAdded.first().recipe.title)

        // Toggle category off (click "Soup" again)
        homeViewModel.onCategorySelected("Soup")
        val resetState = homeViewModel.uiState.value
        assertNull(resetState.selectedCategory)
        assertEquals(2, resetState.recentlyAdded.size)
    }

    @Test
    fun testHomeViewModelSearchQueryFiltering() = runTest {
        val pasta = createSampleRecipe(
            id = 1L,
            title = "Cream Pasta",
            category = "Pasta",
            isFavorite = false,
            isDraft = false
        )
        val soup = createSampleRecipe(
            id = 2L,
            title = "Gazpacho",
            category = "Soup",
            isFavorite = false,
            isDraft = false
        )
        fakeRecipeDao.addSample(pasta)
        fakeRecipeDao.addSample(soup)

        val homeViewModel = HomeViewModel(repository)
        backgroundScope.launch(testDispatcher) { homeViewModel.uiState.collect() }

        assertEquals(2, homeViewModel.uiState.value.recentlyAdded.size)
        assertEquals("", homeViewModel.uiState.value.searchQuery)

        // Search for "pasta" (case-insensitive)
        homeViewModel.onSearchQueryChanged("pasta")
        val filtered = homeViewModel.uiState.value
        assertEquals("pasta", filtered.searchQuery)
        assertEquals(1, filtered.recentlyAdded.size)
        assertEquals("Cream Pasta", filtered.recentlyAdded.first().recipe.title)

        // Clear search
        homeViewModel.onSearchQueryChanged("")
        assertEquals(2, homeViewModel.uiState.value.recentlyAdded.size)

        // Search with no matches
        homeViewModel.onSearchQueryChanged("nonexistent")
        assertEquals(0, homeViewModel.uiState.value.recentlyAdded.size)
    }

    @Test
    fun testHomeViewModelToggleFavorite() = runTest {
        val cake = createSampleRecipe(
            id = 10L,
            title = "Chocolate Cake",
            category = "Dessert",
            isFavorite = false,
            isDraft = false
        )
        fakeRecipeDao.addSample(cake)

        val homeViewModel = HomeViewModel(repository)
        backgroundScope.launch(testDispatcher) { homeViewModel.uiState.collect() }
        assertEquals(0, homeViewModel.uiState.value.favorites.size)

        homeViewModel.onToggleFavorite(10L, true)
        assertEquals(1, homeViewModel.uiState.value.favorites.size)
        assertEquals("Chocolate Cake", homeViewModel.uiState.value.favorites.first().recipe.title)

        homeViewModel.onToggleFavorite(10L, false)
        assertEquals(0, homeViewModel.uiState.value.favorites.size)
    }

    // =========================================================================
    // 2. AddRecipeViewModel Tests
    // =========================================================================

    @Test
    fun testAddRecipeTitleValidationAndCharCount() {
        val addViewModel = AddRecipeViewModel(repository)
        assertEquals("", addViewModel.uiState.value.title)
        assertEquals("0/80", addViewModel.uiState.value.charCount)

        addViewModel.updateTitle("Homemade Pizza")
        assertEquals("Homemade Pizza", addViewModel.uiState.value.title)
        assertEquals("14/80", addViewModel.uiState.value.charCount)

        // Test truncation to 80 characters
        val longTitle = "A".repeat(120)
        addViewModel.updateTitle(longTitle)
        assertEquals(80, addViewModel.uiState.value.title.length)
        assertEquals("80/80", addViewModel.uiState.value.charCount)
    }

    @Test
    fun testAddRecipeFormProperties() {
        val addViewModel = AddRecipeViewModel(repository)

        addViewModel.updateTime(45)
        assertEquals(45, addViewModel.uiState.value.cookingTime)

        addViewModel.updateServings(4)
        assertEquals(4, addViewModel.uiState.value.servings)

        addViewModel.updateDifficulty("Hard")
        assertEquals("Hard", addViewModel.uiState.value.difficulty)

        addViewModel.selectCategory("Dinner")
        assertEquals("Dinner", addViewModel.uiState.value.category)

        addViewModel.updateNotes("Preheat oven to 200C")
        assertEquals("Preheat oven to 200C", addViewModel.uiState.value.notes)

        addViewModel.updateImagePath("/path/to/recipe.jpg")
        assertEquals("/path/to/recipe.jpg", addViewModel.uiState.value.imagePath)
    }

    @Test
    fun testMealTypeAndCategorySeparationAndCustomCategory() {
        val addViewModel = AddRecipeViewModel(repository)

        // Select meal type
        addViewModel.selectMealType("Завтрак")
        assertEquals("Завтрак", addViewModel.uiState.value.mealType)

        // Toggle meal type off
        addViewModel.selectMealType("Завтрак")
        assertEquals("", addViewModel.uiState.value.mealType)

        // Select another meal type
        addViewModel.selectMealType("Обед")
        assertEquals("Обед", addViewModel.uiState.value.mealType)

        // Select category
        addViewModel.selectCategory("Супы")
        assertEquals("Супы", addViewModel.uiState.value.category)

        // Add custom category
        addViewModel.addCustomCategory("Гарниры")
        assertTrue(addViewModel.uiState.value.customCategories.contains("Гарниры"))
        assertEquals("Гарниры", addViewModel.uiState.value.category)

        // Adding blank does nothing
        addViewModel.addCustomCategory("   ")
        assertEquals("Гарниры", addViewModel.uiState.value.category)
    }

    @Test
    fun testSaveRecipeWithMealTypeAndCategory() = runTest {
        val addViewModel = AddRecipeViewModel(repository)
        addViewModel.updateTitle("Borscht")
        addViewModel.selectMealType("Обед")
        addViewModel.selectCategory("Супы")
        addViewModel.saveRecipe()

        val saved = fakeRecipeDao.getAllFull().first()
        assertEquals("Borscht", saved.recipe.title)
        assertEquals("Супы", saved.recipe.category)
        assertTrue(saved.tags.any { it.name == "Обед" })
        assertTrue(saved.tags.any { it.name == "Супы" })
    }

    @Test
    fun testAddRecipeDynamicIngredientsAndSteps() {
        val addViewModel = AddRecipeViewModel(repository)

        // Dynamic ingredients
        addViewModel.addIngredientLine()
        addViewModel.addIngredientLine()
        assertEquals(2, addViewModel.uiState.value.ingredients.size)

        addViewModel.updateIngredient(0, "200g Flour")
        addViewModel.updateIngredient(1, "100ml Milk")
        assertEquals("200g Flour", addViewModel.uiState.value.ingredients[0])
        assertEquals("100ml Milk", addViewModel.uiState.value.ingredients[1])

        addViewModel.removeIngredient(0)
        assertEquals(1, addViewModel.uiState.value.ingredients.size)
        assertEquals("100ml Milk", addViewModel.uiState.value.ingredients[0])

        // Dynamic steps
        addViewModel.addStepLine()
        addViewModel.addStepLine()
        assertEquals(2, addViewModel.uiState.value.steps.size)

        addViewModel.updateStep(0, "Mix flour and milk")
        addViewModel.updateStep(1, "Bake for 20 minutes")
        assertEquals("Mix flour and milk", addViewModel.uiState.value.steps[0])
        assertEquals("Bake for 20 minutes", addViewModel.uiState.value.steps[1])

        addViewModel.removeStep(0)
        assertEquals(1, addViewModel.uiState.value.steps.size)
        assertEquals("Bake for 20 minutes", addViewModel.uiState.value.steps[0])
    }

    @Test
    fun testAddRecipeSaveRecipePersistsToRepository() = runTest {
        val addViewModel = AddRecipeViewModel(repository)

        addViewModel.updateTitle("Apple Tart")
        addViewModel.updateTime(40)
        addViewModel.updateServings(6)
        addViewModel.updateDifficulty("Medium")
        addViewModel.selectCategory("Dessert")
        addViewModel.addIngredientLine()
        addViewModel.updateIngredient(0, "3 Apples")
        addViewModel.addStepLine()
        addViewModel.updateStep(0, "Slice apples and bake")
        addViewModel.updateNotes("Use Honeycrisp apples")

        assertFalse(addViewModel.uiState.value.isSuccess)
        addViewModel.saveRecipe()

        assertTrue(addViewModel.uiState.value.isSuccess)
        assertFalse(addViewModel.uiState.value.isSaving)

        // Verify in fake repository
        val all = fakeRecipeDao.getAllFull()
        assertEquals(1, all.size)
        val saved = all.first()
        assertEquals("Apple Tart", saved.recipe.title)
        assertEquals(40, saved.recipe.cookingTime)
        assertEquals(6, saved.recipe.servings)
        assertEquals("Medium", saved.recipe.difficulty)
        assertEquals("Dessert", saved.recipe.category)
        assertFalse(saved.recipe.isDraft)
        assertEquals(1, saved.ingredients.size)
        assertEquals("3 Apples", saved.ingredients.first().text)
        assertEquals(1, saved.steps.size)
        assertEquals("Slice apples and bake", saved.steps.first().instruction)
        assertEquals("Use Honeycrisp apples", saved.recipe.notes)
    }

    @Test
    fun testAddRecipeSaveDefaultTitle() = runTest {
        val addViewModel = AddRecipeViewModel(repository)
        addViewModel.updateTitle("")
        addViewModel.saveRecipe()

        assertTrue(addViewModel.uiState.value.isSuccess)
        val saved = fakeRecipeDao.getAllFull().first()
        assertEquals("Новый рецепт", saved.recipe.title)
    }

    @Test
    fun testEditRecipeLoadsAndUpdatesExistingRecipe() = runTest {
        val originalRecipe = createSampleRecipe(
            id = 77L,
            title = "Original Pancakes",
            category = "Breakfast",
            cookingTime = 20,
            servings = 2,
            difficulty = "Easy",
            ingredients = listOf(
                Ingredient(id = 301L, recipeId = 77L, text = "Flour", orderIndex = 1)
            ),
            steps = listOf(
                RecipeStep(id = 401L, recipeId = 77L, stepNumber = 1, instruction = "Mix")
            ),
            notes = "Original notes"
        )
        fakeRecipeDao.addSample(originalRecipe)

        val addViewModel = AddRecipeViewModel(repository)
        addViewModel.loadRecipe(77L)

        // Check that state was loaded
        assertEquals(77L, addViewModel.uiState.value.editingRecipeId)
        assertEquals("Original Pancakes", addViewModel.uiState.value.title)
        assertEquals("Breakfast", addViewModel.uiState.value.category)
        assertEquals(20, addViewModel.uiState.value.cookingTime)
        assertEquals(1, addViewModel.uiState.value.ingredients.size)
        assertEquals("Flour", addViewModel.uiState.value.ingredients[0])

        // Update fields
        addViewModel.updateTitle("Fluffy Pancakes")
        addViewModel.updateTime(25)
        addViewModel.updateIngredient(0, "Flour & Sugar")
        addViewModel.addStepLine()
        addViewModel.updateStep(1, "Cook on medium heat")

        addViewModel.saveRecipe()
        assertTrue(addViewModel.uiState.value.isSuccess)

        // Verify update in repository
        val updated = fakeRecipeDao.getAllFull().first { it.recipe.rid == 77L }
        assertEquals("Fluffy Pancakes", updated.recipe.title)
        assertEquals(25, updated.recipe.cookingTime)
        assertEquals("Flour & Sugar", updated.ingredients.first().text)
        assertEquals(2, updated.steps.size)
    }

    @Test
    fun testDeleteRecipeInEditMode() = runTest {
        val sample = createSampleRecipe(
            id = 88L,
            title = "Recipe To Delete",
            category = "Soup"
        )
        fakeRecipeDao.addSample(sample)
        assertEquals(1, fakeRecipeDao.getAllFull().size)

        val addViewModel = AddRecipeViewModel(repository)
        addViewModel.loadRecipe(88L)
        assertEquals(88L, addViewModel.uiState.value.editingRecipeId)
        assertFalse(addViewModel.uiState.value.isDeleted)

        addViewModel.deleteRecipe()
        assertTrue(addViewModel.uiState.value.isDeleted)

        // Verify it was removed from repository
        assertEquals(0, fakeRecipeDao.getAllFull().size)
    }

    // =========================================================================
    // 3. RecipeDetailViewModel Tests
    // =========================================================================

    @Test
    fun testRecipeDetailLoadAndChecklist() = runTest {
        val recipe = createSampleRecipe(
            id = 55L,
            title = "Pancakes",
            category = "Breakfast",
            ingredients = listOf(
                Ingredient(id = 101L, recipeId = 55L, text = "1 cup flour", orderIndex = 1, isChecked = false),
                Ingredient(id = 102L, recipeId = 55L, text = "2 eggs", orderIndex = 2, isChecked = true),
                Ingredient(id = 103L, recipeId = 55L, text = "1 cup milk", orderIndex = 3, isChecked = false)
            ),
            steps = listOf(
                RecipeStep(id = 201L, recipeId = 55L, stepNumber = 1, instruction = "Whisk ingredients"),
                RecipeStep(id = 202L, recipeId = 55L, stepNumber = 2, instruction = "Cook on pan until golden")
            ),
            notes = "Serve with maple syrup"
        )
        fakeRecipeDao.addSample(recipe)

        val detailViewModel = RecipeDetailViewModel(repository)
        detailViewModel.loadRecipe(55L)

        val state = detailViewModel.uiState.value
        assertNotNull(state.recipe)
        assertEquals("Pancakes", state.recipe!!.recipe.title)
        assertEquals(3, state.totalIngredientsCount)
        assertEquals(1, state.checkedCount)
        assertFalse(state.isLoading)

        // Verify updateLastViewed was invoked
        assertTrue(state.recipe!!.recipe.lastViewedAt != null && state.recipe!!.recipe.lastViewedAt!! > 0L)

        // Toggle checkbox on ingredient 101L (1 cup flour)
        detailViewModel.toggleIngredient(101L, true)
        val updatedState = detailViewModel.uiState.value
        assertEquals(2, updatedState.checkedCount)
        assertTrue(updatedState.recipe!!.ingredients.first { it.id == 101L }.isChecked)

        // Toggle back
        detailViewModel.toggleIngredient(101L, false)
        assertEquals(1, detailViewModel.uiState.value.checkedCount)
    }

    @Test
    fun testRecipeDetailToggleFavorite() = runTest {
        val recipe = createSampleRecipe(
            id = 77L,
            title = "Tomato Soup",
            category = "Soup",
            isFavorite = false
        )
        fakeRecipeDao.addSample(recipe)

        val detailViewModel = RecipeDetailViewModel(repository)
        detailViewModel.loadRecipe(77L)
        assertFalse(detailViewModel.uiState.value.recipe!!.recipe.isFavorite)

        detailViewModel.toggleFavorite()
        assertTrue(detailViewModel.uiState.value.recipe!!.recipe.isFavorite)

        detailViewModel.toggleFavorite()
        assertFalse(detailViewModel.uiState.value.recipe!!.recipe.isFavorite)
    }

    @Test
    fun testRecipeDetailGetShareText() = runTest {
        val recipe = createSampleRecipe(
            id = 99L,
            title = "Berry Smoothie",
            category = "Beverage",
            cookingTime = 5,
            servings = 2,
            difficulty = "Easy",
            ingredients = listOf(
                Ingredient(id = 1L, recipeId = 99L, text = "1 cup berries", orderIndex = 1),
                Ingredient(id = 2L, recipeId = 99L, text = "1 banana", orderIndex = 2)
            ),
            steps = listOf(
                RecipeStep(id = 1L, recipeId = 99L, stepNumber = 1, instruction = "Blend all together"),
                RecipeStep(id = 2L, recipeId = 99L, stepNumber = 2, instruction = "Pour into glass and enjoy")
            ),
            notes = "Add ice cubes for extra chill"
        )
        fakeRecipeDao.addSample(recipe)

        val detailViewModel = RecipeDetailViewModel(repository)
        detailViewModel.loadRecipe(99L)

        val shareText = detailViewModel.getShareText()
        assertTrue(shareText.contains("Berry Smoothie"))
        assertTrue(shareText.contains("1 cup berries"))
        assertTrue(shareText.contains("1 banana"))
        assertTrue(shareText.contains("Blend all together"))
        assertTrue(shareText.contains("Pour into glass and enjoy"))
        assertTrue(shareText.contains("Add ice cubes for extra chill"))
        assertTrue(shareText.contains("Easy"))
        assertTrue(shareText.contains("5 мин"))
    }

    // =========================================================================
    // Helpers & In-Memory Fake DAO
    // =========================================================================

    private fun createSampleRecipe(
        id: Long,
        title: String,
        category: String = "Main",
        cookingTime: Int = 20,
        servings: Int = 2,
        difficulty: String = "Easy",
        isFavorite: Boolean = false,
        isDraft: Boolean = false,
        lastViewedAt: Long? = null,
        ingredients: List<Ingredient> = emptyList(),
        steps: List<RecipeStep> = emptyList(),
        tags: List<Tag> = listOf(Tag(name = category)),
        notes: String? = null
    ): RecipeFullDetails {
        return RecipeFullDetails(
            recipe = Recipe(
                rid = id,
                title = title,
                description = "Description for $title",
                imgPath = null,
                cookingTime = cookingTime,
                servings = servings,
                difficulty = difficulty,
                category = category,
                source = null,
                notes = notes,
                isDraft = isDraft,
                isFavorite = isFavorite,
                createdAt = System.currentTimeMillis(),
                lastViewedAt = lastViewedAt
            ),
            ingredients = ingredients,
            steps = steps,
            tags = tags
        )
    }

    private class FakeRecipeDao : RecipeDao {
        private var idCounter = 1000L
        private val list = mutableListOf<RecipeFullDetails>()
        private val flow = MutableStateFlow<List<RecipeFullDetails>>(emptyList())

        fun addSample(item: RecipeFullDetails) {
            list.add(item)
            flow.value = list.toList()
        }

        fun getAllFull(): List<RecipeFullDetails> = list.toList()

        override suspend fun getAllFullRecipes(): List<RecipeFullDetails> =
            list.filter { !it.recipe.isDraft }

        override fun getRecentlyAdded(): Flow<List<RecipeFullDetails>> =
            flow.map { items -> items.filter { !it.recipe.isDraft }.sortedByDescending { it.recipe.createdAt } }

        override fun getRecentlyOpened(): Flow<List<RecipeFullDetails>> =
            flow.map { items -> items.filter { it.recipe.lastViewedAt != null }.sortedByDescending { it.recipe.lastViewedAt } }

        override fun getFavorites(): Flow<List<RecipeFullDetails>> =
            flow.map { items -> items.filter { it.recipe.isFavorite }.sortedByDescending { it.recipe.createdAt } }

        override fun getRecipeById(rid: Long): Flow<RecipeFullDetails?> =
            flow.map { items -> items.firstOrNull { it.recipe.rid == rid } }

        override suspend fun updateLastViewed(rid: Long, timestamp: Long) {
            val idx = list.indexOfFirst { it.recipe.rid == rid }
            if (idx != -1) {
                val current = list[idx]
                list[idx] = current.copy(recipe = current.recipe.copy(lastViewedAt = timestamp))
                flow.value = list.toList()
            }
        }

        override suspend fun toggleFavorite(rid: Long, isFavorite: Boolean) {
            val idx = list.indexOfFirst { it.recipe.rid == rid }
            if (idx != -1) {
                val current = list[idx]
                list[idx] = current.copy(recipe = current.recipe.copy(isFavorite = isFavorite))
                flow.value = list.toList()
            }
        }

        override suspend fun setIngredientChecked(ingredientId: Long, isChecked: Boolean) {
            for (i in list.indices) {
                val item = list[i]
                if (item.ingredients.any { it.id == ingredientId }) {
                    val updatedIng = item.ingredients.map {
                        if (it.id == ingredientId) it.copy(isChecked = isChecked) else it
                    }
                    list[i] = item.copy(ingredients = updatedIng)
                    flow.value = list.toList()
                    break
                }
            }
        }

        override suspend fun insertRecipe(recipe: Recipe): Long = recipe.rid.takeIf { it != 0L } ?: ++idCounter

        override suspend fun insertIngredients(ingredients: List<Ingredient>): List<Long> =
            ingredients.map { it.id.takeIf { id -> id != 0L } ?: ++idCounter }

        override suspend fun insertSteps(steps: List<RecipeStep>): List<Long> =
            steps.map { it.id.takeIf { id -> id != 0L } ?: ++idCounter }

        override suspend fun insertRecipeTags(crossRef: List<RecipeTagCross>) {}

        override suspend fun insertTag(tag: Tag): Long = tag.tid.takeIf { it != 0L } ?: ++idCounter

        override suspend fun getTagByName(name: String): Tag? = null

        override suspend fun insertFullRecipe(
            recipe: Recipe,
            ingredients: List<Ingredient>,
            steps: List<RecipeStep>,
            tags: List<Tag>
        ): Long {
            val recipeId = if (recipe.rid != 0L) recipe.rid else ++idCounter
            val fullRecipe = RecipeFullDetails(
                recipe = recipe.copy(rid = recipeId),
                ingredients = ingredients.mapIndexed { idx, ing ->
                    ing.copy(id = if (ing.id != 0L) ing.id else ++idCounter, recipeId = recipeId)
                },
                steps = steps.mapIndexed { idx, stp ->
                    stp.copy(id = if (stp.id != 0L) stp.id else ++idCounter, recipeId = recipeId)
                },
                tags = tags
            )
            list.add(fullRecipe)
            flow.value = list.toList()
            return recipeId
        }

        override suspend fun insertFullRecipe(details: RecipeFullDetails): Long =
            insertFullRecipe(details.recipe, details.ingredients, details.steps, details.tags)

        override suspend fun count(): Int = list.size

        override suspend fun getAll(): List<Recipe> = list.map { it.recipe }

        override suspend fun findByTitle(search: String): List<Recipe> =
            list.filter { it.recipe.title.contains(search, ignoreCase = true) }.map { it.recipe }

        override fun getRecipesWithTags(): Flow<List<RecipeWithTags>> =
            flow.map { items -> items.map { RecipeWithTags(it.recipe, it.tags) } }

        override suspend fun insertRecipeWithTags(recipe: Recipe, tags: List<Tag>) {}

        override suspend fun deleteAllRecipes() {
            list.clear()
            flow.value = emptyList()
        }

        override suspend fun deleteRecipe(vararg recipe: Recipe) {
            val rids = recipe.map { it.rid }.toSet()
            list.removeAll { it.recipe.rid in rids }
            flow.value = list.toList()
        }

        override suspend fun deleteRecipeById(recipeId: Long) {
            list.removeAll { it.recipe.rid == recipeId }
            flow.value = list.toList()
        }

        override suspend fun deleteIngredientsByRecipeId(recipeId: Long) {}
        override suspend fun deleteStepsByRecipeId(recipeId: Long) {}
        override suspend fun deleteRecipeTagsByRecipeId(recipeId: Long) {}

        override suspend fun updateRecipe(recipe: Recipe) {
            val idx = list.indexOfFirst { it.recipe.rid == recipe.rid }
            if (idx != -1) {
                list[idx] = list[idx].copy(recipe = recipe)
                flow.value = list.toList()
            }
        }

        override suspend fun updateFullRecipe(
            recipe: Recipe,
            ingredients: List<Ingredient>,
            steps: List<RecipeStep>,
            tags: List<Tag>
        ) {
            val idx = list.indexOfFirst { it.recipe.rid == recipe.rid }
            val fullRecipe = RecipeFullDetails(
                recipe = recipe,
                ingredients = ingredients.mapIndexed { _, ing ->
                    ing.copy(id = if (ing.id != 0L) ing.id else ++idCounter, recipeId = recipe.rid)
                },
                steps = steps.mapIndexed { _, stp ->
                    stp.copy(id = if (stp.id != 0L) stp.id else ++idCounter, recipeId = recipe.rid)
                },
                tags = tags
            )
            if (idx != -1) {
                list[idx] = fullRecipe
            } else {
                list.add(fullRecipe)
            }
            flow.value = list.toList()
        }
    }

    private class FakeTagDao : TagDao {
        private val tags = mutableListOf<Tag>()
        override suspend fun getAll(): List<Tag> = tags.toList()
        override suspend fun insertTag(tag: Tag): Long {
            tags.add(tag)
            return if (tag.tid != 0L) tag.tid else (tags.size.toLong())
        }
        override suspend fun insertRecipeTag(crossRef: RecipeTagCross) {}
        override suspend fun deleteTag(vararg tag: Tag) { tags.removeAll(tag.toSet()) }
        override suspend fun deleteRecipeTag(crossRef: RecipeTagCross) {}
    }
}
