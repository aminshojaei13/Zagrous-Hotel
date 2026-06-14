package com.braveboy.hotelzagrous.app.shared.data

import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.UpdateRoomStayRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HotelRepository(
    private val apiBaseUrl: String = defaultApiBaseUrl(),
    httpClient: HttpClient = createHotelHttpClient()
) {
    private val client = httpClient.config {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
        }
    }

    suspend fun getRoom(roomNumber: String): Room? {
        return try {
            client.get("$apiBaseUrl/rooms/$roomNumber").body()
        } catch (_: Throwable) {
            null
        }
    }

    suspend fun getRooms(): List<Room> = client.get("$apiBaseUrl/rooms").body()

    suspend fun getAvailableFoods(): List<FoodItem> = client.get("$apiBaseUrl/foods").body()

    suspend fun getAllReservations(): List<FoodReservation> = client.get("$apiBaseUrl/reservations").body()

    suspend fun getReservationsForRoom(roomNumber: String): List<FoodReservation> = 
        client.get("$apiBaseUrl/rooms/$roomNumber/reservations").body()

    suspend fun saveReservation(reservation: FoodReservation) {
        client.post("$apiBaseUrl/reservations") {
            contentType(ContentType.Application.Json)
            setBody(reservation)
        }
    }

    suspend fun addRoom(room: Room) {
        client.post("$apiBaseUrl/rooms") {
            contentType(ContentType.Application.Json)
            setBody(room)
        }
    }

    suspend fun updateRoomStay(roomNumber: String, checkIn: String, checkOut: String, checkInMillis: Long, checkOutMillis: Long, guestCount: Int) {
        client.put("$apiBaseUrl/rooms/$roomNumber/stay") {
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
}
