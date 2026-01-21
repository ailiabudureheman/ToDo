
package com.example.todo.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import com.example.todo.ui.components.TaskItem
import com.example.todo.ui.components.TaskDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(taskRepository: TaskRepository) {
    val tasks = remember { mutableStateListOf<Task>() }
    val showDialog = remember { mutableStateOf(false) }
    val selectedTask = remember { mutableStateOf<Task?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    fun loadTasks() {
        coroutineScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val loadedTasks = taskRepository.getAllActiveTasks()
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(
                        onClick = { loadTasks() }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedTask.value = null
                    showDialog.value = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
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
                            text = "点击右下角按钮添加第一个任务",
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
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                    ) {
                        items(tasks) { task ->
                            TaskItem(
                                task = task,
                                onEdit = {
                                    selectedTask.value = task
                                    showDialog.value = true
                                },
                                onDelete = {
                                    coroutineScope.launch {
                                        try {
                                            taskRepository.deleteTask(task)
                                            tasks.remove(task)
                                        } catch (e: Exception) {
                                            errorMessage.value = "删除任务失败，请重试"
                                        }
                                    }
                                },
                                onToggleComplete = {
                                    val updatedTask = task.copy(
                                        isCompleted = !task.isCompleted
                                    )
                                    coroutineScope.launch {
                                        try {
                                            taskRepository.updateTask(updatedTask)
                                            val index = tasks.indexOf(task)
                                            if (index != -1) {
                                                tasks[index] = updatedTask
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
            if (showDialog.value) {
                TaskDialog(
                    task = selectedTask.value,
                    onDismiss = { showDialog.value = false },
                    onSave = { task ->
                        coroutineScope.launch {
                            try {
                                if (selectedTask.value == null) {
                                    // 添加新任务
                                    val newTask = task.copy()
                                    val id = taskRepository.addTask(newTask)
                                    tasks.add(0, newTask.copy(id = id.toInt()))
                                } else {
                                    // 更新现有任务
                                    taskRepository.updateTask(task)
                                    val index = tasks.indexOfFirst { it.id == task.id }
                                    if (index != -1) {
                                        tasks[index] = task
                                    }
                                }
                                showDialog.value = false
                            } catch (e: Exception) {
                                errorMessage.value = "保存任务失败，请重试"
                                showDialog.value = false
                            }
                        }
                    }
                )
            }
        }
    }
}
