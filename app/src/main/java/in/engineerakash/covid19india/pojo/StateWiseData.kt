package `in`.engineerakash.covid19india.pojo

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class StateWiseData(
    @SerializedName("active")
    var active: String = "0",

    @SerializedName("confirmed")
    var confirmed: String = "0",

    @SerializedName("deaths")
    var deaths: String = "0",

    @SerializedName("deltaconfirmed")
    var deltaConfirmed: String = "0",

    @SerializedName("deltadeaths")
    var deltaDeaths: String = "0",

    @SerializedName("deltarecovered")
    var deltaRecovered: String = "0",

    @SerializedName("lastupdatedtime")
    var lastUpdatedTime: String = "",

    @SerializedName("recovered")
    var recovered: String = "0",

    @SerializedName("state")
    var state: String = "",

    @SerializedName("statecode")
    var stateCode: String = ""
) : Parcelable