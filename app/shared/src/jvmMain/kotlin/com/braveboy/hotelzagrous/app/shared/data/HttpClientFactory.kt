package com.braveboy.hotelzagrous.app.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

actual fun createHotelHttpClient(): HttpClient = HttpClient(Java)

actual fun defaultApiBaseUrl(): String = "http://localhost:8090/api"
