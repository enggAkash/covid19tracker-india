package `in`.engineerakash.covid19india.api

import `in`.engineerakash.covid19india.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class CovidClient private constructor() {

    companion object {
        private const val TAG = "CovidClient"

        private const val COVID_19_INDIA_BASE_URL = "https://api.covid19india.org/"

        var instance: CovidClient = CovidClient()
        private lateinit var covidService: CovidService
    }

    init {
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
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

    fun getInvoiceBill(url: String?, postData: String?): Observable<Response<ResponseBody?>?>? {
        return covidService.getInvoiceFile(url, postData, "ANDROID")
    }

    fun emailInvoiceBill(url: String?, postData: String?): Observable<Response<ResponseBody?>?>? {
        return covidService.emailInvoiceFile(url, postData, "ANDROID")
    }

    fun login(
        url: String?,
        clientName: String?,
        username: String?,
        password: String?,
        tval: String?
    ): Single<ResponseBody?>? {
        val sname = "LoginRegister"
        val fname = "androidLogin"
        val isweb = 0
        return covidService.login(url, clientName, username, password, sname, fname, tval, isweb)
    }

    fun sendPasswordResetLink(
        url: String?,
        clientName: String?,
        username: String?,
        tval: String?
    ): Observable<ResponseBody?>? {
        val sname = "LoginRegister"
        val fname = "sendResetPasswordLinkAndroid"
        val isweb = 0
        return covidService.sendResetPasswordLink(
            url,
            clientName,
            username,
            sname,
            fname,
            tval,
            isweb
        )
    }

    fun checkIfClientEmailMobExist(
        url: String?,
        email: String?,
        mobile: String?,
        tval: String?
    ): Observable<ResponseBody?>? {
        val sname = "CommonServices"
        val fname = "checkClientEmailMob"
        val isweb = 0
        return covidService.checkIfEmailMobExist(url, email, mobile, sname, fname, tval, isweb)
    }

    fun getStates(url: String?, countryId: Int, tval: String?): Observable<ResponseBody?>? {
        val sname = "CommonServices"
        val fname = "getStates"
        val isweb = 0
        return covidService.getStates(url, countryId, sname, fname, tval, isweb)
    }

    fun getCities(url: String?, stateId: Int, tval: String?): Observable<ResponseBody?>? {
        val sname = "CommonServices"
        val fname = "getCities"
        val isweb = 0
        return covidService.getCities(url, stateId, sname, fname, tval, isweb)
    }

    fun getSubdomainAvailability(
        url: String?,
        subdomain: String?,
        tval: String?
    ): Observable<ResponseBody?>? {
        val sname = "CommonServices"
        val fname = "getSubdomainAvailibility"
        val isweb = 0
        return covidService.getSubdomainAvailability(url, subdomain, sname, fname, tval, isweb)
    }

    fun registerClient(
        url: String?, subdomain: String?, companyName: String?,
        companyAddress: String?, companyCityId: Int, companyStateId: Int,
        companyCountryId: Int, adminName: String?, adminMobile: String?,
        adminEmail: String?, password: String?, tval: String?
    ): Observable<ResponseBody?>? {
        val sname = "CommonServices"
        val fname = "addClientAndroid"
        val isweb = 0
        return covidService.registerClient(
            url,
            subdomain,
            companyName,
            companyAddress,
            companyCityId,
            companyStateId,
            companyCountryId,
            adminName,
            adminMobile,
            adminEmail,
            password,
            sname,
            fname,
            tval,
            isweb
        )
    }

    fun registerDevice(
        url: String?, userId: Int, deviceId: String?,
        fcmToken: String?, tval: String?
    ): Single<ResponseBody?>? {
        val sname = "ProfileService"
        val fname = "registerDeviceDetail"
        val isweb = 0
        val userType = "ADMIN" // ADMIN|CUSTOMER|SUPPLIER
        return covidService.registerDevice(
            url,
            userId,
            userType,
            deviceId,
            fcmToken,
            sname,
            fname,
            tval,
            isweb
        )
    }

}