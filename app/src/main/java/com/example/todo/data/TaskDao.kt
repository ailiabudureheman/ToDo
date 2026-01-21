
package com.example.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getAllActiveTasks(): List<Task>
    
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getPendingTasks(): List<Task>
    
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getCompletedTasks(): List<Task>
    
    @Query("SELECT * FROM tasks WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    suspend fun getDeletedTasks(): List<Task>
    
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND (title LIKE :searchQuery OR description LIKE :searchQuery) ORDER BY createdAt DESC")
    suspend fun searchTasks(searchQuery: String): List<Task>
    
    @Insert
    suspend fun insertTask(task: Task): Long
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Query("UPDATE tasks SET isDeleted = 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :taskId")
    suspend fun markAsDeleted(taskId: Int)
    
    @Query("UPDATE tasks SET isDeleted = 0, updatedAt = CURRENT_TIMESTAMP WHERE id = :taskId")
    suspend fun restoreTask(taskId: Int)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
    
    @Query("DELETE FROM tasks WHERE isDeleted = 1")
    suspend fun deleteAllDeletedTasks()
}
