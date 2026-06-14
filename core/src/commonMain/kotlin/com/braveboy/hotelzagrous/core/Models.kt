package com.braveboy.hotelzagrous.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Room(
    @SerialName("room_number") val roomNumber: String,
    @SerialName("guest_name") val guestName: String = "",
    @SerialName("guest_count") val guestCount: Int = 1,
    @SerialName("check_in_date") val checkInDate: String = "",
    @SerialName("check_out_date") val checkOutDate: String = "",
    @SerialName("check_in_epoch_millis") val checkInEpochMillis: Long = 0,
    @SerialName("check_out_epoch_millis") val checkOutEpochMillis: Long = 0
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
    @SerialName("room_number") val roomNumber: String,
    val date: String,
    @SerialName("guest_meal_selections") val guestMealSelections: List<GuestMealSelection> = emptyList()
)

@Serializable
data class GuestMealSelection(
    @SerialName("guest_index") val guestIndex: Int,
    @SerialName("lunch_food_id") val lunchFoodId: String? = null,
    @SerialName("dinner_food_id") val dinnerFoodId: String? = null
)

@Serializable
data class UpdateRoomStayRequest(
    @SerialName("check_in") val checkIn: String,
    @SerialName("check_out") val checkOut: String,
    @SerialName("check_in_millis") val checkInMillis: Long,
    @SerialName("check_out_millis") val checkOutMillis: Long,
    @SerialName("guest_count") val guestCount: Int
)

@Serializable
data class ApiError(val message: String)
