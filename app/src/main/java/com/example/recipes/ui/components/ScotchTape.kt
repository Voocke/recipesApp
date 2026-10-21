package com.example.recipes.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.recipes.ui.theme.ScotchTapeIvory

/**
 * Semi-transparent matte ivory scotch tape strip with beveled cut edges.
 *
 * @param modifier Modifier for styling, sizing, and positioning.
 * @param rotationDegrees Rotation angle (default 45° for notebook corner taping).
 * @param tapeColor Color of the tape (default #EFECE1 with 0.75 alpha).
 * @param bevelAngleOffset Cut offset in pixels/dp for the beveled ends.
 */
@Composable
fun ScotchTape(
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 45f,
    tapeColor: Color = ScotchTapeIvory,
    bevelAngleOffset: Dp = 6.dp
) {
    Spacer(
        modifier = Modifier
            .size(width = 80.dp, height = 24.dp)
            .then(modifier)
            .drawWithCache {
                val w = size.width
                val h = size.height
                val bevelOffsetPx = bevelAngleOffset.toPx()

                // Precompute tape and shadow paths only once per measured size
                val tapePath = Path().apply {
                    moveTo(bevelOffsetPx, 0f)
                    lineTo(w, 0f)
                    lineTo(w - bevelOffsetPx, h)
                    lineTo(0f, h)
                    close()
                }

                val shadowPath = Path().apply {
                    moveTo(bevelOffsetPx + 1.5f, 2f)
                    lineTo(w + 1.5f, 2f)
                    lineTo(w - bevelOffsetPx + 1.5f, h + 2f)
                    lineTo(1.5f, h + 2f)
                    close()
                }

                val shadowColor = Color(0x18000000)
                val topSheenColor = Color.White.copy(alpha = 0.35f)
                val bottomReflectionColor = Color.Black.copy(alpha = 0.08f)

                onDrawBehind {
                    rotate(degrees = rotationDegrees, pivot = center) {
                        // Soft tape translucent drop shadow
                        drawPath(
                            path = shadowPath,
                            color = shadowColor
                        )

                        // Main matte semi-transparent tape body
                        drawPath(
                            path = tapePath,
                            color = tapeColor
                        )

                        // Subtle frosted tape top highlight sheen
                        drawLine(
                            color = topSheenColor,
                            start = Offset(bevelOffsetPx + 2f, 1.5f),
                            end = Offset(w - 2f, 1.5f),
                            strokeWidth = 1.2f
                        )

                        // Subtle tape bottom cut edge reflection
                        drawLine(
                            color = bottomReflectionColor,
                            start = Offset(2f, h - 1.2f),
                            end = Offset(w - bevelOffsetPx - 2f, h - 1.2f),
                            strokeWidth = 1f
                        )
                    }
                }
            }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EE)
@Composable
private fun ScotchTapePreview() {
    ScotchTape(rotationDegrees = 25f)
}
