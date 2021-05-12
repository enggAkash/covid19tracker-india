package `in`.engineerakash.covid19india.ui.location

import `in`.engineerakash.covid19india.databinding.FragmentChooseLocationBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ChooseLocationFragment : Fragment() {
    private lateinit var binding: FragmentChooseLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseLocationBinding.inflate(inflater, container, false)

        return binding.root
    }
}