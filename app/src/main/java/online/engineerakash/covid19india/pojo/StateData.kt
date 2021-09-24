package online.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class StateData(
    @SerializedName("statewise")
    private var stateWiseData: ArrayList<StateWiseData> = arrayListOf()

) : Parcelable