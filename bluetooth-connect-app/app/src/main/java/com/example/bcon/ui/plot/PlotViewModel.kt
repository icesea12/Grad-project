package com.example.bcon.ui.plot

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.bcon.domain.BluetoothMessage
import com.example.bcon.domain.BluetoothMessageType
import com.example.bcon.domain.toPlotMessage
import com.example.bcon.repository.BluetoothMessageRepository

class PlotViewModel(
    bluetoothMessageRepository: BluetoothMessageRepository
) : ViewModel() {
    private val bluetoothMessages: LiveData<List<BluetoothMessage>> =
        bluetoothMessageRepository.getAllMessagesAsLiveData()
    val listOfPlotGsmMessages = Transformations.map(bluetoothMessages) { messageList ->
        messageList.filter { it.bluetoothMessageType is BluetoothMessageType.Gsm }
            .map { it.toPlotMessage() }
    }
    val listOfPlotRfMessages = Transformations.map(bluetoothMessages) { messageList ->
        messageList.filter { it.bluetoothMessageType is BluetoothMessageType.RfMeter }
            .map { it.toPlotMessage() }
    }
}
