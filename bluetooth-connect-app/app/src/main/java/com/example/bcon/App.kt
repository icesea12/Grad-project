package com.example.bcon

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import com.example.bcon.di.broadcastReceiverModule
import com.example.bcon.di.databaseModule
import com.example.bcon.di.repositoryModule
import com.example.bcon.di.viewModelModule
import com.example.bcon.util.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        configureLogging()
        createNotificationChannel()
        configureDependencyInjection()
    }

    private fun configureLogging() {
        Timber.plant(Timber.DebugTree())
    }

    private fun configureDependencyInjection() {
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    viewModelModule,
                    databaseModule,
                    repositoryModule,
                    broadcastReceiverModule
                )
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Constants.CHANNEL_ID,
                "BCon Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
                enableLights(true)
                enableVibration(true)
                lightColor = Color.RED
            }
            val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
    }
}