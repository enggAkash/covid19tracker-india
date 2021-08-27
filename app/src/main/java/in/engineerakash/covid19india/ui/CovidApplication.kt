package `in`.engineerakash.covid19india.ui

import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.Helper
import `in`.engineerakash.covid19india.util.NotificationHelper
import android.app.Application

class CovidApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Constant.userSelectedState = Helper.getSelectedState(this)
        Constant.userSelectedDistrict = Helper.getSelectedDistrict(this)

        NotificationHelper.createAllNotificationChannel(this)
        NotificationHelper.removeNotifications(this)
    }

}