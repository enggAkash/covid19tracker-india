package `in`.engineerakash.covid19india.ui.track

import `in`.engineerakash.covid19india.chartutil.IndexAxisValueFormatter
import `in`.engineerakash.covid19india.databinding.FragmentTotalAndDailyGraphBinding
import `in`.engineerakash.covid19india.enums.ChartType
import `in`.engineerakash.covid19india.enums.TotalOrDaily
import `in`.engineerakash.covid19india.pojo.TimeSeriesData
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.Helper
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

private const val TIME_SERIES_DATA_LIST = "time_series_data_list"
private const val TOTAL_OR_DAILY = "total_or_daily"

class TotalAndDailyGraphFragment : Fragment() {

    private var timeSeriesDataList: ArrayList<TimeSeriesData> = arrayListOf()
    private var totalOrDaily: TotalOrDaily = TotalOrDaily.DAILY

    private lateinit var listener: TotalAndDailyGraphListener

    private lateinit var binding: FragmentTotalAndDailyGraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            timeSeriesDataList =
                it.getParcelableArrayList(TIME_SERIES_DATA_LIST)!!
            totalOrDaily = it.getSerializable(TOTAL_OR_DAILY) as TotalOrDaily
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTotalAndDailyGraphBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.totalConfirmedCasesTimelineChartMore.setOnClickListener {
            listener.showCompleteChart(ChartType.TOTAL_CONFIRMED)
        }

        binding.totalDeathCasesTimelineChartMore.setOnClickListener {
            listener.showCompleteChart(ChartType.TOTAL_DECEASED)
        }

        binding.totalRecoveredCasesTimelineChartMore.setOnClickListener {
            listener.showCompleteChart(ChartType.TOTAL_RECOVERED)
        }

        binding.dailyConfirmedCasesTimelineChartMore.setOnClickListener {
            listener.showCompleteChart(ChartType.DAILY_CONFIRMED)
        }

        binding.dailyDeathCasesTimelineChartMore.setOnClickListener {
            listener.showCompleteChart(ChartType.DAILY_DECEASED)
        }

        binding.dailyRecoveredCasesTimelineChartMore.setOnClickListener {
            listener.showCompleteChart(ChartType.DAILY_RECOVERED)
        }

        updateGraph()
    }

    fun updateTimeSeriesDataList(
        timeSeriesDataList: ArrayList<TimeSeriesData>,
        forceUpdate: Boolean = false
    ) {
        // if data is already present then return, unless we want to update the data forcefully
        if (this.timeSeriesDataList.isNotEmpty()) {
            if (!forceUpdate)
                return
        }

        this.timeSeriesDataList = timeSeriesDataList
        updateGraph()
    }

    private fun updateGraph() {

        val recentTimeSeriesList: List<TimeSeriesData> =
            getBarChartData(timeSeriesDataList)

        changeGraphVisibility(totalOrDaily)

        if (totalOrDaily == TotalOrDaily.DAILY) {

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

        } else {

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
        }

    }

    private fun changeGraphVisibility(totalOrDaily: TotalOrDaily) {
        /*DAILY*/
        binding.dailyConfirmedCasesTimelineContainer.visibility =
            if (totalOrDaily == TotalOrDaily.DAILY) View.VISIBLE else View.GONE

        binding.dailyDeathCasesTimelineContainer.visibility =
            if (totalOrDaily == TotalOrDaily.DAILY) View.VISIBLE else View.GONE

        binding.dailyRecoveredCasesTimelineContainer.visibility =
            if (totalOrDaily == TotalOrDaily.DAILY) View.VISIBLE else View.GONE

        /*TOTAL*/
        binding.totalConfirmedCasesTimelineContainer.visibility =
            if (totalOrDaily == TotalOrDaily.TOTAL) View.VISIBLE else View.GONE

        binding.totalDeathCasesTimelineContainer.visibility =
            if (totalOrDaily == TotalOrDaily.TOTAL) View.VISIBLE else View.GONE

        binding.totalRecoveredCasesTimelineContainer.visibility =
            if (totalOrDaily == TotalOrDaily.TOTAL) View.VISIBLE else View.GONE
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

    private fun setTotalCasesTimelineChart(
        chartType: ChartType,
        timeSeriesList: List<TimeSeriesData>
    ) {
        val chart: BarChart
        val xAxisList = java.util.ArrayList<String>() // bar values of x axis
        val values =
            java.util.ArrayList<BarEntry>() // bar values of y axis; x will be in multiple of 1 harcoded
        for (timeSeriesData in timeSeriesList) xAxisList.add(
            timeSeriesData.date.trim { it <= ' ' })
        var tempCount = 0 // it must start with 0
        when (chartType) {
            ChartType.TOTAL_CONFIRMED -> {
                chart = binding.totalConfirmedCasesTimelineChart
                binding.totalConfirmedCasesTimelineTitleTv.text =
                    "#Total Confirmed Cases in ${Constant.userSelectedCountry} Timeline"
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
                    "#Total Death Cases in ${Constant.userSelectedCountry} Timeline"
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
                    "#Total Recovered Cases in ${Constant.userSelectedCountry} Timeline"
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
                    "#Daily Confirmed Cases in ${Constant.userSelectedCountry} Timeline"
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
                    "#Daily Death Cases in ${Constant.userSelectedCountry} Timeline"
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
                    "#Daily Recovered Cases in ${Constant.userSelectedCountry} Timeline"
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
        set1.color = Helper.getBarChartColor(context, chartType)
        val dataSets = java.util.ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.barWidth = 0.9f
        chart.data = data
    }

    private fun setChartConfig(chart: BarChart, xAxisList: java.util.ArrayList<String>) {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param timeSeriesData ArrayList<TimeSeriesData>.
         * @return A new instance of fragment TotalAndDailyGraphFragment.
         */
        @JvmStatic
        fun newInstance(
            timeSeriesData: ArrayList<TimeSeriesData>,
            totalOrDaily: TotalOrDaily,
            listener: TotalAndDailyGraphListener
        ) =
            TotalAndDailyGraphFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(TIME_SERIES_DATA_LIST, timeSeriesData)
                    putSerializable(TOTAL_OR_DAILY, totalOrDaily)
                }
                this.listener = listener
            }
    }

    interface TotalAndDailyGraphListener {
        fun showCompleteChart(chartType: ChartType)
    }
}