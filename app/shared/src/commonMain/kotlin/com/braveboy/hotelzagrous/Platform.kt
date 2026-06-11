package com.braveboy.hotelzagrous

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform