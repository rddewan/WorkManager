package com.richarddewan.workmanagerapp

import android.app.Application
import com.richarddewan.workmanagerapp.data.local.db.AppDatabase


/*
created by Richard Dewan 17/07/2021
*/

class WorkManagerApp: Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getInstance(this)
    }
}