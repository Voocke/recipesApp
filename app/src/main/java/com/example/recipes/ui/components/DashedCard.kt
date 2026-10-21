package com.example.recipes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.recipes.ui.theme.BakedMilk
import com.example.recipes.ui.theme.DottedBorderColor
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.TitleMedium

/**
 * Container card with a dashed outline and warm craft styling.
 *
 * @param onClick Optional click handler. If null, card is non-clickable.
 * @param modifier Modifier for styling and layout.
 * @param backgroundColor Card background color (default BakedMilk).
 * @param borderColor Color of the dashed border (default DottedBorderColor).
 * @param borderWidth Thickness of the dashed border (default 1.5.dp).
 * @param cornerRadius Rounded corner radius (default 12.dp).
 * @param contentPadding Padding applied to inner column content.
 * @param content Composable column content.
 */
@Composable
fun DashedCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BakedMilk,
    borderColor: Color = DottedBorderColor,
    borderWidth: Dp = 1.5.dp,
    cornerRadius: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
            .then(clickableModifier)
            .dashedBorder(
                color = borderColor,
                strokeWidth = borderWidth,
                cornerRadius = cornerRadius
            )
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * Custom modifier that draws a dashed border with rounded corners.
 */
fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp,
    cornerRadius: Dp,
    intervals: FloatArray = floatArrayOf(15f, 15f),
    phase: Float = 0f
): Modifier = this.drawWithCache {
    val strokePx = strokeWidth.toPx()
    val halfStroke = strokePx / 2f
    val radiusPx = cornerRadius.toPx()

    val stroke = Stroke(
        width = strokePx,
        pathEffect = PathEffect.dashPathEffect(intervals, phase)
    )

    val topLeft = Offset(halfStroke, halfStroke)
    val rectSize = Size(
        width = size.width - strokePx,
        height = size.height - strokePx
    )
    val cornerRadiusObj = CornerRadius(
        x = (radiusPx - halfStroke).coerceAtLeast(0f),
        y = (radiusPx - halfStroke).coerceAtLeast(0f)
    )

    onDrawWithContent {
        drawContent()
        drawRoundRect(
            color = color,
            topLeft = topLeft,
            size = rectSize,
            cornerRadius = cornerRadiusObj,
            style = stroke
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EE)
@Composable
private fun DashedCardPreview() {
    DashedCard(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Бабушкин яблочный пирог",
            style = TitleMedium,
            color = InkDark
        )
        Text(
            text = "Традиционный рецепт с корицей и запеченным тестом",
            color = InkDark.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
