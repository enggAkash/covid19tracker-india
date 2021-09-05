package `in`.engineerakash.covid19india.util

object Constant {

    const val SUPPORT_EMAIL = "developer.akashkumar@gmail.com"

    const val NO_EMAIL_CLIENT_MSG = "No email app found."

    /*will show top most affected states(which has large number of confirmed cases*/
    const val MOST_AFFECTED_STATES_COUNT = 5

    /*will show top most affected district(which has large number of confirmed cases*/
    const val MOST_AFFECTED_DISTRICT_COUNT = 5

    /*will show last 3 date data*/
    const val BAR_CHART_DATE_COUNT = 3

    const val COVID_REPORT_HOUR = 8 // 8AM
    const val COVID_REPORT_MINUTE = 0 // 8:00AM

    @JvmField
    var userSelectedCountry = "India"

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
}

enum class ChooseLocationStartedFrom {
    TRACK_FRAG, SETTING_FRAG
}

object CovidReportFrequency {
    const val DAILY = "daily"
    const val NEVER = "never"
}