package com.richarddewan.workmanagerapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.richarddewan.workmanagerapp.data.local.db.AppDatabase


/*
created by Richard Dewan 17/07/2021
*/

class WorkManagerApp: Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getInstance(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                "ch01",
                "Channel One",
                NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "This is a DailyWork notification channel"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}