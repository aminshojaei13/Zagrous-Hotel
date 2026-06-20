package com.braveboy.hotelzagrous.core

expect object DateUtils {
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): String
    fun convertMillisToJalaliString(millis: Long): String
    fun convertDateToTimeMillis(date: String): Long
    fun isFriday(dateString: String): Boolean
    fun isEven(dateString: String): Boolean
}
