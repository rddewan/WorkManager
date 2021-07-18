package com.richarddewan.workmanagerapp.data.repository

import com.richarddewan.workmanagerapp.data.local.db.AppDatabase
import com.richarddewan.workmanagerapp.data.local.entity.EmployeeEntity


/*
created by Richard Dewan 17/07/2021
*/

class EmployeeRepository(private val database: AppDatabase) {

    suspend fun insert(employeeEntity: EmployeeEntity) : Long =
        database.employeeDao().insert(employeeEntity)
}