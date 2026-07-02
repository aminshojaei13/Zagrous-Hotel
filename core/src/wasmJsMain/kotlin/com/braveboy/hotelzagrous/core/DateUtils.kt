package com.braveboy.hotelzagrous.core

import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.domain.PersianWeekday
import io.github.faridsolgi.persiandatetime.extensions.persianDayOfWeek
import io.github.faridsolgi.persiandatetime.extensions.toDateString
import io.github.faridsolgi.persiandatetime.extensions.toEpochMilliseconds
import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

actual object DateUtils {
    actual fun gregorianToJalali(gy: Int, gm: Int, gd: Int): String {
        return LocalDate(gy, gm, gd).toPersianDateTime().toDateString()
    }

    actual fun convertMillisToJalaliString(millis: Long): String {
        return Instant.fromEpochMilliseconds(millis).toPersianDateTime(TimeZone.UTC).toDateString()
    }

    actual fun isFriday(dateString: String): Boolean {
        val parts = dateString.split("/")
        if (parts.size != 3) return false
        return try {
            val pd = PersianDateTime(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            pd.persianDayOfWeek() == PersianWeekday.JOMEH
        } catch (e: Exception) {
            false
        }
    }

    actual fun isEven(dateString: String): Boolean {
        val parts = dateString.split("/")
        if (parts.size != 3) return false
        return try {
            val pd = PersianDateTime(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            pd.day % 2 == 0
        } catch (e: Exception) {
            false
        }
    }

    actual fun convertDateToTimeMillis(date: String): Long {
        val parts = date.split("/")
        if (parts.size != 3) return 0
        return try {
            val pd = PersianDateTime(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            pd.toEpochMilliseconds()
        } catch (e: Exception) {
            0
        }
    }

    actual fun getJalaliMonthNames(): List<String> = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    actual fun getDaysInJalaliMonth(year: Int, month: Int): Int {
        return when {
            month <= 6 -> 31
            month <= 11 -> 30
            else -> if (isLeapYear(year)) 30 else 29
        }
    }

    actual fun getFirstDayOfMonth(year: Int, month: Int): Int {
        return try {
            PersianDateTime(year, month, 1).persianDayOfWeek().ordinal
        } catch (e: Exception) {
            0
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        val r = year % 33
        return r == 1 || r == 5 || r == 9 || r == 13 || r == 17 || r == 22 || r == 26 || r == 30
    }
}
