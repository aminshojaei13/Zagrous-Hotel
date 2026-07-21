package com.braveboy.hotelzagrous

import com.braveboy.hotelzagrous.core.ApiError
import com.braveboy.hotelzagrous.core.FinancialReport
import com.braveboy.hotelzagrous.core.FinancialSummaryResponse
import com.braveboy.hotelzagrous.core.FinancialTransaction
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.MenuConfig
import com.braveboy.hotelzagrous.core.PhysicalRoom
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.RoomFinancialSummary
import com.braveboy.hotelzagrous.core.TransactionType
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
        // تنظیم شده روی پورت ۸۰۹۲
        val port = System.getenv("PORT")?.toIntOrNull() ?: 8092
        println("Starting Hotel Zagrous API on port $port...")
        embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
            .start(wait = true)
    } catch (t: Throwable) {
        if (t.message?.contains("Address already in use") == true) {
            println("ERROR: Port 8092 is already busy. Try killing the previous process.")
        } else {
            t.printStackTrace()
        }
    }
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
    
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowNonSimpleContentTypes = true
    }

    routing {
        get("/") {
            call.respondText("Hotel Zagrous API is running on port 8092")
        }

        route("/api") {
            delete("/admin/clear-all") {
                database.clearAllData()
                call.respond(HttpStatusCode.OK, mapOf("message" to "All data cleared successfully"))
            }

            get("/rooms") { call.respond(database.getRooms()) }
            get("/rooms/{idOrNumber}") {
                val idOrNumber = call.parameters["idOrNumber"].orEmpty()
                // Try as UUID/ID first, then as Room Number
                val room = database.getRoom(idOrNumber) ?: database.getRoomByNumber(idOrNumber.normalizeDigits())
                if (room == null) call.respond(HttpStatusCode.NotFound, ApiError("اتاق یافت نشد"))
                else call.respond(room)
            }
            post("/rooms") {
                val room = call.receive<Room>()
                val normalizedRoom = room.copy(
                    roomNumber = room.roomNumber.normalizeDigits(),
                    identificationId = room.identificationId.normalizeDigits()
                )
                val id = database.upsertRoom(normalizedRoom)
                call.respond(HttpStatusCode.Created, normalizedRoom.copy(id = id))
            }
            delete("/rooms/{id}") {
                val id = call.parameters["id"].orEmpty()
                database.deleteRoom(id)
                call.respond(HttpStatusCode.OK)
            }

            get("/physical-rooms") { call.respond(database.getPhysicalRooms()) }
            post("/physical-rooms") {
                val room = call.receive<PhysicalRoom>()
                database.upsertPhysicalRoom(room)
                call.respond(HttpStatusCode.OK, room)
            }
            delete("/physical-rooms/{id}") {
                val id = call.parameters["id"].orEmpty()
                database.deletePhysicalRoom(id)
                call.respond(HttpStatusCode.OK)
            }

            put("/rooms/{id}/stay") {
                val id = call.parameters["id"].orEmpty()
                val request = call.receive<UpdateRoomStayRequest>()
                val updated = database.updateRoomStay(
                    id,
                    request.roomNumber.normalizeDigits(),
                    request.guestName,
                    request.identificationId.normalizeDigits(),
                    request.checkIn,
                    request.checkOut,
                    request.checkInMillis,
                    request.checkOutMillis,
                    request.guestCount,
                    request.hasBreakfast,
                    request.breakfastCount,
                    request.contractAmount
                )
                if (updated) call.respond(HttpStatusCode.OK, database.getRoom(id)!!)
                else call.respond(HttpStatusCode.NotFound, ApiError("شناسه اتاق یافت نشد"))
            }
            get("/foods") { call.respond(database.getFoods()) }
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
            get("/menu-configs") { call.respond(database.getMenuConfigs()) }
            post("/menu-configs") {
                val config = call.receive<MenuConfig>()
                database.upsertMenuConfig(config)
                call.respond(HttpStatusCode.OK, config)
            }
            get("/reservations") { call.respond(database.getReservations()) }
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

            // Financial module
            get("/transactions") {
                call.respond(database.getTransactions())
            }
            post("/transactions") {
                val tx = call.receive<FinancialTransaction>()
                database.upsertTransaction(tx)
                call.respond(HttpStatusCode.OK, tx)
            }
            delete("/transactions/{id}") {
                val id = call.parameters["id"].orEmpty()
                database.deleteTransaction(id)
                call.respond(HttpStatusCode.OK)
            }

            get("/financial-summary") {
                val rooms = database.getRooms()
                val transactions = database.getTransactions()
                
                val summaries = rooms.map { room ->
                    val roomTxs = transactions.filter { it.roomId == room.id }
                    val deposits = roomTxs.filter { it.transactionType == TransactionType.DEPOSIT }.sumOf { it.amount }
                    val expenses = roomTxs.filter { it.transactionType == TransactionType.EXPENSE }.sumOf { it.amount }
                    val settlements = roomTxs.filter { it.transactionType == TransactionType.SETTLEMENT }.sumOf { it.amount }
                    
                    RoomFinancialSummary(
                        roomId = room.id,
                        roomNumber = room.roomNumber,
                        guestName = room.guestName,
                        totalContractAmount = room.contractAmount,
                        totalDeposits = deposits,
                        totalExpenses = expenses,
                        remainingSettlement = room.contractAmount - deposits - settlements,
                        profit = room.contractAmount - expenses
                    )
                }
                
                val totalExpenses = transactions.filter { it.transactionType == TransactionType.EXPENSE }.sumOf { it.amount }
                val totalDeposits = transactions.filter { it.transactionType == TransactionType.DEPOSIT }.sumOf { it.amount }
                val totalSettlements = transactions.filter { it.transactionType == TransactionType.SETTLEMENT }.sumOf { it.amount }
                
                val report = FinancialReport(
                    totalExpenses = totalExpenses,
                    totalDeposits = totalDeposits,
                    totalSettlements = totalSettlements,
                    remainingAmount = summaries.sumOf { it.remainingSettlement },
                    netProfit = rooms.sumOf { it.contractAmount } - totalExpenses,
                    transactions = transactions
                )
                
                call.respond(FinancialSummaryResponse(report, summaries))
            }
        }
    }
}

val Application.database: HotelDatabase by lazy { HotelDatabase() }
