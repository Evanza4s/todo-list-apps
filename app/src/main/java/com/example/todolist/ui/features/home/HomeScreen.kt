package com.example.todolist.ui.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.ui.components.CustomSearchBar
import com.example.todolist.ui.components.TodoListCard
import com.example.todolist.viewModel.ToDoListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ToDoListViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val todoList by viewModel.activityList.collectAsState()

    var searchQueryUI by remember { mutableStateOf("") }

    val snackbarState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        topBar = {
            TopAppBar(
                title = { Text("Task List", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, contentDescription = "Tambah Task", tint = MaterialTheme.colorScheme.onPrimary) }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
        ) {
            CustomSearchBar(
                query = searchQueryUI,
                onQueryChange = { newText ->
                    searchQueryUI = newText
                    viewModel.search(newText)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = todoList, key = { it.id }) { task ->
                    Box(modifier = Modifier.animateItem()) {
                        TodoListCard(
                            task = task,
                            onCheckedChange = { isChecked ->
                                val updatedTask = task.copy(isDone = isChecked)
                                viewModel.update(updatedTask)
                            },
                            onClick = { onNavigateToDetail(task.id) },
                            onDelete = {
                                viewModel.delete(task)
                                coroutineScope.launch {
                                    val result = snackbarState.showSnackbar(
                                        message = "Tugas dihapus",
                                        actionLabel = "Batal",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.undoDelete()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}