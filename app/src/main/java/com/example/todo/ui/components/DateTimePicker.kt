package com.example.todo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DateTimePicker(
    value: LocalDateTime?,
    onValueChange: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDate = remember { mutableStateOf(value?.toLocalDate() ?: LocalDate.now()) }
    val selectedTime = remember { mutableStateOf(value?.toLocalTime() ?: LocalTime.now().withSecond(0)) }
    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showDatePicker.value = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = selectedDate.value.format(dateFormatter))
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { showTimePicker.value = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = selectedTime.value.format(timeFormatter))
            }
        }
        
        if (showDatePicker.value) {
            DatePickerDialog(
                selectedDate = selectedDate.value,
                onDateSelected = { date ->
                    selectedDate.value = date
                    showDatePicker.value = false
                    updateDateTime(selectedDate.value, selectedTime.value, onValueChange)
                },
                onCancel = { showDatePicker.value = false }
            )
        }
        
        if (showTimePicker.value) {
            TimePickerDialog(
                selectedTime = selectedTime.value,
                onTimeSelected = { time ->
                    selectedTime.value = time
                    showTimePicker.value = false
                    updateDateTime(selectedDate.value, selectedTime.value, onValueChange)
                },
                onCancel = { showTimePicker.value = false }
            )
        }
    }
}

private fun updateDateTime(date: LocalDate, time: LocalTime, onValueChange: (LocalDateTime?) -> Unit) {
    val dateTime = LocalDateTime.of(date, time)
    onValueChange(dateTime)
}

@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onCancel: () -> Unit
) {
    val currentYear = selectedDate.year
    val currentMonth = selectedDate.monthValue
    val currentDay = selectedDate.dayOfMonth
    
    val years = (currentYear - 5..currentYear + 5).toList()
    val months = (1..12).toList()
    val daysInMonth = LocalDate.of(currentYear, currentMonth, 1).lengthOfMonth()
    val days = (1..daysInMonth).toList()
    
    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedDay by remember { mutableStateOf(currentDay) }
    
    LaunchedEffect(selectedYear, selectedMonth) {
        val newDaysInMonth = LocalDate.of(selectedYear, selectedMonth, 1).lengthOfMonth()
        if (selectedDay > newDaysInMonth) {
            selectedDay = newDaysInMonth
        }
    }
    
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = "选择日期") },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            selectedYear = if (selectedYear > years.first()) selectedYear - 1 else years.last()
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "减少年份")
                    }
                    Text(
                        text = selectedYear.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(
                        onClick = {
                            selectedYear = if (selectedYear < years.last()) selectedYear + 1 else years.first()
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "增加年份")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            selectedMonth = if (selectedMonth > 1) selectedMonth - 1 else 12
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "减少月份")
                    }
                    Text(
                        text = selectedMonth.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(
                        onClick = {
                            selectedMonth = if (selectedMonth < 12) selectedMonth + 1 else 1
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "增加月份")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val daysInMonth = LocalDate.of(selectedYear, selectedMonth, 1).lengthOfMonth()
                    IconButton(
                        onClick = {
                            selectedDay = if (selectedDay > 1) selectedDay - 1 else daysInMonth
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "减少日期")
                    }
                    Text(
                        text = selectedDay.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(
                        onClick = {
                            selectedDay = if (selectedDay < daysInMonth) selectedDay + 1 else 1
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "增加日期")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDateSelected(LocalDate.of(selectedYear, selectedMonth, selectedDay))
            }) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = "取消")
            }
        }
    )
}

@Composable
fun TimePickerDialog(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onCancel: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(selectedTime.hour) }
    var selectedMinute by remember { mutableStateOf(selectedTime.minute) }
    
    val hours = (0..23).toList()
    val minutes = listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)
    
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = "选择时间") },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            selectedHour = if (selectedHour < 23) selectedHour + 1 else 0
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "增加小时")
                    }
                    Text(
                        text = selectedHour.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            selectedHour = if (selectedHour > 0) selectedHour - 1 else 23
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "减少小时")
                    }
                }
                
                Text(text = ":", modifier = Modifier.padding(horizontal = 16.dp))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            val currentIndex = minutes.indexOf(selectedMinute)
                            val newIndex = if (currentIndex == minutes.size - 1) 0 else currentIndex + 1
                            selectedMinute = minutes[newIndex]
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "增加分钟")
                    }
                    Text(
                        text = selectedMinute.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            val currentIndex = minutes.indexOf(selectedMinute)
                            val newIndex = if (currentIndex == 0) minutes.size - 1 else currentIndex - 1
                            selectedMinute = minutes[newIndex]
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "减少分钟")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onTimeSelected(LocalTime.of(selectedHour, selectedMinute, 0))
            }) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = "取消")
            }
        }
    )
}

@Composable
fun NumberPicker(
    values: List<Int>,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                val currentIndex = values.indexOf(selectedValue)
                val newIndex = if (currentIndex == values.size - 1) 0 else currentIndex + 1
                onValueChange(values[newIndex])
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "增加")
        }
        
        Text(
            text = selectedValue.toString().padStart(2, '0'),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        IconButton(
            onClick = {
                val currentIndex = values.indexOf(selectedValue)
                val newIndex = if (currentIndex == 0) values.size - 1 else currentIndex - 1
                onValueChange(values[newIndex])
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Default.Clear, contentDescription = "减少")
        }
    }
}