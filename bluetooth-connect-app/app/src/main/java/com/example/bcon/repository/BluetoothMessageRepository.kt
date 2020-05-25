package com.example.bcon.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.bcon.database.BluetoothMessageDao
import com.example.bcon.database.DatabaseBluetoothMessage
import com.example.bcon.database.asDomainModel
import com.example.bcon.domain.BluetoothMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BluetoothMessageRepository(
    private val bluetoothMessageDao: BluetoothMessageDao
) {
    fun getAllMessagesAsLiveData(): LiveData<List<BluetoothMessage>> =
        Transformations.map(bluetoothMessageDao.getAllMessagesAsLiveData()) { messageList ->
            messageList.asDomainModel()
        }

    suspend fun insert(databaseBluetoothMessage: DatabaseBluetoothMessage) {
        withContext(Dispatchers.IO) {
            bluetoothMessageDao.insert(databaseBluetoothMessage)
        }
    }

    suspend fun clearDatabase() {
        withContext(Dispatchers.IO) {
            bluetoothMessageDao.clearDatabase()
        }
    }

    suspend fun prePopulateDatabase() {
        withContext(Dispatchers.IO) {
            listOf(
                DatabaseBluetoothMessage(time = 1588329121000, data = "\r\nAT+CSQ\r\r\n+CSQ: 17,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588329121000, data = "420#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588415521000, data = "\r\nAT+CSQ\r\r\n+CSQ: 18,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588415521000, data = "410#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588501921000, data = "\r\nAT+CSQ\r\r\n+CSQ: 19,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588501921000, data = "400#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588588321000, data = "\r\nAT+CSQ\r\r\n+CSQ: 20,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588588321000, data = "390#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588677824000, data = "\r\nAT+CSQ\r\r\n+CSQ: 21,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588764224000, data = "\r\nAT+CSQ\r\r\n+CSQ: 19,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588850624000, data = "\r\nAT+CSQ\r\r\n+CSQ: 19,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588937024000, data = "\r\nAT+CSQ\r\r\n+CSQ: 17,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1589023424000, data = "\r\nAT+CSQ\r\r\n+CSQ: 15,99\r\n\r\nOK\r\n#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588677824000, data = "350#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588764224000, data = "380#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588850624000, data = "350#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1588937024000, data = "320#", macAdd = "77:77:77:77:77:77"),
                DatabaseBluetoothMessage(time = 1589023424000, data = "300#", macAdd = "77:77:77:77:77:77")



                    ).forEach(bluetoothMessageDao::insert)
        }
    }
}