package com.example.todolist.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todolist.BuildConfig
import com.example.todolist.data.DataModel

class AndroidAlarmScheduler(private val context: Context) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val reminderOffsetMillis = BuildConfig.TASK_REMINDER_OFFSET_MILLIS

    fun schedule(task: DataModel) {
        if (task.dueDateMillis <= System.currentTimeMillis()) return

        val pendingIntent = createPendingIntent(task)
        val triggerTime = maxOf(
            System.currentTimeMillis() + 5_000L,
            task.dueDateMillis - reminderOffsetMillis
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }

            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()

            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancel(task: DataModel) {
        alarmManager.cancel(createPendingIntent(task))
    }

    private fun createPendingIntent(task: DataModel): PendingIntent {
        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            action = "com.example.todolist.ACTION_TASK_REMINDER_${task.id}"
            putExtra("EXTRA_ID", task.id)
            putExtra("EXTRA_TITLE", task.title)
            putExtra("EXTRA_REMINDER_OFFSET_MILLIS", reminderOffsetMillis)
        }

        return PendingIntent.getBroadcast(
            context,
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
