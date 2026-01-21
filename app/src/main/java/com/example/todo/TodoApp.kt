
package com.example.todo

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.todo.data.NotificationService
import com.example.todo.data.TaskDatabase
import com.example.todo.data.TaskRepository
import com.example.todo.ui.navigation.MainNavigation
import com.example.todo.ui.theme.ToDoTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun TodoApp(context: Context) {
    val taskRepository = remember {
        val database = TaskDatabase.getDatabase(context)
        TaskRepository(database.taskDao())
    }
    
    val notificationService = remember {
        NotificationService(context)
    }
    
    val coroutineScope = rememberCoroutineScope()
    
    // 检查任务提醒
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val allTasks = taskRepository.getAllActiveTasks()
                notificationService.checkTasksForReminders(allTasks)
            } catch (e: Exception) {
                // 忽略错误
            }
        }
    }
    
    ToDoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
            MainNavigation(taskRepository = taskRepository, notificationService = notificationService)
        }
    }
}
