package com.joshod.sche_do.models

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.joshod.sche_do.models.Task.Companion.UNSCHEDULED
import com.joshod.sche_do.receivers.AlarmBroadcastReceiver
import org.junit.Before
import org.junit.Test

import org.junit.experimental.runners.Enclosed

import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat
import java.util.*


@RunWith(Enclosed::class)
class EnclosedTaskTest {

    @RunWith(MockitoJUnitRunner::class)
    abstract class SharedSetUp {
        lateinit var task: Task
        lateinit var context: Context
        lateinit var intent: Intent
        lateinit var pendingIntent: PendingIntent
        lateinit var calendar: Calendar
        lateinit var alarmManager: AlarmManager


        @Before
        open fun setUp() {
            context = Mockito.mock(Context::class.java)
            alarmManager = Mockito.mock(AlarmManager::class.java)
            intent = Mockito.mock(Intent::class.java)
            pendingIntent = Mockito.mock(PendingIntent::class.java)

            Mockito.`when`(context.getSystemService(Context.ALARM_SERVICE)).thenReturn(alarmManager)
        }

        fun setUpTask(hour: Int){
            calendar = Task.makeCalendar(hour, 0)
            val time = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(calendar.time)
            task = Mockito.spy(Task("Test", time))
            Mockito.doAnswer {
                task.id = 1.toLong()
                task.id
            }.`when`(task).save() // mock save method to assign and return just id
            task.save()

            Mockito.doNothing().`when`(alarmManager).set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            Mockito.`when`(intent.putExtra(AlarmBroadcastReceiver.NAME, task.name)).thenReturn(intent)
            Mockito.doReturn(intent).`when`(task).makeAlarmBroadcastIntent(context)
            Mockito.doReturn(pendingIntent).`when`(task).makePendingIntent(context, intent, 0)
            Mockito.doNothing().`when`(task).makeToastAndLog(context, Task.SCHEDULED, task.scheduledAlarmText())
            task.schedule(context, calendar)
        }
    }

    class TaskWithHourAhead : SharedSetUp() {
        @Before
        override fun setUp() {
            super.setUp()
            val anHourAhead = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1 // initialize an int that's one hour ahead of current hour
            setUpTask(anHourAhead)
        }

        @Test
        fun itCallsAlarmManagerSetOnce() {
            Mockito.verify(alarmManager, Mockito.times(1)).set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        @Test
        fun itCallsAlarmManagerCancelOnce() {
            Mockito.doNothing().`when`(task).makeToastAndLog(context, UNSCHEDULED, task.cancelAlarmText())
            Mockito.doReturn(pendingIntent).`when`(task).makePendingIntent(context, intent, PendingIntent.FLAG_NO_CREATE)
            task.unschedule(context)
            Mockito.verify(alarmManager, Mockito.times(1)).cancel(pendingIntent)
        }
    }

    class TaskWithHourBehind : SharedSetUp() {

        @Before
        override fun setUp(){
            super.setUp()
            val anHourBehind = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) - 1 // initialize an int that's one hour ahead of current hour
            setUpTask(anHourBehind)
        }

        @Test
        fun itCallsAlarmManagerSetZeroTimes(){
            Mockito.verify(alarmManager, Mockito.times(0)).set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }
}