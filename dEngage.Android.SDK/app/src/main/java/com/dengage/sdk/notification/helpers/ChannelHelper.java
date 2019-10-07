package com.dengage.sdk.notification.helpers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.dengage.sdk.notification.Constants;
import com.dengage.sdk.notification.helpers.Utils;

public class ChannelHelper {

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

