package online.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class DistrictDelta(

    @SerializedName("confirmed")
    var confirmed: Int

) : Parcelable