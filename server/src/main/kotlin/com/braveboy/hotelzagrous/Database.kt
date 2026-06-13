package com.braveboy.hotelzagrous

import com.braveboy.hotelzagrous.core.FoodItem
import com.braveboy.hotelzagrous.core.FoodReservation
import com.braveboy.hotelzagrous.core.FoodType
import com.braveboy.hotelzagrous.core.Room
import java.sql.Connection
import java.sql.DriverManager

class HotelDatabase(
    databasePath: String = "hotel-zagrous.sqlite"
) {
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$databasePath")

    init {
        createTables()
        seedDefaults()
    }

    fun getRooms(): List<Room> = connection.prepareStatement(
        """
        SELECT room_number, guest_name, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis
        FROM rooms
        ORDER BY room_number
        """.trimIndent()
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    add(
                        Room(
                            roomNumber = rows.getString("room_number"),
                            guestName = rows.getString("guest_name"),
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

    fun getRoom(roomNumber: String): Room? = connection.prepareStatement(
        """
        SELECT room_number, guest_name, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis
        FROM rooms
        WHERE room_number = ?
        """.trimIndent()
    ).use { statement ->
        statement.setString(1, roomNumber)
        statement.executeQuery().use { rows ->
            if (!rows.next()) {
                null
            } else {
                Room(
                    roomNumber = rows.getString("room_number"),
                    guestName = rows.getString("guest_name"),
                    checkInDate = rows.getString("check_in_date"),
                    checkOutDate = rows.getString("check_out_date"),
                    checkInEpochMillis = rows.getLong("check_in_epoch_millis"),
                    checkOutEpochMillis = rows.getLong("check_out_epoch_millis")
                )
            }
        }
    }

    fun upsertRoom(room: Room) {
        connection.prepareStatement(
            """
            INSERT INTO rooms(room_number, guest_name, check_in_date, check_out_date, check_in_epoch_millis, check_out_epoch_millis)
            VALUES(?, ?, ?, ?, ?, ?)
            ON CONFLICT(room_number) DO UPDATE SET
                guest_name = excluded.guest_name,
                check_in_date = excluded.check_in_date,
                check_out_date = excluded.check_out_date,
                check_in_epoch_millis = excluded.check_in_epoch_millis,
                check_out_epoch_millis = excluded.check_out_epoch_millis
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, room.roomNumber)
            statement.setString(2, room.guestName)
            statement.setString(3, room.checkInDate)
            statement.setString(4, room.checkOutDate)
            statement.setLong(5, room.checkInEpochMillis)
            statement.setLong(6, room.checkOutEpochMillis)
            statement.executeUpdate()
        }
    }

    fun updateRoomStay(roomNumber: String, checkIn: String, checkOut: String, checkInMillis: Long, checkOutMillis: Long): Boolean {
        return connection.prepareStatement(
            """
            UPDATE rooms
            SET check_in_date = ?, check_out_date = ?, check_in_epoch_millis = ?, check_out_epoch_millis = ?
            WHERE room_number = ?
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, checkIn)
            statement.setString(2, checkOut)
            statement.setLong(3, checkInMillis)
            statement.setLong(4, checkOutMillis)
            statement.setString(5, roomNumber)
            statement.executeUpdate() > 0
        }
    }

    fun getFoods(): List<FoodItem> = connection.prepareStatement(
        "SELECT id, name, type FROM food_items ORDER BY type, id"
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    add(
                        FoodItem(
                            id = rows.getString("id"),
                            name = rows.getString("name"),
                            type = FoodType.valueOf(rows.getString("type"))
                        )
                    )
                }
            }
        }
    }

    fun getReservations(): List<FoodReservation> = connection.prepareStatement(
        """
        SELECT room_number, date, lunch_food_id, dinner_food_id
        FROM food_reservations
        ORDER BY date, room_number
        """.trimIndent()
    ).use { statement ->
        statement.executeQuery().use { rows ->
            buildList {
                while (rows.next()) {
                    add(
                        FoodReservation(
                            roomNumber = rows.getString("room_number"),
                            date = rows.getString("date"),
                            lunchFoodId = rows.getString("lunch_food_id"),
                            dinnerFoodId = rows.getString("dinner_food_id")
                        )
                    )
                }
            }
        }
    }

    fun saveReservation(reservation: FoodReservation) {
        connection.prepareStatement(
            """
            INSERT INTO food_reservations(room_number, date, lunch_food_id, dinner_food_id)
            VALUES(?, ?, ?, ?)
            ON CONFLICT(room_number, date) DO UPDATE SET
                lunch_food_id = excluded.lunch_food_id,
                dinner_food_id = excluded.dinner_food_id
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, reservation.roomNumber)
            statement.setString(2, reservation.date)
            statement.setString(3, reservation.lunchFoodId)
            statement.setString(4, reservation.dinnerFoodId)
            statement.executeUpdate()
        }
    }

    private fun createTables() {
        connection.createStatement().use { statement ->
            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS rooms(
                    room_number TEXT PRIMARY KEY,
                    guest_name TEXT NOT NULL,
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
                    type TEXT NOT NULL
                )
                """.trimIndent()
            )
            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS food_reservations(
                    room_number TEXT NOT NULL,
                    date TEXT NOT NULL,
                    lunch_food_id TEXT,
                    dinner_food_id TEXT,
                    PRIMARY KEY(room_number, date),
                    FOREIGN KEY(room_number) REFERENCES rooms(room_number),
                    FOREIGN KEY(lunch_food_id) REFERENCES food_items(id),
                    FOREIGN KEY(dinner_food_id) REFERENCES food_items(id)
                )
                """.trimIndent()
            )
        }
    }

    private fun seedDefaults() {
        if (getFoods().isEmpty()) {
            listOf(
                FoodItem("1", "چلو کباب", FoodType.LUNCH),
                FoodItem("2", "جوجه کباب", FoodType.LUNCH),
                FoodItem("3", "خورشت قیمه", FoodType.LUNCH),
                FoodItem("4", "پیتزا مخصوص", FoodType.DINNER),
                FoodItem("5", "خوراک مرغ", FoodType.DINNER),
                FoodItem("6", "سوپ جو", FoodType.DINNER)
            ).forEach(::insertFood)
        }

        if (getRooms().isEmpty()) {
            listOf(
                Room("101", "رضا احمدی", "1402/08/01", "1402/08/05", 1698823800000L, 1699169400000L),
                Room("102", "مریم علوی", "1402/08/02", "1402/08/06", 1698910200000L, 1699255800000L),
                Room("103", "محمد محمدی", "1402/08/05", "1402/08/10", 1699169400000L, 1699601400000L)
            ).forEach(::upsertRoom)
        }
    }

    private fun insertFood(food: FoodItem) {
        connection.prepareStatement(
            "INSERT INTO food_items(id, name, type) VALUES(?, ?, ?)"
        ).use { statement ->
            statement.setString(1, food.id)
            statement.setString(2, food.name)
            statement.setString(3, food.type.name)
            statement.executeUpdate()
        }
    }
}
