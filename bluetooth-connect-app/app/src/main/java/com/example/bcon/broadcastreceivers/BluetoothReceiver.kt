package com.example.bcon.broadcastreceivers

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.koin.core.KoinComponent
import timber.log.Timber

class BluetoothReceiver : BroadcastReceiver() {
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val _bluetoothState = MutableLiveData(bluetoothAdapter.isEnabled)
    val bluetoothState: LiveData<Boolean> = _bluetoothState

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("received bluetooth broadcast\ncontext: $context, intent: $intent")
        intent?.let {
            val bluetoothState = intent.getIntExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR
            )
            when (bluetoothState) {
                BluetoothAdapter.STATE_ON -> _bluetoothState.postValue(true)
                else -> _bluetoothState.postValue(false)
            }
        }
    }
}
