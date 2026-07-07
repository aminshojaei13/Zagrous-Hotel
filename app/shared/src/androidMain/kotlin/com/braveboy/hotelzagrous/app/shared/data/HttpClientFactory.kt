package com.braveboy.hotelzagrous.app.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun createHotelHttpClient(): HttpClient = HttpClient(Android)

actual fun defaultApiBaseUrl(): String = "http://10.0.2.2:8092/api"
