package com.joshod.sche_do

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.orm.SugarContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SugarContext.init(this)
        createNotificationChannel()
    }

    override fun onTerminate() {
        SugarContext.terminate();
        super.onTerminate()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        val CHANNEL_ID = "ALARM_SERVICE_CHANNEL"
    }
}