package com.braveboy.hotelzagrous.app.shared.features.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.braveboy.hotelzagrous.app.shared.features.PersianDatePickerDialog
import com.braveboy.hotelzagrous.core.*
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.state.collectAsState()
    var currentTab by remember { mutableStateOf("rooms") }
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    "منوی مدیریت",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )

                NavigationItem(label = "مدیریت اتاق‌ها", icon = Icons.Default.Bed, selected = currentTab == "rooms", onClick = { currentTab = "rooms" })
                Spacer(Modifier.height(4.dp))
                NavigationItem(label = "گزارش رزرو غذا", icon = Icons.Default.Restaurant, selected = currentTab == "reservations", onClick = { currentTab = "reservations" })
                Spacer(Modifier.height(4.dp))
                NavigationItem(label = "گزارش تفصیلی روزانه", icon = Icons.AutoMirrored.Filled.Assignment, selected = currentTab == "daily_report", onClick = { currentTab = "daily_report" })
                Spacer(Modifier.height(4.dp))
                NavigationItem("مدیریت منوی غذا", Icons.Default.RestaurantMenu, selected = currentTab == "menu", onClick = { currentTab = "menu" })

                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                NavigationItem(label = "حذف کل داده‌ها", icon = Icons.Default.DeleteForever, selected = false, onClick = { showClearDataDialog = true }, contentColor = MaterialTheme.colorScheme.error)
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
                        else -> "گزارش رزرو غذا"
                    }
                    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (currentTab == "rooms") {
                    Button(
                        shape = MaterialTheme.shapes.small,
                        onClick = { showAddRoomDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("افزودن اتاق", style = MaterialTheme.typography.labelMedium)
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
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    when (currentTab) {
                        "rooms" -> RoomManagementContent(state, viewModel)
                        "menu" -> MenuManagementContent(state, viewModel)
                        "reservations" -> ReservationSummaryContent(state, viewModel)
                        "daily_report" -> DailyDetailedReportContent(state, viewModel)
                    }
                }
            }
        }
    }

    if (showAddRoomDialog) {
        AddRoomDialog(onDismiss = { showAddRoomDialog = false }, onConfirm = { viewModel.onIntent(AdminIntent.AddRoom(it)); showAddRoomDialog = false })
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("پاکسازی کامل پایگاه داده") },
            text = { Text("با تایید این عملیات، تمامی اطلاعات مربوط به اتاق‌ها، مهمانان و تاریخچه‌ی رزروها برای همیشه حذف خواهد شد. آیا مطمئن هستید؟") },
            confirmButton = {
                Button(onClick = { viewModel.onIntent(AdminIntent.ClearAllData); showClearDataDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("بله، کاملاً پاک شود") }
            },
            dismissButton = { TextButton(onClick = { showClearDataDialog = false }) { Text("انصراف") } },
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
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (selected) {
                Spacer(Modifier.weight(1f))
                Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
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
    onUpdate: (String, String, String, String, Long, Long, Int) -> Unit,
    onFoodChange: (String, Int, String?, Boolean) -> Unit
) {
    var guestName by remember(room) { mutableStateOf(room.guestName) }
    var identificationId by remember(room) { mutableStateOf(room.identificationId) }
    var checkIn by remember(room) { mutableStateOf(room.checkInDate) }
    var checkOut by remember(room) { mutableStateOf(room.checkOutDate) }
    var checkInMillis by remember(room) { mutableLongStateOf(room.checkInEpochMillis) }
    var checkOutMillis by remember(room) { mutableLongStateOf(room.checkOutEpochMillis) }
    var guestCount by remember(room) { mutableIntStateOf(room.guestCount) }
    var expanded by remember { mutableStateOf(false) }
    var showFoodDialog by remember { mutableStateOf(false) }
    var showIdentificationIdDialog by remember { mutableStateOf(false) }

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
            // Room Number
            Column(modifier = Modifier.width(50.dp)) {
                Text("اتاق", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(room.roomNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Column(modifier = Modifier.weight(1.2f)) {
                Text("نام مهمان", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(room.guestName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("شناسایی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Surface(onClick = { showIdentificationIdDialog = true }) {
                    Text(text = room.identificationId.ifBlank { identificationId.ifBlank { "0" } }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(Modifier.width(8.dp))

            // Guest Count
            Column(modifier = Modifier.width(75.dp)) {
                Text("تعداد", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Box {
                    Surface(onClick = { expanded = true }, color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "$guestCount نفر", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1)
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp))
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        (1..7).forEach { number -> DropdownMenuItem(text = { Text("$number نفر", style = MaterialTheme.typography.bodySmall) }, onClick = { guestCount = number; expanded = false }) }
                    }
                }
            }

            // Stay Period
            Column(modifier = Modifier.weight(1.4f)) {
                Text("بازه اقامت", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DatePickerFieldSmall(checkIn) { date, millis -> checkIn = date; checkInMillis = millis }
                    Text("-", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 2.dp))
                    DatePickerFieldSmall(checkOut) { date, millis -> checkOut = date; checkOutMillis = millis }
                }
            }

            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(start = 8.dp)) {
                IconButton(onClick = { showFoodDialog = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Restaurant, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                }
                Button(
                    onClick = { onUpdate(guestName, identificationId, checkIn, checkOut, checkInMillis, checkOutMillis, guestCount) },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("به‌روزرسانی", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }
    }

    if (showFoodDialog) {
        RoomFoodReservationsDialog(room = room, availableFoods = availableFoods, reservations = reservations.filter { it.roomNumber == room.roomNumber }, onDismiss = { showFoodDialog = false }, onFoodChange = onFoodChange)
    }
}

@Composable
fun RoomManagementContent(state: AdminState, viewModel: AdminViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("لیست اتاق‌های ثبت شده", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = CircleShape) {
                Text("${state.rooms.size} اتاق", modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(items = state.rooms.filter { it.checkOutEpochMillis >= Clock.System.now().toEpochMilliseconds() }, key = { it.roomNumber }) { room ->
                RoomAdminCard(room = room, availableFoods = state.foods, reservations = state.reservations, onUpdate = { name, id, inD, outD, inM, outM, count -> viewModel.onIntent(AdminIntent.UpdateRoomStay(room.roomNumber, name, id, inD, outD, inM, outM, count)) }, onFoodChange = { d, idx, fId, isL -> viewModel.onIntent(AdminIntent.ChangeFood(room.roomNumber, d, idx, fId, isL)) })
            }
        }
    }
}

@Composable
fun ReservationSummaryContent(state: AdminState, viewModel: AdminViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { TodayReservationDetail(state) }
        item { TodayReservationDetailByRoom(state, viewModel::onIntent) }
        item { ReservationSummary(state.reservations, state.rooms, state.foods, viewModel::onIntent) }
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
            DayType.entries.forEach { FilterChip(selected = selectedDayType == it, onClick = { selectedDayType = it }, label = { Text(when (it) { DayType.EVEN -> "زوج"; DayType.ODD -> " فرد"; else -> "جمعه" }, style = MaterialTheme.typography.labelSmall) }) }
        }
        Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FoodType.entries.forEach { FilterChip(selected = selectedFoodType == it, onClick = { selectedFoodType = it }, label = { Text(if (it == FoodType.LUNCH) "ناهار" else "شام", style = MaterialTheme.typography.labelSmall) }) }
            Spacer(Modifier.weight(1f))
            val config = state.menuConfigs.find { it.dayType == selectedDayType && it.foodType == selectedFoodType }
            Text("فعال", style = MaterialTheme.typography.labelSmall)
            Switch(checked = config?.isEnabled ?: true, onCheckedChange = { viewModel.onIntent(AdminIntent.UpdateMenuConfig(MenuConfig(selectedDayType, selectedFoodType, it))) }, modifier = Modifier.scale(0.55f))
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("لیست غذاها", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Button(onClick = { showAddDialog = true }, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("افزودن", style = MaterialTheme.typography.labelSmall)
            }
        }

        val foods = state.foods.filter { it.dayType == selectedDayType && it.type == selectedFoodType }.sortedBy { it.displayOrder }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(foods) { food ->
                Surface(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape = MaterialTheme.shapes.medium) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(food.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("ترتیب: ${food.displayOrder}", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Checkbox(checked = food.isActive, onCheckedChange = { viewModel.onIntent(AdminIntent.UpsertFood(food.copy(isActive = it))) }, modifier = Modifier.scale(0.7f))
                        Text("فعال", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        Checkbox(checked = food.isVisibleToUsers, onCheckedChange = { viewModel.onIntent(AdminIntent.UpsertFood(food.copy(isVisibleToUsers = it))) }, modifier = Modifier.scale(0.7f))
                        Text("نمایش", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        IconButton(onClick = { editingFood = food }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)) }
                        IconButton(onClick = { viewModel.onIntent(AdminIntent.DeleteFood(food.id)) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp)) }
                    }
                }
            }
        }
    }
    if (showAddDialog) AddEditFoodDialog(null, selectedDayType, selectedFoodType, { showAddDialog = false }) { viewModel.onIntent(AdminIntent.UpsertFood(it)); showAddDialog = false }
    if (editingFood != null) AddEditFoodDialog(editingFood, selectedDayType, selectedFoodType, { editingFood = null }) { viewModel.onIntent(AdminIntent.UpsertFood(it)); editingFood = null }
}

@Composable
fun DailyDetailedReportContent(state: AdminState, viewModel: AdminViewModel) {
    val reportDate = state.selectedReportDate.ifBlank { remember { DateUtils.convertMillisToJalaliString(Clock.System.now().toEpochMilliseconds()) } }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("گزارش تفصیلی روزانه", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("سفارشات به تفکیک اتاق", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DatePickerFieldSmall(reportDate) { date, _ -> viewModel.onIntent(AdminIntent.SelectReportDate(date)) }
                Button(shape = MaterialTheme.shapes.small, onClick = { viewModel.onIntent(AdminIntent.PrintDailyLaunchReport(reportDate)) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary), modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(16.dp)); Text(" ناهار", style = MaterialTheme.typography.labelSmall)
                }
                Button(shape = MaterialTheme.shapes.small, onClick = { viewModel.onIntent(AdminIntent.PrintDailyDinnerReport(reportDate)) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary), modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Icon(Icons.Default.Print, null, modifier = Modifier.size(16.dp)); Text(" شام", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        val reservations = state.reservations.filter { it.date == reportDate }
        val foodMap = state.foods.associateBy { it.id }
        val roomMap = state.rooms.associateBy { it.roomNumber }

        if (reservations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("سفارشی برای $reportDate نیست.", color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.bodySmall) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(reservations.sortedBy { it.roomNumber }) { res ->
                    val room = roomMap[res.roomNumber]
                    val lunchOrders = res.guestMealSelections.mapNotNull { it.lunchFoodId }.groupBy { it }.mapValues { it.value.size }
                    val dinnerOrders = res.guestMealSelections.mapNotNull { it.dinnerFoodId }.groupBy { it }.mapValues { it.value.size }

                    if (lunchOrders.isNotEmpty() || dinnerOrders.isNotEmpty()) {
                        Surface(shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), tonalElevation = 1.dp) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("اتاق ${res.roomNumber}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(room?.guestName ?: "---", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("ناهار", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        if (lunchOrders.isEmpty()) Text("-", style = MaterialTheme.typography.labelSmall)
                                        else lunchOrders.forEach { (foodId, count) ->
                                            Row(modifier = Modifier.fillMaxWidth().padding(top = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(foodMap[foodId]?.name ?: "---", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                                Text("$count پرس", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                            }
                                        }
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("شام", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        if (dinnerOrders.isEmpty()) Text("-", style = MaterialTheme.typography.labelSmall)
                                        else dinnerOrders.forEach { (foodId, count) ->
                                            Row(modifier = Modifier.fillMaxWidth().padding(top = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(foodMap[foodId]?.name ?: "---", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                                Text("$count پرس", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, maxLines = 1)
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
fun AddEditFoodDialog(food: FoodItem?, dayType: DayType, foodType: FoodType, onDismiss: () -> Unit, onConfirm: (FoodItem) -> Unit) {
    var name by remember { mutableStateOf(food?.name ?: "") }
    var order by remember { mutableStateOf(food?.displayOrder?.toString() ?: "0") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (food == null) "افزودن" else "ویرایش") }, text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام غذا") }, singleLine = true); OutlinedTextField(value = order, onValueChange = { order = it }, label = { Text("ترتیب نمایش") }, singleLine = true) } }, confirmButton = { Button(onClick = { onConfirm(food?.copy(name = name, displayOrder = order.toIntOrNull() ?: 0) ?: FoodItem(name = name, type = foodType, dayType = dayType, displayOrder = order.toIntOrNull() ?: 0)) }) { Text("تایید") } })
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
        title = { Text("غذای اتاق ${room.roomNumber} - ${room.guestName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        text = {
            Column(modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().height(450.dp)) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(stayDays) { date ->
                        val reservation = reservations.find { it.date == date }
                        Surface(shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), color = MaterialTheme.colorScheme.surface) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(date, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                                repeat(room.guestCount) { index ->
                                    val selection = reservation?.guestMealSelections?.find { it.guestIndex == index }
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("مهمان ${index + 1}:", modifier = Modifier.width(60.dp), style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        val menu = when { DateUtils.isFriday(date) -> availableFoods.filter { it.dayType == DayType.FRIDAY }; DateUtils.isEven(date) -> availableFoods.filter { it.dayType == DayType.EVEN }; else -> availableFoods.filter { it.dayType == DayType.ODD } }
                                        AdminFoodSelectionItem(label = "ناهار", foods = menu.filter { it.type == FoodType.LUNCH }, selectedId = selection?.lunchFoodId, modifier = Modifier.weight(1f), onSelect = { onFoodChange(date, index, it, true) })
                                        AdminFoodSelectionItem(label = "شام", foods = menu.filter { it.type == FoodType.DINNER }, selectedId = selection?.dinnerFoodId, modifier = Modifier.weight(1f), onSelect = { onFoodChange(date, index, it, false) })
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
            color = if (selectedFood != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            border = BorderStroke(1.dp, if (selectedFood != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
            enabled = enabled
        ) {
            Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(modifier = Modifier.weight(1f), text = selectedFood?.name ?: label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = if (selectedFood != null) FontWeight.Bold else FontWeight.Normal, textAlign = if (enabled) TextAlign.Start else TextAlign.Center)
                if (enabled) Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(12.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("بدون انتخاب", style = MaterialTheme.typography.labelSmall) }, onClick = { onSelect(null); expanded = false }, leadingIcon = { Icon(Icons.Default.Block, null, modifier = Modifier.size(14.dp)) })
            foods.forEach { food -> DropdownMenuItem(text = { Text(food.name, style = MaterialTheme.typography.labelSmall) }, onClick = { onSelect(food.id); expanded = false }, trailingIcon = { if (food.id == selectedId) Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary) }) }
        }
    }
}

@Composable
fun DatePickerFieldSmall(value: String, onDateSelected: (String, Long) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    Surface(onClick = { showDatePicker = true }, color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small) {
        Text(text = value.ifBlank { "انتخاب" }, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
fun ReservationSummary(
    reservations: List<FoodReservation>,
    rooms: List<Room>,
    foods: List<FoodItem>,
    onIntent: (AdminIntent) -> Unit
) {
    val roomMap = rooms.associateBy { it.roomNumber }
    val todayMillis = Clock.System.now().toEpochMilliseconds()
    val today = DateUtils.convertMillisToJalaliString(todayMillis)
    val summaryByDate = reservations.filter { it.date > today }.groupBy { it.date }.toList().sortedBy { it.first }

    if (summaryByDate.isEmpty()) {
        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium) {
            Text("رزروی برای تاریخ‌های آینده ثبت نشده است.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        return
    }

    summaryByDate.forEach { (date, dailyResList) ->
        val validRes = dailyResList.filter { res -> res.guestMealSelections.any { it.lunchFoodId != null || it.dinnerFoodId != null } }
        if (validRes.isNotEmpty()) {
            Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                    validRes.forEach { res ->
                        val room = roomMap[res.roomNumber]
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text("اتاق ${res.roomNumber} — ${room?.guestName ?: "نامعلوم"}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            res.guestMealSelections.forEach { selection ->
                                Row(modifier = Modifier.padding(top = 4.dp, start = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = "مهمان ${selection.guestIndex + 1} :", modifier = Modifier.width(60.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    AdminFoodSelectionItem(label = "ناهار", foods = foods.filter { it.type == FoodType.LUNCH }, selectedId = selection.lunchFoodId, modifier = Modifier.width(120.dp), onSelect = { onIntent(AdminIntent.ChangeFood(res.roomNumber, date, selection.guestIndex, it, true)) })
                                    AdminFoodSelectionItem(label = "شام", foods = foods.filter { it.type == FoodType.DINNER }, selectedId = selection.dinnerFoodId, modifier = Modifier.width(120.dp), onSelect = { onIntent(AdminIntent.ChangeFood(res.roomNumber, date, selection.guestIndex, it, false)) })
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

    Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Today, null, tint = Color.White, modifier = Modifier.padding(5.dp)) }
                Spacer(Modifier.width(10.dp))
                Text("کل سفارشات امروز ($today)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MealSummaryBox("وعده ناهار", allLunch, foodMap, Modifier.weight(1f))
                MealSummaryBox("وعده شام", allDinner, foodMap, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MealSummaryBox(label: String, foodIds: List<String>, foodMap: Map<String, FoodItem>, modifier: Modifier) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            if (foodIds.isEmpty()) { Text("سفارشی نیست", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, maxLines = 1) }
            else {
                foodIds.groupBy { it }.forEach { (id, list) -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(foodMap[id]?.name ?: id, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)); Text("${list.size}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, maxLines = 1) } }
                HorizontalDivider(Modifier.padding(vertical = 6.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("مجموع", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, maxLines = 1); Text("${foodIds.size} پرس", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, maxLines = 1) }
            }
        }
    }
}

@Composable
fun TodayReservationDetailByRoom(state: AdminState, onIntent: (AdminIntent) -> Unit) {
    val todayMillis = Clock.System.now().toEpochMilliseconds()
    val today = DateUtils.convertMillisToJalaliString(todayMillis)
    val todayResList = state.reservations.filter { it.date == today }
    val foodMap = state.foods.associateBy { it.id }

    Column(Modifier.padding(top = 10.dp)) {
        Text("تحویل غذای امروز", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("وضعیت توزیع وعده‌ها", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (todayResList.isEmpty()) { Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium) { Text("سفارشی برای امروز ثبت نشده است.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis) } }
        todayResList.forEach { res -> RoomDeliveryCard(res = res, availableFoods = state.foods, foodMap = foodMap, onIntent = onIntent, today = today); Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
fun RoomDeliveryCard(res: FoodReservation, availableFoods: List<FoodItem>, foodMap: Map<String, FoodItem>, onIntent: (AdminIntent) -> Unit, today: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Column {
            Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Hotel, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("اتاق ${res.roomNumber}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            res.guestMealSelections.forEachIndexed { index, selection -> GuestDeliveryRow(selection = selection, availableFoods = availableFoods, foodMap = foodMap, isLast = index == res.guestMealSelections.size - 1, onLunchDeliver = { onIntent(AdminIntent.MarkLunchDelivered(res.roomNumber, selection.guestIndex, today)) }, onDinnerDeliver = { onIntent(AdminIntent.MarkDinnerDelivered(res.roomNumber, selection.guestIndex, today)) }, onFoodChange = { fId, isL -> onIntent(AdminIntent.ChangeFood(res.roomNumber, today, selection.guestIndex, fId, isL)) }) }
        }
    }
}

@Composable
fun GuestDeliveryRow(selection: GuestMealSelection, availableFoods: List<FoodItem>, foodMap: Map<String, FoodItem>, isLast: Boolean, onLunchDeliver: () -> Unit, onDinnerDeliver: () -> Unit, onFoodChange: (String?, Boolean) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(0.35f)) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("مهمان ${selection.guestIndex + 1}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) } }
            Row(modifier = Modifier.weight(1.65f), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    AdminFoodSelectionItem(label = "ناهار", foods = availableFoods.filter { it.type == FoodType.LUNCH }, selectedId = selection.lunchFoodId, modifier = Modifier.weight(1f), enabled = false, onSelect = { onFoodChange(it, true) })
                    if (selection.lunchFoodId != null) DeliveryChip(isDelivered = selection.lunchDelivered, onClick = onLunchDeliver, deliverLabel = "تحویل", deliveredLabel = "شد")
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    AdminFoodSelectionItem(label = "شام", foods = availableFoods.filter { it.type == FoodType.DINNER }, selectedId = selection.dinnerFoodId, modifier = Modifier.weight(1f), enabled = false, onSelect = { onFoodChange(it, false) })
                    if (selection.dinnerFoodId != null) DeliveryChip(isDelivered = selection.dinnerDelivered, onClick = onDinnerDeliver, deliverLabel = "تحویل", deliveredLabel = "شد")
                }
            }
        }
        if (!isLast) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun DeliveryChip(isDelivered: Boolean, onClick: () -> Unit, deliverLabel: String, deliveredLabel: String) {
    Button(onClick = onClick, enabled = !isDelivered, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White, disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant, disabledContentColor = MaterialTheme.colorScheme.outline), shape = RoundedCornerShape(4.dp), contentPadding = PaddingValues(horizontal = 6.dp), modifier = Modifier.height(26.dp)) {
        if (isDelivered) { Icon(Icons.Default.Check, null, modifier = Modifier.size(10.dp)); Spacer(Modifier.width(2.dp)) }
        Text(if (isDelivered) deliveredLabel else deliverLabel, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
fun AddRoomDialog(onDismiss: () -> Unit, onConfirm: (Room) -> Unit) {
    var roomNumber by remember { mutableStateOf("") }
    var guestName by remember { mutableStateOf("") }
    var identificationId by remember { mutableStateOf("") }
    var guestCount by remember { mutableIntStateOf(1) }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }
    var checkInMillis by remember { mutableLongStateOf(0L) }
    var checkOutMillis by remember { mutableLongStateOf(0L) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت اتاق جدید", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = roomNumber, onValueChange = { roomNumber = it }, label = { Text("شماره اتاق") }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium, singleLine = true)
                    OutlinedTextField(value = identificationId, onValueChange = { identificationId = it }, label = { Text("شناسایی") }, modifier = Modifier.weight(1.5f), shape = MaterialTheme.shapes.medium, singleLine = true)
                }
                OutlinedTextField(value = guestName, onValueChange = { guestName = it }, label = { Text("نام مهمان") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = Color.Transparent) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.People, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("تعداد: $guestCount نفر", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { (1..10).forEach { number -> DropdownMenuItem(text = { Text("$number نفر") }, onClick = { guestCount = number; expanded = false }) } }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DatePickerField(label = "تاریخ ورود", value = checkIn, modifier = Modifier.weight(1f)) { date, millis -> checkIn = date; checkInMillis = millis }
                    DatePickerField(label = "تاریخ خروج", value = checkOut, modifier = Modifier.weight(1f)) { date, millis -> checkOut = date; checkOutMillis = millis }
                }
            }
        },
        confirmButton = { Button(onClick = { if (roomNumber.isNotBlank() && guestName.isNotBlank()) onConfirm(Room(roomNumber = roomNumber.normalizeDigits(), guestName = guestName, identificationId = identificationId.normalizeDigits(), guestCount = guestCount, checkInDate = checkIn, checkOutDate = checkOut, checkInEpochMillis = checkInMillis, checkOutEpochMillis = checkOutMillis)) }, shape = MaterialTheme.shapes.medium) { Text("ثبت در سیستم", fontWeight = FontWeight.Bold, maxLines = 1) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف", maxLines = 1) } }
    )
}

@Composable
fun DatePickerField(label: String, value: String, modifier: Modifier = Modifier, onDateSelected: (String, Long) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    Surface(onClick = { showDatePicker = true }, modifier = modifier, shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = Color.Transparent) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(6.dp))
                Text(value.ifBlank { "انتخاب" }, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1)
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
