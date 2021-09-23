package `in`.engineerakash.covid19india.util

object Constant {

    const val SUPPORT_EMAIL = "developer.akashkumar@gmail.com"

    /*will show top most affected states(which has large number of confirmed cases*/
    const val MOST_AFFECTED_STATES_COUNT = 5

    /*will show top most affected district(which has large number of confirmed cases*/
    const val MOST_AFFECTED_DISTRICT_COUNT = 5

    /*will show last 3 date data*/
    const val BAR_CHART_DATE_COUNT = 3

    const val COVID_REPORT_HOUR = 10 // 10AM
    const val COVID_REPORT_MINUTE = 0 // 10:00AM

    val stateCodeNameMap = HashMap<String, String>()

    @JvmField
    var userSelectedState = "" //""Maharashtra" //""Delhi";

    @JvmField
    var userSelectedDistrict = "" //""Mumbai  " //""East Delhi";

    const val locationIsSelectedByUser = true

    const val stateJsonAssetName = "state_json.json"

    const val DEFAULT_SP_NAME = "covid_sp"
    const val SELECTED_STATE_SP_KEY = "selected_state_key"
    const val SELECTED_DISTRICT_SP_KEY = "selected_district_key"

    const val NOTIFICATION_ID_SP_KEY = "notification_id_key"

    const val PERIODIC_TASK_TAG_SP_KEY = "periodic_task_id_key"
    const val REPORT_FREQUENCY_SP_KEY = "report_frequency_sp_key"

    const val LOCALE_NAME_SP_KEY = "locale_name_sp_key"
    const val IS_LOCALE_NAME_IS_SET_SP_KEY = "is_locale_name_is_set_sp_key"

    const val TOTAL_ITEM_NAME = "Total"
    const val TOTAL_ITEM_CODE = "TT"

    // Locale
    val LOCAL_EN = "en"
    val LOCAL_HI = "hi"

    const val SHOW_ADS = true // todo remove empty space when not showing ads

    //todo change in production
    val THIS_BUILD_IS_FOR = AppStore.AMAZON_APP_STORE
}

enum class ChooseLocationStartedFrom {
    TRACK_FRAG, SETTING_FRAG
}

enum class LanguageSelectionStartedFrom {
    SETTING_FRAG
}

object CovidReportFrequency {
    const val DAILY = "daily"
    const val NEVER = "never"
}

object AppUpdateType {
    const val FLEXIBLE = 0
    const val IMMEDIATE = 1
}

enum class AppStore {
    APK_PURE, AMAZON_APP_STORE, GOOGLE_PLAY_STORE
}

object AppStoreDefaultUrl {
    val APK_PURE = "https://apkpure.com/p/in.engineerakash.covid19india"
    val AMAZON_APP_STORE =
        "http://www.amazon.com/gp/mas/dl/android?p=in.engineerakash.covid19india"
    val GOOGLE_PLAY_STORE =
        "https://play.google.com/store/apps/details?id=in.engineerakash.covid19india"
}