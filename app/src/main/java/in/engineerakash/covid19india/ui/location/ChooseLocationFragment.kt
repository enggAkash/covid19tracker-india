package `in`.engineerakash.covid19india.ui.location

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.FragmentChooseLocationBinding
import `in`.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import `in`.engineerakash.covid19india.ui.home.MainActivity
import `in`.engineerakash.covid19india.ui.home.MainViewModel
import `in`.engineerakash.covid19india.ui.home.MainViewModelFactory
import `in`.engineerakash.covid19india.util.ChooseLocationStartedFrom
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.Helper
import `in`.engineerakash.covid19india.util.ViewUtil.hideKeyboard
import `in`.engineerakash.covid19india.util.ViewUtil.isViewIsChangingProgrammatically
import `in`.engineerakash.covid19india.util.ViewUtil.removeViewIsChangingProgrammatically
import `in`.engineerakash.covid19india.util.ViewUtil.setViewIsChangingProgrammatically
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.JsonParseException
import org.json.JSONArray

class ChooseLocationFragment : Fragment() {

    private lateinit var binding: FragmentChooseLocationBinding

    private var stateDistrictWiseResponse: ArrayList<StateDistrictWiseResponse> = arrayListOf()

    private var stateAdapter: LocationAdapter? = null
    private var districtAdapter: LocationAdapter? = null

    private var selectedState = ""
    private var selectedDistrict = ""

    val args by navArgs<ChooseLocationFragmentArgs>()

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

                        binding.stateAutoCompleteTv.setViewIsChangingProgrammatically()
                        selectedState = Constant.userSelectedState
                        binding.stateAutoCompleteTv.setText(Constant.userSelectedState, true)
                        binding.stateAutoCompleteTv.removeViewIsChangingProgrammatically()

                        Helper.setSelectedState(context, Constant.userSelectedState)

                        setDistrict()

                        binding.mapIv.setImageResource(Helper.getMapImageResource(selectedState))
                    }
                }
            })

        if (context != null) {
            stateAdapter = LocationAdapter(context!!, arrayListOf())
            binding.stateAutoCompleteTv.setAdapter(stateAdapter)

            districtAdapter = LocationAdapter(context!!, arrayListOf())
            binding.districtAutoCompleteTv.setAdapter(districtAdapter)
        }

        binding.stateAutoCompleteTv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedState = (view as TextView?)?.text.toString()
                setDistrict()

                binding.districtAutoCompleteTv.post {
                    binding.districtAutoCompleteTv.requestFocus()
                }

                binding.mapIv.setImageResource(Helper.getMapImageResource(selectedState))
            }

        binding.districtAutoCompleteTv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedDistrict = (view as TextView?)?.text.toString()
                activity?.hideKeyboard()
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

            if (args.chooseLocationStartedFrom == ChooseLocationStartedFrom.SETTING_FRAG) {
                activity?.onBackPressed()

            } else {
                val gotoTrackFragment =
                    ChooseLocationFragmentDirections.actionChooseLocationFragmentToTrackFragment()
                this.findNavController().navigate(gotoTrackFragment)

            }

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

        binding.districtAutoCompleteTv.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(
                textView: TextView?, actionId: Int, event: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN
                    || event?.keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    activity.hideKeyboard()
                    return true
                }
                return false
            }
        })

        binding.stateAutoCompleteTv.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && stateAdapter?.isEmpty == false && selectedState.isEmpty())
                binding.stateAutoCompleteTv.showDropDown()
            else
                binding.stateAutoCompleteTv.dismissDropDown()
        }

        binding.districtAutoCompleteTv.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && districtAdapter?.isEmpty == false && selectedDistrict.isEmpty())
                binding.districtAutoCompleteTv.showDropDown()
            else
                binding.districtAutoCompleteTv.dismissDropDown()
        }

    }

    private fun initComponent() {
        (activity as AppCompatActivity?)?.supportActionBar?.hide()

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

    override fun onStop() {
        super.onStop()

        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.app_name)
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = ""
        (activity as MainActivity?)?.changeThemeColor(true, 0)

    }
}