package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class TimeSeriesStateWiseResponse(

    var name: String,
    var code: String,

    var timeSeriesList: ArrayList<TimeSeriesData> = arrayListOf(),

    ) : Parcelable