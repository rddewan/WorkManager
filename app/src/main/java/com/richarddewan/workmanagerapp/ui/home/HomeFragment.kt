package com.richarddewan.workmanagerapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.richarddewan.workmanagerapp.data.work.CollectLogWork
import com.richarddewan.workmanagerapp.data.work.DataCleanUpWork
import com.richarddewan.workmanagerapp.data.work.RandomNumberWork
import com.richarddewan.workmanagerapp.data.work.UploadLogWork
import com.richarddewan.workmanagerapp.databinding.FragmentHomeBinding
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var workManager: WorkManager
    private lateinit var randomNumberWorkRequestUUID: UUID

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        homeViewModel.resultData.observe(viewLifecycleOwner, {
            textView.text = it.toString()
        })

        homeViewModel.msg.observe(viewLifecycleOwner, {
            textView.text = it.toString()
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        WorkManager  is a service that is responsiable for scheduling all the work that we request
         */
        workManager = WorkManager.getInstance(requireContext())

        //button click listener
        btnClickListener()

    }

    private fun oneTimeWorkRequest() {

        /*
        constraints
         */
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        /*
        input data
        The maximum number of bytes for Data when it is serialized is 10 * 1024 = 10kb
         */
        val inputData = Data.Builder()
            .putInt("KEY_START", 0)
            .putInt("KEY_COUNT", 10)
            .build()

        /*
         work request class allows us to define how and when we want our work to get executed
          */
        val randomNumberWorkRequest = OneTimeWorkRequest.Builder(RandomNumberWork::class.java)
            .addTag(WORK_TAG)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        val dataCleanUpWorkRequest = OneTimeWorkRequestBuilder<DataCleanUpWork>()
            .addTag(WORK_TAG)
            .setInitialDelay(0, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        /*
        parallel work
         */
        val collectLogWorkRequest = OneTimeWorkRequestBuilder<CollectLogWork>()
            .addTag(WORK_TAG)
            .build()

        val uploadLogWorkRequest = OneTimeWorkRequestBuilder<UploadLogWork>()
            .addTag(WORK_TAG)
            .build()

        val parallelWorkRequest = mutableListOf<OneTimeWorkRequest>().apply {
            this.add(collectLogWorkRequest)
            this.add(uploadLogWorkRequest)
        }

        randomNumberWorkRequestUUID = randomNumberWorkRequest.id


        //enqueue a sing work request
        /*workManager.enqueue(randomNumberWorkRequest)*/

        //chaining a work request
        workManager.beginWith(randomNumberWorkRequest)
            .then(dataCleanUpWorkRequest)
            .then(parallelWorkRequest)
            .enqueue()

        //observe the live data for workInfo
        workManager.getWorkInfoByIdLiveData(randomNumberWorkRequest.id)
            .observe(viewLifecycleOwner, {

                when {
                    it.state == WorkInfo.State.RUNNING -> {
                        homeViewModel.msg.value = "RandomNumberWork: RUNNING"
                    }
                    it.state == WorkInfo.State.CANCELLED -> {
                        homeViewModel.msg.value = "RandomNumberWork: CANCELLED"
                    }
                    it.state == WorkInfo.State.FAILED -> {
                        homeViewModel.msg.value = "RandomNumberWork: FAILED  :Error on the for loop"
                        Toast.makeText(requireContext(), "FAILED", Toast.LENGTH_LONG).show()
                    }
                    it.state.isFinished -> {
                        val resultOut = it.outputData.getInt("KEY_RESULT", 0)
                        resultOut.let {
                            homeViewModel.resultData.value = it
                        }
                    }
                }
            })

    }

    private fun btnClickListener() {
        binding.btnStart.setOnClickListener {
            //start one time work request
            oneTimeWorkRequest()
        }

        binding.btnCancel.setOnClickListener {
            workManager.cancelWorkById(randomNumberWorkRequestUUID)

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val WORK_TAG = "com.richarddewan.workmanagerapp.work1"

    }


}