package com.braveboy.hotelzagrous.app.shared.features.admin

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.mvi.BaseViewModel
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.GuestMealSelection
import com.braveboy.hotelzagrous.core.MenuConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Clock

class AdminViewModel(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) : BaseViewModel<AdminState, AdminIntent>(AdminState()) {

    init {
        val today = DateUtils.convertMillisToJalaliString(Clock.System.now().toEpochMilliseconds())
        val parts = today.split("/")
        if (parts.size == 3) {
            updateState { it.copy(calendarYear = parts[0].toInt(), calendarMonth = parts[1].toInt()) }
        }
        onIntent(AdminIntent.LoadData)
    }

    override fun onIntent(intent: AdminIntent) {
        when (intent) {
            is AdminIntent.LoadData -> loadData()
            is AdminIntent.UpdateRoomStay -> updateRoom(intent)
            is AdminIntent.AddRoom -> addRoom(intent)
            is AdminIntent.DeleteRoom -> deleteRoom(intent.roomNumber)
            is AdminIntent.ExportPdf -> exportToPdf()
            is AdminIntent.ClearAllData -> clearAllData()
            is AdminIntent.MarkLunchDelivered -> markLunchDelivered(intent)
            is AdminIntent.MarkDinnerDelivered -> markDinnerDelivered(intent)
            is AdminIntent.ChangeFood -> updateFoodSelection(intent)
            is AdminIntent.SelectRoomForFood -> updateState { it.copy(selectedRoom = intent.room) }
            is AdminIntent.SelectReportDate -> updateState { it.copy(selectedReportDate = intent.date) }
            is AdminIntent.SelectCapacityFilter -> updateState { it.copy(selectedCapacity = intent.capacity) }
            is AdminIntent.ChangeCalendarDate -> updateState { it.copy(calendarMonth = intent.month, calendarYear = intent.year) }
            is AdminIntent.UpsertFood -> upsertFood(intent.food)
            is AdminIntent.DeleteFood -> deleteFood(intent.id)
            is AdminIntent.UpdateMenuConfig -> updateMenuConfig(intent.config)
            is AdminIntent.PrintDailyDinnerReport -> printDailyReport(intent.date, isLunch = false)
            is AdminIntent.PrintDailyLaunchReport -> printDailyReport(intent.date, isLunch = true)
        }
    }

    private fun loadData() {
        if (state.value.rooms.isEmpty() && state.value.foods.isEmpty()) {
            updateState { it.copy(isLoading = true, error = null) }
        }
        scope.launch(Dispatchers.Main) {
            runCatching {
                val rooms = repository.getRooms()
                val reservations = repository.getAllReservations()
                val foods = repository.getAvailableFoods()
                val menuConfigs = repository.getMenuConfigs()
                Triple(rooms, Triple(reservations, foods, menuConfigs), Unit)
            }.onSuccess { (rooms, data, _) ->
                val (reservations, foods, menuConfigs) = data
                updateState { current ->
                    current.copy(
                        isLoading = false,
                        rooms = rooms,
                        reservations = reservations,
                        foods = foods,
                        menuConfigs = menuConfigs,
                        error = null,
                        selectedRoom = rooms.find { it.roomNumber == current.selectedRoom?.roomNumber }
                    )
                }
            }.onFailure { e ->
                updateState {
                    it.copy(
                        isLoading = false,
                        error = "خطا در بارگذاری: ${e.message ?: "ارتباط با سرور برقرار نشد"}"
                    )
                }
            }
        }
    }

