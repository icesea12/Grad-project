package com.example.bcon.util

object Constants {
    const val CANCEL_FOREGROUND_NOTIFICATION_ID = 99
    const val WARNING_NOTIFICATION_ID = 100
    const val FOREGROUND_NOTIFICATION_ID = 101
    const val GSM_SIGNAL_STRENGTH_CRITICAL_THRESHOLD = -85 // Value of 17
    const val RF_METER_SIGNAL_STRENGTH_CRITICAL_THRESHOLD = -51 // Value of 360 (range  0-1023)
    const val ACTION_STOP_SERVICE = "StopSelf"
    const val DEVICE_EXTRA = "bluetoothDevice"
    const val DEVICE_UUID = "bluetoothDeviceUUID"
    const val CHANNEL_ID = "notificationChannel"
}