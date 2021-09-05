package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class StateDistrictWiseResponse(

    var name: String,
    var code: String,

    @SerializedName("delta")
    var delta: Delta?,

    @SerializedName("districts")
    var districtArrayList: ArrayList<District> = arrayListOf(),

    @SerializedName("meta")
    var meta: Meta?,

    @SerializedName("total")
    var total: Total?

) : Parcelable