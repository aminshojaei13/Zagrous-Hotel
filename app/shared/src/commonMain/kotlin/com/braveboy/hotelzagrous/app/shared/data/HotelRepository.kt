package com.braveboy.hotelzagrous.app.shared.data

import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.UpdateRoomStayRequest
import com.braveboy.hotelzagrous.core.normalizeDigits
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HotelRepository(
    private val apiBaseUrl: String = defaultApiBaseUrl(),
    httpClient: HttpClient = createHotelHttpClient()
) {
    private val client = httpClient.config {
        expectSuccess = true
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
    }

    suspend fun getRoom(roomNumber: String): Room? {
        val normalizedRoomNumber = roomNumber.normalizeDigits()
        return try {
            val response: HttpResponse = client.get("$apiBaseUrl/rooms/$normalizedRoomNumber")
            if (response.status.isSuccess()) {
                response.body()
            } else {
                null
            }
        } catch (_: Throwable) {
            null
        }
    }

    suspend fun getRooms(): List<Room> = try {
        val response: HttpResponse = client.get("$apiBaseUrl/rooms")
        if (response.status.isSuccess()) response.body() else emptyList()
    } catch (e: Throwable) {
        emptyList()
    }

    suspend fun getAvailableFoods(): List<FoodItem> = try {
        val response: HttpResponse = client.get("$apiBaseUrl/foods")
        if (response.status.isSuccess()) response.body() else emptyList()
    } catch (_: Throwable) {
        emptyList()
    }

    suspend fun getAllReservations(): List<FoodReservation> = try {
        val response: HttpResponse = client.get("$apiBaseUrl/reservations")
        if (response.status.isSuccess()) response.body() else emptyList()
    } catch (e: Throwable) {
        emptyList()
    }

    suspend fun getReservationsForRoom(roomNumber: String): List<FoodReservation> {
        val normalizedRoomNumber = roomNumber.normalizeDigits()
        return try {
            val response: HttpResponse =
                client.get("$apiBaseUrl/rooms/$normalizedRoomNumber/reservations")
            if (response.status.isSuccess()) {
                response.body()
            } else {
                emptyList()
            }
        } catch (_: Throwable) {
            emptyList()
        }
    }

    suspend fun saveReservation(reservation: FoodReservation) {
        val normalizedReservation = reservation.copy(roomNumber = reservation.roomNumber.normalizeDigits())
        client.post("$apiBaseUrl/reservations") {
            contentType(ContentType.Application.Json)
            setBody(normalizedReservation)
        }
    }

    suspend fun addRoom(room: Room) {
        val normalizedRoom = room.copy(roomNumber = room.roomNumber.normalizeDigits())
        client.post("$apiBaseUrl/rooms") {
            contentType(ContentType.Application.Json)
            setBody(normalizedRoom)
        }
    }

    suspend fun updateRoomStay(
        roomNumber: String,
        checkIn: String,
        checkOut: String,
        checkInMillis: Long,
        checkOutMillis: Long,
        guestCount: Int
    ) {
        val normalizedRoomNumber = roomNumber.normalizeDigits()
        client.put("$apiBaseUrl/rooms/$normalizedRoomNumber/stay") {
            contentType(ContentType.Application.Json)
            setBody(
                UpdateRoomStayRequest(
                    checkIn = checkIn,
                    checkOut = checkOut,
                    checkInMillis = checkInMillis,
                    checkOutMillis = checkOutMillis,
                    guestCount = guestCount
                )
            )
        }
    }

    suspend fun clearAllData() {
        client.delete("$apiBaseUrl/admin/clear-all")
    }
}
