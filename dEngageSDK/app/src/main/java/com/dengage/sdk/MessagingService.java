package com.dengage.sdk;

import android.content.Context;
import android.content.Intent;

import com.dengage.sdk.models.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;


public class MessagingService extends FirebaseMessagingService {

    private Logger logger = Logger.getInstance();

    @Override
    public void onNewToken(String token) {
        try {
            logger.Debug("On new token : " + token);
            DengageManager.getInstance(getApplicationContext()).subscribe(token);
        } catch (Exception e) {
            logger.Error("onNewToken: "+ e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        logger.Verbose("onMessageReceived method is called.");

        Map<String, String> data = remoteMessage.getData();
        if( (data != null && data.size() > 0)) {
            Message pushMessage = new Message(data);
            String json = pushMessage.toJson();
            logger.Verbose("Message Json: "+ json);
            String source = pushMessage.getMessageSource();
            if (Constants.MESSAGE_SOURCE.equals(source)) {
                logger.Debug("There is a message that received from dEngage");
                sendBroadcast(json, data);
            }
        }
    }

    private void sendBroadcast(String json, Map<String, String> data) {
        logger.Verbose("generateNotification method is called.");
        try {
            Context context = getApplicationContext();
            Intent intent = new Intent(NotificationReceiver.PUSH_RECEIVE);
            intent.putExtra("RAW_DATA", json);
            logger.Verbose("RAW_DATA: "+ json);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
            intent.setPackage(context.getPackageName());
            context.sendBroadcast(intent);
        } catch (Exception e) {
            logger.Error("generateNotification: " + e.getMessage());
        }
    }
}