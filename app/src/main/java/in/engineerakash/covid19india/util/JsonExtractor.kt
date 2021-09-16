package `in`.engineerakash.covid19india.util

import `in`.engineerakash.covid19india.pojo.*
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

                val stateCode = stateKeys.next()
                val stateName = Constant.stateCodeNameMap[stateCode] ?: ""

                val stateJo = rootJo.getJSONObject(stateCode)

                var stateDelta: Delta? = null
                if (stateJo.has("delta")) {
                    val stateDeltaJo = stateJo.getJSONObject("delta")

                    stateDelta = Delta(
                        stateDeltaJo.optInt("confirmed"), stateDeltaJo.optInt("deceased"),
                        stateDeltaJo.optInt("recovered")
                    )
                }

                var stateMeta: Meta? = null
                if (stateJo.has("meta")) {
                    val stateMetaJo = stateJo.getJSONObject("meta")

                    stateMeta = Meta(
                        stateMetaJo.optString("date"),
                        DateTimeUtil.parseMetaDateTimeToAppsDefaultDateTime(stateMetaJo.optString("last_updated")),
                        stateMetaJo.optInt("population")
                    )
                }

                val districtDataJo = stateJo.getJSONObject("districts")
                val districtKeys = districtDataJo.keys()
                val districtList: ArrayList<District> = ArrayList<District>()

                while (districtKeys.hasNext()) {
                    val districtName = districtKeys.next()

                    val districtJo = districtDataJo.getJSONObject(districtName)

                    var districtDelta: Delta? = null
                    if (districtJo.has("delta")) {
                        val deltaJo = districtJo.getJSONObject("delta")

                        districtDelta = Delta(
                            deltaJo.optInt("confirmed"), deltaJo.optInt("deceased"),
                            deltaJo.optInt("recovered")
                        )
                    }

                    var districtMeta: Meta? = null
                    if (districtJo.has("meta")) {
                        val metaJo = districtJo.getJSONObject("meta")

                        districtMeta = Meta(
                            metaJo.optString("date"),
                            DateTimeUtil.parseMetaDateTimeToAppsDefaultDateTime(metaJo.optString("last_updated")),
                            metaJo.optInt("population")
                        )
                    }

                    var districtTotal: Total? = null
                    if (districtJo.has("total")) {
                        val totalJo = districtJo.getJSONObject("total")

                        districtTotal = Total(
                            totalJo.optInt("confirmed"), totalJo.optInt("deceased"),
                            totalJo.optInt("recovered"), totalJo.optInt("vaccinated1"),
                            totalJo.optInt("vaccinated2")
                        )
                    }

                    val district =
                        District(districtName, districtDelta, districtMeta, districtTotal)
                    districtList.add(district)
                }

                var stateTotal: Total? = null
                if (stateJo.has("total")) {
                    val stateTotalJo = stateJo.getJSONObject("total")

                    stateTotal = Total(
                        stateTotalJo.optInt("confirmed"),
                        stateTotalJo.optInt("deceased"),
                        stateTotalJo.optInt("recovered"),
                        stateTotalJo.optInt("tested"),
                        stateTotalJo.optInt("vaccinated1"),
                        stateTotalJo.optInt("vaccinated2")
                    )
                }

                val stateDistrictWiseResponse = StateDistrictWiseResponse(
                    stateName, stateCode,
                    stateDelta, districtList, stateMeta, stateTotal
                )
                stateDistrictList.add(stateDistrictWiseResponse)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return stateDistrictList
    }
}