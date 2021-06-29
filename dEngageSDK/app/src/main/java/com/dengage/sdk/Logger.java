package com.dengage.sdk;

import android.util.Log;

public class Logger {

    private Boolean status = false;

    private static Logger _instance = null;

    public static Logger getInstance() {
        if(_instance == null) _instance = new Logger();
        return _instance;
    }

    void setLogStatus(Boolean status) {
        this.status = status;
    }

    void Debug(String message) {
        if (status) {
            Log.d(Constants.LOG_TAG, message);
        }
    }

    public void Error(String message) {
        if (status) {
            Log.e(Constants.LOG_TAG, message);
        }
    }

    void Warn(String message) {
        if (status) {
            Log.w(Constants.LOG_TAG, message);
        }
    }

    void Info(String message) {
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
