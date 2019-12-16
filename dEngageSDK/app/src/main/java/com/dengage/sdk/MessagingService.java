package com.dengage.sdk;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import com.dengage.sdk.models.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        try {
            Logger.INSTANCE.Debug("On new token : " + token);
            DengageManager.subscribe(token);
        } catch (Exception e) {
            Logger.INSTANCE.Error("onNewToken: "+ e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        Message pushMessage = new Message(data);

        String source = pushMessage.getMessageSource();
        if(source.equals(Constants.MESSAGE_SOURCE)) {
            Logger.INSTANCE.Debug("There is a message receives from dEngage: " + pushMessage.getMessage());
            if (!TextUtils.isEmpty(pushMessage.getMessage())) {
                Logger.INSTANCE.Verbose("Generating notification");
                generateNotification(pushMessage, data);
            } else {
                Logger.INSTANCE.Error("onMessageReceived: Message is empty!");
            }
        }
    }

    private void generateNotification(Message pushMessage, Map<String, String> data) {
        Logger.INSTANCE.Verbose("generateNotification method is called.");
        try {

            Bitmap image = null;
            if(pushMessage.getMediaUrl() != null && !TextUtils.isEmpty(pushMessage.getMediaUrl())) {
                image = RequestHelper.INSTANCE.getBitmap(pushMessage.getMediaUrl());
            }

            String notificationChannelId = Constants.CHANNEL_ID;

            if(pushMessage.getSound() != null){
                notificationChannelId += pushMessage.getSound();
            }

            Logger.INSTANCE.Debug("Channel Id: "+ notificationChannelId);

            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
                //Starting with O notification must have a notification channel to work properly
                ChannelHelper.createNotificationChannel(mNotificationManager, pushMessage.getSound(), pushMessage.getBadge(), getApplicationContext(),notificationChannelId);
            }

            PackageManager packageManager = this.getPackageManager();

            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);

            final int appIconResId = applicationInfo.icon;

            Intent denIntent = new Intent(this, OpenerActivity.class);

            denIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Set<Map.Entry<String, String>> entrySet = data.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                denIntent.putExtra(entry.getKey(), entry.getValue());
            }

            Random random = new SecureRandom();

            PendingIntent contentIntent = PendingIntent.getActivity(this, random.nextInt(), denIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
            } else {
                mBuilder.setContentTitle( packageManager.getApplicationLabel(applicationInfo) );
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
            mNotificationManager.notify(pushMessage.getMessageId(), mBuilder.build());

        } catch (Exception e) {
            Logger.INSTANCE.Error("generateNotification: " + e.getMessage());
        }

    }


    private static class ChannelHelper {

        @TargetApi(Build.VERSION_CODES.O)
        public static void createNotificationChannel(NotificationManager notificationManager, String sound, Boolean badge, Context context, String notificationChannelId) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, Constants.CHANNEL_NAME, importance);
            notificationChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            //notificationChannel.enableLights(true);
            if(badge) {
                notificationChannel.setShowBadge(true);
            }
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            //notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if(sound != null & !TextUtils.isEmpty(sound)){
                Uri soundUri = Utils.getSound(context, sound);
                AudioAttributes attributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
                notificationChannel.setSound(soundUri, attributes);
            }
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}