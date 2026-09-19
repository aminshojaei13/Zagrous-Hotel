package com.braveboy.hotelzagrous.app.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun createHotelHttpClient(): HttpClient = HttpClient(Android)

// Pointing to Laravel backend (default port 8000)
// Using 10.0.2.2 for Android Emulator to access localhost
actual fun defaultApiBaseUrl(): String = "http://10.0.2.2:8000/api"
