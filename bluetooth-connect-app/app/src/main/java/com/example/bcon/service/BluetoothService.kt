package com.example.bcon.service

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.bcon.R
import com.example.bcon.database.BluetoothMessageDao
import com.example.bcon.database.DatabaseBluetoothMessage
import com.example.bcon.domain.BluetoothMessageType
import com.example.bcon.domain.isValidGsm
import com.example.bcon.domain.isValidRfMeter
import com.example.bcon.ui.MainActivity
import com.example.bcon.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.IOException
import java.util.*

class BluetoothService : Service(), KoinComponent {
    // Coroutine scopes
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // Socket
    private lateinit var socket: BluetoothSocket

    // Database dao
    private val bluetoothMessageDao by inject<BluetoothMessageDao>()

    // Buffer for the incoming data storing them until a full message is received
    val stringBuilder = StringBuilder("")

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(Constants.FOREGROUND_NOTIFICATION_ID, foregroundServiceNotification())
        return if (intent != null && intent.action == Constants.ACTION_STOP_SERVICE) {
            stopSelf()
            START_NOT_STICKY
        } else {
            val bundle = intent!!.extras!!
            val bluetoothDevice: BluetoothDevice = bundle.getParcelable(Constants.DEVICE_EXTRA)!!
            val deviceUuid: UUID = UUID.fromString(bundle.getString(Constants.DEVICE_UUID))
            Timber.d("Service received device: $bluetoothDevice\nuuid: $deviceUuid")
            serviceScope.launch {
                startReading(bluetoothDevice, deviceUuid)
            }
            START_REDELIVER_INTENT
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::socket.isInitialized) {
            socket.close()
        }
        serviceJob.cancel()
    }

    private suspend fun startReading(
        bluetoothDevice: BluetoothDevice,
        deviceUuid: UUID
    ) {
        val socket = connectBt(bluetoothDevice, deviceUuid) ?: return
        readInput(socket)
    }

    private suspend fun connectBt(
        bluetoothDevice: BluetoothDevice,
        deviceUuid: UUID
    ): BluetoothSocket? = withContext(Dispatchers.IO) {
        Timber.d("Connecting to bt socket")
        socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(deviceUuid)
        try {
            socket.connect() // TODO error handling
            return@withContext socket
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.d("The connection failed, the exception was: $e")
        }
        stopSelf() // TODO maybe not just stop this? Retry it? Something else?
        null
    }

    private suspend fun readInput(socket: BluetoothSocket) = withContext(Dispatchers.IO) {
        Timber.d("Reading from socket")
        val inputStream = socket.inputStream
        while (true) {
            Timber.d("Looping")
            if (inputStream.available() > 0) {
                val buffer = ByteArray(255)
                val bytesRead = inputStream.read(buffer)
                Timber.d("Bytes quantity read: $bytesRead")
                //I can get bluetooth device from socket so I will pass it here
                handleDataRead(buffer, bytesRead, socket.remoteDevice)
            } else {
                delay(1000)
            }
        }
    }

    private fun handleDataRead(data: ByteArray, length: Int, bluetoothDevice: BluetoothDevice) {
        val stringRead = byteArrayToString(data, length)
        stringBuilder.append(stringRead)
        val extractedData = extractData() ?: return
        if (isValidData(extractedData)) {
            Timber.d("Valid extracted data: $extractedData")
            insertToDatabase(extractedData, bluetoothDevice)
            handleNotificationCase(extractedData)
        } else {
            Timber.d("Invalid extracted data: $extractedData")
        }
    }

    fun byteArrayToString(data: ByteArray, length: Int): String {
        val stringRead = String(data, 0, length)
        Timber.d("String read was: $stringRead")
        return stringRead
    }

    fun extractData(): String? {
        val hashTagIndex = stringBuilder.indexOfFirst { it == '#' }
        if (hashTagIndex == -1) {
            return null
        }
        // Extract value including '#'
        val fullValue = stringBuilder.substring(0, hashTagIndex.plus(1))
        // Remove up to '#' inclusive
        stringBuilder.replace(0, hashTagIndex.plus(1), "")
        Timber.d("Returning extracted full data: $fullValue")
        return fullValue
    }

    private fun handleNotificationCase(rawData: String) {
        if (BluetoothMessageType.fromRawData(rawData).hasCriticalSignalStrength()) {
            Timber.d("Sending warning notification")
            sendWarningNotification()
        }
    }

    private fun insertToDatabase(rawData: String, bluetoothDevice: BluetoothDevice) {
        Timber.d("Inserting to database raw data: $rawData")
        val bluetoothMessage =
            DatabaseBluetoothMessage(data = rawData, macAdd = bluetoothDevice.address)
        Timber.d("Inserting to database: $bluetoothMessage")
        bluetoothMessageDao.insert(bluetoothMessage)
        Timber.d("Done inserting to database: $bluetoothMessage")
    }

    fun isValidData(data: String): Boolean {
        return data.isValidGsm() || data.isValidRfMeter()
    }

    private fun foregroundServiceNotification(): Notification {
        // Action to stop itself
        val cancelServiceIntent = Intent(this, BluetoothService::class.java)
        cancelServiceIntent.action = Constants.ACTION_STOP_SERVICE
        val cancelServicePendingIntent = PendingIntent.getService(
            this,
            Constants.CANCEL_FOREGROUND_NOTIFICATION_ID,
            cancelServiceIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        // Intent to launch when notification is clicked. Launch the MainActivity
        val notificationClickedIntent = Intent(this, MainActivity::class.java)
        val notificationClickedPendingIntent = PendingIntent.getActivity(
            this,
            Constants.FOREGROUND_NOTIFICATION_ID,
            notificationClickedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat
            .Builder(this, Constants.CHANNEL_ID)
            // Do not allow user to delete/swipe this and set it as top notification
            .setOngoing(true)
            // Basic info
            .setContentText("Bluetooth Connection - Receiving values")
            .setSmallIcon(R.drawable.ic_bluetooth_black_24dp)
            // Intent on click
            .setContentIntent(notificationClickedPendingIntent)
            // Priority level
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Action to stop itself
            .addAction(
                R.drawable.ic_bluetooth_black_24dp,
                "Stop reading",
                cancelServicePendingIntent
            )
            .build()
    }

    private fun sendWarningNotification() {
        // Big picture style
        val notificationBigImage = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.this_is_fine
        )
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(notificationBigImage)
            .bigLargeIcon(null)
        // Intent to launch when notification is clicked. Launch the MainActivity
        val notificationClickedIntent = Intent(this, MainActivity::class.java)
        val notificationClickedPendingIntent = PendingIntent.getActivity(
            this,
            Constants.FOREGROUND_NOTIFICATION_ID,
            notificationClickedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Intent to launch when notification is clicked. Launch the MainActivity
        val notification = NotificationCompat
            .Builder(this, Constants.CHANNEL_ID)
            // Basic info
            .setContentText(getString(R.string.critical_value_warning))
            .setSmallIcon(R.drawable.ic_bluetooth_black_24dp)
            // Intent on click
            .setContentIntent(notificationClickedPendingIntent)
            // Priority level
            .setPriority(NotificationCompat.PRIORITY_MAX)
            // Big picture style
            .setStyle(bigPictureStyle)
            .setLargeIcon(notificationBigImage)
            // Cancel on click
            .setAutoCancel(true)
            // Visibility
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        val manager = this.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Constants.WARNING_NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
