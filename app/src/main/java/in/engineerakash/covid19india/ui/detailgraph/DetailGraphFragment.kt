package `in`.engineerakash.covid19india.ui.detailgraph

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.chartutil.IndexAxisValueFormatter
import `in`.engineerakash.covid19india.databinding.FragmentDetailGraphBinding
import `in`.engineerakash.covid19india.enums.ChartType
import `in`.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import `in`.engineerakash.covid19india.pojo.TimeSeriesData
import `in`.engineerakash.covid19india.pojo.TimeSeriesStateWiseResponse
import `in`.engineerakash.covid19india.ui.home.MainActivity
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.DateTimeUtil
import `in`.engineerakash.covid19india.util.Helper.getBarChartColor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import java.util.*

class DetailGraphFragment : Fragment() {

    private lateinit var currentChartType: ChartType

    private lateinit var timeSeriesStateWiseResponse: TimeSeriesStateWiseResponse
    private var stateDistrictList: ArrayList<StateDistrictWiseResponse> = arrayListOf()

    private lateinit var binding: FragmentDetailGraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val detailGraphFragmentArgs: DetailGraphFragmentArgs by navArgs()

        currentChartType = detailGraphFragmentArgs.chartType
        timeSeriesStateWiseResponse = detailGraphFragmentArgs.timeSeriesDateWiseResponse
        stateDistrictList =
            ArrayList<StateDistrictWiseResponse>(listOf(*detailGraphFragmentArgs.stateDistrictWiseResponse))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailGraphBinding.inflate(inflater, container, false)

        initComponent()

        return binding.root
    }

    private fun initComponent() {
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowHomeEnabled(true)

        if (Constant.SHOW_ADS)
            loadAds()
    }

    override fun onStart() {
        super.onStart()
        if (activity != null) (activity as MainActivity?)?.changeThemeColor(
            false, getBarChartColor(
                context, currentChartType
            )
        )
        val casesTimeSeriesList: ArrayList<TimeSeriesData> =
            timeSeriesStateWiseResponse.timeSeriesList
        setTotalCasesTimelineChart(currentChartType, casesTimeSeriesList)

        when (currentChartType) {
            ChartType.TOTAL_CONFIRMED -> {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "Total Confirmed Cases"
                (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("in " + Constant.userSelectedCountry)
            }
            ChartType.TOTAL_DECEASED -> {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "Total Death Cases"
                (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("in " + Constant.userSelectedCountry)
            }
            ChartType.TOTAL_RECOVERED -> {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "Total Recovered Cases"
                (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("in " + Constant.userSelectedCountry)
            }
            ChartType.DAILY_CONFIRMED -> {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "Daily Confirmed Cases"
                (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("in " + Constant.userSelectedCountry)
            }
            ChartType.DAILY_DECEASED -> {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "Daily Death Cases"
                (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("in " + Constant.userSelectedCountry)
            }
            ChartType.DAILY_RECOVERED -> {
                (activity as AppCompatActivity?)?.supportActionBar?.title = "Daily Recovered Cases"
                (activity as AppCompatActivity?)?.supportActionBar?.setSubtitle("in " + Constant.userSelectedCountry)
            }
        }
    }

    private fun setTotalCasesTimelineChart(
        chartType: ChartType?,
        timeSeriesList: List<TimeSeriesData>
    ) {
        val chart: BarChart
        val xAxisList = ArrayList<String>() // bar values of y axis
        val values =
            ArrayList<BarEntry>() // bar values of y axis; x will be in multiple of 1 harcoded
        for (timeSeriesData in timeSeriesList)
            xAxisList.add(
                DateTimeUtil.parseDateTimeToAppsDefaultDateFormat(
                    timeSeriesData.date.trim { it <= ' ' }, "yyyy-MM-dd"
                )
            )
        var tempCount = 0 // it must start with 0
        when (chartType) {
            ChartType.TOTAL_CONFIRMED -> {
                chart = binding.timelineChart
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        (timeSeriesData.total?.confirmed ?: 0f).toFloat()
                    )
                )
            }
            ChartType.TOTAL_DECEASED -> {
                chart = binding.timelineChart
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        (timeSeriesData.total?.deceased ?: 0f).toFloat()
                    )
                )
            }
            ChartType.TOTAL_RECOVERED -> {
                chart = binding.timelineChart
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        (timeSeriesData.total?.recovered ?: 0f).toFloat()
                    )
                )
            }
            ChartType.DAILY_CONFIRMED -> {
                chart = binding.timelineChart
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        (timeSeriesData.delta?.confirmed ?: 0f).toFloat()
                    )
                )
            }
            ChartType.DAILY_DECEASED -> {
                chart = binding.timelineChart
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        (timeSeriesData.delta?.deceased ?: 0f).toFloat()
                    )
                )
            }
            ChartType.DAILY_RECOVERED -> {
                chart = binding.timelineChart
                for (timeSeriesData in timeSeriesList) values.add(
                    BarEntry(
                        tempCount++.toFloat(),
                        (timeSeriesData.delta?.recovered ?: 0f).toFloat()
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

        // Maximum of 5 element visible at a time, rest is scrollable
        chart.setVisibleXRangeMaximum(5f)
        chart
            .moveViewToAnimated(
                chart.xChartMax,
                chart.yChartMax,
                YAxis.AxisDependency.RIGHT,
                2000
            )
    }

    private fun setChartConfig(chart: BarChart, xAxisList: ArrayList<String>) {
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.description.isEnabled = false

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(true)

//        chart.setDoubleTapToZoomEnabled(true);
//        chart.setScaleEnabled(true);
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
        chart.axisLeft.isEnabled = true
        chart.setFitBars(true)

        // Smooth Animation
        chart.animateY(1000)

        // Don't show BarDataSet Label
        chart.legend.isEnabled = false
    }

    override fun onStop() {
        super.onStop()

        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.app_name)
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = ""
        (activity as MainActivity?)?.changeThemeColor(true, 0)
    }

    private fun loadAds() {
        context.let {
            MobileAds.initialize(it!!) {}

            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }

}