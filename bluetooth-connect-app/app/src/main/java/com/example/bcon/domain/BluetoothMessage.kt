package com.example.bcon.domain

import org.joda.time.Instant
import java.util.*

data class BluetoothMessage(
    val id: Long = 0L,
    val time: Long = Instant().millis,
    val bluetoothMessageType: BluetoothMessageType,
    val macAdd: String
) {
    override fun toString(): String {
        return "ID: $id, Recorded at: ${Date(time)}, From: $macAdd, Data: $bluetoothMessageType"
    }
}