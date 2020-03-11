package com.dengage.sdk;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import com.dengage.sdk.models.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.Set;


public class MessagingService extends FirebaseMessagingService {

    private Logger logger = Logger.getInstance();

    @Override
    public void onNewToken(String token) {
        try {
            logger.Debug("On new token : " + token);
            DengageManager.subscribe(token);
        } catch (Exception e) {
            logger.Error("onNewToken: "+ e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        logger.Verbose("There is a message.");
        Map<String, String> data = remoteMessage.getData();
        if( (data != null && data.size() > 0)) {
            Message pushMessage = new Message(data);
            logger.Verbose("Message Json: "+ pushMessage.toJson());
            String source = pushMessage.getMessageSource();
            if (Constants.MESSAGE_SOURCE.equals(source)) {
                logger.Debug("There is a message that received from dEngage");
                generateNotification(pushMessage, data);
            }
        }
    }

    private void generateNotification(Message pushMessage, Map<String, String> data) {
        logger.Verbose("generateNotification method is called.");
        try {

            Context context = getApplicationContext();
            String notificationChannelId = Constants.CHANNEL_ID;
            NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && mNotificationManager != null) {
                ChannelHelper.createNotificationChannel(mNotificationManager, pushMessage.getBadge(), context,notificationChannelId);
            }

            PackageManager packageManager = this.getPackageManager();

            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, notificationChannelId)
                    .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                    .setAutoCancel(true);

            if(!TextUtils.isEmpty(pushMessage.getMediaUrl())) {
                NotificationCompat.Style style;
                Request req = new Request();
                Bitmap image = req.getBitmap(pushMessage.getMediaUrl());
                if(image == null) {
                    style = new NotificationCompat.BigTextStyle()
                            .bigText(pushMessage.getMessage());
                } else {
                    style = new NotificationCompat.BigPictureStyle()
                            .bigPicture(image)
                            .bigLargeIcon(null)
                            .setSummaryText(pushMessage.getMessage());
                }
                mBuilder.setStyle(style);
            }

            if(!TextUtils.isEmpty(pushMessage.getTitle())) {
                mBuilder.setContentTitle(pushMessage.getTitle());
            } else { // default
                String title = Utils.getAppLabel(context, "");
                mBuilder.setContentTitle(title);
            }

            if(!TextUtils.isEmpty(pushMessage.getSubTitle())) {
                mBuilder.setSubText(pushMessage.getSubTitle());
            }

            if(!TextUtils.isEmpty(pushMessage.getMessage())) {
                mBuilder.setContentText(pushMessage.getMessage());
            }

            final int appIconResId = applicationInfo.icon;
            mBuilder.setSmallIcon(appIconResId);

            if(!TextUtils.isEmpty(pushMessage.getSound())){
                mBuilder.setSound(Utils.getSound(context, pushMessage.getSound()));
            } else {
                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(sound);
            }

            if(pushMessage.getBadgeCount() > 0){
                mBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
                mBuilder.setNumber(pushMessage.getBadgeCount());
            }

            Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
            ComponentName componentName = intent.getComponent();

            Intent notificationIntent = Intent.makeRestartActivityTask(componentName);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            Set<Map.Entry<String, String>> entrySet = data.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                notificationIntent.putExtra(entry.getKey(), entry.getValue());
            }

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);

            Notification notification = mBuilder.build();

            mNotificationManager.notify(12, notification);

        } catch (Exception e) {
            logger.Error("generateNotification: " + e.getMessage());
        }
    }
 
    private static class ChannelHelper {
        @TargetApi(Build.VERSION_CODES.O)
        public static void createNotificationChannel(NotificationManager notificationManager, Boolean badge, Context context, String notificationChannelId) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, Constants.CHANNEL_NAME, importance);
            notificationChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}