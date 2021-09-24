package online.engineerakash.covid19india.util

import online.engineerakash.covid19india.R
import online.engineerakash.covid19india.enums.ChartType
import online.engineerakash.covid19india.pojo.*
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.URISyntaxException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Helper {
    private const val TAG = "Helper"
    private var progressDialog: AlertDialog? = null

    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun waSupport(context: Context?) {
        /* String phone = context.getString(R.string.wa_support_number);
        String message = context.getString(R.string.wa_support_default_msg);

        if (AppUtil.appInstalledOrNot(context, "com.whatsapp")) {
            //whatsapp is installed

            String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + message;
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(i);
        } else {

            //whatsapp is not installed, Send SMS
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
            intent.putExtra("sms_body", message);
            context.startActivity(intent);

        }*/
    }

    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null &&
                connectivityManager.activeNetworkInfo!!.isConnected
    }

    fun encodeToBase64(data: String): String {
        val bytes = data.toByteArray(StandardCharsets.UTF_8)
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decodeFromBase64(data: String): String {
        var decodedStr = ""
        try {
            val bytes = Base64.decode(data.toByteArray(), Base64.DEFAULT)
            decodedStr = String(bytes, StandardCharsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return decodedStr
    }

    @JvmOverloads
    fun showErr(
        context: Context?,
        err: String? = context?.getString(R.string.something_went_wrong)
    ) {
        if (context != null) Toast.makeText(context, err, Toast.LENGTH_LONG).show()
    }

    fun showSuccess(context: Context?, msg: String?) {
        if (context != null) Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    @JvmOverloads
    fun showProgressDialog(
        context: Context?, text: String? = null, isCancelable: Boolean = false,
        autoCloseAfterInMilli: Int = 0
    ) {
        /*if (context == null)
            return;
        if (progressDialog == null)
            progressDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.ProgressDialogTheme)).create();

        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, null);
        if (!TextUtils.isEmpty(text))
            ((TextView) dialogLayout.findViewById(R.id.progress_dialog_text_view)).setText(text);
        progressDialog.setCancelable(isCancelable);
        progressDialog.setView(dialogLayout);
        progressDialog.show();

        if (autoCloseAfterInMilli > 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, autoCloseAfterInMilli);*/
    }

    fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }

    fun getTVal(context: Context?): String {
        var tval = ""
        if (context != null) {
            val sp = context.getSharedPreferences("client_sp", Context.MODE_PRIVATE)
            tval = sp.getString("tval", "").toString()
        }
        return tval
    }

    fun showMsg(context: Context?, msg: String?) {
        if (context == null) return
        Toast
            .makeText(context, msg, Toast.LENGTH_LONG)
            .show()
    }

    fun showNotInternetMsg(context: Context?) {
        showMsg(context, context?.getString(R.string.internet_not_available))
    }

    fun appInstalledOrNot(context: Context, uri: String?): Boolean {
        val pm = context.packageManager
        val app_installed: Boolean
        app_installed = try {
            pm.getPackageInfo(uri!!, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    @JvmOverloads
    fun whatsapp(context: Context, msg: String?, mobile: String = "") {
        if (!appInstalledOrNot(context, "com.whatsapp")) {
            showMsg(context, context.getString(R.string.whatsapp_not_installed))
            return
        }
        val formattedNumber = mobile.replace("[\\s+-]".toRegex(), "")
        try {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
            if (!TextUtils.isEmpty(formattedNumber)) {
                sendIntent.putExtra("jid", "$formattedNumber@s.whatsapp.net")
                //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            }
            sendIntent.setPackage("com.whatsapp")

            try {
                context.startActivity(sendIntent)
            } catch (e: ActivityNotFoundException) {
                showMsg(context, context.getString(R.string.failed_to_send_whatsapp))
            }
        } catch (e: Exception) {
            showMsg(context, context.getString(R.string.failed_to_send_whatsapp))
        }
    }

    fun handlePhoneCallLink(context: Context, url: String?) {
        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.parse(url)
        )
        context.startActivity(intent)
    }

    fun handleMailToLink(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_SENDTO)

        // For only email app handle this intent
        intent.data = Uri.parse("mailto:")

        // Extract the email address from mailto url
        val to = url.split("[:?]".toRegex()).toTypedArray()[1]
        if (!TextUtils.isEmpty(to)) {
            val toArray = to.split(";".toRegex()).toTypedArray()
            // Put the primary email addresses array into intent
            intent.putExtra(Intent.EXTRA_EMAIL, toArray)
        }

        // Extract the cc
        if (url.contains("cc=")) {
            var cc = url.split("cc=".toRegex()).toTypedArray()[1]
            if (!TextUtils.isEmpty(cc)) {
                //cc = cc.split("&")[0];
                cc = cc.split("&".toRegex()).toTypedArray()[0]
                val ccArray = cc.split(";".toRegex()).toTypedArray()
                // Put the cc email addresses array into intent
                intent.putExtra(Intent.EXTRA_CC, ccArray)
            }
        }

        // Extract the bcc
        if (url.contains("bcc=")) {
            var bcc = url.split("bcc=".toRegex()).toTypedArray()[1]
            if (!TextUtils.isEmpty(bcc)) {
                //cc = cc.split("&")[0];
                bcc = bcc.split("&".toRegex()).toTypedArray()[0]
                val bccArray = bcc.split(";".toRegex()).toTypedArray()
                // Put the bcc email addresses array into intent
                intent.putExtra(Intent.EXTRA_BCC, bccArray)
            }
        }

        // Extract the subject
        if (url.contains("subject=")) {
            var subject = url.split("subject=".toRegex()).toTypedArray()[1]
            if (!TextUtils.isEmpty(subject)) {
                subject = subject.split("&".toRegex()).toTypedArray()[0]
                // Encode the subject
                try {
                    subject = URLDecoder.decode(subject, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                // Put the mail subject into intent
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            }
        }

        // Extract the body
        if (url.contains("body=")) {
            var body = url.split("body=".toRegex()).toTypedArray()[1]
            if (!TextUtils.isEmpty(body)) {
                body = body.split("&".toRegex()).toTypedArray()[0]
                // Encode the body text
                try {
                    body = URLDecoder.decode(body, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                // Put the mail body into intent
                intent.putExtra(Intent.EXTRA_TEXT, body)
            }
        }

        // Email address not null or empty
        if (!TextUtils.isEmpty(to)) {
            try {
                // Finally, open the mail client activity
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, context.getString(R.string.no_email_app_found), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleWhatsapp(context: Context, url: String) {
        Log.d(TAG, "handleWhatsapp: $url")
        try {
            val intent: Intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.app_not_installed),
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (use: URISyntaxException) {
            Log.e(TAG, use.message!!)
        }
    }

    /**
     * Input : 15/04/2020 01:10:24<br></br>
     * Output: 15 Apr 2020 1:10 AM
     */
    @JvmStatic
    fun parseDate(inputTimeStamp: String?): String? {
        var outputTimeStamp = inputTimeStamp
        val inputDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("d MMM yyyy h:mm a", Locale.getDefault())
        try {
            val inputDateParsed = inputDateFormat.parse(inputTimeStamp.toString())
            if (inputDateParsed != null) outputTimeStamp = outputDateFormat.format(inputDateParsed)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputTimeStamp
    }

    @JvmStatic
    fun getBarChartColor(context: Context?, chartType: ChartType?): Int {
        return when (chartType) {
            ChartType.TOTAL_CONFIRMED, ChartType.DAILY_CONFIRMED -> ContextCompat.getColor(
                context!!, R.color.confirmedCasesColor
            )
            ChartType.TOTAL_RECOVERED, ChartType.DAILY_RECOVERED -> ContextCompat.getColor(
                context!!,
                R.color.recoveredCasesColor
            )
            ChartType.TOTAL_DECEASED, ChartType.DAILY_DECEASED -> ContextCompat.getColor(
                context!!,
                R.color.deathCasesColor
            )
            else -> ContextCompat.getColor(context!!, R.color.colorAccent)
        }
    }

    @JvmStatic
    fun readAssetFile(context: Context?, assetName: String): String {
        val stateJsonIs: InputStream? = context?.assets?.open(assetName)

        val buffer = ByteArray(stateJsonIs?.available() ?: 0)
        stateJsonIs?.read(buffer)
        stateJsonIs?.close()

        return String(buffer)
    }

    fun openUrl(context: Context?, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        try {
            context?.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, context?.getString(R.string.app_not_installed), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun setSelectedState(context: Context?, state: String) {
        context
            ?.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(Constant.SELECTED_STATE_SP_KEY, state)
            ?.apply()

        Constant.userSelectedState = state
    }

    fun setSelectedDistrict(context: Context?, district: String) {
        context
            ?.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(Constant.SELECTED_DISTRICT_SP_KEY, district)
            ?.apply()

        Constant.userSelectedDistrict = district
    }

    fun getSelectedState(context: Context): String {
        return context
            .getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .getString(Constant.SELECTED_STATE_SP_KEY, "") ?: ""
    }

    fun getSelectedDistrict(context: Context): String {
        return context
            .getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            .getString(Constant.SELECTED_DISTRICT_SP_KEY, "") ?: ""
    }

    fun saveNotificationId(context: Context?, notificationId: Int) {
        context
            ?.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putInt(Constant.NOTIFICATION_ID_SP_KEY, notificationId)
            ?.apply()
    }

    fun getNotificationId(context: Context?): Int {
        return context
            ?.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            ?.getInt(Constant.NOTIFICATION_ID_SP_KEY, 0) ?: 0
    }

    fun savePeriodicWorkTag(context: Context?, periodicTaskId: String) {
        context
            ?.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(Constant.PERIODIC_TASK_TAG_SP_KEY, periodicTaskId)
            ?.apply()
    }

    fun getPeriodicWorkTag(context: Context?): String {
        return context
            ?.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            ?.getString(Constant.PERIODIC_TASK_TAG_SP_KEY, "") ?: ""
    }

    fun saveReportFrequency(context: Context?, frequency: String) {
        context
            ?.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(Constant.REPORT_FREQUENCY_SP_KEY, frequency)
            ?.apply()
    }

    fun getReportFrequency(context: Context?): String {
        return context
            ?.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            ?.getString(Constant.REPORT_FREQUENCY_SP_KEY, "") ?: ""
    }

    fun getMapImageResource(stateName: String?): Int {
        return when (stateName) {
            "Andaman and Nicobar Islands" -> R.drawable.andman
            "Andhra Pradesh" -> R.drawable.andhra
            "Arunachal Pradesh" -> R.drawable.arunachal
            "Assam" -> R.drawable.assam
            "Bihar" -> R.drawable.bihar
            "Chandigarh" -> R.drawable.chandigarh
            "Chhattisgarh" -> R.drawable.chhatisgarh
            "Dadra and Nagar Haveli" -> R.drawable.dadra_nagar_haveli
            "Daman and Diu" -> R.drawable.daman_and_diu
            "Delhi" -> R.drawable.delhi
            "Goa" -> R.drawable.goa
            "Gujarat" -> R.drawable.gujarat
            "Haryana" -> R.drawable.haryana
            "Himachal Pradesh" -> R.drawable.himachal
            "Jammu and Kashmir" -> R.drawable.jammu_kashmir
            "Jharkhand" -> R.drawable.jharkhand
            "Karnataka" -> R.drawable.karnataka
            "Kerala" -> R.drawable.kerala
            "Lakshadweep" -> R.drawable.lakshadweep
            "Madhya Pradesh" -> R.drawable.mp
            "Maharashtra" -> R.drawable.maharashtra
            "Manipur" -> R.drawable.manipur
            "Meghalaya" -> R.drawable.meghalaya
            "Mizoram" -> R.drawable.mizoram
            "Nagaland" -> R.drawable.nagaland
            "Odisha" -> R.drawable.odisha
            "Puducherry" -> R.drawable.puducherry
            "Punjab" -> R.drawable.punjab
            "Rajasthan" -> R.drawable.rajasthan
            "Sikkim" -> R.drawable.sikkim
            "Tamil Nadu" -> R.drawable.tamil
            "Telangana" -> R.drawable.telangana
            "Tripura" -> R.drawable.tripura
            "Uttarakhand" -> R.drawable.uttarakhand
            "Uttar Pradesh" -> R.drawable.up
            "West Bengal" -> R.drawable.wb
            "Ladakh" -> R.drawable.ladakh
            else -> R.drawable.india
        }
    }

    fun getMostAffectedDistricts(stateWiseDataArrayList: ArrayList<StateDistrictWiseResponse>?): ArrayList<District> {
        val mostAffectedDistricts: ArrayList<District> = ArrayList<District>()

        //Get user selected state
        var userSelectedState: StateDistrictWiseResponse? = null
        for (stateDistrictWiseResponse in stateWiseDataArrayList!!) {
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(stateDistrictWiseResponse.name.trim { it <= ' ' }, ignoreCase = true)) {
                userSelectedState = stateDistrictWiseResponse
                break
            }
        }
        var districtList: ArrayList<District> = ArrayList<District>()
        if (userSelectedState != null) districtList = userSelectedState.districtArrayList


        // Sort District according to confirmed cases
        districtList.sortWith { o1, o2 -> // descending
            (o2.total?.confirmed ?: 0) - (o1.total?.confirmed ?: 0)
        }
        if (districtList.size >= Constant.MOST_AFFECTED_DISTRICT_COUNT) {
            // if district list is greater than the data count to show, then its perfect, add the required subset
            mostAffectedDistricts.addAll(
                districtList.subList(
                    0,
                    Constant.MOST_AFFECTED_DISTRICT_COUNT - 1
                )
            )
        } else {
            // if district list is less than the data count required to show, then add all of them
            mostAffectedDistricts.addAll(districtList)
        }

        // Check if user has selected their state and district then only perform this action
        if (Constant.locationIsSelectedByUser) {
            var userDistrictIsInMostAffectedState = false
            var userDistrictIsInMostAffectedStateIndex = -1

            // check if user selected state is in list, if not add them
            for (i in mostAffectedDistricts.indices) {
                val district: District = mostAffectedDistricts[i]
                if (Constant.userSelectedDistrict.trim { it <= ' ' }
                        .equals(district.name.trim { it <= ' ' }, ignoreCase = true)) {
                    userDistrictIsInMostAffectedState = true
                    userDistrictIsInMostAffectedStateIndex = i
                    break
                }
            }

            // if user selected district is not in the most affected state list, add them from all state list
            if (!userDistrictIsInMostAffectedState) {
                for (district in districtList) {
                    if (Constant.userSelectedDistrict.trim { it <= ' ' }
                            .equals(district.name.trim { it <= ' ' }, ignoreCase = true)) {
                        mostAffectedDistricts.add(0, district)
                        break
                    }
                }
            } else {

                // move user selected district at 0th index
                val temp: District = mostAffectedDistricts[userDistrictIsInMostAffectedStateIndex]
                mostAffectedDistricts.removeAt(userDistrictIsInMostAffectedStateIndex)
                mostAffectedDistricts.add(0, temp)
            }
        }
        return mostAffectedDistricts
    }

    fun getCurrentStateStats(stateDistrictList: ArrayList<StateDistrictWiseResponse>): StateDistrictWiseResponse? {
        var currentStateStats: StateDistrictWiseResponse? = null
        for (stateDistrictData in stateDistrictList) {
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(stateDistrictData.name.trim { it <= ' ' }, ignoreCase = true)) {
                currentStateStats = stateDistrictData
                break
            }
        }
        return currentStateStats
    }

    fun getMostAffectedStates(stateDistrictList: ArrayList<StateDistrictWiseResponse>): ArrayList<StateDistrictWiseResponse> {
        val mostAffectedStates: ArrayList<StateDistrictWiseResponse> = arrayListOf()

        // Remove Total if there is any
        for (i in stateDistrictList.indices) {
            if (stateDistrictList[i].code.equals(Constant.TOTAL_ITEM_CODE, ignoreCase = true) ||
                stateDistrictList[i].name.equals(Constant.TOTAL_ITEM_NAME, ignoreCase = true)
            ) {
                stateDistrictList.removeAt(i)
                break
            }
        }

        // Sort State according to confirmed cases
        stateDistrictList.sortWith { o1, o2 -> // descending
            (o2.total?.confirmed ?: 0) - (o1.total?.confirmed ?: 0)
        }

        if (stateDistrictList.size >= Constant.MOST_AFFECTED_STATES_COUNT) {
            // if state list is greater than the data count to show, then its perfect, add the required subset
            mostAffectedStates.addAll(
                stateDistrictList.subList(
                    0,
                    Constant.MOST_AFFECTED_STATES_COUNT - 1
                )
            )
        } else {
            // if state list is less than the data count required to show, then add all of them
            mostAffectedStates.addAll(stateDistrictList)
        }

        // Check if user has selected their state and district then only perform this action
        if (Constant.locationIsSelectedByUser) {
            var userStateIsInMostAffectedState = false
            var userStateIsInMostAffectedStateIndex = -1
            // check if user selected state is in list, if not add them
            for (i in mostAffectedStates.indices) {
                val mostAffectedState: StateDistrictWiseResponse = mostAffectedStates[i]
                if (Constant.userSelectedState.trim { it <= ' ' }
                        .equals(
                            mostAffectedState.name.trim { it <= ' ' },
                            ignoreCase = true
                        )
                ) {
                    userStateIsInMostAffectedState = true
                    userStateIsInMostAffectedStateIndex = i
                    break
                }
            }

            // if user selected state is not in the most affected state list, add them from all state list
            if (!userStateIsInMostAffectedState) {
                for (stateWiseData in stateDistrictList) {
                    if (Constant.userSelectedState.trim { it <= ' ' }
                            .equals(
                                stateWiseData.name.trim { it <= ' ' },
                                ignoreCase = true
                            )
                    ) {
                        mostAffectedStates.add(0, stateWiseData)
                        break
                    }
                }
            } else {

                // move user selected state at 0th index
                val temp: StateDistrictWiseResponse =
                    mostAffectedStates[userStateIsInMostAffectedStateIndex]
                mostAffectedStates.removeAt(userStateIsInMostAffectedStateIndex)
                mostAffectedStates.add(0, temp)
            }
        }
        return mostAffectedStates
    }

    fun getObjectTotalOfAffectedState(stateDistrictList: ArrayList<StateDistrictWiseResponse>): StateDistrictWiseResponse {
        var objectTotalOfMostAffectedState: StateDistrictWiseResponse? = null
        for (stateWiseData in stateDistrictList) {
            if (Constant.TOTAL_ITEM_CODE.equals(stateWiseData.code, ignoreCase = true) ||
                Constant.TOTAL_ITEM_NAME.equals(stateWiseData.name, ignoreCase = true)
            ) {
                objectTotalOfMostAffectedState = stateWiseData
            }
        }
        if (objectTotalOfMostAffectedState == null) {
            var confirmed = 0
            var deaths = 0
            var deltaConfirmed = 0
            var deltaDeaths = 0
            var deltaRecovered = 0
            var tested = 0
            var vaccinated1 = 0
            var vaccinated2 = 0

            var recovered = 0
            val state = Constant.TOTAL_ITEM_NAME
            val stateCode = Constant.TOTAL_ITEM_CODE

            // if Object "Total" Does not found in the list, compute manually
            for (stateWiseData in stateDistrictList) {
                confirmed += stateWiseData.total?.confirmed ?: 0
                deaths += stateWiseData.total?.deceased ?: 0
                tested += stateWiseData.total?.tested ?: 0
                vaccinated1 += stateWiseData.total?.vaccinated1 ?: 0
                vaccinated2 += stateWiseData.total?.vaccinated2 ?: 0
                deltaConfirmed += stateWiseData.delta?.confirmed ?: 0
                deltaDeaths += stateWiseData.delta?.deceased ?: 0
                deltaRecovered += stateWiseData.delta?.recovered ?: 0
                recovered += stateWiseData.delta?.recovered ?: 0
            }
            objectTotalOfMostAffectedState = StateDistrictWiseResponse(
                state, stateCode, Delta(deltaConfirmed, deltaDeaths, deltaRecovered),
                arrayListOf(), Meta(),
                Total(confirmed, deaths, recovered)
            )
        }
        return objectTotalOfMostAffectedState
    }

    fun getObjectTotalOfAffectedDistricts(stateWiseDataArrayList: ArrayList<StateDistrictWiseResponse>?): ArrayList<District> {
        val districtObjectTotal: ArrayList<District> = ArrayList<District>()

        //Get user selected state
        var userSelectedState: StateDistrictWiseResponse? = null
        for (stateDistrictWiseResponse in stateWiseDataArrayList!!) {
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(stateDistrictWiseResponse.name.trim { it <= ' ' }, ignoreCase = true)) {
                userSelectedState = stateDistrictWiseResponse
                break
            }
        }

        districtObjectTotal.add(
            District(
                Constant.TOTAL_ITEM_NAME,
                Delta(
                    userSelectedState?.delta?.confirmed, userSelectedState?.delta?.deceased,
                    userSelectedState?.delta?.recovered
                ),
                Meta(
                    userSelectedState?.meta?.date, userSelectedState?.meta?.lastUpdated,
                    userSelectedState?.meta?.population
                ),
                Total(
                    userSelectedState?.total?.confirmed, userSelectedState?.total?.deceased,
                    userSelectedState?.total?.recovered
                )
            )
        )
        return districtObjectTotal
    }

}