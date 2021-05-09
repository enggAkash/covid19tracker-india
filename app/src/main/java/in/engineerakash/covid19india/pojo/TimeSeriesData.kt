package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class TimeSeriesData(
    @SerializedName("dailyconfirmed")
    var dailyConfirmed: String = "0",

    @SerializedName("dailydeceased")
    var dailyDeceased: String = "0",

    @SerializedName("dailyrecovered")
    var dailyRecovered: String = "0",

    @SerializedName("date")
    var date: String = "0",

    @SerializedName("totalconfirmed")
    var totalConfirmed: String = "0",

    @SerializedName("totaldeceased")
    var totalDeceased: String = "0",

    @SerializedName("totalrecovered")
    var totalRecovered: String = "0",

    ) : Parcelable