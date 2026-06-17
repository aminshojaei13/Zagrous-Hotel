package com.braveboy.hotelzagrous.app.shared.features.admin

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.mvi.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) : BaseViewModel<AdminState, AdminIntent>(AdminState()) {

    init {
        onIntent(AdminIntent.LoadData)
    }

    override fun onIntent(intent: AdminIntent) {
        when (intent) {
            is AdminIntent.LoadData -> loadData()
            is AdminIntent.UpdateRoomStay -> updateRoom(intent)
            is AdminIntent.AddRoom -> addRoom(intent)
            is AdminIntent.ExportPdf -> exportToPdf()
            is AdminIntent.ClearAllData -> clearAllData()
            is AdminIntent.MarkLunchDelivered -> markLunchDelivered(intent)
            is AdminIntent.MarkDinnerDelivered -> markDinnerDelivered(intent)
        }
    }

    private fun loadData() {
        updateState { it.copy(isLoading = true, error = null) }
        scope.launch(Dispatchers.Main) {
            runCatching {
                val rooms = repository.getRooms()
                val reservations = repository.getAllReservations()
                val foods = repository.getAvailableFoods()
                Triple(rooms, reservations, foods)
            }.onSuccess { (rooms, reservations, foods) ->
                println("xavi is $reservations")
                updateState { it.copy(
                    isLoading = false,
                    rooms = rooms,
                    reservations = reservations,
                    foods = foods,
                    error = null
                ) }
            }.onFailure { e ->
                updateState { it.copy(isLoading = false, error = "خطا در بارگذاری: ${e.message ?: "ارتباط با سرور برقرار نشد"}") }
            }
        }
    }

    private fun updateRoom(intent: AdminIntent.UpdateRoomStay) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.updateRoomStay(
                    intent.roomNumber,
                    intent.checkIn,
                    intent.checkOut,
                    intent.checkInMillis,
                    intent.checkOutMillis,
                    intent.guestCount
                )
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { current -> current.copy(error = "به‌روزرسانی اتاق انجام نشد: ${e.message}") }
            }
        }
    }

    private fun addRoom(intent: AdminIntent.AddRoom) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.addRoom(intent.room)
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { current -> current.copy(error = "افزودن اتاق انجام نشد: ${e.message}") }
            }
        }
    }

    private fun clearAllData() {
        updateState { it.copy(isLoading = true) }
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.clearAllData()
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { it.copy(isLoading = false, error = "حذف اطلاعات با خطا مواجه شد: ${e.message}") }
            }
        }
    }

    private fun markLunchDelivered(intent: AdminIntent.MarkLunchDelivered) {
        scope.launch(Dispatchers.Main) {
            val reservation = state.value.reservations.find { it.roomNumber == intent.roomNumber && it.date == intent.date }
            if (reservation != null) {
                val updatedSelections = reservation.guestMealSelections.map {
                    if (it.guestIndex == intent.guestIndex) it.copy(lunchDelivered = true) else it
                }
                val updatedRes = reservation.copy(guestMealSelections = updatedSelections)
                runCatching {
                    repository.saveReservation(updatedRes)
                }.onSuccess {
                    loadData()
                }
            }
        }
    }

    private fun markDinnerDelivered(intent: AdminIntent.MarkDinnerDelivered) {
        scope.launch(Dispatchers.Main) {
            val reservation = state.value.reservations.find { it.roomNumber == intent.roomNumber && it.date == intent.date }
            if (reservation != null) {
                val updatedSelections = reservation.guestMealSelections.map {
                    if (it.guestIndex == intent.guestIndex) it.copy(dinnerDelivered = true) else it
                }
                val updatedRes = reservation.copy(guestMealSelections = updatedSelections)
                runCatching {
                    repository.saveReservation(updatedRes)
                }.onSuccess {
                    loadData()
                }
            }
        }
    }

    private fun exportToPdf() {
        // In Web, we can use browser's print functionality
    }
}
