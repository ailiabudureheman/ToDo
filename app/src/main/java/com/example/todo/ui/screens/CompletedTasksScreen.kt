package com.example.todo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todo.R
import com.example.todo.data.Task
import com.example.todo.data.TaskRepository
import com.example.todo.ui.components.TaskDialog
import com.example.todo.ui.components.TaskItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTasksScreen(
    taskRepository: TaskRepository,
    modifier: Modifier = Modifier
) {
    val tasks = remember { mutableStateListOf<Task>() }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val selectedTask = remember { mutableStateOf<Task?>(null) }
    val showTaskDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    fun loadTasks() {
        coroutineScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val loadedTasks = taskRepository.getCompletedTasks()
                tasks.clear()
                tasks.addAll(loadedTasks)
            } catch (e: Exception) {
                errorMessage.value = "加载任务失败，请重试"
            } finally {
                isLoading.value = false
            }
        }
    }
    
    // 加载任务列表
    LaunchedEffect(Unit) {
        loadTasks()
    }
    
    fun handleSaveTask(updatedTask: Task) {
        coroutineScope.launch {
            try {
                taskRepository.updateTask(updatedTask)
                val index = tasks.indexOfFirst { it.id == updatedTask.id }
                if (index != -1) {
                    tasks[index] = updatedTask
                }
                showTaskDialog.value = false
                selectedTask.value = null
            } catch (e: Exception) {
                errorMessage.value = "更新任务失败，请重试"
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.completed_tasks_title)) },
                actions = {
                    IconButton(
                        onClick = { loadTasks() }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading.value -> {
                    // 加载状态
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "加载中...",
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                errorMessage.value != null -> {
                    // 错误状态
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage.value ?: "",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        androidx.compose.material3.Button(
                            onClick = { loadTasks() }
                        ) {
                            Text("重试")
                        }
                    }
                }
                tasks.isEmpty() -> {
                    // 空状态
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.empty_tasks),
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "暂无已完成的任务",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                else -> {
                    // 任务列表
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(tasks) { task ->
                            TaskItem(
                                task = task,
                                onEdit = {
                                    // 编辑已完成任务 - 显示任务详情对话框
                                    selectedTask.value = task
                                    showTaskDialog.value = true
                                },
                                onDelete = {
                                    coroutineScope.launch {
                                        try {
                                            taskRepository.markAsDeleted(task.id)
                                            val index = tasks.indexOf(task)
                                            if (index != -1) {
                                                tasks.removeAt(index)
                                            }
                                        } catch (e: Exception) {
                                            errorMessage.value = "删除任务失败，请重试"
                                        }
                                    }
                                },
                                onToggleComplete = {
                                    val updatedTask = task.copy(
                                        isCompleted = !task.isCompleted,
                                        updatedAt = java.time.LocalDateTime.now()
                                    )
                                    coroutineScope.launch {
                                        try {
                                            taskRepository.updateTask(updatedTask)
                                            val index = tasks.indexOf(task)
                                            if (index != -1) {
                                                tasks.removeAt(index)
                                            }
                                        } catch (e: Exception) {
                                            errorMessage.value = "更新任务失败，请重试"
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
            
            // 任务编辑对话框
            if (showTaskDialog.value && selectedTask.value != null) {
                TaskDialog(
                    task = selectedTask.value,
                    onDismiss = {
                        showTaskDialog.value = false
                        selectedTask.value = null
                    },
                    onSave = ::handleSaveTask
                )
            }
        }
    }
}
