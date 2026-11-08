package com.bineetshaw.markit.Screen.ToDo.repository

import com.bineetshaw.markit.Screen.ToDo.room_database.TaskDao
import com.bineetshaw.markit.Screen.ToDo.room_database.TaskItem
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    fun getALLTasks(): Flow<List<TaskItem>> {

        return dao.getALLTasks()
    }

    suspend fun insert(task: TaskItem) = dao.insert(task)


    suspend fun update(task: TaskItem) = dao.update(task)


    suspend fun delete(task: TaskItem) = dao.delete(task)
}