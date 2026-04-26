package com.example.todolist.data

enum class Priority { HIGH, MEDIUM, LOW }

data class DataModel (
    val id: Int = 0,
    val title: String,
    val description: String,
    val isDone: Boolean = false,
    val dueDateMillis: Long,
    val priority: Priority = Priority.MEDIUM
)