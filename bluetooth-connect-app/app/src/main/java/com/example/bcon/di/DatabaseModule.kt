package com.example.bcon.di

import android.content.Context
import androidx.room.Room
import com.example.bcon.database.BluetoothDatabase
import com.example.bcon.database.DATABASE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { provideRoomDatabase(androidContext()) }
    single { get<BluetoothDatabase>().bluetoothMessageDao }
}

fun provideRoomDatabase(context: Context): BluetoothDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        BluetoothDatabase::class.java,
        DATABASE_NAME
    ).build()
}
