
package com.example.todo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.todo.data.Task
import java.time.format.DateTimeFormatter

@Composable
fun TaskItem(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val cardColor = if (task.isCompleted) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 300)
    )
    
    var isPressed by remember { mutableStateOf(false) }
    val pressedScale = if (isPressed) 0.98f else 1f
    val animatedScale by animateFloatAsState(
        targetValue = pressedScale,
        animationSpec = tween(durationMillis = 100)
    )
    
    // 添加本地状态来跟踪Checkbox的勾选状态，初始值始终为false
    var isChecked by remember { mutableStateOf(false) }
    
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor)
            .padding(16.dp)
            .scale(scale * animatedScale)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it } // 只更新本地状态，不直接完成任务
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onEdit()
                    }
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                }
                if (task.dueDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "截止时间: ${task.dueDate.format(dateFormatter)}",
                        style = MaterialTheme.typography.labelMedium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        } else if (task.dueDate.isBefore(java.time.LocalDateTime.now())) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
                // 根据任务状态和用户操作显示不同的按钮
                when {
                    // 当任务已删除时，只在用户勾选后显示"恢复"按钮
                    task.isDeleted && isChecked -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = {
                                onEdit() // 在废纸桶中，onEdit回调用于恢复任务
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("恢复")
                        }
                    }
                    // 当任务未完成且用户勾选后，显示"完成"按钮
                    !task.isCompleted && isChecked -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = {
                                onToggleComplete() // 点击完成按钮才真正完成任务
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("完成")
                        }
                    }
                    // 当任务已完成且用户取消勾选后，显示"恢复"按钮
                    task.isCompleted && isChecked -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = {
                                onToggleComplete() // 点击恢复按钮才真正恢复任务
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("恢复")
                        }
                    }
                }
            }
            Row {
                IconButton(
                    onClick = {
                        onEdit()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = {
                        onDelete()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
