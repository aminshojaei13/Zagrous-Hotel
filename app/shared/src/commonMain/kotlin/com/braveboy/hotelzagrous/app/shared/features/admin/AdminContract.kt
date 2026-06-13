package com.braveboy.hotelzagrous.app.shared.features.admin

import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.Room

data class AdminState(
    val rooms: List<Room> = emptyList(),
    val reservations: List<FoodReservation> = emptyList(),
    val isLoading: Boolean = false,
    val selectedRoom: Room? = null
)

sealed class AdminIntent {
    data class UpdateRoomStay(
        val roomNumber: String, 
        val checkIn: String, 
        val checkOut: String,
        val checkInMillis: Long,
        val checkOutMillis: Long
    ) : AdminIntent()
    data class AddRoom(val room: Room) : AdminIntent()
    object ExportPdf : AdminIntent()
    object LoadData : AdminIntent()
}
