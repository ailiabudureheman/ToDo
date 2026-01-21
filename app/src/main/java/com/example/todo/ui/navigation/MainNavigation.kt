package com.example.todo.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.todo.R
import com.example.todo.data.NotificationService
import com.example.todo.data.TaskRepository
import com.example.todo.data.Task
import com.example.todo.ui.screens.PendingTasksScreen
import com.example.todo.ui.screens.CompletedTasksScreen
import com.example.todo.ui.screens.TrashScreen
import com.example.todo.ui.screens.StatsScreen
import com.example.todo.ui.screens.SearchScreen
import com.example.todo.ui.components.TaskDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

sealed class Screen(val route: String, val label: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Pending : Screen("pending", R.string.pending_tasks, Icons.Default.List)
    object Completed : Screen("completed", R.string.completed_tasks, Icons.Default.CheckCircle)
    object Trash : Screen("trash", R.string.trash, Icons.Default.Delete)
    object Search : Screen("search", R.string.search, Icons.Default.Search)
    object Stats : Screen("stats", R.string.stats, Icons.Default.MoreVert)
}

@Composable
fun MainNavigation(taskRepository: TaskRepository, notificationService: NotificationService) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Pending) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    val screens = listOf(
        Screen.Pending,
        Screen.Completed,
        Screen.Trash,
        Screen.Search,
        Screen.Stats
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = { Icon(screen.icon, contentDescription = stringResource(screen.label)) },
                        label = { Text(stringResource(screen.label)) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == Screen.Pending) {
                FloatingActionButton(
                    onClick = {
                        showAddDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task))
                }
            }
        }
    ) { paddingValues ->
        when (currentScreen) {
            is Screen.Pending -> PendingTasksScreen(
                taskRepository = taskRepository,
                modifier = Modifier.padding(paddingValues)
            )
            is Screen.Completed -> CompletedTasksScreen(
                taskRepository = taskRepository,
                modifier = Modifier.padding(paddingValues)
            )
            is Screen.Trash -> TrashScreen(
                taskRepository = taskRepository,
                modifier = Modifier.padding(paddingValues)
            )
            is Screen.Search -> SearchScreen(
                taskRepository = taskRepository,
                modifier = Modifier.padding(paddingValues)
            )
            is Screen.Stats -> StatsScreen(
                taskRepository = taskRepository,
                modifier = Modifier.padding(paddingValues)
            )
        }
        

        // Add Task Dialog
        if (showAddDialog) {
            TaskDialog(
                task = null,
                onDismiss = { showAddDialog = false },
                onSave = { task ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            taskRepository.addTask(task)
                            // 检查是否需要发送提醒
                            notificationService.checkTasksForReminders(listOf(task))
                            showAddDialog = false
                        } catch (e: Exception) {
                            showAddDialog = false
                        }
                    }
                }
            )
        }

    }
}
