package com.example.todolist.ui.features.taskdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todolist.data.DataModel
import com.example.todolist.data.Priority
import com.example.todolist.ui.components.ShowTimePicker
import com.example.todolist.utils.formatExactDueDate
import com.example.todolist.viewModel.ToDoListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    viewModel: ToDoListViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var currentTask by remember { mutableStateOf<DataModel?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTimeMillis by remember { mutableLongStateOf(0L) }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var isDone by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        isLoading = true
        val taskToEdit = viewModel.getTaskById(taskId)
        if (taskToEdit != null) {
            currentTask = taskToEdit
            title = taskToEdit.title
            description = taskToEdit.description
            selectedTimeMillis = taskToEdit.dueDateMillis
            priority = taskToEdit.priority
            isDone = taskToEdit.isDone
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Tugas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (currentTask == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Tugas tidak ditemukan")
                }
            } else {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Tenggang Waktu:", style = MaterialTheme.typography.labelLarge)
                Button(
                    onClick = { ShowTimePicker(context) { millis -> selectedTimeMillis = millis } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(formatExactDueDate(selectedTimeMillis))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Prioritas:", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Priority.entries.forEach { option ->
                        val isSelected = option == priority
                        Button(
                            onClick = { priority = option },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(option.name)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = { isDone = it }
                    )
                    Text(
                        text = "Tandai tugas sudah selesai",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        currentTask?.let { task ->
                            val updatedTask = task.copy(
                                title = title,
                                description = description,
                                dueDateMillis = selectedTimeMillis,
                                priority = priority,
                                isDone = isDone
                            )
                            viewModel.update(updatedTask)
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && selectedTimeMillis > 0L
                ) {
                    Text("Perbarui Tugas")
                }
            }
        }
    }
}
