package com.dengage.sdk.models;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Session {

    private static Session instance = null;

    public static Session createSession() {
        return instance = new Session();
    }

    public static Session getSession() {
        if (instance == null) return createSession();
        return instance;
    }

    private String sessionId = UUID.randomUUID().toString().toLowerCase();
    private Date expiresIn = getNextExpiredTime();

    public Date getNextExpiredTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);
        return cal.getTime();
    }

    public void extend() {
        this.expiresIn = getNextExpiredTime();
    }

    public boolean isExpired() {
        int diff = Calendar.getInstance().getTime().compareTo(this.expiresIn);
        return diff > 0;
    }

    public String getSessionId() {
        if(isExpired()) extend();
        return this.sessionId;
    }
}