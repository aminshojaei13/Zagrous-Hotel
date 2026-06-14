package com.braveboy.hotelzagrous.app.shared.features.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodItem

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

        state.error?.let { error ->
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Text("آمار رزرو غذاها", style = MaterialTheme.typography.titleLarge)
        ReservationSummary(state.reservations, state.foods)
        
        Spacer(Modifier.height(16.dp))

        Text("مدیریت اتاق‌ها", style = MaterialTheme.typography.titleLarge)
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.rooms) { room ->
                RoomAdminCard(room) { checkIn, checkOut, checkInMillis, checkOutMillis, guestCount ->
                    viewModel.onIntent(AdminIntent.UpdateRoomStay(room.roomNumber, checkIn, checkOut, checkInMillis, checkOutMillis, guestCount))
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

@Composable
fun ReservationSummary(reservations: List<FoodReservation>, foods: List<FoodItem>) {
    val summary = reservations.groupBy { it.date }.toList().sortedBy { it.first }
    val foodMap = foods.associateBy { it.id }
    
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (summary.isEmpty()) {
                Text("رزروی ثبت نشده است")
            }
            for (entry in summary) {
                val date = entry.first
                val dailyReservations = entry.second
                
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Text("تاریخ: $date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    val lunchSelections = dailyReservations.flatMap { it.guestMealSelections }.mapNotNull { it.lunchFoodId }
                    val lunchCounts = lunchSelections.groupBy { it }.mapValues { it.value.size }
                    
                    if (lunchCounts.isNotEmpty()) {
                        Text("ناهار:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                        lunchCounts.forEach { (id, count) ->
                            Text("  - ${foodMap[id]?.name ?: "نامعلوم"}: $count پرس")
                        }
                    } else {
                        Text("ناهار: موردی رزرو نشده", style = MaterialTheme.typography.bodySmall)
                    }

                    val dinnerSelections = dailyReservations.flatMap { it.guestMealSelections }.mapNotNull { it.dinnerFoodId }
                    val dinnerCounts = dinnerSelections.groupBy { it }.mapValues { it.value.size }
                    
                    if (dinnerCounts.isNotEmpty()) {
                        Text("شام:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                        dinnerCounts.forEach { (id, count) ->
                            Text("  - ${foodMap[id]?.name ?: "نامعلوم"}: $count پرس")
                        }
                    } else {
                        Text("شام: موردی رزرو نشده", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
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
    return DateUtils.convertMillisToJalaliString(millis)
}

@Composable
fun AddRoomDialog(onDismiss: () -> Unit, onConfirm: (Room) -> Unit) {
    var roomNumber by remember { mutableStateOf("") }
    var guestName by remember { mutableStateOf("") }
    var guestCount by remember { mutableStateOf("1") }
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
                OutlinedTextField(
                    value = guestCount,
                    onValueChange = { guestCount = it },
                    label = { Text("تعداد نفرات") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    onConfirm(Room(roomNumber, guestName, guestCount.toIntOrNull() ?: 1, checkIn, checkOut, checkInMillis, checkOutMillis))
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
fun RoomAdminCard(room: Room, onUpdate: (String, String, Long, Long, Int) -> Unit) {
    var checkIn by remember { mutableStateOf(room.checkInDate) }
    var checkOut by remember { mutableStateOf(room.checkOutDate) }
    var checkInMillis by remember { mutableStateOf(room.checkInEpochMillis) }
    var checkOutMillis by remember { mutableStateOf(room.checkOutEpochMillis) }
    var guestCount by remember { mutableStateOf(room.guestCount.toString()) }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("اتاق ${room.roomNumber} - ${room.guestName}", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = guestCount,
                onValueChange = { guestCount = it },
                label = { Text("تعداد نفرات") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
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
                onClick = { onUpdate(checkIn, checkOut, checkInMillis, checkOutMillis, guestCount.toIntOrNull() ?: 1) },
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            ) {
                Text("به‌روزرسانی")
            }
        }
    }
}
