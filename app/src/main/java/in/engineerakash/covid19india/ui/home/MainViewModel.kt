package `in`.engineerakash.covid19india.ui.home

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.api.CovidClient
import `in`.engineerakash.covid19india.enums.ChartType
import `in`.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import `in`.engineerakash.covid19india.pojo.TimeSeriesStateWiseResponse
import `in`.engineerakash.covid19india.ui.CovidApplication
import `in`.engineerakash.covid19india.util.JsonExtractor
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "MainViewModel"

class MainViewModel(
    application: Application,
    var disposables: CompositeDisposable = CompositeDisposable()
) : AndroidViewModel(application) {

    private val stateDistrictListLiveData: MutableLiveData<ArrayList<StateDistrictWiseResponse>> by lazy {
        MutableLiveData<ArrayList<StateDistrictWiseResponse>>().also {
            fetchStateDistrictData()
        }
    }

    val stateDistrictListErrorLiveData = MutableLiveData<String>()
    val stateDistrictListLoaderLiveData = MutableLiveData<Boolean>()

    private val timeSeriesStateWiseResponseLiveData: MutableLiveData<ArrayList<TimeSeriesStateWiseResponse>> by lazy {
        MutableLiveData<ArrayList<TimeSeriesStateWiseResponse>>().also {
            fetchTimeSeriesAndStateWiseData()
        }
    }

    val timeSeriesStateWiseResponseErrorLiveData = MutableLiveData<String>()
    val timeSeriesStateWiseResponseLoaderLiveData = MutableLiveData<Boolean>()

    private val graphChartMoreClickLiveData = MutableLiveData<ChartType?>()


    fun getStateDistrictListLiveData(forceUpdate: Boolean = false): LiveData<ArrayList<StateDistrictWiseResponse>> {
        if (forceUpdate) {
            stateDistrictListErrorLiveData.value = ""
            fetchStateDistrictData()
        }
        return stateDistrictListLiveData
    }

    fun getTimeSeriesStateWiseResponseLiveData(forceUpdate: Boolean = false): LiveData<ArrayList<TimeSeriesStateWiseResponse>> {
        if (forceUpdate) {
            timeSeriesStateWiseResponseErrorLiveData.value = ""
            fetchTimeSeriesAndStateWiseData()
        }
        return timeSeriesStateWiseResponseLiveData
    }

    fun getGraphChartMoreClickLiveData(): LiveData<ChartType?> {
        return graphChartMoreClickLiveData
    }


    fun fetchStateDistrictData() {
        //todo save last fetched json, and show them if internet is not connected

        stateDistrictListLoaderLiveData.value = true
        CovidClient
            .instance
            .stateDistrictWiseData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                stateDistrictListLoaderLiveData.value = false
            }
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onSuccess(responseBodyResponse: ResponseBody) {
                    try {
                        val response = responseBodyResponse.string()
                        val stateDistrictList =
                            JsonExtractor.parseStateDistrictWiseResponseJson(response)

                        stateDistrictListLiveData.value = stateDistrictList

                    } catch (e: IOException) {
                        e.printStackTrace()
                        stateDistrictListErrorLiveData.value = e.localizedMessage
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    stateDistrictListErrorLiveData.value =
                        if (e is UnknownHostException || e is SocketTimeoutException)
                            getApplication<CovidApplication>().getString(R.string.please_check_internet)
                        else
                            e.localizedMessage
                }
            })
    }

    fun fetchTimeSeriesAndStateWiseData() {
        //todo save last fetched json, and show them if internet is not connected
        timeSeriesStateWiseResponseLoaderLiveData.value = true
        CovidClient
            .instance
            .timeSeriesAndStateWiseData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                timeSeriesStateWiseResponseLoaderLiveData.value = false
            }
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onSuccess(responseBodyResponse: ResponseBody) {
                    try {
                        val response = responseBodyResponse.string()
                        Log.d(TAG, "onSuccess: TimeSeriesAndStateWiseData Response: $response")

                        val timeSeriesStateWiseResponse =
                            JsonExtractor.parseTimeSeriesWiseResponseJson(response)

                        timeSeriesStateWiseResponseLiveData.value = timeSeriesStateWiseResponse

                    } catch (e: IOException) {
                        e.printStackTrace()
                        timeSeriesStateWiseResponseErrorLiveData.value = e.localizedMessage
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    timeSeriesStateWiseResponseErrorLiveData.value =
                        if (e is UnknownHostException || e is SocketTimeoutException)
                            getApplication<CovidApplication>().getString(R.string.please_check_internet)
                        else
                            e.localizedMessage
                }
            })
    }

    fun setGraphChartMoreClickLiveData(chartType: ChartType?) {
        graphChartMoreClickLiveData.value = chartType
    }

    override fun onCleared() {
        if (!disposables.isDisposed)
            disposables.dispose()
        super.onCleared()
    }


}