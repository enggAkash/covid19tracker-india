package online.engineerakash.covid19india.ui

import online.engineerakash.covid19india.ui.bg_process.CovidWorkManagerUtil
import online.engineerakash.covid19india.util.Constant
import online.engineerakash.covid19india.util.CovidReportFrequency
import online.engineerakash.covid19india.util.Helper
import online.engineerakash.covid19india.util.NotificationHelper
import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.gson.JsonParseException
import org.json.JSONArray

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


        val stateJsonFile = Helper.readAssetFile(this, Constant.stateJsonAssetName)

        try {
            val rootJa = JSONArray(stateJsonFile)

            Constant.stateCodeNameMap.clear()
            for (i in 0 until rootJa.length()) {
                val stateJo = rootJa.getJSONObject(i)
                Constant.stateCodeNameMap[stateJo.getString("code")] = stateJo.getString("name")
            }
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        //todo implement it via jetpack's startup library
        MobileAds.initialize(this, object : OnInitializationCompleteListener {
            override fun onInitializationComplete(initializationStatus: InitializationStatus?) {}
        })

    }


}