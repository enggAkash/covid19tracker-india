package `in`.engineerakash.covid19india.util

import `in`.engineerakash.covid19india.pojo.District
import `in`.engineerakash.covid19india.pojo.DistrictDelta
import `in`.engineerakash.covid19india.pojo.StateDistrictWiseResponse
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object JsonExtractor {

    @JvmStatic
    fun parseStateDistrictWiseResponseJson(json: String?): ArrayList<StateDistrictWiseResponse> {

        val stateDistrictList: ArrayList<StateDistrictWiseResponse> =
            ArrayList<StateDistrictWiseResponse>()

        try {
            val rootJo = JSONObject(json.toString())
            val stateKeys = rootJo.keys()

            while (stateKeys.hasNext()) {

                val stateName = stateKeys.next()
                val stateJo = rootJo.getJSONObject(stateName)
                val districtDataJo = stateJo.getJSONObject("districtData")
                val districtKeys = districtDataJo.keys()
                val districtList: ArrayList<District> = ArrayList<District>()

                while (districtKeys.hasNext()) {
                    val districtName = districtKeys.next()

                    val districtJo = districtDataJo.getJSONObject(districtName)
                    val confirmed = districtJo.optInt("confirmed")
                    val lastUpdatedTime = districtJo.optString("lastupdatedtime")
                    val deltaJo = districtJo.getJSONObject("delta")
                    val deltaConfirmed = deltaJo.optInt("confirmed")
                    val districtDelta = DistrictDelta(deltaConfirmed)

                    val district = District(districtName, confirmed, lastUpdatedTime, districtDelta)
                    districtList.add(district)
                }

                val stateDistrictWiseResponse = StateDistrictWiseResponse(stateName, districtList)
                stateDistrictList.add(stateDistrictWiseResponse)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return stateDistrictList
    }
}