package online.engineerakash.covid19india.ui.bg_process

import online.engineerakash.covid19india.R
import online.engineerakash.covid19india.api.CovidClient
import online.engineerakash.covid19india.pojo.District
import online.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import online.engineerakash.covid19india.util.Constant
import online.engineerakash.covid19india.util.Helper
import online.engineerakash.covid19india.util.JsonExtractor
import online.engineerakash.covid19india.util.NotificationHelper
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "LatestReportWorker"
class LatestReportWorker(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    var result: Result = Result.success()

    override fun doWork(): Result {
        Log.d(TAG, "doWork: ")

        fetchLatestReport()

        return result
    }

    private fun fetchLatestReport() {
        Log.d(TAG, "fetchLatestReport: ")

        fetchStateDistrictData(context)
    }

    private fun fetchStateDistrictData(context: Context) {
        Log.d(TAG, "fetchStateDistrictData: ")

        var disposable : Disposable? = null

        //todo save last fetched json, and show them if internet is not connected
        CovidClient
            .instance
            .stateDistrictWiseData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                    Log.d(TAG, "onSubscribe: ")
                }

                override fun onSuccess(responseBodyResponse: ResponseBody) {
                    Log.d(TAG, "onSuccess: ")
                    try {
                        val response = responseBodyResponse.string()
                        val stateDistrictList =
                            JsonExtractor.parseStateDistrictWiseResponseJson(response)

                        val mostAffectedDistricts: ArrayList<District> =
                            Helper.getMostAffectedDistricts(stateDistrictList)
                        val dashboardStats: StateDistrictWiseResponse? =
                            Helper.getCurrentStateStats(stateDistrictList)

                        val confirmedCasesInDistrict = getDistrictData(mostAffectedDistricts)
                        val confirmedCasesInState = dashboardStats?.delta?.confirmed ?: 0
                        val confirmedCasesInIndia =
                            Helper.getObjectTotalOfAffectedState(stateDistrictList).delta?.confirmed
                                ?: 0

                        createNotification(
                            context,
                            confirmedCasesInDistrict,
                            confirmedCasesInState,
                            confirmedCasesInIndia
                        )

                        result = Result.success()

                    } catch (e: IOException) {
                        e.printStackTrace()
                        result = Result.failure()
                        Log.d(TAG, "onSuccess: Exception " + e.message)
                    }


                    if (disposable?.isDisposed == false)
                        disposable?.dispose()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Log.d(TAG, "onError: " + e.message)

                    if (e is HttpException)
                        result = Result.retry()

                    if (disposable?.isDisposed == false)
                        disposable?.dispose()
                }
            })
    }

    private fun getDistrictData(mostAffectedDistricts: ArrayList<District>): Int {
        Log.d(TAG, "getDistrictData: ")
        val filter = mostAffectedDistricts.filter {
            it.name.equals(Constant.userSelectedDistrict, false)
        }

        return if (filter.isNullOrEmpty())
            0
        else {
            filter[0].delta?.confirmed ?: 0
        }
    }

    private fun createNotification(
        context: Context, casesInDistrict: Int, casesInState: Int, casesInIndia: Int
    ) {

        val notificationText =
            if (Constant.userSelectedDistrict.isNotEmpty() && Constant.userSelectedState.isNotEmpty()) {
                context.getString(
                    R.string.covid_report, casesInDistrict, Constant.userSelectedDistrict,
                    casesInState, Constant.userSelectedState, casesInIndia,
                    context.getString(R.string.india)
                )
            } else {
                context.getString(
                    R.string.covid_report_india_only, casesInIndia, context.getString(R.string.india)
                )
            }

        Log.d(TAG, "createNotification: $notificationText")

        NotificationHelper.createNotification(
            context,
            "covid_report_id", context.getString(R.string.daily_covid_report_notif_title),
            notificationText, R.mipmap.ic_launcher_rectangular, notificationText
        )
    }

}