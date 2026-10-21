package com.example.recipes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.HandwrittenFamily
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.InkWashed
import com.example.recipes.ui.theme.Terracotta

/**
 * Craft pill badge displaying a recipe parameter (e.g. cooking time, servings, difficulty).
 *
 * @param icon The vector icon representing the parameter.
 * @param label The small descriptive caption (e.g. "Время", "Порции", "Сложность").
 * @param value The primary value (e.g. "25 мин", "4 порции", "Легко").
 * @param modifier Modifier for styling and layout.
 * @param iconTint Color tint applied to the icon (default Terracotta).
 * @param backgroundColor Background color of the pill (default BakedMilk).
 * @param borderColor Border stroke color (default DottedBorderColor).
 */
@Composable
fun RecipeParamPill(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Terracotta,
    backgroundColor: Color = BakedMilk,
    borderColor: Color = DottedBorderColor
) {
    val pillShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = pillShape)
            .border(width = 1.dp, color = borderColor, shape = pillShape)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = value,
                    fontFamily = HandwrittenFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 19.sp,
                    color = InkDark
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkWashed
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EE)
@Composable
private fun RecipeParamPillPreview() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RecipeParamPill(
            icon = Icons.Outlined.Timer,
            label = "Время",
            value = "25 мин"
        )
        RecipeParamPill(
            icon = Icons.Outlined.People,
            label = "Порции",
            value = "4 порции"
        )
        RecipeParamPill(
            icon = Icons.Outlined.DinnerDining,
            label = "Сложность",
            value = "Легко"
        )
    }
}
