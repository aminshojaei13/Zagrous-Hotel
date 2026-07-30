package com.braveboy.hotelzagrous.app.shared.features.reservation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.DayType
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodType
import kotlin.time.Clock

@Composable
fun ReservationScreen(viewModel: ReservationViewModel) {
    val state by viewModel.state.collectAsState()
    val strings = if (state.isArabic) ArabicStrings else FarsiStrings

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!state.isLoggedIn) {
            LoginSection(
                roomNumber = state.roomNumber,
                onRoomNumberChange = { viewModel.onIntent(ReservationIntent.UpdateRoomNumber(it)) },
                identificationId = state.identificationId,
                onIdentificationIdChange = {
                    viewModel.onIntent(
                        ReservationIntent.UpdateIdentificationId(
                            it
                        )
                    )
                },
                onLogin = { viewModel.onIntent(ReservationIntent.Login) },
                onToggleLanguage = { viewModel.onIntent(ReservationIntent.ToggleLanguage) },
                error = state.error,
                strings = strings
            )
        } else {
            UserDashboard(state, viewModel, strings)
        }
    }
}

@Composable
fun LoginSection(
    roomNumber: String,
    onRoomNumberChange: (String) -> Unit,
    identificationId: String,
    onIdentificationIdChange: (String) -> Unit,
    onLogin: () -> Unit,
    onToggleLanguage: () -> Unit,
    error: String?,
    strings: AppStrings
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
            TextButton(onClick = onToggleLanguage) {
                Icon(Icons.Default.Language, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(strings.switchLanguage)
            }
        }

        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopStart)
                .offset(x = (-150).dp, y = (-150).dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(24.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Hotel,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        strings.welcome,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        strings.systemTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(40.dp))

                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = onRoomNumberChange,
                        label = { Text(strings.roomNumber) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                Icons.Default.MeetingRoom,
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = identificationId,
                        onValueChange = onIdentificationIdChange,
                        label = { Text(strings.identificationId) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Badge,
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                    )

                    if (error != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Error,
                                null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = onLogin,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            strings.login,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                strings.supportText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(state: ReservationState, viewModel: ReservationViewModel, strings: AppStrings) {
    val room = state.room
    val stayDays = remember(room, state.tempReservations) {
        if (room != null && room.checkInEpochMillis != 0L && room.checkOutEpochMillis != 0L) {
            val days = mutableListOf<String>()
            var currentMillis = room.checkInEpochMillis
            while (currentMillis <= room.checkOutEpochMillis) {
                days.add(DateUtils.convertMillisToJalaliString(currentMillis))
                currentMillis += 24 * 60 * 60 * 1000L
            }

            if (days.size > 1) {
                val firstDay = days.first()
                val firstDayReservation = state.tempReservations.find { it.date == firstDay }
                val isFirstDayLunchSelected =
                    firstDayReservation?.guestMealSelections?.any { it.lunchFoodId != null } == true
                if (isFirstDayLunchSelected) {
                    days.removeLast()
                }
            }
            days
        } else {
            emptyList()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            strings.guestPanel,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            strings.hotelName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.onIntent(ReservationIntent.ToggleLanguage) }) {
                        Text(if (state.isArabic) "فارسی" else "العربية")
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                strings.roomLabel(room?.roomNumber ?: ""),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 40.dp, y = 40.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        )

                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                strings.dearGuest(room?.guestName ?: ""),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.People,
                                    null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    strings.personsCount(room?.guestCount ?: 0),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(Modifier.width(24.dp))
                                Icon(
                                    Icons.Default.DateRange,
                                    null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "${room?.checkInDate} ${strings.toLabel} ${room?.checkOutDate}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.RestaurantMenu,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            strings.selectFoodProgram,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                strings.reservationDeadlineInfo,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            items(stayDays) { date ->
                FoodCard(date, state, viewModel, strings)
            }

            item {
                Spacer(Modifier.height(24.dp))
                if (state.error != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            state.error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Button(
                    onClick = { viewModel.onIntent(ReservationIntent.ConfirmReservation) },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = MaterialTheme.shapes.large,
                    enabled = !state.isLoading,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                strings.confirmReservation,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FoodCard(
    date: String,
    state: ReservationState,
    viewModel: ReservationViewModel,
    strings: AppStrings
) {
    val guestCount = state.room?.guestCount ?: 1
    val reservation = state.tempReservations.find { it.date == date }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Event,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    strings.dateLabel(date),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(20.dp))

            // Daily Breakfast Selection
            DailyBreakfastRow(
                reservation = reservation,
                guestCount = guestCount,
                strings = strings,
                onCountChange = { viewModel.onIntent(ReservationIntent.ChangeBreakfastCount(date, it)) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )

            repeat(guestCount) { index ->
                val guestSelection =
                    reservation?.guestMealSelections?.find { it.guestIndex == index }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            strings.guestSelection(index + 1),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val dayType = when {
                            DateUtils.isFriday(date) -> DayType.FRIDAY
                            DateUtils.isEven(date) -> DayType.EVEN
                            else -> DayType.ODD
                        }

                        val allFoods =
                            state.availableFoods.filter { it.dayType == dayType && it.isActive && it.isVisibleToUsers }
                                .sortedBy { it.displayOrder }

                        val lunchEnabled =
                            state.menuConfigs.find { it.dayType == dayType && it.foodType == FoodType.LUNCH }?.isEnabled
                                ?: true
                        val dinnerEnabled =
                            state.menuConfigs.find { it.dayType == dayType && it.foodType == FoodType.DINNER }?.isEnabled
                                ?: true

                        val sixHours = 6 * 60 * 60 * 1000L
                        val twelveHours = 12 * 60 * 60 * 1000L
                        val isLunchTimeLocked =
                            (DateUtils.convertDateToTimeMillis(date) - Clock.System.now()
                                .toEpochMilliseconds()) <= sixHours
                        val isDinnerTimeLocked =
                            ((DateUtils.convertDateToTimeMillis(date) + twelveHours) - Clock.System.now()
                                .toEpochMilliseconds()) <= 0

                        FoodSelectionItem(
                            modifier = Modifier.weight(1f),
                            label = strings.lunch,
                            icon = Icons.Default.WbSunny,
                            foods = allFoods.filter { it.type == FoodType.LUNCH },
                            selectedId = guestSelection?.lunchFoodId,
                            enabled = lunchEnabled && !isLunchTimeLocked,
                            strings = strings,
                            isArabic = state.isArabic
                        ) { foodId ->
                            viewModel.onIntent(
                                ReservationIntent.ChangeFood(
                                    date = date,
                                    guestIndex = index,
                                    foodId = foodId,
                                    foodType = FoodType.LUNCH
                                )
                            )
                        }

                        FoodSelectionItem(
                            modifier = Modifier.weight(1f),
                            label = strings.dinner,
                            icon = Icons.Default.NightsStay,
                            foods = allFoods.filter { it.type == FoodType.DINNER },
                            selectedId = guestSelection?.dinnerFoodId,
                            enabled = dinnerEnabled && !isDinnerTimeLocked,
                            strings = strings,
                            isArabic = state.isArabic
                        ) { foodId ->
                            viewModel.onIntent(
                                ReservationIntent.ChangeFood(
                                    date = date,
                                    guestIndex = index,
                                    foodId = foodId,
                                    foodType = FoodType.DINNER
                                )
                            )
                        }
                    }
                }

                if (index < guestCount - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DailyBreakfastRow(
    reservation: FoodReservation?,
    guestCount: Int,
    strings: AppStrings,
    onCountChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentCount = reservation?.breakfastCount ?: 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
                color = if (currentCount > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = CircleShape,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.WbSunny,
                        null,
                        tint = if (currentCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    strings.breakfast,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (currentCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    strings.buffet,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Box {
            Surface(
                onClick = { expanded = true },
                shape = MaterialTheme.shapes.medium,
                color = if (currentCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, if (currentCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentCount == 0) strings.noBreakfast else strings.personLabel(currentCount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (currentCount > 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowDropDown,
                        null,
                        tint = if (currentCount > 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(strings.noBreakfast) },
                    onClick = { onCountChange(0); expanded = false },
                    leadingIcon = { Icon(Icons.Default.Block, null, modifier = Modifier.size(18.dp)) }
                )
                (1..guestCount).forEach { num ->
                    DropdownMenuItem(
                        text = { Text(strings.personLabel(num)) },
                        onClick = { onCountChange(num); expanded = false },
                        trailingIcon = { if (currentCount == num) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }
                    )
                }
            }
        }
    }
}

@Composable
fun FoodSelectionItem(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    foods: List<FoodItem>,
    selectedId: String?,
    enabled: Boolean,
    strings: AppStrings,
    isArabic: Boolean,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedFood = foods.find { it.id == selectedId }

    Box(modifier = modifier) {
        Surface(
            enabled = enabled,
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = if (!enabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3F) else if (selectedFood != null) MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.1f
            ) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = if (selectedFood != null) BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary
            ) else null
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = if (!enabled) MaterialTheme.colorScheme.outline else if (selectedFood != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (!enabled) MaterialTheme.colorScheme.outline else if (selectedFood != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (!enabled && selectedFood == null) strings.noSelection
                        else (if (isArabic && !selectedFood?.nameAr.isNullOrBlank()) selectedFood.nameAr else selectedFood?.name) ?: strings.notSelected,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedFood != null) FontWeight.Bold else FontWeight.Normal,
                        color = if (!enabled) MaterialTheme.colorScheme.outline else if (selectedFood != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (enabled) {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = if (selectedFood != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (enabled) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    .width(IntrinsicSize.Min)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            strings.noSelection,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onSelect(null)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Block,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                foods.forEach { food ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                (if (isArabic && !food.nameAr.isNullOrBlank()) food.nameAr else food.name) ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            onSelect(food.id)
                            expanded = false
                        },
                        trailingIcon = {
                            if (food.id == selectedId) {
                                Icon(
                                    Icons.Default.Check,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

// Language Handling
interface AppStrings {
    val welcome: String
    val systemTitle: String
    val roomNumber: String
    val identificationId: String
    val login: String
    val supportText: String
    val guestPanel: String
    val hotelName: String
    val roomLabel: (String) -> String
    val dearGuest: (String) -> String
    val personsCount: (Int) -> String
    val toLabel: String
    val selectFoodProgram: String
    val confirmReservation: String
    val dateLabel: (String) -> String
    val guestSelection: (Int) -> String
    val lunch: String
    val dinner: String
    val breakfast: String
    val buffet: String
    val noBreakfast: String
    val personLabel: (Int) -> String
    val noSelection: String
    val notSelected: String
    val switchLanguage: String
    val reservationDeadlineInfo: String
}

object FarsiStrings : AppStrings {
    override val welcome = "خوش آمدید"
    override val systemTitle = "سامانه رزرو غذای هتل زاگرس"
    override val roomNumber = "شماره اتاق"
    override val identificationId = "شماره شناسایی"
    override val login = "ورود به سامانه"
    override val supportText = "در صورت بروز مشکل به پذیرش مراجعه فرمایید"
    override val guestPanel = "پنل مهمان"
    override val hotelName = "هتل زاگرس"
    override val roomLabel: (String) -> String = { "اتاق $it" }
    override val dearGuest: (String) -> String = { "مهمان گرامی، جناب $it" }
    override val personsCount: (Int) -> String = { "$it نفر" }
    override val toLabel = "الی"
    override val selectFoodProgram = "انتخاب برنامه غذایی"
    override val confirmReservation = "ثبت نهایی و تایید رزروها"
    override val dateLabel: (String) -> String = { "تاریخ: $it" }
    override val guestSelection: (Int) -> String = { "انتخاب مهمان $it:" }
    override val lunch = "وعده ناهار"
    override val dinner = "وعده شام"
    override val breakfast = "صبحانه سلف"
    override val buffet = "سلف سرویس"
    override val noBreakfast = "بدون صبحانه"
    override val personLabel: (Int) -> String = { "$it نفر" }
    override val noSelection = "عدم انتخاب (هیچکدام)"
    override val notSelected = "انتخاب نشده"
    override val switchLanguage = "تغییر زبان"
    override val reservationDeadlineInfo = "توجه: امکان انتخاب یا تغییر ناهار تا ساعت ۱۸ روز قبل و شام تا ساعت ۱۲ همان روز میسر است."
}

object ArabicStrings : AppStrings {
    override val welcome = "أهلاً بك"
    override val systemTitle = "نظام حجز طعام فندق زاكروس"
    override val roomNumber = "رقم الغرفة"
    override val identificationId = "رقم الهوية"
    override val login = "تسجيل الدخول"
    override val supportText = "في حال حدوث مشكلة، يرجى مراجعة الاستقبال"
    override val guestPanel = "لوحة الضيف"
    override val hotelName = "فندق زاكروس"
    override val roomLabel: (String) -> String = { "غرفة $it" }
    override val dearGuest: (String) -> String = { "ضيفنا العزيز، السيد $it" }
    override val personsCount: (Int) -> String = { "$it أشخاص" }
    override val toLabel = "إلى"
    override val selectFoodProgram = "اختيار برنامج الغذاء"
    override val confirmReservation = "التأكيد النهائي وحجز الوجبات"
    override val dateLabel: (String) -> String = { "التاريخ: $it" }
    override val guestSelection: (Int) -> String = { "اختيار الضيف $it:" }
    override val lunch = "وجبة الغداء"
    override val dinner = "وجبة العشاء"
    override val breakfast = "إفطار سلف"
    override val buffet = "بوفيه مفتوح"
    override val noBreakfast = "بدون إفطار"
    override val personLabel: (Int) -> String = { "$it أشخاص" }
    override val noSelection = "عدم الاختيار (لا شيء)"
    override val notSelected = "لم يتم الاختيار"
    override val switchLanguage = "تغيير اللغة"
    override val reservationDeadlineInfo = "تنبيه: يمكن اختيار أو تغییر وجبة الغداء حتى الساعة 6 مساءً من اليوم السابق، ووجبة العشاء حتى الساعة 12 ظهراً من نفس اليوم."
}
