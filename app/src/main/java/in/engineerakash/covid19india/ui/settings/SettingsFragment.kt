package `in`.engineerakash.covid19india.ui.settings

import `in`.engineerakash.covid19india.databinding.FragmentSettingsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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
    }

}