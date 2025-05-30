package com.example.newnoteapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.example.newnoteapp.domain.Importance
import androidx.compose.ui.unit.dp

@Composable
fun ImportanceSelector(
    importance: Importance,
    onImportanceChange: (Importance) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Важность",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Importance.entries.forEach { value ->
                FilterChip(
                    selected = importance == value,
                    onClick = { onImportanceChange(value) },
                    label = { Text(value.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (value) {
                            Importance.HIGH -> MaterialTheme.colorScheme.errorContainer
                            Importance.NORMAL -> MaterialTheme.colorScheme.primaryContainer
                            Importance.LOW -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                )
            }
        }
    }
}

private val Importance.displayName: String
    get() = when (this) {
        Importance.HIGH -> "Важная"
        Importance.NORMAL -> "Обычная"
        Importance.LOW -> "Низкая"
    }