package com.braveboy.hotelzagrous.app.shared.features.admin

import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation

object AdminReportCalculator {
    fun calculateDailySummary(
        date: String,
        reservations: List<FoodReservation>,
        foods: List<FoodItem>
    ): DailyReportSummary {
        val dailyRes = reservations.filter { it.date == date }
        val foodMap = foods.associateBy { it.id }

        val totalBreakfast = dailyRes.sumOf { it.breakfastCount }

        val lunchMap = mutableMapOf<String, Int>()
        val dinnerMap = mutableMapOf<String, Int>()

        dailyRes.forEach { res ->
            res.guestMealSelections.forEach { sel ->
                sel.lunchFoodId?.let { id ->
                    lunchMap[id] = (lunchMap[id] ?: 0) + 1
                }
                sel.dinnerFoodId?.let { id ->
                    dinnerMap[id] = (dinnerMap[id] ?: 0) + 1
                }
            }
        }

        val lunchOrders = lunchMap.map { (id, count) ->
            DailyReportFoodItem(id, foodMap[id]?.name ?: id, count)
        }.sortedBy { it.foodName }
        
        val dinnerOrders = dinnerMap.map { (id, count) ->
            DailyReportFoodItem(id, foodMap[id]?.name ?: id, count)
        }.sortedBy { it.foodName }

        return DailyReportSummary(
            date = date,
            totalBreakfast = totalBreakfast,
            lunchOrders = lunchOrders,
            dinnerOrders = dinnerOrders,
            totalLunch = lunchOrders.sumOf { it.quantity },
            totalDinner = dinnerOrders.sumOf { it.quantity }
        )
    }
}
