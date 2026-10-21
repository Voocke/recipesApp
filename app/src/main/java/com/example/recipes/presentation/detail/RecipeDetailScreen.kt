package com.example.recipes.presentation.detail

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.recipes.presentation.home.formatCookingTime
import com.example.recipes.ui.components.RecipeParamPill
import com.example.recipes.ui.components.StickyNote
import com.example.recipes.ui.components.WavyCutShape
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.HandwrittenFamily
import com.example.recipes.ui.theme.HeadlineLarge
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.InkWashed
import com.example.recipes.ui.theme.PaperCream
import com.example.recipes.ui.theme.SageGreen
import com.example.recipes.ui.theme.Terracotta
import com.example.recipes.ui.theme.getCategoryColor

fun formatDifficulty(difficulty: String): String = when (difficulty.lowercase().trim()) {
    "easy", "легко", "простой" -> "Легко"
    "medium", "средне", "средний" -> "Средне"
    "hard", "сложно", "сложный" -> "Сложно"
    else -> difficulty
}

fun formatServings(count: Int): String {
    val rem10 = count % 10
    val rem100 = count % 100
    return when {
        rem100 in 11..19 -> "$count порций"
        rem10 == 1 -> "$count порция"
        rem10 in 2..4 -> "$count порции"
        else -> "$count порций"
    }
}

private val HeaderWavyShape = WavyCutShape(waveCount = 18, waveHeight = 12.dp)

@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onEditRecipe: () -> Unit = {},
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    val details = uiState.recipe

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PaperCream,
        bottomBar = {
            if (details != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BakedMilk,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Dotted divider line
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                        ) {
                            drawLine(
                                color = DottedBorderColor,
                                start = Offset(0f, 1f),
                                end = Offset(size.width, 1f),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Edit button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onEditRecipe() }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "Редактировать рецепт",
                                    tint = Terracotta,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Изменить",
                                    fontFamily = HandwrittenFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Terracotta
                                )
                            }

                            // Bookmark / Save button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.toggleFavorite() }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (details.recipe.isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = if (details.recipe.isFavorite) "Сохранено в избранное" else "В избранное",
                                    tint = Terracotta,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (details.recipe.isFavorite) "В избранном" else "В избранное",
                                    fontFamily = HandwrittenFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Terracotta
                                )
                            }

                            // Share button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        val shareText = viewModel.getShareText()
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Поделиться рецептом")
                                        context.startActivity(shareIntent)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Поделиться рецептом",
                                    tint = Terracotta,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Поделиться",
                                    fontFamily = HandwrittenFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Terracotta
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading || details == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Terracotta)
            }
        } else {
            val recipe = details.recipe
            val cuisineName = details.tags.firstOrNull()?.name ?: recipe.category
            val cuisineColor = getCategoryColor(cuisineName)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Header: Full width dish photo with custom WavyCutShape and circular back button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    if (!recipe.imgPath.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(recipe.imgPath)
                                .size(width = 800, height = 600)
                                .crossfade(true)
                                .build(),
                            contentDescription = recipe.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(HeaderWavyShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(HeaderWavyShape)
                                .background(BakedMilk),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Outlined.RestaurantMenu,
                                    contentDescription = null,
                                    tint = cuisineColor.copy(alpha = 0.45f),
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = recipe.category,
                                    fontFamily = HandwrittenFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = cuisineColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Circular back button over photo
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .statusBarsPadding()
                            .padding(start = 16.dp, top = 16.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(PaperCream.copy(alpha = 0.92f))
                            .size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = InkDark
                        )
                    }

                    // Circular edit button over photo
                    IconButton(
                        onClick = onEditRecipe,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(end = 16.dp, top = 16.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(PaperCream.copy(alpha = 0.92f))
                            .size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Редактировать рецепт",
                            tint = InkDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Info Block: Title, Cuisine Badge, Source Subtitle, 3 Param Pills
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Cuisine & Category badges
                    val badges = remember(details) {
                        val list = mutableListOf<String>()
                        if (recipe.category.isNotBlank()) {
                            list.add(recipe.category)
                        }
                        details.tags.forEach { tag ->
                            if (tag.name.isNotBlank() && list.none { it.equals(tag.name, ignoreCase = true) }) {
                                list.add(tag.name)
                            }
                        }
                        list
                    }

                    if (badges.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            badges.forEach { badgeText ->
                                val badgeColor = getCategoryColor(badgeText)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(badgeColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        fontFamily = HandwrittenFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = badgeColor
                                    )
                                }
                            }
                        }
                    }

                    // Handwritten style title
                    Text(
                        text = recipe.title,
                        style = HeadlineLarge,
                        color = InkDark
                    )

                    // Source subtitle in italics
                    if (!recipe.source.isNullOrBlank()) {
                        Text(
                            text = "от ${recipe.source}",
                            fontFamily = HandwrittenFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 18.sp,
                            color = InkWashed
                        )
                    }

                    // 3 Param Pills: TIME, SERVES, DIFFICULTY
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RecipeParamPill(
                            icon = Icons.Outlined.Timer,
                            label = "Время",
                            value = formatCookingTime(recipe.cookingTime),
                            modifier = Modifier.weight(1f)
                        )

                        RecipeParamPill(
                            icon = Icons.Outlined.People,
                            label = "Порции",
                            value = formatServings(recipe.servings),
                            modifier = Modifier.weight(1f)
                        )

                        RecipeParamPill(
                            icon = Icons.Outlined.DinnerDining,
                            label = "Сложность",
                            value = formatDifficulty(recipe.difficulty),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // 3. Block «Ingredients»: Interactive checklist with live counter (0/7 checked)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ингредиенты",
                            fontFamily = HandwrittenFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = InkDark
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SageGreen.copy(alpha = 0.18f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${uiState.checkedCount}/${uiState.totalIngredientsCount} готово",
                                fontFamily = HandwrittenFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SageGreen
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, DottedBorderColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        details.ingredients.sortedBy { it.orderIndex }.forEach { ingredient ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.toggleIngredient(ingredient.id, !ingredient.isChecked)
                                    }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = ingredient.isChecked,
                                    onCheckedChange = { isChecked ->
                                        viewModel.toggleIngredient(ingredient.id, isChecked)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = SageGreen,
                                        uncheckedColor = DottedBorderColor
                                    )
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = ingredient.text,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDecoration = if (ingredient.isChecked) TextDecoration.LineThrough else null
                                    ),
                                    color = if (ingredient.isChecked) InkWashed else InkDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // 4. Block «Method»: Step-by-step instructions in numbered cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Способ приготовления",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = InkDark
                    )

                    details.steps.sortedBy { it.stepNumber }.forEach { step ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(BakedMilk)
                                .border(1.dp, DottedBorderColor, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Terracotta),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${step.stepNumber}",
                                        color = Color.White,
                                        fontFamily = HandwrittenFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = step.instruction,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = InkDark,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // 5. Block «Notes»: Tilted yellow sticker with red pushpin
                if (!recipe.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Заметки шефа",
                            fontFamily = HandwrittenFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = InkDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        StickyNote(
                            noteText = recipe.notes,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
