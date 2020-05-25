package com.example.bcon.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bcon.domain.BluetoothMessage
import com.example.bcon.domain.BluetoothMessageType
import com.example.bcon.util.deviceNameMappings
import org.joda.time.Instant

const val MESSAGES_TABLE_NAME = "bluetooth_messages"

@Entity(tableName = MESSAGES_TABLE_NAME)
data class DatabaseBluetoothMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val time: Long = Instant().millis,
    val data: String,
    val macAdd: String
)

//When data is fetched from database we need to parse it
fun List<DatabaseBluetoothMessage>.asDomainModel(): List<BluetoothMessage> {
    return map {
        BluetoothMessage(
            id = it.id,
            time = it.time,
            bluetoothMessageType = BluetoothMessageType.fromRawData(it.data),
            macAdd = it.macAdd.toDeviceName()
        )
    }
}

fun String.toDeviceName(): String =
    deviceNameMappings.getOrElse(this) {
        //Default value if that MAC is not part of the mappings
        "Unnamed device: $this"
    }
