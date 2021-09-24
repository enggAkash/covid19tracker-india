package online.engineerakash.covid19india.pojo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Precaution(
    var groupTitle: String = "",
    var groupSubTitle: String = "",
    var iconResource: Int? = null,
    var title: String = "",
    var description: String = "",

    var affiliateLink1Text: String = "",
    var affiliateLink1Url: String = "",
    var affiliateLink2Text: String = "",
    var affiliateLink2Url: String = ""

) : Parcelable