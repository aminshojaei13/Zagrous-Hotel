package com.braveboy.hotelzagrous.app.shared.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.braveboy.hotelzagrous.core.Room

@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("پنل مدیریت هتل زاگرس", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(Modifier.height(16.dp))
        
        Button(onClick = { viewModel.onIntent(AdminIntent.ExportPdf) }) {
            Text("خروجی PDF لیست غذاها")
        }

        Spacer(Modifier.height(16.dp))

        Text("مدیریت اتاق‌ها", style = MaterialTheme.typography.titleLarge)
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.rooms) { room ->
                RoomAdminCard(room) { checkIn, checkOut ->
                    viewModel.onIntent(AdminIntent.UpdateRoomStay(room.roomNumber, checkIn, checkOut))
                }
            }
        }
    }
}

@Composable
fun RoomAdminCard(room: Room, onUpdate: (String, String) -> Unit) {
    var checkIn by remember { mutableStateOf(room.checkInDate) }
    var checkOut by remember { mutableStateOf(room.checkOutDate) }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("اتاق ${room.roomNumber} - ${room.guestName}")
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = checkIn,
                    onValueChange = { checkIn = it },
                    label = { Text("ورود") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = checkOut,
                    onValueChange = { checkOut = it },
                    label = { Text("خروج") },
                    modifier = Modifier.weight(1f)
                )
            }
            Button(
                onClick = { onUpdate(checkIn, checkOut) },
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            ) {
                Text("به‌روزرسانی")
            }
        }
    }
}
