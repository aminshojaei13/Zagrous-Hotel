package com.braveboy.hotelzagrous.app.shared.features.reservation

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.mvi.BaseViewModel
import com.braveboy.hotelzagrous.core.FoodReservation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) : BaseViewModel<ReservationState, ReservationIntent>(ReservationState()) {

    init {
        updateState { it.copy(availableFoods = repository.getAvailableFoods()) }
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
        val room = repository.getRoom(state.value.roomNumber)
        if (room != null) {
            updateState { it.copy(room = room, isLoggedIn = true, error = null) }
        } else {
            updateState { it.copy(error = "شماره اتاق یافت نشد") }
        }
    }

    private fun updateFoodSelection(intent: ReservationIntent.ChangeFood) {
        // قانون: تغییرات فقط تا قبل از ساعت 14 روز قبل مجاز است.
        // برای سادگی در این نسخه، فقط لیست موقت را در State بروزرسانی می‌کنیم.
        updateState { currentState ->
            val updated = currentState.tempReservations.toMutableList()
            val existing = updated.find { it.date == intent.date } ?: FoodReservation(currentState.roomNumber, intent.date)
            
            // جایگزینی آیتم قبلی
            updated.removeAll { it.date == intent.date }
            
            val newItem = if (intent.isLunch) {
                existing.copy(lunchFoodId = intent.foodId)
            } else {
                existing.copy(dinnerFoodId = intent.foodId)
            }
            
            updated.add(newItem)
            currentState.copy(tempReservations = updated)
        }
    }

    private fun submit() {
        scope.launch {
            updateState { it.copy(isLoading = true) }
            state.value.tempReservations.forEach { repository.saveReservation(it) }
            updateState { it.copy(isLoading = false, error = "رزرو با موفقیت ثبت شد") }
        }
    }
}
