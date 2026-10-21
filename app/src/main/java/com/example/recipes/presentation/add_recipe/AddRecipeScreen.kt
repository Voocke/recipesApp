package com.example.recipes.presentation.add_recipe

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.recipes.ui.components.CategoryChip
import com.example.recipes.ui.components.DashedCard
import com.example.recipes.ui.components.ScotchTape
import com.example.recipes.ui.components.dashedBorder
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.HandwrittenFamily
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.InkWashed
import com.example.recipes.ui.theme.PaperCream
import com.example.recipes.ui.theme.SageGreen
import com.example.recipes.ui.theme.StickerYellow
import com.example.recipes.ui.theme.Terracotta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    recipeId: Long? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onRecipeDeleted: () -> Unit = onNavigateBack,
    viewModel: AddRecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var showAddCategoryDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmationDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        if (recipeId != null && recipeId > 0L) {
            viewModel.loadRecipe(recipeId)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onRecipeDeleted()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onImagePicked(uri)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PaperCream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.editingRecipeId != null) "Редактировать рецепт" else "Новый рецепт",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = InkDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = InkDark
                        )
                    }
                },
                actions = {
                    // Delete button in edit mode
                    if (uiState.editingRecipeId != null) {
                        IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Удалить рецепт",
                                tint = Terracotta
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperCream
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Photo Zone: Dashed card with scotch tape at corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                DashedCard(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    backgroundColor = BakedMilk,
                    borderColor = DottedBorderColor,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (!uiState.imagePath.isNullOrBlank()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = uiState.imagePath,
                                contentDescription = "Recipe photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Изменить фото",
                                    fontFamily = HandwrittenFamily,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddAPhoto,
                                contentDescription = "Добавить фото блюда",
                                tint = Terracotta,
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Добавить фото блюда",
                                fontFamily = HandwrittenFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = InkDark
                            )
                            Text(
                                text = "Нажмите, чтобы выбрать из галереи",
                                fontFamily = HandwrittenFamily,
                                fontSize = 14.sp,
                                color = InkWashed
                            )
                        }
                    }
                }

                // Scotch tape on corners
                ScotchTape(
                    rotationDegrees = -25f,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-8).dp, y = (-6).dp)
                        .size(52.dp, 16.dp)
                )

                ScotchTape(
                    rotationDegrees = 25f,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-6).dp)
                        .size(52.dp, 16.dp)
                )
            }

            // 2. Title Field: "Recipe title..." with counter "0/80"
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Название рецепта...",
                        fontFamily = HandwrittenFamily,
                        fontSize = 20.sp,
                        color = InkWashed
                    )
                },
                supportingText = {
                    Text(
                        text = uiState.charCount,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontFamily = HandwrittenFamily,
                        fontSize = 14.sp,
                        color = InkWashed
                    )
                },
                textStyle = TextStyle(
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = InkDark
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = DottedBorderColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // 3. 3 Metadata Cards: TIME, SERVES, LEVEL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TIME Card
                MetadataAdjustCard(
                    icon = Icons.Outlined.Timer,
                    label = "ВРЕМЯ",
                    value = "${uiState.cookingTime} мин",
                    onDecrease = { viewModel.updateTime(uiState.cookingTime - 5) },
                    onIncrease = { viewModel.updateTime(uiState.cookingTime + 5) },
                    modifier = Modifier.weight(1f)
                )

                // SERVES Card
                MetadataAdjustCard(
                    icon = Icons.Outlined.People,
                    label = "ПОРЦИИ",
                    value = "${uiState.servings}",
                    onDecrease = { viewModel.updateServings(uiState.servings - 1) },
                    onIncrease = { viewModel.updateServings(uiState.servings + 1) },
                    modifier = Modifier.weight(1f)
                )

                // LEVEL Card
                MetadataLevelCard(
                    icon = Icons.Outlined.DinnerDining,
                    label = "СЛОЖНОСТЬ",
                    level = uiState.difficulty,
                    onCycleLevel = {
                        val next = when (uiState.difficulty) {
                            "Легко" -> "Средне"
                            "Средне" -> "Сложно"
                            "Easy" -> "Medium"
                            "Medium" -> "Hard"
                            else -> "Легко"
                        }
                        viewModel.updateDifficulty(next)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // 4A. Block «Meal Type» (Приём пищи: Завтрак / Обед / Ужин)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Приём пищи",
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = InkDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MealTypeOptions.forEach { meal ->
                        CategoryChip(
                            category = meal,
                            isSelected = uiState.mealType.equals(meal, ignoreCase = true),
                            onClick = { viewModel.selectMealType(meal) }
                        )
                    }
                }
            }

            // 4B. Block «Category»: Standard + custom categories + "+ Добавить"
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Категория блюда",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = InkDark
                    )

                    if (uiState.category.isNotBlank() && !MealTypeOptions.any { it.equals(uiState.category, ignoreCase = true) }) {
                        Text(
                            text = uiState.category,
                            fontFamily = HandwrittenFamily,
                            fontSize = 15.sp,
                            color = Terracotta
                        )
                    }
                }

                val allCategories = remember(uiState.customCategories) {
                    (DefaultCategoryOptions + uiState.customCategories).distinct()
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    item {
                        AddCategoryButton(
                            onClick = { showAddCategoryDialog = true }
                        )
                    }

                    items(allCategories) { cat ->
                        CategoryChip(
                            category = cat,
                            isSelected = uiState.category.equals(cat, ignoreCase = true),
                            onClick = { viewModel.selectCategory(cat) }
                        )
                    }
                }
            }

            // 5. Block «Ingredients»: Count badge, list with checkboxes, "+ add line" button in dashed border
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ингредиенты",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = InkDark
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Terracotta.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${uiState.ingredients.size}",
                            fontFamily = HandwrittenFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Terracotta
                        )
                    }
                }

                // Dynamic Ingredient lines
                uiState.ingredients.forEachIndexed { index, ingredient ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = DottedBorderColor
                            ),
                            modifier = Modifier.size(36.dp)
                        )

                        OutlinedTextField(
                            value = ingredient,
                            onValueChange = { viewModel.updateIngredient(index, it) },
                            placeholder = {
                                Text(
                                    text = "например, 200 г муки",
                                    fontFamily = HandwrittenFamily,
                                    fontSize = 16.sp,
                                    color = InkWashed
                                )
                            },
                            textStyle = TextStyle(
                                fontFamily = HandwrittenFamily,
                                fontSize = 17.sp,
                                color = InkDark
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Terracotta,
                                unfocusedBorderColor = DottedBorderColor,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { viewModel.removeIngredient(index) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить строку",
                                tint = InkWashed
                            )
                        }
                    }
                }

                // "+ add line" button in dashed border
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .dashedBorder(
                            color = DottedBorderColor,
                            strokeWidth = 1.5.dp,
                            cornerRadius = 10.dp
                        )
                        .clickable { viewModel.addIngredientLine() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить строку",
                            tint = Terracotta,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ добавить строку",
                            fontFamily = HandwrittenFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Terracotta
                        )
                    }
                }
            }

            // 6. Block «Method»: Numbered steps, "+ add step" button in dashed border
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Способ приготовления",
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = InkDark
                )

                // Dynamic Step lines
                uiState.steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 10.dp, end = 8.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Terracotta),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                fontFamily = HandwrittenFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        OutlinedTextField(
                            value = step,
                            onValueChange = { viewModel.updateStep(index, it) },
                            placeholder = {
                                Text(
                                    text = "Опишите шаг ${index + 1}...",
                                    fontFamily = HandwrittenFamily,
                                    fontSize = 16.sp,
                                    color = InkWashed
                                )
                            },
                            textStyle = TextStyle(
                                fontFamily = HandwrittenFamily,
                                fontSize = 17.sp,
                                color = InkDark
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Terracotta,
                                unfocusedBorderColor = DottedBorderColor,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { viewModel.removeStep(index) },
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить шаг",
                                tint = InkWashed
                            )
                        }
                    }
                }

                // "+ add step" button in dashed border
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .dashedBorder(
                            color = DottedBorderColor,
                            strokeWidth = 1.5.dp,
                            cornerRadius = 10.dp
                        )
                        .clickable { viewModel.addStepLine() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить шаг",
                            tint = Terracotta,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ добавить шаг",
                            fontFamily = HandwrittenFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Terracotta
                        )
                    }
                }
            }

            // 7. Block «Notes»: Notes input field
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Заметки",
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = InkDark
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(10.dp))
                        .background(StickerYellow, RoundedCornerShape(10.dp))
                        .border(1.dp, DottedBorderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = { viewModel.updateNotes(it) },
                        placeholder = {
                            Text(
                                text = "Секреты шефа, советы, подача...",
                                fontFamily = HandwrittenFamily,
                                fontSize = 17.sp,
                                color = InkWashed
                            )
                        },
                        textStyle = TextStyle(
                            fontFamily = HandwrittenFamily,
                            fontSize = 18.sp,
                            color = InkDark
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 8. Save Recipe Primary Button
            Button(
                onClick = { viewModel.saveRecipe() },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Terracotta
                )
            ) {
                Text(
                    text = when {
                        uiState.isSaving -> "Сохранение..."
                        uiState.editingRecipeId != null -> "Сохранить изменения"
                        else -> "Сохранить рецепт"
                    },
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

            // 9. Delete Recipe Button (in edit mode)
            if (uiState.editingRecipeId != null) {
                OutlinedButton(
                    onClick = { showDeleteConfirmationDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Terracotta.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Terracotta
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = Terracotta,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Удалить рецепт",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = Terracotta
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = {
                Text(
                    text = "Удалить рецепт?",
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = InkDark
                )
            },
            text = {
                Text(
                    text = "Вы действительно хотите удалить рецепт «${uiState.title.ifBlank { "Без названия" }}»?\n\nЭто действие нельзя будет отменить.",
                    fontFamily = HandwrittenFamily,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    color = InkDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmationDialog = false
                        viewModel.deleteRecipe()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terracotta,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Удалить",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmationDialog = false }
                ) {
                    Text(
                        text = "Отмена",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = InkWashed
                    )
                }
            },
            containerColor = PaperCream,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showAddCategoryDialog) {
        var newCategoryText by rememberSaveable { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = {
                Text(
                    text = "Новая категория",
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = InkDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Введите название категории блюда:",
                        fontFamily = HandwrittenFamily,
                        fontSize = 16.sp,
                        color = InkWashed
                    )
                    OutlinedTextField(
                        value = newCategoryText,
                        onValueChange = { newCategoryText = it },
                        placeholder = {
                            Text(
                                text = "например, Заготовки",
                                fontFamily = HandwrittenFamily,
                                fontSize = 16.sp,
                                color = InkWashed
                            )
                        },
                        textStyle = TextStyle(
                            fontFamily = HandwrittenFamily,
                            fontSize = 18.sp,
                            color = InkDark
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            val trimmed = newCategoryText.trim()
                            if (trimmed.isNotEmpty()) {
                                viewModel.addCustomCategory(trimmed)
                                showAddCategoryDialog = false
                            }
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Terracotta,
                            unfocusedBorderColor = DottedBorderColor,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newCategoryText.trim()
                        if (trimmed.isNotEmpty()) {
                            viewModel.addCustomCategory(trimmed)
                            showAddCategoryDialog = false
                        }
                    },
                    enabled = newCategoryText.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terracotta,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Добавить",
                        fontFamily = HandwrittenFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddCategoryDialog = false }
                ) {
                    Text(
                        text = "Отмена",
                        fontFamily = HandwrittenFamily,
                        fontSize = 16.sp,
                        color = InkWashed
                    )
                }
            },
            containerColor = PaperCream,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Metadata parameter adjustment card (TIME, SERVES).
 */
@Composable
private fun MetadataAdjustCard(
    icon: ImageVector,
    label: String,
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BakedMilk, RoundedCornerShape(12.dp))
            .border(1.dp, DottedBorderColor, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Terracotta,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = InkWashed
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontFamily = HandwrittenFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = InkDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, DottedBorderColor, CircleShape)
                        .clickable(onClick = onDecrease),
                    contentAlignment = Alignment.Center
                ) {
                    Text("-", fontFamily = HandwrittenFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkDark)
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, DottedBorderColor, CircleShape)
                        .clickable(onClick = onIncrease),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontFamily = HandwrittenFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkDark)
                }
            }
        }
    }
}

/**
 * Metadata level toggle card (LEVEL: Easy / Medium / Hard).
 */
@Composable
private fun MetadataLevelCard(
    icon: ImageVector,
    label: String,
    level: String,
    onCycleLevel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BakedMilk, RoundedCornerShape(12.dp))
            .border(1.dp, DottedBorderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onCycleLevel)
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Terracotta,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = InkWashed
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = level,
                fontFamily = HandwrittenFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = InkDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SageGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "нажмите для смены",
                    fontFamily = HandwrittenFamily,
                    fontSize = 11.sp,
                    color = SageGreen
                )
            }
        }
    }
}

/**
 * Button chip to add a custom category.
 */
@Composable
private fun AddCategoryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Terracotta.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить категорию",
                tint = Terracotta,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Добавить",
                fontFamily = HandwrittenFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Terracotta
            )
        }
    }
}
