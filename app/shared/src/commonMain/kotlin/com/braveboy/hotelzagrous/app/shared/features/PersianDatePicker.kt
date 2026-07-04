package com.braveboy.hotelzagrous.app.shared.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.braveboy.hotelzagrous.core.DateUtils
import kotlin.time.Clock

@Composable
fun PersianDatePicker(
    initialDate: String,
    onDateSelected: (String, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val now = Clock.System.now().toEpochMilliseconds()
    val todayJalali = DateUtils.convertMillisToJalaliString(now)
    
    val initialParts = if (initialDate.isNotBlank()) initialDate.split("/") else todayJalali.split("/")
    val initialYear = initialParts.getOrNull(0)?.toIntOrNull() ?: todayJalali.split("/")[0].toInt()
    val initialMonth = initialParts.getOrNull(1)?.toIntOrNull() ?: todayJalali.split("/")[1].toInt()
    val initialDay = initialParts.getOrNull(2)?.toIntOrNull() ?: todayJalali.split("/")[2].toInt()

    var currentYear by remember { mutableStateOf(initialYear) }
    var currentMonth by remember { mutableStateOf(initialMonth) }
    var selectedDay by remember { mutableStateOf(initialDay) }

    val monthNames = DateUtils.getJalaliMonthNames()
    val daysInMonth = DateUtils.getDaysInJalaliMonth(currentYear, currentMonth)
    val firstDayOfMonth = DateUtils.getFirstDayOfMonth(currentYear, currentMonth) // 0 for Shanbeh

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentMonth == 12) {
                        currentMonth = 1
                        currentYear++
                    } else {
                        currentMonth++
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Next Month")
                }

                Text(
                    text = "${monthNames[currentMonth - 1]} $currentYear",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    if (currentMonth == 1) {
                        currentMonth = 12
                        currentYear--
                    } else {
                        currentMonth--
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Previous Month")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekdays
            val weekDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Days Grid
            val totalCells = firstDayOfMonth + daysInMonth
            val cells = (0 until totalCells).toList()

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(260.dp),
                userScrollEnabled = false
            ) {
                items(cells) { index ->
                    if (index < firstDayOfMonth) {
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val day = index - firstDayOfMonth + 1
                        val isSelected = day == selectedDay && currentMonth == initialMonth && currentYear == initialYear
                        val isToday = day == todayJalali.split("/")[2].toInt() && 
                                      currentMonth == todayJalali.split("/")[1].toInt() && 
                                      currentYear == todayJalali.split("/")[0].toInt()

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary 
                                    else if (isToday) MaterialTheme.colorScheme.primaryContainer 
                                    else Color.Transparent
                                )
                                .clickable {
                                    selectedDay = day
                                    val dateStr = "$currentYear/${currentMonth.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
                                    val millis = DateUtils.convertDateToTimeMillis(dateStr)
                                    onDateSelected(dateStr, millis)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                        else if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("انصراف")
                }
            }
        }
    }
}

@Composable
fun PersianDatePickerDialog(
    initialDate: String,
    onDateSelected: (String, Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        text = {
            PersianDatePicker(
                initialDate = initialDate,
                onDateSelected = onDateSelected,
                onDismiss = onDismiss
            )
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}
