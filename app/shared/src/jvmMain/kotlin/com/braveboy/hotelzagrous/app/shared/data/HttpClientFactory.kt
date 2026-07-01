package com.braveboy.hotelzagrous.app.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

const val localhost = "192.168.10.107"

actual fun createHotelHttpClient(): HttpClient = HttpClient(Java)

actual fun defaultApiBaseUrl(): String = "http://$localhost:8090/api"
