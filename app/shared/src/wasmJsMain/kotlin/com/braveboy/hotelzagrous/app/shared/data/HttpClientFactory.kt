package com.braveboy.hotelzagrous.app.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

actual fun createHotelHttpClient(): HttpClient = HttpClient(Js)

actual fun defaultApiBaseUrl(): String = "http://localhost:8090/api"
