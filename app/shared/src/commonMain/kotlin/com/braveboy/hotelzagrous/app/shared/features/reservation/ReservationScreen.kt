package com.braveboy.hotelzagrous.app.shared.features.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodType

@Composable
fun ReservationScreen(viewModel: ReservationViewModel) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (!state.isLoggedIn) {
            LoginSection(
                number = state.roomNumber,
                onNumberChange = { viewModel.onIntent(ReservationIntent.UpdateRoomNumber(it)) },
                onLogin = { viewModel.onIntent(ReservationIntent.Login) },
                error = state.error
            )
        } else {
            UserDashboard(state, viewModel)
        }
    }
}

@Composable
fun LoginSection(number: String, onNumberChange: (String) -> Unit, onLogin: () -> Unit, error: String?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("ورود به سامانه رزرو غذا هتل زاگرس", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = number,
            onValueChange = onNumberChange,
            label = { Text("شماره اتاق") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        Button(onClick = onLogin, modifier = Modifier.padding(top = 16.dp)) {
            if (number.isBlank()) {
                Text("ورود")
            } else {
                Text("ورود به اتاق $number")
            }
        }
    }
}

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
        } else {
            emptyList()
        }
    }

    Column {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("خوش آمدید، اتاق ${room?.roomNumber}", style = MaterialTheme.typography.titleLarge)
                Text("نام مسافر: ${room?.guestName}")
                Text("تعداد نفرات: ${room?.guestCount} نفر")
                Text("مدت اقامت: ${room?.checkInDate} تا ${room?.checkOutDate}")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(stayDays) { date ->
                FoodRow(date, state, viewModel)
            }
        }
        
        state.error?.let {
             Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
        }

        Button(
            onClick = { viewModel.onIntent(ReservationIntent.ConfirmReservation) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("تایید نهایی رزروها")
            }
        }
    }
}

@Composable
fun FoodRow(date: String, state: ReservationState, viewModel: ReservationViewModel) {
    val guestCount = state.room?.guestCount ?: 1
    val reservation = state.tempReservations.find { it.date == date }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("تاریخ: $date", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            
            repeat(guestCount) { index ->
                val guestSelection = reservation?.guestMealSelections?.find { it.guestIndex == index }
                
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("مهمان ${index + 1}:", style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FoodDropdown(
                            label = "ناهار",
                            foods = state.availableFoods.filter { it.type == FoodType.LUNCH },
                            selectedId = guestSelection?.lunchFoodId,
                            modifier = Modifier.weight(1f)
                        ) { foodId ->
                            viewModel.onIntent(ReservationIntent.ChangeFood(date, index, foodId, true))
                        }
                        
                        FoodDropdown(
                            label = "شام",
                            foods = state.availableFoods.filter { it.type == FoodType.DINNER },
                            selectedId = guestSelection?.dinnerFoodId,
                            modifier = Modifier.weight(1f)
                        ) { foodId ->
                            viewModel.onIntent(ReservationIntent.ChangeFood(date, index, foodId, false))
                        }
                    }
                }
                if (index < guestCount - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun FoodDropdown(
    label: String, 
    foods: List<FoodItem>, 
    selectedId: String?, 
    modifier: Modifier = Modifier,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedFood = foods.find { it.id == selectedId }
    val buttonText = selectedFood?.name ?: "انتخاب $label"

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(buttonText, maxLines = 1)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("هیچکدام") },
                onClick = {
                    onSelect(null)
                    expanded = false
                }
            )
            foods.forEach { food ->
                DropdownMenuItem(
                    text = { Text(food.name) },
                    onClick = {
                        onSelect(food.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
