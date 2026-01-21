package com.example.todo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.todo.R
import com.example.todo.data.Task
import com.example.todo.data.TaskRepository
import com.example.todo.ui.components.TaskItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    taskRepository: TaskRepository,
    modifier: Modifier = Modifier
) {
    val searchQuery = remember { mutableStateOf("") }
    val tasks = remember { mutableStateListOf<Task>() }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    fun performSearch(query: String) {
        if (query.length < 2) {
            tasks.clear()
            return
        }
        
        coroutineScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                delay(300) // Debounce search
                val searchResults = taskRepository.searchTasks(query)
                tasks.clear()
                tasks.addAll(searchResults)
            } catch (e: Exception) {
                errorMessage.value = "搜索失败，请重试"
            } finally {
                isLoading.value = false
            }
        }
    }
    
    // 搜索查询变化时执行搜索
    LaunchedEffect(searchQuery.value) {
        performSearch(searchQuery.value)
    }
    

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_title)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 搜索框
                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    placeholder = { Text("输入关键词搜索任务") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            performSearch(searchQuery.value)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                
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
                                text = "搜索中...",
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
                                onClick = { performSearch(searchQuery.value) }
                            ) {
                                Text("重试")
                            }
                        }
                    }
                    searchQuery.value.length < 2 -> {
                        // 搜索提示
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "请输入至少2个字符进行搜索",
                                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    tasks.isEmpty() -> {
                        // 无结果状态
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "未找到匹配的任务",
                                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "尝试使用不同的关键词",
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    else -> {
                        // 搜索结果列表
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(tasks) { task ->
                                TaskItem(
                                    task = task,
                                    onEdit = {
                                        // 编辑任务
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
            }
        }
    }

}
