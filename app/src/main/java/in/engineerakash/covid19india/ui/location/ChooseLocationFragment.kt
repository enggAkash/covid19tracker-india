package `in`.engineerakash.covid19india.ui.location

import `in`.engineerakash.covid19india.databinding.FragmentChooseLocationBinding
import `in`.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import `in`.engineerakash.covid19india.ui.home.MainViewModel
import `in`.engineerakash.covid19india.ui.home.MainViewModelFactory
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.Helper
import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.util.*

class ChooseLocationFragment : Fragment() {

    private lateinit var binding: FragmentChooseLocationBinding

    private var stateDistrictWiseResponse: ArrayList<StateDistrictWiseResponse> = arrayListOf()

    private var countryList = arrayListOf<String>("India")
    private var stateList = arrayListOf<String>()
    private var districtList = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChooseLocationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: MainViewModel by activityViewModels { MainViewModelFactory(activity!!.application) }

        viewModel.getStateDistrictListLiveData()
            .observe(viewLifecycleOwner, {
                stateDistrictWiseResponse = it

            })
        val countryAdapter: ArrayAdapter<String>
        val stateAdapter: ArrayAdapter<String>
        val districtAdapter: ArrayAdapter<String>

        if (context != null) {
            countryAdapter =
                ArrayAdapter<String>(context!!, R.layout.select_dialog_item, countryList)
            binding.countryAutoCompleteTv.setAdapter(countryAdapter)

            stateAdapter = ArrayAdapter<String>(context!!, R.layout.select_dialog_item, stateList)
            binding.stateAutoCompleteTv.setAdapter(stateAdapter)

            districtAdapter =
                ArrayAdapter<String>(context!!, R.layout.select_dialog_item, districtList)
            binding.districtAutoCompleteTv.setAdapter(districtAdapter)
        }

        binding.countryAutoCompleteTv.onItemClickListener =
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCountryName = (view as TextView?)?.text.toString()


                }
            }

    }

    private fun getStateList(countryName: String) {
        val stateJsonFile = Helper.readAssetFile(context, Constant.stateJsonAssetName)



    }

}