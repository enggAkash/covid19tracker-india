package `in`.engineerakash.covid19india.ui

import `in`.engineerakash.covid19india.ui.bg_process.CovidWorkManagerUtil
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.CovidReportFrequency
import `in`.engineerakash.covid19india.util.Helper
import `in`.engineerakash.covid19india.util.NotificationHelper
import android.app.Application
import android.util.Log

private const val TAG = "CovidApplication"
class CovidApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Constant.userSelectedState = Helper.getSelectedState(this)
        Constant.userSelectedDistrict = Helper.getSelectedDistrict(this)

        NotificationHelper.createAllNotificationChannel(this)
        NotificationHelper.removeNotifications(this)

        /*If there is no Report Frequency set yet, make it Daily*/
        if (Helper.getReportFrequency(this).isEmpty()) {
            Log.d(TAG, "onCreate: Creating periodic task from application class")
            Helper.saveReportFrequency(this, CovidReportFrequency.DAILY)

            CovidWorkManagerUtil.createPeriodicTask(this)
        }
    }

}