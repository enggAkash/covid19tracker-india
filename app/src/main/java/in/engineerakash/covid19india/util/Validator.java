package in.engineerakash.covid19india.util;

import android.net.Uri;

import java.util.regex.Pattern;

public final class Validator {

    public static boolean isValidName(String name) {
        String nameRegex = "^[a-zA-Z\\s]{1,50}$";
        return Pattern.compile(nameRegex).matcher(name.trim()).matches();
    }

    public static boolean isValidClientName(String clientName) {
        String domainRegex = "^(?!-)(?!_)([a-zA-Z\\d\\-_]{0,61})[a-zA-Z\\d]$";
        return Pattern.compile(domainRegex).matcher(clientName).matches();
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public static boolean isValidMobile(String mobile) {
        String mobileRegex = "^[6|7|8|9]\\d{9}$";
        return Pattern.compile(mobileRegex).matcher(mobile).matches();
    }

    public static boolean isValidSignupPassword(String password) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z@_\\-!\\d]{8,}$";
        return Pattern.compile(passwordRegex).matcher(password).matches();
    }

    public static boolean isValidCompanyName(String companyName) {
        return companyName.length() >= 2;
    }

    public static boolean isValidCompanyAddress(String companyAddress) {
        return companyAddress.length() >= 10;
    }

    public static boolean isValidUsername(String username) {
        return username.length() >= 6;
    }

    public static boolean isValidPassword(String password) {
        return password.length() > 6;
    }

    public static boolean isBillinDomain(String url) {
        return url.toLowerCase().contains(".billin.in");
//        return url.toLowerCase().contains("192.168");
    }

    public static boolean isBillinDomain(Uri uri) {
        return uri.getHost() != null && uri.getHost().equalsIgnoreCase("billin");
    }

}
