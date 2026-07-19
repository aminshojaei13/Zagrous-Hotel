package com.braveboy.hotelzagrous.core

fun String.normalizeDigits(): String {
    return this.map { char ->
        when (char) {
            in '۰'..'۹' -> (char - '۰' + '0'.code).toChar()
            in '٠'..'٩' -> (char - '٠' + '0'.code).toChar()
            else -> char
        }
    }.joinToString("").trim()
}

fun Long.formatPrice(): String {
    val reversed = this.toString().reversed()
    val sb = StringBuilder()
    for (i in reversed.indices) {
        if (i > 0 && i % 3 == 0) sb.append(",")
        sb.append(reversed[i])
    }
    return sb.reverse().toString()
}
