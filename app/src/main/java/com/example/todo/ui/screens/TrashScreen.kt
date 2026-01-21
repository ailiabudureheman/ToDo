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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    taskRepository: TaskRepository,
    modifier: Modifier = Modifier
) {
    val tasks = remember { mutableStateListOf<Task>() }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val showEmptyDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    fun loadTasks() {
        coroutineScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val allTasks = taskRepository.getAllTasks()
                val deletedTasks = allTasks.filter { it.isDeleted }
                tasks.clear()
                tasks.addAll(deletedTasks)
            } catch (e: Exception) {
                errorMessage.value = "加载任务失败: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
    
    // 加载任务列表
    LaunchedEffect(Unit) {
        loadTasks()
    }
    
    fun emptyTrash() {
        coroutineScope.launch {
            try {
                taskRepository.deleteAllDeletedTasks()
                tasks.clear()
                showEmptyDialog.value = false
            } catch (e: Exception) {
                errorMessage.value = "清空废纸桶失败，请重试"
                showEmptyDialog.value = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.trash_title)) },
                actions = {
                    IconButton(
                        onClick = { loadTasks() }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    if (tasks.isNotEmpty()) {
                        IconButton(
                    onClick = { showEmptyDialog.value = true }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Empty Trash")
                }
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
                            text = stringResource(R.string.empty_trash),
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "删除的任务会显示在这里",
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
                                    // 恢复任务
                                    coroutineScope.launch {
                                        try {
                                            taskRepository.restoreTask(task.id)
                                            val index = tasks.indexOf(task)
                                            if (index != -1) {
                                                tasks.removeAt(index)
                                            }
                                        } catch (e: Exception) {
                                            errorMessage.value = "恢复任务失败，请重试"
                                        }
                                    }
                                },
                                onDelete = {
                                    // 永久删除
                                    coroutineScope.launch {
                                        try {
                                            taskRepository.deleteTaskById(task.id)
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
                                    // 在废纸桶中不允许切换完成状态
                                }
                            )
                        }
                    }
                }
            }
            
            // 清空废纸桶对话框
            if (showEmptyDialog.value) {
                AlertDialog(
                    onDismissRequest = { showEmptyDialog.value = false },
                    title = { Text(stringResource(R.string.trash_title)) },
                    text = { Text(stringResource(R.string.empty_trash_confirm)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                emptyTrash()
                            }
                        ) {
                            Text(stringResource(R.string.yes))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showEmptyDialog.value = false }
                        ) {
                            Text(stringResource(R.string.no))
                        }
                    }
                )
            }
        }
    }
}
