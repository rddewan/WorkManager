package com.richarddewan.workmanagerapp.data.work

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.richarddewan.workmanagerapp.R
import java.util.*


/*
created by Richard Dewan 18/08/2021
*/

class ForegroundWork(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {

    override fun doWork(): Result {
        return try {
            setForegroundAsync(createNotification(applicationContext,id))

            //for loop
            for (i in 0..120){
                Log.d(TAG, "ForegroundWork: $i" )
                Thread.sleep(1000)
            }

            /*
            pass data to result
            workDataOf is a ktx extension methods
             */
            val data = workDataOf("KEY_RESULT" to 120)

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
        const val TAG = "ForegroundWork"

        private fun createNotification(context: Context, id: UUID): ForegroundInfo {
            val intent = WorkManager.getInstance(context).createCancelPendingIntent(id)

            val notification = NotificationCompat.Builder(context, "ch01")
                .setContentTitle("Long Running Task")
                .setContentText("We are downloading a file please wait..")
                .setSmallIcon(R.drawable.ic_notification_long)
                .setOngoing(true)
                .addAction(R.drawable.ic_notification_cancel,"Cancel",intent)
                .setProgress(100,0,false)
                .build()

            return ForegroundInfo(10001,notification)
        }
    }
}