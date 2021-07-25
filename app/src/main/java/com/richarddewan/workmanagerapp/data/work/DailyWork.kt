package com.richarddewan.workmanagerapp.data.work

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.richarddewan.workmanagerapp.R
import java.util.*
import java.util.concurrent.TimeUnit


/*
created by Richard Dewan 25/07/2021
*/

class DailyWork(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private lateinit var notificationManager: NotificationManagerCompat

    override fun doWork(): Result {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        //set time to 8 am
        dueDate.set(Calendar.HOUR_OF_DAY,12)
        dueDate.set(Calendar.MINUTE,30)
        dueDate.set(Calendar.SECOND,0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY,24)
        }
        //calculate time difference
        val timeDiff = (dueDate.timeInMillis - currentDate.timeInMillis)

        val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyWork>()
            .setInitialDelay(timeDiff,TimeUnit.MILLISECONDS)
            .addTag(TAG)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)

        createNotification()

        return  Result.success()
    }

    private fun createNotification(){
        notificationManager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context,"ch01")
            .setSmallIcon(R.drawable.ic_notification_work)
            .setContentTitle("Work")
            .setContentText("This is a DailyWork notification")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()

        notificationManager.notify(1001,notification)
    }

    companion object {
        const val TAG = "DailyWork"
    }
}