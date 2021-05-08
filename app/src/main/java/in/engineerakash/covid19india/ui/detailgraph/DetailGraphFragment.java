package in.engineerakash.covid19india.ui.detailgraph;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.engineerakash.covid19india.R;
import in.engineerakash.covid19india.chartutil.IndexAxisValueFormatter;
import in.engineerakash.covid19india.databinding.FragmentDetailGraphBinding;
import in.engineerakash.covid19india.enums.ChartType;
import in.engineerakash.covid19india.pojo.StateDistrictWiseResponse;
import in.engineerakash.covid19india.pojo.TimeSeriesData;
import in.engineerakash.covid19india.pojo.TimeSeriesStateWiseResponse;
import in.engineerakash.covid19india.ui.home.MainActivity;
import in.engineerakash.covid19india.util.Constant;
import in.engineerakash.covid19india.util.Helper;

public class DetailGraphFragment extends Fragment {

    private ChartType currentChartType;
    private TimeSeriesStateWiseResponse timeSeriesStateWiseResponse;
    private ArrayList<StateDistrictWiseResponse> stateDistrictList;

    private FragmentDetailGraphBinding binding;

    public DetailGraphFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DetailGraphFragmentArgs detailGraphFragmentArgs = DetailGraphFragmentArgs.fromBundle(getArguments());

        currentChartType = detailGraphFragmentArgs.getChartType();
        timeSeriesStateWiseResponse = detailGraphFragmentArgs.getTimeSeriesDateWiseResponse();
        stateDistrictList = new ArrayList<StateDistrictWiseResponse>(Arrays.asList(detailGraphFragmentArgs.getStateDistrictWiseResponse()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailGraphBinding.inflate(inflater, container, false);

        initComponent();

        return binding.getRoot();

    }

    private void initComponent() {
        if (((AppCompatActivity) getActivity()) != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() != null)
            ((MainActivity) getActivity()).changeThemeColor(false, Helper.getBarChartColor(getContext(), currentChartType));

        ArrayList<TimeSeriesData> casesTimeSeriesList =
                timeSeriesStateWiseResponse.getCasesTimeSeriesArrayList();

        setTotalCasesTimelineChart(currentChartType, casesTimeSeriesList);

        switch (currentChartType) {
            case TOTAL_CONFIRMED:
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Total Confirmed Cases");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("in " + Constant.userSelectedCountry);

                break;
            case TOTAL_DECEASED:

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Total Death Cases");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("in " + Constant.userSelectedCountry);

                break;
            case TOTAL_RECOVERED:

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Total Recovered Cases");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("in " + Constant.userSelectedCountry);

                break;
            case DAILY_CONFIRMED:

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Daily Confirmed Cases");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("in " + Constant.userSelectedCountry);

                break;
            case DAILY_DECEASED:

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Daily Death Cases");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("in " + Constant.userSelectedCountry);

                break;
            case DAILY_RECOVERED:

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Daily Recovered Cases");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("in " + Constant.userSelectedCountry);

                break;
            default:
                return;
        }

    }

    private void setTotalCasesTimelineChart(ChartType chartType,
                                            List<TimeSeriesData> timeSeriesList) {

        BarChart chart;
        ArrayList<String> xAxisList = new ArrayList<>(); // bar values of y axis
        ArrayList<BarEntry> values = new ArrayList<>(); // bar values of y axis; x will be in multiple of 1 harcoded

        for (TimeSeriesData timeSeriesData : timeSeriesList)
            xAxisList.add(timeSeriesData.getDate().trim());

        int tempCount = 0; // it must start with 0
        switch (chartType) {
            case TOTAL_CONFIRMED:
                chart = binding.timelineChart;

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getTotalConfirmed())));

                break;
            case TOTAL_DECEASED:
                chart = binding.timelineChart;

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getTotalDeceased())));

                break;
            case TOTAL_RECOVERED:
                chart = binding.timelineChart;

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getTotalRecovered())));

                break;
            case DAILY_CONFIRMED:
                chart = binding.timelineChart;

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getDailyConfirmed())));

                break;
            case DAILY_DECEASED:
                chart = binding.timelineChart;

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getDailyDeceased())));

                break;
            case DAILY_RECOVERED:
                chart = binding.timelineChart;

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getDailyRecovered())));

                break;
            default:
                return;
        }

        setChartConfig(chart, xAxisList);

        BarDataSet set1 = new BarDataSet(values, "");

        set1.setColor(Helper.getBarChartColor(getContext(), chartType));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        chart.setData(data);

        // Maximum of 5 element visible at a time, rest is scrollable
        chart.setVisibleXRangeMaximum(5);


        chart
                .moveViewToAnimated(
                        chart.getXChartMax(),
                        chart.getYChartMax(),
                        YAxis.AxisDependency.RIGHT,
                        2000
                );
    }

    private void setChartConfig(BarChart chart, ArrayList<String> xAxisList) {
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(true);

//        chart.setDoubleTapToZoomEnabled(true);
//        chart.setScaleEnabled(true);

        ValueFormatter xAxisFormatter = new IndexAxisValueFormatter(xAxisList);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setValueFormatter(xAxisFormatter);

        // Don't show Right Side Y Axis
        chart.getAxisRight().setEnabled(false);

        // Uniform bar height
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setEnabled(true);

        chart.setFitBars(true);

        // Smooth Animation
        chart.animateY(1000);

        // Don't show BarDataSet Label
        chart.getLegend().setEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (((AppCompatActivity) getActivity()) != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");


            ((MainActivity) getActivity()).changeThemeColor(true, 0);
        }
    }

}
