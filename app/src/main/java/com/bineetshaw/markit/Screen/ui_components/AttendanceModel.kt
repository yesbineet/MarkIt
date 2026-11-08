package com.bineetshaw.markit.Screen.ui_components

import java.time.DayOfWeek

enum class DayAttendanceStatus {
    ATTENDED,
    ABSENT,
    PENDING,
    OFF_DAY
}
data class DayDotItem(
    val dayLabel: String,
    val dayOfWeek: DayOfWeek,
    val countOrText: String,
    val status: DayAttendanceStatus,
    val isToday: Boolean = false
)
data class AttendanceSummaryState(
    val attendedClasses: Int = 60,
    val totalConductedClasses: Int = 73,
    val minimumRequirementPercentage: Float = 0.75f,
    val weeklyDays: List<DayDotItem> = emptyList()
) {

    // Current Attendance % (e.g. 82.19% -> 82%)
    val currentPercentage: Int
        get() = if (totalConductedClasses == 0) 100
    else ((attendedClasses.toFloat() / totalConductedClasses) * 100).toInt()

    // Bunk Engine: Safe skip buffer
    // Formula: floor((Attended - (Req * Total)) / Req)
    val safeBunkBuffer: Int
        get() {
            if (totalConductedClasses == 0) return 0
            val buffer = (attendedClasses - (minimumRequirementPercentage * totalConductedClasses)) / minimumRequirementPercentage
            return buffer.toInt().coerceAtLeast(0)
        }

    // Classes needed to recover if below threshold
    // Formula: ceil(((Req * Total) - Attended) / (1 - Req))
    val classesNeededToRecover: Int
        get() {
            if (currentPercentage >= (minimumRequirementPercentage * 100)) return 0
            val needed = ((minimumRequirementPercentage * totalConductedClasses) - attendedClasses) / (1f - minimumRequirementPercentage)
            return kotlin.math.ceil(needed).toInt()
        }

    val isSafe: Boolean
        get() = currentPercentage >= (minimumRequirementPercentage * 100)
}