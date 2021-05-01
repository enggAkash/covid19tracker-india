package in.engineerakash.covid19india.util;

public final class Constant {

    //    public static final String DOMAIN = "https://billin.in/";
    public static final String DOMAIN = "http://192.168.0.110/billin_website/index.php";

    public static final String SUPPORT_EMAIL = "support@billin.in";
    public static final String SUPPORT_PHONE = "+91 9999361953";
    //    public static final String API_ENDPOINT = "https://api.billin.in/api/index.php";
    public static final String API_ENDPOINT = "http://192.168.0.110/billin_api/api/index.php";

    public static final String[] PARAM_KEYS = {"device_type"};
    public static final String[] PARAM_VALUES = {"android"};

    public static final String NO_EMAIL_CLIENT_MSG = "No email client found.";


    /*will show top most affected states(which has large number of confirmed cases*/
    public static final int MOST_AFFECTED_STATES_COUNT = 5;

    /*will show top most affected district(which has large number of confirmed cases*/
    public static final int MOST_AFFECTED_DISTRICT_COUNT = 5;

    /*will show last 3 date data*/
    public static final int BAR_CHART_DATE_COUNT = 3;

    public static String userSelectedCountry = "India";
    public static String userSelectedState = "Maharashtra";//""Delhi";
    public static String userSelectedDistrict = "Mumbai  ";//""East Delhi";
    public static boolean isUserSelected = true;

    public enum ChartType {
        TOTAL_CONFIRMED, TOTAL_RECOVERED, TOTAL_DECEASED, DAILY_CONFIRMED, DAILY_RECOVERED, DAILY_DECEASED
    }
}
