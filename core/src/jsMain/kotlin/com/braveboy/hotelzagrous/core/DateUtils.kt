package com.braveboy.hotelzagrous.core

import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual object DateUtils {
    actual fun gregorianToJalali(gy: Int, gm: Int, gd: Int): String {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        var gDayNo = 365 * (gy - 1600) + (gy - 1501) / 4 - (gy - 1601) / 100 + (gy - 1201) / 400
        for (i in 1 until gm) gDayNo += gDaysInMonth[i]
        if (gm > 2 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) gDayNo++
        gDayNo += gd - 1

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053
        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461
        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 1..12) {
            jm = i
            if (jDayNo < jDaysInMonth[i]) break
            jDayNo -= jDaysInMonth[i]
        }
        val jd = jDayNo + 1

        return "$jy/${jm.toString().padStart(2, '0')}/${jd.toString().padStart(2, '0')}"
    }

    actual fun convertMillisToJalaliString(millis: Long): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val localDate = instant.toLocalDateTime(TimeZone.UTC).date
        return gregorianToJalali(localDate.year, localDate.month.ordinal + 1, localDate.day)
    }

    actual fun convertDateToTimeMillis(date: String): Long {
        TODO("Not yet implemented")
    }

    actual fun isFriday(dateString: String): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isEven(dateString: String): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getJalaliMonthNames(): List<String> {
        TODO("Not yet implemented")
    }

    actual fun getDaysInJalaliMonth(year: Int, month: Int): Int {
        TODO("Not yet implemented")
    }

    actual fun getFirstDayOfMonth(year: Int, month: Int): Int {
        TODO("Not yet implemented")
    }
}
