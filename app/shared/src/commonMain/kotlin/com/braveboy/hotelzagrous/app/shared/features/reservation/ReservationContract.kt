package com.braveboy.hotelzagrous.app.shared.features.reservation

import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.MenuConfig
import com.braveboy.hotelzagrous.core.Room

data class ReservationState(
    val roomNumber: String = "",
    val identificationId: String = "",
    val room: Room? = null,
    val availableFoods: List<FoodItem> = emptyList(),
    val menuConfigs: List<MenuConfig> = emptyList(),
    val tempReservations: List<FoodReservation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val isArabic: Boolean = false
)

sealed class ReservationIntent {
    data class UpdateRoomNumber(val roomNumber: String) : ReservationIntent()
    data class UpdateIdentificationId(val identificationId: String) : ReservationIntent()
    object Login : ReservationIntent()
    data class ChangeFood(
        val date: String,
        val guestIndex: Int,
        val foodId: String?,
        val isLunch: Boolean
    ) : ReservationIntent()
    object ConfirmReservation : ReservationIntent()
    object ToggleLanguage : ReservationIntent()
}
