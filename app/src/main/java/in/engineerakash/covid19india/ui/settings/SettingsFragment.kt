package `in`.engineerakash.covid19india.ui.settings

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.FragmentSettingsBinding
import `in`.engineerakash.covid19india.ui.bg_process.LatestReportWorker
import `in`.engineerakash.covid19india.util.ChooseLocationStartedFrom
import `in`.engineerakash.covid19india.util.Helper
import `in`.engineerakash.covid19india.util.IntentUtil
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        initComponent()

        return binding.root
    }

    private fun initComponent() {
        (activity as AppCompatActivity?)?.supportActionBar?.hide()

        binding.dailyNotificationRg.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.dailyNotificationRb) {

            } else if (checkedId == R.id.neverNotificationRb) {

            }
        }

        binding.aboutCovid19IndiaTitleTv.setOnClickListener {

        }

        binding.changeLocationTitleTv.setOnClickListener {
            findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToChooseLocationFragment(
                    ChooseLocationStartedFrom.SETTING_FRAG
                )
            )
        }

        binding.shareAppTitleTv.setOnClickListener {
            IntentUtil.shareApp(context)
        }

        binding.contactTitleTv.setOnClickListener {
            IntentUtil.contactAppDeveloper(context)
        }

        binding.aboutAppTitleTv.setOnClickListener {
            startActivity(Intent(context, AboutAppActivity::class.java))
        }
    }

    fun createPeriodicTask() {
        val fetchReportConstraints =
            Constraints
                .Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<LatestReportWorker>(24, TimeUnit.HOURS)
            .setConstraints(fetchReportConstraints)
            .build()

        val id = periodicWorkRequest.id.toString()
        context?.let { Helper.savePeriodicId(it, id) }

        context?.let { WorkManager.getInstance(it).enqueue(periodicWorkRequest) }

    }
}