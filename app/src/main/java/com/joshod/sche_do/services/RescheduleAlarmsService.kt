package com.joshod.sche_do.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.joshod.sche_do.models.Task
import com.orm.SugarRecord
import java.text.SimpleDateFormat
import java.util.*

class RescheduleAlarmsService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val tasks = SugarRecord.listAll(Task::class.java)
        tasks.forEach { task ->
            val time = task.time
            val date = SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(time)
            val calendar = Calendar.getInstance()
            calendar.time = date!!
            val calendarActual = Calendar.getInstance()
            calendarActual.timeInMillis = System.currentTimeMillis()
            calendarActual.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            calendarActual.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            calendarActual.set(Calendar.SECOND, 0)
            calendarActual.set(Calendar.MILLISECOND, 0)

            task.schedule(applicationContext, calendarActual)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
