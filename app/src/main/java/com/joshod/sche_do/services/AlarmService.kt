package com.joshod.sche_do.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.PendingIntent
import android.content.Context
import android.os.Vibrator
import android.media.MediaPlayer
import com.joshod.sche_do.App.Companion.CHANNEL_ID
import com.joshod.sche_do.activities.RingActivity
import com.joshod.sche_do.receivers.AlarmBroadcastReceiver.Companion.NAME
import android.os.VibrationEffect
import android.os.Build
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.joshod.sche_do.R


class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, ringtone)
        mediaPlayer!!.isLooping = true
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, RingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val alarmTitle = String.format("%s Alarm", intent.getStringExtra(NAME))

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(alarmTitle)
            .setSmallIcon(R.drawable.ic_alarm_black_24dp)
            .setContentIntent(pendingIntent)
            .build()

        mediaPlayer!!.start()

        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator!!.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            vibrator!!.vibrate(500)
        }
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
        vibrator!!.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}
