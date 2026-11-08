package com.bineetshaw.markit.Screen.ToDo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bineetshaw.markit.Screen.ToDo.repository.TaskRepository
import com.bineetshaw.markit.Screen.ToDo.room_database.TaskDatabase
import com.bineetshaw.markit.Screen.ToDo.room_database.TaskItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = TaskDatabase.getDatabase(application).taskDao
    private val repository = TaskRepository(dao)

    val allTasks: StateFlow<List<TaskItem>> = repository.getALLTasks()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addTask(task: TaskItem) {
        viewModelScope.launch {
            repository.insert(task)
        }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}