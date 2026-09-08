@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package com.braveboy.hotelzagrous.app.shared.features.admin

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.core.*
import kotlinx.coroutines.*
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class AdminWebBridge internal constructor(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) {
    private val viewModel = AdminViewModel(repository, scope)

    fun getCurrentState(): AdminStateJs = viewModel.state.value.toJs()

    fun subscribe(callback: (AdminStateJs) -> Unit): () -> Unit {
        val job = scope.launch {
            viewModel.state.collect { state ->
                callback(state.toJs())
            }
        }
        return { job.cancel() }
    }

    fun loadData() {
        viewModel.onIntent(AdminIntent.LoadData)
    }

    fun addRoom(
        roomNumber: String,
        guestName: String,
        identificationId: String,
        guestCount: Int,
        hasBreakfast: Boolean,
        breakfastCount: Int,
        checkInDate: String,
        checkOutDate: String,
        checkInEpochMillis: Double,
        checkOutEpochMillis: Double
    ) {
        viewModel.onIntent(
            AdminIntent.AddRoom(
                Room(
                    roomNumber = roomNumber,
                    guestName = guestName,
                    identificationId = identificationId,
                    guestCount = guestCount,
                    hasBreakfast = hasBreakfast,
                    breakfastCount = breakfastCount,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    checkInEpochMillis = checkInEpochMillis.toLong(),
                    checkOutEpochMillis = checkOutEpochMillis.toLong()
                )
            )
        )
    }

    fun updateRoomStay(
        id: String,
        roomNumber: String,
        guestName: String,
        identificationId: String,
        checkInDate: String,
        checkOutDate: String,
        checkInEpochMillis: Double,
        checkOutEpochMillis: Double,
        guestCount: Int,
        hasBreakfast: Boolean,
        breakfastCount: Int
    ) {
        viewModel.onIntent(
            AdminIntent.UpdateRoomStay(
                id = id,
                roomNumber = roomNumber,
                guestName = guestName,
                identificationId = identificationId,
                checkIn = checkInDate,
                checkOut = checkOutDate,
                checkInMillis = checkInEpochMillis.toLong(),
                checkOutMillis = checkOutEpochMillis.toLong(),
                guestCount = guestCount,
                hasBreakfast = hasBreakfast,
                breakfastCount = breakfastCount
            )
        )
    }

    fun deleteRoom(id: String) {
        viewModel.onIntent(AdminIntent.DeleteRoom(id))
    }

    fun upsertFood(
        id: String,
        name: String,
        nameAr: String?,
        type: String,
        dayType: String,
        isActive: Boolean,
        isVisibleToUsers: Boolean,
        displayOrder: Int
    ) {
        viewModel.onIntent(
            AdminIntent.UpsertFood(
                FoodItem(
                    id = id,
                    name = name,
                    nameAr = nameAr,
                    type = FoodType.valueOf(type),
                    dayType = DayType.valueOf(dayType),
                    isActive = isActive,
                    isVisibleToUsers = isVisibleToUsers,
                    displayOrder = displayOrder
                )
            )
        )
    }

    fun deleteFood(id: String) {
        viewModel.onIntent(AdminIntent.DeleteFood(id))
    }

    fun updateMenuConfig(dayType: String, foodType: String, isEnabled: Boolean) {
        viewModel.onIntent(
            AdminIntent.UpdateMenuConfig(
                MenuConfig(
                    dayType = DayType.valueOf(dayType),
                    foodType = FoodType.valueOf(foodType),
                    isEnabled = isEnabled
                )
            )
        )
    }

    fun selectReportDate(date: String) {
        viewModel.onIntent(AdminIntent.SelectReportDate(date))
    }

    fun markLunchDelivered(roomNumber: String, guestIndex: Int, date: String) {
        viewModel.onIntent(AdminIntent.MarkLunchDelivered(roomNumber, guestIndex, date))
    }

    fun markDinnerDelivered(roomNumber: String, guestIndex: Int, date: String) {
        viewModel.onIntent(AdminIntent.MarkDinnerDelivered(roomNumber, guestIndex, date))
    }

    private fun AdminState.toJs() = AdminStateJs(
        rooms = rooms.map { it.toAdminJs() }.toTypedArray(),
        foods = foods.map { it.toAdminJs() }.toTypedArray(),
        menuConfigs = menuConfigs.map { it.toAdminJs() }.toTypedArray(),
        reservations = reservations.map { it.toAdminJs() }.toTypedArray(),
        dailyReservations = dailyReservations.map { it.toAdminJs() }.toTypedArray(),
        selectedReportDate = selectedReportDate,
        dailyReportSummary = dailyReportSummary.toJs(),
        isLoading = isLoading,
        error = error
    )

    private fun DailyReportSummary.toJs() = DailyReportSummaryJs(
        date = date,
        totalBreakfast = totalBreakfast,
        lunchOrders = lunchOrders.map { it.toJs() }.toTypedArray(),
        dinnerOrders = dinnerOrders.map { it.toJs() }.toTypedArray(),
        totalLunch = totalLunch,
        totalDinner = totalDinner
    )

    private fun DailyReportFoodItem.toJs() = DailyReportFoodItemJs(
        foodId = foodId,
        foodName = foodName,
        quantity = quantity
    )

    private fun FoodReservation.toAdminJs() = AdminReservationJs(
        roomNumber = roomNumber,
        date = date,
        guestMealSelections = guestMealSelections.map { it.toAdminJs() }.toTypedArray(),
        breakfastCount = breakfastCount
    )

    private fun GuestMealSelection.toAdminJs() = AdminGuestMealSelectionJs(
        guestIndex = guestIndex,
        lunchFoodId = lunchFoodId,
        dinnerFoodId = dinnerFoodId,
        lunchDelivered = lunchDelivered,
        dinnerDelivered = dinnerDelivered
    )

    private fun FoodItem.toAdminJs() = AdminFoodItemJs(
        id = id,
        name = name,
        nameAr = nameAr,
        type = type.name,
        dayType = dayType.name,
        isActive = isActive,
        isVisibleToUsers = isVisibleToUsers,
        displayOrder = displayOrder
    )

    private fun MenuConfig.toAdminJs() = AdminMenuConfigJs(
        dayType = dayType.name,
        foodType = foodType.name,
        isEnabled = isEnabled
    )

    private fun Room.toAdminJs() = AdminRoomJs(
        id = id,
        roomNumber = roomNumber,
        guestName = guestName,
        identificationId = identificationId,
        guestCount = guestCount,
        hasBreakfast = hasBreakfast,
        breakfastCount = breakfastCount,
        checkInDate = checkInDate,
        checkOutDate = checkOutDate,
        checkInEpochMillis = checkInEpochMillis.toDouble(),
        checkOutEpochMillis = checkOutEpochMillis.toDouble()
    )
}

