package com.braveboy.hotelzagrous.core

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val roomNumber: String,
    val guestName: String = "",
    val identificationId: String = "",
    val guestCount: Int = 1,
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val checkInEpochMillis: Long = 0,
    val checkOutEpochMillis: Long = 0
)

@Serializable
enum class FoodType { LUNCH, DINNER }

@Serializable
enum class DayType { EVEN, ODD, FRIDAY }

@Serializable
data class FoodItem(
    val id: String = "",
    val name: String,
    val type: FoodType,
    val dayType: DayType,
    val isActive: Boolean = true,
    val isVisibleToUsers: Boolean = true,
    val displayOrder: Int = 0
)

@Serializable
data class MenuConfig(
    val dayType: DayType,
    val foodType: FoodType,
    val isEnabled: Boolean = true
)

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
    val dinnerFoodId: String? = null,
    val lunchDelivered: Boolean = false,
    val dinnerDelivered: Boolean = false
)

@Serializable
data class UpdateRoomStayRequest(
    val guestName: String,
    val identificationId: String,
    val checkIn: String,
    val checkOut: String,
    val checkInMillis: Long,
    val checkOutMillis: Long,
    val guestCount: Int
)

@Serializable
data class ApiError(val message: String)
