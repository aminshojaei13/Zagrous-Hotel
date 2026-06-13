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
        }
    }

    private fun loadData() {
        updateState { it.copy(isLoading = true) }
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.getRooms() to repository.getAllReservations()
            }.onSuccess { (rooms, reservations) ->
                updateState { it.copy(isLoading = false, rooms = rooms, reservations = reservations, error = null) }
            }.onFailure {
                updateState { it.copy(isLoading = false, error = "ارتباط با سرور برقرار نشد") }
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
                    intent.checkOutMillis
                )
            }.onSuccess {
                loadData()
            }.onFailure {
                updateState { current -> current.copy(error = "به‌روزرسانی اتاق انجام نشد") }
            }
        }
    }

    private fun addRoom(intent: AdminIntent.AddRoom) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.addRoom(intent.room)
            }.onSuccess {
                loadData()
            }.onFailure {
                updateState { current -> current.copy(error = "افزودن اتاق انجام نشد") }
            }
        }
    }

    private fun exportToPdf() {
        // In Web, we can use browser's print functionality
    }
}
