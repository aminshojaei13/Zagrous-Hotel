package com.braveboy.hotelzagrous.app.shared.features.finance

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.mvi.BaseViewModel
import com.braveboy.hotelzagrous.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinanceViewModel(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) : BaseViewModel<FinanceState, FinanceIntent>(FinanceState()) {

    init {
        onIntent(FinanceIntent.LoadData)
    }

    override fun onIntent(intent: FinanceIntent) {
        when (intent) {
            is FinanceIntent.LoadData -> loadData()
            is FinanceIntent.UpsertTransaction -> upsertTransaction(intent.transaction)
            is FinanceIntent.DeleteTransaction -> deleteTransaction(intent.id)
            is FinanceIntent.SetSearchQuery -> updateState { it.copy(searchQuery = intent.query) }
            is FinanceIntent.SetTypeFilter -> updateState { it.copy(typeFilter = intent.type) }
            is FinanceIntent.SelectRoom -> updateState { it.copy(selectedRoomId = intent.roomId) }
        }
    }

    private fun loadData() {
        scope.launch(Dispatchers.Main) {
            updateState { it.copy(isLoading = true) }
            runCatching {
                val (report, summaries) = repository.getFinancialSummary()
                val rooms = repository.getRooms()
                report to (summaries to rooms)
            }.onSuccess { (report, pair) ->
                val (summaries, rooms) = pair
                updateState { it.copy(
                    isLoading = false,
                    report = report,
                    roomSummaries = summaries,
                    rooms = rooms,
                    error = null
                ) }
            }.onFailure { e ->
                updateState { it.copy(isLoading = false, error = e.message ?: "خطا در بارگذاری اطلاعات") }
            }
        }
    }

    private fun upsertTransaction(tx: FinancialTransaction) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.upsertTransaction(tx)
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { it.copy(error = e.message ?: "خطا در ذخیره تراکنش") }
            }
        }
    }

    private fun deleteTransaction(id: String) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.deleteTransaction(id)
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { it.copy(error = e.message ?: "خطا در حذف تراکنش") }
            }
        }
    }
}
