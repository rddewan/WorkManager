package com.richarddewan.workmanagerapp.data.work

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf


/*
created by Richard Dewan 11/07/2021
*/

/*
worker class is where we define our work to perform in background
 */
class RandomNumberPeriodicWork(context: Context, workerParams: WorkerParameters): Worker(context,
    workerParams,) {

    //doWork method runs asynchronously in the background thread - this wont block our UI
    override fun doWork(): Result {

        return try {

            //get the input data
            val start = inputData.getInt("KEY_START",0)
            val count = inputData.getInt("KEY_COUNT",0)

            //for loop
            for (i in start..count){
                Log.d(TAG, "RandomNumberPeriodicWork: $i" )
                /*if (i == 5) {
                    throw Exception("Error on the for loop")
                }*/

                Thread.sleep(1000)
            }

            /*
            pass data to result
            workDataOf is a ktx extension methods
             */
            val data = workDataOf("KEY_RESULT" to 10)

            Result.success(data)
        }
        catch (e: Exception) {
            /*
            if there is an error return FAILURE
             */
            //Result.failure()
            /*
            you may want to retry it later then you can return retry
            */
            Result.retry()
        }


    }

    companion object {
        const val TAG = "PeriodicWork"
    }
}