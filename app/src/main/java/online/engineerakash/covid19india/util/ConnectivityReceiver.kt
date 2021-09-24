package online.engineerakash.covid19india.util

import online.engineerakash.covid19india.util.Helper.isNetworkAvailable
import online.engineerakash.covid19india.util.ObservableObject.Companion.instance
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (isNetworkAvailable(context)) intent.putExtra(
            "connected",
            true
        ) else intent.putExtra("connected", false)
        instance.updateValue(intent)
    }

}