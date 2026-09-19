package com.braveboy.hotelzagrous.app.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlinx.browser.window

actual fun createHotelHttpClient(): HttpClient = HttpClient(Js)

actual fun defaultApiBaseUrl(): String {
    val protocol = window.location.protocol
    val hostname = window.location.hostname.ifBlank { "localhost" }
    // Laravel backend on port 8000
    return "$protocol//$hostname:8000/api"
}
