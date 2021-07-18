package com.richarddewan.workmanagerapp.data.work

import android.content.Context
import android.util.Log
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.richarddewan.workmanagerapp.WorkManagerApp
import com.richarddewan.workmanagerapp.data.local.entity.UserEntity
import com.richarddewan.workmanagerapp.data.repository.UserRepository
import io.reactivex.Single


/*
created by Richard Dewan 18/07/2021
*/

class UserRxJavaWorker(appContext: Context, workerParams: WorkerParameters): RxWorker(appContext,
    workerParams,) {

    private val database = (appContext.applicationContext as WorkManagerApp).database
    private val repository = UserRepository(database)

    override fun createWork(): Single<Result> {

       return repository.insert(UserEntity(name = "David", password = "121212"))
           .flatMap { id->
               repository.update(UserEntity(id = id,name = "Rich",password = "123213"))
           }
           .map { result->
              val data  = workDataOf("KEY_RESULT" to result)
               Log.d(TAG, result.toString())
              Result.success(data)
           }
    }

    companion object {
        const val TAG = "UserRxJavaWorker"
    }
}