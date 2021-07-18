package com.richarddewan.workmanagerapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.richarddewan.workmanagerapp.data.local.dao.EmployeeDao
import com.richarddewan.workmanagerapp.data.local.dao.UserDao
import com.richarddewan.workmanagerapp.data.local.entity.EmployeeEntity
import com.richarddewan.workmanagerapp.data.local.entity.UserEntity


/*
created by Richard Dewan 17/07/2021
*/

@Database(entities = [EmployeeEntity::class, UserEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "work_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                   INSTANCE = instance
                }
                return  instance
            }

        }
    }
}