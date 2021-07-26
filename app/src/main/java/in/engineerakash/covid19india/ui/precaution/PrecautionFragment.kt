package `in`.engineerakash.covid19india.ui.precaution

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.FragmentPrecautionBinding
import `in`.engineerakash.covid19india.pojo.Precaution
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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
    }

    private fun getPrecautionList(): ArrayList<Precaution> {
        val list = arrayListOf<Precaution>()

        val precautionList = arrayListOf<Precaution>(
            Precaution("PREVENTION"),
            Precaution(
                "", "", R.drawable.hand_washing,
                "Wash your hands often with Soap and Water",
                "Scrub for at least 20 seconds. If Soap and Water are not available, use an Alcohol-Based Sanitizer.",
                "Buy Soap", "", "Buy Sanitizer", ""
            ),
            Precaution(
                "", "", R.drawable.avoid_close_contact,
                "Avoid Close Contact with people who are sick",
                "A person with Coronavirus can spread it to others who are up to about 6 feet away."
            ),
            Precaution(
                "", "", R.drawable.wiping,
                "Clean and Disinfect commonly used surfaces",
                "Wipe Down door knobs, tables, light switches, keyboards, and remote controls.",
                "Buy Surface Cleaner", "", "Buy Keyboard Cleaner", ""
            ),
            Precaution(
                "", "", R.drawable.nose_touching,
                "Don’t touch your eyes, nose or mouth",
                "A virus can enter your body this way."
            ),
            Precaution(
                "", "", R.drawable.face_mask,
                "Cover your nose and mouth with a mask when around others",
                "This helps reduce the risk of spread both by close contact and by airborne transmission.",
                "Buy Safest Mask"
            ),
            Precaution(
                "", "", R.drawable.quarantine,
                "Stay home if you are sick",
                "If you have coronavirus symptoms, stay home except to get medical care.",
                "Call Ambulance", "tel:102"
            )
        )

        val cureList = arrayListOf<Precaution>(
            Precaution("CURE"),
            Precaution("", "At Home:"),
            Precaution(
                "", "", R.drawable.doctor,
                "Be in Touch with treating physician"
            ),
            Precaution(
                "", "", R.drawable.medications,
                "Continue the medications for other comorbid illness after consulting the physician"
            ),
            Precaution(
                "", "", R.drawable.hot_water,
                "Perform warm water gargles or take steam inhalation twice a day"
            ),
            Precaution(
                "", "Rush for Medical Attention:"
            ),
            Precaution(
                "", "", R.drawable.breath,
                "If you find Difficulty in breathing"
            ),
            Precaution(
                "", "", R.drawable.oxygen_saturation,
                "Dip in oxygen saturation (SpO2 < 94% on room air)"
            ),
            Precaution(
                "", "", R.drawable.chest_pain_or_pressure,
                "Persistent pain/pressure in the chest"
            ),
            Precaution(
                "", "", R.drawable.confused,
                "Mental confusion or inability to arouse"
            )
        )

        list.addAll(precautionList)
        list.addAll(cureList)

        return list
    }

}