    private fun upsertFood(food: FoodItem) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.upsertFood(food)
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { it.copy(error = "خطا در ذخیره غذا: ${e.message}") }
            }
        }
    }

    private fun deleteFood(id: String) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.deleteFood(id)
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { it.copy(error = "خطا در حذف غذا: ${e.message}") }
            }
        }
    }

    private fun updateMenuConfig(config: MenuConfig) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.upsertMenuConfig(config)
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { it.copy(error = "خطا در تنظیمات منو: ${e.message}") }
            }
        }
    }

    private fun updateRoom(intent: AdminIntent.UpdateRoomStay) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.updateRoomStay(
                    intent.roomNumber,
                    intent.guestName,
                    intent.identificationId,
                    intent.checkIn,
                    intent.checkOut,
                    intent.checkInMillis,
                    intent.checkOutMillis,
                    intent.guestCount,
                    intent.capacity
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

    private fun deleteRoom(roomNumber: String) {
        scope.launch(Dispatchers.Main) {
            runCatching {
                repository.deleteRoom(roomNumber)
            }.onSuccess {
                loadData()
            }.onFailure { e ->
                updateState { it.copy(error = "حذف اتاق انجام نشد: ${e.message}") }
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
                updateState {
                    it.copy(
                        isLoading = false,
                        error = "حذف اطلاعات با خطا مواجه شد: ${e.message}"
                    )
                }
            }
        }
    }

    private fun markLunchDelivered(intent: AdminIntent.MarkLunchDelivered) {
        scope.launch(Dispatchers.Main) {
            val reservation =
                state.value.reservations.find { it.roomNumber == intent.roomNumber && it.date == intent.date }
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
                    println("saveReservation failure: ${e.message}")
                }
            }
        }
    }

    private fun markDinnerDelivered(intent: AdminIntent.MarkDinnerDelivered) {
        scope.launch(Dispatchers.Main) {
            val reservation =
                state.value.reservations.find { it.roomNumber == intent.roomNumber && it.date == intent.date }
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
                    println("saveReservation failure: ${e.message}")
                }
            }
        }
    }

    private fun updateFoodSelection(intent: AdminIntent.ChangeFood) {
        scope.launch(Dispatchers.Main) {
            val reservation =
                state.value.reservations.find { it.roomNumber == intent.roomNumber && it.date == intent.date }
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

            val updatedRes =
                reservation.copy(guestMealSelections = updatedSelections.sortedBy { it.guestIndex })

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
    }

    private fun printDailyReport(date: String, isLunch: Boolean) {
        val reservations =
            state.value.reservations.filter { it.date == date }.sortedBy { it.roomNumber }
        val foodMap = state.value.foods.associateBy { it.id }

        if (reservations.isEmpty()) return

        // 1. Identify unique foods ordered on this day for the specific meal
        val orderedFoodIds = reservations.flatMap { res ->
            res.guestMealSelections.mapNotNull { if (isLunch) it.lunchFoodId else it.dinnerFoodId }
        }.distinct()

        val columnFoods = orderedFoodIds.mapNotNull { foodMap[it] }.sortedBy { it.displayOrder }

        if (columnFoods.isEmpty()) return

        val mealTitle = if (isLunch) "ناهار" else "شام"

        val html = buildString {
            append("<!DOCTYPE html><html><head><meta charset='UTF-8'><style>")
            append("body { direction: rtl; font-family: Tahoma, Arial, sans-serif; padding: 10px; }")
            append("h2 { text-align: center; margin-bottom: 20px; font-size: 20px; }")
            append("table { width: 100%; border-collapse: collapse; border: 2px solid black; }")
            append("th, td { border: 1.5px solid black; padding: 10px 4px; text-align: center; font-size: 14px; }")
            append("th { background-color: #f8f8f8; font-weight: bold; }")
            append(".total-row { font-weight: bold; background-color: #f0f0f0; }")
            append(".food-header { writing-mode: horizontal-rl; white-space: nowrap; height: 50px; padding: 5px 0; }")
            append("@media print { body { padding: 0; } }")
            append("</style></head><body>")

            append("<h2>گزارش $mealTitle - تاریخ: $date</h2>")

            append("<table>")
            append("<thead><tr>")
            append("<th style='width: 40px;'>ردیف</th>")
            append("<th style='width: 70px;'>اتاق</th>")
            append("<th style='width: 50px;'>تعداد</th>")
            columnFoods.forEach { food ->
                append("<th class='food-header'>${food.name}</th>")
            }
            append("</tr></thead>")

            append("<tbody>")
            val columnTotals = IntArray(columnFoods.size) { 0 }
            var totalCountSum = 0
            var rowIndex = 1

            reservations.forEach { res ->
                val roomSelections = res.guestMealSelections
                val roomFoodIds =
                    roomSelections.mapNotNull { if (isLunch) it.lunchFoodId else it.dinnerFoodId }

                if (roomFoodIds.isEmpty()) return@forEach

                append("<tr>")
                append("<td>${rowIndex++}</td>")
                append("<td>${res.roomNumber}</td>")
                append("<td>${roomFoodIds.size}</td>")
                totalCountSum += roomFoodIds.size

                columnFoods.forEachIndexed { colIndex, food ->
                    val count = roomFoodIds.count { it == food.id }
                    append("<td>${if (count > 0) count else " - "}</td>")
                    columnTotals[colIndex] += count
                }
                append("</tr>")
            }
            append("</tbody>")

            append("<tfoot><tr class='total-row'>")
            append("<td colspan='2'>جمع کل</td>")
            append("<td>$totalCountSum</td>")
            columnTotals.forEach { total ->
                append("<td>$total</td>")
            }
            append("</tr></tfoot>")
            append("</table>")

            append("</body></html>")
        }

        // Changed from printHtml to openInBrowser to bypass "print service not found" issues
        ReportPrinter.openInBrowser(html)
    }
}
