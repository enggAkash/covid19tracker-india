package `in`.engineerakash.covid19india.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface CovidService {
    @get:GET("/v4/min/timeseries.min.json")
    val timeSeriesAndStateWiseData: Single<ResponseBody>

    @get:GET("/v4/min/data.min.json")
    val stateDistrictWiseData: Single<ResponseBody>
}