package `in`.engineerakash.covid19india.ui.track

import `in`.engineerakash.covid19india.api.CovidClient
import `in`.engineerakash.covid19india.chartutil.IndexAxisValueFormatter
import `in`.engineerakash.covid19india.databinding.FragmentTrackBinding
import `in`.engineerakash.covid19india.enums.ChartType
import `in`.engineerakash.covid19india.enums.ListType
import `in`.engineerakash.covid19india.pojo.*
import `in`.engineerakash.covid19india.ui.home.DistrictWiseAdapter
import `in`.engineerakash.covid19india.ui.home.StateWiseAdapter
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.Helper.getBarChartColor
import `in`.engineerakash.covid19india.util.Helper.parseDate
import `in`.engineerakash.covid19india.util.JsonExtractor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.gson.Gson
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.IOException
import java.util.*

class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding
    private var disposables: CompositeDisposable? = null

    private var timeSeriesStateWiseResponse: TimeSeriesStateWiseResponse = TimeSeriesStateWiseResponse()
    private var stateDistrictList: ArrayList<StateDistrictWiseResponse> = arrayListOf()
    private lateinit var navController: NavController

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

        disposables = CompositeDisposable()

        getStateDistrictData()
        getTimeSeriesAndStateWiseData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)

        binding.totalCasesInUserStateContainer.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailListFragment(
                    ListType.STATE,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.totalConfirmedCasesTimelineChartMore.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                    ChartType.TOTAL_CONFIRMED,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.totalDeathCasesTimelineChartMore.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                    ChartType.TOTAL_DECEASED,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.totalRecoveredCasesTimelineChartMore.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                    ChartType.TOTAL_RECOVERED,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.dailyConfirmedCasesTimelineChartMore.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                    ChartType.DAILY_CONFIRMED,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.dailyDeathCasesTimelineChartMore.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                    ChartType.DAILY_DECEASED,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.dailyRecoveredCasesTimelineChartMore.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailGraphFragment(
                    ChartType.DAILY_RECOVERED,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.completeDistrictList.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailListFragment(
                    ListType.DISTRICT,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
        binding.completeStateList.setOnClickListener { v: View? ->
            navController.navigate(
                TrackFragmentDirections.actionTrackFragmentToDetailListFragment(
                    ListType.STATE,
                    timeSeriesStateWiseResponse,
                    stateDistrictList.toTypedArray()
                )
            )
        }
    }

    private fun getTimeSeriesAndStateWiseData() {
        CovidClient
            .getInstance()
            .getTimeSeriesAndStateWiseData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    disposables!!.add(d)
                }

                override fun onSuccess(responseBodyResponse: ResponseBody) {
                    try {
                        val response = responseBodyResponse.string()
                        timeSeriesStateWiseResponse =
                            Gson().fromJson<TimeSeriesStateWiseResponse>(
                                response,
                                TimeSeriesStateWiseResponse::class.java
                            )
                        val casesTimeSeriesList: ArrayList<TimeSeriesData> =
                            timeSeriesStateWiseResponse?.casesTimeSeriesArrayList ?: arrayListOf()
                        val stateWiseDataList: ArrayList<StateWiseData> =
                            timeSeriesStateWiseResponse?.stateWiseDataArrayList ?: arrayListOf()

                        fillDashboard(stateWiseDataList)
                        fillMostAffectedStateSection(stateWiseDataList)
                        val recentTimeSeriesList: List<TimeSeriesData> =
                            getBarChartData(casesTimeSeriesList)
                        setTotalCasesTimelineChart(
                            ChartType.TOTAL_CONFIRMED,
                            recentTimeSeriesList
                        )
                        setTotalCasesTimelineChart(
                            ChartType.TOTAL_DECEASED,
                            recentTimeSeriesList
                        )
                        setTotalCasesTimelineChart(
                            ChartType.TOTAL_RECOVERED,
                            recentTimeSeriesList
                        )
                        setTotalCasesTimelineChart(
                            ChartType.DAILY_CONFIRMED,
                            recentTimeSeriesList
                        )
                        setTotalCasesTimelineChart(
                            ChartType.DAILY_DECEASED,
                            recentTimeSeriesList
                        )
                        setTotalCasesTimelineChart(
                            ChartType.DAILY_RECOVERED,
                            recentTimeSeriesList
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    private fun getBarChartData(timeSeriesList: ArrayList<TimeSeriesData>): List<TimeSeriesData> {
        return if (timeSeriesList.size < Constant.BAR_CHART_DATE_COUNT) {
            timeSeriesList
        } else {
            timeSeriesList.subList(
                timeSeriesList.size - Constant.BAR_CHART_DATE_COUNT,
                timeSeriesList.size
            )
        }
    }

    private fun fillDashboard(stateWiseDataArrayList: ArrayList<StateWiseData>) {
        val dashboardStats: StateWiseData = getCurrentStateStats(stateWiseDataArrayList)
        binding.totalCasesInUserStateTitleTv.text = "#Total Cases in " + dashboardStats.state
        binding.defaultStateNameTv.text = dashboardStats.state
        binding.totalConfirmedCasesTv.text = """
            Confirmed
            ---------
            ${dashboardStats.confirmed}
            """.trimIndent()
        binding.totalDeathCasesTv.text = """
            Death
            ---------
            ${dashboardStats.deaths}
            """.trimIndent()
        binding.totalRecoveredCasesTv.text = """
            Recovered
            ---------
            ${dashboardStats.recovered}
            """.trimIndent()
        binding.dashBoardStatsLastUpdateTime.text =
            "Last Updated: " + parseDate(dashboardStats.lastUpdatedTime)
    }

    private fun fillMostAffectedDistrictSection(stateDistrictList: ArrayList<StateDistrictWiseResponse>?) {
        val mostAffectedDistricts: ArrayList<District> = getMostAffectedDistricts(stateDistrictList)
        mostAffectedDistricts.addAll(getObjectTotalOfAffectedDistricts(stateDistrictList))
        binding.mostAffectedDistrictTitleTv.text =
            "#Most Affected District in " + Constant.userSelectedState.trim { it <= ' ' }
        val districtWiseAdapter = DistrictWiseAdapter(mostAffectedDistricts)
        binding.mostAffectedDistrictRv.layoutManager = LinearLayoutManager(context)
        binding.mostAffectedDistrictRv.adapter = districtWiseAdapter
    }

    private fun fillMostAffectedStateSection(stateWiseDataArrayList: ArrayList<StateWiseData>) {
        val mostAffectedStateWiseList: ArrayList<StateWiseData> =
            getMostAffectedStates(stateWiseDataArrayList)
        mostAffectedStateWiseList
            .add(getObjectTotalOfAffectedState(stateWiseDataArrayList))
        binding.mostAffectedStateTitleTv.text =
            "#Most Affected States in " + Constant.userSelectedCountry.trim { it <= ' ' }
        val stateWiseAdapter = StateWiseAdapter(mostAffectedStateWiseList)
        binding.mostAffectedStateRv.layoutManager = LinearLayoutManager(context)
        binding.mostAffectedStateRv.adapter = stateWiseAdapter
    }

    private fun getCurrentStateStats(stateWiseDataArrayList: ArrayList<StateWiseData>): StateWiseData {
        var currentStateStats = StateWiseData()
        for (stateWiseData in stateWiseDataArrayList) {
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(stateWiseData.state?.trim { it <= ' ' }, ignoreCase = true)) {
                currentStateStats = stateWiseData
                break
            }
        }
        return currentStateStats
    }

    private fun getStateDistrictData() {

        CovidClient
            .getInstance()
            .getStateDistrictWiseData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    disposables!!.add(d)
                }

                override fun onSuccess(responseBodyResponse: ResponseBody) {
                    try {
                        val response = responseBodyResponse.string()
                        stateDistrictList =
                            JsonExtractor.parseStateDistrictWiseResponseJson(response)
                        fillMostAffectedDistrictSection(stateDistrictList)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
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
        if (stateWiseDataArrayList.size >= Constant.MOST_AFFECTED_DISTRICT_COUNT) {
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

    private fun setTotalCasesTimelineChart(
        chartType: ChartType,
        timeSeriesList: List<TimeSeriesData>
    ) {
        val chart: BarChart
        val xAxisList = ArrayList<String>() // bar values of x axis
        val values =
            ArrayList<BarEntry>() // bar values of y axis; x will be in multiple of 1 harcoded
        for (timeSeriesData in timeSeriesList) xAxisList.add(
            timeSeriesData.date.trim { it <= ' ' })
        var tempCount = 0 // it must start with 0
        when (chartType) {
            ChartType.TOTAL_CONFIRMED -> {
                chart = binding.totalConfirmedCasesTimelineChart
                binding.totalConfirmedCasesTimelineTitleTv.text =
                    "#Total Confirmed Cases in " + Constant.userSelectedCountry + " Timeline"
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        timeSeriesData.totalConfirmed.toFloat()
                    )
                )
            }
            ChartType.TOTAL_DECEASED -> {
                chart = binding.totalDeathCasesTimelineChart
                binding.totalDeathCasesTimelineTitleTv.text =
                    "#Total Death Cases in " + Constant.userSelectedCountry + " Timeline"
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        timeSeriesData.totalDeceased.toFloat()
                    )
                )
            }
            ChartType.TOTAL_RECOVERED -> {
                chart = binding.totalRecoveredCasesTimelineChart
                binding.totalRecoveredCasesTimelineTitleTv.text =
                    "#Total Recovered Cases in " + Constant.userSelectedCountry + " Timeline"
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        timeSeriesData.totalRecovered.toFloat()
                    )
                )
            }
            ChartType.DAILY_CONFIRMED -> {
                chart = binding.dailyConfirmedCasesTimelineChart
                binding.dailyConfirmedCasesTimelineTitleTv.text =
                    "#Daily Confirmed Cases in " + Constant.userSelectedCountry + " Timeline"
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        timeSeriesData.dailyConfirmed.toFloat()
                    )
                )
            }
            ChartType.DAILY_DECEASED -> {
                chart = binding.dailyDeathCasesTimelineChart
                binding.dailyDeathCasesTimelineTitleTv.text =
                    "#Daily Death Cases in " + Constant.userSelectedCountry + " Timeline"
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        timeSeriesData.dailyDeceased.toFloat()
                    )
                )
            }
            ChartType.DAILY_RECOVERED -> {
                chart = binding.dailyRecoveredCasesTimelineChart
                binding.dailyRecoveredCasesTimelineTitleTv.text =
                    "#Daily Recovered Cases in " + Constant.userSelectedCountry + " Timeline"
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        timeSeriesData.dailyRecovered.toFloat()
                    )
                )
            }
            else -> return
        }
        setChartConfig(chart, xAxisList)
        val set1 = BarDataSet(values, "")
        set1.color = getBarChartColor(context, chartType)
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.barWidth = 0.9f
        chart.data = data
    }

    private fun setChartConfig(chart: BarChart, xAxisList: ArrayList<String>) {
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.description.isEnabled = false
        chart.setPinchZoom(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.setScaleEnabled(false)
        val xAxisFormatter: ValueFormatter = IndexAxisValueFormatter(xAxisList)
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.valueFormatter = xAxisFormatter

        // Don't show Right Side Y Axis
        chart.axisRight.isEnabled = false

        // Uniform bar height
        chart.axisLeft.axisMinimum = 0f

        // Smooth Animation
        chart.animateY(1000)

        // Don't show BarDataSet Label
        chart.legend.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (disposables != null && !disposables!!.isDisposed) disposables!!.dispose()
    }

    companion object {
        private const val TAG = "TrackFragment"
    }
}