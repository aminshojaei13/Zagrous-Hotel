package com.braveboy.hotelzagrous.core

expect object DateUtils {
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): String
    fun convertMillisToJalaliString(millis: Long): String
    fun convertDateToTimeMillis(date: String): Long
    fun isFriday(dateString: String): Boolean
    fun isEven(dateString: String): Boolean
    fun getJalaliMonthNames(): List<String>
    fun getDaysInJalaliMonth(year: Int, month: Int): Int
    fun getFirstDayOfMonth(year: Int, month: Int): Int // 0 for Shanbeh, 6 for Jomeh
}
