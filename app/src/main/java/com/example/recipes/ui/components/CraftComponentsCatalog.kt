package com.example.recipes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.recipes.ui.theme.HeadlineLarge
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.InkWashed
import com.example.recipes.ui.theme.PaperCream
import com.example.recipes.ui.theme.RecipesTheme
import com.example.recipes.ui.theme.ScriptLabel
import com.example.recipes.ui.theme.Terracotta
import com.example.recipes.ui.theme.TitleMedium

/**
 * Interactive showcase demonstrating all custom craft components in the "Кулинарный блокнот" design system.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CraftComponentsCatalog(
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Ужин") }
    var currentNavRoute by remember { mutableStateOf("recipes") }

    val categories = listOf("Ужин", "Супы", "Завтрак", "Десерты", "Обед")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PaperCream,
        bottomBar = {
            CraftBottomBar(
                currentRoute = currentNavRoute,
                onNavigate = { currentNavRoute = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Screen Header
            Text(
                text = "Кулинарный блокнот",
                style = HeadlineLarge,
                color = InkDark
            )
            Text(
                text = "Дизайн-система & Кастомные UI компоненты",
                style = ScriptLabel,
                color = InkWashed,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 1. Wavy Cut Photo / Banner with Scotch Tape
            Text(
                text = "1. WavyCutShape & ScotchTape",
                style = TitleMedium,
                color = InkDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                // Scotch tape on top-left corner
                ScotchTape(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-8).dp, y = (-6).dp)
                        .zIndex(2f),
                    rotationDegrees = -25f
                )

                // Scotch tape on top-right corner
                ScotchTape(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-6).dp)
                        .zIndex(2f),
                    rotationDegrees = 25f
                )

                // Photo container with wavy cut bottom edge
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(WavyCutShape(waveCount = 18, waveHeight = 12.dp))
                        .background(Terracotta)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Паста сливочная с пармезаном",
                            style = HeadlineLarge,
                            color = PaperCream
                        )
                        Text(
                            text = "Волнистый срез фотографии рецепта",
                            style = ScriptLabel,
                            color = PaperCream.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Category Chips
            Text(
                text = "2. CategoryChip (Цветные чипы категорий)",
                style = TitleMedium,
                color = InkDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. RecipeParamPill Badges
            Text(
                text = "3. RecipeParamPill (Параметры рецепта)",
                style = TitleMedium,
                color = InkDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecipeParamPill(
                    icon = Icons.Outlined.Timer,
                    label = "Время",
                    value = "25 мин",
                    modifier = Modifier.weight(1f)
                )
                RecipeParamPill(
                    icon = Icons.Outlined.People,
                    label = "Порции",
                    value = "4 порции",
                    modifier = Modifier.weight(1f)
                )
                RecipeParamPill(
                    icon = Icons.Outlined.DinnerDining,
                    label = "Сложность",
                    value = "Легко",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. DashedCard Container
            Text(
                text = "4. DashedCard (Пунктирная карточка)",
                style = TitleMedium,
                color = InkDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            DashedCard(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ингредиенты на 4 порции",
                    style = TitleMedium,
                    color = InkDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• 350г пасты феттуччине\n• 2 стакана жирных сливок\n• 3 зубчика чеснока\n• 1 стакан тертого пармезана\n• 2 ст. л. сливочного масла",
                    color = InkDark.copy(alpha = 0.85f),
                    style = ScriptLabel
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. StickyNote Memo
            Text(
                text = "5. StickyNote (Желтый стикер с булавкой)",
                style = TitleMedium,
                color = InkDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StickyNote(
                noteText = "Секрет шефа: добавьте свежий мускатный орех в соус перед подачей, чтобы подчеркнуть аромат сыра!",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CraftComponentsCatalogPreview() {
    RecipesTheme {
        CraftComponentsCatalog()
    }
}
