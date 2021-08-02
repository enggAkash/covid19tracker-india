package `in`.engineerakash.covid19india.ui.location

import `in`.engineerakash.covid19india.databinding.FragmentChooseLocationBinding
import `in`.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import `in`.engineerakash.covid19india.ui.home.MainViewModel
import `in`.engineerakash.covid19india.ui.home.MainViewModelFactory
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.Helper
import `in`.engineerakash.covid19india.util.ViewUtil.isViewIsChangingProgrammatically
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonParseException
import org.json.JSONArray

private const val TAG = "ChooseLocationFragment"

class ChooseLocationFragment : Fragment() {

    private lateinit var binding: FragmentChooseLocationBinding

    private var stateDistrictWiseResponse: ArrayList<StateDistrictWiseResponse> = arrayListOf()

    //private var stateList = arrayListOf<String>()
    //private var districtList = arrayListOf<String>()

    private var stateAdapter: LocationAdapter? = null
    private var districtAdapter: LocationAdapter? = null

    private var selectedState = ""
    private var selectedDistrict = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChooseLocationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponent()

        val viewModel: MainViewModel by activityViewModels { MainViewModelFactory(activity!!.application) }

        viewModel.getStateDistrictListLiveData()
            .observe(viewLifecycleOwner, {
                stateDistrictWiseResponse = it

                if (Constant.userSelectedState.isNotEmpty()) {
                    val stateIndexToSelect =
                        stateAdapter?.getPosition(Constant.userSelectedState) ?: -1
                    if (stateIndexToSelect >= 0) {
                        selectedState = Constant.userSelectedState
                        binding.stateAutoCompleteTv.setText(Constant.userSelectedState, true)

                        Helper.setSelectedState(context, Constant.userSelectedState)

                        setDistrict()
                    }
                }
            })

        if (context != null) {
            stateAdapter = LocationAdapter(context!!, arrayListOf())
            binding.stateAutoCompleteTv.setAdapter(stateAdapter)

            districtAdapter = LocationAdapter(context!!, arrayListOf())
            binding.districtAutoCompleteTv.setAdapter(districtAdapter)
        }

        binding.stateAutoCompleteTv.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedState = (view as TextView?)?.text.toString()
                setDistrict()
            }
        }

        binding.districtAutoCompleteTv.onItemClickListener =
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedDistrict = (view as TextView?)?.text.toString()
                }
            }

        if (Constant.userSelectedState.isEmpty() || Constant.userSelectedDistrict.isEmpty())
            binding.skipBtn.visibility = View.GONE
        else
            binding.skipBtn.visibility = View.VISIBLE

        setupClickListeners()

        stateAdapter?.setList(getStateList())
    }

    private fun setDistrict() {
        selectedDistrict = ""

        districtAdapter?.setList(getDistrictList(selectedState, stateDistrictWiseResponse))
        binding.districtAutoCompleteTv.setText("")
        //binding.districtAutoCompleteTv.clearComposingText()

        if (Constant.userSelectedDistrict.isNotEmpty()) {
            val districtIndexToSelect =
                districtAdapter?.getPosition(Constant.userSelectedDistrict) ?: -1
            if (districtIndexToSelect >= 0) {
                binding.districtAutoCompleteTv.setText(Constant.userSelectedDistrict, false)
                selectedDistrict = Constant.userSelectedDistrict

                Helper.setSelectedDistrict(context, Constant.userSelectedDistrict)
            }
        }
    }

    private fun setupClickListeners() {
        binding.saveBtn.setOnClickListener {

            if (selectedState.isEmpty()) {
                binding.stateAutoCompleteTv.error = "Please Select State"
                return@setOnClickListener
            }

            if (selectedDistrict.isEmpty()) {
                binding.districtAutoCompleteTv.error = "Please Select District"
                return@setOnClickListener
            }

            Helper.setSelectedState(context, selectedState)
            Helper.setSelectedDistrict(context, selectedDistrict)

            val gotoTrackFragment =
                ChooseLocationFragmentDirections.actionChooseLocationFragmentToTrackFragment(true)
            this.findNavController().navigate(gotoTrackFragment)
        }

        binding.skipBtn.setOnClickListener {
            val gotoTrackFragment =
                ChooseLocationFragmentDirections.actionChooseLocationFragmentToTrackFragment()
            this.findNavController().navigate(gotoTrackFragment)
        }

        binding.stateAutoCompleteTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                str: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {

                if (!binding.stateAutoCompleteTv.isViewIsChangingProgrammatically()) {
                    selectedState = ""
                    selectedDistrict = ""
                }

            }
        })
    }

    private fun initComponent() {
        if (Constant.userSelectedState.isEmpty() || Constant.userSelectedDistrict.isEmpty())
            binding.skipBtn.visibility = View.GONE
        else
            binding.skipBtn.visibility = View.VISIBLE
    }

    private fun getStateList(): ArrayList<String> {
        val stateList = arrayListOf<String>()

        val stateJsonFile = Helper.readAssetFile(context, Constant.stateJsonAssetName)

        try {
            val rootJa = JSONArray(stateJsonFile)

            for (i in 0 until rootJa.length()) {
                val stateJo = rootJa.getJSONObject(i)
                stateList.add(stateJo.getString("name"))
            }
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        return stateList
    }

    private fun getDistrictList(
        stateName: String, stateDistrictWiseResponse: ArrayList<StateDistrictWiseResponse>
    ): ArrayList<String> {
        val districtInThisStateList = arrayListOf<String>()

        for (stateDistrict in stateDistrictWiseResponse) {
            if (stateDistrict.name.contains(stateName, true)) {

                for (district in stateDistrict.districtArrayList) {
                    if (district.name.contains("Unknown", true))
                        continue

                    districtInThisStateList.add(district.name)
                }

                break
            }
        }

        return districtInThisStateList
    }
}