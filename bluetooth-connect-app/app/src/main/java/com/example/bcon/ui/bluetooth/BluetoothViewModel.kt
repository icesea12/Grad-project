package com.example.bcon.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bcon.broadcastreceivers.BluetoothReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BluetoothViewModel(
    bluetoothReceiver: BluetoothReceiver
) : ViewModel() {
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    // Device list
    private val _listOfDevices = MutableLiveData<List<BluetoothDevice>>()
    val listOfDevices: LiveData<List<BluetoothDevice>> = _listOfDevices

    // Bluetooth state
    val bluetoothState = bluetoothReceiver.bluetoothState

    fun searchForDevices() = viewModelScope.launch(Dispatchers.Default) {
        val pairedDevices = bluetoothAdapter.bondedDevices.toList()
        _listOfDevices.postValue(pairedDevices)
    }
}