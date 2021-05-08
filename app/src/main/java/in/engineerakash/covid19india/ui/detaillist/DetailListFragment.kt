package `in`.engineerakash.covid19india.ui.detaillist

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.FragmentDetailListBinding
import `in`.engineerakash.covid19india.enums.ListType
import `in`.engineerakash.covid19india.pojo.*
import `in`.engineerakash.covid19india.ui.home.DistrictWiseAdapter
import `in`.engineerakash.covid19india.ui.home.StateWiseAdapter
import `in`.engineerakash.covid19india.util.Constant
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.*

class DetailListFragment : Fragment() {
    private var currentListType: ListType? = null
    private lateinit var timeSeriesStateWiseResponse: TimeSeriesStateWiseResponse
    private var stateDistrictList: ArrayList<StateDistrictWiseResponse> = arrayListOf()
    private lateinit var binding: FragmentDetailListBinding
    //private var root: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val detailListFragmentArgs : DetailListFragmentArgs by navArgs()

        currentListType = detailListFragmentArgs.listType
        timeSeriesStateWiseResponse = detailListFragmentArgs.timeSeriesDateWiseResponse
        stateDistrictList =
            ArrayList<StateDistrictWiseResponse>(listOf(*detailListFragmentArgs.stateDistrictWiseResponse))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailListBinding.inflate(inflater, container, false)
        //root = binding.root

        initComponent()

