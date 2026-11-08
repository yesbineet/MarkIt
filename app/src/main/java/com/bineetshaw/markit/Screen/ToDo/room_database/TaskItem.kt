package com.bineetshaw.markit.Screen.ToDo.room_database
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "tasks")
class TaskItem (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val taskName: String,
    val isDone: Boolean = false
)
