package com.example.recipes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.recipes.ui.components.CraftNavItem
import com.example.recipes.ui.components.WavyCutShape
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.CategoryBreakfast
import com.example.recipes.ui.theme.CategoryDessert
import com.example.recipes.ui.theme.CategoryDinner
import com.example.recipes.ui.theme.CategoryLunch
import com.example.recipes.ui.theme.CategorySoup
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.InkWashed
import com.example.recipes.ui.theme.PaperCream
import com.example.recipes.ui.theme.SageGreen
import com.example.recipes.ui.theme.ScotchTapeIvory
import com.example.recipes.ui.theme.StickerYellow
import com.example.recipes.ui.theme.Terracotta
import com.example.recipes.ui.theme.getCategoryColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DesignSystemTest {

    @Test
    fun testPaletteColorValues() {
        assertEquals(Color(0xFFF9F6EE), PaperCream)
        assertEquals(Color(0xFFF0EAE1), BakedMilk)
        assertEquals(Color(0xFFB85032), Terracotta)
        assertEquals(Color(0xFF7A9A7E), SageGreen)
        assertEquals(Color(0xFFFEF7CD), StickerYellow)
        assertEquals(Color(0xFFD4C9BC), DottedBorderColor)
        assertEquals(Color(0xFF2C2523), InkDark)
        assertEquals(Color(0xFF7D7571), InkWashed)

        // Categories
        assertEquals(Color(0xFFB85032), CategoryDinner)
        assertEquals(Color(0xFF7A9A7E), CategorySoup)
        assertEquals(Color(0xFF88A07A), CategoryBreakfast)
        assertEquals(Color(0xFFC06C54), CategoryDessert)
        assertEquals(Color(0xFF6B5B52), CategoryLunch)

        // Scotch tape ivory (#EFECE1 with alpha 0.75)
        assertEquals(Color(0xBFEFECE1), ScotchTapeIvory)
    }

    @Test
    fun testCategoryColorMapping() {
        assertEquals(CategoryDinner, getCategoryColor("Dinner"))
        assertEquals(CategoryDinner, getCategoryColor("ужин"))
        assertEquals(CategorySoup, getCategoryColor("Soup"))
        assertEquals(CategorySoup, getCategoryColor("суп"))
        assertEquals(CategoryBreakfast, getCategoryColor("Breakfast"))
        assertEquals(CategoryBreakfast, getCategoryColor("завтрак"))
        assertEquals(CategoryDessert, getCategoryColor("Dessert"))
        assertEquals(CategoryDessert, getCategoryColor("десерт"))
        assertEquals(CategoryDessert, getCategoryColor("десерты"))
        assertEquals(CategoryDessert, getCategoryColor("выпечка"))
        assertEquals(CategoryLunch, getCategoryColor("Lunch"))
        assertEquals(CategoryLunch, getCategoryColor("обед"))
        assertEquals(CategoryDinner, getCategoryColor("паста"))
    }

    @Test
    fun testWavyCutShapeConfiguration() {
        val shape = WavyCutShape(waveCount = 18, waveHeight = 12.dp)
        assertEquals(18, shape.waveCount)
        assertEquals(12.dp, shape.waveHeight)
        val identicalShape = WavyCutShape(waveCount = 18, waveHeight = 12.dp)
        assertEquals(identicalShape, shape)
        assertEquals(identicalShape.hashCode(), shape.hashCode())
    }

    @Test
    fun testCraftNavItems() {
        assertEquals(3, CraftNavItem.items.size)
        assertEquals("recipes", CraftNavItem.AllRecipes.route)
        assertEquals("recent", CraftNavItem.Recent.route)
        assertEquals("settings", CraftNavItem.Settings.route)
        assertNotNull(CraftNavItem.AllRecipes.selectedIcon)
        assertNotNull(CraftNavItem.AllRecipes.unselectedIcon)
    }
}
