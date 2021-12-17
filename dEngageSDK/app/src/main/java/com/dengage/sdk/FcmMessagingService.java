package com.dengage.sdk;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Map;

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
       // DengageManager.getInstance(getApplicationContext()).onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        if (Utils.showDengageNotification(data))
            DengageManager.getInstance(getApplicationContext()).onMessageReceived(data);
    }
}
