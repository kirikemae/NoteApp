package com.example.newnoteapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfDestructSelector(
    hasSelfDestruct: Boolean,
    selfDestructDate: Long?,
    onSelfDestructChange: (Boolean, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val defaultDate = remember { System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 } // +30 дней

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Самоуничтожение",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Switch(
                checked = hasSelfDestruct,
                onCheckedChange = { enabled ->
                    onSelfDestructChange(
                        enabled,
                        selfDestructDate ?: defaultDate
                    )
                    if (enabled) showDatePicker = true
                }
            )
        }

        if (hasSelfDestruct) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Дата: ${selfDestructDate?.let {
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "Не задана"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = { showDatePicker = true }) {
                    Text("Изменить")
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selfDestructDate ?: defaultDate
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onSelfDestructChange(true, it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}