package in.engineerakash.covid19india.ui.home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import in.engineerakash.covid19india.R;
import in.engineerakash.covid19india.databinding.ActivityMainBinding;
import in.engineerakash.covid19india.pojo.StateDistrictWiseResponse;
import in.engineerakash.covid19india.pojo.TimeSeriesStateWiseResponse;
import in.engineerakash.covid19india.ui.detailgraph.DetailGraphFragment;
import in.engineerakash.covid19india.ui.detaillist.DetailListFragment;
import in.engineerakash.covid19india.ui.precaution.PrecautionFragment;
import in.engineerakash.covid19india.ui.settings.SettingsFragment;
import in.engineerakash.covid19india.ui.track.TrackFragment;
import in.engineerakash.covid19india.util.Constant;

public class MainActivity extends AppCompatActivity implements TrackFragment.OnTrackFragmentListener {
    private static final String TAG = "MainActivity";
    public final String TRACK_FRAG_TAG = "track_frag_tag";
    public final String PRECAUTION_FRAG_TAG = "precaution_frag_tag";
    public final String SETTINGS_FRAG_TAG = "settings_frag_tag";
    public final String DETAIL_GRAPH_FRAG_TAG = "detail_graph_frag_tag";
    public final String DETAIL_LIST_FRAG_TAG = "detail_list_frag_tag";
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private Fragment activeFragment;

    private TrackFragment trackFragment;
    private DetailGraphFragment detailGraphFragment;
    private DetailListFragment detailListFragment;

    private PrecautionFragment precautionFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initComponent();

    }

    private void initComponent() {

        fragmentManager = getSupportFragmentManager();

//        setUpNavigation();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_track:
                    showTrackFragment(true);
                    return true;

                case R.id.menu_precaution:
                    showPrecautionFragment();
                    return true;

                case R.id.menu_settings:
                    showSettingsFragment();
                    return true;
            }
            return false;
        });

        showTrackFragment(false);


    }

    private void showTrackFragment(boolean removeChildFragment) {

        if (trackFragment == null)
            trackFragment = TrackFragment.newInstance();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, trackFragment)
                .commit();

        activeFragment = trackFragment;

        if (removeChildFragment) {
            Fragment tempListFrag = fragmentManager.findFragmentByTag(DETAIL_LIST_FRAG_TAG);
            Fragment tempGraphFrag = fragmentManager.findFragmentByTag(DETAIL_GRAPH_FRAG_TAG);

            if (tempListFrag != null) {
                fragmentManager
                        .beginTransaction()
                        .remove(tempListFrag)
                        .commit();
            }
            if (tempGraphFrag != null) {
                fragmentManager
                        .beginTransaction()
                        .remove(tempGraphFrag)
                        .commit();
            }
        }
    }

    private void showDetailListFragment(DetailListFragment.ListType listType,
                                        TimeSeriesStateWiseResponse timeSeriesStateWiseResponse,
                                        ArrayList<StateDistrictWiseResponse> stateDistrictList) {

        if (detailListFragment == null)
            detailListFragment = DetailListFragment.newInstance(listType, timeSeriesStateWiseResponse, stateDistrictList);

        //TODO pass data

        detailListFragment.setListType(listType);

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, detailListFragment, DETAIL_LIST_FRAG_TAG)
                .addToBackStack(null)
                .commit();

        activeFragment = detailListFragment;

    }

    private void showDetailGraphFragment(Constant.ChartType chartType, TimeSeriesStateWiseResponse timeSeriesStateWiseResponse,
                                         ArrayList<StateDistrictWiseResponse> stateDistrictList) {

        /*if (activeFragment != null)
            fragmentManager
                    .beginTransaction()
                    .hide(activeFragment)
                    .commit();*/

        detailGraphFragment = DetailGraphFragment.newInstance(chartType, timeSeriesStateWiseResponse);

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, detailGraphFragment, DETAIL_GRAPH_FRAG_TAG)
                .commit();

        activeFragment = detailGraphFragment;

    }

    public void changeThemeColor(boolean defaultColor, int color) {

        int statusBarColor;
        int actionBarColor;
        int bottomNavColor;

        if (defaultColor) {
            statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            actionBarColor = ContextCompat.getColor(this, R.color.colorPrimary);
            bottomNavColor = ContextCompat.getColor(this, R.color.colorPrimary);
        } else {
            statusBarColor = actionBarColor = bottomNavColor = color;
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusBarColor);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setBackgroundDrawable(new ColorDrawable(actionBarColor));

        binding.bottomNavigation.setBackgroundColor(bottomNavColor);
    }

    private void showPrecautionFragment() {

        /*if (activeFragment != null)
            fragmentManager
                    .beginTransaction()
                    .hide(activeFragment)
                    .commit();*/

        if (precautionFragment == null)
            precautionFragment = PrecautionFragment.newInstance();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, precautionFragment, PRECAUTION_FRAG_TAG)
                .addToBackStack(null)
                .commit();

        activeFragment = precautionFragment;

    }

    private void showSettingsFragment() {

        /*if (activeFragment != null)
            fragmentManager
                    .beginTransaction()
                    .hide(activeFragment)
                    .commit();*/

        if (settingsFragment == null)
            settingsFragment = SettingsFragment.newInstance();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, settingsFragment, SETTINGS_FRAG_TAG)
                .addToBackStack(null)
                .commit();

        activeFragment = settingsFragment;

    }

    @Override
    public void onDetailListClicked(DetailListFragment.ListType listType,
                                    TimeSeriesStateWiseResponse timeSeriesStateWiseResponse,
                                    ArrayList<StateDistrictWiseResponse> stateDistrictList) {
        showDetailListFragment(listType, timeSeriesStateWiseResponse, stateDistrictList);
    }

    @Override
    public void onDetailGraphClicked(Constant.ChartType chartType,
                                     TimeSeriesStateWiseResponse timeSeriesStateWiseResponse,
                                     ArrayList<StateDistrictWiseResponse> stateDistrictList) {
        showDetailGraphFragment(chartType, timeSeriesStateWiseResponse, stateDistrictList);
    }

    /*@Override
    public boolean navigateUpTo(Intent upIntent) {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }*/

    /*@Override
    public void onNavigated(@NonNull NavController controller,
                            @NonNull NavDestination destination) {
        ActionBar actionBar = getSupportActionBar();
        CharSequence title = destination.getLabel();
        if (!TextUtils.isEmpty(title)) {
            actionBar.setTitle(title);
        }
        boolean isStartDestination =
                controller.getGraph().getStartDestination() == destination.getId();
        actionBar.setDisplayHomeAsUpEnabled(mDrawerLayout != null || !isStartDestination);
        setActionBarUpIndicator(mDrawerLayout != null && isStartDestination);
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {

                if (activeFragment instanceof DetailListFragment || activeFragment instanceof DetailGraphFragment) {
                    showTrackFragment(true);
                } else {
                    super.onBackPressed();
                }

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (activeFragment instanceof DetailListFragment || activeFragment instanceof DetailGraphFragment) {
            showTrackFragment(true);
        } else {
            super.onBackPressed();
        }
    }
}
