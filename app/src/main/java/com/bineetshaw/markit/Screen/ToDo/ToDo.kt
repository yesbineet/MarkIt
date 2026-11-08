package com.bineetshaw.markit.Screen.ToDo

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bineetshaw.markit.R
import com.bineetshaw.markit.Screen.ui_components.MyBottomNavBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// --- Color Palette ---
private val ScreenBg = Color(0xFFF7F7F8)
private val BlackPill = Color(0xFF141415)
private val CardWhite = Color(0xFFFFFFFF)
private val BorderSubtle = Color(0xFFECECEE)
private val TextPrimary = Color(0xFF141415)
private val TextSecondary = Color(0xFF8E8E93)

enum class TaskCategory { ALL, TODAY, UPCOMING, DONE }

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val scheduleTag: String, // Deadline string (e.g. "Tomorrow", "Thu, 5 Nov")
    val isPriority: Boolean = false,
    val isToday: Boolean = false,
    val isUpcoming: Boolean = true,
    val isDone: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasks: List<TaskItem> = emptyList(),
    currentNavIndex: Int = 1,
    onNavItemSelected: (Int) -> Unit = {},
    onAddTask: (title: String, subject: String, deadline: String) -> Unit = { _, _, _ -> },
    onToggleTaskDone: (String) -> Unit = {},
    onDeleteTask: (TaskItem) -> Unit = { task ->
        (tasks as? MutableList<TaskItem>)?.remove(task)
    },
    onRestoreTask: (task: TaskItem, index: Int) -> Unit = { task, index ->
        (tasks as? MutableList<TaskItem>)?.let { list ->
            if (index in 0..list.size) list.add(index, task) else list.add(task)
        }
    }
) {
    var selectedCategory by remember { mutableStateOf(TaskCategory.ALL) }
    var showAddSheet by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TaskItem?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val openCount = tasks.count { !it.isDone }
    val doneCount = tasks.count { it.isDone }
    val todayCount = tasks.count { it.isToday && !it.isDone }
    val upcomingCount = tasks.count { it.isUpcoming && !it.isDone }

    val filteredTasks = when (selectedCategory) {
        TaskCategory.ALL -> tasks
        TaskCategory.TODAY -> tasks.filter { it.isToday && !it.isDone }
        TaskCategory.UPCOMING -> tasks.filter { it.isUpcoming && !it.isDone }
        TaskCategory.DONE -> tasks.filter { it.isDone }
    }

    // Modal sheet to ask for Title, Subject, and Deadline
    if (showAddSheet) {
        AddTaskBottomSheet(
            onDismiss = { showAddSheet = false },
            onConfirm = { title, subject, deadline ->
                onAddTask(title, subject, deadline)
                showAddSheet = false
            }
        )
    }

    // Delete Confirmation Dialog shown on Long-Press
    if (taskToDelete != null) {
        val targetTask = taskToDelete!!
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = {
                Text(
                    text = "Delete Task?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${targetTask.title}\"?",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val task = targetTask
                        taskToDelete = null
                        val originalIndex = tasks.indexOfFirst { it.id == task.id }
                        onDeleteTask(task)

                        coroutineScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            val result = snackbarHostState.showSnackbar(
                                message = "\"${task.title}\" deleted",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                onRestoreTask(task, if (originalIndex != -1) originalIndex else tasks.size)
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { taskToDelete = null }
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- 1. Top Header ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Your ",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextPrimary
                        )
                        Text(
                            text = "tasks",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$openCount open • $doneCount done",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }

                // Add Task Circular Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BlackPill)
                        .clickable { showAddSheet = true },
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

            Spacer(modifier = Modifier.height(18.dp))

            // --- 2. Filter Chips ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TaskFilterChip(
                    title = "All",
                    count = tasks.size,
                    isSelected = selectedCategory == TaskCategory.ALL,
                    onClick = { selectedCategory = TaskCategory.ALL }
                )
                TaskFilterChip(
                    title = "Today",
                    count = todayCount,
                    isSelected = selectedCategory == TaskCategory.TODAY,
                    onClick = { selectedCategory = TaskCategory.TODAY }
                )
                TaskFilterChip(
                    title = "Upcoming",
                    count = upcomingCount,
                    isSelected = selectedCategory == TaskCategory.UPCOMING,
                    onClick = { selectedCategory = TaskCategory.UPCOMING }
                )
                TaskFilterChip(
                    title = "Done",
                    count = doneCount,
                    isSelected = selectedCategory == TaskCategory.DONE,
                    onClick = { selectedCategory = TaskCategory.DONE }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // --- 3. Content: Empty State or Task List ---
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(ScreenBg)
                            .padding(vertical = 44.dp, horizontal = 24.dp)
                            .clickable { showAddSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ("No Task Yet"),
                            color = TextSecondary,

                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(filteredTasks, key = { _, item -> item.id }) { _, item ->
                        TaskCardItem(
                            task = item,
                            onToggle = { onToggleTaskDone(item.id) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                taskToDelete = item
                            }
                        )
                    }
                }
            }
        }

        // --- 4. Undo Snackbar Popup (placed above the BottomNavBar) ---
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
                .padding(horizontal = 24.dp)
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = BlackPill,
                contentColor = Color.White,
                actionColor = Color(0xFF6897EB),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // --- 5. Bottom Navigation Bar ---
        MyBottomNavBar(
            selectedIndex = currentNavIndex,
            onItemSelected = onNavItemSelected,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

// -------------------------------------------------------------
// Bottom Sheet Dialog: Asks for Title, Subject & Deadline
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (title: String, subject: String, deadline: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var deadlineText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    // Google's Material 3 Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
                            deadlineText = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = BlackPill, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CardWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "New Task",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Task Title
            Text("Task Title", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Presentation slides", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlackPill,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = ScreenBg,
                    unfocusedContainerColor = ScreenBg
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Subject
            Text("Subject", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                placeholder = { Text("e.g. Communication, DSA", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlackPill,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = ScreenBg,
                    unfocusedContainerColor = ScreenBg
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Deadline Field (Click opens DatePicker)
            Text("Deadline", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ScreenBg)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = deadlineText.ifEmpty { "Pick due date" },
                        fontSize = 15.sp,
                        color = if (deadlineText.isEmpty()) TextSecondary else TextPrimary,
                        fontWeight = if (deadlineText.isEmpty()) FontWeight.Normal else FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(R.drawable.alarm),
                        contentDescription = "Pick Deadline",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val finalSubject = subject.ifBlank { "General" }
                        val finalDeadline = deadlineText.ifBlank { "No deadline" }
                        onConfirm(title.trim(), finalSubject.trim(), finalDeadline)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlackPill,
                    disabledContainerColor = BorderSubtle
                )
            ) {
                Text("Add Task", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// -------------------------------------------------------------
// Component: Filter Chip
// -------------------------------------------------------------
@Composable
fun TaskFilterChip(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) BlackPill else CardWhite,
        label = "chipBg"
    )
    val textColor = if (isSelected) Color.White else TextPrimary
    val countColor = if (isSelected) Color.White.copy(alpha = 0.65f) else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else BorderSubtle,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = textColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = count.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = countColor
            )
        }
    }
}

// -------------------------------------------------------------
// Component: Task Card
// -------------------------------------------------------------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCardItem(
    task: TaskItem,
    onToggle: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val isDark = task.isPriority && !task.isDone
    val cardBg = if (isDark) BlackPill else CardWhite
    val titleColor = if (task.isDone) TextSecondary else (if (isDark) Color.White else TextPrimary)
    val subtitleColor = if (isDark) Color.White.copy(alpha = 0.6f) else TextSecondary
    val badgeBg = if (isDark) Color.White.copy(alpha = 0.12f) else ScreenBg

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(cardBg)
            .then(
                if (!isDark) Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(28.dp))
                else Modifier
            )
            .combinedClickable(
                onClick = { onToggle() },
                onLongClick = { onLongClick() }
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.alarm),
                        contentDescription = null,
                        tint = if (isDark) Color.White else TextPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (task.isDone) "Done" else task.scheduleTag,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else TextPrimary
                    )
                }

                Text(
                    text = task.subject,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtitleColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (task.isDone) BlackPill else Color.Transparent)
                        .border(
                            width = 1.5.dp,
                            color = if (task.isDone) BlackPill else (if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFFD1D1D6)),
                            shape = CircleShape
                        ),
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

                Text(
                    text = task.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TasksScreenPreview() {
    var navIndex by remember { mutableIntStateOf(1) }
    val taskList = remember { mutableStateListOf<TaskItem>() }

    TasksScreen(
        tasks = taskList,
        currentNavIndex = navIndex,
        onNavItemSelected = { navIndex = it },
        onAddTask = { title, subject, deadline ->
            taskList.add(
                TaskItem(
                    title = title,
                    subject = subject,
                    scheduleTag = deadline,
                    isPriority = taskList.isEmpty(), // First task gets dark priority background
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