        return binding.root
    }

    private fun initComponent() {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        if (currentListType === ListType.DISTRICT) {
            binding.districtHeader.root.visibility = View.VISIBLE
            /*root!!.findViewById<View>(R.id.districtHeader).visibility =
                View.VISIBLE*/

            binding.stateHeader.root.visibility = View.GONE
            /*root!!.findViewById<View>(R.id.stateHeader).visibility = View.GONE*/

            val sortedAffectedDistricts: ArrayList<District> =
                getSortedAffectedDistricts(stateDistrictList)
            sortedAffectedDistricts.addAll(getObjectTotalOfAffectedDistricts(stateDistrictList))

            val districtWiseAdapter = DistrictWiseAdapter(sortedAffectedDistricts)

            binding.mostAffectedDistrictAndStateRv.layoutManager = LinearLayoutManager(context)
            binding.mostAffectedDistrictAndStateRv.adapter = districtWiseAdapter

        } else if (currentListType === ListType.STATE) {

            binding.districtHeader.root.visibility = View.GONE
            //root!!.findViewById<View>(R.id.districtHeader).visibility = View.GONE

            binding.stateHeader.root.visibility = View.VISIBLE
            /*root!!.findViewById<View>(R.id.stateHeader).visibility = View.VISIBLE*/

            val sortedAffectedStateWiseList: ArrayList<StateWiseData> =
                getSortedAffectedStates(timeSeriesStateWiseResponse.stateWiseDataArrayList)
            sortedAffectedStateWiseList
                .add(getObjectTotalOfAffectedState(timeSeriesStateWiseResponse.stateWiseDataArrayList))

            val stateWiseAdapter = StateWiseAdapter(sortedAffectedStateWiseList)

            binding.mostAffectedDistrictAndStateRv.layoutManager = LinearLayoutManager(context)
            binding.mostAffectedDistrictAndStateRv.adapter = stateWiseAdapter
        }

        if (currentListType === ListType.DISTRICT) {
            (activity as AppCompatActivity?)?.supportActionBar?.setTitle(Constant.userSelectedState.trim { it <= ' ' } + "'s")
            (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("Most affected Districts")
        } else {
            (activity as AppCompatActivity?)?.supportActionBar?.setTitle(Constant.userSelectedCountry.trim { it <= ' ' } + "'s")
            (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("Most affected States")
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)?.supportActionBar?.setTitle(getString(R.string.app_name))
        (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("")
    }

    private fun getSortedAffectedDistricts(stateWiseDataArrayList: ArrayList<StateDistrictWiseResponse>?): ArrayList<District> {

        //Get user selected state
        var userSelectedState: StateDistrictWiseResponse? = null
        for (stateDistrictWiseResponse in stateWiseDataArrayList!!) {
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(stateDistrictWiseResponse.name.trim { it <= ' ' }, ignoreCase = true)) {
                userSelectedState = stateDistrictWiseResponse
                break
            }
        }
        var districtList: ArrayList<District> = ArrayList<District>()
        if (userSelectedState != null) districtList = userSelectedState.districtArrayList


        // Sort District according to confirmed cases
        districtList.sortWith { o1, o2 -> // descending
            o2.confirmed - o1.confirmed
        }
        val mostAffectedDistricts: ArrayList<District> = ArrayList<District>(districtList)

        // Check if user has selected their state and district then only perform this action
        if (Constant.isUserSelected) {
            var userDistrictIsInMostAffectedState = false
            var userDistrictIsInMostAffectedStateIndex = -1

            // check if user selected state is in list, if not add them
            for (i in mostAffectedDistricts.indices) {
                val district: District = mostAffectedDistricts[i]
                if (Constant.userSelectedDistrict.trim { it <= ' ' }
                        .equals(district.name.trim { it <= ' ' }, ignoreCase = true)) {
                    userDistrictIsInMostAffectedState = true
                    userDistrictIsInMostAffectedStateIndex = i
                    break
                }
            }

            // if user selected district is not in the most affected state list, add them from all state list
            if (!userDistrictIsInMostAffectedState) {
                for (district in districtList) {
                    if (Constant.userSelectedDistrict.trim { it <= ' ' }
                            .equals(district.name.trim { it <= ' ' }, ignoreCase = true)) {
                        mostAffectedDistricts.add(0, district)
                        break
                    }
                }
            } else {

                // move user selected district at 0th index
                val temp: District = mostAffectedDistricts[userDistrictIsInMostAffectedStateIndex]
                mostAffectedDistricts.removeAt(userDistrictIsInMostAffectedStateIndex)
                mostAffectedDistricts.add(0, temp)
            }
        }
        return mostAffectedDistricts
    }

    private fun getObjectTotalOfAffectedDistricts(stateWiseDataArrayList: ArrayList<StateDistrictWiseResponse>?): ArrayList<District> {
        val districtObjectTotal: ArrayList<District> = ArrayList<District>()

        //Get user selected state
        var userSelectedState: StateDistrictWiseResponse? = null
        for (stateDistrictWiseResponse in stateWiseDataArrayList!!) {
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(stateDistrictWiseResponse.name.trim { it <= ' ' }, ignoreCase = true)) {
                userSelectedState = stateDistrictWiseResponse
                break
            }
        }
        var districtList: ArrayList<District> = ArrayList<District>()
        if (userSelectedState != null) districtList = userSelectedState.districtArrayList
        var confirmed = 0
        val lastUpdatedTime = ""
        var deltaConfirmed = 0
        for (district in districtList) {
            confirmed += district.confirmed
            deltaConfirmed += district.delta?.confirmed ?: 0
        }
        val districtDelta = DistrictDelta(deltaConfirmed)
        districtObjectTotal.add(
            District("Total", confirmed, lastUpdatedTime, districtDelta)
        )
        return districtObjectTotal
    }

    private fun getSortedAffectedStates(stateWiseDataArrayList: ArrayList<StateWiseData>): ArrayList<StateWiseData> {

        // Remove Total if there is any
        for (i in stateWiseDataArrayList.indices) {
            if (stateWiseDataArrayList[i].state.equals("Total", ignoreCase = true) ||
                stateWiseDataArrayList[i].stateCode.equals("TT", ignoreCase = true)
            ) {
                stateWiseDataArrayList.removeAt(i)
                break
            }
        }

        // Sort State according to confirmed cases
        stateWiseDataArrayList.sortWith { o1, o2 -> // descending
            o2.confirmed.toInt() - o1.confirmed.toInt()
        }
        val mostAffectedStates: ArrayList<StateWiseData> =
            ArrayList<StateWiseData>(stateWiseDataArrayList)

        // Check if user has selected their state and district then only perform this action
        if (Constant.isUserSelected) {
            var userStateIsInMostAffectedState = false
            var userStateIsInMostAffectedStateIndex = -1
            // check if user selected state is in list, if not add them
            for (i in mostAffectedStates.indices) {
                val mostAffectedState: StateWiseData = mostAffectedStates[i]
                if (Constant.userSelectedState.trim { it <= ' ' }
                        .equals(
                            mostAffectedState.state.trim { it <= ' ' },
                            ignoreCase = true
                        )
                ) {
                    userStateIsInMostAffectedState = true
                    userStateIsInMostAffectedStateIndex = i
                    break
                }
            }

            // if user selected state is not in the most affected state list, add them from all state list
            if (!userStateIsInMostAffectedState) {
                for (stateWiseData in stateWiseDataArrayList) {
                    if (Constant.userSelectedState.trim { it <= ' ' }
                            .equals(
                                stateWiseData.state.trim { it <= ' ' },
                                ignoreCase = true
                            )
                    ) {
                        mostAffectedStates.add(0, stateWiseData)
                        break
                    }
                }
            } else {

                // move user selected state at 0th index
                val temp: StateWiseData = mostAffectedStates[userStateIsInMostAffectedStateIndex]
                mostAffectedStates.removeAt(userStateIsInMostAffectedStateIndex)
                mostAffectedStates.add(0, temp)
            }
        }
        return mostAffectedStates
    }

    private fun getObjectTotalOfAffectedState(stateWiseDataArrayList: ArrayList<StateWiseData>): StateWiseData {
        var objectTotalOfMostAffectedState: StateWiseData? = null
        for (stateWiseData in stateWiseDataArrayList) {
            if ("Total".equals(stateWiseData.state, ignoreCase = true) ||
                "TT".equals(stateWiseData.stateCode, ignoreCase = true)
            ) {
                objectTotalOfMostAffectedState = stateWiseData
            }
        }
        if (objectTotalOfMostAffectedState == null) {
            var active = 0
            var confirmed = 0
            var deaths = 0
            var deltaConfirmed = 0
            var deltaDeaths = 0
            var deltaRecovered = 0
            val lastUpdatedTime = ""
            var recovered = 0
            val state = "Total"
            val stateCode = "TT"

            // if Object "Total" Does not found in the list, compute manually
            for (stateWiseData in stateWiseDataArrayList) {
                active += stateWiseData.active.toInt()
                confirmed += stateWiseData.confirmed.toInt()
                deaths += stateWiseData.deaths.toInt()
                deltaConfirmed += stateWiseData.deltaConfirmed.toInt()
                deltaDeaths += stateWiseData.deltaDeaths.toInt()
                deltaRecovered += stateWiseData.deltaRecovered.toInt()
                recovered += stateWiseData.recovered.toInt()
            }
            objectTotalOfMostAffectedState = StateWiseData(
                active.toString(),
                confirmed.toString(),
                deaths.toString(),
                deltaConfirmed.toString(),
                deltaDeaths.toString(),
                deltaRecovered.toString(),
                lastUpdatedTime,
                recovered.toString(),
                state,
                stateCode
            )
        }
        return objectTotalOfMostAffectedState
    }
}