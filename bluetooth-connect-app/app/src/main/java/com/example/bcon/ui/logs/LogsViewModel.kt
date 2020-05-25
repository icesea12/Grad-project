package com.example.bcon.ui.logs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bcon.domain.BluetoothMessage
import com.example.bcon.repository.BluetoothMessageRepository
import kotlinx.coroutines.launch

class LogsViewModel(
    bluetoothMessageRepository: BluetoothMessageRepository
) : ViewModel() {
    val bluetoothMessages: LiveData<List<BluetoothMessage>> =
        bluetoothMessageRepository.getAllMessagesAsLiveData()


    init {
        viewModelScope.launch {
            //bluetoothMessageRepository.clearDatabase()
            //bluetoothMessageRepository.prePopulateDatabase()
        }
    }
}
