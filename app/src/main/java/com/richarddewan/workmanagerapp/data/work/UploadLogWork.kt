package com.richarddewan.workmanagerapp.data.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf


/*
created by Richard Dewan 11/07/2021
*/

class UploadLogWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams,) {

    override fun doWork(): Result {
        //for loop
        for (i in 50..60){
            Log.d(TAG, "UploadLogWork: $i" )

            Thread.sleep(1000)
        }

        /*
        pass data to result
        workDataOf is a ktx extension methods
         */
        val data = workDataOf("KEY_RESULT" to 10)

        return Result.success(data)
    }

    companion object {
        const val TAG = "UploadLogWork"
    }
}