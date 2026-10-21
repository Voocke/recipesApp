package com.example.recipes.ui.theme

import androidx.compose.ui.graphics.Color

// Craft Notebook Core Palette
val PaperCream = Color(0xFFF9F6EE)
val BakedMilk = Color(0xFFF0EAE1)
val Terracotta = Color(0xFFB85032)
val SageGreen = Color(0xFF7A9A7E)
val StickerYellow = Color(0xFFFEF7CD)
val DottedBorderColor = Color(0xFFD4C9BC)
val InkDark = Color(0xFF2C2523)
val InkWashed = Color(0xFF7D7571)

// Category Colors
val CategoryDinner = Color(0xFFB85032)
val CategorySoup = Color(0xFF7A9A7E)
val CategoryBreakfast = Color(0xFF88A07A)
val CategoryDessert = Color(0xFFC06C54)
val CategoryLunch = Color(0xFF6B5B52)
val CategorySalad = Color(0xFF628B67)
val CategorySnack = Color(0xFFD4813B)
val CategoryDrink = Color(0xFF5B8296)
val CategoryHot = Color(0xFFB85032)
val CategorySauce = Color(0xFFA65B48)

// Supplementary Craft Colors
val ScotchTapeIvory = Color(0xBFEFECE1) // #EFECE1 with 0.75 alpha
val PushPinRed = Color(0xFFC62828)
val PushPinShadow = Color(0x40000000)

// Legacy Material Palette (preserved for compatibility)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

/**
 * Returns the matching category color based on category name.
 */
fun getCategoryColor(category: String): Color {
    return when (category.lowercase().trim()) {
        "dinner", "ужин" -> CategoryDinner
        "soup", "суп", "супы" -> CategorySoup
        "breakfast", "завтрак", "завтраки" -> CategoryBreakfast
        "dessert", "десерт", "десерты", "baking", "выпечка" -> CategoryDessert
        "lunch", "обед" -> CategoryLunch
        "pasta", "паста" -> CategoryDinner
        "salad", "салат", "салаты" -> CategorySalad
        "snack", "appetizer", "закуска", "закуски" -> CategorySnack
        "drink", "beverage", "напиток", "напитки" -> CategoryDrink
        "hot", "горячее", "основное", "main" -> CategoryHot
        "sauce", "соус", "соусы" -> CategorySauce
        else -> Terracotta
    }
}
