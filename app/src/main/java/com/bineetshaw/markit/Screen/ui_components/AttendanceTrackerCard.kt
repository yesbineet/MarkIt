package com.bineetshaw.markit.Screen.ui_components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bineetshaw.markit.ui.theme.MarkItTheme
import java.time.DayOfWeek

// --- Color Palette ---
private val CardBg = Color(0xFFFFFFFF)
private val DarkPill = Color(0xFF141415)
private val SubtitleGray = Color(0xFF8E8E93)
private val DividerColor = Color(0xFFF2F2F4)
private val AbsentBg = Color(0xFFFCE8E6)
private val AbsentRed = Color(0xFFE53935)
private val OffDayBg = Color(0xFFF2F2F5)
private val RingTrackColor = Color(0xFFEBEBEF)

@Composable
fun AttendanceTrackerCard(
    state: AttendanceSummaryState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(CardBg)
            .padding(horizontal = 24.dp, vertical = 22.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // --- 1. TOP ROW: Bunk Engine Text + Circular Gauge ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Bunk summary
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (state.isSafe) "You can still skip" else "Attention required",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SubtitleGray
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (state.isSafe) "${state.safeBunkBuffer} classes" else "Attend ${state.classesNeededToRecover} classes",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkPill
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (state.isSafe) "and stay above ${(state.minimumRequirementPercentage * 100).toInt()}%"
                        else "to recover above ${(state.minimumRequirementPercentage * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = SubtitleGray
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                // Right: Circular Attendance Ring
                AttendanceGaugeRing(
                    percentage = state.currentPercentage,
                    subtext = "overall",
                    modifier = Modifier.size(92.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Thin Horizontal Divider
            HorizontalDivider(
                thickness = 1.dp,
                color = DividerColor
            )

            Spacer(modifier = Modifier.height(18.dp))

            // --- 2. BOTTOM ROW: Weekly Day Dots (Mon - Sun) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.weeklyDays.forEach { item ->
                    AttendanceDayDot(item = item)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Component: Circular Progress Ring with Inner Text
// -------------------------------------------------------------------------
@Composable
fun AttendanceGaugeRing(
    percentage: Int,
    subtext: String,
    modifier: Modifier = Modifier,
    ringColor: Color = DarkPill,
    trackColor: Color = RingTrackColor,
    strokeWidth: Dp = 8.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val arcSize = size.minDimension - strokePx
            val topLeft = Offset(
                (size.width - arcSize) / 2f,
                (size.height - arcSize) / 2f
            )

            // Background Circle Track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokePx)
            )

            // Progress Arc (Starts from -90° which is the 12 o'clock top position)
            val sweepAngle = (percentage / 100f).coerceIn(0f, 1f) * 360f
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        // Center Percentage & Label
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPill
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = SubtitleGray
            )
        }
    }
}

// -------------------------------------------------------------------------
// Component: Single Day Dot Item (Day label + Dot indicator)
// -------------------------------------------------------------------------
@Composable
fun AttendanceDayDot(
    item: DayDotItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day Label ("Mon", "Tue", etc.)
        Text(
            text = item.dayLabel,
            fontSize = 12.sp,
            fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Medium,
            color = if (item.isToday) DarkPill else SubtitleGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Visual Indicator Dot
        when (item.status) {
            DayAttendanceStatus.ATTENDED -> {
                // Black filled circle with class count number
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkPill),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.countOrText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            DayAttendanceStatus.ABSENT -> {
                // Light red background with red '✕'
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AbsentBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AbsentRed,
                        textAlign = TextAlign.Center
                    )
                }
            }

            DayAttendanceStatus.PENDING -> {
                // Hollow white circle with thin dark border (e.g. Saturday)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(1.5.dp, DarkPill, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.countOrText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = DarkPill
                    )
                }
            }

            DayAttendanceStatus.OFF_DAY -> {
                // Light muted gray circle with dash (e.g. Sunday / Holiday)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(OffDayBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.countOrText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SubtitleGray
                    )
                }
            }
        }
    }
}