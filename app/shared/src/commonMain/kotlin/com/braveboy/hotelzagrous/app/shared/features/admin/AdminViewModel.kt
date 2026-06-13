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
            val rooms = repository.getRooms()
            updateState { it.copy(isLoading = false, rooms = rooms) } 
        }
    }

    private fun updateRoom(intent: AdminIntent.UpdateRoomStay) {
        repository.updateRoomStay(intent.roomNumber, intent.checkIn, intent.checkOut)
        loadData()
    }

    private fun addRoom(intent: AdminIntent.AddRoom) {
        repository.addRoom(intent.room)
        loadData()
    }

    private fun exportToPdf() {
        // In Web, we can use browser's print functionality
    }
}
