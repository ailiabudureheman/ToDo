package com.example.todo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.todo.R
import com.example.todo.data.Task
import com.example.todo.data.TaskRepository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    taskRepository: TaskRepository,
    modifier: Modifier = Modifier
) {
    val tasks = remember { mutableStateOf<List<Task>>(emptyList()) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    fun loadTasks() {
        coroutineScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val allTasks = taskRepository.getAllActiveTasks()
                tasks.value = allTasks
            } catch (e: Exception) {
                errorMessage.value = "加载数据失败，请重试"
            } finally {
                isLoading.value = false
            }
        }
    }
    
    // 加载任务数据
    LaunchedEffect(Unit) {
        loadTasks()
    }
    
    // 计算统计数据
    val statsData = remember(tasks.value) {
        calculateStats(tasks.value)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stats_title)) },
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
                tasks.value.isEmpty() -> {
                    // 空状态
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.empty_stats),
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "添加任务后查看统计数据",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                else -> {
                    // 统计数据
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // 统计卡片
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp)
                        ) {
                            // 概览统计
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 24.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.tasks_total),
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = statsData.totalTasks.toString(),
                                    style = androidx.compose.material3.MaterialTheme.typography.displayMedium
                                )
                                
                                Text(
                                    text = stringResource(R.string.tasks_completed),
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                                Text(
                                    text = statsData.completedTasks.toString(),
                                    style = androidx.compose.material3.MaterialTheme.typography.displayMedium
                                )
                                
                                Text(
                                    text = stringResource(R.string.tasks_pending),
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                                Text(
                                    text = statsData.pendingTasks.toString(),
                                    style = androidx.compose.material3.MaterialTheme.typography.displayMedium
                                )
                                
                                Text(
                                    text = stringResource(R.string.completion_rate),
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                                Text(
                                    text = "${statsData.completionRate}%",
                                    style = androidx.compose.material3.MaterialTheme.typography.displayMedium
                                )
                            }
                            
                            // 最近7天任务柱状图
                            Text(
                                text = stringResource(R.string.last_7_days),
                                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            // MPAndroidChart BarChart
                            AndroidView(
                                factory = { context ->
                                    BarChart(context).apply {
                                        // 配置图表
                                        description.isEnabled = false
                                        setTouchEnabled(true)
                                        setDrawGridBackground(false)
                                        setDrawBarShadow(false)
                                        
                                        // 配置X轴
                                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                                        xAxis.setDrawGridLines(false)
                                        xAxis.valueFormatter = IndexAxisValueFormatter(statsData.last7DaysLabels)
                                        xAxis.granularity = 1f
                                        xAxis.setLabelCount(statsData.last7DaysLabels.size, true)
                                        
                                        // 配置Y轴
                                        axisLeft.setDrawGridLines(true)
                                        axisLeft.granularity = 1f
                                        axisLeft.setLabelCount(5, true)
                                        axisRight.isEnabled = false
                                        
                                        // 设置数据
                                        val entries = mutableListOf<BarEntry>()
                                        statsData.last7DaysData.forEachIndexed { index, count ->
                                            entries.add(BarEntry(index.toFloat(), count.toFloat()))
                                        }
                                        
                                        val dataSet = BarDataSet(entries, "任务数")
                                        dataSet.color = context.getColor(android.R.color.holo_blue_light)
                                        dataSet.valueTextColor = context.getColor(android.R.color.black)
                                        dataSet.valueTextSize = 12f
                                        
                                        val data = BarData(dataSet)
                                        data.barWidth = 0.6f
                                        
                                        this.data = data
                                        // 动画效果
                                        animateXY(1000, 1000)
                                        invalidate()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(300.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 统计数据类
data class StatsData(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val completionRate: Int,
    val last7DaysData: List<Int>,
    val last7DaysLabels: List<String>
)

// 计算统计数据
fun calculateStats(tasks: List<Task>): StatsData {
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val pendingTasks = totalTasks - completedTasks
    val completionRate = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0
    
    // 计算最近7天的任务数据
    val last7DaysData = mutableListOf<Int>()
    val last7DaysLabels = mutableListOf<String>()
    val formatter = DateTimeFormatter.ofPattern("MM/dd")
    
    for (i in 6 downTo 0) {
        val date = LocalDate.now().minusDays(i.toLong())
        val dayTasks = tasks.count { task ->
            val taskDate = task.createdAt.toLocalDate()
            taskDate.isEqual(date)
        }
        
        last7DaysData.add(dayTasks)
        last7DaysLabels.add(date.format(formatter))
    }
    
    return StatsData(
        totalTasks = totalTasks,
        completedTasks = completedTasks,
        pendingTasks = pendingTasks,
        completionRate = completionRate,
        last7DaysData = last7DaysData,
        last7DaysLabels = last7DaysLabels
    )
}
