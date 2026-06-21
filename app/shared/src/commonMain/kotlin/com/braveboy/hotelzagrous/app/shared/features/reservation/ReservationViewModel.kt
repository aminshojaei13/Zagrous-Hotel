package com.braveboy.hotelzagrous.app.shared.features.reservation

import com.braveboy.hotelzagrous.app.shared.data.HotelRepository
import com.braveboy.hotelzagrous.app.shared.mvi.BaseViewModel
import com.braveboy.hotelzagrous.core.DateUtils
import com.braveboy.hotelzagrous.core.DayType
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodType
import com.braveboy.hotelzagrous.core.GuestMealSelection
import com.braveboy.hotelzagrous.core.MenuConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val repository: HotelRepository,
    private val scope: CoroutineScope
) : BaseViewModel<ReservationState, ReservationIntent>(ReservationState()) {

    private var allMenuConfigs: List<MenuConfig> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        scope.launch {
            runCatching {
                val foods = repository.getAvailableFoods()
                val configs = repository.getMenuConfigs()
                foods to configs
            }.onSuccess { (foods, configs) ->
                allMenuConfigs = configs
                updateState { it.copy(availableFoods = foods, error = null) }
            }.onFailure {
                updateState { it.copy(error = "ارتباط با سرور برقرار نشد") }
            }
        }
    }

    override fun onIntent(intent: ReservationIntent) {
        when (intent) {
            is ReservationIntent.UpdateRoomNumber -> updateState { it.copy(roomNumber = intent.roomNumber) }
            is ReservationIntent.UpdateIdentificationId -> updateState { it.copy(identificationId = intent.identificationId) }
            is ReservationIntent.Login -> login()
            is ReservationIntent.ChangeFood -> updateFoodSelection(intent)
            is ReservationIntent.ConfirmReservation -> submit()
        }
    }

    private fun login() {
        val currentRoomNumber = state.value.roomNumber
        val currentIdentificationId = state.value.identificationId

        if (currentRoomNumber.isBlank() || currentIdentificationId.isBlank()) {
            updateState { it.copy(error = "لطفاً شماره اتاق و شماره شناسایی را وارد کنید") }
            return
        }

        scope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            runCatching {
                val room = repository.getRoom(currentRoomNumber)
                val reservations = if (room != null) repository.getReservationsForRoom(room.roomNumber) else emptyList()
                room to reservations
            }.onSuccess { (room, reservations) ->
                if (room != null) {
                    if (room.identificationId == currentIdentificationId) {
                        updateState { it.copy(
                            room = room,
                            tempReservations = reservations,
                            isLoggedIn = true,
                            isLoading = false,
                            error = null
                        ) }
                    } else {
                        updateState { it.copy(isLoading = false, error = "شماره شناسایی با شماره اتاق مطابقت ندارد") }
                    }
                } else {
                    updateState { it.copy(isLoading = false, error = "شماره اتاق یافت نشد") }
                }
            }.onFailure { e ->
                updateState { it.copy(isLoading = false, error = "ارتباط با سرور برقرار نشد: ${e.message}") }
            }
        }
    }

    private fun updateFoodSelection(intent: ReservationIntent.ChangeFood) {
        updateState { currentState ->
            val updatedTemp = currentState.tempReservations.toMutableList()
            val existingRes = updatedTemp.find { it.date == intent.date } 
                ?: FoodReservation(currentState.roomNumber, intent.date)
            
            val updatedSelections = existingRes.guestMealSelections.toMutableList()
            val existingSelection = updatedSelections.find { it.guestIndex == intent.guestIndex }
                ?: GuestMealSelection(intent.guestIndex)
            
            updatedSelections.removeAll { it.guestIndex == intent.guestIndex }
            val newSelection = if (intent.isLunch) {
                existingSelection.copy(lunchFoodId = intent.foodId)
            } else {
                existingSelection.copy(dinnerFoodId = intent.foodId)
            }
            updatedSelections.add(newSelection)
            
            val newRes = existingRes.copy(guestMealSelections = updatedSelections.sortedBy { it.guestIndex })
            
            updatedTemp.removeAll { it.date == intent.date }
            updatedTemp.add(newRes)
            
            currentState.copy(tempReservations = updatedTemp)
        }
    }

    private fun submit() {
        scope.launch {
            updateState { it.copy(isLoading = true) }
            runCatching {
                state.value.tempReservations.forEach { repository.saveReservation(it) }
            }.onSuccess {
                updateState { it.copy(isLoading = false, error = "رزرو با موفقیت ثبت شد") }
            }.onFailure {
                updateState { it.copy(isLoading = false, error = "ثبت رزرو انجام نشد") }
            }
        }
    }

    /**
     * منطق نمایش منو بر اساس تاریخ (بند ۳ نیازمندی‌ها)
     */
    fun getFoodsForDate(dateString: String, foodType: FoodType): List<FoodItem> {
        val dayType = determineDayType(dateString)
        
        // بررسی فعال بودن کل منو (بند ۲.۵)
        val isMenuEnabled = allMenuConfigs.find { it.dayType == dayType && it.foodType == foodType }?.isEnabled ?: true
        if (!isMenuEnabled) return emptyList()

        return state.value.availableFoods.filter { 
            it.dayType == dayType && 
            it.type == foodType && 
            it.isActive &&           // بند ۳ - شرط فعال بودن
            it.isVisibleToUsers      // بند ۳ - شرط نمایش به کاربر
        }.sortedBy { it.displayOrder }
    }

    private fun determineDayType(dateString: String): DayType {
        if (DateUtils.isFriday(dateString)) return DayType.FRIDAY
        
        val parts = dateString.split("/")
        val dayOfMonth = if (parts.size == 3) parts[2].toIntOrNull() ?: 1 else 1
        
        return if (dayOfMonth % 2 == 0) DayType.EVEN else DayType.ODD
    }
}
