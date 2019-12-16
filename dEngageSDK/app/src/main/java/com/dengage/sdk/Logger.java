package com.dengage.sdk;

import android.util.Log;

public class Logger {

    private Boolean status = false;

    public static Logger INSTANCE = new Logger();

    public void setLogStatus(Boolean status) {
        this.status = status;
    }

    public void Debug(String message) {
        if (status) {
            Log.d(Constants.LOG_TAG, message);
        }
    }

    public void Error(String message) {
        if (status) {
            Log.e(Constants.LOG_TAG, message);
        }
    }

    public void Warn(String message) {
        if (status) {
            Log.w(Constants.LOG_TAG, message);
        }
    }

    public void Info(String message) {
        if (status) {
            Log.i(Constants.LOG_TAG, message);
        }
    }

    public void Verbose(String message) {
        if (status) {
            Log.v(Constants.LOG_TAG, message);
        }
    }
}
