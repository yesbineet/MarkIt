package com.bineetshaw.markit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bineetshaw.markit.Screen.MainScreen.MainScreen
import com.bineetshaw.markit.Screen.ToDo.TaskItem
import com.bineetshaw.markit.Screen.ToDo.TasksScreen
import com.bineetshaw.markit.Screen.WelcomeScreen.WelcomeScreen
import com.bineetshaw.markit.ui.theme.MarkItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarkItTheme {
                val navController = rememberNavController()
                val taskList = remember { mutableStateListOf<TaskItem>() }

                NavHost(
                    navController = navController,
                    startDestination = "welcome",
                    enterTransition = { fadeIn(animationSpec = tween(120)) },
                    exitTransition = { fadeOut(animationSpec = tween(120)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(120)) },
                    popExitTransition = { fadeOut(animationSpec = tween(120)) }
                ) {
                    composable("welcome") {
                        WelcomeScreen(
                            onGetStartedClick = {
                                navController.navigate("main") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            },
                            onAlreadyHaveAccountClick = {
                                navController.navigate("main") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        MainScreen(
                            tasks = taskList,
                            onToggleTask = { id ->
                                val idx = taskList.indexOfFirst { it.id == id }
                                if (idx != -1) {
                                    taskList[idx] = taskList[idx].copy(isDone = !taskList[idx].isDone)
                                }
                            },
                            onAddTaskClick = {
                                navController.navigate("todo") {
                                    popUpTo("main") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onNavigateToToDo = {
                                navController.navigate("todo") {
                                    popUpTo("main") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }

                    composable("todo") {
                        TasksScreen(
                            tasks = taskList,
                            currentNavIndex = 1,
                            onNavItemSelected = { index ->
                                if (index == 0) {
                                    navController.navigate("main") {
                                        popUpTo("main") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            onAddTask = { title, subject, deadline ->
                                taskList.add(
                                    TaskItem(
                                        title = title,
                                        subject = subject,
                                        scheduleTag = deadline,
                                        isPriority = taskList.isEmpty(),
                                        isToday = deadline.contains("Today", ignoreCase = true),
                                        isUpcoming = true
                                    )
                                )
                            },
                            onToggleTaskDone = { id ->
                                val idx = taskList.indexOfFirst { it.id == id }
                                if (idx != -1) {
                                    taskList[idx] = taskList[idx].copy(isDone = !taskList[idx].isDone)
                                }
                            },
                            onDeleteTask = { task ->
                                taskList.remove(task)
                            },
                            onRestoreTask = { task, index ->
                                if (index in 0..taskList.size) {
                                    taskList.add(index, task)
                                } else {
                                    taskList.add(task)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
