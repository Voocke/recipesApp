package com.example.recipes.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.recipes.data.local.entities.Recipe
import com.example.recipes.presentation.add_recipe.translateCategoryToRussian
import com.example.recipes.ui.components.CategoryChip
import com.example.recipes.ui.components.CraftBottomBar
import com.example.recipes.ui.components.CraftNavItem
import com.example.recipes.ui.components.ScotchTape
import com.example.recipes.ui.components.dashedBorder
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.HandwrittenFamily
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.InkWashed
import com.example.recipes.ui.theme.PaperCream
import com.example.recipes.ui.theme.Terracotta
import com.example.recipes.ui.theme.getCategoryColor

enum class ShowcaseSection {
    RECENTLY_OPENED,
    BOOKMARKS
}

/**
 * Formats cooking time into craft compact text, e.g. "1 ч 10 мин", "20 мин".
 */
fun formatCookingTime(minutes: Int): String {
    return when {
        minutes <= 0 -> "15 мин"
        minutes >= 60 -> {
            val hours = minutes / 60
            val rem = minutes % 60
            if (rem > 0) "$hours ч $rem мин" else "$hours ч"
        }
        else -> "$minutes мин"
    }
}

private val DefaultCategories = listOf("Завтрак", "Обед", "Ужин", "Супы", "Горячее", "Салаты", "Закуски", "Паста", "Выпечка", "Десерты", "Напитки")

