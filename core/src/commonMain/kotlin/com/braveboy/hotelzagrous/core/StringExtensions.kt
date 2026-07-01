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
