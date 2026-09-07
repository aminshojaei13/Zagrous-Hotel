@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package com.braveboy.hotelzagrous.app.shared.features.reservation

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import kotlinx.coroutines.*
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class ReservationWebBridge internal constructor(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) {
    private val viewModel = ReservationViewModel(repository, scope)

    fun getCurrentState(): ReservationStateJs = viewModel.state.value.toJs()

    fun subscribe(callback: (ReservationStateJs) -> Unit): () -> Unit {
        val job = scope.launch {
            viewModel.state.collect { state ->
                callback(state.toJs())
            }
        }
        return { job.cancel() }
    }

    fun updateCredentials(roomNumber: String, id: String) {
        viewModel.onIntent(ReservationIntent.UpdateRoomNumber(roomNumber))
        viewModel.onIntent(ReservationIntent.UpdateIdentificationId(id))
    }

    fun login() {
        viewModel.onIntent(ReservationIntent.Login)
    }

    private fun ReservationState.toJs() = ReservationStateJs(
        roomNumber = roomNumber,
        isLoading = isLoading,
        error = error,
        isLoggedIn = isLoggedIn
    )
}

@JsExport
data class ReservationStateJs(
    val roomNumber: String,
    val isLoading: Boolean,
    val error: String?,
    val isLoggedIn: Boolean
)

@JsExport
@JsName("createReservationBridge")
fun createReservationBridge(): ReservationWebBridge {
    return ReservationWebBridge(HotelRepository(), MainScope())
}
