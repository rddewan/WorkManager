package com.richarddewan.workmanagerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.richarddewan.workmanagerapp.data.local.entity.EmployeeEntity


/*
created by Richard Dewan 17/07/2021
*/

@Dao
interface EmployeeDao {

    @Insert
    suspend fun insert(employeeEntity: EmployeeEntity): Long
}