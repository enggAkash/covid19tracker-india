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

    @JvmField
    var userSelectedCountry = "India"

    @JvmField
    var userSelectedState = "" //""Maharashtra" //""Delhi";

    @JvmField
    var userSelectedDistrict = "" //""Mumbai  " //""East Delhi";

    @JvmField
    var locationIsSelectedByUser = true

    @JvmField
    var stateJsonAssetName = "state_json.json"

    @JvmField
    var DEFAULT_SP_NAME = "covid_sp"

    @JvmField
    var SELECTED_STATE_SP_KEY = "selected_state_key"

    @JvmField
    var SELECTED_DISTRICT_SP_KEY = "selected_district_key"

    @JvmField
    var NOTIFICATION_ID_SP_KEY = "notification_id_key"

    @JvmField
    var PERIODIC_TASK_ID_SP_KEY = "periodic_task_id_key"
}

object NotificationChannelName {
    const val DEFAULT = "default"
    const val COVID_REPORT = "covid_report"
    const val COVID_RISING_NEARBY = "covid_rising_nearby"
    const val BUY_FROM_HOME = "buy_from_home"
    const val PROMOTIONAL = "promotional"
}

enum class ChooseLocationStartedFrom {
    TRACK_FRAG, SETTING_FRAG
}