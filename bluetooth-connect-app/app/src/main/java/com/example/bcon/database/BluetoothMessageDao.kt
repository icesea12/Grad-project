package com.example.bcon.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BluetoothMessageDao {
    @Insert
    fun insert(databaseBluetoothMessage: DatabaseBluetoothMessage)

    @Query("DELETE FROM $MESSAGES_TABLE_NAME")
    fun clearDatabase()

    @Query(
        """SELECT *
            FROM $MESSAGES_TABLE_NAME
            ORDER BY id DESC"""
    )
    fun getAllMessagesAsLiveData(): LiveData<List<DatabaseBluetoothMessage>>

    @Query(
        """SELECT *
           FROM $MESSAGES_TABLE_NAME
           ORDER BY id DESC
           LIMIT 1"""
    )
    fun getLastMessage(): DatabaseBluetoothMessage
}