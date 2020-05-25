package com.example.bcon.domain

data class PlotBluetoothMessage(
    val time: Long,
    val signalStrength: Int
)

fun BluetoothMessage.toPlotMessage() =
    PlotBluetoothMessage(
        this.time,
        this.bluetoothMessageType.getSignalStrength()
    )