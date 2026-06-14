package com.braveboy.hotelzagrous.app.shared.features.reservation

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.mvi.BaseViewModel
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.GuestMealSelection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) : BaseViewModel<ReservationState, ReservationIntent>(ReservationState()) {

    init {
        scope.launch {
            runCatching {
                repository.getAvailableFoods()
            }.onSuccess { foods ->
                updateState { it.copy(availableFoods = foods, error = null) }
            }.onFailure {
                updateState { it.copy(error = "ارتباط با سرور برقرار نشد") }
            }
        }
    }

    override fun onIntent(intent: ReservationIntent) {
        when (intent) {
            is ReservationIntent.UpdateRoomNumber -> updateState { it.copy(roomNumber = intent.roomNumber) }
            is ReservationIntent.Login -> login()
            is ReservationIntent.ChangeFood -> updateFoodSelection(intent)
            is ReservationIntent.ConfirmReservation -> submit()
        }
    }

    private fun login() {
        scope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            runCatching {
                val room = repository.getRoom(state.value.roomNumber)
                val reservations = if (room != null) repository.getReservationsForRoom(room.roomNumber) else emptyList()
                room to reservations
            }.onSuccess { (room, reservations) ->
                if (room != null) {
                    updateState { it.copy(
                        room = room,
                        tempReservations = reservations,
                        isLoggedIn = true,
                        isLoading = false,
                        error = null
                    ) }
                } else {
                    updateState { it.copy(isLoading = false, error = "شماره اتاق یافت نشد") }
                }
            }.onFailure { e ->
                updateState { it.copy(isLoading = false, error = "ارتباط با سرور برقرار نشد: ${e.message}") }
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
                updateState { it.copy(isLoading = false, error = "رزرو با موفقیت ثبت شد") }
            }.onFailure {
                updateState { it.copy(isLoading = false, error = "ثبت رزرو انجام نشد") }
            }
        }
    }
}
