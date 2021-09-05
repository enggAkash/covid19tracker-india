package `in`.engineerakash.covid19india.ui.bg_process

import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.Helper
import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "CovidWorkManagerUtil"

object CovidWorkManagerUtil {

    fun createPeriodicTask(context: Context?) {
        Log.d(TAG, "createPeriodicTask: ")

        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val totalHourInDay = 24
        val thisTaskShouldKickInAtThisHour = Constant.COVID_REPORT_HOUR
        val thisTaskShouldKickInAtThisMinute = Constant.COVID_REPORT_MINUTE

        val totalMinutesInAnHour = 60

        val initialDelayHour = if (currentHour > thisTaskShouldKickInAtThisHour)
            totalHourInDay - currentHour + thisTaskShouldKickInAtThisHour
        else
            thisTaskShouldKickInAtThisHour - currentHour
        val initialDelayMinute = if (currentMinute > thisTaskShouldKickInAtThisMinute)
            totalMinutesInAnHour - currentMinute + thisTaskShouldKickInAtThisMinute
        else
            thisTaskShouldKickInAtThisMinute - currentMinute

        val totalInitialDelayInMinute = (initialDelayHour * 60) + initialDelayMinute

        val fetchReportConstraints =
            Constraints
                .Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        Log.d(
            TAG,
            "createPeriodicTask: initial delay in minute: $totalInitialDelayInMinute"
        )

        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<LatestReportWorker>(24, TimeUnit.HOURS)
                .setConstraints(fetchReportConstraints)
                .setInitialDelay(totalInitialDelayInMinute.toLong(), TimeUnit.MINUTES)
                .build()

        val id = periodicWorkRequest.id.toString()
        context?.let { Helper.savePeriodicWorkTag(it, id) }

        context?.let { WorkManager.getInstance(it).enqueue(periodicWorkRequest) }
    }

    fun cancelPeriodicTask(context: Context?, lastPeriodicWorkTag: String) {
        context?.let {
            WorkManager.getInstance(it)
                .cancelAllWorkByTag(lastPeriodicWorkTag)
        }
    }

}