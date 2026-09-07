@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package com.braveboy.hotelzagrous.app.shared.features.reservation

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.core.*
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

    // Actions
    fun updateCredentials(roomNumber: String, id: String) {
        viewModel.onIntent(ReservationIntent.UpdateRoomNumber(roomNumber))
        viewModel.onIntent(ReservationIntent.UpdateIdentificationId(id))
    }

    fun login() {
        viewModel.onIntent(ReservationIntent.Login)
    }

    fun changeFood(date: String, guestIndex: Int, foodId: String?, foodType: String) {
        val type = if (foodType == "LUNCH") FoodType.LUNCH else FoodType.DINNER
        viewModel.onIntent(ReservationIntent.ChangeFood(date, guestIndex, foodId, type))
    }

    fun changeBreakfastCount(date: String, count: Int) {
        viewModel.onIntent(ReservationIntent.ChangeBreakfastCount(date, count))
    }

    fun confirmReservation() {
        viewModel.onIntent(ReservationIntent.ConfirmReservation)
    }

    fun toggleLanguage() {
        viewModel.onIntent(ReservationIntent.ToggleLanguage)
    }

    private fun ReservationState.toJs(): ReservationStateJs {
        val roomJs = room?.let {
            RoomJs(
                roomNumber = it.roomNumber,
                guestName = it.guestName,
                guestCount = it.guestCount,
                checkInDate = it.checkInDate,
                checkOutDate = it.checkOutDate
            )
        }

        val foodsJs = availableFoods.map {
            FoodItemJs(
                id = it.id,
                name = it.name,
                nameAr = it.nameAr,
                type = it.type.name,
                dayType = it.dayType.name,
                displayOrder = it.displayOrder
            )
        }.toTypedArray()

        val reservationsJs = tempReservations.map { res ->
            FoodReservationJs(
                date = res.date,
                breakfastCount = res.breakfastCount,
                guestMealSelections = res.guestMealSelections.map { sel ->
                    GuestMealSelectionJs(
                        guestIndex = sel.guestIndex,
                        lunchFoodId = sel.lunchFoodId,
                        dinnerFoodId = sel.dinnerFoodId
                    )
                }.toTypedArray()
            )
        }.toTypedArray()

        val configsJs = menuConfigs.map {
            MenuConfigJs(
                dayType = it.dayType.name,
                foodType = it.foodType.name,
                isEnabled = it.isEnabled
            )
        }.toTypedArray()

        // Calculate stay days logic from Compose UI
        val days = mutableListOf<String>()
        if (room != null && room.checkInEpochMillis != 0L && room.checkOutEpochMillis != 0L) {
            var currentMillis = room.checkInEpochMillis
            while (currentMillis <= room.checkOutEpochMillis) {
                days.add(DateUtils.convertMillisToJalaliString(currentMillis))
                currentMillis += 24 * 60 * 60 * 1000L
            }

            if (days.size > 1) {
                val firstDay = days.first()
                val firstDayReservation = tempReservations.find { it.date == firstDay }
                val isFirstDayLunchSelected =
                    firstDayReservation?.guestMealSelections?.any { it.lunchFoodId != null } == true
                if (isFirstDayLunchSelected) {
                    days.removeLast()
                }
            }
        }

        return ReservationStateJs(
            roomNumber = roomNumber,
            identificationId = identificationId,
            room = roomJs,
            availableFoods = foodsJs,
            menuConfigs = configsJs,
            tempReservations = reservationsJs,
            stayDays = days.toTypedArray(),
            isLoading = isLoading,
            error = error,
            isLoggedIn = isLoggedIn,
            isArabic = isArabic
        )
    }
}

@JsExport
data class ReservationStateJs(
    val roomNumber: String,
    val identificationId: String,
    val room: RoomJs?,
    val availableFoods: Array<FoodItemJs>,
    val menuConfigs: Array<MenuConfigJs>,
    val tempReservations: Array<FoodReservationJs>,
    val stayDays: Array<String>,
    val isLoading: Boolean,
    val error: String?,
    val isLoggedIn: Boolean,
    val isArabic: Boolean
)

@JsExport
data class RoomJs(
    val roomNumber: String,
    val guestName: String,
    val guestCount: Int,
    val checkInDate: String,
    val checkOutDate: String
)

@JsExport
data class FoodItemJs(
    val id: String,
    val name: String,
    val nameAr: String?,
    val type: String,
    val dayType: String,
    val displayOrder: Int
)

@JsExport
data class MenuConfigJs(
    val dayType: String,
    val foodType: String,
    val isEnabled: Boolean
)

@JsExport
data class FoodReservationJs(
    val date: String,
    val guestMealSelections: Array<GuestMealSelectionJs>,
    val breakfastCount: Int
)

@JsExport
data class GuestMealSelectionJs(
    val guestIndex: Int,
    val lunchFoodId: String?,
    val dinnerFoodId: String?
)

@JsExport
@JsName("createReservationBridge")
fun createReservationBridge(): ReservationWebBridge {
    return ReservationWebBridge(HotelRepository(), MainScope())
}
