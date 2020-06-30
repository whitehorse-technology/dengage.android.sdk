package com.dengage.sdk;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FcmMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        DengageManager.getInstance(getApplicationContext()).onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        DengageManager.getInstance(getApplicationContext()).onMessageReceived(data);
    }
}
