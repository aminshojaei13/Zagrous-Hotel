package com.braveboy.hotelzagrous.app.shared.data

import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodType
import com.braveboy.hotelzagrous.core.FoodReservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HotelRepository {
    private val _rooms = MutableStateFlow(listOf(
        Room("101", "رضا احمدی", "1402/08/01", "1402/08/05", 1698823800000L, 1699169400000L),
        Room("102", "مریم علوی", "1402/08/02", "1402/08/06", 1698910200000L, 1699255800000L),
        Room("103", "محمد محمدی", "1402/08/05", "1402/08/10", 1699169400000L, 1699601400000L)
    ))
    
    private val _foods = listOf(
        FoodItem("1", "چلو کباب", FoodType.LUNCH),
        FoodItem("2", "جوجه کباب", FoodType.LUNCH),
        FoodItem("3", "خورشت قیمه", FoodType.LUNCH),
        FoodItem("4", "پیتزا مخصوص", FoodType.DINNER),
        FoodItem("5", "خوراک مرغ", FoodType.DINNER),
        FoodItem("6", "سوپ جو", FoodType.DINNER)
    )

    private val _reservations = MutableStateFlow<List<FoodReservation>>(emptyList())

    fun getRoom(roomNumber: String): Room? = _rooms.value.find { it.roomNumber == roomNumber }
    fun getRooms(): List<Room> = _rooms.value
    fun getAvailableFoods(): List<FoodItem> = _foods
    fun getAllReservations(): StateFlow<List<FoodReservation>> = _reservations

    fun saveReservation(reservation: FoodReservation) {
        val current = _reservations.value.toMutableList()
        current.removeAll { it.roomNumber == reservation.roomNumber && it.date == reservation.date }
        current.add(reservation)
        _reservations.value = current
    }

    fun addRoom(room: Room) {
        val current = _rooms.value.toMutableList()
        if (current.none { it.roomNumber == room.roomNumber }) {
            current.add(room)
            _rooms.value = current
        }
    }

    fun updateRoomStay(roomNumber: String, checkIn: String, checkOut: String, checkInMillis: Long, checkOutMillis: Long) {
        val current = _rooms.value.toMutableList()
        val index = current.indexOfFirst { it.roomNumber == roomNumber }
        if (index != -1) {
            current[index] = current[index].copy(
                checkInDate = checkIn, 
                checkOutDate = checkOut,
                checkInEpochMillis = checkInMillis,
                checkOutEpochMillis = checkOutMillis
            )
            _rooms.value = current
        }
    }
}
