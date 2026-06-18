package com.braveboy.hotelzagrous.app.shared.features.admin

import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.Room

data class AdminState(
    val rooms: List<Room> = emptyList(),
    val reservations: List<FoodReservation> = emptyList(),
    val foods: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedRoom: Room? = null,
    val error: String? = null
)

sealed class AdminIntent {
    data class UpdateRoomStay(
        val roomNumber: String, 
        val checkIn: String, 
        val checkOut: String,
        val checkInMillis: Long,
        val checkOutMillis: Long,
        val guestCount: Int
    ) : AdminIntent()
    data class AddRoom(val room: Room) : AdminIntent()
    object ExportPdf : AdminIntent()
    object LoadData : AdminIntent()
    object ClearAllData : AdminIntent()
    
    data class MarkLunchDelivered(
        val roomNumber: String,
        val guestIndex: Int,
        val date: String
    ) : AdminIntent()

    data class MarkDinnerDelivered(
        val roomNumber: String,
        val guestIndex: Int,
        val date: String
    ) : AdminIntent()

    data class ChangeFood(
        val roomNumber: String,
        val date: String,
        val guestIndex: Int,
        val foodId: String?,
        val isLunch: Boolean
    ) : AdminIntent()

    data class SelectRoomForFood(val room: Room?) : AdminIntent()
}
