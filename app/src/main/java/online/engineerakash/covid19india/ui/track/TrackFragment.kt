package online.engineerakash.covid19india.ui.track

import online.engineerakash.covid19india.R
import online.engineerakash.covid19india.databinding.FragmentTrackBinding
import online.engineerakash.covid19india.enums.ChartType
import online.engineerakash.covid19india.enums.ListType
import online.engineerakash.covid19india.enums.TotalOrDaily
import online.engineerakash.covid19india.pojo.District
import online.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import online.engineerakash.covid19india.pojo.TimeSeriesStateWiseResponse
import online.engineerakash.covid19india.ui.home.MainViewModel
import online.engineerakash.covid19india.ui.home.MainViewModelFactory
import online.engineerakash.covid19india.util.ChooseLocationStartedFrom
import online.engineerakash.covid19india.util.Constant
import online.engineerakash.covid19india.util.DateTimeUtil
import online.engineerakash.covid19india.util.Helper
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
import com.google.android.gms.ads.*
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "TrackFragment"

class TrackFragment : Fragment() {

    private lateinit var binding: FragmentTrackBinding

    private var timeSeriesStateWiseResponse: ArrayList<TimeSeriesStateWiseResponse> = arrayListOf()
    private var countryWideCasesTimeSeries: TimeSeriesStateWiseResponse =
        TimeSeriesStateWiseResponse(
            context?.getString(R.string.total) ?: "",
            Constant.TOTAL_ITEM_CODE
        )
    private var stateDistrictList: ArrayList<StateDistrictWiseResponse> = arrayListOf()
    private lateinit var navController: NavController

    private var totalAndDailyGraphList = arrayListOf<String>()
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

