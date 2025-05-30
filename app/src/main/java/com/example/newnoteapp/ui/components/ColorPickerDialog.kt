package com.example.newnoteapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import android.util.Log


@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val tag = "ColorPickerDialog"

    val initialHsv = remember { FloatArray(3).apply {
        android.graphics.Color.colorToHSV(initialColor.toArgb(), this)
    } }

    var hue by remember { mutableFloatStateOf(initialHsv[0] / 360f) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var brightness by remember { mutableFloatStateOf(initialHsv[2]) }

    val currentColor = remember(hue, saturation, brightness) {
        Color.hsv(
            hue * 360f,
            saturation,
            brightness
        )
    }

    var markerPosition by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(Size.Zero) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите цвет") },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(currentColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .onSizeChanged { size ->
                            boxSize = Size(size.width.toFloat(), size.height.toFloat())
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val width = boxSize.width
                                val height = boxSize.height
                                val x = change.position.x.coerceIn(0f, width)
                                val y = change.position.y.coerceIn(0f, height)

                                markerPosition = Offset(x, y)
                                hue = (x / width).coerceIn(0f, 1f)
                                saturation = (y / height).coerceIn(0f, 1f)
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Red,
                                    Color.Yellow,
                                    Color.Green,
                                    Color.Cyan,
                                    Color.Blue,
                                    Color.Magenta,
                                    Color.Red
                                )
                            )
                        )

                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.White, Color.Transparent),
                                startY = 0f,
                                endY = size.height
                            )
                        )

                        if (markerPosition != Offset.Zero || initialColor != Color.White) {
                            val markerPos = if (markerPosition == Offset.Zero) {
                                Offset(
                                    x = hue * size.width,
                                    y = saturation * size.height
                                )
                            } else {
                                markerPosition
                            }

                            drawCircle(
                                color = Color.Black,
                                radius = 8f,
                                center = markerPos
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Яркость:", modifier = Modifier.padding(bottom = 8.dp))
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = currentColor,
                        activeTrackColor = currentColor.copy(alpha = 0.5f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorSelected(currentColor)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}




