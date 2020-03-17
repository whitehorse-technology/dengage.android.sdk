package com.dengage.sdk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.dengage.sdk.models.Message;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

    private static Logger logger = Logger.getInstance();

    public static final String PUSH_RECEIVE = "com.dengage.push.intent.RECEIVE";
    public static final String PUSH_OPEN = "com.dengage.push.intent.OPEN";
    public static final String PUSH_DELETE = "com.dengage.push.intent.DELETE";
    public static final String PUSH_ACTION_CLICK = "com.dengage.push.intent.ACTION_CLICK";
    public static final String CAROUSEL_ITEM_CLICK = "com.dengage.push.intent.CAROUSEL_ITEM_CLICK";

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.Verbose("onReceive method is called.");

        String intentAction = intent.getAction();
        if (intentAction != null) {
            switch (intentAction) {
                case PUSH_RECEIVE:
                    onPushReceive(context, intent);
                    break;
                case PUSH_OPEN:
                    onPushOpen(context, intent);
                    break;
                case PUSH_DELETE:
                    onPushDismiss(context, intent);
                    break;
                case PUSH_ACTION_CLICK:
                    onActionClick(context, intent);
                    break;
                case CAROUSEL_ITEM_CLICK:
                    onCarouselItemClick(context, intent);
                    break;
            }
        }
    }

    protected void onPushReceive(Context context, Intent intent) {
        logger.Verbose("onPushReceive method is called.");

        generateNotification(context, intent);
    }

    protected void onPushOpen(Context context, Intent intent) {
        logger.Verbose("onPushOpen method is called.");

        String uri = null;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            DengageManager.getInstance(context).sendOpenEvent(new Message(extras));
            uri = extras.getString("targetUrl");
        } else {
            logger.Error("No extra data.");
        }

        Class<? extends Activity> cls = getActivity(context, intent);
        Intent activityIntent;
        if (uri != null && !TextUtils.isEmpty(uri)) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        } else {
            activityIntent = new Intent(context, cls);
        }

        activityIntent.putExtras(intent.getExtras());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivities(context, cls, activityIntent);
        } else {
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(activityIntent);
        }
    }

    protected void onPushDismiss(Context context, Intent intent) {
        logger.Verbose("onPushDismiss method is called.");
    }

    protected void onActionClick(Context context, Intent intent) {
        logger.Verbose("onActionClick method is called.");
    }

    protected void onCarouselItemClick(Context context, Intent intent) {
        logger.Verbose("onCarouselItemClick method is called.");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void startActivities(Context context, Class<? extends Activity> cls, Intent activityIntent) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(activityIntent);
        stackBuilder.startActivities();
    }

    protected void generateNotification(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras == null) return;

        Random random = new Random();
        int contentIntentRequestCode = random.nextInt();
        int deleteIntentRequestCode = random.nextInt();

        String packageName = context.getPackageName();
        Intent contentIntent = getContentIntent(extras, packageName);
        Intent deleteIntent = getDeleteIntent(extras, packageName);

        PendingIntent pContentIntent = PendingIntent.getBroadcast(context, contentIntentRequestCode,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pDeleteIntent = PendingIntent.getBroadcast(context, deleteIntentRequestCode,
                deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = getNotificationChannel(context, intent);
            createNotificationChannel(context, notificationChannel);
            channelId = notificationChannel.getId();
        }

        @SuppressWarnings("ConstantConditions")
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);

        notificationBuilder
                .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                .setContentIntent(pContentIntent)
                .setDeleteIntent(pDeleteIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(getSmallIconId(context, intent));

        Message pushMessage = new Message(extras);

        if(!TextUtils.isEmpty(pushMessage.getMediaUrl())) {

            NotificationCompat.Style style;
            Request req = new Request();
            Bitmap image = req.getBitmap(pushMessage.getMediaUrl());
            if(image == null) {
                logger.Debug(pushMessage.getMediaUrl());
                style = new NotificationCompat.BigTextStyle()
                        .bigText(pushMessage.getMessage());
            } else {
                style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon(null)
                        .setSummaryText(pushMessage.getMessage());
            }
            notificationBuilder.setStyle(style);
        }

        if(!TextUtils.isEmpty(pushMessage.getTitle())) {
            notificationBuilder.setContentTitle(pushMessage.getTitle());
        } else {
            String label = Utils.getAppLabel(context, "");
            notificationBuilder.setContentTitle(label);
        }

        if(!TextUtils.isEmpty(pushMessage.getSubTitle())) {
            notificationBuilder.setSubText(pushMessage.getSubTitle());
        }

        if(!TextUtils.isEmpty(pushMessage.getMessage())) {
            notificationBuilder.setContentText(pushMessage.getMessage());
        }

        if(!TextUtils.isEmpty(pushMessage.getSound())){
            notificationBuilder.setSound(Utils.getSound(context, pushMessage.getSound()));
        } else {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(sound);
        }

        if(pushMessage.getBadgeCount() > 0){
            notificationBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
            notificationBuilder.setNumber(pushMessage.getBadgeCount());
        }

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        if (mNotificationManager != null) {
            mNotificationManager.notify(pushMessage.getMessageId(), notification);
        }
    }

    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            return null;
        }
        String className = launchIntent.getComponent().getClassName();
        Class<? extends Activity> cls = null;
        try {
            cls = (Class<? extends Activity>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            // do nothing
        }
        return cls;
    }

    protected Intent getContentIntent(Bundle extras, String packageName) {
        Intent contentIntent = new Intent(NotificationReceiver.PUSH_OPEN);
        contentIntent.putExtras(extras);
        contentIntent.setPackage(packageName);
        return contentIntent;
    }

    protected Intent getDeleteIntent(Bundle extras, String packageName) {
        Intent contentIntent = new Intent(NotificationReceiver.PUSH_DELETE);
        contentIntent.putExtras(extras);
        contentIntent.setPackage(packageName);
        return contentIntent;
    }

    @TargetApi(Build.VERSION_CODES.O)
    protected NotificationChannel getNotificationChannel(Context context, Intent intent) {
        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(Constants.CHANNEL_DESCRIPTION);
        return channel;
    }

    @TargetApi(Build.VERSION_CODES.O)
    protected void createNotificationChannel(Context context, NotificationChannel notificationChannel) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.createNotificationChannel(notificationChannel);
    }

    protected int getSmallIconId(Context context, Intent intent) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}
