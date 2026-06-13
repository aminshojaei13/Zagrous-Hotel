package com.braveboy.hotelzagrous.app.shared.features.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.braveboy.hotelzagrous.core.Room
import kotlinx.datetime.*

@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.state.collectAsState()
    var showAddRoomDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("پنل مدیریت هتل زاگرس", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { showAddRoomDialog = true }) {
                Text("افزودن اتاق")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Button(onClick = { viewModel.onIntent(AdminIntent.ExportPdf) }) {
            Text("خروجی PDF لیست غذاها")
        }

        Spacer(Modifier.height(16.dp))

        Text("مدیریت اتاق‌ها", style = MaterialTheme.typography.titleLarge)
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.rooms) { room ->
                RoomAdminCard(room) { checkIn, checkOut, checkInMillis, checkOutMillis ->
                    viewModel.onIntent(AdminIntent.UpdateRoomStay(room.roomNumber, checkIn, checkOut, checkInMillis, checkOutMillis))
                }
            }
        }
    }

    if (showAddRoomDialog) {
        AddRoomDialog(
            onDismiss = { showAddRoomDialog = false },
            onConfirm = { room ->
                viewModel.onIntent(AdminIntent.AddRoom(room))
                showAddRoomDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: String,
    onDateSelected: (String, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePicker = true }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formattedDate = convertMillisToDateString(millis)
                        onDateSelected(formattedDate, millis)
                    }
                    showDatePicker = false
                }) {
                    Text("تایید")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("انصراف")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

fun convertMillisToDateString(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val localDate = instant.toLocalDateTime(TimeZone.UTC).date
    return gregorianToJalali(localDate.year, localDate.monthNumber, localDate.dayOfMonth)
}

fun gregorianToJalali(gy: Int, gm: Int, gd: Int): String {
    val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

    var gDayNo = 365 * (gy - 1600) + (gy - 1501) / 4 - (gy - 1601) / 100 + (gy - 1201) / 400
    for (i in 1 until gm) gDayNo += gDaysInMonth[i]
    if (gm > 2 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) gDayNo++
    gDayNo += gd - 1

    var jDayNo = gDayNo - 79
    val jNp = jDayNo / 12053
    jDayNo %= 12053
    var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
    jDayNo %= 1461
    if (jDayNo >= 366) {
        jy += (jDayNo - 1) / 365
        jDayNo = (jDayNo - 1) % 365
    }

    var jm = 0
    for (i in 1..12) {
        jm = i
        if (jDayNo < jDaysInMonth[i]) break
        jDayNo -= jDaysInMonth[i]
    }
    val jd = jDayNo + 1
    
    return "$jy/${jm.toString().padStart(2, '0')}/${jd.toString().padStart(2, '0')}"
}

@Composable
fun AddRoomDialog(onDismiss: () -> Unit, onConfirm: (Room) -> Unit) {
    var roomNumber by remember { mutableStateOf("") }
    var guestName by remember { mutableStateOf("") }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var checkInMillis by remember { mutableStateOf(0L) }
    var checkOutMillis by remember { mutableStateOf(0L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن اتاق جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    label = { Text("شماره اتاق") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    label = { Text("نام مهمان") },
                    modifier = Modifier.fillMaxWidth()
                )
                DatePickerField(
                    label = "تاریخ ورود",
                    value = checkIn,
                    onDateSelected = { date, millis -> 
                        checkIn = date
                        checkInMillis = millis
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DatePickerField(
                    label = "تاریخ خروج",
                    value = checkOut,
                    onDateSelected = { date, millis -> 
                        checkOut = date
                        checkOutMillis = millis
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (roomNumber.isNotBlank() && guestName.isNotBlank()) {
                    onConfirm(Room(roomNumber, guestName, checkIn, checkOut, checkInMillis, checkOutMillis))
                }
            }) {
                Text("تایید")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

@Composable
fun RoomAdminCard(room: Room, onUpdate: (String, String, Long, Long) -> Unit) {
    var checkIn by remember { mutableStateOf(room.checkInDate) }
    var checkOut by remember { mutableStateOf(room.checkOutDate) }
    var checkInMillis by remember { mutableStateOf(room.checkInEpochMillis) }
    var checkOutMillis by remember { mutableStateOf(room.checkOutEpochMillis) }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("اتاق ${room.roomNumber} - ${room.guestName}")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickerField(
                    label = "ورود",
                    value = checkIn,
                    onDateSelected = { date, millis -> 
                        checkIn = date
                        checkInMillis = millis
                    },
                    modifier = Modifier.weight(1f)
                )
                DatePickerField(
                    label = "خروج",
                    value = checkOut,
                    onDateSelected = { date, millis -> 
                        checkOut = date
                        checkOutMillis = millis
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Button(
                onClick = { onUpdate(checkIn, checkOut, checkInMillis, checkOutMillis) },
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            ) {
                Text("به‌روزرسانی")
            }
        }
    }
}
