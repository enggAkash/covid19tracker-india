package online.engineerakash.covid19india.util

import online.engineerakash.covid19india.BuildConfig
import online.engineerakash.covid19india.R
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

            val playStoreUrl =
                if (Constant.THIS_BUILD_IS_FOR == AppStore.AMAZON_APP_STORE) {
                    AppStoreDefaultUrl.AMAZON_APP_STORE
                } else if (Constant.THIS_BUILD_IS_FOR == AppStore.APK_PURE) {
                    AppStoreDefaultUrl.APK_PURE
                } else if (Constant.THIS_BUILD_IS_FOR == AppStore.MI_APP_STORE) {
                    AppStoreDefaultUrl.MI_APP_STORE
                } else {
                    // google play store
                    AppStoreDefaultUrl.GOOGLE_PLAY_STORE
                }

            val shareMessage =
                context?.getString(R.string.share_app_description, appName, playStoreUrl)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context?.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.choose_one)))

        } catch (e: Exception) {
            Toast.makeText(context, context?.getString(R.string.app_not_installed), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun contactAppDeveloper(context: Context?) {
        Toast.makeText(context, context?.getString(R.string.select_email_app), Toast.LENGTH_SHORT).show()


        val subject = context?.getString(R.string.app_support)

        val instruction = context?.getString(R.string.below_info_help_us_to_support_better)
        val deviceId = context?.getString(R.string.device_id, getAndroidId(context))
        val dm = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
        val display = context.getString(R.string.display_pixel, dm.widthPixels, dm.heightPixels)
        val androidVersion = context.getString(R.string.android_version, Build.VERSION.RELEASE)
        val product = context.getString(R.string.android_product, Build.PRODUCT)
        val versionName = context.getString(R.string.app_version, BuildConfig.VERSION_NAME)

        val body = "\n\n\n\n\n$instruction\n$deviceId$display$androidVersion$product$versionName\n"

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constant.SUPPORT_EMAIL))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)

        try {
            context.startActivity(
                Intent.createChooser(
                    emailIntent,
                    context.getString(R.string.reach_out_to_developer)
                )
            );
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.no_email_app_found), Toast.LENGTH_SHORT).show()
        }
    }

    fun getAndroidId(context: Context?): String? {
        return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    }
}