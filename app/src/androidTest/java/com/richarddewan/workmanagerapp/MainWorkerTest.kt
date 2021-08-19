package com.richarddewan.workmanagerapp

import android.content.Context
import android.util.Log
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.android.material.datepicker.RangeDateSelector
import com.richarddewan.workmanagerapp.data.work.EmployeeCoroutineWork
import com.richarddewan.workmanagerapp.data.work.RandomNumberWork
import com.richarddewan.workmanagerapp.data.work.UserRxJavaWorker
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


/*
created by Richard Dewan 18/08/2021
*/

class MainWorkerTest {

    private lateinit var context: Context
    private lateinit var configuration: Configuration

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        configuration = Configuration.Builder()
            //set the log level to DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            //user a SynchronousExecutor to make it easy to write our test
            .setExecutor(SynchronousExecutor())
            .build()

        //initialize workmanager for instrumentation test
        WorkManagerTestInitHelper.initializeTestWorkManager(context, configuration)

    }

    @Test
    fun testRandomNumberWork() {
        //set input data
        val inputData = workDataOf("KEY_START" to 0, "KEY_COUNT" to 0)
        //result output data
        val resultOutputData = workDataOf("KEY_RESULT" to 10)

        //request
        val request = OneTimeWorkRequestBuilder<RandomNumberWork>()
            .setInputData(inputData)
            .build()

        //workmanager
        val workManager = WorkManager.getInstance(context)
        //enqueue the work and wait for the result
        workManager.enqueue(request).result.get()
        //work info
        val info = workManager.getWorkInfoById(request.id).get()
        val outputData = info.outputData
        //assert
        assertThat(info.state, `is`(WorkInfo.State.SUCCEEDED))
        assertThat(outputData, `is`(resultOutputData))
    }

    @Test
    fun testWithInitialDelay() {
        //set input data
        val inputData = workDataOf("KEY_START" to 0, "KEY_COUNT" to 0)
        //result output data
        val resultOutputData = workDataOf("KEY_RESULT" to 10)

        //request
        val request = OneTimeWorkRequestBuilder<RandomNumberWork>()
            .setInputData(inputData)
            .setInitialDelay(30, TimeUnit.SECONDS)
            .build()
        //work manager
        val workManager = WorkManager.getInstance(context)
        //test driver
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        //enqueue our work
        workManager.enqueue(request).result.get()
        //tell the work manager that initial delay are set
        testDriver?.setInitialDelayMet(request.id)
        //work info
        val workInfo = workManager.getWorkInfoById(request.id).get()
        //Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
        assertThat(workInfo.outputData, `is`(resultOutputData))

    }

    @Test
    fun testWithConstraints() {
        //set input data
        val inputData = workDataOf("KEY_START" to 0, "KEY_COUNT" to 0)
        //result output data
        val resultOutputData = workDataOf("KEY_RESULT" to 10)
        //constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        //work request
        val request = OneTimeWorkRequestBuilder<RandomNumberWork>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        //work manager
        val workManager = WorkManager.getInstance(context)
        //test driver
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        //enqueue work
        workManager.enqueue(request).result.get()
        //Tells TestDriver to pretend that all constraints on the androidx.work.WorkRequest
        // with the given workSpecId are met. This may trigger execution of the work.
        testDriver?.setAllConstraintsMet(request.id)
        //get workinfo
        val workInfo = workManager.getWorkInfoById(request.id).get()
        //Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
        assertThat(workInfo.outputData, `is`(resultOutputData))
    }

    @Test
    fun testWithPeriodicWork() {
        //set input data
        val inputData = workDataOf("KEY_START" to 0, "KEY_COUNT" to 0)
        //result output data
        val resultOutputData = workDataOf("KEY_RESULT" to 10)
        //constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        //work request
        val request = PeriodicWorkRequestBuilder<RandomNumberWork>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        //work manager
        val workManager = WorkManager.getInstance(context)
        //test driver
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        //enqueue work
        workManager.enqueue(request).result.get()
        //Tells TestDriver to pretend that the period delay on the androidx.work.PeriodicWorkRequest
        // with the given workSpecId is met. This may trigger execution of the work.
        testDriver?.setPeriodDelayMet(request.id)
        //work info
        val workInfo = workManager.getWorkInfoById(request.id).get()
        //Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))

    }


    @Test
    fun testWithCoroutinesWorker(){
        //set input data
        val inputData = workDataOf("KEY_START" to 0, "KEY_COUNT" to 0)

        //worker
        val worker  = TestListenableWorkerBuilder<EmployeeCoroutineWork>(context, inputData)
            .build()

        runBlocking {
            val doWork = worker.startWork().get()
            //result output data
            val resultOutputData = workDataOf("KEY_RESULT" to doWork.outputData.getLong("KEY_RESULT",0))

            //Assert
            assertEquals(resultOutputData,doWork.outputData)
            assertThat(ListenableWorker.Result.success(resultOutputData), `is`(doWork))

        }
    }

    @Test
    fun testWithRxJavaWorker(){
        //create a worker
        val worker = TestListenableWorkerBuilder<UserRxJavaWorker>(context)
            .build()
        //subscribe it
        worker.createWork().subscribe { result ->
            println(result.outputData.getInt("KEY_RESULT",0))
            //get the result output data
            val data  = workDataOf("KEY_RESULT" to result.outputData.getInt("KEY_RESULT",0))
            //Assert
            assertThat(result, `is`(ListenableWorker.Result.success(data)))
        }


    }
}