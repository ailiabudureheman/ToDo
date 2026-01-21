
package com.example.todo.di

import android.content.Context
import com.example.todo.data.TaskDatabase
import com.example.todo.data.TaskRepository

interface AppContainer {
    val taskRepository: TaskRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val taskRepository: TaskRepository by lazy {
        val database = TaskDatabase.getDatabase(context)
        TaskRepository(database.taskDao())
    }
}
