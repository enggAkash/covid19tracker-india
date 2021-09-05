package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class District(
    @SerializedName("name")
    var name: String,

    @SerializedName("delta")
    var delta: Delta?,

    @SerializedName("meta")
    var meta: Meta?,

    @SerializedName("total")
    var total: Total?

) : Parcelable