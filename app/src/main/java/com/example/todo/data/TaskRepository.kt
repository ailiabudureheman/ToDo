
package com.example.todo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class TaskRepository(private val taskDao: TaskDao) {
    suspend fun getAllActiveTasks(): List<Task> {
        return taskDao.getAllActiveTasks()
    }
    
    suspend fun getPendingTasks(): List<Task> {
        return taskDao.getPendingTasks()
    }
    
    suspend fun getCompletedTasks(): List<Task> {
        return taskDao.getCompletedTasks()
    }
    
    suspend fun getDeletedTasks(): List<Task> {
        return taskDao.getDeletedTasks()
    }
    
    suspend fun searchTasks(searchQuery: String): List<Task> {
        return taskDao.searchTasks("%$searchQuery%")
    }
    
    suspend fun addTask(task: Task): Long {
        return taskDao.insertTask(task)
    }
    
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
    
    suspend fun markAsDeleted(taskId: Int) {
        taskDao.markAsDeleted(taskId)
    }
    
    suspend fun restoreTask(taskId: Int) {
        taskDao.restoreTask(taskId)
    }
    
    private suspend fun getTaskById(taskId: Int): Task? {
        // 这里我们需要添加一个方法来根据ID获取任务
        // 先从所有状态中查找
        val allTasks = getAllTasks()
        return allTasks.find { it.id == taskId }
    }
    
    suspend fun getAllTasks(): List<Task> {
        // 获取所有任务，包括已删除的
        val pending = getPendingTasks()
        val completed = getCompletedTasks()
        val deleted = getDeletedTasks()
        return pending + completed + deleted
    }
    
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
    
    suspend fun deleteTaskById(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }
    
    suspend fun deleteAllDeletedTasks() {
        taskDao.deleteAllDeletedTasks()
    }
}
