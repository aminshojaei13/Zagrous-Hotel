package com.braveboy.hotelzagrous

import com.braveboy.hotelzagrous.core.ApiError
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.UpdateRoomStayRequest
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
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8090
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val database = HotelDatabase()

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
        allowMethod(HttpMethod.Options)
    }

    routing {
        get("/") {
            call.respondText("Hotel Zagrous API is running")
        }

        route("/api") {
            get("/rooms") {
                call.respond(database.getRooms())
            }

            get("/rooms/{roomNumber}") {
                val roomNumber = call.parameters["roomNumber"].orEmpty()
                val room = database.getRoom(roomNumber)
                if (room == null) {
                    call.respond(HttpStatusCode.NotFound, ApiError("شماره اتاق یافت نشد"))
                } else {
                    call.respond(room)
                }
            }

            post("/rooms") {
                val room = call.receive<Room>()
                database.upsertRoom(room)
                call.respond(HttpStatusCode.Created, room)
            }

            put("/rooms/{roomNumber}/stay") {
                val roomNumber = call.parameters["roomNumber"].orEmpty()
                val request = call.receive<UpdateRoomStayRequest>()
                val updated = database.updateRoomStay(
                    roomNumber = roomNumber,
                    checkIn = request.checkIn,
                    checkOut = request.checkOut,
                    checkInMillis = request.checkInMillis,
                    checkOutMillis = request.checkOutMillis
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

            get("/reservations") {
                call.respond(database.getReservations())
            }

            post("/reservations") {
                val reservation = call.receive<FoodReservation>()
                database.saveReservation(reservation)
                call.respond(HttpStatusCode.Created, reservation)
            }
        }
    }
}
