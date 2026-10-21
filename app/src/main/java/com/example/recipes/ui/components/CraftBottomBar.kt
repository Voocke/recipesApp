package com.example.recipes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.HandwrittenFamily
import com.example.recipes.ui.theme.InkWashed
import com.example.recipes.ui.theme.Terracotta

/**
 * Navigation items for the Craft Bottom Bar.
 */
sealed class CraftNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object AllRecipes : CraftNavItem(
        route = "recipes",
        label = "Все рецепты",
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook
    )

    data object Recent : CraftNavItem(
        route = "recent",
        label = "Недавние",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )

    data object Settings : CraftNavItem(
        route = "settings",
        label = "Настройки",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        val items: List<CraftNavItem>
            get() = listOf(AllRecipes, Recent, Settings)
    }
}

private val TopDividerDashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)

/**
 * Craft-styled bottom navigation bar with dotted top divider, warm colors,
 * and handwritten typography.
 *
 * @param currentRoute Currently active navigation route.
 * @param onNavigate Callback when a navigation item is selected.
 * @param modifier Modifier for styling and layout.
 * @param items List of navigation destinations (defaults to AllRecipes, Recent, Settings).
 */
@Composable
fun CraftBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<CraftNavItem> = CraftNavItem.items
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BakedMilk)
            .navigationBarsPadding()
    ) {
        // Dotted craft separator line across the top
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
                pathEffect = TopDividerDashEffect
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                CraftBottomBarItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CraftBottomBarItem(
    item: CraftNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isSelected) Terracotta else InkWashed
    val animatedBgColor = if (isSelected) Terracotta.copy(alpha = 0.12f) else Color.Transparent
    val itemShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .clip(itemShape)
            .background(animatedBgColor, itemShape)
            .clickable(
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Terracotta.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.label,
                fontFamily = HandwrittenFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                color = iconColor
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EE)
@Composable
private fun CraftBottomBarPreview() {
    CraftBottomBar(
        currentRoute = "recipes",
        onNavigate = {}
    )
}
