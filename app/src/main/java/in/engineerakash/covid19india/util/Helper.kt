package `in`.engineerakash.covid19india.util

import `in`.engineerakash.covid19india.BuildConfig
import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.enums.ChartType
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.DisplayMetrics
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

    fun emailSupport(context: Context) {
        val subject = "Billin App Support"
        val instruction = "-- Below Information will help us to give you better support. --"
        val deviceId = """
               
               Device ID : ${getAndroidId(context)}
               """.trimIndent()
        val dm = DisplayMetrics()

        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
        val display = """
Display: ${dm.widthPixels} X ${dm.heightPixels}"""
        val androidVersion = """
               
               Android Version: ${Build.VERSION.RELEASE}
               """.trimIndent()
        val product = """
               
               Product Name : ${Build.PRODUCT}
               """.trimIndent()
        val versionName = """
               
               App Version : ${BuildConfig.VERSION_NAME}
               
               
               """.trimIndent()
        val body = """
               
               
               
               
               
               $instruction$deviceId$display$androidVersion$product$versionName
               """.trimIndent()
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constant.SUPPORT_EMAIL))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(emailIntent, "Reach to Billin support..."))
    }

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

    fun phoneSupport(context: Context) {
        val phone = Constant.SUPPORT_PHONE
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(callIntent)
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
        err: String? = "Something Went Wrong while processing your request"
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
        showMsg(context, "Internet is Not available.")
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
            showMsg(context, "Whatsapp is not Installed")
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
                showMsg(context, "Failed to send whatsapp")
            }
        } catch (e: Exception) {
            showMsg(context, "Failed to send whatsapp")
        }
    }

    fun sharingUrl(context: Context): String {
        return "https://play.google.com/store/apps/details?id=" + context.packageName
    }

    fun shareApp(context: Context) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            """
                Billin is a complete solution for your business. It involves handling multiple branches, employees, sale and purchase record tracking, report generation and much more. 
                
                And after all it is completely free for a month. 
                
                Grow your business with billin now!
                ${sharingUrl(context)}
                """.trimIndent()
        )
        shareIntent.type = "text/plain"
        context.startActivity(shareIntent)
    }

    fun isSelfUrl(url: String?): Boolean {
        val uri = Uri.parse(Constant.DOMAIN)
        return if (uri.host != null && url != null) {
            url.toLowerCase(Locale.ENGLISH).contains(uri.host!!.toLowerCase(Locale.ENGLISH))
        } else false
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
                Toast.makeText(context, Constant.NO_EMAIL_CLIENT_MSG, Toast.LENGTH_SHORT).show()
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
                    "App is not installed to handle this request",
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
            Toast.makeText(context, "No App is available to Handle this request", Toast.LENGTH_LONG)
                .show()
        }
    }
}