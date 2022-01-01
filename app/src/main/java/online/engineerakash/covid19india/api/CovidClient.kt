package online.engineerakash.covid19india.api

import online.engineerakash.covid19india.BuildConfig
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class CovidClient private constructor() {

    companion object {
        private const val TAG = "CovidClient"

        private const val COVID_19_INDIA_BASE_URL = "https://data.incovid19.org/"

        var instance: CovidClient = CovidClient()
        private lateinit var covidService: CovidService
    }

    init {
        val okHttpClient = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) else interceptor.setLevel(
            HttpLoggingInterceptor.Level.NONE
        )
        okHttpClient.addInterceptor(interceptor)
        val retrofit = Retrofit.Builder()
            .baseUrl(COVID_19_INDIA_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient.build())
            .build()

        covidService = retrofit.create(CovidService::class.java)
    }

    val timeSeriesAndStateWiseData: Single<ResponseBody>
        get() = covidService.timeSeriesAndStateWiseData
    val stateDistrictWiseData: Single<ResponseBody>
        get() = covidService.stateDistrictWiseData

}