package `in`.engineerakash.covid19india.util

import `in`.engineerakash.covid19india.util.Helper.isNetworkAvailable
import `in`.engineerakash.covid19india.util.ObservableObject.Companion.instance
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