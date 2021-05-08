package `in`.engineerakash.covid19india.api

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface CovidService {
    @get:GET("/data.json")
    val timeSeriesAndStateWiseData: Single<ResponseBody?>?

    @get:GET("/state_district_wise.json")
    val stateDistrictWiseData: Single<ResponseBody?>?

    @POST
    @FormUrlEncoded
    @Streaming
    fun getInvoiceFile(
        @Url url: String?, @Field("data") postData: String?,
        @Field("device_type") deviceType: String?
    ): Observable<Response<ResponseBody?>?>?

    @POST
    @FormUrlEncoded
    fun emailInvoiceFile(
        @Url url: String?, @Field("data") postData: String?,
        @Field("device_type") deviceType: String?
    ): Observable<Response<ResponseBody?>?>?

    @POST
    @FormUrlEncoded
    fun login(
        @Url url: String?,
        @Field("client_name") clientName: String?,
        @Field("username") username: String?,
        @Field("password") password: String?,
        @Field("sname") sname: String?,
        @Field("fname") fname: String?,
        @Field("tval") tval: String?,
        @Field("isweb") isweb: Int
    ): Single<ResponseBody?>?

    @POST
    @FormUrlEncoded
    fun sendResetPasswordLink(
        @Url url: String?, @Field("client_name") clientName: String?,
        @Field("username_email") username: String?, @Field("sname") sname: String?,
        @Field("fname") fname: String?, @Field("tval") tval: String?,
        @Field("isweb") isweb: Int
    ): Observable<ResponseBody?>?

    @POST
    @FormUrlEncoded
    fun checkIfEmailMobExist(
        @Url url: String?, @Field("email") email: String?,
        @Field("mobile") mobile: String?,
        @Field("sname") sname: String?, @Field("fname") fname: String?,
        @Field("tval") tval: String?, @Field("isweb") isweb: Int
    ): Observable<ResponseBody?>?

    @POST
    @FormUrlEncoded
    fun getStates(
        @Url url: String?, @Field("country_id") countryId: Int,
        @Field("sname") sname: String?, @Field("fname") fname: String?,
        @Field("tval") tval: String?, @Field("isweb") isweb: Int
    ): Observable<ResponseBody?>?

    @POST
    @FormUrlEncoded
    fun getCities(
        @Url url: String?, @Field("state_id") stateId: Int,
        @Field("sname") sname: String?, @Field("fname") fname: String?,
        @Field("tval") tval: String?, @Field("isweb") isweb: Int
    ): Observable<ResponseBody?>?

    @POST
    @FormUrlEncoded
    fun getSubdomainAvailability(
        @Url url: String?, @Field("subdomain") subdomain: String?,
        @Field("sname") sname: String?, @Field("fname") fname: String?,
        @Field("tval") tval: String?, @Field("isweb") isweb: Int
    ): Observable<ResponseBody?>?

    @POST
    @FormUrlEncoded
    fun registerClient(
        @Url url: String?, @Field("subdomain") subdomain: String?,
        @Field("company_name") companyName: String?,
        @Field("company_address") companyAddress: String?,
        @Field("company_city") companyCityId: Int,
        @Field("company_state") companyStateId: Int,
        @Field("company_country") companyCountryId: Int,
        @Field("name") adminName: String?,
        @Field("contact_number") adminContactNumber: String?,
        @Field("email") adminEmail: String?,
        @Field("password") password: String?,
        @Field("sname") sname: String?, @Field("fname") fname: String?,
        @Field("tval") tval: String?, @Field("isweb") isweb: Int
    ): Observable<ResponseBody?>?

    @POST
    @FormUrlEncoded
    fun registerDevice(
        @Url url: String?, @Field("user_id") userId: Int,
        @Field("user_type") userType: String?, @Field("device_id") deviceId: String?,
        @Field("fcm_token") fcmToken: String?, @Field("sname") sname: String?,
        @Field("fname") fname: String?, @Field("tval") tval: String?,
        @Field("isweb") isweb: Int
    ): Single<ResponseBody?>?
}