package com.richarddewan.workmanagerapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.richarddewan.workmanagerapp.R
import com.richarddewan.workmanagerapp.data.work.*
import com.richarddewan.workmanagerapp.databinding.FragmentDashboardBinding
import com.richarddewan.workmanagerapp.ui.home.HomeFragment
import java.util.concurrent.TimeUnit

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var workManager: WorkManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
       WorkManager  is a service that is responsiable for scheduling all the work that we request
        */
        workManager = WorkManager.getInstance(requireContext())

        binding.btnStart.setOnClickListener {
            oneTimeWorkRequest()
        }

        //setup the periodic work request
        periodicWorkRequest()

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
            .addTag(HomeFragment.WORK_TAG)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        val dataCleanUpWorkRequest = OneTimeWorkRequestBuilder<DataCleanUpWork>()
            .addTag(HomeFragment.WORK_TAG)
            .setInitialDelay(0, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()


        //chaining a work request
        workManager.beginUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, randomNumberWorkRequest)
            .then(dataCleanUpWorkRequest)
            .enqueue()

        //observe the live data for workInfo


    }

    private fun periodicWorkRequest(){
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
        periodic work request
         */
        val randomNumberWork = PeriodicWorkRequest.Builder(
            RandomNumberPeriodicWork::class.java,15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueue(randomNumberWork)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val WORK_NAME = "100288787"
    }
}