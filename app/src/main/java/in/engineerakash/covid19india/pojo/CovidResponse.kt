package `in`.engineerakash.covid19india.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Total(
    @SerializedName("confirmed")
    var confirmed: Int? = null,

    @SerializedName("deceased")
    var deceased: Int? = null,

    @SerializedName("recovered")
    var recovered: Int? = null,

    @SerializedName("tested")
    var tested: Int? = null,

    @SerializedName("vaccinated1")
    var vaccinated1: Int? = null,

    @SerializedName("vaccinated2")
    var vaccinated2: Int? = null
) : Parcelable

@Parcelize
class Delta(
    @SerializedName("confirmed")
    var confirmed: Int? = null,

    @SerializedName("deceased")
    var deceased: Int? = null,

    @SerializedName("recovered")
    var recovered: Int? = null,

    @SerializedName("tested")
    var tested: Int? = null,

    @SerializedName("vaccinated1")
    var vaccinated1: Int? = null,

    @SerializedName("vaccinated2")
    var vaccinated2: Int? = null

) : Parcelable

@Parcelize
class Meta(
    @SerializedName("date")
    var date: String? = null,

    @SerializedName("last_updated")
    var lastUpdated: String? = null,

    @SerializedName("population")
    var population: Int? = null,
) : Parcelable

/*
"meta": {
            "date": "2021-09-05",
            "last_updated": "2021-09-05T17:56:52+05:30",
            "population": 1192000,
            "tested": {
                "date": "2021-09-04",
                "source": "https://dipr.mizoram.gov.in/post/covid-19-positive-thar-825-hmuhchhuah-a-ni"
            }
        }
*/