        totalAndDailyGraphList = arrayListOf<String>(
            context?.getString(R.string.daily) ?: "",
            context?.getString(R.string.total) ?: ""
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: MainViewModel by activityViewModels { MainViewModelFactory(activity!!.application) }

        getStateDistrictData(viewModel)
        getTimeSeriesData(viewModel)

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

        viewModel.stateDistrictListLoaderLiveData.observe(viewLifecycleOwner, {
            if (it || viewModel.timeSeriesStateWiseResponseLoaderLiveData.value == true) {
                binding.dataLayout.visibility = View.GONE
                binding.errorLayout.visibility = View.GONE

                binding.loaderLayout.visibility = View.VISIBLE
            } else {
                // error or data liveData will hide the loader
            }
        })
        viewModel.timeSeriesStateWiseResponseLoaderLiveData.observe(viewLifecycleOwner, {
            if (it || viewModel.stateDistrictListLoaderLiveData.value == true) {
                binding.dataLayout.visibility = View.GONE
                binding.errorLayout.visibility = View.GONE

                binding.loaderLayout.visibility = View.VISIBLE
            } else {
                // error or data liveData will hide the loader
            }
        })

        viewModel.stateDistrictListErrorLiveData.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                binding.dataLayout.visibility = View.GONE
                binding.loaderLayout.visibility = View.GONE

                binding.errorMessageTv.text = it
                binding.retryBtn.visibility = View.VISIBLE
                binding.errorLayout.visibility = View.VISIBLE
            }
        })

        viewModel.timeSeriesStateWiseResponseErrorLiveData.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                binding.dataLayout.visibility = View.GONE
                binding.loaderLayout.visibility = View.GONE

                binding.errorMessageTv.text = it
                binding.retryBtn.visibility = View.VISIBLE
                binding.errorLayout.visibility = View.VISIBLE
            }
        })


        setupClickListeners(viewModel)

        if (args.refreshData) {
            viewModel.fetchTimeSeriesAndStateWiseData()
            viewModel.fetchStateDistrictData()
        }

        if (Constant.SHOW_ADS)
            loadAds()
    }

    private fun getTimeSeriesData(viewModel: MainViewModel, forceUpdate: Boolean = false) {
        viewModel.getTimeSeriesStateWiseResponseLiveData(forceUpdate)
            .observe(viewLifecycleOwner, {

                timeSeriesStateWiseResponse = it

                if (
                    viewModel.stateDistrictListLoaderLiveData.value == false &&
                    viewModel.stateDistrictListErrorLiveData.value.isNullOrEmpty()
                ) {

                    binding.loaderLayout.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                    binding.dataLayout.visibility = View.VISIBLE
                }

                countryWideCasesTimeSeries =
                    getCountryWideCasesTimeSeries(timeSeriesStateWiseResponse)

                (totalAndDailyGraphFragmentList[binding.totalAndDailyViewPager.currentItem] as TotalAndDailyGraphFragment?)?.updateTimeSeriesDataList(
                    countryWideCasesTimeSeries.timeSeriesList,
                    true
                )

            })
    }

    private fun getStateDistrictData(viewModel: MainViewModel, forceUpdate: Boolean = false) {
        viewModel.getStateDistrictListLiveData(forceUpdate)
            .observe(viewLifecycleOwner, {

                if (
                    viewModel.timeSeriesStateWiseResponseLoaderLiveData.value == false &&
                    viewModel.timeSeriesStateWiseResponseErrorLiveData.value.isNullOrEmpty()
                ) {

                    binding.loaderLayout.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                    binding.dataLayout.visibility = View.VISIBLE
                }

                stateDistrictList.clear()
                stateDistrictList.addAll(it)
                fillMostAffectedDistrictSection(stateDistrictList)

                fillDashboard(stateDistrictList)
                fillMostAffectedStateSection(stateDistrictList)

            })
    }

    private fun setupClickListeners(viewModel: MainViewModel) {
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

        binding.retryBtn.setOnClickListener {
            getStateDistrictData(viewModel, true)
            getTimeSeriesData(viewModel, true)
        }
    }

    private fun fillDashboard(stateDistrictList: ArrayList<StateDistrictWiseResponse>) {
        val dashboardStats: StateDistrictWiseResponse? =
            Helper.getCurrentStateStats(stateDistrictList)
        binding.totalCasesInUserStateTitleTv.text =
            getString(R.string.cases_in_state, (dashboardStats?.name ?: "-"))
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
            getString(
                R.string.last_updated_date,
                (DateTimeUtil.parseMetaDateTimeToAppsDefaultDateTime(
                    dashboardStats?.meta?.lastUpdated ?: "-"
                ))
            )
    }

    private fun fillMostAffectedDistrictSection(stateDistrictList: ArrayList<StateDistrictWiseResponse>?) {
        val mostAffectedDistricts: ArrayList<District> =
            Helper.getMostAffectedDistricts(stateDistrictList)
        mostAffectedDistricts.addAll(Helper.getObjectTotalOfAffectedDistricts(stateDistrictList))
        binding.mostAffectedDistrictTitleTv.text =
            getString(
                R.string.most_affected_district_in_state,
                (Constant.userSelectedState.trim { it <= ' ' })
            )
        val districtWiseAdapter = DistrictWiseAdapter(mostAffectedDistricts, false)
        binding.mostAffectedDistrictRv.layoutManager = LinearLayoutManager(context)
        binding.mostAffectedDistrictRv.adapter = districtWiseAdapter
    }

    private fun fillMostAffectedStateSection(stateDistrictList: ArrayList<StateDistrictWiseResponse>) {
        val mostAffectedStateWiseList: ArrayList<StateDistrictWiseResponse> =
            Helper.getMostAffectedStates(stateDistrictList)
        mostAffectedStateWiseList
            .add(Helper.getObjectTotalOfAffectedState(stateDistrictList))
        binding.mostAffectedStateTitleTv.text =
            getString(R.string.most_affected_state_in_india, context?.getString(R.string.india)?.trim { it <= ' ' })
        val stateWiseAdapter = StateWiseAdapter(mostAffectedStateWiseList, false)
        binding.mostAffectedStateRv.layoutManager = LinearLayoutManager(context)
        binding.mostAffectedStateRv.adapter = stateWiseAdapter
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
            TimeSeriesStateWiseResponse(
                context?.getString(R.string.total) ?: "",
                Constant.TOTAL_ITEM_CODE
            )

        for (timeSeriesData in timeSeriesList) {
            if (
                timeSeriesData.code.equals(Constant.TOTAL_ITEM_CODE, true) ||
                timeSeriesData.name.equals(context?.getString(R.string.total), true)
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

    private fun loadAds() {
        context.let {
            MobileAds.initialize(it!!) {}

            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
            binding.adView2.loadAd(adRequest)
        }
    }

}