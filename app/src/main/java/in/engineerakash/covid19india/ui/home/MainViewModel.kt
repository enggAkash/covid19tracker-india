package `in`.engineerakash.covid19india.ui.home

import `in`.engineerakash.covid19india.api.CovidClient
import `in`.engineerakash.covid19india.enums.ChartType
import `in`.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import `in`.engineerakash.covid19india.pojo.TimeSeriesStateWiseResponse
import `in`.engineerakash.covid19india.util.JsonExtractor
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.IOException

private const val TAG = "MainViewModel"
class MainViewModel(
    application: Application,
    var disposables: CompositeDisposable = CompositeDisposable()
) : AndroidViewModel(application) {

    private val stateDistrictListLiveData: MutableLiveData<ArrayList<StateDistrictWiseResponse>> by lazy {
        MutableLiveData<ArrayList<StateDistrictWiseResponse>>().also {
            //todo check for internet connection
            fetchTimeSeriesAndStateWiseData()
        }
    }

    private val timeSeriesStateWiseResponseLiveData: MutableLiveData<TimeSeriesStateWiseResponse> by lazy {
        MutableLiveData<TimeSeriesStateWiseResponse>().also {
            //todo check for internet connection
            fetchStateDistrictData()
        }
    }

    private val graphChartMoreClickLiveData = MutableLiveData<ChartType?>()


    fun getStateDistrictListLiveData(): LiveData<ArrayList<StateDistrictWiseResponse>> {
        return stateDistrictListLiveData
    }

    fun getTimeSeriesStateWiseResponseLiveData(): LiveData<TimeSeriesStateWiseResponse> {
        return timeSeriesStateWiseResponseLiveData
    }

    fun getGraphChartMoreClickLiveData(): LiveData<ChartType?> {
        return graphChartMoreClickLiveData
    }


    private fun fetchStateDistrictData() {
        //todo save last fetched json, and show them if internet is not connected
        CovidClient
            .instance
            .stateDistrictWiseData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onSuccess(responseBodyResponse: ResponseBody) {
                    try {
                        val response = responseBodyResponse.string()
                        val stateDistrictList =
                            JsonExtractor.parseStateDistrictWiseResponseJson(response)

                        stateDistrictListLiveData.postValue(stateDistrictList)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    private fun fetchTimeSeriesAndStateWiseData() {
        //todo save last fetched json, and show them if internet is not connected
        CovidClient
            .instance
            .timeSeriesAndStateWiseData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onSuccess(responseBodyResponse: ResponseBody) {
                    try {
                        val response = responseBodyResponse.string()
                        Log.d(TAG, "onSuccess: TimeSeriesAndStateWiseData Response: $response")

                        val timeSeriesStateWiseResponse =
                            Gson().fromJson<TimeSeriesStateWiseResponse>(
                                response,
                                TimeSeriesStateWiseResponse::class.java
                            )

                        timeSeriesStateWiseResponseLiveData.postValue(timeSeriesStateWiseResponse)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    fun setGraphChartMoreClickLiveData(chartType: ChartType?) {
        graphChartMoreClickLiveData.postValue(chartType)
    }

    override fun onCleared() {
        if (!disposables.isDisposed)
            disposables.dispose()
        super.onCleared()
    }
}