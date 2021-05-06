package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class District(
    @SerializedName("name")
    var name: String,

    @SerializedName("confirmed")
    var confirmed: Int,

    @SerializedName("lastupdatedtime")
    var lastUpdateTime: String,

    @SerializedName("delta")
    var delta: DistrictDelta?

) : Parcelable