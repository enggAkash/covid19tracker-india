package online.engineerakash.covid19india.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*


object AppUtil {
    private const val TAG = "AppUtil"

    fun setAppLocale(context: Context) {
        val prefs = context.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
        var localeName =
            prefs.getString(Constant.LOCALE_NAME_SP_KEY, "")
        if (TextUtils.isEmpty(localeName)) localeName = "en"
        setAppLocale(context, localeName, false)
    }

    fun setAppLocale(
        context: Context,
        newLocaleName: String?,
        restartApp: Boolean
    ) {
        newLocaleName?.let {
            if (it.contains(getCurrentLocale(context))) return
            val newLocale = Locale(it)
            Locale.setDefault(newLocale)
            val configuration = Configuration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(newLocale)
            } else {
                configuration.locale = newLocale
            }

            val prefs = context.getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(Constant.IS_LOCALE_NAME_IS_SET_SP_KEY, true).apply()

            Log.d(TAG, "onClick: now updating configuration $newLocaleName")
            context.resources.updateConfiguration(
                configuration,
                context.resources.displayMetrics
            )
            if (restartApp) restartApp(context)
        }
    }

    fun restartApp(context: Context) {
        val i = (context as AppCompatActivity).baseContext.packageManager
            .getLaunchIntentForPackage(context.baseContext.packageName)
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.baseContext.startActivity(i)
        }
    }

    fun getCurrentLocale(context: Context): String {
        val currentLocale: String
        currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].toString()
        } else {
            context.resources.configuration.locale.toString()
        }
        return currentLocale
    }

    fun getStringByLocal(context: Activity, id: Int, locale: String?): String {
        locale?.let {
            val configuration =
                Configuration(context.resources.configuration)
            configuration.setLocale(Locale(locale))
            return context.createConfigurationContext(configuration).resources.getString(id)
        }
        return ""
    }

    fun getStringArrayByLocal(context: Activity, id: Int, locale: String?): Array<String> {
        locale?.let {
            val configuration =
                Configuration(context.resources.configuration)
            configuration.setLocale(Locale(locale))
            return context.createConfigurationContext(configuration).resources.getStringArray(id)
        }
        return arrayOf()
    }

    /**
     * s: 1234
     * formatted = 1
     * s = 234
     */

    fun String?.formatToIndianNumber(): String {
        var s = this ?: ""
        var formatted = ""
        if (s.length > 1) {
            formatted = s.substring(0, 1)
            s = s.substring(1)
        }

        while (s.length > 3) {
            formatted += "," + s.substring(0, 2)
            s = s.substring(2)
        }
        return "$formatted,$s"
    }
}