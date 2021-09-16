package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class TimeSeriesStateWiseResponse(
    @SerializedName("cases_time_series")
    var casesTimeSeriesArrayList: ArrayList<TimeSeriesData> = arrayListOf(),

    ) : Parcelable