package com.dengage.sdk.notification.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import com.dengage.sdk.notification.Constants;
import com.dengage.sdk.notification.dEngageMobileManager;
import com.dengage.sdk.notification.helpers.ChannelHelper;
import com.dengage.sdk.notification.helpers.RequestHelper;
import com.dengage.sdk.notification.helpers.Utils;
import com.dengage.sdk.notification.logging.Logger;
import com.dengage.sdk.notification.models.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Set;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        try {
            Logger.Debug("On new token : " + token);
            dEngageMobileManager.getInstance().subscribe(token);
        } catch (Exception e) {
            Logger.Error("onNewToken: "+ e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Logger.Debug("onMessageReceived is called");

        Map<String, String> data = remoteMessage.getData();
        Message pushMessage = new Message(data);
        
        Logger.Debug("onMessageReceived : " + pushMessage.getMessage());

        if (!TextUtils.isEmpty(pushMessage.getMessage())) {
            Logger.Verbose("Generating notification");
            generateNotification(pushMessage, data, RequestHelper.getInstance().getBitmap(pushMessage.getMediaUrl()));
        } else {
            Logger.Error("onMessageReceived: Message is empty!");
        }
    }

    private void generateNotification(Message pushMessage, Map<String, String> data, Bitmap image) {
        Logger.Verbose("generateNotification method is called.");
        try {
            String notificationChannelId = Constants.CHANNEL_ID;

            if(pushMessage.getSound() != null){
                notificationChannelId += pushMessage.getSound();
            }

            Logger.Debug("Channel Id: "+ notificationChannelId);

            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
                //Starting with O notification must have a notification channel to work properly
                ChannelHelper.createNotificationChannel(mNotificationManager, pushMessage.getSound(), pushMessage.getBadge(), getApplicationContext(),notificationChannelId);
            }

            PackageManager packageManager = this.getPackageManager();

            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);

            final int appIconResId = applicationInfo.icon;

            Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());

            ComponentName componentName = intent.getComponent();

            Intent notificationIntent = Intent.makeRestartActivityTask(componentName);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            Set<Map.Entry<String, String>> entrySet = data.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                notificationIntent.putExtra(entry.getKey(), entry.getValue());
            }

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Style style = image == null ?
                    new NotificationCompat.BigTextStyle()
                            .bigText(pushMessage.getMessage()) :
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(image)
                            .bigLargeIcon(null)
                            .setSummaryText(pushMessage.getMessage());

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, notificationChannelId)
                    .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                    .setSmallIcon(appIconResId)
                    .setAutoCancel(true)
                    .setLargeIcon(image)
                    .setStyle(style)
                    .setContentTitle(Utils.getAppLabel(getApplicationContext(), ""))
                    .setContentText(pushMessage.getMessage());

            if(pushMessage.getTitle() != null && !TextUtils.isEmpty(pushMessage.getTitle())) {
                mBuilder.setContentTitle(pushMessage.getTitle());
            }

            if(pushMessage.getSubTitle() != null & !TextUtils.isEmpty(pushMessage.getSubTitle())) {
                mBuilder.setSubText(pushMessage.getSubTitle());
            }

            if(pushMessage.getBadge() && pushMessage.getBadgeCount() > 0){
                mBuilder.setNumber(pushMessage.getBadgeCount());
            }

            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            if(pushMessage.getSound() != null & !TextUtils.isEmpty(pushMessage.getSound())){
                mBuilder.setSound(Utils.getSound(getApplicationContext(), pushMessage.getSound()));
            }

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(12, mBuilder.build());

        } catch (Exception e) {
            Logger.Error("generateNotification: " + e.getMessage());
        }
    }
}