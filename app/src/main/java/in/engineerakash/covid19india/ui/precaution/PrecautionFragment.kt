package `in`.engineerakash.covid19india.ui.precaution

import `in`.engineerakash.covid19india.databinding.FragmentPrecautionBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class PrecautionFragment : Fragment() {
    private var binding: FragmentPrecautionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrecautionBinding.inflate(inflater, container, false)

        initComponent()

        return binding!!.root
    }

    private fun initComponent() {
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
    }

}