package com.example.recipes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.recipes.ui.theme.Terracotta

/**
 * Custom Shape producing a wavy/scalloped cut along the bottom edge,
 * simulating vintage craft scissors or torn photo edges.
 *
 * @param waveCount Number of wave teeth across the width (default 18).
 * @param waveHeight Amplitude/height of each wave cut (default 12.dp).
 */
class WavyCutShape(
    val waveCount: Int = 18,
    val waveHeight: Dp = 12.dp
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        if (size.width <= 0f || size.height <= 0f) {
            return Outline.Generic(Path())
        }

        val waveHeightPx = with(density) { waveHeight.toPx() }.coerceAtMost(size.height / 2f)
        val baseBottom = size.height - waveHeightPx
        val count = waveCount.coerceAtLeast(1)
        val step = size.width / count

        val path = Path().apply {
            // Top-left
            moveTo(0f, 0f)
            // Left side down to wave start
            lineTo(0f, baseBottom)

            // Scalloped wavy cut along bottom edge
            for (i in 0 until count) {
                val xStart = i * step
                val xEnd = if (i == count - 1) size.width else (i + 1) * step
                val xMid = (xStart + xEnd) / 2f
                // Quadratic curve down to trough and back up to crest
                quadraticTo(
                    x1 = xMid,
                    y1 = size.height,
                    x2 = xEnd,
                    y2 = baseBottom
                )
            }

            // Right side up to top-right
            lineTo(size.width, 0f)
            close()
        }

        return Outline.Generic(path)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WavyCutShape) return false
        return waveCount == other.waveCount && waveHeight == other.waveHeight
    }

    override fun hashCode(): Int {
        return 31 * waveCount + waveHeight.hashCode()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EE)
@Composable
private fun WavyCutShapePreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .clip(WavyCutShape(waveCount = 18, waveHeight = 12.dp))
            .background(Terracotta)
    )
}