private val GridCardShape = RoundedCornerShape(12.dp)
private val PhotoCornerShape = RoundedCornerShape(8.dp)
private val CategoryBadgeShape = RoundedCornerShape(6.dp)
private val PolaroidCardShape = RoundedCornerShape(8.dp)
private val PolaroidPhotoShape = RoundedCornerShape(4.dp)
private val TabShape = RoundedCornerShape(14.dp)
private val CardBorderColor = DottedBorderColor.copy(alpha = 0.6f)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (Long) -> Unit,
    onAddRecipeClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentNavTab by rememberSaveable { mutableStateOf(CraftNavItem.AllRecipes.route) }
    var currentShowcaseSection by rememberSaveable { mutableStateOf(ShowcaseSection.RECENTLY_OPENED) }

    val recentlyOpenedListState = rememberLazyListState()
    val bookmarksListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currentShowcaseListState = when (currentShowcaseSection) {
        ShowcaseSection.RECENTLY_OPENED -> recentlyOpenedListState
        ShowcaseSection.BOOKMARKS -> bookmarksListState
    }

    LaunchedEffect(currentShowcaseSection) {
        currentShowcaseListState.scrollToItem(0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PaperCream,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecipeClick,
                containerColor = Terracotta,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить рецепт",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        bottomBar = {
            CraftBottomBar(
                currentRoute = currentNavTab,
                onNavigate = { route ->
                    if (route == CraftNavItem.Settings.route) {
                        onSettingsClick()
                    } else {
                        currentNavTab = route
                        if (route == CraftNavItem.Recent.route) {
                            currentShowcaseSection = ShowcaseSection.RECENTLY_OPENED
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val displayedRecipes = remember(currentNavTab, uiState.recentlyAdded, uiState.recentlyOpened) {
            if (currentNavTab == CraftNavItem.Recent.route) {
                uiState.recentlyOpened
            } else {
                uiState.recentlyAdded
            }
        }
        val sectionTitle = if (currentNavTab == CraftNavItem.Recent.route) "Недавно открытые" else "Недавно добавленные"

        val showcaseItems = remember(currentShowcaseSection, uiState.recentlyOpened, uiState.favorites, uiState.recentlyAdded) {
            when (currentShowcaseSection) {
                ShowcaseSection.RECENTLY_OPENED -> {
                    if (uiState.recentlyOpened.isNotEmpty()) uiState.recentlyOpened else uiState.recentlyAdded.take(5)
                }
                ShowcaseSection.BOOKMARKS -> {
                    if (uiState.favorites.isNotEmpty()) uiState.favorites else uiState.recentlyAdded.take(5)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Top App Header
            item(span = { GridItemSpan(2) }, contentType = "header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Книга рецептов",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp,
                        color = InkDark
                    )
                    Text(
                        text = "С любовью и семейными традициями",
                        fontFamily = HandwrittenFamily,
                        fontSize = 18.sp,
                        color = InkWashed
                    )
                }
            }

            // 2. Showcase Header with Sections for Recently Opened and Bookmarks
            item(span = { GridItemSpan(2) }, contentType = "showcase") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShowcaseTabButton(
                            title = "Недавние",
                            isSelected = currentShowcaseSection == ShowcaseSection.RECENTLY_OPENED,
                            count = uiState.recentlyOpened.size,
                            onClick = {
                                if (currentShowcaseSection == ShowcaseSection.RECENTLY_OPENED) {
                                    coroutineScope.launch { recentlyOpenedListState.animateScrollToItem(0) }
                                } else {
                                    currentShowcaseSection = ShowcaseSection.RECENTLY_OPENED
                                }
                            }
                        )

                        ShowcaseTabButton(
                            title = "Избранное",
                            isSelected = currentShowcaseSection == ShowcaseSection.BOOKMARKS,
                            count = uiState.favorites.size,
                            onClick = {
                                if (currentShowcaseSection == ShowcaseSection.BOOKMARKS) {
                                    coroutineScope.launch { bookmarksListState.animateScrollToItem(0) }
                                } else {
                                    currentShowcaseSection = ShowcaseSection.BOOKMARKS
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (showcaseItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .dashedBorder(
                                    color = DottedBorderColor,
                                    strokeWidth = 1.5.dp,
                                    cornerRadius = 12.dp
                                )
                                .background(BakedMilk.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentShowcaseSection == ShowcaseSection.RECENTLY_OPENED) {
                                    "Здесь появятся недавно открытые рецепты"
                                } else {
                                    "Добавляйте рецепты в избранное, чтобы они были под рукой"
                                },
                                fontFamily = HandwrittenFamily,
                                fontSize = 16.sp,
                                color = InkWashed,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        // Horizontal LazyRow of Polaroid cards
                        LazyRow(
                            state = currentShowcaseListState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(205.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = showcaseItems,
                                key = { "${currentShowcaseSection.name}_${it.recipe.rid}" },
                                contentType = { "showcase_polaroid" }
                            ) { details ->
                                ShowcasePolaroidCard(
                                    recipe = details.recipe,
                                    onClick = { onRecipeClick(details.recipe.rid) }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Search Bar above list of categories/tags
            item(span = { GridItemSpan(2) }, contentType = "search_bar") {
                RecipeSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // 4. Category Chips Row
            item(span = { GridItemSpan(2) }, contentType = "categories") {
                val categoriesList = remember(uiState.recentlyAdded) {
                    val dynamicCategories = uiState.recentlyAdded.flatMap { details ->
                        listOf(translateCategoryToRussian(details.recipe.category)) + details.tags.map { translateCategoryToRussian(it.name) }
                    }.filter { it.isNotBlank() }
                    (DefaultCategories + dynamicCategories).distinctBy { it.lowercase().trim() }
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .padding(top = 2.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = categoriesList,
                        key = { it },
                        contentType = { "category_chip" }
                    ) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = uiState.selectedCategory.equals(category, ignoreCase = true),
                            onClick = { viewModel.onCategorySelected(category) }
                        )
                    }
                }
            }

            // 4. "Recently added" Section Header with Decorative Stroke
            item(span = { GridItemSpan(2) }, contentType = "section_title") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = sectionTitle,
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = InkDark
                    )
                    // Decorative hand-drawn stroke flourish cached once per measurement
                    Spacer(
                        modifier = Modifier
                            .width(190.dp)
                            .height(8.dp)
                            .padding(top = 2.dp)
                            .drawWithCache {
                                val path = Path().apply {
                                    moveTo(2f, size.height * 0.65f)
                                    quadraticTo(
                                        size.width * 0.45f,
                                        1f,
                                        size.width * 0.82f,
                                        size.height * 0.8f
                                    )
                                    quadraticTo(
                                        size.width * 0.94f,
                                        size.height * 0.4f,
                                        size.width,
                                        size.height * 0.65f
                                    )
                                }
                                val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                onDrawBehind {
                                    drawPath(
                                        path = path,
                                        color = Terracotta,
                                        style = stroke
                                    )
                                }
                            }
                    )
                }
            }

            // 5. Vertical Grid Items (2 columns)
            if (displayedRecipes.isEmpty()) {
                item(span = { GridItemSpan(2) }, contentType = "empty_search_state") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RestaurantMenu,
                            contentDescription = null,
                            tint = Terracotta.copy(alpha = 0.45f),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) {
                                "Рецепты по запросу «${uiState.searchQuery}» не найдены"
                            } else if (currentNavTab == CraftNavItem.Recent.route) {
                                "Нет недавно открытых рецептов"
                            } else {
                                "Ваша кулинарная книга пока пуста"
                            },
                            fontFamily = HandwrittenFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = InkDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) {
                                "Попробуйте изменить поисковый запрос"
                            } else {
                                "Запишите свой первый семейный рецепт!"
                            },
                            fontFamily = HandwrittenFamily,
                            fontSize = 16.sp,
                            color = InkWashed,
                            textAlign = TextAlign.Center
                        )

                        if (uiState.searchQuery.isBlank()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onAddRecipeClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Terracotta
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Добавить рецепт",
                                    fontFamily = HandwrittenFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                items(
                    items = displayedRecipes,
                    key = { it.recipe.rid },
                    contentType = { "recipe_grid_card" }
                ) { item ->
                    RecipeGridCard(
                        recipe = item.recipe,
                        onRecipeClick = { onRecipeClick(item.recipe.rid) },
                        onToggleFavorite = { isFav ->
                            viewModel.onToggleFavorite(item.recipe.rid, isFav)
                        },
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 12.dp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Craft search bar for finding recipes by title.
 */
@Composable
fun RecipeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Поиск рецепта по названию...",
                fontFamily = HandwrittenFamily,
                fontSize = 18.sp,
                color = InkWashed
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск",
                tint = if (query.isNotBlank()) Terracotta else InkWashed
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Очистить поиск",
                        tint = InkWashed
                    )
                }
            }
        },
        textStyle = TextStyle(
            fontFamily = HandwrittenFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = InkDark
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Terracotta,
            unfocusedBorderColor = DottedBorderColor,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = Terracotta
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Filter tab button for showcase sections (Recently Opened / Bookmarks).
 */
@Composable
private fun ShowcaseTabButton(
    title: String,
    isSelected: Boolean,
    count: Int,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Terracotta else BakedMilk
    val textColor = if (isSelected) Color.White else InkDark
    val borderModifier = if (isSelected) Modifier else Modifier.border(1.dp, DottedBorderColor, TabShape)

    Box(
        modifier = Modifier
            .clip(TabShape)
            .background(bgColor, TabShape)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                fontFamily = HandwrittenFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 17.sp,
                color = textColor
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else DottedBorderColor)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "$count",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}

/**
 * Upper showcase card styled like a classic white Polaroid photo with corner tape.
 */
@Composable
private fun ShowcasePolaroidCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(170.dp)
            .padding(top = 8.dp) // Room for top tape
    ) {
        // Polaroid Frame Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp, PolaroidCardShape)
                .clip(PolaroidCardShape)
                .background(Color.White)
                .border(1.dp, CardBorderColor, PolaroidCardShape)
                .clickable(onClick = onClick)
                .padding(8.dp)
        ) {
            // Photo or Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(PolaroidPhotoShape)
                    .background(BakedMilk),
                contentAlignment = Alignment.Center
            ) {
                if (!recipe.imgPath.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.imgPath)
                            .size(width = 400, height = 300)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(PolaroidPhotoShape)
                    )
                } else {
                    RecipePlaceholder(category = recipe.category)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Handwritten signature below photo
            Text(
                text = recipe.title,
                fontFamily = HandwrittenFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = InkDark
            )

            val formattedTime = remember(recipe.cookingTime) { formatCookingTime(recipe.cookingTime) }
            Text(
                text = "${recipe.category} • $formattedTime",
                fontFamily = HandwrittenFamily,
                fontSize = 14.sp,
                color = InkWashed
            )
        }

        // Scotch tape strips at the top corners
        ScotchTape(
            rotationDegrees = -25f,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-10).dp, y = (-4).dp)
                .size(48.dp, 16.dp)
        )

        ScotchTape(
            rotationDegrees = 25f,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 10.dp, y = (-4).dp)
                .size(48.dp, 16.dp)
        )
    }
}

/**
 * Card for the 2-column LazyVerticalGrid.
 */
@Composable
private fun RecipeGridCard(
    recipe: Recipe,
    onRecipeClick: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = remember(recipe.category) { getCategoryColor(recipe.category) }
    val formattedTime = remember(recipe.cookingTime) { formatCookingTime(recipe.cookingTime) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp) // Room for top tape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, GridCardShape)
                .clip(GridCardShape)
                .background(Color.White)
                .border(1.dp, CardBorderColor, GridCardShape)
                .clickable(onClick = onRecipeClick)
                .padding(8.dp)
        ) {
            // Photo box with tape on corner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(PhotoCornerShape)
                    .background(BakedMilk),
                contentAlignment = Alignment.Center
            ) {
                if (!recipe.imgPath.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.imgPath)
                            .size(width = 400, height = 300)
                            .crossfade(true)
                            .build(),
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(PhotoCornerShape)
                    )
                } else {
                    RecipePlaceholder(category = recipe.category)
                }

                // Bookmark toggle on top-right
                IconButton(
                    onClick = { onToggleFavorite(!recipe.isFavorite) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "В избранное",
                        tint = if (recipe.isFavorite) Terracotta else InkWashed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Handwritten title
            Text(
                text = recipe.title,
                fontFamily = HandwrittenFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = InkDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Footer: Colored category badge + time with clock icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored category badge
                Box(
                    modifier = Modifier
                        .clip(CategoryBadgeShape)
                        .background(categoryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = recipe.category,
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = categoryColor
                    )
                }

                // Time with clock icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Время приготовления",
                        tint = InkWashed,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = InkWashed,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Scotch tape on corner
        ScotchTape(
            rotationDegrees = -20f,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-6).dp, y = (-2).dp)
                .size(44.dp, 14.dp)
        )
    }
}

/**
 * Placeholder graphic when recipe has no custom photo.
 */
@Composable
private fun RecipePlaceholder(
    category: String,
    modifier: Modifier = Modifier
) {
    val color = remember(category) { getCategoryColor(category) }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.RestaurantMenu,
            contentDescription = null,
            tint = color.copy(alpha = 0.5f),
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = category,
            fontFamily = HandwrittenFamily,
            fontSize = 14.sp,
            color = color.copy(alpha = 0.7f)
        )
    }
}
