package com.joshod.sche_do.models

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar
import android.util.Log
import android.widget.Toast
import com.joshod.sche_do.receivers.AlarmBroadcastReceiver
import com.joshod.sche_do.receivers.AlarmBroadcastReceiver.Companion.NAME
import com.orm.SugarRecord


class Task: SugarRecord{
    lateinit var name: String
    lateinit var time: String

    constructor(){

    }

    constructor(name: String, time: String){
        this.name = name
        this.time = time
    }

    fun schedule(context: Context, calendar: Calendar) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val Id = id.toInt()
        intent.putExtra(NAME, name)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, Id, intent, 0)

        var toastText = ""

        try {
            toastText = String.format("Alarm %s scheduled for %s at %s", name, Calendar.DAY_OF_WEEK, time, Id)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if(calendar.timeInMillis > System.currentTimeMillis()) {
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
            Log.i("schedule", toastText)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmPendingIntent
            )
        }
    }

    fun unschedule(context: Context) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, id.toInt(), intent, 0)
        alarmManager.cancel(alarmPendingIntent)
        val toastText = String.format(
            "Alarm cancelled for %s with id %d",
            time,
            id
        )
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        Log.i("unschedule", toastText)
    }

}