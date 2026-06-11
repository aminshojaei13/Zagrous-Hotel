package com.braveboy.hotelzagrous

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.features.admin.AdminScreen
import com.braveboy.hotelzagrous.app.shared.features.admin.AdminViewModel
import com.braveboy.hotelzagrous.app.shared.features.reservation.ReservationScreen
import com.braveboy.hotelzagrous.app.shared.features.reservation.ReservationViewModel

@Composable
fun App() {
    val repository = remember { HotelRepository() }
    val scope = rememberCoroutineScope()
    
    // وضعیت برای جابجایی بین پنل مسافر و مدیریت
    var selectedTab by remember { mutableStateOf(0) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("رزرو غذا (مسافر)") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("مدیریت هتل") }
                    )
                }

                when (selectedTab) {
                    0 -> {
                        val resViewModel = remember { ReservationViewModel(repository, scope) }
                        ReservationScreen(resViewModel)
                    }
                    1 -> {
                        val adminViewModel = remember { AdminViewModel(repository, scope) }
                        AdminScreen(adminViewModel)
                    }
                }
            }
        }
    }
}
