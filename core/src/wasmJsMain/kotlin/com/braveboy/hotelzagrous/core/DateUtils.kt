package com.braveboy.hotelzagrous.core

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
        TODO("Not yet implemented")
    }
}
