package com.braveboy.hotelzagrous.core

expect object DateUtils {
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): String

    fun convertMillisToJalaliString(millis: Long): String
}
