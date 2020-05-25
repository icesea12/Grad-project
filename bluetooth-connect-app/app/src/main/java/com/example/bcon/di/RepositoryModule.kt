package com.example.bcon.di

import com.example.bcon.database.BluetoothMessageDao
import com.example.bcon.repository.BluetoothMessageRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { BluetoothMessageRepository(get<BluetoothMessageDao>()) }
}