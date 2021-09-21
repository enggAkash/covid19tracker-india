package `in`.engineerakash.covid19india.ui.precaution

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.FragmentPrecautionBinding
import `in`.engineerakash.covid19india.pojo.Precaution
import `in`.engineerakash.covid19india.util.Constant
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class PrecautionFragment : Fragment() {
    private lateinit var binding: FragmentPrecautionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrecautionBinding.inflate(inflater, container, false)

        initComponent()

        return binding.root
    }

    private fun initComponent() {
        (activity as AppCompatActivity?)?.supportActionBar?.hide()

        binding.precautionRv.adapter = PrecautionAdapter(getPrecautionList())

        if (Constant.SHOW_ADS)
            loadAds()
    }

    private fun getPrecautionList(): ArrayList<Precaution> {
        val list = arrayListOf<Precaution>()

        //todo add affiliate marketing links

        val precautionList = arrayListOf<Precaution>(
            Precaution("PREVENTION"),
            Precaution(
                "",
                "",
                R.drawable.hand_washing,
                getString(R.string.wash_your_hands_often),
                getString(R.string.scrub_for_20_sec),
                getString(R.string.buy_soap),
                "",
                getString(R.string.buy_sanitizer),
                ""
            ),
            Precaution(
                "",
                "",
                R.drawable.avoid_close_contact,
                getString(R.string.avoid_close_contact),
                getString(R.string.person_with_corona_can_spread)
            ),
            Precaution(
                "",
                "",
                R.drawable.wiping,
                getString(R.string.clean_surface),
                getString(R.string.wipe_surface_door),
                getString(R.string.buy_surface_cleaner),
                "",
                getString(R.string.buy_keyboard_cleaner),
                ""
            ),
            Precaution(
                "",
                "",
                R.drawable.nose_touching,
                getString(R.string.dont_touch_eyes),
                getString(R.string.virus_can_enter_your_body)
            ),
            Precaution(
                "",
                "",
                R.drawable.face_mask,
                getString(R.string.cover_nose_mouth_with_mask),
                getString(R.string.reduce_airborn_transmission),
                getString(R.string.buy_safest_mask)
            ),
            Precaution(
                "",
                "",
                R.drawable.quarantine,
                getString(R.string.stay_home_if_sick),
                getString(R.string.stay_home_if_have_symptoms),
                getString(R.string.call_ambulance),
                getString(R.string.ambulance_number)
            )
        )

        val cureList = arrayListOf<Precaution>(
            Precaution(getString(R.string.cure)),
            Precaution("", getString(R.string.at_home)),
            Precaution(
                "", "", R.drawable.doctor,
                getString(R.string.be_in_touch_with_physician)
            ),
            Precaution(
                "", "", R.drawable.medications,
                getString(R.string.continue_other_medications)
            ),
            Precaution(
                "", "", R.drawable.hot_water,
                getString(R.string.perform_warm_gargles)
            ),
            Precaution(
                "", getString(R.string.rush_for_medical_attention)
            ),
            Precaution(
                "", "", R.drawable.breath,
                getString(R.string.difficulty_in_breathing)
            ),
            Precaution(
                "", "", R.drawable.oxygen_saturation,
                getString(R.string.dip_in_oxygen)
            ),
            Precaution(
                "", "", R.drawable.chest_pain_or_pressure,
                getString(R.string.pain_in_chest)
            ),
            Precaution(
                "", "", R.drawable.confused,
                getString(R.string.mental_confusion)
            )
        )

        list.addAll(precautionList)
        list.addAll(cureList)

        return list
    }

    private fun loadAds() {
        context.let {
            MobileAds.initialize(it!!) {}

            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }

}