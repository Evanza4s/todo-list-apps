package com.example.todolist.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.data.DataModel
import com.example.todolist.data.Priority
import com.example.todolist.utils.formatDueDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListCard(
    task: DataModel,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color(0xFFE57373)
        Priority.MEDIUM -> Color(0xFFFFB74D)
        Priority.LOW -> Color(0xFF81C784)
    }

    val isOverdue = !task.isDone && task.dueDateMillis > 0L && task.dueDateMillis < System.currentTimeMillis()

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart ||
                dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                onDelete()
                return@rememberSwipeToDismissBoxState true
            }
            return@rememberSwipeToDismissBoxState false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled)
                    Color.Transparent else MaterialTheme.colorScheme.error,
                label = "swipe_color"
            )
            val alignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                Alignment.CenterStart else Alignment.CenterEnd

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White)
            }
        },
        content = {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {

                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .background(priorityColor)
                    )

                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = onCheckedChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (task.isDone) Color.Gray else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (task.description.isNotEmpty()) {
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val dateColor = when {
                                task.isDone -> Color.Gray
                                isOverdue -> Color(0xFFE53935)
                                else -> MaterialTheme.colorScheme.primary
                            }

                            val dateText = when {
                                task.dueDateMillis == 0L -> "Waktu belum diatur"
                                isOverdue -> "Terlambat (${formatDueDate(task.dueDateMillis)})"
                                else -> formatDueDate(task.dueDateMillis)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Tenggat Waktu",
                                    modifier = Modifier.size(14.dp),
                                    tint = dateColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = dateText,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = dateColor
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}