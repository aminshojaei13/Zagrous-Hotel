package com.braveboy.hotelzagrous.app.shared.data

import io.ktor.client.HttpClient

expect fun createHotelHttpClient(): HttpClient

expect fun defaultApiBaseUrl(): String
