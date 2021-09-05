package `in`.engineerakash.covid19india.ui.bg_process

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.util.NotificationHelper
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "LatestReportWorker"
class LatestReportWorker(var context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        Log.d(TAG, "doWork: ")
        fetchLatestReport()

        return Result.success()
    }

    private fun fetchLatestReport() {
        Log.d(TAG, "fetchLatestReport: ")
        //todo network calling to fetch data
        // sort data
        val casesInDistrict = 432
        val casesInState = 43143
        val casesInIndia = 1237204

        createNotification(context, casesInDistrict, casesInState, casesInIndia)
    }

    private fun createNotification(
        context: Context, casesInDistrict: Int, casesInState: Int, casesInIndia: Int
    ) {
        Log.d(TAG, "createNotification: ")
        val notificationText = context.getString(R.string.covid_report, casesInDistrict, casesInState, casesInIndia)

        NotificationHelper.createNotification(
            context,
            "covid_report_id", "Daily Covid Report",
            notificationText, R.mipmap.ic_launcher_rectangular
        )
    }

}