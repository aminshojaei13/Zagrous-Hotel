package com.braveboy.hotelzagrous.app.shared.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.normalizeDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.state.collectAsState()
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showReservationSummary by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = Color.Black
                ),
                title = {
                    Text(
                        modifier = Modifier.basicMarquee().padding(horizontal = 16.dp),
                        text = "هتل زاگرس",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    TextButton(
                        onClick = { showClearDataDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("حذف کل داده‌ها")
                    }
                    Button(
                        onClick = { showAddRoomDialog = true },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text("افزودن اتاق")
                    }
                }
            )
        }
    ) { paddingValues ->
        Row(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
                    .background(Color.Gray.copy(alpha = 0.5F))
                    .padding(all = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        showReservationSummary = false
                    }
                ) {
                    Text(
                        "مشاهده و مدیریت اتاق‌ها",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        showReservationSummary = true
                    }
                ) {
                    Text(
                        "گزارش روزانه رستوران",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        //showReservationSummary = false
                    }
                ) {
                    Text(
                        "خروجی گزارش رستوران",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(4F)
                    .background(Color.Gray.copy(alpha = 0.3F))
                    .padding(horizontal = 24.dp),
            ) {
                when {
                    state.error != null -> {
                        Text(
                            state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    showReservationSummary && state.reservations.isNotEmpty() -> {
                        Text(
                            modifier = Modifier.padding(all = 24.dp),
                            text = "گزارش روزانه رستوران",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        ReservationSummary(state.reservations, state.rooms, state.foods)
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            item {
                                Text(
                                    modifier = Modifier.padding(all = 24.dp),
                                    text = "مدیریت اتاق‌ها",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            items(state.rooms, key = { it.roomNumber }) { room ->
                                RoomAdminCard(room) { checkIn, checkOut, checkInMillis, checkOutMillis, guestCount ->
                                    viewModel.onIntent(
                                        AdminIntent.UpdateRoomStay(
                                            room.roomNumber,
                                            checkIn,
                                            checkOut,
                                            checkInMillis,
                                            checkOutMillis,
                                            guestCount
                                        )
                                    )
                                }
                            }
                        }
                    }
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

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("حذف تمام اطلاعات") },
            text = { Text("آیا از حذف تمامی اطلاعات اتاق‌ها و رزروها اطمینان دارید؟ این عمل غیرقابل بازگشت است.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onIntent(AdminIntent.ClearAllData)
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("بله، حذف شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
fun ReservationSummary(
    reservations: List<FoodReservation>,
    rooms: List<Room>,
    foods: List<FoodItem>
) {
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
        val validRes = dailyResList.filter { res ->
            res.guestMealSelections.any { it.lunchFoodId != null || it.dinnerFoodId != null }
        }

        if (validRes.isEmpty()) continue

        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "تاریخ: $date",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                validRes.forEach { res ->
                    val room = roomMap[res.roomNumber]
                    Text(
                        "اتاق ${res.roomNumber} (${room?.guestName ?: "نامعلوم"}):",
                        style = MaterialTheme.typography.labelLarge
                    )

                    res.guestMealSelections.forEach { selection ->
                        if (selection.lunchFoodId != null || selection.dinnerFoodId != null) {
                            val lunch = foodMap[selection.lunchFoodId]?.name ?: "-"
                            val dinner = foodMap[selection.dinnerFoodId]?.name ?: "-"
                            Text(
                                "  - مهمان ${selection.guestIndex + 1}: ناهار: $lunch، شام: $dinner",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                val allLunch =
                    validRes.flatMap { it.guestMealSelections }.mapNotNull { it.lunchFoodId }
                val allDinner =
                    validRes.flatMap { it.guestMealSelections }.mapNotNull { it.dinnerFoodId }

                if (allLunch.isNotEmpty()) {
                    Text("مجموع ناهار: ${allLunch.size} پرس", fontWeight = FontWeight.SemiBold)
                    allLunch.groupBy { it }.forEach { (id, list) ->
                        Text(
                            "  • ${foodMap[id]?.name ?: id}: ${list.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                if (allDinner.isNotEmpty()) {
                    Text("مجموع شام: ${allDinner.size} پرس", fontWeight = FontWeight.SemiBold)
                    allDinner.groupBy { it }.forEach { (id, list) ->
                        Text(
                            "  • ${foodMap[id]?.name ?: id}: ${list.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
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

    Box(
        modifier = modifier.clip(MaterialTheme.shapes.large)
    ) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = "$label: $value"
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
    var guestCount by remember { mutableStateOf(1) }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var checkInMillis by remember { mutableStateOf(0L) }
    var checkOutMillis by remember { mutableStateOf(0L) }
    var expanded by remember { mutableStateOf(false) }

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

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "تعداد نفرات: $guestCount",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        (1..7).forEach { number ->
                            DropdownMenuItem(
                                text = { Text(number.toString()) },
                                onClick = {
                                    guestCount = number
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                DatePickerField(
                    label = "تاریخ ورود",
                    value = checkIn,
                    onDateSelected = { date, millis ->
                        checkIn = date
                        checkInMillis = millis
                    },
                )

                DatePickerField(
                    label = "تاریخ خروج",
                    value = checkOut,
                    onDateSelected = { date, millis ->
                        checkOut = date
                        checkOutMillis = millis
                    },
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (roomNumber.isNotBlank() && guestName.isNotBlank()) {
                    onConfirm(
                        Room(
                            roomNumber.normalizeDigits(),
                            guestName,
                            guestCount,
                            checkIn,
                            checkOut,
                            checkInMillis,
                            checkOutMillis
                        )
                    )
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
    var guestCount by remember(room) { mutableStateOf(room.guestCount) }
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = room.roomNumber + " . ",
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            Text(text = room.guestName, fontWeight = FontWeight.Bold)

            Spacer(Modifier.weight(1f))

            Box {
                Text(
                    text = "تعداد نفرات: $guestCount",
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .clickable { expanded = true }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                DropdownMenu(
                    modifier = Modifier.clip(MaterialTheme.shapes.large),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    (1..7).forEach { number ->
                        DropdownMenuItem(
                            text = { Text(number.toString()) },
                            onClick = {
                                guestCount = number
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            DatePickerField(
                label = "ورود",
                value = checkIn,
                onDateSelected = { date, millis ->
                    checkIn = date
                    checkInMillis = millis
                }
            )

            Spacer(Modifier.weight(1f))

            DatePickerField(
                label = "خروج",
                value = checkOut,
                onDateSelected = { date, millis ->
                    checkOut = date
                    checkOutMillis = millis
                }
            )

            Spacer(Modifier.weight(1f))

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    onUpdate(checkIn, checkOut, checkInMillis, checkOutMillis, guestCount)
                }
            ) {
                Text("به‌روزرسانی")
            }
        }
    }
}
