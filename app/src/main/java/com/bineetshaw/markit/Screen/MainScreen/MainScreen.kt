package com.bineetshaw.markit.Screen.MainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bineetshaw.markit.R
import com.bineetshaw.markit.Screen.ToDo.TaskItem
import com.bineetshaw.markit.Screen.ui_components.MyBottomNavBar

@Composable
fun MainScreen(
    tasks: List<TaskItem> = emptyList(),
    onToggleTask: (String) -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    onNavigateToToDo: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
    contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF141415))

        )
    }
    Scaffold(
        bottomBar = {
            // Wrap your bar inside a full-width Row to handle centering
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                MyBottomNavBar(
                    selectedIndex = 0,
                    onItemSelected = { index ->
                        if (index == 1) {
                            onNavigateToToDo()
                        }
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F2))
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            HomeTodoSection(
                tasks = tasks,
                onToggleTask = onToggleTask,
                onAddTaskClick = onAddTaskClick,
                onSeeAllClick = onNavigateToToDo
            )
        }
    }
}

@Composable
fun HomeTodoSection(
    tasks: List<TaskItem>,
    onToggleTask: (String) -> Unit,
    onAddTaskClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSegment by remember { mutableIntStateOf(0) } // 0: To-do, 1: Overview
    val uncompletedTasks = tasks.filter { !it.isDone }

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Segment Switcher (To-do | Overview) + Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE9E9EB))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // To-do Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedSegment == 0) Color.White else Color.Transparent)
                        .clickable { selectedSegment = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "To-do",
                            fontSize = 14.sp,
                            fontWeight = if (selectedSegment == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedSegment == 0) Color(0xFF141415) else Color(0xFF8E8E93)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF141415)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uncompletedTasks.size.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Overview Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedSegment == 1) Color.White else Color.Transparent)
                        .clickable { selectedSegment = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Overview",
                        fontSize = 14.sp,
                        fontWeight = if (selectedSegment == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSegment == 1) Color(0xFF141415) else Color(0xFF8E8E93)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Add (+) Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF141415))
                    .clickable { onAddTaskClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.plus),
                    contentDescription = "Add Task",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. White To-Do Card Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .clickable { onSeeAllClick() }
                .padding(vertical = 6.dp, horizontal = 20.dp)
        ) {
            val displayList = uncompletedTasks.take(3)

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No pending tasks",
                        fontSize = 14.sp,
                        color = Color(0xFF8E8E93),
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    displayList.forEachIndexed { index, task ->
                        HomeTodoItemRow(
                            task = task,
                            onToggle = { onToggleTask(task.id) }
                        )

                        if (index < displayList.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 38.dp, top = 8.dp, bottom = 8.dp),
                                thickness = 0.8.dp,
                                color = Color(0xFFF0F0F2)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTodoItemRow(
    task: TaskItem,
    onToggle: () -> Unit
) {
    val isTomorrow = task.scheduleTag.equals("Tomorrow", ignoreCase = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (task.isDone) Color(0xFF141415) else Color.Transparent)
                    .border(
                        width = 1.5.dp,
                        color = if (task.isDone) Color(0xFF141415) else Color(0xFFD1D1D6),
                        shape = CircleShape
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isDone) {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isDone) Color(0xFF8E8E93) else Color(0xFF141415),
                    lineHeight = 16.sp,
                    style = androidx.compose.ui.text.TextStyle(
                        platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Text(
                    text = task.subject,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8E8E93)
                )
            }
        }

        if (task.scheduleTag.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isTomorrow) Color(0xFF141415) else Color.Transparent)
                    .then(
                        if (!isTomorrow) {
                            Modifier.border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(14.dp))
                        } else Modifier
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = task.scheduleTag,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isTomorrow) Color.White else Color(0xFF141415)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
