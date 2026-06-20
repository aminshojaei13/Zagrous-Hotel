package com.braveboy.hotelzagrous.core

import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.domain.PersianWeekday
import io.github.faridsolgi.persiandatetime.extensions.toDateString
import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

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
            pd.day == PersianWeekday.JOMEH.number
        } catch (e: Exception) {
            false
        }
    }
}
