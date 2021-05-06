package in.engineerakash.covid19india.ui.detaillist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import in.engineerakash.covid19india.R;
import in.engineerakash.covid19india.databinding.FragmentDetailListBinding;
import in.engineerakash.covid19india.enums.ListType;
import in.engineerakash.covid19india.pojo.District;
import in.engineerakash.covid19india.pojo.DistrictDelta;
import in.engineerakash.covid19india.pojo.StateDistrictWiseResponse;
import in.engineerakash.covid19india.pojo.StateWiseData;
import in.engineerakash.covid19india.pojo.TimeSeriesStateWiseResponse;
import in.engineerakash.covid19india.ui.home.DistrictWiseAdapter;
import in.engineerakash.covid19india.ui.home.StateWiseAdapter;
import in.engineerakash.covid19india.util.Constant;

public class DetailListFragment extends Fragment {

    private static final String LIST_TYPE_PARAM = "list_type";
    private static final String TIME_SERIES_STATE_WISE_PARAM = "time_series_state_wise_param";
    private static final String STATE_DISTRICT_LIST_PARAM = "state_district_list_param";

    private ListType currentListType;
    private TimeSeriesStateWiseResponse timeSeriesStateWiseResponse;
    private ArrayList<StateDistrictWiseResponse> stateDistrictList;
    private FragmentDetailListBinding binding;
    private View root;

    public DetailListFragment() {
    }

    public static DetailListFragment newInstance(ListType listType,
                                                 TimeSeriesStateWiseResponse timeSeriesStateWiseResponse,
                                                 ArrayList<StateDistrictWiseResponse> stateDistrictList) {
        DetailListFragment fragment = new DetailListFragment();
        Bundle args = new Bundle();
        args.putSerializable(LIST_TYPE_PARAM, listType);
        args.putParcelable(TIME_SERIES_STATE_WISE_PARAM, timeSeriesStateWiseResponse);
        args.putParcelableArrayList(STATE_DISTRICT_LIST_PARAM, stateDistrictList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DetailListFragmentArgs detailListFragmentArgs = DetailListFragmentArgs.fromBundle(getArguments());

        currentListType = detailListFragmentArgs.getListType();
        timeSeriesStateWiseResponse = detailListFragmentArgs.getTimeSeriesDateWiseResponse();
        stateDistrictList = new ArrayList<StateDistrictWiseResponse>(Arrays.asList(detailListFragmentArgs.getStateDistrictWiseResponse()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailListBinding.inflate(inflater, container, false);

        root = binding.getRoot();

        initComponent(root);

        return root;
    }

    private void initComponent(View root) {
        if (((AppCompatActivity) getActivity()) != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if (currentListType == ListType.DISTRICT) {
            root.findViewById(R.id.districtHeader).setVisibility(View.VISIBLE);
            root.findViewById(R.id.stateHeader).setVisibility(View.GONE);

            ArrayList<District> sortedAffectedDistricts = getSortedAffectedDistricts(stateDistrictList);

            sortedAffectedDistricts.addAll(getObjectTotalOfAffectedDistricts(stateDistrictList));

            DistrictWiseAdapter districtWiseAdapter = new DistrictWiseAdapter(sortedAffectedDistricts);
            binding.mostAffectedDistrictAndStateRv.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.mostAffectedDistrictAndStateRv.setAdapter(districtWiseAdapter);

        } else if (currentListType == ListType.STATE) {

            root.findViewById(R.id.districtHeader).setVisibility(View.GONE);
            root.findViewById(R.id.stateHeader).setVisibility(View.VISIBLE);

            ArrayList<StateWiseData> sortedAffectedStateWiseList =
                    getSortedAffectedStates(timeSeriesStateWiseResponse.getStateWiseDataArrayList());

            sortedAffectedStateWiseList
                    .add(getObjectTotalOfAffectedState(timeSeriesStateWiseResponse.getStateWiseDataArrayList()));

            StateWiseAdapter stateWiseAdapter = new StateWiseAdapter(sortedAffectedStateWiseList);
            binding.mostAffectedDistrictAndStateRv.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.mostAffectedDistrictAndStateRv.setAdapter(stateWiseAdapter);

        }

        if (currentListType == ListType.DISTRICT) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Constant.userSelectedState.trim() + "'s");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Most affected Districts");
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Constant.userSelectedCountry.trim() + "'s");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Most affected States");
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (((AppCompatActivity) getActivity()) != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
        }
    }

    private ArrayList<District> getSortedAffectedDistricts(ArrayList<StateDistrictWiseResponse> stateWiseDataArrayList) {

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

        ArrayList<District> mostAffectedDistricts = new ArrayList<>(districtList);

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

    private ArrayList<StateWiseData> getSortedAffectedStates(ArrayList<StateWiseData> stateWiseDataArrayList) {

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

        ArrayList<StateWiseData> mostAffectedStates = new ArrayList<>(stateWiseDataArrayList);

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

    public void setListType(ListType listType) {
        currentListType = listType;
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Navigation.findNavController(binding.getRoot()).navigateUp();
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}