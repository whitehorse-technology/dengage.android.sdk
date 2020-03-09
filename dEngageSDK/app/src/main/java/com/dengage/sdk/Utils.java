package com.dengage.sdk;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import java.util.UUID;

class Utils {

    private static String uniqueID = null;

    synchronized static String udid(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.DEN_DEVICE_UNIQUE_ID, Context.MODE_PRIVATE);
                uniqueID = sharedPrefs.getString(Constants.DEN_DEVICE_UNIQUE_ID, null);
                if (uniqueID == null) {
                    uniqueID = UUID.randomUUID().toString();
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(Constants.DEN_DEVICE_UNIQUE_ID, uniqueID);
                    editor.apply();
                }
        }
        return uniqueID;
    }

    static String savePrefString(Context context, String key, String value) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(key, value);
        spEditor.apply();
        return value;
    }

    static boolean hasPrefString(Context context, String key) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName,
                Context.MODE_PRIVATE);
        boolean res = sp.contains(key);
        return res;
    }

    static String getPrefString(Context context, String key) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    static String appVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
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
            manager.getNetworkOperator();
        } catch (Exception ex) {
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
        String userAgent = Utils.getAppLabel(context, "An Android App") + "/"+ Utils.appVersion(context) + " "+ Build.MANUFACTURER +"/"+ Build.MODEL +" "+ System.getProperty("http.agent") +" Mobile/"+ Build.ID +"";
        return userAgent;
    }

    static String deviceType(Context context) {
        return android.os.Build.MANUFACTURER + " : " + android.os.Build.MODEL;
    }

    static String getAppLabel(Context pContext, String defaultText) {
        PackageManager lPackageManager = pContext.getPackageManager();
        ApplicationInfo lApplicationInfo = null;
        try {
            lApplicationInfo = lPackageManager.getApplicationInfo(pContext.getApplicationInfo().packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return (String) (lApplicationInfo != null ? lPackageManager.getApplicationLabel(lApplicationInfo) : defaultText);
    }

}