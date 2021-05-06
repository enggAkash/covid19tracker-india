package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class StateDistrictWiseResponse(
    @SerializedName("name")
    var name: String,

    @SerializedName("districtData")
    var districtArrayList: ArrayList<District> = arrayListOf()

) : Parcelable