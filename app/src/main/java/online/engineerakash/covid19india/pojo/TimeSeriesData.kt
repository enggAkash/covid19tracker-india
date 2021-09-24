package online.engineerakash.covid19india.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TimeSeriesData(

    var date: String = "",

    var delta: Delta? = null,

    var total: Total? = null

) : Parcelable