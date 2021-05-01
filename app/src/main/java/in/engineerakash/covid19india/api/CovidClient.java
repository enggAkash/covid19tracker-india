package in.engineerakash.covid19india.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import in.engineerakash.covid19india.BuildConfig;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class CovidClient {
    private static final String TAG = "CovidClient";

    private static final String BILLIN_BASE_URL = "https://api.covid19india.org/";

    private static CovidClient instance;
    private static CovidService covidService;

    private CovidClient() {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        okHttpClient.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(BILLIN_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient.build())
                .build();

        covidService = retrofit.create(CovidService.class);
    }

    public static CovidClient getInstance() {
        if (instance == null)
            instance = new CovidClient();
        return instance;
    }

    public Single<ResponseBody> getTimeSeriesAndStateWiseData() {
        return covidService.getTimeSeriesAndStateWiseData();
    }

    public Single<ResponseBody> getStateDistrictWiseData() {
        return covidService.getStateDistrictWiseData();
    }

    public Observable<Response<ResponseBody>> getInvoiceBill(String url, String postData) {
        return covidService.getInvoiceFile(url, postData, "ANDROID");
    }

    public Observable<Response<ResponseBody>> emailInvoiceBill(String url, String postData) {
        return covidService.emailInvoiceFile(url, postData, "ANDROID");
    }

    public Single<ResponseBody> login(String url, String clientName, String username, String password, String tval) {
        String sname = "LoginRegister";
        String fname = "androidLogin";
        int isweb = 0;
        return covidService.login(url, clientName, username, password, sname, fname, tval, isweb);
    }

    public Observable<ResponseBody> sendPasswordResetLink(String url, String clientName, String username, String tval) {
        String sname = "LoginRegister";
        String fname = "sendResetPasswordLinkAndroid";
        int isweb = 0;
        return covidService.sendResetPasswordLink(url, clientName, username, sname, fname, tval, isweb);
    }

    public Observable<ResponseBody> checkIfClientEmailMobExist(String url, String email, String mobile, String tval) {
        String sname = "CommonServices";
        String fname = "checkClientEmailMob";
        int isweb = 0;
        return covidService.checkIfEmailMobExist(url, email, mobile, sname, fname, tval, isweb);
    }

    public Observable<ResponseBody> getStates(String url, int countryId, String tval) {
        String sname = "CommonServices";
        String fname = "getStates";
        int isweb = 0;
        return covidService.getStates(url, countryId, sname, fname, tval, isweb);
    }

    public Observable<ResponseBody> getCities(String url, int stateId, String tval) {
        String sname = "CommonServices";
        String fname = "getCities";
        int isweb = 0;
        return covidService.getCities(url, stateId, sname, fname, tval, isweb);
    }

    public Observable<ResponseBody> getSubdomainAvailability(String url, String subdomain, String tval) {
        String sname = "CommonServices";
        String fname = "getSubdomainAvailibility";
        int isweb = 0;
        return covidService.getSubdomainAvailability(url, subdomain, sname, fname, tval, isweb);
    }

    public Observable<ResponseBody> registerClient(String url, String subdomain, String companyName,
                                                   String companyAddress, int companyCityId, int companyStateId,
                                                   int companyCountryId, String adminName, String adminMobile,
                                                   String adminEmail, String password, String tval) {
        String sname = "CommonServices";
        String fname = "addClientAndroid";
        int isweb = 0;

        return covidService.registerClient(url, subdomain, companyName, companyAddress, companyCityId, companyStateId,
                companyCountryId, adminName, adminMobile, adminEmail, password, sname, fname, tval, isweb);
    }

    public Single<ResponseBody> registerDevice(String url, int userId, String deviceId,
                                               String fcmToken, String tval) {
        String sname = "ProfileService";
        String fname = "registerDeviceDetail";
        int isweb = 0;
        String userType = "ADMIN"; // ADMIN|CUSTOMER|SUPPLIER

        return covidService.registerDevice(url, userId, userType, deviceId, fcmToken, sname, fname, tval, isweb);
    }
}