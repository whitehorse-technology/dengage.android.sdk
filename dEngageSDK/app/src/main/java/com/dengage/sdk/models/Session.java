package com.dengage.sdk.models;

import android.content.Context;

import com.dengage.sdk.Utils;
import com.dengage.sdk.cache.Prefs;

import java.util.Calendar;
import java.util.Date;

public class Session {

    private static Session instance = null;
    private static Prefs prefs;

    public static Session createSession() {
        return instance = new Session();
    }

    public static Session getSession(Context ctx) {
        prefs = new Prefs(ctx);
        if (instance == null) return createSession();
        return instance;
    }


    private Date expiresIn = getNextExpiredTime();

    public Date getNextExpiredTime() {
        return new Date(prefs.getAppSessionTime());
    }

    public void extend() {
        this.expiresIn = Utils.getCurrentDateObject();
        prefs.setAppSessionTime(expiresIn.getTime());


    }

    public boolean isExpired() {
        int diff = Calendar.getInstance().getTime().compareTo(this.expiresIn);
        return diff > 0;
    }

    public String getSessionId() {
        String sessionId = "";
        if (isExpired()) {
            sessionId = Utils.generateSessionId();
            prefs.setAppSessionId(sessionId);
            extend();
        } else {
            sessionId = prefs.getAppSessionId();
            prefs.setAppSessionId(sessionId);
        }
        return sessionId;
    }
}
