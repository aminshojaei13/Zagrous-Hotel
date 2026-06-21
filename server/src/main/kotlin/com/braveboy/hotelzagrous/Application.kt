package com.braveboy.hotelzagrous

import com.braveboy.hotelzagrous.core.ApiError
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.MenuConfig
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.UpdateRoomStayRequest
import com.braveboy.hotelzagrous.core.normalizeDigits
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    try {
        val port = System.getenv("PORT")?.toIntOrNull() ?: 8090
        println("Starting Hotel Zagrous API on port $port...")
        embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
            .start(wait = true)
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

fun Application.module() {
    println("Initializing Application module...")
    val database = try {
        HotelDatabase()
    } catch (e: Exception) {
        println("Failed to initialize database: ${e.message}")
        e.printStackTrace()
        throw e
    }

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
        )
    }
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
    }

    routing {
        get("/") {
            call.respondText("Hotel Zagrous API is running")
        }

        route("/api") {
            // Admin clear data endpoint
            delete("/admin/clear-all") {
                database.clearAllData()
                call.respond(HttpStatusCode.OK, mapOf("message" to "All data cleared successfully"))
            }

            get("/rooms") {
                call.respond(database.getRooms())
            }

            get("/rooms/{roomNumber}") {
                val roomNumber = call.parameters["roomNumber"].orEmpty().normalizeDigits()
                val room = database.getRoom(roomNumber)
                if (room == null) {
                    call.respond(HttpStatusCode.NotFound, ApiError("شماره اتاق یافت نشد"))
                } else {
                    call.respond(room)
                }
            }

            post("/rooms") {
                val room = call.receive<Room>()
                val normalizedRoom = room.copy(roomNumber = room.roomNumber.normalizeDigits())
                database.upsertRoom(normalizedRoom)
                call.respond(HttpStatusCode.Created, normalizedRoom)
            }

            put("/rooms/{roomNumber}/stay") {
                val roomNumber = call.parameters["roomNumber"].orEmpty().normalizeDigits()
                val request = call.receive<UpdateRoomStayRequest>()
                val updated = database.updateRoomStay(
                    roomNumber = roomNumber,
                    guestName = request.guestName,
                    identificationId = request.identificationId,
                    checkIn = request.checkIn,
                    checkOut = request.checkOut,
                    checkInMillis = request.checkInMillis,
                    checkOutMillis = request.checkOutMillis,
                    guestCount = request.guestCount
                )
                if (updated) {
                    call.respond(HttpStatusCode.OK, database.getRoom(roomNumber)!!)
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiError("شماره اتاق یافت نشد"))
                }
            }

            get("/foods") {
                call.respond(database.getFoods())
            }

            post("/foods") {
                val food = call.receive<FoodItem>()
                database.upsertFood(food)
                call.respond(HttpStatusCode.OK, food)
            }

            delete("/foods/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                database.deleteFood(id)
                call.respond(HttpStatusCode.OK)
            }

            get("/menu-configs") {
                call.respond(database.getMenuConfigs())
            }

            post("/menu-configs") {
                val config = call.receive<MenuConfig>()
                database.upsertMenuConfig(config)
                call.respond(HttpStatusCode.OK, config)
            }

            get("/reservations") {
                call.respond(database.getReservations())
            }

            get("/rooms/{roomNumber}/reservations") {
                val roomNumber = call.parameters["roomNumber"].orEmpty().normalizeDigits()
                call.respond(database.getReservationsForRoom(roomNumber))
            }

            post("/reservations") {
                val reservation = call.receive<FoodReservation>()
                val normalizedRes = reservation.copy(roomNumber = reservation.roomNumber.normalizeDigits())
                database.saveReservation(normalizedRes)
                call.respond(HttpStatusCode.Created, normalizedRes)
            }
        }
    }
}
