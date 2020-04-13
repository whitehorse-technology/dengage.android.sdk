package com.dengage.sdk;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.dengage.sdk.models.Session;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

class Utils {

    private static String installationID = null;

    public static int getScreenWith(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        if(manager!=null) {
            manager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        } else {
            return -1;
        }
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        if(manager!=null) {
            manager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        } else {
            return -1;
        }
    }

    public static String getAppLanguage(){
        return Locale.getDefault().getDisplayLanguage();
    }

    public static String getSystemLanguage() {
        return Resources.getSystem().getConfiguration().locale.getLanguage();
    }

    public static int getTimezoneId() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        return tz.getRawOffset();
    }

    public static String getSdkVersion() {
        return com.dengage.sdk.BuildConfig.VERSION_NAME;
    }

    public static String getOsVersion() {
        return "Android " + Build.VERSION.RELEASE;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getDeviceUniqueId() {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        String serial;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial";
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    synchronized static String getDeviceId(Context context) {
        if (installationID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.DEN_DEVICE_UNIQUE_ID, Context.MODE_PRIVATE);
            installationID = sharedPrefs.getString(Constants.DEN_DEVICE_UNIQUE_ID, null);
                if (installationID == null) {
                    installationID = UUID.randomUUID().toString();
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(Constants.DEN_DEVICE_UNIQUE_ID, installationID);
                    editor.apply();
                }
        }
        return installationID;
    }

    static void saveSubscription(Context context, String value) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(Constants.SUBSCRIPTION_KEY, value);
        spEditor.apply();
    }

    static boolean hasSubscription(Context context) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        return sp.contains(Constants.SUBSCRIPTION_KEY);
    }

    static String getSubscription(Context context) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        return sp.getString(Constants.SUBSCRIPTION_KEY, "");
    }

    static String appVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception ignored) {
        }
        return null;
    }

    static String osVersion() {
        return Build.VERSION.RELEASE;
    }

    static String osType() {
        return "Android";
    }

    static String carrier(Context context) {
        String carrier = "";
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            assert manager != null;
            manager.getNetworkOperator();
        } catch (Exception ignored) {
        }
        return carrier;
    }

    static String local(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    static String deviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    static String getUserAgent(Context context) {
        return Utils.getAppLabel(context, "An Android App") + "/"+ Utils.appVersion(context) + " "+ Build.MANUFACTURER +"/"+ Build.MODEL +" "+ System.getProperty("http.agent") +" Mobile/"+ Build.ID +"";
    }

    static String deviceType() {
        return android.os.Build.MANUFACTURER + " : " + android.os.Build.MODEL;
    }

    static String getAppLabel(Context pContext, String defaultText) {
        PackageManager lPackageManager = pContext.getPackageManager();
        ApplicationInfo lApplicationInfo = null;
        try {
            lApplicationInfo = lPackageManager.getApplicationInfo(pContext.getApplicationInfo().packageName, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return (String) (lApplicationInfo != null ? lPackageManager.getApplicationLabel(lApplicationInfo) : defaultText);
    }

    public static Uri getSound(Context context, String sound) {
        int id = context.getResources().getIdentifier(sound, "raw", context.getPackageName());
        if (id != 0) {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + id);
        }else{
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
    }
}