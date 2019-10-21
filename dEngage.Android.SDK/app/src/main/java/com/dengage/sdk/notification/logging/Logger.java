package com.dengage.sdk.notification.logging;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.dengage.sdk.BuildConfig;
import com.dengage.sdk.notification.Constants;
import com.dengage.sdk.notification.dEngageMobileManager;

public class Logger {

    private static Boolean isDebug = true;

    public Logger(Context context) {
        this.isDebug = ( BuildConfig.DEBUG || (context.getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    public static void Debug(String message) {
        if (isDebug) {
            Log.d(Constants.LOG_TAG, message);
        }
    }

    public static void Error(String message) {
        if (isDebug) {
            Log.e(Constants.LOG_TAG, message);
        }
    }

    public static void Warn(String message) {
        if (isDebug) {
            Log.w(Constants.LOG_TAG, message);
        }
    }

    public static void Info(String message) {
        if (isDebug) {
            Log.i(Constants.LOG_TAG, message);
        }
    }

    public static void Verbose(String message) {
        if (isDebug) {
            Log.v(Constants.LOG_TAG, message);
        }
    }
}
