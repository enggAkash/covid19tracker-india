package in.engineerakash.covid19india.ui.track;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.engineerakash.covid19india.api.CovidClient;
import in.engineerakash.covid19india.chartutil.IndexAxisValueFormatter;
import in.engineerakash.covid19india.databinding.FragmentTrackBinding;
import in.engineerakash.covid19india.enums.ListType;
import in.engineerakash.covid19india.pojo.District;
import in.engineerakash.covid19india.pojo.DistrictDelta;
import in.engineerakash.covid19india.pojo.StateDistrictWiseResponse;
import in.engineerakash.covid19india.pojo.StateWiseData;
import in.engineerakash.covid19india.pojo.TimeSeriesData;
import in.engineerakash.covid19india.pojo.TimeSeriesStateWiseResponse;
import in.engineerakash.covid19india.ui.home.DistrictWiseAdapter;
import in.engineerakash.covid19india.ui.home.StateWiseAdapter;
import in.engineerakash.covid19india.util.Constant;
import in.engineerakash.covid19india.util.Helper;
import in.engineerakash.covid19india.util.JsonExtractor;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class TrackFragment extends Fragment {

    private static final String TAG = "TrackFragment";

    private FragmentTrackBinding binding;

    private CompositeDisposable disposables;
    private TimeSeriesStateWiseResponse timeSeriesStateWiseResponse;
    private ArrayList<StateDistrictWiseResponse> stateDistrictList;
    private OnTrackFragmentListener listener;


    public TrackFragment() {

    }

    public static TrackFragment newInstance() {
        TrackFragment fragment = new TrackFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTrackBinding.inflate(inflater, container, false);

        initComponent();

        return binding.getRoot();
    }

    private void initComponent() {

        if (((AppCompatActivity) ((AppCompatActivity) getActivity())) != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        disposables = new CompositeDisposable();

        getStateDistrictData();

        getTimeSeriesAndStateWiseData();

        binding.totalCasesInUserStateContainer.setOnClickListener(v -> {
            listener.onDetailListClicked(ListType.STATE, timeSeriesStateWiseResponse, stateDistrictList);
        });


        binding.totalConfirmedCasesTimelineChartMore.setOnClickListener(v -> {
            listener.onDetailGraphClicked(Constant.ChartType.TOTAL_CONFIRMED, timeSeriesStateWiseResponse, stateDistrictList);
        });

        binding.totalDeathCasesTimelineChartMore.setOnClickListener(v -> {
            listener.onDetailGraphClicked(Constant.ChartType.TOTAL_DECEASED, timeSeriesStateWiseResponse, stateDistrictList);
        });

        binding.totalRecoveredCasesTimelineChartMore.setOnClickListener(v -> {
            listener.onDetailGraphClicked(Constant.ChartType.TOTAL_RECOVERED, timeSeriesStateWiseResponse, stateDistrictList);
        });

        binding.dailyConfirmedCasesTimelineChartMore.setOnClickListener(v -> {
            listener.onDetailGraphClicked(Constant.ChartType.DAILY_CONFIRMED, timeSeriesStateWiseResponse, stateDistrictList);
        });

        binding.dailyDeathCasesTimelineChartMore.setOnClickListener(v -> {
            listener.onDetailGraphClicked(Constant.ChartType.DAILY_DECEASED, timeSeriesStateWiseResponse, stateDistrictList);
        });

        binding.dailyRecoveredCasesTimelineChartMore.setOnClickListener(v -> {
            listener.onDetailGraphClicked(Constant.ChartType.DAILY_RECOVERED, timeSeriesStateWiseResponse, stateDistrictList);
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.completeDistrictList.setOnClickListener(v -> {

            /*Bundle bundle = new Bundle();
            bundle.putParcelable("time_series_state_wise_response", timeSeriesStateWiseResponse);
            bundle.putParcelableArrayList("state_district_list", stateDistrictList);

            listener.onDetailListClicked(ListType.DISTRICT, timeSeriesStateWiseResponse, stateDistrictList);*/

            Navigation.findNavController(binding.getRoot()).navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailListFragment(ListType.DISTRICT, timeSeriesStateWiseResponse, stateDistrictList.toArray(new StateDistrictWiseResponse[0]))
            );
        });

        binding.completeStateList.setOnClickListener(v -> {

            /*Bundle bundle = new Bundle();
            bundle.putParcelable("time_series_state_wise_response", timeSeriesStateWiseResponse);
            bundle.putParcelableArrayList("state_district_list", stateDistrictList);

            listener.onDetailListClicked(ListType.STATE, timeSeriesStateWiseResponse, stateDistrictList);*/

            Navigation.findNavController(binding.getRoot()).navigate(
                    TrackFragmentDirections.actionTrackFragmentToDetailListFragment(ListType.STATE, timeSeriesStateWiseResponse, stateDistrictList.toArray(new StateDistrictWiseResponse[0]))
            );
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTrackFragmentListener) {
            listener = (OnTrackFragmentListener) context;
        } else {
            throw new RuntimeException("Activity must implement OnTrackFragmentListener");
        }
    }

    private void getTimeSeriesAndStateWiseData() {

        CovidClient
                .getInstance()
                .getTimeSeriesAndStateWiseData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(ResponseBody responseBodyResponse) {

                        try {
                            String response = responseBodyResponse.string();

                            timeSeriesStateWiseResponse = new Gson().fromJson(response, TimeSeriesStateWiseResponse.class);

                            ArrayList<TimeSeriesData> casesTimeSeriesList =
                                    timeSeriesStateWiseResponse.getCasesTimeSeriesArrayList();

                            ArrayList<StateWiseData> stateWiseDataList =
                                    timeSeriesStateWiseResponse.getStateWiseDataArrayList();

                            fillDashboard(stateWiseDataList);

                            fillMostAffectedStateSection(stateWiseDataList);

                            List<TimeSeriesData> recentTimeSeriesList = getBarChartData(casesTimeSeriesList);

                            setTotalCasesTimelineChart(Constant.ChartType.TOTAL_CONFIRMED, recentTimeSeriesList);
                            setTotalCasesTimelineChart(Constant.ChartType.TOTAL_DECEASED, recentTimeSeriesList);
                            setTotalCasesTimelineChart(Constant.ChartType.TOTAL_RECOVERED, recentTimeSeriesList);
                            setTotalCasesTimelineChart(Constant.ChartType.DAILY_CONFIRMED, recentTimeSeriesList);
                            setTotalCasesTimelineChart(Constant.ChartType.DAILY_DECEASED, recentTimeSeriesList);
                            setTotalCasesTimelineChart(Constant.ChartType.DAILY_RECOVERED, recentTimeSeriesList);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                });
    }

    private List<TimeSeriesData> getBarChartData(ArrayList<TimeSeriesData> timeSeriesList) {
        if (timeSeriesList.size() < Constant.BAR_CHART_DATE_COUNT) {
            return timeSeriesList;
        } else {
            return timeSeriesList.subList(
                    timeSeriesList.size() - Constant.BAR_CHART_DATE_COUNT,
                    timeSeriesList.size());
        }
    }

    private void fillDashboard(ArrayList<StateWiseData> stateWiseDataArrayList) {

        StateWiseData dashboardStats = getCurrentStateStats(stateWiseDataArrayList);


        binding.totalCasesInUserStateTitleTv.setText("#Total Cases in " + dashboardStats.getState());

        binding.defaultStateNameTv.setText(dashboardStats.getState());

        binding.totalConfirmedCasesTv.setText("Confirmed\n---------\n" + dashboardStats.getConfirmed());
        binding.totalDeathCasesTv.setText("Death\n---------\n" + dashboardStats.getDeaths());
        binding.totalRecoveredCasesTv.setText("Recovered\n---------\n" + dashboardStats.getRecovered());

        binding.dashBoardStatsLastUpdateTime.setText("Last Updated: " + Helper.parseDate(dashboardStats.getLastUpdatedTime()));
    }

    private void fillMostAffectedDistrictSection(ArrayList<StateDistrictWiseResponse> stateDistrictList) {

        ArrayList<District> mostAffectedDistricts = getMostAffectedDistricts(stateDistrictList);

        mostAffectedDistricts.addAll(getObjectTotalOfAffectedDistricts(stateDistrictList));

        binding.mostAffectedDistrictTitleTv.setText("#Most Affected District in " + Constant.userSelectedState.trim());

        DistrictWiseAdapter districtWiseAdapter = new DistrictWiseAdapter(mostAffectedDistricts);
        binding.mostAffectedDistrictRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.mostAffectedDistrictRv.setAdapter(districtWiseAdapter);
    }

    private void fillMostAffectedStateSection(ArrayList<StateWiseData> stateWiseDataArrayList) {


        ArrayList<StateWiseData> mostAffectedStateWiseList =
                getMostAffectedStates(stateWiseDataArrayList);

        mostAffectedStateWiseList
                .add(getObjectTotalOfAffectedState(stateWiseDataArrayList));

        binding.mostAffectedStateTitleTv.setText("#Most Affected States in " + Constant.userSelectedCountry.trim());

        StateWiseAdapter stateWiseAdapter = new StateWiseAdapter(mostAffectedStateWiseList);
        binding.mostAffectedStateRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.mostAffectedStateRv.setAdapter(stateWiseAdapter);
    }

    private StateWiseData getCurrentStateStats(ArrayList<StateWiseData> stateWiseDataArrayList) {

        StateWiseData currentStateStats = new StateWiseData();

        for (StateWiseData stateWiseData : stateWiseDataArrayList) {
            if (Constant.userSelectedState.trim().equalsIgnoreCase(stateWiseData.getState().trim())) {
                currentStateStats = stateWiseData;
                break;
            }
        }

        return currentStateStats;
    }

    private void getStateDistrictData() {
        CovidClient
                .getInstance()
                .getStateDistrictWiseData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onSuccess(ResponseBody responseBodyResponse) {

                        try {
                            String response = responseBodyResponse.string();

                            stateDistrictList = JsonExtractor.parseStateDistrictWiseResponseJson(response);

                            fillMostAffectedDistrictSection(stateDistrictList);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                });

    }

    private StateWiseData getObjectTotalOfAffectedState(ArrayList<StateWiseData> stateWiseDataArrayList) {

        StateWiseData objectTotalOfMostAffectedState = null;

        for (StateWiseData stateWiseData : stateWiseDataArrayList) {
            if ("Total".equalsIgnoreCase(stateWiseData.getState()) ||
                    "TT".equalsIgnoreCase(stateWiseData.getStateCode())) {
                objectTotalOfMostAffectedState = stateWiseData;
            }
        }

        if (objectTotalOfMostAffectedState == null) {

            int active = 0;
            int confirmed = 0;
            int deaths = 0;
            int deltaConfirmed = 0;
            int deltaDeaths = 0;
            int deltaRecovered = 0;
            String lastUpdatedTime = "";
            int recovered = 0;
            String state = "Total";
            String stateCode = "TT";

            // if Object "Total" Does not found in the list, compute manually
            for (StateWiseData stateWiseData : stateWiseDataArrayList) {
                active += Integer.parseInt(stateWiseData.getActive());
                confirmed += Integer.parseInt(stateWiseData.getConfirmed());
                deaths += Integer.parseInt(stateWiseData.getDeaths());
                deltaConfirmed += Integer.parseInt(stateWiseData.getDeltaConfirmed());
                deltaDeaths += Integer.parseInt(stateWiseData.getDeltaDeaths());
                deltaRecovered += Integer.parseInt(stateWiseData.getDeltaRecovered());
                recovered += Integer.parseInt(stateWiseData.getRecovered());
            }

            objectTotalOfMostAffectedState =
                    new StateWiseData(String.valueOf(active), String.valueOf(confirmed),
                            String.valueOf(deaths), String.valueOf(deltaConfirmed), String.valueOf(deltaDeaths),
                            String.valueOf(deltaRecovered), lastUpdatedTime, String.valueOf(recovered),
                            state, stateCode);
        }

        return objectTotalOfMostAffectedState;
    }

    private ArrayList<StateWiseData> getMostAffectedStates(ArrayList<StateWiseData> stateWiseDataArrayList) {

        ArrayList<StateWiseData> mostAffectedStates = new ArrayList<>();

        // Remove Total if there is any
        for (int i = 0; i < stateWiseDataArrayList.size(); i++) {
            if (stateWiseDataArrayList.get(i).getState().equalsIgnoreCase("Total") ||
                    stateWiseDataArrayList.get(i).getStateCode().equalsIgnoreCase("TT")) {
                stateWiseDataArrayList.remove(i);
                break;
            }
        }

        // Sort State according to confirmed cases
        Collections.sort(stateWiseDataArrayList, new Comparator<StateWiseData>() {
            @Override
            public int compare(StateWiseData o1, StateWiseData o2) {
                // descending
                return Integer.parseInt(o2.getConfirmed()) - Integer.parseInt(o1.getConfirmed());
            }
        });

        if (stateWiseDataArrayList.size() >= Constant.MOST_AFFECTED_STATES_COUNT) {
            // if state list is greater than the data count to show, then its perfect, add the required subset
            mostAffectedStates.addAll(stateWiseDataArrayList.subList(0, Constant.MOST_AFFECTED_STATES_COUNT - 1));

        } else {
            // if state list is less than the data count required to show, then add all of them
            mostAffectedStates.addAll(stateWiseDataArrayList);
        }

        // Check if user has selected their state and district then only perform this action
        if (Constant.isUserSelected) {

            boolean userStateIsInMostAffectedState = false;
            int userStateIsInMostAffectedStateIndex = -1;
            // check if user selected state is in list, if not add them
            for (int i = 0; i < mostAffectedStates.size(); i++) {
                StateWiseData mostAffectedState = mostAffectedStates.get(i);
                if (Constant.userSelectedState.trim().equalsIgnoreCase(mostAffectedState.getState().trim())) {
                    userStateIsInMostAffectedState = true;
                    userStateIsInMostAffectedStateIndex = i;
                    break;
                }
            }

            // if user selected state is not in the most affected state list, add them from all state list
            if (!userStateIsInMostAffectedState) {

                for (StateWiseData stateWiseData : stateWiseDataArrayList) {
                    if (Constant.userSelectedState.trim().equalsIgnoreCase(stateWiseData.getState().trim())) {
                        mostAffectedStates.add(0, stateWiseData);
                        break;
                    }
                }
            } else {

                // move user selected state at 0th index
                StateWiseData temp = mostAffectedStates.get(userStateIsInMostAffectedStateIndex);
                mostAffectedStates.remove(userStateIsInMostAffectedStateIndex);
                mostAffectedStates.add(0, temp);

            }
        }

        return mostAffectedStates;
    }

    private ArrayList<District> getObjectTotalOfAffectedDistricts(ArrayList<StateDistrictWiseResponse> stateWiseDataArrayList) {

        ArrayList<District> districtObjectTotal = new ArrayList<>();

        //Get user selected state
        StateDistrictWiseResponse userSelectedState = null;
        for (StateDistrictWiseResponse stateDistrictWiseResponse : stateWiseDataArrayList) {
            if (Constant.userSelectedState.trim().equalsIgnoreCase(stateDistrictWiseResponse.getName().trim())) {
                userSelectedState = stateDistrictWiseResponse;
                break;
            }
        }

        ArrayList<District> districtList = new ArrayList<>();

        if (userSelectedState != null)
            districtList = userSelectedState.getDistrictArrayList();

        int confirmed = 0;
        String lastUpdatedTime = "";
        int deltaConfirmed = 0;

        for (District district : districtList) {
            confirmed += district.getConfirmed();
            deltaConfirmed += district.getDelta().getConfirmed();
        }

        DistrictDelta districtDelta = new DistrictDelta(deltaConfirmed);

        districtObjectTotal.add(
                new District("Total", confirmed, lastUpdatedTime, districtDelta)
        );

        return districtObjectTotal;
    }

    private ArrayList<District> getMostAffectedDistricts(ArrayList<StateDistrictWiseResponse> stateWiseDataArrayList) {

        ArrayList<District> mostAffectedDistricts = new ArrayList<>();

        //Get user selected state
        StateDistrictWiseResponse userSelectedState = null;
        for (StateDistrictWiseResponse stateDistrictWiseResponse : stateWiseDataArrayList) {
            if (Constant.userSelectedState.trim().equalsIgnoreCase(stateDistrictWiseResponse.getName().trim())) {
                userSelectedState = stateDistrictWiseResponse;
                break;
            }
        }

        ArrayList<District> districtList = new ArrayList<>();

        if (userSelectedState != null)
            districtList = userSelectedState.getDistrictArrayList();


        // Sort District according to confirmed cases
        Collections.sort(districtList, new Comparator<District>() {
            @Override
            public int compare(District o1, District o2) {
                // descending
                return o2.getConfirmed() - o1.getConfirmed();
            }
        });

        if (stateWiseDataArrayList.size() >= Constant.MOST_AFFECTED_DISTRICT_COUNT) {
            // if district list is greater than the data count to show, then its perfect, add the required subset
            mostAffectedDistricts.addAll(districtList.subList(0, Constant.MOST_AFFECTED_DISTRICT_COUNT - 1));

        } else {
            // if district list is less than the data count required to show, then add all of them
            mostAffectedDistricts.addAll(districtList);
        }

        // Check if user has selected their state and district then only perform this action
        if (Constant.isUserSelected) {

            boolean userDistrictIsInMostAffectedState = false;
            int userDistrictIsInMostAffectedStateIndex = -1;

            // check if user selected state is in list, if not add them
            for (int i = 0; i < mostAffectedDistricts.size(); i++) {
                District district = mostAffectedDistricts.get(i);
                if (Constant.userSelectedDistrict.trim().equalsIgnoreCase(district.getName().trim())) {
                    userDistrictIsInMostAffectedState = true;
                    userDistrictIsInMostAffectedStateIndex = i;
                    break;
                }
            }

            // if user selected district is not in the most affected state list, add them from all state list
            if (!userDistrictIsInMostAffectedState) {

                for (District district : districtList) {
                    if (Constant.userSelectedDistrict.trim().equalsIgnoreCase(district.getName().trim())) {
                        mostAffectedDistricts.add(0, district);
                        break;
                    }
                }
            } else {

                // move user selected district at 0th index
                District temp = mostAffectedDistricts.get(userDistrictIsInMostAffectedStateIndex);
                mostAffectedDistricts.remove(userDistrictIsInMostAffectedStateIndex);
                mostAffectedDistricts.add(0, temp);

            }
        }

        return mostAffectedDistricts;
    }

    private void setTotalCasesTimelineChart(Constant.ChartType chartType,
                                            List<TimeSeriesData> timeSeriesList) {

        BarChart chart;
        ArrayList<String> xAxisList = new ArrayList<>(); // bar values of x axis
        ArrayList<BarEntry> values = new ArrayList<>(); // bar values of y axis; x will be in multiple of 1 harcoded

        for (TimeSeriesData timeSeriesData : timeSeriesList)
            xAxisList.add(timeSeriesData.getDate().trim());

        int tempCount = 0; // it must start with 0
        switch (chartType) {
            case TOTAL_CONFIRMED:
                chart = binding.totalConfirmedCasesTimelineChart;

                binding.totalConfirmedCasesTimelineTitleTv.setText("#Total Confirmed Cases in " + Constant.userSelectedCountry + " Timeline");

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getTotalConfirmed())));

                break;
            case TOTAL_DECEASED:
                chart = binding.totalDeathCasesTimelineChart;

                binding.totalDeathCasesTimelineTitleTv.setText("#Total Death Cases in " + Constant.userSelectedCountry + " Timeline");

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getTotalDeceased())));

                break;
            case TOTAL_RECOVERED:
                chart = binding.totalRecoveredCasesTimelineChart;

                binding.totalRecoveredCasesTimelineTitleTv.setText("#Total Recovered Cases in " + Constant.userSelectedCountry + " Timeline");

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getTotalRecovered())));

                break;
            case DAILY_CONFIRMED:
                chart = binding.dailyConfirmedCasesTimelineChart;

                binding.dailyConfirmedCasesTimelineTitleTv.setText("#Daily Confirmed Cases in " + Constant.userSelectedCountry + " Timeline");

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getDailyConfirmed())));

                break;
            case DAILY_DECEASED:
                chart = binding.dailyDeathCasesTimelineChart;

                binding.dailyDeathCasesTimelineTitleTv.setText("#Daily Death Cases in " + Constant.userSelectedCountry + " Timeline");

                for (TimeSeriesData timeSeriesData : timeSeriesList)
                    values.add(new BarEntry(tempCount++, Float.parseFloat(timeSeriesData.getDailyDeceased())));

                break;
            case DAILY_RECOVERED:
                chart = binding.dailyRecoveredCasesTimelineChart;

                binding.dailyRecoveredCasesTimelineTitleTv.setText("#Daily Recovered Cases in " + Constant.userSelectedCountry + " Timeline");

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
    }

    private void setChartConfig(BarChart chart, ArrayList<String> xAxisList) {
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);

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

        // Smooth Animation
        chart.animateY(1000);

        // Don't show BarDataSet Label
        chart.getLegend().setEnabled(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposables != null && !disposables.isDisposed())
            disposables.dispose();
    }

    public interface OnTrackFragmentListener {

        void onDetailListClicked(ListType listType, TimeSeriesStateWiseResponse timeSeriesStateWiseResponse, ArrayList<StateDistrictWiseResponse> stateDistrictList);

        void onDetailGraphClicked(Constant.ChartType chartType, TimeSeriesStateWiseResponse timeSeriesStateWiseResponse, ArrayList<StateDistrictWiseResponse> stateDistrictList);

    }
}
