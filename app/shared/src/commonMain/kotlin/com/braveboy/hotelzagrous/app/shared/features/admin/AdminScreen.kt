package com.braveboy.hotelzagrous.app.shared.features.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braveboy.hotelzagrous.app.shared.features.PersianDatePickerDialog
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.DayType
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodType
import com.braveboy.hotelzagrous.core.GuestMealSelection
import com.braveboy.hotelzagrous.core.MenuConfig
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.normalizeDigits
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.state.collectAsState()
    var currentTab by remember { mutableStateOf("rooms") }
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    var isReady by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        if (Clock.System.now().toEpochMilliseconds() > 1788208200000L) {
            isReady = false
        }
    }

    if (!isReady) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("خطای لایسنس") },
            text = { Text("زمان استفاده از نسخه دمو به پایان رسیده است. لطفا با برنامه نویس تماس بگیرید.") },
            confirmButton = { }
        )
    }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Surface(
            modifier = Modifier
                .width(240.dp)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp, start = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Hotel,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "هتل زاگرس",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    "منوی مدیریت",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )

                NavigationItem(
                    label = "مدیریت اتاق‌ها",
                    icon = Icons.Default.Bed,
                    selected = currentTab == "rooms",
                    onClick = { currentTab = "rooms" })
                Spacer(Modifier.height(4.dp))
                NavigationItem(
                    label = "گزارش رزرو غذا",
                    icon = Icons.Default.Restaurant,
                    selected = currentTab == "reservations",
                    onClick = { currentTab = "reservations" })
                Spacer(Modifier.height(4.dp))
                NavigationItem(
                    label = "گزارش و چاپ روزانه",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    selected = currentTab == "daily_report",
                    onClick = { currentTab = "daily_report" })
                Spacer(Modifier.height(4.dp))
                NavigationItem(
                    label = "مدیریت منوی غذا",
                    icon = Icons.Default.RestaurantMenu,
                    selected = currentTab == "menu",
                    onClick = { currentTab = "menu" })
                Spacer(Modifier.height(4.dp))
                NavigationItem(
                    label = "تاریخچه",
                    icon = Icons.Default.History,
                    selected = currentTab == "history",
                    onClick = { currentTab = "history" })

                Spacer(Modifier.weight(1f))
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                NavigationItem(
                    label = "بروزرسانی داده‌ها",
                    icon = Icons.Default.Refresh,
                    selected = false,
                    onClick = { viewModel.onIntent(AdminIntent.LoadData) },
                    contentColor = MaterialTheme.colorScheme.error
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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val title = when (currentTab) {
                        "rooms" -> "مدیریت اتاق‌ها"
                        "menu" -> "مدیریت منو غذا"
                        "daily_report" -> "مشاهده و چاپ غذا"
                        "history" -> "تاریخچه اتاق‌های ثبت شده"
                        else -> "گزارش رزرو غذا"
                    }
                    Text(
                        title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (currentTab == "rooms") {
                    Button(
                        shape = MaterialTheme.shapes.small,
                        onClick = { showAddRoomDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("افزودن اتاق", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (state.isLoading) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                } else if (state.error != null) {
                    Box(
                        Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                state.error ?: "خطای ناشناخته",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.onIntent(AdminIntent.LoadData) }) {
                                Text("تلاش مجدد")
                            }
                        }
                    }
                } else {
                    when (currentTab) {
                        "rooms" -> RoomManagementContent(state, viewModel)
                        "menu" -> MenuManagementContent(state, viewModel)
                        "reservations" -> ReservationSummaryContent(state, viewModel)
                        "daily_report" -> DailyDetailedReportContent(state, viewModel)
                        "history" -> RoomHistoryContent(state)
                    }
                }
            }
        }
    }

    if (showAddRoomDialog) {
        AddRoomDialog(
            existingRooms = state.rooms,
            onDismiss = { showAddRoomDialog = false },
            onConfirm = { viewModel.onIntent(AdminIntent.AddRoom(it)); showAddRoomDialog = false })
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("پاکسازی کامل پایگاه داده") },
            text = { Text("با تایید این عملیات، تمامی اطلاعات مربوط به اتاق‌ها، مهمانان و تاریخچه‌ی رزروها برای همیشه حذف خواهد شد. آیا مطمئن هستید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onIntent(AdminIntent.ClearAllData); showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("بله، کاملاً پاک شود") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showClearDataDialog = false
                }) { Text("انصراف") }
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
    contentColor: Color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(40.dp),
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.primary.copy(0.1f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                label,
                color = contentColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (selected) {
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier.size(4.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomAdminCard(
    room: Room,
    availableFoods: List<FoodItem>,
    reservations: List<FoodReservation>,
    onUpdate: (String, String, String, String, String, Long, Long, Int, Boolean, Int) -> Unit,
    onDelete: () -> Unit,
    onFoodChange: (String, Int, String?, FoodType) -> Unit,
    onBreakfastChange: (String, Int) -> Unit
) {
    var roomNumber by remember(room) { mutableStateOf(room.roomNumber) }
    var guestName by remember(room) { mutableStateOf(room.guestName) }
    var identificationId by remember(room) { mutableStateOf(room.identificationId) }
    var checkIn by remember(room) { mutableStateOf(room.checkInDate) }
    var checkOut by remember(room) { mutableStateOf(room.checkOutDate) }
    var checkInMillis by remember(room) { mutableLongStateOf(room.checkInEpochMillis) }
    var checkOutMillis by remember(room) { mutableLongStateOf(room.checkOutEpochMillis) }
    var guestCount by remember(room) { mutableIntStateOf(room.guestCount) }
    var hasBreakfast by remember(room) { mutableStateOf(room.hasBreakfast) }
    var breakfastCount by remember(room) { mutableIntStateOf(room.breakfastCount) }
    var expandedGuestCount by remember { mutableStateOf(false) }
    var showFoodDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var changeRoomNumber by remember { mutableStateOf(false) }
    var changeName by remember { mutableStateOf(false) }
    var changeId by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room
            if (changeRoomNumber) {
                AlertDialog(
                    onDismissRequest = { changeRoomNumber = false },
                    title = { Text("شماره اتاق جدید") },
                    text = {
                        OutlinedTextField(
                            value = roomNumber,
                            onValueChange = { roomNumber = it.normalizeDigits() },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            shape = MaterialTheme.shapes.small,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onUpdate(
                                    roomNumber,
                                    guestName,
                                    identificationId,
                                    checkIn,
                                    checkOut,
                                    checkInMillis,
                                    checkOutMillis,
                                    guestCount,
                                    hasBreakfast,
                                    breakfastCount
                                )
                                changeRoomNumber = false
                            }
                        ) { Text("تایید") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            changeRoomNumber = false
                        }) { Text("انصراف") }
                    },
                    shape = MaterialTheme.shapes.large
                )
            }
            Column(modifier = Modifier.width(55.dp)) {
                Text(
                    text = "اتاق",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    modifier = Modifier.clickable { changeRoomNumber = true },
                    text = room.roomNumber,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (changeName) {
                AlertDialog(
                    onDismissRequest = { changeName = false },
                    title = { Text("نام جدید") },
                    text = {
                        OutlinedTextField(
                            value = guestName,
                            onValueChange = { guestName = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            shape = MaterialTheme.shapes.small,
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onUpdate(
                                    roomNumber,
                                    guestName,
                                    identificationId,
                                    checkIn,
                                    checkOut,
                                    checkInMillis,
                                    checkOutMillis,
                                    guestCount,
                                    hasBreakfast,
                                    breakfastCount
                                )
                                changeName = false
                            }
                        ) { Text("تایید") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            changeName = false
                        }) { Text("انصراف") }
                    },
                    shape = MaterialTheme.shapes.large
                )
            }
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "نام مهمان",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    modifier = Modifier.clickable { changeName = true },
                    text = room.guestName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (changeId) {
                AlertDialog(
                    onDismissRequest = { changeId = false },
                    title = { Text("تغییر شناسه") },
                    text = {
                        OutlinedTextField(
                            value = identificationId,
                            onValueChange = { identificationId = it.normalizeDigits() },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            shape = MaterialTheme.shapes.small,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onUpdate(
                                    roomNumber,
                                    guestName,
                                    identificationId,
                                    checkIn,
                                    checkOut,
                                    checkInMillis,
                                    checkOutMillis,
                                    guestCount,
                                    hasBreakfast,
                                    breakfastCount
                                )
                                changeId = false
                            }
                        ) { Text("تایید") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            changeId = false
                        }) { Text("انصراف") }
                    },
                    shape = MaterialTheme.shapes.large
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "شناسایی",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    modifier = Modifier.clickable { changeId = true },
                    text = room.identificationId.ifBlank { "0" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            // Guest Count
            Column(modifier = Modifier.width(75.dp)) {
                Text(
                    "تعداد",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                Box {
                    Surface(
                        onClick = { expandedGuestCount = true },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$guestCount نفر",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = expandedGuestCount,
                        onDismissRequest = { expandedGuestCount = false }
                    )
                    {
                        (1..7).forEach { number ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "$number نفر",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    guestCount = number
                                    onUpdate(
                                        roomNumber,
                                        guestName,
                                        identificationId,
                                        checkIn,
                                        checkOut,
                                        checkInMillis,
                                        checkOutMillis,
                                        guestCount,
                                        hasBreakfast,
                                        breakfastCount
                                    )
                                    expandedGuestCount = false
                                }
                            )
                        }
                    }
                }
            }

            // Stay Period
            Column(modifier = Modifier.weight(1.4f)) {
                Text(
                    "بازه اقامت",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DatePickerFieldSmall(checkIn) { date, millis ->
                        checkIn = date
                        checkInMillis = millis
                        onUpdate(
                            roomNumber,
                            guestName,
                            identificationId,
                            checkIn,
                            checkOut,
                            checkInMillis,
                            checkOutMillis,
                            guestCount,
                            hasBreakfast,
                            breakfastCount
                        )
                    }
                    Text(
                        "-",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    DatePickerFieldSmall(checkOut) { date, millis ->
                        checkOut = date
                        checkOutMillis = millis
                        onUpdate(
                            roomNumber,
                            guestName,
                            identificationId,
                            checkIn,
                            checkOut,
                            checkInMillis,
                            checkOutMillis,
                            guestCount,
                            hasBreakfast,
                            breakfastCount
                        )
                    }
                }
            }

            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                IconButton(onClick = { showFoodDialog = true }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Restaurant,
                        null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
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
            onFoodChange = onFoodChange,
            onBreakfastChange = onBreakfastChange
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف اتاق ${room.roomNumber}") },
            text = { Text("آیا از حذف این اتاق اطمینان دارید؟") },
            confirmButton = {
                TextButton(
                    onClick = { onDelete(); showDeleteConfirm = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
fun RoomManagementContent(state: AdminState, viewModel: AdminViewModel) {
    val filteredRooms = remember(state.rooms) {
        state.rooms.filter {
            it.checkOutEpochMillis >= Clock.System.now().toEpochMilliseconds()
        }.sortedBy { it.roomNumber }
            .sortedBy {
                val num = it.roomNumber.normalizeDigits().filter { c -> c.isDigit() }
                if (num.length >= 2) num[1] else ' '
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "لیست اتاق‌های ثبت شده",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Text(
                    "${filteredRooms.size} اتاق",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filteredRooms.forEachIndexed { index, room ->
                if (index > 0) {
                    val prevRoom = filteredRooms[index - 1]
                    val currentNum = room.roomNumber.normalizeDigits().filter { it.isDigit() }
                    val prevNum = prevRoom.roomNumber.normalizeDigits().filter { it.isDigit() }
                    val currentMiddle = if (currentNum.length >= 2) currentNum[1] else null
                    val prevMiddle = if (prevNum.length >= 2) prevNum[1] else null

                    if (currentMiddle != prevMiddle) {
                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                thickness = 2.dp
                            )
                        }
                    }
                }

                item(key = room.id.ifBlank { "active_${room.roomNumber}_${room.checkInEpochMillis}" }) {
                    RoomAdminCard(
                        room = room,
                        availableFoods = state.foods,
                        reservations = state.reservations,
                        onUpdate = { rNum, name, idId, inD, outD, inM, outM, count, hasB, bCount ->
                            val effectiveId = room.id.ifBlank { room.roomNumber }
                            println("id s is $effectiveId")
                            viewModel.onIntent(
                                AdminIntent.UpdateRoomStay(
                                    id = effectiveId,
                                    roomNumber = rNum,
                                    guestName = name,
                                    identificationId = idId,
                                    checkIn = inD,
                                    checkOut = outD,
                                    checkInMillis = inM,
                                    checkOutMillis = outM,
                                    guestCount = count,
                                    hasBreakfast = hasB,
                                    breakfastCount = bCount
                                )
                            )
                        },
                        onFoodChange = { d, idx, fId, fType ->
                            viewModel.onIntent(
                                AdminIntent.ChangeFood(
                                    room.roomNumber,
                                    d,
                                    idx,
                                    fId,
                                    fType
                                )
                            )
                        },
                        onBreakfastChange = { d, count ->
                            viewModel.onIntent(
                                AdminIntent.ChangeBreakfastCount(
                                    room.roomNumber,
                                    d,
                                    count
                                )
                            )
                        },
                        onDelete = {
                            val effectiveId = room.id.ifBlank { room.roomNumber }
                            viewModel.onIntent(
                                AdminIntent.DeleteRoom(
                                    id = effectiveId
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReservationSummaryContent(state: AdminState, viewModel: AdminViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val expandedDates = remember { mutableStateMapOf<String, Boolean>() }
    var deliveryExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it.normalizeDigits() },
                modifier = Modifier.weight(1f),
                placeholder = { Text("جستجو بر اساس شماره اتاق یا نام مهمان...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            if (searchQuery.isEmpty()) {
                TextButton(
                    onClick = {
                        val today = DateUtils.convertMillisToJalaliString(Clock.System.now().toEpochMilliseconds())
                        val allFutureDates = state.reservations.filter { it.date > today }.map { it.date }.distinct()
                        val anyCollapsed = allFutureDates.any { expandedDates[it] != true }
                        allFutureDates.forEach { expandedDates[it] = anyCollapsed }
                    }
                ) {
                    val today = DateUtils.convertMillisToJalaliString(Clock.System.now().toEpochMilliseconds())
                    val allFutureDates = state.reservations.filter { it.date > today }.map { it.date }.distinct()
                    val anyCollapsed = allFutureDates.any { expandedDates[it] != true }
                    Text(if (anyCollapsed) "باز کردن همه" else "بستن همه")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (searchQuery.isEmpty()) {
                item { TodayReservationDetail(state = state) }
            }

            item {
                TodayReservationDetailByRoom(
                    state = state,
                    searchQuery = searchQuery,
                    isExpanded = deliveryExpanded,
                    onToggle = { deliveryExpanded = !deliveryExpanded },
                    onIntent = viewModel::onIntent
                )
            }

            val roomMap = state.rooms.associateBy { it.roomNumber }
            val todayMillis = Clock.System.now().toEpochMilliseconds()
            val today = DateUtils.convertMillisToJalaliString(todayMillis)
            val summaryByDate = state.reservations
                .filter { it.date > today }
                .groupBy { it.date }
                .toList()
                .sortedBy { it.first }

            if (summaryByDate.isEmpty() && searchQuery.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            "رزروی برای تاریخ‌های آینده ثبت نشده است.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                summaryByDate.forEach { (date, dailyResList) ->
                    val filteredRes = dailyResList.filter { res ->
                        val room = roomMap[res.roomNumber]
                        (res.guestMealSelections.any { it.lunchFoodId != null || it.dinnerFoodId != null } || res.breakfastCount > 0) &&
                                (searchQuery.isEmpty() || res.roomNumber.contains(searchQuery) || room?.guestName?.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) == true)
                    }

                    if (filteredRes.isNotEmpty()) {
                        item(key = "summary_$date") {
                            val isExpanded = expandedDates[date] ?: searchQuery.isNotEmpty()
                            DateReservationGroup(
                                date = date,
                                reservations = filteredRes,
                                roomMap = roomMap,
                                foods = state.foods,
                                isExpanded = isExpanded,
                                onToggle = { expandedDates[date] = !isExpanded },
                                onIntent = viewModel::onIntent
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementContent(state: AdminState, viewModel: AdminViewModel) {
    var selectedDayType by remember { mutableStateOf(DayType.EVEN) }
    var selectedFoodType by remember { mutableStateOf(FoodType.LUNCH) }
    var editingFood by remember { mutableStateOf<FoodItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DayType.entries.forEach {
                FilterChip(
                    selected = selectedDayType == it,
                    onClick = { selectedDayType = it },
                    label = {
                        Text(
                            when (it) {
                                DayType.EVEN -> "زوج"; DayType.ODD -> "فرد"; else -> "جمعه"
                            }, style = MaterialTheme.typography.labelMedium
                        )
                    })
            }
        }
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FoodType.entries.forEach {
                FilterChip(
                    selected = selectedFoodType == it,
                    onClick = { selectedFoodType = it },
                    label = {
                        Text(
                            if (it == FoodType.LUNCH) "ناهار" else "شام",
                            style = MaterialTheme.typography.labelMedium
                        )
                    })
            }
            Spacer(Modifier.weight(1f))
            val config =
                state.menuConfigs.find { it.dayType == selectedDayType && it.foodType == selectedFoodType }
            Text("فعال", style = MaterialTheme.typography.labelMedium)
            Switch(
                checked = config?.isEnabled ?: true,
                onCheckedChange = {
                    viewModel.onIntent(
                        AdminIntent.UpdateMenuConfig(
                            MenuConfig(
                                selectedDayType,
                                selectedFoodType,
                                it
                            )
                        )
                    )
                },
                modifier = Modifier.scale(0.55f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "لیست غذاها",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("افزودن", style = MaterialTheme.typography.labelMedium)
            }
        }

        val foods =
            state.foods.filter { it.dayType == selectedDayType && it.type == selectedFoodType }
                .sortedBy { it.displayOrder }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(foods, key = { it.id.ifBlank { "${it.name}_${it.type}_${it.dayType}" } }) { food ->
                Surface(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                food.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "ترتیب: ${food.displayOrder}",
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Checkbox(
                            checked = food.isActive,
                            onCheckedChange = {
                                viewModel.onIntent(
                                    AdminIntent.UpsertFood(
                                        food.copy(isActive = it)
                                    )
                                )
                            },
                            modifier = Modifier.scale(0.7f)
                        )
                        Text("فعال", style = MaterialTheme.typography.labelMedium, maxLines = 1)
                        Checkbox(
                            checked = food.isVisibleToUsers,
                            onCheckedChange = {
                                viewModel.onIntent(
                                    AdminIntent.UpsertFood(
                                        food.copy(isVisibleToUsers = it)
                                    )
                                )
                            },
                            modifier = Modifier.scale(0.7f)
                        )
                        Text("نمایش", style = MaterialTheme.typography.labelMedium, maxLines = 1)
                        IconButton(
                            onClick = { editingFood = food },
                            modifier = Modifier.size(28.dp)
                        ) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)) }
                        IconButton(
                            onClick = { viewModel.onIntent(AdminIntent.DeleteFood(food.id)) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) AddEditFoodDialog(
        null,
        selectedDayType,
        selectedFoodType,
        { showAddDialog = false }) {
        viewModel.onIntent(AdminIntent.UpsertFood(it)); showAddDialog = false
    }
    if (editingFood != null) AddEditFoodDialog(
        editingFood,
        selectedDayType,
        selectedFoodType,
        { editingFood = null }) {
        viewModel.onIntent(AdminIntent.UpsertFood(it)); editingFood = null
    }
}

@Composable
fun DailyDetailedReportContent(state: AdminState, viewModel: AdminViewModel) {
    val reportDate = state.selectedReportDate.ifBlank {
        remember {
            DateUtils.convertMillisToJalaliString(
                Clock.System.now().toEpochMilliseconds()
            )
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "گزارش روزانه",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "سفارشات به تفکیک اتاق",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = {
                        val today = DateUtils.convertMillisToJalaliString(Clock.System.now().toEpochMilliseconds())
                        viewModel.onIntent(AdminIntent.SelectReportDate(today))
                    }
                ) {
                    Icon(Icons.Default.Today, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }

                DatePickerFieldSmall(reportDate) { date, _ ->
                    viewModel.onIntent(
                        AdminIntent.SelectReportDate(
                            date
                        )
                    )
                }
                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = { viewModel.onIntent(AdminIntent.PrintDailyBreakfastReport(reportDate)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(16.dp)); Text(
                    " صبحانه",
                    style = MaterialTheme.typography.labelMedium
                )
                }
                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = { viewModel.onIntent(AdminIntent.PrintDailyLaunchReport(reportDate)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(16.dp)); Text(
                    " ناهار",
                    style = MaterialTheme.typography.labelMedium
                )
                }
                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = { viewModel.onIntent(AdminIntent.PrintDailyDinnerReport(reportDate)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(16.dp)); Text(
                    " شام",
                    style = MaterialTheme.typography.labelMedium
                )
                }
            }
        }

        val reservations = state.reservations.filter { it.date == reportDate }
        val foodMap = state.foods.associateBy { it.id }
        val roomMap = state.rooms.associateBy { it.roomNumber }

        if (reservations.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "سفارشی برای $reportDate نیست.",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(reservations.sortedBy { it.roomNumber }) { res ->
                    val room = roomMap[res.roomNumber]
                    val lunchOrders =
                        res.guestMealSelections.mapNotNull { it.lunchFoodId }.groupBy { it }
                            .mapValues { it.value.size }
                    val dinnerOrders =
                        res.guestMealSelections.mapNotNull { it.dinnerFoodId }.groupBy { it }
                            .mapValues { it.value.size }

                    if (lunchOrders.isNotEmpty() || dinnerOrders.isNotEmpty()) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            tonalElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "اتاق ${res.roomNumber}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        room?.guestName ?: "---",
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "ناهار",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (lunchOrders.isEmpty()) Text(
                                            "-",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        else lunchOrders.forEach { (foodId, count) ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth()
                                                    .padding(top = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    foodMap[foodId]?.name ?: "---",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    "$count پرس",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "شام",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (dinnerOrders.isEmpty()) Text(
                                            "-",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        else dinnerOrders.forEach { (foodId, count) ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth()
                                                    .padding(top = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    foodMap[foodId]?.name ?: "---",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    "$count پرس",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    maxLines = 1
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
        }
    }
}

@Composable
fun AddEditFoodDialog(
    food: FoodItem?,
    dayType: DayType,
    foodType: FoodType,
    onDismiss: () -> Unit,
    onConfirm: (FoodItem) -> Unit
) {
    var name by remember { mutableStateOf(food?.name ?: "") }
    var nameAr by remember { mutableStateOf(food?.nameAr ?: "") }
    var order by remember { mutableStateOf(food?.displayOrder?.toString() ?: "0") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (food == null) "افزودن" else "ویرایش") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام غذا (فارسی)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = nameAr,
                    onValueChange = { nameAr = it },
                    label = { Text("نام غذا (عربی)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = order,
                    onValueChange = { order = it.normalizeDigits() },
                    label = { Text("ترتیب نمایش") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    food?.copy(
                        name = name,
                        nameAr = nameAr,
                        displayOrder = order.toIntOrNull() ?: 0
                    ) ?: FoodItem(
                        name = name,
                        nameAr = nameAr,
                        type = foodType,
                        dayType = dayType,
                        displayOrder = order.toIntOrNull() ?: 0
                    )
                )
            }) { Text("تایید") }
        })
}

@Composable
fun RoomFoodReservationsDialog(
    room: Room,
    availableFoods: List<FoodItem>,
    reservations: List<FoodReservation>,
    onDismiss: () -> Unit,
    onFoodChange: (String, Int, String?, FoodType) -> Unit,
    onBreakfastChange: (String, Int) -> Unit
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
                "غذای اتاق ${room.roomNumber} - ${room.guestName}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        text = {
            Column(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().height(450.dp)) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(stayDays) { date ->
                        val reservation = reservations.find { it.date == date }
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    date,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val currentBreakfastCount = reservation?.breakfastCount ?: if (room.hasBreakfast) room.breakfastCount else 0
                                    Text("تعداد صبحانه امروز:", style = MaterialTheme.typography.labelMedium)
                                    AdminBreakfastCountSelection(
                                        currentCount = currentBreakfastCount,
                                        guestCount = room.guestCount,
                                        onCountChange = { onBreakfastChange(date, it) }
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                                repeat(room.guestCount) { index ->
                                    val selection =
                                        reservation?.guestMealSelections?.find { it.guestIndex == index }
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            "مهمان ${index + 1}:",
                                            modifier = Modifier.width(60.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        val menu = when {
                                            DateUtils.isFriday(date) -> availableFoods.filter { it.dayType == DayType.FRIDAY && it.isActive }
                                            DateUtils.isEven(date) -> availableFoods.filter { it.dayType == DayType.EVEN && it.isActive }
                                            else -> availableFoods.filter { it.dayType == DayType.ODD && it.isActive }
                                        }.sortedBy { it.displayOrder }

                                        AdminFoodSelectionItem(
                                            label = "ناهار",
                                            foods = menu.filter { it.type == FoodType.LUNCH },
                                            selectedId = selection?.lunchFoodId,
                                            modifier = Modifier.weight(1f),
                                            onSelect = { onFoodChange(date, index, it, FoodType.LUNCH) })
                                        AdminFoodSelectionItem(
                                            label = "شام",
                                            foods = menu.filter { it.type == FoodType.DINNER },
                                            selectedId = selection?.dinnerFoodId,
                                            modifier = Modifier.weight(1f),
                                            onSelect = { onFoodChange(date, index, it, FoodType.DINNER) })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("بستن") } },
        shape = MaterialTheme.shapes.large
    )
}

@Composable
fun AdminBreakfastCountSelection(
    currentCount: Int,
    guestCount: Int,
    onCountChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            onClick = { expanded = true },
            color = if (currentCount > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, if (currentCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentCount == 0) "بدون صبحانه" else "$currentCount نفر",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("بدون صبحانه") },
                onClick = { onCountChange(0); expanded = false }
            )
            (1..guestCount).forEach { num ->
                DropdownMenuItem(
                    text = { Text("$num نفر") },
                    onClick = { onCountChange(num); expanded = false }
                )
            }
        }
    }
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
        if (selectedFood == null) {
            Text(
                modifier = Modifier.clickable{ expanded = true },
                text = "عدم رزرو $label",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                textAlign = if (enabled) TextAlign.Start else TextAlign.Center
            )
        } else {
            Surface(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.onPrimary,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary
                ),
                enabled = enabled
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = selectedFood.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        textAlign = if (enabled) TextAlign.Start else TextAlign.Center
                    )
                    if (enabled) Icon(
                        Icons.Default.ArrowDropDown,
                        null,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = {
                    Text(
                        "بدون انتخاب",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                onClick = { onSelect(null); expanded = false },
                leadingIcon = { Icon(Icons.Default.Block, null, modifier = Modifier.size(14.dp)) })
            foods.forEach { food ->
                DropdownMenuItem(
                    text = {
                        Text(
                            food.name,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    onClick = { onSelect(food.id); expanded = false },
                    trailingIcon = {
                        if (food.id == selectedId) Icon(
                            Icons.Default.Check,
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    })
            }
        }
    }
}

@Composable
fun DatePickerFieldSmall(value: String, onDateSelected: (String, Long) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    Surface(
        onClick = { showDatePicker = true },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = value.ifBlank { "انتخاب" },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    if (showDatePicker) {
        PersianDatePickerDialog(
            initialDate = value,
            onDateSelected = { date, millis ->
                onDateSelected(date, millis)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun DateReservationGroup(
    date: String,
    reservations: List<FoodReservation>,
    roomMap: Map<String, Room>,
    foods: List<FoodItem>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onIntent: (AdminIntent) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        onClick = onToggle
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Event,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        date,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            "${reservations.size} اتاق",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            if (isExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    reservations.forEach { res ->
                        val room = roomMap[res.roomNumber]
                        Column {
                            Text(
                                "اتاق ${res.roomNumber} — ${room?.guestName ?: "نامعلوم"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "صبحانه:",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.width(60.dp)
                                )
                                AdminBreakfastCountSelection(
                                    currentCount = res.breakfastCount,
                                    guestCount = room?.guestCount ?: 7,
                                    onCountChange = {
                                        onIntent(
                                            AdminIntent.ChangeBreakfastCount(
                                                res.roomNumber,
                                                date,
                                                it
                                            )
                                        )
                                    }
                                )
                            }
                            res.guestMealSelections.forEach { selection ->
                                Row(
                                    modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "مهمان ${selection.guestIndex + 1} :",
                                        modifier = Modifier.width(60.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    val filteredFoods =
                                        foods.filter { it.isActive }.sortedBy { it.displayOrder }
                                    AdminFoodSelectionItem(
                                        label = "ناهار",
                                        foods = filteredFoods.filter { it.type == FoodType.LUNCH },
                                        selectedId = selection.lunchFoodId,
                                        modifier = Modifier.width(120.dp),
                                        onSelect = {
                                            onIntent(
                                                AdminIntent.ChangeFood(
                                                    res.roomNumber,
                                                    date,
                                                    selection.guestIndex,
                                                    it,
                                                    FoodType.LUNCH
                                                )
                                            )
                                        })
                                    AdminFoodSelectionItem(
                                        label = "شام",
                                        foods = filteredFoods.filter { it.type == FoodType.DINNER },
                                        selectedId = selection.dinnerFoodId,
                                        modifier = Modifier.width(120.dp),
                                        onSelect = {
                                            onIntent(
                                                AdminIntent.ChangeFood(
                                                    res.roomNumber,
                                                    date,
                                                    selection.guestIndex,
                                                    it,
                                                    FoodType.DINNER
                                                )
                                            )
                                        })
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

    val buffetBreakfastCount = todayResList.sumOf { it.breakfastCount }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Today,
                        null,
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "کل سفارشات امروز ($today)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "وعده صبحانه",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "سلف سرویس",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        HorizontalDivider(
                            Modifier.padding(vertical = 6.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "مجموع",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                "$buffetBreakfastCount پرس",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
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
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            if (foodIds.isEmpty()) {
                Text(
                    "سفارشی نیست",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1
                )
            } else {
                foodIds.groupBy { it }.forEach { (id, list) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            foodMap[id]?.name ?: id,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        ); Text(
                        "${list.size}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                    }
                }
                HorizontalDivider(
                    Modifier.padding(vertical = 6.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "مجموع",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    ); Text(
                    "${foodIds.size} پرس",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
                }
            }
        }
    }
}

@Composable
fun TodayReservationDetailByRoom(
    state: AdminState,
    searchQuery: String = "",
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onIntent: (AdminIntent) -> Unit
) {
    val todayMillis = Clock.System.now().toEpochMilliseconds()
    val today = DateUtils.convertMillisToJalaliString(todayMillis)
    val roomMap = state.rooms.associateBy { it.roomNumber }
    val todayResList = state.reservations
        .filter { it.date == today }
        .filter { res ->
            val room = roomMap[res.roomNumber]
            searchQuery.isEmpty() || res.roomNumber.contains(searchQuery) || room?.guestName?.contains(
                searchQuery,
                ignoreCase = true
            ) == true
        }

    Column(Modifier.padding(top = 10.dp)) {
        Surface(
            onClick = onToggle,
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "تحویل غذای امروز",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "وضعیت توزیع وعده‌ها",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (isExpanded || searchQuery.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            if (todayResList.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        "سفارشی برای امروز ثبت نشده است.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            todayResList.forEach { res ->
                RoomDeliveryCard(
                    res = res,
                    availableFoods = state.foods,
                    onIntent = onIntent,
                    today = today
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun RoomDeliveryCard(
    res: FoodReservation,
    availableFoods: List<FoodItem>,
    onIntent: (AdminIntent) -> Unit,
    today: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Hotel,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "اتاق ${res.roomNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            res.guestMealSelections.forEachIndexed { index, selection ->
                GuestDeliveryRow(
                    selection = selection,
                    availableFoods = availableFoods,
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
                    onFoodChange = { fId, fType ->
                        onIntent(
                            AdminIntent.ChangeFood(
                                res.roomNumber,
                                today,
                                selection.guestIndex,
                                fId,
                                fType
                            )
                        )
                    })
            }
        }
    }
}

@Composable
fun GuestDeliveryRow(
    selection: GuestMealSelection,
    availableFoods: List<FoodItem>,
    isLast: Boolean,
    onLunchDeliver: () -> Unit,
    onDinnerDeliver: () -> Unit,
    onFoodChange: (String?, FoodType) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.35f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(14.dp)
                    ); Spacer(Modifier.width(4.dp)); Text(
                    "مهمان ${selection.guestIndex + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                }
            }
            Row(
                modifier = Modifier.weight(1.65f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    AdminFoodSelectionItem(
                        modifier = Modifier.weight(2f),
                        label = "ناهار",
                        foods = availableFoods.filter { it.type == FoodType.LUNCH },
                        selectedId = selection.lunchFoodId,
                        enabled = false,
                        onSelect = { onFoodChange(it, FoodType.LUNCH) }
                    )
                    if (selection.lunchFoodId != null)
                        DeliveryChip(
                            modifier = Modifier.weight(1f),
                            isDelivered = selection.lunchDelivered,
                            onClick = onLunchDeliver,
                            deliverLabel = "تحویل",
                            deliveredLabel = "تحویل شد"
                        )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    AdminFoodSelectionItem(
                        modifier = Modifier.weight(2f),
                        label = "شام",
                        foods = availableFoods.filter { it.type == FoodType.DINNER },
                        selectedId = selection.dinnerFoodId,
                        enabled = false,
                        onSelect = { onFoodChange(it, FoodType.DINNER) }
                    )
                    if (selection.dinnerFoodId != null)
                        DeliveryChip(
                            modifier = Modifier.weight(1f),
                            isDelivered = selection.dinnerDelivered,
                            onClick = onDinnerDeliver,
                            deliverLabel = "تحویل",
                            deliveredLabel = "تحویل شد"
                        )
                }
            }
        }
        if (!isLast) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun DeliveryChip(
    modifier: Modifier = Modifier,
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
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 6.dp),
        modifier = modifier.height(26.dp)
    ) {
        if (isDelivered) {
            Icon(
                Icons.Default.Check, null, modifier = Modifier.size(10.dp)
            )
            Spacer(
                Modifier.width(2.dp)
            )
        }
        Text(
            if (isDelivered) deliveredLabel else deliverLabel,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun AddRoomDialog(existingRooms: List<Room>, onDismiss: () -> Unit, onConfirm: (Room) -> Unit) {
    var roomNumber by remember { mutableStateOf("") }
    var guestName by remember { mutableStateOf("") }
    var identificationId by remember { mutableStateOf("") }
    var guestCount by remember { mutableIntStateOf(1) }
    var hasBreakfast by remember { mutableStateOf(false) }
    var breakfastCount by remember { mutableIntStateOf(1) }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var checkInMillis by remember { mutableLongStateOf(0L) }
    var checkOutMillis by remember { mutableLongStateOf(0L) }
    var expandedGuest by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val today = remember { DateUtils.convertMillisToJalaliString(Clock.System.now().toEpochMilliseconds()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت اتاق جدید", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { 
                        roomNumber = it.normalizeDigits()
                        errorMessage = null 
                    },
                    label = { Text("شماره اتاق") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    label = { Text("نام مهمان (اختیاری)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                OutlinedTextField(
                    value = identificationId,
                    onValueChange = { identificationId = it.normalizeDigits() },
                    label = { Text("کد شناسایی") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Surface(
                            onClick = { expandedGuest = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "تعداد مهمان: $guestCount",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                        DropdownMenu(
                            expanded = expandedGuest,
                            onDismissRequest = { expandedGuest = false }) {
                            (1..7).forEach { num ->
                                DropdownMenuItem(
                                    text = { Text("$num نفر") },
                                    onClick = {
                                        guestCount = num
                                        if (breakfastCount > num) breakfastCount = num
                                        expandedGuest = false
                                    })
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                    val normalizedNum = roomNumber.normalizeDigits()
                    val activeRoom = existingRooms.find { 
                        it.roomNumber.normalizeDigits() == normalizedNum && it.checkOutDate > today 
                    }
                    
                    if (roomNumber.isBlank()) {
                        errorMessage = "لطفا شماره اتاق را وارد کنید"
                    } else if (activeRoom != null) {
                        errorMessage = "این اتاق در حال حاضر توسط ${activeRoom.guestName} اشغال شده است (تا تاریخ ${activeRoom.checkOutDate})"
                    } else {
                        onConfirm(
                            Room(
                                roomNumber = normalizedNum,
                                guestName = guestName,
                                identificationId = identificationId.normalizeDigits(),
                                guestCount = guestCount,
                                hasBreakfast = hasBreakfast,
                                breakfastCount = if (hasBreakfast) breakfastCount else 0,
                                checkInDate = checkIn,
                                checkOutDate = checkOut,
                                checkInEpochMillis = checkInMillis,
                                checkOutEpochMillis = checkOutMillis
                            )
                        )
                    }
                },
                shape = MaterialTheme.shapes.medium
            ) { Text("ثبت در سیستم", fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onDateSelected: (String, Long) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    Surface(
        onClick = { showDatePicker = true },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    value.ifBlank { "انتخاب" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
    if (showDatePicker) {
        PersianDatePickerDialog(
            initialDate = value,
            onDateSelected = { date, millis ->
                onDateSelected(date, millis)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun RoomHistoryContent(
    state: AdminState,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تاریخچه اتاق‌های ثبت شده",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            state.rooms.filter {
                it.checkOutEpochMillis < Clock.System.now().toEpochMilliseconds()
            }.let {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        "${it.size} اتاق",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = state.rooms.filter {
                it.checkOutEpochMillis < Clock.System.now().toEpochMilliseconds()
            }, key = { it.id.ifBlank { "history_${it.roomNumber}_${it.checkInEpochMillis}" } })
            { room ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Room
                        Column(modifier = Modifier.width(55.dp)) {
                            Text(
                                text = "اتاق",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = room.roomNumber,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "نام مهمان",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = room.guestName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "شناسایی",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = room.identificationId.ifBlank { "0" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        // Guest Count
                        Column(modifier = Modifier.width(75.dp)) {
                            Text(
                                text = "تعداد",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = room.guestCount.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        // Stay Period
                        Column(modifier = Modifier.weight(1.4f)) {
                            Text(
                                "بازه اقامت",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = room.checkInDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                                Text(
                                    text = "-",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                                Text(
                                    text = room.checkOutDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1.4f)) {
                            val roomReservations =
                                state.reservations.filter { it.roomNumber == room.roomNumber }
                            val totalLunch = roomReservations.flatMap { it.guestMealSelections }
                                .count { it.lunchDelivered }
                            val totalDinner = roomReservations.flatMap { it.guestMealSelections }
                                .count { it.dinnerDelivered }

                            Text(
                                "ناهار: $totalLunch",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                "شام: $totalDinner",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