@JsExport
data class AdminStateJs(
    val rooms: Array<AdminRoomJs>,
    val foods: Array<AdminFoodItemJs>,
    val menuConfigs: Array<AdminMenuConfigJs>,
    val reservations: Array<AdminReservationJs>,
    val dailyReservations: Array<AdminReservationJs>,
    val selectedReportDate: String,
    val dailyReportSummary: DailyReportSummaryJs,
    val isLoading: Boolean,
    val error: String?
)

@JsExport
data class DailyReportSummaryJs(
    val date: String,
    val totalBreakfast: Int,
    val lunchOrders: Array<DailyReportFoodItemJs>,
    val dinnerOrders: Array<DailyReportFoodItemJs>,
    val totalLunch: Int,
    val totalDinner: Int
)

@JsExport
data class DailyReportFoodItemJs(
    val foodId: String,
    val foodName: String,
    val quantity: Int
)

@JsExport
data class AdminReservationJs(
    val roomNumber: String,
    val date: String,
    val guestMealSelections: Array<AdminGuestMealSelectionJs>,
    val breakfastCount: Int
)

@JsExport
data class AdminGuestMealSelectionJs(
    val guestIndex: Int,
    val lunchFoodId: String?,
    val dinnerFoodId: String?,
    val lunchDelivered: Boolean,
    val dinnerDelivered: Boolean
)

@JsExport
data class AdminFoodItemJs(
    val id: String,
    val name: String,
    val nameAr: String?,
    val type: String,
    val dayType: String,
    val isActive: Boolean,
    val isVisibleToUsers: Boolean,
    val displayOrder: Int
)

@JsExport
data class AdminMenuConfigJs(
    val dayType: String,
    val foodType: String,
    val isEnabled: Boolean
)

@JsExport
data class AdminRoomJs(
    val id: String,
    val roomNumber: String,
    val guestName: String,
    val identificationId: String,
    val guestCount: Int,
    val hasBreakfast: Boolean,
    val breakfastCount: Int,
    val checkInDate: String,
    val checkOutDate: String,
    val checkInEpochMillis: Double,
    val checkOutEpochMillis: Double
)

@JsExport
@JsName("createAdminBridge")
fun createAdminBridge(): AdminWebBridge {
    return AdminWebBridge(HotelRepository(), MainScope())
}
