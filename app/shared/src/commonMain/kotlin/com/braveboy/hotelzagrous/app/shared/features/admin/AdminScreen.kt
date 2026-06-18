package com.braveboy.hotelzagrous.app.shared.features.admin

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodType
import com.braveboy.hotelzagrous.core.GuestMealSelection
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.normalizeDigits
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.state.collectAsState()
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showReservationSummary by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Surface(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 40.dp, start = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Hotel,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "هتل زاگرس",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    "منوی مدیریت",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                )

                NavigationItem(
                    label = "مدیریت اتاق‌ها",
                    icon = Icons.Default.Bed,
                    selected = !showReservationSummary,
                    onClick = { showReservationSummary = false }
                )

                Spacer(Modifier.height(8.dp))

                NavigationItem(
                    label = "گزارش رزرو غذا",
                    icon = Icons.Default.Restaurant,
                    selected = showReservationSummary,
                    onClick = { showReservationSummary = true }
                )

                Spacer(Modifier.weight(1f))

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                NavigationItem(
                    label = "حذف کل داده‌ها",
                    icon = Icons.Default.DeleteForever,
                    selected = false,
                    onClick = { showClearDataDialog = true },
                    contentColor = MaterialTheme.colorScheme.error
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(32.dp)
        ) {
            // Page Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (showReservationSummary) "گزارش جامع رزروها" else "داشبورد مدیریت اتاق‌ها",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (showReservationSummary) "مشاهده و مدیریت برنامه غذایی مهمانان" else "لیست و وضعیت اقامت تمام اتاق‌ها",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!showReservationSummary) {
                    Button(
                        onClick = { showAddRoomDialog = true },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("افزودن اتاق جدید", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // Main Content Card
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.error != null -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    state.error!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        state.isLoading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    strokeWidth = 3.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        showReservationSummary -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(32.dp),
                                verticalArrangement = Arrangement.spacedBy(32.dp)
                            ) {
                                item { TodayReservationDetail(state) }
                                item { TodayReservationDetailByRoom(state, viewModel::onIntent) }
                                item {
                                    Column {
                                        Text(
                                            text = "گزارش روزهای آتی",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 20.dp)
                                        )
                                        ReservationSummary(
                                            state.reservations,
                                            state.rooms,
                                            state.foods,
                                            viewModel::onIntent
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "لیست اتاق‌های ثبت شده",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            "${state.rooms.size} اتاق",
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 4.dp
                                            ),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(state.rooms, key = { it.roomNumber }) { room ->
                                        RoomAdminCard(
                                            room = room,
                                            availableFoods = state.foods,
                                            reservations = state.reservations,
                                            onUpdate = { checkIn, checkOut, checkInMillis, checkOutMillis, guestCount ->
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
                                            },
                                            onFoodChange = { date, guestIndex, foodId, isLunch ->
                                                viewModel.onIntent(
                                                    AdminIntent.ChangeFood(
                                                        room.roomNumber,
                                                        date,
                                                        guestIndex,
                                                        foodId,
                                                        isLunch
                                                    )
                                                )
                                            }
                                        )
                                    }
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
            title = { Text("پاکسازی کامل پایگاه داده") },
            text = { Text("با تایید این عملیات، تمامی اطلاعات مربوط به اتاق‌ها، مهمانان و تاریخچه‌ی رزروها برای همیشه حذف خواهد شد. آیا مطمئن هستید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onIntent(AdminIntent.ClearAllData)
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("بله، کاملاً پاک شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("انصراف")
                }
            },
            shape = MaterialTheme.shapes.large
        )
    }
}

@Composable
fun NavigationItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    contentColor: Color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
) {
    val backgroundColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
            if (selected) {
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}

@Composable
fun RoomAdminCard(
    room: Room,
    availableFoods: List<FoodItem>,
    reservations: List<FoodReservation>,
    onUpdate: (String, String, Long, Long, Int) -> Unit,
    onFoodChange: (String, Int, String?, Boolean) -> Unit
) {
    var checkIn by remember(room) { mutableStateOf(room.checkInDate) }
    var checkOut by remember(room) { mutableStateOf(room.checkOutDate) }
    var checkInMillis by remember(room) { mutableStateOf(room.checkInEpochMillis) }
    var checkOutMillis by remember(room) { mutableStateOf(room.checkOutEpochMillis) }
    var guestCount by remember(room) { mutableStateOf(room.guestCount) }
    var expanded by remember { mutableStateOf(false) }
    var showFoodDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room Number
            Column(modifier = Modifier.width(100.dp)) {
                Text(
                    "شماره اتاق",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    room.roomNumber,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Guest Name
            Column(modifier = Modifier.weight(1.5f)) {
                Text(
                    "نام مهمان اصلی",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    room.guestName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Guest Count
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "ظرفیت (نفر)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Box {
                    Surface(
                        onClick = { expanded = true },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$guestCount نفر",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        (1..10).forEach { number ->
                            DropdownMenuItem(
                                text = { Text("$number نفر") },
                                onClick = { guestCount = number; expanded = false }
                            )
                        }
                    }
                }
            }

            // Stay Period
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    "بازه اقامت",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DatePickerFieldSmall(value = checkIn) { date, millis ->
                        checkIn = date; checkInMillis = millis
                    }
                    Text(
                        " تا ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    DatePickerFieldSmall(value = checkOut) { date, millis ->
                        checkOut = date; checkOutMillis = millis
                    }
                }
            }

            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showFoodDialog = true },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Restaurant, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "مدیریت غذا",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        onUpdate(
                            checkIn,
                            checkOut,
                            checkInMillis,
                            checkOutMillis,
                            guestCount
                        )
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text(
                        "به‌روزرسانی",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showFoodDialog) {
        RoomFoodReservationsDialog(
            room = room,
            availableFoods = availableFoods,
            reservations = reservations.filter { it.roomNumber == room.roomNumber },
            onDismiss = { showFoodDialog = false },
            onFoodChange = onFoodChange
        )
    }
}

