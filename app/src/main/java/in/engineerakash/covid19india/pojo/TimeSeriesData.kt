package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class TimeSeriesData(
    @SerializedName("dailyconfirmed")
    var dailyConfirmed: String,

    @SerializedName("dailydeceased")
    var dailyDeceased: String,

    @SerializedName("dailyrecovered")
    var dailyRecovered: String,

    @SerializedName("date")
    var date: String,

    @SerializedName("totalconfirmed")
    var totalConfirmed: String,

    @SerializedName("totaldeceased")
    var totalDeceased: String,

    @SerializedName("totalrecovered")
    var totalRecovered: String

) : Parcelable