package com.example.recipes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipes.ui.theme.InkDark
import com.example.recipes.ui.theme.PushPinRed
import com.example.recipes.ui.theme.PushPinShadow
import com.example.recipes.ui.theme.ScriptBody
import com.example.recipes.ui.theme.StickerYellow

/**
 * Craft sticky note tilted at -2.5° with a realistic 3D red pushpin with shadow at the top center.
 *
 * @param noteText The handwritten note message to display.
 * @param modifier Modifier for positioning and layout.
 * @param rotationDegrees Rotation angle (default -2.5°).
 * @param noteColor Background color of the sticky note (default StickerYellow).
 * @param textColor Color of the handwritten note text (default InkDark).
 * @param textStyle Typography style (default ScriptBody with Caveat font).
 */
@Composable
fun StickyNote(
    noteText: String,
    modifier: Modifier = Modifier,
    rotationDegrees: Float = -2.5f,
    noteColor: Color = StickerYellow,
    textColor: Color = InkDark,
    textStyle: TextStyle = ScriptBody
) {
    Box(
        modifier = modifier
            .rotate(rotationDegrees)
            .padding(top = 10.dp) // Room for top pushpin head
    ) {
        // Main sticky note paper sheet
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(3.dp),
                    ambientColor = Color(0x33000000),
                    spotColor = Color(0x40000000)
                )
                .background(noteColor, RoundedCornerShape(3.dp))
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 22.dp, // Extra top clearance so text doesn't overlap the pin
                    bottom = 16.dp
                )
        ) {
            Text(
                text = noteText,
                style = textStyle,
                color = textColor
            )
        }

        // Round red pushpin with 3D sphere highlight and drop shadow
        PushPin(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-8).dp)
        )
    }
}

/**
 * 3D Pushpin with pinhead, highlight reflection, and directional drop shadow.
 */
@Composable
private fun PushPin(
    modifier: Modifier = Modifier,
    pinColor: Color = PushPinRed
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = size.minDimension / 2.4f

        // Directional shadow on the paper beneath the pin
        drawCircle(
            color = PushPinShadow,
            radius = radius * 0.95f,
            center = Offset(centerX + 2.5f, centerY + 3f)
        )

        // Pin base ring / rim
        drawCircle(
            color = pinColor.copy(alpha = 0.85f),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Main sphere head
        drawCircle(
            color = pinColor,
            radius = radius * 0.82f,
            center = Offset(centerX, centerY)
        )

        // 3D spherical light reflection highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.65f),
            radius = radius * 0.28f,
            center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f)
        )

        // Subtle dark center point (needle insertion depth cue)
        drawCircle(
            color = Color.Black.copy(alpha = 0.25f),
            radius = radius * 0.18f,
            center = Offset(centerX, centerY)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F6EE)
@Composable
private fun StickyNotePreview() {
    StickyNote(
        noteText = "Совет от шефа: оставьте немного воды из-под пасты, чтобы соус получился кремовым!",
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    )
}
