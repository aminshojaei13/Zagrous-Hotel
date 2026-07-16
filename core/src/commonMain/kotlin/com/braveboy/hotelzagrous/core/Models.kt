package com.braveboy.hotelzagrous.core

import kotlinx.serialization.Serializable

@Serializable
data class PhysicalRoom(
    val id: String = "",
    val roomNumber: String,
    val bedCount: Int = 1,
    val capacity: Int = 1,
    val type: String = "STANDARD"
)

@Serializable
data class Room(
    val id: String = "",
    val roomNumber: String,
    val guestName: String = "",
    val identificationId: String = "",
    val guestCount: Int = 1,
    val hasBreakfast: Boolean = false,
    val breakfastCount: Int = 0,
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val checkInEpochMillis: Long = 0,
    val checkOutEpochMillis: Long = 0,
    val contractAmount: Long = 0
)

@Serializable
enum class FoodType { LUNCH, DINNER }

@Serializable
enum class DayType { EVEN, ODD, FRIDAY }

@Serializable
data class FoodItem(
    val id: String = "",
    val name: String,
    val nameAr: String? = null,
    val type: FoodType,
    val dayType: DayType,
    val isActive: Boolean = true,
    val isVisibleToUsers: Boolean = true,
    val displayOrder: Int = 0
)

@Serializable
data class MenuConfig(
    val dayType: DayType,
    val foodType: FoodType,
    val isEnabled: Boolean = true
)

@Serializable
data class FoodReservation(
    val roomNumber: String,
    val date: String,
    val guestMealSelections: List<GuestMealSelection> = emptyList(),
    val breakfastCount: Int = 0
)

@Serializable
data class GuestMealSelection(
    val guestIndex: Int,
    val lunchFoodId: String? = null,
    val dinnerFoodId: String? = null,
    val lunchDelivered: Boolean = false,
    val dinnerDelivered: Boolean = false
)

@Serializable
data class UpdateRoomStayRequest(
    val roomNumber: String,
    val guestName: String,
    val identificationId: String,
    val checkIn: String,
    val checkOut: String,
    val checkInMillis: Long,
    val checkOutMillis: Long,
    val guestCount: Int,
    val hasBreakfast: Boolean = false,
    val breakfastCount: Int = 0,
    val contractAmount: Long = 0
)

@Serializable
data class ApiError(val message: String)

@Serializable
enum class TransactionType { EXPENSE, DEPOSIT, SETTLEMENT }

@Serializable
enum class PaymentMethod { CASH, CARD, BANK_TRANSFER }

@Serializable
enum class TransactionStatus { PAID, PENDING }

@Serializable
data class FinancialTransaction(
    val id: String = "",
    val roomId: String, // Related to Room.id
    val title: String,
    val description: String = "",
    val amount: Long,
    val transactionType: TransactionType,
    val paymentDate: String, // Jalali date format: yyyy/MM/dd
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val notes: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val status: TransactionStatus = TransactionStatus.PAID
)

@Serializable
data class RoomFinancialSummary(
    val roomId: String,
    val roomNumber: String,
    val guestName: String,
    val totalContractAmount: Long,
    val totalDeposits: Long,
    val totalExpenses: Long,
    val remainingSettlement: Long,
    val profit: Long
)

@Serializable
data class FinancialReport(
    val totalExpenses: Long,
    val totalDeposits: Long,
    val totalSettlements: Long,
    val remainingAmount: Long,
    val netProfit: Long,
    val transactions: List<FinancialTransaction> = emptyList()
)
