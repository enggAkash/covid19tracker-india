package `in`.engineerakash.covid19india.ui.detaillist

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.FragmentDetailListBinding
import `in`.engineerakash.covid19india.enums.ListType
import `in`.engineerakash.covid19india.pojo.*
import `in`.engineerakash.covid19india.ui.track.DistrictWiseAdapter
import `in`.engineerakash.covid19india.ui.track.StateWiseAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val detailListFragmentArgs: DetailListFragmentArgs by navArgs()

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

            binding.stateHeader.root.visibility = View.GONE

            val sortedAffectedDistricts: ArrayList<District> =
                getSortedAffectedDistricts(stateDistrictList)
            sortedAffectedDistricts.addAll(getObjectTotalOfAffectedDistricts(stateDistrictList))

            val districtWiseAdapter = DistrictWiseAdapter(sortedAffectedDistricts)

            binding.mostAffectedDistrictAndStateRv.layoutManager = LinearLayoutManager(context)
            binding.mostAffectedDistrictAndStateRv.adapter = districtWiseAdapter

        } else if (currentListType === ListType.STATE) {

            binding.districtHeader.root.visibility = View.GONE

            binding.stateHeader.root.visibility = View.VISIBLE

            val sortedAffectedStateWiseList: ArrayList<StateDistrictWiseResponse> =
                getSortedAffectedStates(stateDistrictList)
            sortedAffectedStateWiseList
                .add(getObjectTotalOfAffectedState(stateDistrictList))

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
            (o2.total?.confirmed ?: 0) - (o1.total?.confirmed ?: 0)
        }
        val mostAffectedDistricts: ArrayList<District> = ArrayList<District>(districtList)

        // Check if user has selected their state and district then only perform this action
        if (Constant.locationIsSelectedByUser) {
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

        districtObjectTotal.add(
            District(
                Constant.TOTAL_ITEM_NAME,
                Delta(
                    userSelectedState?.total?.confirmed,
                    userSelectedState?.total?.deceased,
                    userSelectedState?.total?.recovered,
                ),
                Meta(
                    userSelectedState?.meta?.date, userSelectedState?.meta?.lastUpdated,
                    userSelectedState?.meta?.population
                ),
                Total(
                    userSelectedState?.total?.confirmed, userSelectedState?.total?.deceased,
                    userSelectedState?.total?.recovered, userSelectedState?.total?.tested,
                    userSelectedState?.total?.vaccinated1, userSelectedState?.total?.vaccinated2
                )
            )
        )
        return districtObjectTotal
    }

    private fun getSortedAffectedStates(stateWiseDataArrayList: ArrayList<StateDistrictWiseResponse>): ArrayList<StateDistrictWiseResponse> {

        // Remove Total if there is any
        for (i in stateWiseDataArrayList.indices) {
            if (stateWiseDataArrayList[i].code.equals(Constant.TOTAL_ITEM_CODE, ignoreCase = true) ||
                stateWiseDataArrayList[i].name.equals(Constant.TOTAL_ITEM_NAME, ignoreCase = true)
            ) {
                stateWiseDataArrayList.removeAt(i)
                break
            }
        }

        // Sort State according to confirmed cases
        stateWiseDataArrayList.sortWith { o1, o2 -> // descending
            (o2.total?.confirmed ?: 0) - (o1.total?.confirmed ?: 0)
        }
        val mostAffectedStates: ArrayList<StateDistrictWiseResponse> =
            ArrayList<StateDistrictWiseResponse>(stateWiseDataArrayList)

        // Check if user has selected their state and district then only perform this action
        if (Constant.locationIsSelectedByUser) {
            var userStateIsInMostAffectedState = false
            var userStateIsInMostAffectedStateIndex = -1
            // check if user selected state is in list, if not add them
            for (i in mostAffectedStates.indices) {
                val mostAffectedState: StateDistrictWiseResponse = mostAffectedStates[i]
                if (Constant.userSelectedState.trim { it <= ' ' }
                        .equals(
                            mostAffectedState.name.trim { it <= ' ' },
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
                                stateWiseData.name.trim { it <= ' ' },
                                ignoreCase = true
                            )
                    ) {
                        mostAffectedStates.add(0, stateWiseData)
                        break
                    }
                }
            } else {

                // move user selected state at 0th index
                val temp: StateDistrictWiseResponse =
                    mostAffectedStates[userStateIsInMostAffectedStateIndex]
                mostAffectedStates.removeAt(userStateIsInMostAffectedStateIndex)
                mostAffectedStates.add(0, temp)
            }
        }
        return mostAffectedStates
    }

    private fun getObjectTotalOfAffectedState(stateWiseDataArrayList: ArrayList<StateDistrictWiseResponse>): StateDistrictWiseResponse {
        var objectTotalOfMostAffectedState: StateDistrictWiseResponse? = null
        for (stateWiseData in stateWiseDataArrayList) {
            if (Constant.TOTAL_ITEM_CODE.equals(stateWiseData.code, ignoreCase = true) ||
                Constant.TOTAL_ITEM_NAME.equals(stateWiseData.name, ignoreCase = true)
            ) {
                objectTotalOfMostAffectedState = stateWiseData
            }
        }
        if (objectTotalOfMostAffectedState == null) {
            var confirmed = 0
            var deaths = 0
            var recovered = 0
            var deltaConfirmed = 0
            var deltaDeaths = 0
            var deltaRecovered = 0
            var tested = 0
            var vaccinated1 = 0
            var vaccinated2 = 0
            val state = Constant.TOTAL_ITEM_NAME
            val stateCode = Constant.TOTAL_ITEM_CODE

            // if Object "Total" Does not found in the list, compute manually
            for (stateWiseData in stateWiseDataArrayList) {
                confirmed += stateWiseData.total?.confirmed ?: 0
                deaths += stateWiseData.total?.deceased ?: 0
                recovered += stateWiseData.total?.recovered ?: 0
                tested += stateWiseData.total?.tested ?: 0
                vaccinated1 += stateWiseData.total?.vaccinated1 ?: 0
                vaccinated2 += stateWiseData.total?.vaccinated2 ?: 0
                deltaConfirmed += stateWiseData.delta?.confirmed ?: 0
                deltaDeaths += stateWiseData.delta?.deceased ?: 0
                deltaRecovered += stateWiseData.delta?.recovered ?: 0
                recovered += stateWiseData.delta?.recovered ?: 0
            }
            objectTotalOfMostAffectedState = StateDistrictWiseResponse(
                state, stateCode, Delta(deltaConfirmed, deltaDeaths, deltaRecovered),
                arrayListOf(), Meta(),
                Total(confirmed, deaths, recovered, tested, vaccinated1, vaccinated2)
            )
        }
        return objectTotalOfMostAffectedState
    }
}