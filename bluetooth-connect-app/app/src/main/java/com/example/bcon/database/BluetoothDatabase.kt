package com.example.bcon.database

import androidx.room.Database
import androidx.room.RoomDatabase

const val DATABASE_NAME = "bluetooth_messages_database"

@Database(
    entities = [DatabaseBluetoothMessage::class],
    version = 2,
    exportSchema = false
)
abstract class BluetoothDatabase : RoomDatabase() {
    abstract val bluetoothMessageDao: BluetoothMessageDao
}