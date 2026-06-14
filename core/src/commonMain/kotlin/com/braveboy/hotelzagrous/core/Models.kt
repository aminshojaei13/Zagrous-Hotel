package com.braveboy.hotelzagrous.core

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val roomNumber: String,
    val guestName: String = "",
    val guestCount: Int = 1,
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val checkInEpochMillis: Long = 0,
    val checkOutEpochMillis: Long = 0
)

@Serializable
data class FoodItem(
    val id: String,
    val name: String,
    val type: FoodType
)

@Serializable
enum class FoodType { LUNCH, DINNER }

@Serializable
data class FoodReservation(
    val roomNumber: String,
    val date: String,
    val guestMealSelections: List<GuestMealSelection> = emptyList()
)

@Serializable
data class GuestMealSelection(
    val guestIndex: Int,
    val lunchFoodId: String? = null,
    val dinnerFoodId: String? = null
)

@Serializable
data class UpdateRoomStayRequest(
    val checkIn: String,
    val checkOut: String,
    val checkInMillis: Long,
    val checkOutMillis: Long,
    val guestCount: Int
)

@Serializable
data class ApiError(val message: String)
