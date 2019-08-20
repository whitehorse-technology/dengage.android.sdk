package com.dengage.sdk.notification.logging;

import android.util.Log;
import com.dengage.sdk.BuildConfig;
import com.dengage.sdk.notification.Constants;

public class Logger {

    public static void Debug(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(Constants.LOG_TAG, message);
        }
    }

    public static void Error(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(Constants.LOG_TAG, message);
        }
    }

    public static void Warn(String message) {
        if (BuildConfig.DEBUG) {
            Log.w(Constants.LOG_TAG, message);
        }
    }

    public static void Info(String message) {
        if (BuildConfig.DEBUG) {
            Log.i(Constants.LOG_TAG, message);
        }
    }

    public static void Verbose(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(Constants.LOG_TAG, message);
        }
    }
}
