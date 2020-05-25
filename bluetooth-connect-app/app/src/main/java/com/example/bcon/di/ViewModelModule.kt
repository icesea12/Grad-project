package com.example.bcon.di

import com.example.bcon.broadcastreceivers.BluetoothReceiver
import com.example.bcon.repository.BluetoothMessageRepository
import com.example.bcon.ui.bluetooth.BluetoothViewModel
import com.example.bcon.ui.logs.LogsViewModel
import com.example.bcon.ui.plot.PlotViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LogsViewModel(get<BluetoothMessageRepository>()) }
    viewModel { PlotViewModel(get<BluetoothMessageRepository>()) }
    viewModel { BluetoothViewModel(get<BluetoothReceiver>()) }
}
