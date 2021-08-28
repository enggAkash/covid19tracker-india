package `in`.engineerakash.covid19india.util

import `in`.engineerakash.covid19india.BuildConfig
import `in`.engineerakash.covid19india.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.widget.Toast


object IntentUtil {

    fun shareApp(context: Context?) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context?.getString(R.string.app_name))

            val appName = context?.getString(R.string.app_name)

            val shareMessage = context?.getString(
                R.string.share_app_description, appName, BuildConfig.APPLICATION_ID
            )
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context?.startActivity(Intent.createChooser(shareIntent, "choose one"))

        } catch (e: Exception) {
            Toast.makeText(context, "There is no app to handle this Request", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun contactAppDeveloper(context: Context?) {
        Toast.makeText(context, "Select Email App", Toast.LENGTH_SHORT).show()


        val subject = "App Support"

        val instruction = "-- Below Information will help us to give you better support. --"
        val deviceId = "Device ID : ${getAndroidId(context)}"
        val dm = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
        val display = "Display: ${dm.widthPixels} X ${dm.heightPixels}"
        val androidVersion = "Android Version: ${Build.VERSION.RELEASE}"
        val product = "Product Name : ${Build.PRODUCT}"
        val versionName = "App Version : ${BuildConfig.VERSION_NAME}"

        val body = "\n\n\n\n\n$instruction\n$deviceId$display$androidVersion$product$versionName\n"

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constant.SUPPORT_EMAIL))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Reach out to developer via email"));
        } catch (e: Exception) {
            Toast.makeText(context, Constant.NO_EMAIL_CLIENT_MSG, Toast.LENGTH_SHORT).show()
        }
    }

    fun getAndroidId(context: Context?): String? {
        return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    }
}