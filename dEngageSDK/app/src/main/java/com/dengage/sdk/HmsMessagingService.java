package com.dengage.sdk;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import java.util.Map;

public class HmsMessagingService extends HmsMessageService {

    @Override
    public void onNewToken(String token) {
        DengageManager.getInstance(getApplicationContext()).onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getDataOfMap();
        if (Utils.showDengageNotification(data))
            DengageManager.getInstance(getApplicationContext()).onMessageReceived(data);
    }
}