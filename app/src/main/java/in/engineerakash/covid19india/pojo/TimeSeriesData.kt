package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class TimeSeriesData(

    var date: String = "",

    var delta: Delta? = null,

    var total: Total? = null

) : Parcelable