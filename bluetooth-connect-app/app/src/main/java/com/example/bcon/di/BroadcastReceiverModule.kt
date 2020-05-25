package com.example.bcon.di

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import com.example.bcon.broadcastreceivers.BluetoothReceiver
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val broadcastReceiverModule = module {
    single<BluetoothReceiver> { provideBluetoothReceiver(androidApplication()) }
}

fun provideBluetoothReceiver(application: Application): BluetoothReceiver {
    val bluetoothReceiver = BluetoothReceiver()
    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    application.registerReceiver(bluetoothReceiver, filter)
    return bluetoothReceiver
}