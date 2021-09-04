package com.joshod.sche_do.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.content.Intent
import android.widget.Toast
import com.joshod.sche_do.services.AlarmService
import com.joshod.sche_do.services.RescheduleAlarmsService


class AlarmBroadcastReceiver : BroadcastReceiver(){

    companion object{
        val NAME = "NAME"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val toastText = String.format("Alarm Reboot")
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            startRescheduleAlarmsService(context)
        } else {
            val toastText = String.format("Alarm Received")
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            startAlarmService(context, intent)
        }
    }

    private fun startAlarmService(context: Context, intent: Intent) {
        val intentService = Intent(context, AlarmService::class.java)
        intentService.putExtra(NAME, intent.getStringExtra(NAME))
        startIntentService(context, intentService)
    }

    private fun startRescheduleAlarmsService(context: Context) {
        val intentService = Intent(context, RescheduleAlarmsService::class.java)
        startIntentService(context, intentService)
    }

    private fun startIntentService(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(intent)
        else context.startService(intent)
    }

}