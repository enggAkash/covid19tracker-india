package in.engineerakash.covid19india.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Helper.isNetworkAvailable(context))
            intent.putExtra("connected", true);
        else
            intent.putExtra("connected", false);

        ObservableObject.getInstance().updateValue(intent);
    }
}
