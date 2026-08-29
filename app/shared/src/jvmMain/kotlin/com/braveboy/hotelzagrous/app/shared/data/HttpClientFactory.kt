package com.braveboy.hotelzagrous.app.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

const val localhost = "localhost" //"192.168.10.107"
const val port = "8092"

actual fun createHotelHttpClient(): HttpClient = HttpClient(Java)

actual fun defaultApiBaseUrl(): String = "http://$localhost:$port/api"
