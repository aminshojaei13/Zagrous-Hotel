package com.braveboy.hotelzagrous.app.shared.features.admin

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.mvi.BaseViewModel
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.GuestMealSelection
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
            is AdminIntent.ChangeFood -> updateFoodSelection(intent)
            is AdminIntent.SelectRoomForFood -> updateState { it.copy(selectedRoom = intent.room) }
        }
    }

    private fun loadData() {
        // Only show full loading if we have no rooms yet
        if (state.value.rooms.isEmpty()) {
            updateState { it.copy(isLoading = true, error = null) }
        }
        scope.launch(Dispatchers.Main) {
            runCatching {
                val rooms = repository.getRooms()
                val reservations = repository.getAllReservations()
                val foods = repository.getAvailableFoods()
                Triple(rooms, reservations, foods)
            }.onSuccess { (rooms, reservations, foods) ->
                updateState { current -> 
                    current.copy(
                        isLoading = false,
                        rooms = rooms,
                        reservations = reservations,
                        foods = foods,
                        error = null,
                        selectedRoom = rooms.find { it.roomNumber == current.selectedRoom?.roomNumber }
                    ) 
                }
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
                    if (it.guestIndex == intent.guestIndex) {
                        it.copy(lunchDelivered = true)
                    } else it
                }
                val updatedRes = reservation.copy(guestMealSelections = updatedSelections)
                runCatching {
                    repository.saveReservation(updatedRes)
                }.onSuccess {
                    loadData()
                }.onFailure { e ->
                    println("xavi - saveReservation failure: ${e.message}")
                }
            }
        }
    }

    private fun markDinnerDelivered(intent: AdminIntent.MarkDinnerDelivered) {
        scope.launch(Dispatchers.Main) {
            val reservation = state.value.reservations.find { it.roomNumber == intent.roomNumber && it.date == intent.date }
            if (reservation != null) {
                val updatedSelections = reservation.guestMealSelections.map {
                    if (it.guestIndex == intent.guestIndex) {
                        it.copy(dinnerDelivered = true)
                    } else it
                }
                val updatedRes = reservation.copy(guestMealSelections = updatedSelections)
                runCatching {
                    repository.saveReservation(updatedRes)
                }.onSuccess {
                    loadData()
                }.onFailure { e ->
                    println("xavi - saveReservation failure: ${e.message}")
                }
            }
        }
    }

    private fun updateFoodSelection(intent: AdminIntent.ChangeFood) {
        scope.launch(Dispatchers.Main) {
            val reservation = state.value.reservations.find { it.roomNumber == intent.roomNumber && it.date == intent.date }
                ?: FoodReservation(intent.roomNumber, intent.date)

            val updatedSelections = reservation.guestMealSelections.toMutableList()
            val existingSelection = updatedSelections.find { it.guestIndex == intent.guestIndex }
                ?: GuestMealSelection(intent.guestIndex)

            updatedSelections.removeAll { it.guestIndex == intent.guestIndex }
            val newSelection = if (intent.isLunch) {
                existingSelection.copy(lunchFoodId = intent.foodId)
            } else {
                existingSelection.copy(dinnerFoodId = intent.foodId)
            }
            updatedSelections.add(newSelection)

            val updatedRes = reservation.copy(guestMealSelections = updatedSelections.sortedBy { it.guestIndex })
            
            runCatching {
                repository.saveReservation(updatedRes)
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { it.copy(error = "تغییر غذا با خطا مواجه شد: ${e.message}") }
            }
        }
    }

    private fun exportToPdf() {
        // In Web, we can use browser's print functionality
    }
}