@Composable
fun RoomFoodReservationsDialog(
    room: Room,
    availableFoods: List<FoodItem>,
    reservations: List<FoodReservation>,
    onDismiss: () -> Unit,
    onFoodChange: (String, Int, String?, Boolean) -> Unit
) {
    val stayDays = remember(room) {
        if (room.checkInEpochMillis != 0L && room.checkOutEpochMillis != 0L) {
            val days = mutableListOf<String>()
            var currentMillis = room.checkInEpochMillis
            while (currentMillis <= room.checkOutEpochMillis) {
                days.add(DateUtils.convertMillisToJalaliString(currentMillis))
                currentMillis += 24 * 60 * 60 * 1000L
            }
            days
        } else emptyList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "مدیریت غذای اتاق ${room.roomNumber} - ${room.guestName}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.width(600.dp).height(500.dp)) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(stayDays) { date ->
                        val reservation = reservations.find { it.date == date }
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    date,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                repeat(room.guestCount) { index ->
                                    val selection =
                                        reservation?.guestMealSelections?.find { it.guestIndex == index }
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            "مهمان ${index + 1}:",
                                            modifier = Modifier.width(80.dp),
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        // Lunch
                                        AdminFoodSelectionItem(
                                            label = "ناهار",
                                            foods = availableFoods.filter { it.type == FoodType.LUNCH },
                                            selectedId = selection?.lunchFoodId,
                                            modifier = Modifier.weight(1f),
                                            onSelect = { onFoodChange(date, index, it, true) }
                                        )

                                        // Dinner
                                        AdminFoodSelectionItem(
                                            label = "شام",
                                            foods = availableFoods.filter { it.type == FoodType.DINNER },
                                            selectedId = selection?.dinnerFoodId,
                                            modifier = Modifier.weight(1f),
                                            onSelect = { onFoodChange(date, index, it, false) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("بستن") }
        },
        shape = MaterialTheme.shapes.large
    )
}

@Composable
fun AdminFoodSelectionItem(
    modifier: Modifier = Modifier,
    label: String,
    foods: List<FoodItem>,
    selectedId: String?,
    enabled: Boolean = true,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedFood = foods.find { it.id == selectedId }

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = if (selectedFood != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            ),
            border = BorderStroke(
                1.dp,
                if (selectedFood != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            ),
            enabled = enabled
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = selectedFood?.name ?: label,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    fontWeight = if (selectedFood != null) FontWeight.Bold else FontWeight.Normal,
                    textAlign = if (enabled) TextAlign.Start else TextAlign.Center
                )
                if (enabled){
                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp))
                }
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("بدون انتخاب", style = MaterialTheme.typography.bodySmall) },
                onClick = { onSelect(null); expanded = false },
                leadingIcon = { Icon(Icons.Default.Block, null, modifier = Modifier.size(16.dp)) }
            )
            foods.forEach { food ->
                DropdownMenuItem(
                    text = { Text(food.name, style = MaterialTheme.typography.bodySmall) },
                    onClick = { onSelect(food.id); expanded = false },
                    trailingIcon = {
                        if (food.id == selectedId) Icon(
                            Icons.Default.Check,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerFieldSmall(value: String, onDateSelected: (String, Long) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Surface(
        onClick = { showDatePicker = true },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = value.ifBlank { "انتخاب" },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(
                            DateUtils.convertMillisToJalaliString(
                                it
                            ), it
                        )
                    }
                    showDatePicker = false
                }) { Text("تایید", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("انصراف") }
            },
            shape = MaterialTheme.shapes.large
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ReservationSummary(
    reservations: List<FoodReservation>,
    rooms: List<Room>,
    foods: List<FoodItem>,
    onIntent: (AdminIntent) -> Unit
) {
    val roomMap = rooms.associateBy { it.roomNumber }
    val foodMap = foods.associateBy { it.id }
    val todayMillis = Clock.System.now().toEpochMilliseconds()
    val today = DateUtils.convertMillisToJalaliString(todayMillis)

    val summaryByDate = reservations
        .filter { it.date > today }
        .groupBy { it.date }
        .toList()
        .sortedBy { it.first }

    if (summaryByDate.isEmpty()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                "در حال حاضر هیچ رزروی برای تاریخ‌های آینده ثبت نشده است.",
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
        return
    }

    summaryByDate.forEach { (date, dailyResList) ->
        val validRes =
            dailyResList.filter { res -> res.guestMealSelections.any { it.lunchFoodId != null || it.dinnerFoodId != null } }
        if (validRes.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            date,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )

                    validRes.forEach { res ->
                        val room = roomMap[res.roomNumber]
                        Column(modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(
                                "اتاق ${res.roomNumber} — ${room?.guestName ?: "نامعلوم"}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            res.guestMealSelections.forEach { selection ->
                                Row(
                                    modifier = Modifier.padding(top = 4.dp, start = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "مهمان ${selection.guestIndex + 1} :",
                                        modifier = Modifier.width(80.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    AdminFoodSelectionItem(
                                        label = "ناهار",
                                        foods = foods.filter { it.type == FoodType.LUNCH },
                                        selectedId = selection.lunchFoodId,
                                        modifier = Modifier.width(150.dp),
                                        onSelect = {
                                            onIntent(
                                                AdminIntent.ChangeFood(
                                                    res.roomNumber,
                                                    date,
                                                    selection.guestIndex,
                                                    it,
                                                    true
                                                )
                                            )
                                        }
                                    )

                                    AdminFoodSelectionItem(
                                        label = "شام",
                                        foods = foods.filter { it.type == FoodType.DINNER },
                                        selectedId = selection.dinnerFoodId,
                                        modifier = Modifier.width(150.dp),
                                        onSelect = {
                                            onIntent(
                                                AdminIntent.ChangeFood(
                                                    res.roomNumber,
                                                    date,
                                                    selection.guestIndex,
                                                    it,
                                                    false
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodayReservationDetail(state: AdminState) {
    val todayMillis = Clock.System.now().toEpochMilliseconds()
    val today = DateUtils.convertMillisToJalaliString(todayMillis)
    val todayResList = state.reservations.filter { it.date == today }
    val foodMap = state.foods.associateBy { it.id }

    val allLunch = todayResList.flatMap { it.guestMealSelections }.mapNotNull { it.lunchFoodId }
    val allDinner = todayResList.flatMap { it.guestMealSelections }.mapNotNull { it.dinnerFoodId }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Today,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    "خلاصه کل سفارشات امروز ($today)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MealSummaryBox("وعده ناهار", allLunch, foodMap, Modifier.weight(1f))
                MealSummaryBox("وعده شام", allDinner, foodMap, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MealSummaryBox(
    label: String,
    foodIds: List<String>,
    foodMap: Map<String, FoodItem>,
    modifier: Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            if (foodIds.isEmpty()) {
                Text(
                    "سفارشی ثبت نشده است",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                foodIds.groupBy { it }.forEach { (id, list) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(foodMap[id]?.name ?: id, style = MaterialTheme.typography.bodyMedium)
                        Text("${list.size} پرس", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                HorizontalDivider(
                    Modifier.padding(vertical = 12.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("مجموع کل", fontWeight = FontWeight.Bold)
                    Text(
                        "${foodIds.size} پرس",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TodayReservationDetailByRoom(
    state: AdminState,
    onIntent: (AdminIntent) -> Unit
) {
    val todayMillis = Clock.System.now().toEpochMilliseconds()
    val today = DateUtils.convertMillisToJalaliString(todayMillis)
    val todayResList = state.reservations.filter { it.date == today }
    val foodMap = state.foods.associateBy { it.id }

    Column(Modifier.padding(top = 16.dp)) {
        Text(
            "مدیریت تحویل غذای امروز",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "لیست اتاق‌ها و وضعیت توزیع وعده‌های غذایی",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (todayResList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "هیچ سفارشی برای امروز ثبت نشده است.",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
        }

        todayResList.forEach { res ->
            RoomDeliveryCard(
                res = res,
                availableFoods = state.foods,
                foodMap = foodMap,
                onIntent = onIntent,
                today = today
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun RoomDeliveryCard(
    res: FoodReservation,
    availableFoods: List<FoodItem>,
    foodMap: Map<String, FoodItem>,
    onIntent: (AdminIntent) -> Unit,
    today: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column {
            // Room Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Hotel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "اتاق ${res.roomNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Guest Rows
            res.guestMealSelections.forEachIndexed { index, selection ->
                GuestDeliveryRow(
                    selection = selection,
                    availableFoods = availableFoods,
                    foodMap = foodMap,
                    isLast = index == res.guestMealSelections.size - 1,
                    onLunchDeliver = {
                        onIntent(
                            AdminIntent.MarkLunchDelivered(
                                res.roomNumber,
                                selection.guestIndex,
                                today
                            )
                        )
                    },
                    onDinnerDeliver = {
                        onIntent(
                            AdminIntent.MarkDinnerDelivered(
                                res.roomNumber,
                                selection.guestIndex,
                                today
                            )
                        )
                    },
                    onFoodChange = { foodId, isLunch ->
                        onIntent(
                            AdminIntent.ChangeFood(
                                res.roomNumber,
                                today,
                                selection.guestIndex,
                                foodId,
                                isLunch
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun GuestDeliveryRow(
    selection: GuestMealSelection,
    availableFoods: List<FoodItem>,
    foodMap: Map<String, FoodItem>,
    isLast: Boolean,
    onLunchDeliver: () -> Unit,
    onDinnerDeliver: () -> Unit,
    onFoodChange: (String?, Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.5f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "مهمان ${selection.guestIndex + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.weight(1.5f),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lunch Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminFoodSelectionItem(
                        label = "انتخاب ناهار",
                        foods = availableFoods.filter { it.type == FoodType.LUNCH },
                        selectedId = selection.lunchFoodId,
                        modifier = Modifier.width(140.dp),
                        enabled = false,
                        onSelect = { onFoodChange(it, true) }
                    )
                    if (selection.lunchFoodId != null) {
                        DeliveryChip(
                            isDelivered = selection.lunchDelivered,
                            onClick = onLunchDeliver,
                            deliverLabel = "تحویل",
                            deliveredLabel = "تحویل شد"
                        )
                    }
                }

                // Dinner Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminFoodSelectionItem(
                        label = "انتخاب شام",
                        foods = availableFoods.filter { it.type == FoodType.DINNER },
                        selectedId = selection.dinnerFoodId,
                        modifier = Modifier.width(140.dp),
                        enabled = false,
                        onSelect = { onFoodChange(it, false) }
                    )
                    if (selection.dinnerFoodId != null) {
                        DeliveryChip(
                            isDelivered = selection.dinnerDelivered,
                            onClick = onDinnerDeliver,
                            deliverLabel = "تحویل",
                            deliveredLabel = "تحویل شد"
                        )
                    }
                }
            }
        }
        if (!isLast) {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun DeliveryChip(
    isDelivered: Boolean,
    onClick: () -> Unit,
    deliverLabel: String,
    deliveredLabel: String
) {
    Button(
        onClick = onClick,
        enabled = !isDelivered,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        modifier = Modifier.height(32.dp)
    ) {
        if (isDelivered) {
            Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(
            if (isDelivered) deliveredLabel else deliverLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
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
        title = { Text("ثبت اتاق و مهمان جدید", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    label = { Text("شماره اتاق") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    leadingIcon = { Icon(Icons.Default.Numbers, null) }
                )
                OutlinedTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    label = { Text("نام و نام خانوادگی مهمان") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )

                Column {
                    Text(
                        "تعداد نفرات همراه",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    "تعداد: $guestCount نفر",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            (1..10).forEach { number ->
                                DropdownMenuItem(
                                    text = { Text("$number نفر") },
                                    onClick = { guestCount = number; expanded = false })
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DatePickerField(
                        label = "تاریخ ورود",
                        value = checkIn,
                        modifier = Modifier.weight(1f)
                    ) { date, millis -> checkIn = date; checkInMillis = millis }
                    DatePickerField(
                        label = "تاریخ خروج",
                        value = checkOut,
                        modifier = Modifier.weight(1f)
                    ) { date, millis -> checkOut = date; checkOutMillis = millis }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
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
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("تایید و ثبت در سیستم", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        },
        shape = MaterialTheme.shapes.large
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onDateSelected: (String, Long) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Surface(
        onClick = { showDatePicker = true },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    value.ifBlank { "انتخاب" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(
                            DateUtils.convertMillisToJalaliString(
                                it
                            ), it
                        )
                    }
                    showDatePicker = false
                }) { Text("تایید", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("انصراف") }
            },
            shape = MaterialTheme.shapes.large
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
