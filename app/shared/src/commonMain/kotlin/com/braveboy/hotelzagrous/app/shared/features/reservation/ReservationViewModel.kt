package com.braveboy.hotelzagrous.app.shared.features.reservation

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.mvi.BaseViewModel
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.DayType
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodType
import com.braveboy.hotelzagrous.core.GuestMealSelection
import com.braveboy.hotelzagrous.core.MenuConfig
import com.braveboy.hotelzagrous.core.normalizeDigits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import hotelzagrous.app.shared.generated.resources.Res
import hotelzagrous.app.shared.generated.resources.*

class ReservationViewModel(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) : BaseViewModel<ReservationState, ReservationIntent>(ReservationState()) {

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        scope.launch {
            runCatching {
                val foods = repository.getAvailableFoods()
                val configs = repository.getMenuConfigs()
                foods to configs
            }.onSuccess { (foods, configs) ->
                updateState { it.copy(availableFoods = foods, menuConfigs = configs, error = null) }
            }.onFailure {
                // خطا در لود اولیه
                updateState { it.copy(error = "error_connection") }
            }
        }
    }

    override fun onIntent(intent: ReservationIntent) {
        when (intent) {
            is ReservationIntent.UpdateRoomNumber -> updateState { it.copy(roomNumber = intent.roomNumber) }
            is ReservationIntent.UpdateIdentificationId -> updateState { it.copy(identificationId = intent.identificationId) }
            is ReservationIntent.Login -> login()
            is ReservationIntent.ChangeFood -> updateFoodSelection(intent)
            is ReservationIntent.ConfirmReservation -> submit()
            is ReservationIntent.ToggleLanguage -> updateState { it.copy(isArabic = !it.isArabic) }
        }
    }

    private fun login() {
        val currentRoomNumber = state.value.roomNumber.normalizeDigits()
        val currentIdentificationId = state.value.identificationId.normalizeDigits()

        if (currentRoomNumber.isBlank() || currentIdentificationId.isBlank()) {
            updateState { it.copy(error = "error_fill_fields") }
            return
        }

        scope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            runCatching {
                val room = repository.getRoom(currentRoomNumber)
                val reservations = if (room != null) repository.getReservationsForRoom(room.roomNumber) else emptyList()
                room to reservations
            }.onSuccess { (room, reservations) ->
                if (room != null) {
                    if (room.identificationId == currentIdentificationId) {
                        updateState { it.copy(
                            room = room,
                            tempReservations = reservations,
                            isLoggedIn = true,
                            isLoading = false,
                            error = null
                        ) }
                    } else {
                        updateState { it.copy(isLoading = false, error = "error_id_mismatch") }
                    }
                } else {
                    updateState { it.copy(isLoading = false, error = "error_room_not_found") }
                }
            }.onFailure { e ->
                updateState { it.copy(isLoading = false, error = "error_connection") }
            }
        }
    }

    private fun updateFoodSelection(intent: ReservationIntent.ChangeFood) {
        updateState { currentState ->
            val updatedTemp = currentState.tempReservations.toMutableList()
            val existingRes = updatedTemp.find { it.date == intent.date } 
                ?: FoodReservation(currentState.roomNumber, intent.date)
            
            val updatedSelections = existingRes.guestMealSelections.toMutableList()
            val existingSelection = updatedSelections.find { it.guestIndex == intent.guestIndex }
                ?: GuestMealSelection(intent.guestIndex)
            
            updatedSelections.removeAll { it.guestIndex == intent.guestIndex }
            val newSelection = if (intent.isLunch) {
                existingSelection.copy(lunchFoodId = intent.foodId)
            } else {
                existingSelection.copy(dinnerFoodId = intent.foodId)
            }
            updatedSelections.add(newSelection)
            
            val newRes = existingRes.copy(guestMealSelections = updatedSelections.sortedBy { it.guestIndex })
            
            updatedTemp.removeAll { it.date == intent.date }
            updatedTemp.add(newRes)
            
            currentState.copy(tempReservations = updatedTemp)
        }
    }

    private fun submit() {
        scope.launch {
            updateState { it.copy(isLoading = true) }
            runCatching {
                state.value.tempReservations.forEach { repository.saveReservation(it) }
            }.onSuccess {
                updateState { it.copy(isLoading = false, error = "reservation_success") }
            }.onFailure {
                updateState { it.copy(isLoading = false, error = "reservation_failed") }
            }
        }
    }
}
