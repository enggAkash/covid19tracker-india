package online.engineerakash.covid19india.ui.settings

import online.engineerakash.covid19india.R
import online.engineerakash.covid19india.databinding.FragmentSettingsBinding
import online.engineerakash.covid19india.ui.bg_process.CovidWorkManagerUtil
import online.engineerakash.covid19india.ui.language.LanguageActivity
import online.engineerakash.covid19india.util.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import online.engineerakash.covid19india.util.ViewUtil.isViewIsChangingProgrammatically
import online.engineerakash.covid19india.util.ViewUtil.removeViewIsChangingProgrammatically
import online.engineerakash.covid19india.util.ViewUtil.setViewIsChangingProgrammatically
import java.util.*

private const val TAG = "SettingsFragment"

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
        Log.d(TAG, "initComponent: ")

        (activity as AppCompatActivity?)?.supportActionBar?.hide()

        val reportFrequency = Helper.getReportFrequency(context)

        if (reportFrequency == CovidReportFrequency.DAILY) {
            binding.dailyNotificationRg.setViewIsChangingProgrammatically()
            binding.dailyNotificationRg.check(R.id.dailyNotificationRb)
            binding.dailyNotificationRg.removeViewIsChangingProgrammatically()
        } else if (reportFrequency == CovidReportFrequency.NEVER) {
            binding.dailyNotificationRg.setViewIsChangingProgrammatically()
            binding.dailyNotificationRg.check(R.id.neverNotificationRb)
            binding.dailyNotificationRg.removeViewIsChangingProgrammatically()
        }

        binding.dailyNotificationRg.setOnCheckedChangeListener { radioGroup, checkedId ->
            Log.d(TAG, "initComponent: ")
            if (radioGroup.isViewIsChangingProgrammatically())
                return@setOnCheckedChangeListener

            if (checkedId == R.id.dailyNotificationRb) {
                Log.d(TAG, "initComponent: checkedId Daily")

                Helper.saveReportFrequency(context, CovidReportFrequency.DAILY)
                CovidWorkManagerUtil.createPeriodicTask(context)

            } else if (checkedId == R.id.neverNotificationRb) {
                Log.d(TAG, "initComponent: checkedId Never")

                Helper.saveReportFrequency(context, CovidReportFrequency.NEVER)

                val lastPeriodicWorkTag = Helper.getPeriodicWorkTag(context)
                CovidWorkManagerUtil.cancelPeriodicTask(context, lastPeriodicWorkTag)
            }
        }

        binding.aboutInCovid19TitleTv.setOnClickListener {
            startActivity(Intent(context, AboutInCovid19OrgActivity::class.java))
        }

        binding.changeLocationTitleTv.setOnClickListener {
            findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToChooseLocationFragment(
                    ChooseLocationStartedFrom.SETTING_FRAG
                )
            )
        }

        binding.changeLanguageTitleTv.setOnClickListener {
            startActivity(
                Intent(context, LanguageActivity::class.java)
                    .apply {
                        putExtra(
                            "language_screen_started_from",
                            LanguageSelectionStartedFrom.SETTING_FRAG
                        )
                    }
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

        if (Constant.SHOW_ADS)
            loadAds()
    }

    private fun loadAds() {
        context.let {
            MobileAds.initialize(it!!) {}

            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }

}