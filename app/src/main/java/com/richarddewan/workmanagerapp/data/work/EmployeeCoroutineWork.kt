package com.richarddewan.workmanagerapp.data.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.richarddewan.workmanagerapp.WorkManagerApp
import com.richarddewan.workmanagerapp.data.local.entity.EmployeeEntity
import com.richarddewan.workmanagerapp.data.repository.EmployeeRepository


/*
created by Richard Dewan 17/07/2021
*/

class EmployeeCoroutineWork(appContext: Context,
                            params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TAG = "EmployeeCoroutineWork"
    }

    private val database = (appContext.applicationContext as WorkManagerApp).database
    private val repository = EmployeeRepository(database)

    override suspend fun doWork(): Result {
        val result  = repository.insert(EmployeeEntity(name = "Richard", age = 20))

        val data = workDataOf("KEY_RESULT" to result)

        Log.d(TAG,result.toString())

        return Result.success(data)
    }
}