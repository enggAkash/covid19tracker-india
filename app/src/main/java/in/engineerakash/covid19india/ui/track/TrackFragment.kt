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
import `in`.engineerakash.covid19india.util.DateTimeUtil
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

private const val TAG = "TrackFragment"

class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding

    private var timeSeriesStateWiseResponse: ArrayList<TimeSeriesStateWiseResponse> = arrayListOf()
    private var countryWideCasesTimeSeries: TimeSeriesStateWiseResponse =
        TimeSeriesStateWiseResponse(Constant.TOTAL_ITEM_NAME, Constant.TOTAL_ITEM_CODE)
    private var stateDistrictList: ArrayList<StateDistrictWiseResponse> = arrayListOf()
    private lateinit var navController: NavController

    private val totalAndDailyGraphList = arrayListOf<String>("Daily", "Total")
    private lateinit var totalAndDailyGraphAdapter: TotalAndDailyGraphAdapter
    private var totalAndDailyGraphFragmentList = arrayListOf<Fragment?>(null, null)

    private val args: TrackFragmentArgs by navArgs<TrackFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = this.findNavController()

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

                fillDashboard(stateDistrictList)
                fillMostAffectedStateSection(stateDistrictList)

            })

        viewModel.getTimeSeriesStateWiseResponseLiveData()
            .observe(viewLifecycleOwner, {

                timeSeriesStateWiseResponse = it

                countryWideCasesTimeSeries =
                    getCountryWideCasesTimeSeries(timeSeriesStateWiseResponse)

                (totalAndDailyGraphFragmentList[binding.totalAndDailyViewPager.currentItem] as TotalAndDailyGraphFragment?)?.updateTimeSeriesDataList(
                    countryWideCasesTimeSeries.timeSeriesList,
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
                    countryWideCasesTimeSeries.timeSeriesList
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
                    countryWideCasesTimeSeries,
                    stateDistrictList.toTypedArray()
                )
            )
        }

        binding.completeDistrictList.setOnClickListener {
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailListFragment(
                    ListType.DISTRICT,
                    countryWideCasesTimeSeries,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.completeStateList.setOnClickListener {
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailListFragment(
                    ListType.STATE,
                    countryWideCasesTimeSeries,
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

    private fun fillDashboard(stateDistrictList: ArrayList<StateDistrictWiseResponse>) {
        val dashboardStats: StateDistrictWiseResponse? = getCurrentStateStats(stateDistrictList)
        binding.totalCasesInUserStateTitleTv.text = "#Cases in ${dashboardStats?.name ?: "-"}"
        binding.defaultStateNameTv.text = (dashboardStats?.name ?: "-")

        binding.totalConfirmedCasesTv.text = "${dashboardStats?.total?.confirmed ?: "-"}"

        binding.deltaConfirmedCasesTv.text = if ((dashboardStats?.delta?.confirmed ?: 0) == 0)
            "-"
        else
            "(+${dashboardStats?.delta?.confirmed ?: 0})"

        binding.totalDeathCasesTv.text = "${dashboardStats?.total?.deceased ?: "-"}"
        binding.deltaDeathCasesTv.text = if ((dashboardStats?.delta?.deceased ?: 0) == 0)
            "-"
        else
            "(+${dashboardStats?.delta?.deceased ?: 0})"

        binding.totalRecoveredCasesTv.text = "${dashboardStats?.total?.recovered ?: "-"}"
        binding.deltaRecoveredCasesTv.text = if ((dashboardStats?.delta?.recovered ?: 0) == 0)
            "-"
        else
            "(+${dashboardStats?.delta?.recovered ?: "-"})"

        binding.dashBoardStatsLastUpdateTime.text =
            "Last Updated: ${DateTimeUtil.parseMetaDateTimeToAppsDefaultDateTime(dashboardStats?.meta?.lastUpdated ?: "-")}"
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

    private fun fillMostAffectedStateSection(stateDistrictList: ArrayList<StateDistrictWiseResponse>) {
        val mostAffectedStateWiseList: ArrayList<StateDistrictWiseResponse> =
            getMostAffectedStates(stateDistrictList)
        mostAffectedStateWiseList
            .add(getObjectTotalOfAffectedState(stateDistrictList))
        binding.mostAffectedStateTitleTv.text =
            "#Most Affected States & UT in ${Constant.userSelectedCountry.trim { it <= ' ' }}"
        val stateWiseAdapter = StateWiseAdapter(mostAffectedStateWiseList, false)
        binding.mostAffectedStateRv.layoutManager = LinearLayoutManager(context)
        binding.mostAffectedStateRv.adapter = stateWiseAdapter
    }


    private fun getCurrentStateStats(stateDistrictList: ArrayList<StateDistrictWiseResponse>): StateDistrictWiseResponse? {
        var currentStateStats: StateDistrictWiseResponse? = null
        for (stateDistrictData in stateDistrictList) {
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(stateDistrictData.name.trim { it <= ' ' }, ignoreCase = true)) {
                currentStateStats = stateDistrictData
                break
            }
        }
        return currentStateStats
    }

    private fun getObjectTotalOfAffectedState(stateDistrictList: ArrayList<StateDistrictWiseResponse>): StateDistrictWiseResponse {
        var objectTotalOfMostAffectedState: StateDistrictWiseResponse? = null
        for (stateWiseData in stateDistrictList) {
            if (Constant.TOTAL_ITEM_CODE.equals(stateWiseData.code, ignoreCase = true) ||
                Constant.TOTAL_ITEM_NAME.equals(stateWiseData.name, ignoreCase = true)
            ) {
                objectTotalOfMostAffectedState = stateWiseData
            }
        }
        if (objectTotalOfMostAffectedState == null) {
            var confirmed = 0
            var deaths = 0
            var deltaConfirmed = 0
            var deltaDeaths = 0
            var deltaRecovered = 0
            var tested = 0
            var vaccinated1 = 0
            var vaccinated2 = 0

            var recovered = 0
            val state = Constant.TOTAL_ITEM_NAME
            val stateCode = Constant.TOTAL_ITEM_CODE

            // if Object "Total" Does not found in the list, compute manually
            for (stateWiseData in stateDistrictList) {
                confirmed += stateWiseData.total?.confirmed ?: 0
                deaths += stateWiseData.total?.deceased ?: 0
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
                Total(confirmed, deaths, recovered)
            )
        }
        return objectTotalOfMostAffectedState
    }

    private fun getMostAffectedStates(stateDistrictList: ArrayList<StateDistrictWiseResponse>): ArrayList<StateDistrictWiseResponse> {
        val mostAffectedStates: ArrayList<StateDistrictWiseResponse> = arrayListOf()

        // Remove Total if there is any
        for (i in stateDistrictList.indices) {
            if (stateDistrictList[i].code.equals(Constant.TOTAL_ITEM_CODE, ignoreCase = true) ||
                stateDistrictList[i].name.equals(Constant.TOTAL_ITEM_NAME, ignoreCase = true)
            ) {
                stateDistrictList.removeAt(i)
                break
            }
        }

        // Sort State according to confirmed cases
        stateDistrictList.sortWith { o1, o2 -> // descending
            (o2.total?.confirmed ?: 0) - (o1.total?.confirmed ?: 0)
        }

        if (stateDistrictList.size >= Constant.MOST_AFFECTED_STATES_COUNT) {
            // if state list is greater than the data count to show, then its perfect, add the required subset
            mostAffectedStates.addAll(
                stateDistrictList.subList(
                    0,
                    Constant.MOST_AFFECTED_STATES_COUNT - 1
                )
            )
        } else {
            // if state list is less than the data count required to show, then add all of them
            mostAffectedStates.addAll(stateDistrictList)
        }

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
                for (stateWiseData in stateDistrictList) {
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
                    userSelectedState?.delta?.confirmed, userSelectedState?.delta?.deceased,
                    userSelectedState?.delta?.recovered
                ),
                Meta(
                    userSelectedState?.meta?.date, userSelectedState?.meta?.lastUpdated,
                    userSelectedState?.meta?.population
                ),
                Total(
                    userSelectedState?.total?.confirmed, userSelectedState?.total?.deceased,
                    userSelectedState?.total?.recovered
                )
            )
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
            (o2.total?.confirmed ?: 0) - (o1.total?.confirmed ?: 0)
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
                        countryWideCasesTimeSeries,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
            ChartType.TOTAL_DECEASED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        countryWideCasesTimeSeries,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
            ChartType.TOTAL_RECOVERED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        countryWideCasesTimeSeries,
                        stateDistrictList.toTypedArray()
                    )
                )
            }

            ChartType.DAILY_CONFIRMED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        countryWideCasesTimeSeries,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
            ChartType.DAILY_DECEASED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        countryWideCasesTimeSeries,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
            ChartType.DAILY_RECOVERED -> {
                navController.navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                        chartType,
                        countryWideCasesTimeSeries,
                        stateDistrictList.toTypedArray()
                    )
                )
            }
        }
    }

    private fun getCountryWideCasesTimeSeries(timeSeriesList: ArrayList<TimeSeriesStateWiseResponse>): TimeSeriesStateWiseResponse {

        val countryWideCaseTimeSeries =
            TimeSeriesStateWiseResponse(Constant.TOTAL_ITEM_NAME, Constant.TOTAL_ITEM_CODE)

        for (timeSeriesData in timeSeriesList) {
            if (
                timeSeriesData.code.equals(Constant.TOTAL_ITEM_CODE, true) ||
                timeSeriesData.name.equals(Constant.TOTAL_ITEM_NAME, true)
            ) {
                countryWideCaseTimeSeries.timeSeriesList = timeSeriesData.timeSeriesList
                break
            }
        }

        return countryWideCaseTimeSeries
    }

    inner class TotalAndDailyGraphAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = totalAndDailyGraphList.size

        override fun createFragment(position: Int): Fragment {

            val fragment: Fragment = if (position == 0)
                TotalAndDailyGraphFragment.newInstance(
                    countryWideCasesTimeSeries.timeSeriesList,
                    TotalOrDaily.DAILY
                )
            else
                TotalAndDailyGraphFragment.newInstance(
                    countryWideCasesTimeSeries.timeSeriesList,
                    TotalOrDaily.TOTAL
                )

            totalAndDailyGraphFragmentList[position] = fragment

            return fragment
        }
    }

}