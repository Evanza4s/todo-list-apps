package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todolist.ui.features.home.HomeScreen
import com.example.todolist.ui.features.addtask.AddTaskScreen
import com.example.todolist.ui.features.taskdetail.TaskDetailScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.viewModel.ToDoListViewModel

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val viewModel: ToDoListViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAdd = {
                    navController.navigate(Screen.AddTask.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToDetail = { id -> navController.navigate(Screen.TaskDetail.createRoute(id)) }
            )
        }

        composable(route = Screen.AddTask.route) {
            AddTaskScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("taskId") ?: 0
            TaskDetailScreen(
                taskId = id,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
