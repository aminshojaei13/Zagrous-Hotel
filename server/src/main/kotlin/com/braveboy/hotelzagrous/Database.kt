package com.braveboy.hotelzagrous

import com.braveboy.hotelzagrous.core.DayType
import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodType
import com.braveboy.hotelzagrous.core.Room
import com.braveboy.hotelzagrous.core.GuestMealSelection
import com.braveboy.hotelzagrous.core.MenuConfig
import com.braveboy.hotelzagrous.core.normalizeDigits
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID
import kotlinx.serialization.json.Json

class HotelDatabase(
    databasePath: String = "hotel-zagrous.sqlite"
) {
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$databasePath")

    init {
        createTables()
        migrateIfNeeded()
        seedDefaults()
    }

    fun clearAllData() {
        connection.createStatement().use { statement ->
            statement.executeUpdate("DELETE FROM food_reservations")
            statement.executeUpdate("DELETE FROM rooms")
        }
    }

    fun getRooms(): List<Room> = connection.prepareStatement(
        """
        SELECT id, room_number, guest_name, identification_id, guest_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis
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
                            checkInDate = rows.getString("check_in_date"),
                            checkOutDate = rows.getString("check_out_date"),
                            checkInEpochMillis = rows.getLong("check_in_epoch_millis"),
                            checkOutEpochMillis = rows.getLong("check_out_epoch_millis")
                        )
                    )
                }
            }
        }
    }

    fun getRoom(id: String): Room? = connection.prepareStatement(
        """
        SELECT id, room_number, guest_name, identification_id, guest_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis
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
                    checkInDate = rows.getString("check_in_date"),
                    checkOutDate = rows.getString("check_out_date"),
                    checkInEpochMillis = rows.getLong("check_in_epoch_millis"),
                    checkOutEpochMillis = rows.getLong("check_out_epoch_millis")
                )
            }
        }
    }

    fun getRoomByNumber(roomNumber: String): Room? {
        val normalized = roomNumber.normalizeDigits()
        return connection.prepareStatement(
            """
            SELECT id, room_number, guest_name, identification_id, guest_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis
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
                        checkInDate = rows.getString("check_in_date"),
                        checkOutDate = rows.getString("check_out_date"),
                        checkInEpochMillis = rows.getLong("check_in_epoch_millis"),
                        checkOutEpochMillis = rows.getLong("check_out_epoch_millis")
                    )
                }
            }
        }
    }

    fun upsertRoom(room: Room) {
        val id = room.id.ifBlank { UUID.randomUUID().toString() }
        connection.prepareStatement(
            """
            INSERT INTO rooms(id, room_number, guest_name, identification_id, guest_count, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                room_number = excluded.room_number,
                guest_name = excluded.guest_name,
                identification_id = excluded.identification_id,
                guest_count = excluded.guest_count,
                check_in_date = excluded.check_in_date,
                check_out_date = excluded.check_out_date,
                check_in_epoch_millis = excluded.check_in_epoch_millis,
                check_out_epoch_millis = excluded.check_out_epoch_millis
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, id)
            statement.setString(2, room.roomNumber)
            statement.setString(3, room.guestName)
            statement.setString(4, room.identificationId)
            statement.setInt(5, room.guestCount)
            statement.setString(6, room.checkInDate)
            statement.setString(7, room.checkOutDate)
            statement.setLong(8, room.checkInEpochMillis)
            statement.setLong(9, room.checkOutEpochMillis)
            statement.executeUpdate()
        }
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
        guestCount: Int
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
            SET room_number = ?, guest_name = ?, identification_id = ?, check_in_date = ?, check_out_date = ?, check_in_epoch_millis = ?, check_out_epoch_millis = ?, guest_count = ?
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
            statement.setString(9, id)
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
        SELECT room_number, date, guest_meal_selections
        FROM food_reservations
        ORDER BY date, room_number
        """.trimIndent()
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    val selectionsJson = rows.getString("guest_meal_selections")
                    val selections = try {
                        Json.decodeFromString<List<GuestMealSelection>>(selectionsJson)
                    } catch (e: Exception) {
                        emptyList()
                    }
                    add(
                        FoodReservation(
                            roomNumber = rows.getString("room_number"),
                            date = rows.getString("date"),
                            guestMealSelections = selections
                        )
                    )
                }
            }
        }
    }

    fun getReservationsForRoom(roomNumber: String): List<FoodReservation> =
        connection.prepareStatement(
            """
        SELECT room_number, date, guest_meal_selections
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
                            emptyList()
                        }
                        add(
                            FoodReservation(
                                roomNumber = rows.getString("room_number"),
                                date = rows.getString("date"),
                                guestMealSelections = selections
                            )
                        )
                    }
                }
            }
        }

    fun saveReservation(reservation: FoodReservation) {
        connection.prepareStatement(
            """
            INSERT INTO food_reservations(room_number, date, guest_meal_selections)
            VALUES(?, ?, ?)
            ON CONFLICT(room_number, date) DO UPDATE SET
                guest_meal_selections = excluded.guest_meal_selections
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, reservation.roomNumber)
            statement.setString(2, reservation.date)
            statement.setString(3, Json.encodeToString(reservation.guestMealSelections))
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
                    check_in_date TEXT NOT NULL,
                    check_out_date TEXT NOT NULL,
                    check_in_epoch_millis INTEGER NOT NULL,
                    check_out_epoch_millis INTEGER NOT NULL
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
                    PRIMARY KEY(room_number, date),
                    FOREIGN KEY(room_number) REFERENCES rooms(room_number)
                )
                """.trimIndent()
            )
        }
    }

    private fun migrateIfNeeded() {
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

            if (!roomColumns.contains("identification_id")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN identification_id TEXT")
            }
            if (!roomColumns.contains("capacity")) {
                statement.executeUpdate("ALTER TABLE rooms ADD COLUMN capacity INTEGER NOT NULL DEFAULT 1")
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

        if (getMenuConfigs().isEmpty()) {
            DayType.entries.forEach { dayType ->
                FoodType.entries.forEach { foodType ->
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
