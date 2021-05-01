package in.engineerakash.covid19india.api;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface CovidService {

    @GET("/data.json")
    Single<ResponseBody> getTimeSeriesAndStateWiseData();

    @GET("/state_district_wise.json")
    Single<ResponseBody> getStateDistrictWiseData();

    @POST
    @FormUrlEncoded
    @Streaming
    Observable<Response<ResponseBody>> getInvoiceFile(@Url String url, @Field("data") String postData,
                                                      @Field("device_type") String deviceType);

    @POST
    @FormUrlEncoded
    Observable<Response<ResponseBody>> emailInvoiceFile(@Url String url, @Field("data") String postData,
                                                        @Field("device_type") String deviceType);

    @POST
    @FormUrlEncoded
    Single<ResponseBody> login(@Url String url, @Field("client_name") String clientName,
                               @Field("username") String username, @Field("password") String password,
                               @Field("sname") String sname, @Field("fname") String fname, @Field("tval") String tval,
                               @Field("isweb") int isweb);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> sendResetPasswordLink(@Url String url, @Field("client_name") String clientName,
                                                   @Field("username_email") String username, @Field("sname") String sname,
                                                   @Field("fname") String fname, @Field("tval") String tval,
                                                   @Field("isweb") int isweb);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> checkIfEmailMobExist(@Url String url, @Field("email") String email,
                                                  @Field("mobile") String mobile,
                                                  @Field("sname") String sname, @Field("fname") String fname,
                                                  @Field("tval") String tval, @Field("isweb") int isweb);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> getStates(@Url String url, @Field("country_id") int countryId,
                                       @Field("sname") String sname, @Field("fname") String fname,
                                       @Field("tval") String tval, @Field("isweb") int isweb);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> getCities(@Url String url, @Field("state_id") int stateId,
                                       @Field("sname") String sname, @Field("fname") String fname,
                                       @Field("tval") String tval, @Field("isweb") int isweb);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> getSubdomainAvailability(@Url String url, @Field("subdomain") String subdomain,
                                                      @Field("sname") String sname, @Field("fname") String fname,
                                                      @Field("tval") String tval, @Field("isweb") int isweb);

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> registerClient(@Url String url, @Field("subdomain") String subdomain,
                                            @Field("company_name") String companyName,
                                            @Field("company_address") String companyAddress,
                                            @Field("company_city") int companyCityId,
                                            @Field("company_state") int companyStateId,
                                            @Field("company_country") int companyCountryId,
                                            @Field("name") String adminName,
                                            @Field("contact_number") String adminContactNumber,
                                            @Field("email") String adminEmail,
                                            @Field("password") String password,
                                            @Field("sname") String sname, @Field("fname") String fname,
                                            @Field("tval") String tval, @Field("isweb") int isweb);

    @POST
    @FormUrlEncoded
    Single<ResponseBody> registerDevice(@Url String url, @Field("user_id") int userId,
                                        @Field("user_type") String userType, @Field("device_id") String deviceId,
                                        @Field("fcm_token") String fcmToken, @Field("sname") String sname,
                                        @Field("fname") String fname, @Field("tval") String tval,
                                        @Field("isweb") int isweb);
}