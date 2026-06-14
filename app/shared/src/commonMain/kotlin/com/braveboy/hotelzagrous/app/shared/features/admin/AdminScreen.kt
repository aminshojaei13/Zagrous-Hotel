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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.state.collectAsState()
    var showAddRoomDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پنل مدیریت هتل زاگرس") },
                actions = {
                    Button(onClick = { showAddRoomDialog = true }, modifier = Modifier.padding(end = 8.dp)) {
                        Text("افزودن اتاق")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.onIntent(AdminIntent.ExportPdf) }, modifier = Modifier.fillMaxWidth()) {
                    Text("خروجی PDF گزارشات")
                }
            }

            item {
                Text("گزارش روزانه رستوران", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                ReservationSummary(state.reservations, state.rooms, state.foods)
            }

            item {
                Text("مدیریت اتاق‌ها", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            items(state.rooms, key = { it.roomNumber }) { room ->
                RoomAdminCard(room) { checkIn, checkOut, checkInMillis, checkOutMillis, guestCount ->
                    viewModel.onIntent(AdminIntent.UpdateRoomStay(room.roomNumber, checkIn, checkOut, checkInMillis, checkOutMillis, guestCount))
                }
            }
            
            item {
                if (state.error != null) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
                }
                Spacer(Modifier.height(32.dp))
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
fun ReservationSummary(reservations: List<FoodReservation>, rooms: List<Room>, foods: List<FoodItem>) {
    val roomMap = rooms.associateBy { it.roomNumber }
    val foodMap = foods.associateBy { it.id }
    
    val summaryByDate = reservations.groupBy { it.date }.toList().sortedBy { it.first }
    
    if (summaryByDate.isEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text("رزروی ثبت نشده است", modifier = Modifier.padding(16.dp))
        }
        return
    }

    for ((date, dailyResList) in summaryByDate) {
        // Filter out empty reservations
        val validRes = dailyResList.filter { res ->
            res.guestMealSelections.any { it.lunchFoodId != null || it.dinnerFoodId != null }
        }
        
        if (validRes.isEmpty()) continue

        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("تاریخ: $date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                validRes.forEach { res ->
                    val room = roomMap[res.roomNumber]
                    Text("اتاق ${res.roomNumber} (${room?.guestName ?: "نامعلوم"}):", style = MaterialTheme.typography.labelLarge)
                    
                    res.guestMealSelections.forEach { selection ->
                        if (selection.lunchFoodId != null || selection.dinnerFoodId != null) {
                            val lunch = foodMap[selection.lunchFoodId]?.name ?: "-"
                            val dinner = foodMap[selection.dinnerFoodId]?.name ?: "-"
                            Text("  - مهمان ${selection.guestIndex + 1}: ناهار: $lunch، شام: $dinner", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Totals for the day
                val allLunch = validRes.flatMap { it.guestMealSelections }.mapNotNull { it.lunchFoodId }
                val allDinner = validRes.flatMap { it.guestMealSelections }.mapNotNull { it.dinnerFoodId }
                
                if (allLunch.isNotEmpty()) {
                    Text("مجموع ناهار: ${allLunch.size} پرس", fontWeight = FontWeight.SemiBold)
                    allLunch.groupBy { it }.forEach { (id, list) ->
                        Text("  • ${foodMap[id]?.name ?: id}: ${list.size}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                
                if (allDinner.isNotEmpty()) {
                    Text("مجموع شام: ${allDinner.size} پرس", fontWeight = FontWeight.SemiBold)
                    allDinner.groupBy { it }.forEach { (id, list) ->
                        Text("  • ${foodMap[id]?.name ?: id}: ${list.size}", style = MaterialTheme.typography.bodyMedium)
                    }
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
                        val formattedDate = DateUtils.convertMillisToJalaliString(millis)
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
    var checkIn by remember(room) { mutableStateOf(room.checkInDate) }
    var checkOut by remember(room) { mutableStateOf(room.checkOutDate) }
    var checkInMillis by remember(room) { mutableStateOf(room.checkInEpochMillis) }
    var checkOutMillis by remember(room) { mutableStateOf(room.checkOutEpochMillis) }
    var guestCount by remember(room) { mutableStateOf(room.guestCount.toString()) }

    Card(modifier = Modifier.fillMaxWidth()) {
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
