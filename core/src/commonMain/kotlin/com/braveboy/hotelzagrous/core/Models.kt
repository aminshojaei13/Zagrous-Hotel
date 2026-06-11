package com.braveboy.hotelzagrous.core

data class Room(
    val roomNumber: String,
    val guestName: String = "",
    val checkInDate: String = "",
    val checkOutDate: String = ""
)

data class FoodItem(
    val id: String,
    val name: String,
    val type: FoodType
)

enum class FoodType { LUNCH, DINNER }

data class FoodReservation(
    val roomNumber: String,
    val date: String,
    val lunchFoodId: String? = null,
    val dinnerFoodId: String? = null
)
