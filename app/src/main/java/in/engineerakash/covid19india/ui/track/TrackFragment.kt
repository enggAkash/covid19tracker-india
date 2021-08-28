package `in`.engineerakash.covid19india.ui.track

import `in`.engineerakash.covid19india.databinding.FragmentTrackBinding
import `in`.engineerakash.covid19india.enums.ChartType
import `in`.engineerakash.covid19india.enums.ListType
import `in`.engineerakash.covid19india.enums.TotalOrDaily
import `in`.engineerakash.covid19india.pojo.*
import `in`.engineerakash.covid19india.ui.home.MainViewModel
import `in`.engineerakash.covid19india.ui.home.MainViewModelFactory
import `in`.engineerakash.covid19india.util.ChooseLocationStartedFrom
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.Helper.parseDate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

private const val TAG = "TrackFragment"

class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding

    private var timeSeriesStateWiseResponse: TimeSeriesStateWiseResponse =
        TimeSeriesStateWiseResponse()
    private var stateDistrictList: ArrayList<StateDistrictWiseResponse> = arrayListOf()
    private lateinit var navController: NavController

    private val totalAndDailyGraphList = arrayListOf<String>("Daily", "Total")
    private lateinit var totalAndDailyGraphAdapter: TotalAndDailyGraphAdapter
    private var totalAndDailyGraphFragmentList = arrayListOf<Fragment?>(null, null)

    private val args: TrackFragmentArgs by navArgs<TrackFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = this.findNavController()

        //todo check if location is selected(Constant.locationIsSelectedByUser), if not redirect them to choose location screen
        if (Constant.userSelectedState.isEmpty() || Constant.userSelectedDistrict.isEmpty()) {
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToChooseLocationFragmentWithClearBackstack(
                    ChooseLocationStartedFrom.TRACK_FRAG
                )
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackBinding.inflate(inflater, container, false)

        initComponent()

        return binding.root
    }

    private fun initComponent() {
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: MainViewModel by activityViewModels { MainViewModelFactory(activity!!.application) }

        viewModel.getStateDistrictListLiveData()
            .observe(viewLifecycleOwner, {
                stateDistrictList.clear()
                stateDistrictList.addAll(it)
                fillMostAffectedDistrictSection(stateDistrictList)
            })

        viewModel.getTimeSeriesStateWiseResponseLiveData()
            .observe(viewLifecycleOwner, {

                timeSeriesStateWiseResponse = it

                val casesTimeSeriesList: ArrayList<TimeSeriesData> =
                    timeSeriesStateWiseResponse.casesTimeSeriesArrayList

                val stateWiseDataList: java.util.ArrayList<StateWiseData> =
                    timeSeriesStateWiseResponse.stateWiseDataArrayList

                fillDashboard(stateWiseDataList)
                fillMostAffectedStateSection(stateWiseDataList)

                (totalAndDailyGraphFragmentList[binding.totalAndDailyViewPager.currentItem] as TotalAndDailyGraphFragment?)?.updateTimeSeriesDataList(
                    timeSeriesStateWiseResponse.casesTimeSeriesArrayList,
                    true
                )

            })

        viewModel.getGraphChartMoreClickLiveData().observe(viewLifecycleOwner, {
            if (it != null) {
                showCompleteChart(it)

                viewModel.setGraphChartMoreClickLiveData(null)
            }
        })

        totalAndDailyGraphAdapter = TotalAndDailyGraphAdapter(this)
        binding.totalAndDailyViewPager.adapter = totalAndDailyGraphAdapter

        TabLayoutMediator(
            binding.totalAndDailyTab,
            binding.totalAndDailyViewPager,
            true, true,
        ) { tab, position -> tab.text = totalAndDailyGraphList[position] }.attach()

        binding.totalAndDailyViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                (totalAndDailyGraphFragmentList[position] as TotalAndDailyGraphFragment?)?.updateTimeSeriesDataList(
                    timeSeriesStateWiseResponse.casesTimeSeriesArrayList
                )
            }

        })

        setupClickListeners()

        if (args.refreshData) {
            viewModel.fetchTimeSeriesAndStateWiseData()
            viewModel.fetchStateDistrictData()
        }
    }

    private fun setupClickListeners() {
        binding.totalCasesInUserStateContainer.setOnClickListener {
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailListFragment(
                    ListType.STATE,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }

        binding.completeDistrictList.setOnClickListener {
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailListFragment(
                    ListType.DISTRICT,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.completeStateList.setOnClickListener {
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailListFragment(
                    ListType.STATE,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }

        binding.changeLocationTv.setOnClickListener {
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToChooseLocationFragment(
                    ChooseLocationStartedFrom.TRACK_FRAG
                )
            )
        }
    }

    private fun fillDashboard(stateWiseDataArrayList: ArrayList<StateWiseData>) {
        val dashboardStats: StateWiseData = getCurrentStateStats(stateWiseDataArrayList)
        binding.totalCasesInUserStateTitleTv.text = "#Total Cases in " + dashboardStats.state
        binding.defaultStateNameTv.text = dashboardStats.state
        binding.totalConfirmedCasesTv.text = "Confirmed\n---------\n${dashboardStats.confirmed}"
        binding.totalDeathCasesTv.text = "Death\n---------\n${dashboardStats.deaths}"
        binding.totalRecoveredCasesTv.text = "Recovered\n---------\n${dashboardStats.recovered}"
        binding.dashBoardStatsLastUpdateTime.text =
            "Last Updated: ${parseDate(dashboardStats.lastUpdatedTime)}"
    }

    private fun fillMostAffectedDistrictSection(stateDistrictList: ArrayList<StateDistrictWiseResponse>?) {
        val mostAffectedDistricts: ArrayList<District> = getMostAffectedDistricts(stateDistrictList)
        mostAffectedDistricts.addAll(getObjectTotalOfAffectedDistricts(stateDistrictList))
        binding.mostAffectedDistrictTitleTv.text =
            "#Most Affected District in ${Constant.userSelectedState.trim { it <= ' ' }}"
        val districtWiseAdapter = DistrictWiseAdapter(mostAffectedDistricts, false)
        binding.mostAffectedDistrictRv.layoutManager = LinearLayoutManager(context)
        binding.mostAffectedDistrictRv.adapter = districtWiseAdapter
    }

    private fun fillMostAffectedStateSection(stateWiseDataArrayList: ArrayList<StateWiseData>) {
        val mostAffectedStateWiseList: ArrayList<StateWiseData> =
            getMostAffectedStates(stateWiseDataArrayList)
        mostAffectedStateWiseList
            .add(getObjectTotalOfAffectedState(stateWiseDataArrayList))
        binding.mostAffectedStateTitleTv.text =
            "#Most Affected States & UT in ${Constant.userSelectedCountry.trim { it <= ' ' }}"
        val stateWiseAdapter = StateWiseAdapter(mostAffectedStateWiseList, false)
        binding.mostAffectedStateRv.layoutManager = LinearLayoutManager(context)
        binding.mostAffectedStateRv.adapter = stateWiseAdapter
    }


    private fun getCurrentStateStats(stateWiseDataArrayList: ArrayList<StateWiseData>): StateWiseData {
        var currentStateStats = StateWiseData()
        for (stateWiseData in stateWiseDataArrayList) {
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(stateWiseData.state.trim { it <= ' ' }, ignoreCase = true)) {
                currentStateStats = stateWiseData
                break
            }
        }
        return currentStateStats
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

    private fun getMostAffectedStates(stateWiseDataArrayList: ArrayList<StateWiseData>): ArrayList<StateWiseData> {
        val mostAffectedStates: ArrayList<StateWiseData> = ArrayList<StateWiseData>()

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

        if (stateWiseDataArrayList.size >= Constant.MOST_AFFECTED_STATES_COUNT) {
            // if state list is greater than the data count to show, then its perfect, add the required subset
            mostAffectedStates.addAll(
                stateWiseDataArrayList.subList(
                    0,
                    Constant.MOST_AFFECTED_STATES_COUNT - 1
                )
            )
        } else {
            // if state list is less than the data count required to show, then add all of them
            mostAffectedStates.addAll(stateWiseDataArrayList)
        }

        // Check if user has selected their state and district then only perform this action
        if (Constant.locationIsSelectedByUser) {
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

    private fun getMostAffectedDistricts(stateWiseDataArrayList: ArrayList<StateDistrictWiseResponse>?): ArrayList<District> {
        val mostAffectedDistricts: ArrayList<District> = ArrayList<District>()

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
        if (districtList.size >= Constant.MOST_AFFECTED_DISTRICT_COUNT) {
            // if district list is greater than the data count to show, then its perfect, add the required subset
            mostAffectedDistricts.addAll(
                districtList.subList(
                    0,
                    Constant.MOST_AFFECTED_DISTRICT_COUNT - 1
                )
            )
        } else {
            // if district list is less than the data count required to show, then add all of them
            mostAffectedDistricts.addAll(districtList)
        }

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

    private fun showCompleteChart(chartType: ChartType) {

        when (chartType) {
            ChartType.TOTAL_CONFIRMED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        timeSeriesStateWiseResponse,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
            ChartType.TOTAL_DECEASED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        timeSeriesStateWiseResponse,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
            ChartType.TOTAL_RECOVERED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        timeSeriesStateWiseResponse,
                        stateDistrictList.toTypedArray()
                    )
                )
            }

            ChartType.DAILY_CONFIRMED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        timeSeriesStateWiseResponse,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
            ChartType.DAILY_DECEASED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        timeSeriesStateWiseResponse,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
            ChartType.DAILY_RECOVERED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        timeSeriesStateWiseResponse,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
        }
    }

    inner class TotalAndDailyGraphAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = totalAndDailyGraphList.size

        override fun createFragment(position: Int): Fragment {

            val fragment: Fragment = if (position == 0)
                TotalAndDailyGraphFragment.newInstance(
                    timeSeriesStateWiseResponse.casesTimeSeriesArrayList,
                    TotalOrDaily.DAILY
                )
            else
                TotalAndDailyGraphFragment.newInstance(
                    timeSeriesStateWiseResponse.casesTimeSeriesArrayList,
                    TotalOrDaily.TOTAL
                )

            totalAndDailyGraphFragmentList[position] = fragment

            return fragment
        }
    }

}