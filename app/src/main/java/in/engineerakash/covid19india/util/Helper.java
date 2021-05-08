package in.engineerakash.covid19india.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.engineerakash.covid19india.BuildConfig;
import in.engineerakash.covid19india.R;
import in.engineerakash.covid19india.enums.ChartType;

import static in.engineerakash.covid19india.util.Constant.NO_EMAIL_CLIENT_MSG;

public class Helper {
    private static final String TAG = "Helper";
    private static AlertDialog progressDialog = null;

    public static void emailSupport(Context context) {
        String subject = "Billin App Support";

        String instruction = "-- Below Information will help us to give you better support. --";
        String deviceId = "\nDevice ID : " + getAndroidId(context);
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        String display = "\nDisplay: " + dm.widthPixels + " X " + dm.heightPixels;
        String androidVersion = "\nAndroid Version: " + Build.VERSION.RELEASE;
        String product = "\nProduct Name : " + Build.PRODUCT;
        String versionName = "\nApp Version : " + BuildConfig.VERSION_NAME + "\n\n";

        String body = "\n\n\n\n\n" + instruction + deviceId + display + androidVersion + product + versionName;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constant.SUPPORT_EMAIL});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(emailIntent, "Reach to Billin support..."));

    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void waSupport(Context context) {
       /* String phone = context.getString(R.string.wa_support_number);
        String message = context.getString(R.string.wa_support_default_msg);

        if (AppUtil.appInstalledOrNot(context, "com.whatsapp")) {
            //whatsapp is installed

            String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + message;
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(i);
        } else {

            //whatsapp is not installed, Send SMS
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
            intent.putExtra("sms_body", message);
            context.startActivity(intent);

        }*/
    }

    public static void phoneSupport(Context context) {

        String phone = Constant.SUPPORT_PHONE;

        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(callIntent);

    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static String encodeToBase64(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        String encodeString = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encodeString;
    }

    public static String decodeFromBase64(String data) {
        String decodedStr = "";

        try {
            byte[] bytes = Base64.decode(data.getBytes(), Base64.DEFAULT);
            decodedStr = new String(bytes, StandardCharsets.UTF_8);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return decodedStr;
    }

    public static void showErr(Context context) {
        showErr(context, "Something Went Wrong while processing your request");
    }

    public static void showErr(Context context, String err) {
        if (context != null)
            Toast.makeText(context, err, Toast.LENGTH_LONG).show();
    }

    public static void showSuccess(Context context, String msg) {
        if (context != null)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showProgressDialog(Context context) {
        showProgressDialog(context, null, false, 0);
    }

    public static void showProgressDialog(Context context, String text) {
        showProgressDialog(context, text, false, 0);
    }

    public static void showProgressDialog(Context context, String text, boolean isCancelable) {
        showProgressDialog(context, text, isCancelable, 0);
    }

    public static void showProgressDialog(Context context, String text, boolean isCancelable,
                                          final int autoCloseAfterInMilli) {
        /*if (context == null)
            return;
        if (progressDialog == null)
            progressDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.ProgressDialogTheme)).create();

        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, null);
        if (!TextUtils.isEmpty(text))
            ((TextView) dialogLayout.findViewById(R.id.progress_dialog_text_view)).setText(text);
        progressDialog.setCancelable(isCancelable);
        progressDialog.setView(dialogLayout);
        progressDialog.show();

        if (autoCloseAfterInMilli > 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, autoCloseAfterInMilli);*/
    }

    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static String getTVal(Context context) {
        String tval = "";
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences("client_sp", Context.MODE_PRIVATE);
            tval = sp.getString("tval", "");
        }
        return tval;
    }

    public static void showMsg(Context context, String msg) {
        if (context == null)
            return;
        Toast
                .makeText(context, msg, Toast.LENGTH_LONG)
                .show();
    }

    public static void showNotInternetMsg(Context context) {
        showMsg(context, "Internet is Not available.");
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void whatsapp(Context context, String msg) {
        whatsapp(context, msg, "");
    }

    public static void whatsapp(Context context, String msg, String mobile) {

        if (!appInstalledOrNot(context, "com.whatsapp")) {
            showMsg(context, "Whatsapp is not Installed");
            return;
        }

        String formattedNumber = mobile.replaceAll("[\\s+-]", "");
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
            if (!TextUtils.isEmpty(formattedNumber)) {
                sendIntent.putExtra("jid", formattedNumber + "@s.whatsapp.net");
                //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            }
            sendIntent.setPackage("com.whatsapp");

            if (sendIntent.resolveActivity(context.getPackageManager()) == null) {
                showMsg(context, "Failed to send whatsapp");
                return;
            }
            context.startActivity(sendIntent);
        } catch (Exception e) {
            showMsg(context, "Failed to send whatsapp");
        }
    }

    public static String sharingUrl(Context context) {
        return "https://play.google.com/store/apps/details?id=" + context.getPackageName();
    }

    public static void shareApp(Context context) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Billin is a complete solution for your business. It involves handling multiple" +
                        " branches, employees, sale and purchase record tracking, report generation and " +
                        "much more. \n\nAnd after all it is completely free for a month. \n\nGrow your business with billin now!\n"
                        + sharingUrl(context));
        shareIntent.setType("text/plain");
        context.startActivity(shareIntent);
    }

    public static boolean isSelfUrl(String url) {
        Uri uri = Uri.parse(Constant.DOMAIN);

        if (uri.getHost() != null && url != null) {
            return url.toLowerCase().contains(uri.getHost().toLowerCase());
        }
        return false;
    }

    public static void handlePhoneCallLink(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse(url));
        context.startActivity(intent);
    }

    public static void handleMailToLink(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        // For only email app handle this intent
        intent.setData(Uri.parse("mailto:"));

        // Extract the email address from mailto url
        String to = url.split("[:?]")[1];
        if (!TextUtils.isEmpty(to)) {
            String[] toArray = to.split(";");
            // Put the primary email addresses array into intent
            intent.putExtra(Intent.EXTRA_EMAIL, toArray);
        }

        // Extract the cc
        if (url.contains("cc=")) {
            String cc = url.split("cc=")[1];
            if (!TextUtils.isEmpty(cc)) {
                //cc = cc.split("&")[0];
                cc = cc.split("&")[0];
                String[] ccArray = cc.split(";");
                // Put the cc email addresses array into intent
                intent.putExtra(Intent.EXTRA_CC, ccArray);
            }
        }

        // Extract the bcc
        if (url.contains("bcc=")) {
            String bcc = url.split("bcc=")[1];
            if (!TextUtils.isEmpty(bcc)) {
                //cc = cc.split("&")[0];
                bcc = bcc.split("&")[0];
                String[] bccArray = bcc.split(";");
                // Put the bcc email addresses array into intent
                intent.putExtra(Intent.EXTRA_BCC, bccArray);
            }
        }

        // Extract the subject
        if (url.contains("subject=")) {
            String subject = url.split("subject=")[1];
            if (!TextUtils.isEmpty(subject)) {
                subject = subject.split("&")[0];
                // Encode the subject
                try {
                    subject = URLDecoder.decode(subject, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // Put the mail subject into intent
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
        }

        // Extract the body
        if (url.contains("body=")) {
            String body = url.split("body=")[1];
            if (!TextUtils.isEmpty(body)) {
                body = body.split("&")[0];
                // Encode the body text
                try {
                    body = URLDecoder.decode(body, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // Put the mail body into intent
                intent.putExtra(Intent.EXTRA_TEXT, body);
            }
        } else {
        }

        // Email address not null or empty
        if (!TextUtils.isEmpty(to)) {
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                // Finally, open the mail client activity
                context.startActivity(intent);
            } else {
                Toast.makeText(context, NO_EMAIL_CLIENT_MSG, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static void handleWhatsapp(Context context, String url) {
        Log.d(TAG, "handleWhatsapp: " + url);
        try {
            Intent intent;
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

            if (intent.resolveActivity(context.getPackageManager()) != null)
                context.startActivity(intent);
            else {
                Toast.makeText(context, "App is not installed to handle this request", Toast.LENGTH_SHORT).show();
            }

        } catch (URISyntaxException use) {
            Log.e(TAG, use.getMessage());
        }
    }

    /**
     * Input : 15/04/2020 01:10:24<br>
     * Output: 15 Apr 2020 1:10 AM
     */
    public static String parseDate(String inputTimeStamp) {
        String outputTimeStamp = inputTimeStamp;

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("d MMM yyyy h:mm a", Locale.getDefault());
        try {
            Date inputDateParsed = inputDateFormat.parse(inputTimeStamp);

            if (inputDateParsed != null)
                outputTimeStamp = outputDateFormat.format(inputDateParsed);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputTimeStamp;
    }

    public static int getBarChartColor(Context context, ChartType chartType) {
        switch (chartType) {
            case TOTAL_CONFIRMED:
            case DAILY_CONFIRMED:
                return ContextCompat.getColor(context, R.color.confirmedCasesColor);
            case TOTAL_RECOVERED:
            case DAILY_RECOVERED:
                return ContextCompat.getColor(context, R.color.recoveredCasesColor);
            case TOTAL_DECEASED:
            case DAILY_DECEASED:
                return ContextCompat.getColor(context, R.color.deathCasesColor);
            default:
                return ContextCompat.getColor(context, R.color.colorAccent);
        }
    }
}
