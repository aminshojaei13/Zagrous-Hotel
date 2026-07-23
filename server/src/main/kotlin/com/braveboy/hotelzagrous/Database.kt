package com.braveboy.hotelzagrous

import com.braveboy.hotelzagrous.core.BookingSource
import com.braveboy.hotelzagrous.core.DayType
import com.braveboy.hotelzagrous.core.FinancialTransaction
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodType
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.GuestMealSelection
import com.braveboy.hotelzagrous.core.MenuConfig
import com.braveboy.hotelzagrous.core.PaymentMethod
import com.braveboy.hotelzagrous.core.PhysicalRoom
import com.braveboy.hotelzagrous.core.SettlementType
import com.braveboy.hotelzagrous.core.TransactionStatus
import com.braveboy.hotelzagrous.core.TransactionType
import com.braveboy.hotelzagrous.core.normalizeDigits
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID
import kotlinx.serialization.json.Json

class HotelDatabase(
    databasePath: String = "hotel-zagrous.sqlite"
) {
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$databasePath")
    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
    }

    init {
        createTables()
        migrateIfNeeded()
        seedDefaults()
    }

    fun clearAllData() {
        connection.createStatement().use { statement ->
            statement.executeUpdate("DELETE FROM food_reservations")
            statement.executeUpdate("DELETE FROM financial_transactions")
            statement.executeUpdate("DELETE FROM rooms")
        }
    }

    // Financial Transactions
    fun getTransactions(): List<FinancialTransaction> = connection.prepareStatement(
        """
        SELECT id, room_id, title, description, amount, transaction_type, payment_date, created_at, updated_at, notes, payment_method, status
        FROM financial_transactions
        ORDER BY payment_date DESC
        """.trimIndent()
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    add(
                        FinancialTransaction(
                            id = rows.getString("id"),
                            roomId = rows.getString("room_id"),
                            title = rows.getString("title"),
                            description = rows.getString("description"),
                            amount = rows.getLong("amount"),
                            transactionType = TransactionType.valueOf(rows.getString("transaction_type")),
                            paymentDate = rows.getString("payment_date"),
                            createdAt = rows.getLong("created_at"),
                            updatedAt = rows.getLong("updated_at"),
                            notes = rows.getString("notes"),
                            paymentMethod = PaymentMethod.valueOf(rows.getString("payment_method")),
                            status = TransactionStatus.valueOf(rows.getString("status"))
                        )
                    )
                }
            }
        }
    }

    fun upsertTransaction(tx: FinancialTransaction) {
        val id = tx.id.ifBlank { UUID.randomUUID().toString() }
        val now = System.currentTimeMillis()
        connection.prepareStatement(
            """
            INSERT INTO financial_transactions(id, room_id, title, description, amount, transaction_type, payment_date, created_at, updated_at, notes, payment_method, status)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                room_id = excluded.room_id,
                title = excluded.title,
                description = excluded.description,
                amount = excluded.amount,
                transaction_type = excluded.transaction_type,
                payment_date = excluded.payment_date,
                updated_at = excluded.updated_at,
                notes = excluded.notes,
                payment_method = excluded.payment_method,
                status = excluded.status
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, id)
            statement.setString(2, tx.roomId)
            statement.setString(3, tx.title)
            statement.setString(4, tx.description)
            statement.setLong(5, tx.amount)
            statement.setString(6, tx.transactionType.name)
            statement.setString(7, tx.paymentDate)
            statement.setLong(8, if (tx.createdAt == 0L) now else tx.createdAt)
            statement.setLong(9, now)
            statement.setString(10, tx.notes)
            statement.setString(11, tx.paymentMethod.name)
            statement.setString(12, tx.status.name)
            statement.executeUpdate()
        }
    }

    fun deleteTransaction(id: String) {
        connection.prepareStatement("DELETE FROM financial_transactions WHERE id = ?").use { statement ->
            statement.setString(1, id)
            statement.executeUpdate()
        }
    }

    fun getRooms(): List<Room> = connection.prepareStatement(
        """
        SELECT id, room_number, guest_name, identification_id, guest_count, has_breakfast, breakfast_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis, contract_amount, booking_source, agency_name, settlement_type, agency_amount, guest_amount
        FROM rooms
        ORDER BY room_number
        """.trimIndent()
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    add(
                        Room(
                            id = rows.getString("id"),
                            roomNumber = rows.getString("room_number"),
                            guestName = rows.getString("guest_name"),
                            identificationId = rows.getString("identification_id") ?: "",
                            guestCount = rows.getInt("guest_count"),
                            hasBreakfast = rows.getInt("has_breakfast") == 1,
                            breakfastCount = rows.getInt("breakfast_count"),
                            checkInDate = rows.getString("check_in_date"),
                            checkOutDate = rows.getString("check_out_date"),
                            checkInEpochMillis = rows.getLong("check_in_epoch_millis"),
                            checkOutEpochMillis = rows.getLong("check_out_epoch_millis"),
                            contractAmount = rows.getLong("contract_amount"),
                            bookingSource = BookingSource.valueOf(rows.getString("booking_source") ?: BookingSource.DIRECT.name),
                            agencyName = rows.getString("agency_name") ?: "",
                            settlementType = SettlementType.valueOf(rows.getString("settlement_type") ?: SettlementType.FULL_GUEST.name),
                            agencyAmount = rows.getLong("agency_amount"),
                            guestAmount = rows.getLong("guest_amount")
                        )
                    )
                }
            }
        }
    }

    fun getPhysicalRooms(): List<PhysicalRoom> = connection.prepareStatement(
        "SELECT id, room_number, bed_count, capacity, type, is_active FROM physical_rooms ORDER BY room_number"
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    add(
                        PhysicalRoom(
                            id = rows.getString("id"),
                            roomNumber = rows.getString("room_number"),
                            bedCount = rows.getInt("bed_count"),
                            capacity = rows.getInt("capacity"),
                            type = rows.getString("type"),
                            isActive = rows.getInt("is_active") == 1
                        )
                    )
                }
            }
        }
    }

    fun upsertPhysicalRoom(room: PhysicalRoom) {
        val id = room.id.ifBlank { UUID.randomUUID().toString() }
        connection.prepareStatement(
            """
            INSERT INTO physical_rooms(id, room_number, bed_count, capacity, type, is_active)
            VALUES(?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                room_number = excluded.room_number,
                bed_count = excluded.bed_count,
                capacity = excluded.capacity,
                type = excluded.type,
                is_active = excluded.is_active
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, id)
            statement.setString(2, room.roomNumber)
            statement.setInt(3, room.bedCount)
            statement.setInt(4, room.capacity)
            statement.setString(5, room.type)
            statement.setInt(6, if (room.isActive) 1 else 0)
            statement.executeUpdate()
        }
    }

    fun deletePhysicalRoom(id: String) {
        connection.prepareStatement("DELETE FROM physical_rooms WHERE id = ?").use { statement ->
            statement.setString(1, id)
            statement.executeUpdate()
        }
    }

    fun getRoom(id: String): Room? = connection.prepareStatement(
        """
        SELECT id, room_number, guest_name, identification_id, guest_count, has_breakfast, breakfast_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis, contract_amount, booking_source, agency_name, settlement_type, agency_amount, guest_amount
        FROM rooms
        WHERE id = ?
        """.trimIndent()
    ).use { statement ->
        statement.setString(1, id)
        statement.executeQuery().use { rows ->
            if (!rows.next()) {
                null
            } else {
                Room(
                    id = rows.getString("id"),
                    roomNumber = rows.getString("room_number"),
                    guestName = rows.getString("guest_name"),
                    identificationId = rows.getString("identification_id") ?: "",
                    guestCount = rows.getInt("guest_count"),
                    hasBreakfast = rows.getInt("has_breakfast") == 1,
                    breakfastCount = rows.getInt("breakfast_count"),
                    checkInDate = rows.getString("check_in_date"),
                    checkOutDate = rows.getString("check_out_date"),
                    checkInEpochMillis = rows.getLong("check_in_epoch_millis"),
                    checkOutEpochMillis = rows.getLong("check_out_epoch_millis"),
                    contractAmount = rows.getLong("contract_amount"),
                    bookingSource = BookingSource.valueOf(rows.getString("booking_source") ?: BookingSource.DIRECT.name),
                    agencyName = rows.getString("agency_name") ?: "",
                    settlementType = SettlementType.valueOf(rows.getString("settlement_type") ?: SettlementType.FULL_GUEST.name),
                    agencyAmount = rows.getLong("agency_amount"),
                    guestAmount = rows.getLong("guest_amount")
                )
            }
        }
    }

    fun getRoomByNumber(roomNumber: String): Room? {
        val normalized = roomNumber.normalizeDigits()
        return connection.prepareStatement(
            """
            SELECT id, room_number, guest_name, identification_id, guest_count, has_breakfast, breakfast_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis, contract_amount, booking_source, agency_name, settlement_type, agency_amount, guest_amount
            FROM rooms
            WHERE room_number = ?
            ORDER BY check_out_epoch_millis DESC
            LIMIT 1
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, normalized)
            statement.executeQuery().use { rows ->
                if (!rows.next()) {
                    null
                } else {
                    Room(
                        id = rows.getString("id"),
                        roomNumber = rows.getString("room_number"),
                        guestName = rows.getString("guest_name"),
                        identificationId = rows.getString("identification_id") ?: "",
                        guestCount = rows.getInt("guest_count"),
                        hasBreakfast = rows.getInt("has_breakfast") == 1,
                        breakfastCount = rows.getInt("breakfast_count"),
                        checkInDate = rows.getString("check_in_date"),
                        checkOutDate = rows.getString("check_out_date"),
                        checkInEpochMillis = rows.getLong("check_in_epoch_millis"),
                        checkOutEpochMillis = rows.getLong("check_out_epoch_millis"),
                        contractAmount = rows.getLong("contract_amount"),
                        bookingSource = BookingSource.valueOf(rows.getString("booking_source") ?: BookingSource.DIRECT.name),
                        agencyName = rows.getString("agency_name") ?: "",
                        settlementType = SettlementType.valueOf(rows.getString("settlement_type") ?: SettlementType.FULL_GUEST.name),
                        agencyAmount = rows.getLong("agency_amount"),
                        guestAmount = rows.getLong("guest_amount")
                    )
                }
            }
        }
    }

    fun upsertRoom(room: Room): String {
        val id = room.id.ifBlank { UUID.randomUUID().toString() }
        connection.prepareStatement(
            """
            INSERT INTO rooms(id, room_number, guest_name, identification_id, guest_count, has_breakfast, breakfast_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis, contract_amount, booking_source, agency_name, settlement_type, agency_amount, guest_amount)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                room_number = excluded.room_number,
                guest_name = excluded.guest_name,
                identification_id = excluded.identification_id,
                guest_count = excluded.guest_count,
                hasBreakfast = excluded.has_breakfast,
                breakfast_count = excluded.breakfast_count,
                check_in_date = excluded.check_in_date,
                check_out_date = excluded.check_out_date,
                check_in_epoch_millis = excluded.check_in_epoch_millis,
                check_out_epoch_millis = excluded.check_out_epoch_millis,
                contract_amount = excluded.contract_amount,
                booking_source = excluded.booking_source,
                agency_name = excluded.agency_name,
                settlement_type = excluded.settlement_type,
                agency_amount = excluded.agency_amount,
                guest_amount = excluded.guest_amount
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, id)
            statement.setString(2, room.roomNumber)
            statement.setString(3, room.guestName)
            statement.setString(4, room.identificationId)
            statement.setInt(5, room.guestCount)
            statement.setInt(6, if (room.hasBreakfast) 1 else 0)
            statement.setInt(7, room.breakfastCount)
            statement.setString(8, room.checkInDate)
            statement.setString(9, room.checkOutDate)
            statement.setLong(10, room.checkInEpochMillis)
            statement.setLong(11, room.checkOutEpochMillis)
            statement.setLong(12, room.contractAmount)
            statement.setString(13, room.bookingSource.name)
            statement.setString(14, room.agencyName)
            statement.setString(15, room.settlementType.name)
            statement.setLong(16, room.agencyAmount)
            statement.setLong(17, room.guestAmount)
            statement.executeUpdate()
        }
        return id
    }

    fun updateRoomStay(
        id: String,
        roomNumber: String,
        guestName: String,
        identificationId: String,
        checkIn: String,
        checkOut: String,
        checkInMillis: Long,
        checkOutMillis: Long,
        guestCount: Int,
        hasBreakfast: Boolean,
        breakfastCount: Int,
        contractAmount: Long,
        bookingSource: BookingSource = BookingSource.DIRECT,
        agencyName: String = "",
        settlementType: SettlementType = SettlementType.FULL_GUEST,
        agencyAmount: Long = 0,
        guestAmount: Long = 0
    ): Boolean {
        val oldRoom = getRoom(id) ?: return false
        val newRoomNumber = roomNumber.normalizeDigits()

        if (oldRoom.roomNumber != newRoomNumber) {
            connection.prepareStatement("UPDATE food_reservations SET room_number = ? WHERE room_number = ?").use { statement ->
                statement.setString(1, newRoomNumber)
                statement.setString(2, oldRoom.roomNumber)
                statement.executeUpdate()
            }
        }

        return connection.prepareStatement(
            """
            UPDATE rooms
            SET room_number = ?, guest_name = ?, identification_id = ?, check_in_date = ?, check_out_date = ?, check_in_epoch_millis = ?, check_out_epoch_millis = ?, guest_count = ?, hasBreakfast = ?, breakfast_count = ?, contract_amount = ?, booking_source = ?, agency_name = ?, settlement_type = ?, agency_amount = ?, guest_amount = ?
            WHERE id = ?
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, newRoomNumber)
            statement.setString(2, guestName)
            statement.setString(3, identificationId)
            statement.setString(4, checkIn)
            statement.setString(5, checkOut)
            statement.setLong(6, checkInMillis)
            statement.setLong(7, checkOutMillis)
            statement.setInt(8, guestCount)
            statement.setInt(9, if (hasBreakfast) 1 else 0)
            statement.setInt(10, breakfastCount)
            statement.setLong(11, contractAmount)
            statement.setString(12, bookingSource.name)
            statement.setString(13, agencyName)
            statement.setString(14, settlementType.name)
            statement.setLong(15, agencyAmount)
            statement.setLong(16, guestAmount)
            statement.setString(17, id)
            statement.executeUpdate() > 0
        }
    }

    fun deleteRoom(id: String) {
        val room = getRoom(id) ?: return
        connection.prepareStatement("DELETE FROM food_reservations WHERE room_number = ?").use { statement ->
            statement.setString(1, room.roomNumber)
            statement.executeUpdate()
        }
        connection.prepareStatement("DELETE FROM rooms WHERE id = ?").use { statement ->
            statement.setString(1, id)
            statement.executeUpdate()
        }
    }

    fun getFoods(): List<FoodItem> = connection.prepareStatement(
        "SELECT id, name, type, day_type, is_active, is_visible_to_users, display_order FROM food_items ORDER BY day_type, type, display_order"
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    add(
                        FoodItem(
                            id = rows.getString("id"),
                            name = rows.getString("name"),
                            type = FoodType.valueOf(rows.getString("type")),
                            dayType = DayType.valueOf(rows.getString("day_type")),
                            isActive = rows.getInt("is_active") == 1,
                            isVisibleToUsers = rows.getInt("is_visible_to_users") == 1,
                            displayOrder = rows.getInt("display_order")
                        )
                    )
                }
            }
        }
    }

    fun upsertFood(food: FoodItem) {
        val id = food.id.ifBlank { UUID.randomUUID().toString() }
        connection.prepareStatement(
            """
            INSERT INTO food_items(id, name, type, day_type, is_active, is_visible_to_users, display_order)
            VALUES(?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                name = excluded.name,
                type = excluded.type,
                day_type = excluded.day_type,
                is_active = excluded.is_active,
                is_visible_to_users = excluded.is_visible_to_users,
                display_order = excluded.display_order
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, id)
            statement.setString(2, food.name)
            statement.setString(3, food.type.name)
            statement.setString(4, food.dayType.name)
            statement.setInt(5, if (food.isActive) 1 else 0)
            statement.setInt(6, if (food.isVisibleToUsers) 1 else 0)
            statement.setInt(7, food.displayOrder)
            statement.executeUpdate()
        }
    }

    fun deleteFood(id: String) {
        connection.prepareStatement("DELETE FROM food_items WHERE id = ?").use { statement ->
            statement.setString(1, id)
            statement.executeUpdate()
        }
    }

    fun getMenuConfigs(): List<MenuConfig> = connection.prepareStatement(
        "SELECT day_type, food_type, is_enabled FROM menu_configs"
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    add(
                        MenuConfig(
                            dayType = DayType.valueOf(rows.getString("day_type")),
                            foodType = FoodType.valueOf(rows.getString("food_type")),
                            isEnabled = rows.getInt("is_enabled") == 1
                        )
                    )
                }
            }
        }
    }

    fun upsertMenuConfig(config: MenuConfig) {
        connection.prepareStatement(
            """
            INSERT INTO menu_configs(day_type, food_type, is_enabled)
            VALUES(?, ?, ?)
            ON CONFLICT(day_type, food_type) DO UPDATE SET
                is_enabled = excluded.is_enabled
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, config.dayType.name)
            statement.setString(2, config.foodType.name)
            statement.setInt(3, if (config.isEnabled) 1 else 0)
            statement.executeUpdate()
        }
    }

    fun getReservations(): List<FoodReservation> = connection.prepareStatement(
        """
        SELECT room_number, date, guest_meal_selections, breakfast_count
        FROM food_reservations
        ORDER BY date, room_number
        """.trimIndent()
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    val selectionsJson = rows.getString("guest_meal_selections")
                    val selections = try {
                        json.decodeFromString<List<GuestMealSelection>>(selectionsJson)
                    } catch (e: Exception) {
                        println("list empty with error -> ${e.message}")
                        emptyList()
                    }
                    add(
                        FoodReservation(
                            roomNumber = rows.getString("room_number"),
                            date = rows.getString("date"),
                            guestMealSelections = selections,
                            breakfastCount = rows.getInt("breakfast_count")
                        )
                    )
                }
            }
        }
    }

    fun getReservationsForRoom(roomNumber: String): List<FoodReservation> =
        connection.prepareStatement(
            """
        SELECT room_number, date, guest_meal_selections, breakfast_count
        FROM food_reservations
        WHERE room_number = ?
        ORDER BY date
        """.trimIndent()
        ).use { statement ->
            statement.setString(1, roomNumber)
            statement.executeQuery().use { rows ->
                buildList {
                    while (rows.next()) {
                        val selectionsJson = rows.getString("guest_meal_selections")
                        val selections = try {
                            Json.decodeFromString<List<GuestMealSelection>>(selectionsJson)
                        } catch (e: Exception) {
                            println("list empty with error -> ${e.message}")
                            emptyList()
                        }
                        add(
                            FoodReservation(
                                roomNumber = rows.getString("room_number"),
                                date = rows.getString("date"),
                                guestMealSelections = selections,
                                breakfastCount = rows.getInt("breakfast_count")
                            )
                        )
                    }
                }
            }
        }

    fun saveReservation(reservation: FoodReservation) {
        connection.prepareStatement(
            """
            INSERT INTO food_reservations(room_number, date, guest_meal_selections, breakfast_count)
            VALUES(?, ?, ?, ?)
            ON CONFLICT(room_number, date) DO UPDATE SET
                guest_meal_selections = excluded.guest_meal_selections,
                breakfast_count = excluded.breakfast_count
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, reservation.roomNumber)
            statement.setString(2, reservation.date)
            statement.setString(3, json.encodeToString(reservation.guestMealSelections))
            statement.setInt(4, reservation.breakfastCount)
            statement.executeUpdate()
        }
    }

    private fun createTables() {
        connection.createStatement().use { statement ->
            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS rooms(
                    id TEXT PRIMARY KEY,
                    room_number TEXT NOT NULL,
                    capacity INTEGER NOT NULL DEFAULT 1,
                    guest_name TEXT NOT NULL,
                    identification_id TEXT,
                    guest_count INTEGER NOT NULL DEFAULT 1,
                    has_breakfast INTEGER NOT NULL DEFAULT 0,
                    breakfast_count INTEGER NOT NULL DEFAULT 0,
                    check_in_date TEXT NOT NULL,
                    check_out_date TEXT NOT NULL,
                    check_in_epoch_millis INTEGER NOT NULL,
                    check_out_epoch_millis INTEGER NOT NULL,
                    contract_amount INTEGER NOT NULL DEFAULT 0,
                    booking_source TEXT NOT NULL DEFAULT 'DIRECT',
                    agency_name TEXT NOT NULL DEFAULT '',
                    settlement_type TEXT NOT NULL DEFAULT 'FULL_GUEST',
                    agency_amount INTEGER NOT NULL DEFAULT 0,
                    guest_amount INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )

            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS financial_transactions(
                    id TEXT PRIMARY KEY,
                    room_id TEXT,
                    title TEXT NOT NULL,
                    description TEXT,
                    amount INTEGER NOT NULL,
                    transaction_type TEXT NOT NULL,
                    payment_date TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    notes TEXT,
                    payment_method TEXT NOT NULL,
                    status TEXT NOT NULL,
                    FOREIGN KEY(room_id) REFERENCES rooms(id)
                )
                """.trimIndent()
            )

            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS food_items(
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    day_type TEXT NOT NULL DEFAULT 'EVEN',
                    is_active INTEGER NOT NULL DEFAULT 1,
                    is_visible_to_users INTEGER NOT NULL DEFAULT 1,
                    display_order INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )

            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS menu_configs(
                    day_type TEXT NOT NULL,
                    food_type TEXT NOT NULL,
                    is_enabled INTEGER NOT NULL DEFAULT 1,
                    PRIMARY KEY(day_type, food_type)
                )
                """.trimIndent()
            )

            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS food_reservations(
                    room_number TEXT NOT NULL,
                    date TEXT NOT NULL,
                    guest_meal_selections TEXT NOT NULL,
                    breakfast_count INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(room_number, date),
                    FOREIGN KEY(room_number) REFERENCES rooms(room_number)
                )
                """.trimIndent()
            )

            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS physical_rooms(
                    id TEXT PRIMARY KEY,
                    room_number TEXT NOT NULL,
                    bed_count INTEGER NOT NULL DEFAULT 1,
                    capacity INTEGER NOT NULL DEFAULT 1,
                    type TEXT NOT NULL DEFAULT 'STANDARD',
                    is_active INTEGER NOT NULL DEFAULT 1
                )
                """.trimIndent()
            )
        }
    }

    private fun migrateIfNeeded() {
        val resColumns = mutableSetOf<String>()
        connection.metaData.getColumns(null, null, "food_reservations", null).use { rs ->
            while (rs.next()) {
                resColumns.add(rs.getString("COLUMN_NAME"))
            }
        }
        if (!resColumns.contains("breakfast_count")) {
            connection.createStatement().use { it.executeUpdate("ALTER TABLE food_reservations ADD COLUMN breakfast_count INTEGER NOT NULL DEFAULT 0") }
        }

        val roomColumns = mutableSetOf<String>()
        connection.metaData.getColumns(null, null, "rooms", null).use { rs ->
            while (rs.next()) {
                roomColumns.add(rs.getString("COLUMN_NAME"))
            }
        }

        connection.createStatement().use { statement ->
            if (!roomColumns.contains("id")) {
                println("Migrating database: Adding 'id' column to 'rooms' table...")
                // In SQLite, changing PK is hard. We'll recreate the table.
                statement.executeUpdate("ALTER TABLE rooms RENAME TO rooms_old")
                statement.executeUpdate(
                    """
                    CREATE TABLE rooms(
                        id TEXT PRIMARY KEY,
                        room_number TEXT NOT NULL,
                        capacity INTEGER NOT NULL DEFAULT 1,
                        guest_name TEXT NOT NULL,
                        identification_id TEXT,
                        guest_count INTEGER NOT NULL DEFAULT 1,
                        check_in_date TEXT NOT NULL,
                        check_out_date TEXT NOT NULL,
                        check_in_epoch_millis INTEGER NOT NULL,
                        check_out_epoch_millis INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                statement.executeUpdate(
                    """
                    INSERT INTO rooms (id, room_number, capacity, guest_name, identification_id, guest_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis)
                    SELECT room_number, room_number, capacity, guest_name, identification_id, guest_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis FROM rooms_old
                    """.trimIndent()
                )
                statement.executeUpdate("DROP TABLE rooms_old")
                println("Migration completed successfully.")
            }
            
            // Re-fetch columns after possible recreation
            roomColumns.clear()
            connection.metaData.getColumns(null, null, "rooms", null).use { rs ->
                while (rs.next()) {
                    roomColumns.add(rs.getString("COLUMN_NAME"))
                }
            }

            if (!roomColumns.contains("breakfast_count")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN breakfast_count INTEGER NOT NULL DEFAULT 0")
            }
            if (!roomColumns.contains("has_breakfast")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN has_breakfast INTEGER NOT NULL DEFAULT 0")
            }
            if (!roomColumns.contains("identification_id")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN identification_id TEXT")
            }
            if (!roomColumns.contains("capacity")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN capacity INTEGER NOT NULL DEFAULT 1")
            }
            if (!roomColumns.contains("contract_amount")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN contract_amount INTEGER NOT NULL DEFAULT 0")
            }
            if (!roomColumns.contains("booking_source")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN booking_source TEXT NOT NULL DEFAULT 'DIRECT'")
            }
            if (!roomColumns.contains("agency_name")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN agency_name TEXT NOT NULL DEFAULT ''")
            }
            if (!roomColumns.contains("settlement_type")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN settlement_type TEXT NOT NULL DEFAULT 'FULL_GUEST'")
            }
            if (!roomColumns.contains("agency_amount")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN agency_amount INTEGER NOT NULL DEFAULT 0")
            }
            if (!roomColumns.contains("guest_amount")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN guest_amount INTEGER NOT NULL DEFAULT 0")
            }
        }

        val txColumns = mutableSetOf<String>()
        connection.metaData.getColumns(null, null, "financial_transactions", null).use { rs ->
            while (rs.next()) {
                txColumns.add(rs.getString("COLUMN_NAME"))
            }
        }
        
        connection.createStatement().use { statement ->
            var isRoomIdNotNull = false
            connection.metaData.getColumns(null, null, "financial_transactions", "room_id").use { rs ->
                if (rs.next()) {
                    isRoomIdNotNull = rs.getInt("NULLABLE") == 0 // columnNoNulls = 0
                }
            }

            if (isRoomIdNotNull) {
                println("Migrating financial_transactions: making room_id nullable...")
                statement.executeUpdate("ALTER TABLE financial_transactions RENAME TO financial_transactions_old")
                statement.executeUpdate(
                    """
                    CREATE TABLE financial_transactions(
                        id TEXT PRIMARY KEY,
                        room_id TEXT,
                        title TEXT NOT NULL,
                        description TEXT,
                        amount INTEGER NOT NULL,
                        transaction_type TEXT NOT NULL,
                        payment_date TEXT NOT NULL,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL,
                        notes TEXT,
                        payment_method TEXT NOT NULL,
                        status TEXT NOT NULL,
                        FOREIGN KEY(room_id) REFERENCES rooms(id)
                    )
                    """.trimIndent()
                )
                statement.executeUpdate(
                    """
                    INSERT INTO financial_transactions (id, room_id, title, description, amount, transaction_type, payment_date, created_at, updated_at, notes, payment_method, status)
                    SELECT id, room_id, title, description, amount, transaction_type, payment_date, created_at, updated_at, notes, payment_method, status FROM financial_transactions_old
                    """.trimIndent()
                )
                statement.executeUpdate("DROP TABLE financial_transactions_old")
            }
        }

        val foodColumns = mutableSetOf<String>()
        connection.metaData.getColumns(null, null, "food_items", null).use { rs ->
            while (rs.next()) {
                foodColumns.add(rs.getString("COLUMN_NAME"))
            }
        }

        connection.createStatement().use { statement ->
            if (!foodColumns.contains("day_type")) {
                statement.executeUpdate("ALTER TABLE food_items ADD COLUMN day_type TEXT NOT NULL DEFAULT 'EVEN'")
            }
            if (!foodColumns.contains("is_active")) {
                statement.executeUpdate("ALTER TABLE food_items ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1")
            }
            if (!foodColumns.contains("is_visible_to_users")) {
                statement.executeUpdate("ALTER TABLE food_items ADD COLUMN is_visible_to_users INTEGER NOT NULL DEFAULT 1")
            }
            if (!foodColumns.contains("display_order")) {
                statement.executeUpdate("ALTER TABLE food_items ADD COLUMN display_order INTEGER NOT NULL DEFAULT 0")
            }
        }

        val physicalRoomColumns = mutableSetOf<String>()
        connection.metaData.getColumns(null, null, "physical_rooms", null).use { rs ->
            while (rs.next()) {
                physicalRoomColumns.add(rs.getString("COLUMN_NAME"))
            }
        }
        if (!physicalRoomColumns.contains("is_active")) {
            connection.createStatement().use { statement ->
                statement.executeUpdate("ALTER TABLE physical_rooms ADD COLUMN is_active INTEGER NOT NULL DEFAULT 1")
            }
        }
    }

    private fun seedDefaults() {
        if (getFoods().isEmpty()) {
            val defaultFoods = listOf(
                // روزهای زوج - ناهار
                FoodItem(
                    name = "چلو جوجه",
                    type = FoodType.LUNCH,
                    dayType = DayType.EVEN,
                    displayOrder = 0
                ),
                FoodItem(
                    name = "خورشت قیمه",
                    type = FoodType.LUNCH,
                    dayType = DayType.EVEN,
                    displayOrder = 1
                ),
                FoodItem(
                    name = "چلو کباب نگینی",
                    type = FoodType.LUNCH,
                    dayType = DayType.EVEN,
                    displayOrder = 2
                ),
                FoodItem(
                    name = "مرغ ربی",
                    type = FoodType.LUNCH,
                    dayType = DayType.EVEN,
                    displayOrder = 3
                ),

                // روزهای زوج - شام
                FoodItem(
                    name = "شنیسل مرغ",
                    type = FoodType.DINNER,
                    dayType = DayType.EVEN,
                    displayOrder = 0
                ),
                FoodItem(
                    name = "خوراک لقمه",
                    type = FoodType.DINNER,
                    dayType = DayType.EVEN,
                    displayOrder = 1
                ),
                FoodItem(
                    name = "رولت گوشت",
                    type = FoodType.DINNER,
                    dayType = DayType.EVEN,
                    displayOrder = 2
                ),
                FoodItem(
                    name = "میرزا قاسمی",
                    type = FoodType.DINNER,
                    dayType = DayType.EVEN,
                    displayOrder = 3
                ),
                FoodItem(
                    name = "عدس پلو",
                    type = FoodType.DINNER,
                    dayType = DayType.EVEN,
                    displayOrder = 4
                ),
                FoodItem(
                    name = "جوجه",
                    type = FoodType.DINNER,
                    dayType = DayType.EVEN,
                    displayOrder = 5
                ),

                // روزهای فرد - ناهار
                FoodItem(
                    name = "چلو کباب کوبیده",
                    type = FoodType.LUNCH,
                    dayType = DayType.ODD,
                    displayOrder = 0
                ),
                FoodItem(
                    name = "چلو خورشت قرمه سبزی",
                    type = FoodType.LUNCH,
                    dayType = DayType.ODD,
                    displayOrder = 1
                ),
                FoodItem(
                    name = "چلو کباب نگینی",
                    type = FoodType.LUNCH,
                    dayType = DayType.ODD,
                    displayOrder = 2
                ),
                FoodItem(
                    name = "چلو مرغ ربی",
                    type = FoodType.LUNCH,
                    dayType = DayType.ODD,
                    displayOrder = 3
                ),
                FoodItem(
                    name = "چلو جوجه",
                    type = FoodType.LUNCH,
                    dayType = DayType.ODD,
                    displayOrder = 4
                ),

                // روزهای فرد - شام
                FoodItem(
                    name = "شنیسل مرغ",
                    type = FoodType.DINNER,
                    dayType = DayType.ODD,
                    displayOrder = 0
                ),
                FoodItem(
                    name = "کوفته تبریزی",
                    type = FoodType.DINNER,
                    dayType = DayType.ODD,
                    displayOrder = 1
                ),
                FoodItem(
                    name = "ماکارانی",
                    type = FoodType.DINNER,
                    dayType = DayType.ODD,
                    displayOrder = 2
                ),
                FoodItem(
                    name = "خوراک لقمه",
                    type = FoodType.DINNER,
                    dayType = DayType.ODD,
                    displayOrder = 3
                ),
                FoodItem(
                    name = "جوجه",
                    type = FoodType.DINNER,
                    dayType = DayType.ODD,
                    displayOrder = 4
                ),

                // جمعه - ناهار
                FoodItem(
                    name = "خورشت قیمه",
                    type = FoodType.LUNCH,
                    dayType = DayType.FRIDAY,
                    displayOrder = 0
                ),
                FoodItem(
                    name = "چلو کباب نگینی",
                    type = FoodType.LUNCH,
                    dayType = DayType.FRIDAY,
                    displayOrder = 1
                ),
                FoodItem(
                    name = "چلو جوجه",
                    type = FoodType.LUNCH,
                    dayType = DayType.FRIDAY,
                    displayOrder = 2
                ),
                FoodItem(
                    name = "چلو مرغ ربی",
                    type = FoodType.LUNCH,
                    dayType = DayType.FRIDAY,
                    displayOrder = 3
                ),

                // جمعه - شام
                FoodItem(
                    name = "خوراک لقمه",
                    type = FoodType.DINNER,
                    dayType = DayType.FRIDAY,
                    displayOrder = 0
                ),
                FoodItem(
                    name = "شنیسل مرغ",
                    type = FoodType.DINNER,
                    dayType = DayType.FRIDAY,
                    displayOrder = 1
                ),
                FoodItem(
                    name = "رولت گوشت",
                    type = FoodType.DINNER,
                    dayType = DayType.FRIDAY,
                    displayOrder = 2
                ),
                FoodItem(
                    name = "ماکارانی",
                    type = FoodType.DINNER,
                    dayType = DayType.FRIDAY,
                    displayOrder = 3
                ),
                FoodItem(
                    name = "جوجه",
                    type = FoodType.DINNER,
                    dayType = DayType.FRIDAY,
                    displayOrder = 4
                )
            )
            defaultFoods.forEach(::upsertFood)
        }

        // Ensure all menu configs exist
        DayType.entries.forEach { dayType ->
            FoodType.entries.forEach { foodType ->
                val exists = connection.prepareStatement("SELECT 1 FROM menu_configs WHERE day_type = ? AND food_type = ?")
                    .use { stmt ->
                        stmt.setString(1, dayType.name)
                        stmt.setString(2, foodType.name)
                        stmt.executeQuery().next()
                    }
                if (!exists) {
                    upsertMenuConfig(MenuConfig(dayType, foodType, true))
                }
            }
        }

        if (getRooms().isEmpty()) {
            listOf(
                Room(
                    id = UUID.randomUUID().toString(),
                    roomNumber = "101",
                    guestName = "رضا احمدی",
                    identificationId = "09121112233",
                    guestCount = 2,
                    checkInDate = "1402/08/01",
                    checkOutDate = "1402/08/05",
                    checkInEpochMillis = 1698823800000L,
                    checkOutEpochMillis = 1699169400000L
                ),
                Room(
                    id = UUID.randomUUID().toString(),
                    roomNumber = "102",
                    guestName = "مریم علوی",
                    identificationId = "09124445566",
                    guestCount = 1,
                    checkInDate = "1402/08/02",
                    checkOutDate = "1402/08/06",
                    checkInEpochMillis = 1698910200000L,
                    checkOutEpochMillis = 1699255800000L
                ),
                Room(
                    id = UUID.randomUUID().toString(),
                    roomNumber = "103",
                    guestName = "محمد محمدی",
                    identificationId = "09127778899",
                    guestCount = 3,
                    checkInDate = "1402/08/05",
                    checkOutDate = "1402/08/10",
                    checkInEpochMillis = 1699169400000L,
                    checkOutEpochMillis = 1699601400000L
                )
            ).forEach(::upsertRoom)
        }
    }
}
