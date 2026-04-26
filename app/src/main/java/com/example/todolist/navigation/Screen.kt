package com.example.todolist.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object AddTask : Screen("add_task/{taskId}") {
        fun createRoute(taskId: Int = 0) = "add_task/$taskId"
    }
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Int) = "task_detail/$taskId"
    }
}