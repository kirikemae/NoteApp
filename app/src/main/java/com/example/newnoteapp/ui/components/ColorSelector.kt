package com.example.newnoteapp.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable
fun ColorSelector(
    color: Color,
    onColorClick: () -> Unit,
    onPresetColorClick: (Color) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val presetColors = listOf(
        Color.White,
        Color(0xFFFF5722),
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFF00BCD4),
        Color(0xFFFFEB3B)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Цвет заметки",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                presetColors.forEach { presetColor ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = presetColor,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = if (color == presetColor) 3.dp else 1.dp,
                                color = if (color == presetColor)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onPresetColorClick(presetColor) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onColorClick() },
                contentAlignment = Alignment.Center
            ) {
                if (color.luminance() > 0.8f) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Выбрать цвет",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
