package com.joshod.sche_do.models

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.joshod.sche_do.receivers.AlarmBroadcastReceiver
import com.joshod.sche_do.receivers.AlarmBroadcastReceiver.Companion.NAME
import com.orm.SugarRecord
import java.util.*


open class Task: SugarRecord{
    var name: String = ""
    var time: String = ""

    companion object {
        fun makeCalendar(hour: Int, minute: Int): Calendar{
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar
        }

        var SCHEDULED = "SCHEDULED"
        var UNSCHEDULED = "UNSCHEDULED"
    }

    constructor(){

    }

    constructor(name: String, time: String){
        this.name = name
        this.time = time
    }

    open fun makeAlarmBroadcastIntent(context: Context): Intent{
        return Intent(context, AlarmBroadcastReceiver::class.java)
    }

    open fun makeAlarmManager(context: Context): AlarmManager{
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    open fun makePendingIntent(context: Context, intent: Intent, flag: Int): PendingIntent{
        return PendingIntent.getBroadcast(
            context,
            idToInt(),
            intent,
            flag
        )
    }

    open fun makeToastAndLog(context: Context, tag: String, toastText: String){
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
        Log.i(tag, toastText)
    }

    fun idToInt(): Int{
        return id.toInt()
    }

    fun scheduledAlarmText(): String{
        return String.format("Alarm %s scheduled for %s at %s", name, Calendar.DAY_OF_WEEK, time)
    }

    fun cancelAlarmText(): String{
        return String.format(
            "Alarm cancelled for %s at %s",
            name,
            time
        )
    }

    fun schedule(context: Context, calendar: Calendar) {
        val alarmManager = makeAlarmManager(context)
        val intent = makeAlarmBroadcastIntent(context)
        intent.putExtra(NAME, name)
        val alarmPendingIntent = makePendingIntent(context, intent, 0)

        if(calendar.timeInMillis > System.currentTimeMillis()) {
            makeToastAndLog(context, SCHEDULED, scheduledAlarmText())
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmPendingIntent
            )
        }
    }

    open fun unschedule(context: Context?) { // any() not be null. testing can't match
        val alarmManager = makeAlarmManager(context!!)
        val intent = makeAlarmBroadcastIntent(context)
        val alarmPendingIntent = makePendingIntent(context, intent, 0)
        alarmManager.cancel(alarmPendingIntent)
        makeToastAndLog(context, UNSCHEDULED, cancelAlarmText())
    }

}