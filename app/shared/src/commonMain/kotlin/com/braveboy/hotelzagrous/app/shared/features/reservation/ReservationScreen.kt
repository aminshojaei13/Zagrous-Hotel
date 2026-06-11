package com.braveboy.hotelzagrous.app.shared.features.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            Text("ورود")
        }
    }
}

@Composable
fun UserDashboard(state: ReservationState, viewModel: ReservationViewModel) {
    Column {
        Text("خوش آمدید، اتاق ${state.room?.roomNumber}", style = MaterialTheme.typography.titleLarge)
        Text("مسافر: ${state.room?.guestName}")
        Text("مدت اقامت: ${state.room?.checkInDate} تا ${state.room?.checkOutDate}")
        
        Spacer(Modifier.height(16.dp))
        
        val stayDays = listOf("1402/08/21", "1402/08/22", "1402/08/23")
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(stayDays) { date ->
                FoodRow(date, state, viewModel)
            }
        }
        
        if (state.error != null) {
             Text(state.error!!, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
        }

        Button(
            onClick = { viewModel.onIntent(ReservationIntent.ConfirmReservation) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("تایید نهایی رزروها")
            }
        }
    }
}

@Composable
fun FoodRow(date: String, state: ReservationState, viewModel: ReservationViewModel) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("تاریخ: $date", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FoodDropdown(
                    label = "نهار",
                    foods = state.availableFoods.filter { it.type == FoodType.LUNCH },
                    selectedId = state.tempReservations.find { it.date == date }?.lunchFoodId
                ) { foodId ->
                    viewModel.onIntent(ReservationIntent.ChangeFood(date, foodId, true))
                }
                
                FoodDropdown(
                    label = "شام",
                    foods = state.availableFoods.filter { it.type == FoodType.DINNER },
                    selectedId = state.tempReservations.find { it.date == date }?.dinnerFoodId
                ) { foodId ->
                    viewModel.onIntent(ReservationIntent.ChangeFood(date, foodId, false))
                }
            }
        }
    }
}

@Composable
fun FoodDropdown(label: String, foods: List<FoodItem>, selectedId: String?, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedFood = foods.find { it.id == selectedId }
    val buttonText = selectedFood?.name ?: "انتخاب $label"

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(buttonText)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
