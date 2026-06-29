package com.braveboy.hotelzagrous.app.shared.features.reservation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.DayType
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodType
import hotelzagrous.app.shared.generated.resources.Res
import hotelzagrous.app.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun ReservationScreen(viewModel: ReservationViewModel) {
    val state by viewModel.state.collectAsState()

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
                    viewModel.onIntent(ReservationIntent.UpdateIdentificationId(it))
                },
                onLogin = { viewModel.onIntent(ReservationIntent.Login) },
                onToggleLanguage = { viewModel.onIntent(ReservationIntent.ToggleLanguage) },
                error = state.error
            )
        } else {
            UserDashboard(state, viewModel)
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
    error: String?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
            TextButton(onClick = onToggleLanguage) {
                Icon(Icons.Default.Language, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.switch_language))
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.9f).padding(24.dp)
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
                        stringResource(Res.string.welcome),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        stringResource(Res.string.system_title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(40.dp))

                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = onRoomNumberChange,
                        label = { Text(stringResource(Res.string.room_number)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.MeetingRoom, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = identificationId,
                        onValueChange = onIdentificationIdChange,
                        label = { Text(stringResource(Res.string.identification_id)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Badge, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )

                    if (error != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                resolveErrorMessage(error),
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
                            stringResource(Res.string.login),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                stringResource(Res.string.support_text),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(state: ReservationState, viewModel: ReservationViewModel) {
    val room = state.room
    val stayDays = remember(room) {
        if (room != null && room.checkInEpochMillis != 0L && room.checkOutEpochMillis != 0L) {
            val days = mutableListOf<String>()
            var currentMillis = room.checkInEpochMillis
            while (currentMillis <= room.checkOutEpochMillis) {
                days.add(DateUtils.convertMillisToJalaliString(currentMillis))
                currentMillis += 24 * 60 * 60 * 1000L
            }
            days
        } else emptyList()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(Res.string.guest_panel), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Text(stringResource(Res.string.hotel_name), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.onIntent(ReservationIntent.ToggleLanguage) }) {
                        Text(if (state.isArabic) stringResource(Res.string.persian) else stringResource(Res.string.arabic))
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(Res.string.room_label, room?.roomNumber ?: ""),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            stringResource(Res.string.dear_guest, room?.guestName ?: ""),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.People, null, modifier = Modifier.size(18.dp), tint = Color.White.copy(alpha = 0.8f))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(Res.string.persons_count, room?.guestCount ?: 0),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(Modifier.width(24.dp))
                            Icon(Icons.Default.DateRange, null, modifier = Modifier.size(18.dp), tint = Color.White.copy(alpha = 0.8f))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${room?.checkInDate} ${stringResource(Res.string.to_label)} ${room?.checkOutDate}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RestaurantMenu, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(Res.string.select_food_program), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }

            items(stayDays) { date ->
                FoodCard(date, state, viewModel)
            }

            item {
                Spacer(Modifier.height(24.dp))
                state.error?.let { err ->
                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Text(resolveErrorMessage(err), color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
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
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null)
                            Spacer(Modifier.width(12.dp))
                            Text(stringResource(Res.string.confirm_reservation), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FoodCard(date: String, state: ReservationState, viewModel: ReservationViewModel) {
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
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape, modifier = Modifier.size(36.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Event, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(stringResource(Res.string.date_label, date), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))

            repeat(guestCount) { index ->
                val guestSelection = reservation?.guestMealSelections?.find { it.guestIndex == index }

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(stringResource(Res.string.guest_selection, index + 1), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val foods = when {
                            DateUtils.isFriday(date) -> state.availableFoods.filter { it.dayType == DayType.FRIDAY }
                            DateUtils.isEven(date) -> state.availableFoods.filter { it.dayType == DayType.EVEN }
                            else -> state.availableFoods.filter { it.dayType == DayType.ODD }
                        }

                        val twelveHours = 8 * 60 * 60 * 1000L
                        val isMoreThan12Hours = (DateUtils.convertDateToTimeMillis(date) - Clock.System.now().toEpochMilliseconds()) > twelveHours

                        FoodSelectionItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.lunch),
                            icon = Icons.Default.WbSunny,
                            foods = foods.filter { it.type == FoodType.LUNCH },
                            selectedId = guestSelection?.lunchFoodId,
                            enabled = isMoreThan12Hours,
                            isArabic = state.isArabic
                        ) { viewModel.onIntent(ReservationIntent.ChangeFood(date, index, it, true)) }

                        FoodSelectionItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.dinner),
                            icon = Icons.Default.NightsStay,
                            foods = foods.filter { it.type == FoodType.DINNER },
                            selectedId = guestSelection?.dinnerFoodId,
                            enabled = isMoreThan12Hours,
                            isArabic = state.isArabic
                        ) { viewModel.onIntent(ReservationIntent.ChangeFood(date, index, it, false)) }
                    }
                }
                if (index < guestCount - 1) HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
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
    isArabic: Boolean,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedFood = foods.find { it.id == selectedId }
    val displayName = if (isArabic) selectedFood?.nameAr ?: selectedFood?.name else selectedFood?.name

    Box(modifier = modifier) {
        Surface(
            enabled = enabled,
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = if (!enabled) MaterialTheme.colorScheme.outline.copy(alpha = 0.3F) else if (selectedFood != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = if (selectedFood != null) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, modifier = Modifier.size(14.dp), tint = if (selectedFood != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.width(6.dp))
                    Text(label, style = MaterialTheme.typography.labelSmall, color = if (selectedFood != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                }
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        displayName ?: stringResource(Res.string.not_selected),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedFood != null) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedFood != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(Icons.Default.ArrowDropDown, null, tint = if (selectedFood != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
                }
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface).width(IntrinsicSize.Min)) {
            foods.forEach { food ->
                DropdownMenuItem(
                    text = { Text(if (isArabic) food.nameAr ?: food.name else food.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold) },
                    onClick = { onSelect(food.id); expanded = false },
                    trailingIcon = { if (food.id == selectedId) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary) }
                )
            }
        }
    }
}

@Composable
fun resolveErrorMessage(error: String): String {
    return when (error) {
        "error_connection" -> stringResource(Res.string.error_connection)
        "error_fill_fields" -> stringResource(Res.string.error_fill_fields)
        "error_id_mismatch" -> stringResource(Res.string.error_id_mismatch)
        "error_room_not_found" -> stringResource(Res.string.error_room_not_found)
        "reservation_success" -> stringResource(Res.string.reservation_success)
        "reservation_failed" -> stringResource(Res.string.reservation_failed)
        else -> error
    }
}
