package com.example.todolist.ui.features.addtask

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
fun AddTaskScreen(
    viewModel: ToDoListViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTimeMillis by remember { mutableLongStateOf(0L) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Tugas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
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
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tenggang Waktu:", style = MaterialTheme.typography.labelLarge)
            Button(
                onClick = { ShowTimePicker(context) { millis -> selectedTimeMillis = millis } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (selectedTimeMillis == 0L) {
                        "Atur Tanggal & Jam"
                    } else {
                        formatExactDueDate(selectedTimeMillis)
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Prioritas:", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Priority.entries.forEach { priority ->
                    val isSelected = priority == selectedPriority
                    Button(
                        onClick = { selectedPriority = priority },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text(priority.name)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val newTask = DataModel(
                        title = title,
                        description = description,
                        isDone = false,
                        dueDateMillis = selectedTimeMillis,
                        priority = selectedPriority
                    )
                    viewModel.insert(newTask)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && selectedTimeMillis > 0L
            ) {
                Text("Simpan Tugas")
            }
        }
    }
}
