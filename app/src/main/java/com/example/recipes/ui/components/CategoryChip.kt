package com.example.recipes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.HandwrittenFamily
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.getCategoryColor

private val CategoryChipShape = RoundedCornerShape(16.dp)

/**
 * Colored category filter chip styled for the craft notebook theme.
 *
 * @param category Category name (e.g. "Dinner", "Soup", "Breakfast", "Dessert", "Lunch").
 * @param isSelected Whether this category is currently selected.
 * @param onClick Callback triggered on click.
 * @param modifier Modifier for positioning and styling.
 * @param categoryColor Color associated with this category.
 */
@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    categoryColor: Color = getCategoryColor(category)
) {
    val chipShape = CategoryChipShape

    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) categoryColor else BakedMilk,
        label = "chipBgColor"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else InkDark,
        label = "chipTextColor"
    )

    val borderModifier = if (isSelected) {
        Modifier
    } else {
        Modifier.border(
            width = 1.dp,
            color = DottedBorderColor,
            shape = chipShape
        )
    }

    val shadowElevation = if (isSelected) 2.dp else 0.dp

    Box(
        modifier = modifier
            .shadow(elevation = shadowElevation, shape = chipShape)
            .clip(chipShape)
            .background(color = animatedBg, shape = chipShape)
            .then(borderModifier)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Optional tiny colored craft dot indicator when unselected
            if (!isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                Box(modifier = Modifier.padding(start = 6.dp))
            }

            Text(
                text = category,
                fontFamily = HandwrittenFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 20.sp,
                color = animatedTextColor
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EE)
@Composable
private fun CategoryChipPreview() {
    Row(modifier = Modifier.padding(16.dp)) {
        CategoryChip(
            category = "Ужин",
            isSelected = true,
            onClick = {},
            modifier = Modifier.padding(end = 8.dp)
        )
        CategoryChip(
            category = "Супы",
            isSelected = false,
            onClick = {},
            modifier = Modifier.padding(end = 8.dp)
        )
        CategoryChip(
            category = "Десерты",
            isSelected = false,
            onClick = {}
        )
    }
}
