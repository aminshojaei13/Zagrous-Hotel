package com.braveboy.hotelzagrous.app.shared.features.finance

import com.braveboy.hotelzagrous.core.*

data class FinanceState(
    val isLoading: Boolean = false,
    val report: FinancialReport = FinancialReport(0, 0, 0, 0, 0),
    val roomSummaries: List<RoomFinancialSummary> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val error: String? = null,
    val selectedRoomId: String? = null,
    val searchQuery: String = "",
    val typeFilter: TransactionType? = null
)

sealed class FinanceIntent {
    object LoadData : FinanceIntent()
    data class UpsertTransaction(val transaction: FinancialTransaction) : FinanceIntent()
    data class DeleteTransaction(val id: String) : FinanceIntent()
    data class SetSearchQuery(val query: String) : FinanceIntent()
    data class SetTypeFilter(val type: TransactionType?) : FinanceIntent()
    data class SelectRoom(val roomId: String?) : FinanceIntent()
}

sealed class FinanceEffect {
    data class ShowError(val message: String) : FinanceEffect()
    object TransactionSaved : FinanceEffect()
}
