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
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;

import com.dengage.sdk.models.ActionButton;
import com.dengage.sdk.models.Message;
import com.dengage.sdk.models.NotificationType;
import java.net.URL;
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
            logger.Error("No extra data for open.");
        }

        launchActivity(context, intent, uri);
    }

    protected void onPushDismiss(Context context, Intent intent) {
        logger.Verbose("onPushDismiss method is called.");
    }

    protected void onActionClick(Context context, Intent intent) {
        logger.Verbose("onActionClick method is called.");

        String uri = null;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            // send action click event to dengage.
            uri = extras.getString("targetUrl");
        } else {
            logger.Debug("No extra data for action.");
        }
    }

    protected void onCarouselItemClick(Context context, Intent intent) {
        logger.Verbose("onCarouselItemClick method is called.");
    }

    private  void launchActivity(Context context, Intent intent, String uri) {
        Class<? extends Activity> cls = getActivity(context, intent);
        Intent activityIntent;
        if (uri != null && !TextUtils.isEmpty(uri)) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        } else {
            activityIntent = new Intent(context, cls);
        }

        if(intent.getExtras() != null)
            activityIntent.putExtras(intent.getExtras());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivities(context, cls, activityIntent);
        } else {
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(activityIntent);
        }
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

        Message message = new Message(extras);

        Random random = new Random();
        int contentIntentRequestCode = random.nextInt();
        int deleteIntentRequestCode = random.nextInt();

        final String packageName = context.getPackageName();
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

        if(!TextUtils.isEmpty(message.getTitle())) {
            notificationBuilder.setContentTitle(message.getTitle());
        } else {
            String label = Utils.getAppLabel(context, "");
            notificationBuilder.setContentTitle(label);
        }

        if(!TextUtils.isEmpty(message.getSubText())) {
            notificationBuilder.setSubText(message.getSubText());
        }

        if(!TextUtils.isEmpty(message.getMessage())) {
            notificationBuilder.setContentText(message.getMessage());
        }

        if(!TextUtils.isEmpty(message.getSound())){
            notificationBuilder.setSound(Utils.getSound(context, message.getSound()));
        } else {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(sound);
        }

        if(message.getBadgeCount() > 0){
            notificationBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
            notificationBuilder.setNumber(message.getBadgeCount());
        }

        if(message.getActionButtons() != null && message.getActionButtons().length > 0) {
            for (ActionButton button : message.getActionButtons()) {
                int requestCode = random.nextInt();
                Intent buttonIntent = new Intent(NotificationReceiver.PUSH_ACTION_CLICK);
                buttonIntent.putExtra("id", button.getId());
                buttonIntent.putExtra("targetUrl", button.getTargetUrl());
                buttonIntent.setPackage(packageName);
                PendingIntent btnPendingIntent = PendingIntent.getBroadcast(context, requestCode, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                int icon = getResourceId(context, button.getIcon());
                notificationBuilder.addAction(icon, button.getText(), btnPendingIntent);
            }
        }


        if(message.getNotificationType() == NotificationType.CAROUSEL) {
            generateCarouselNotification(context, message, notificationBuilder);
        } else if(message.getNotificationType() == NotificationType.RICH) {
            generateRichNotification(context, message, notificationBuilder);
        } else {
            generateTextNotification(context, message, notificationBuilder);
        }
    }

    protected void generateTextNotification(Context context, Message pushMessage, NotificationCompat.Builder notificationBuilder) {
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        if (mNotificationManager != null) {
            mNotificationManager.notify(pushMessage.getMessageId(), notification);
        }
    }

    protected void generateRichNotification(Context context, Message pushMessage, NotificationCompat.Builder notificationBuilder) {

        if(!TextUtils.isEmpty(pushMessage.getMediaUrl())) {
            NotificationCompat.Style style;
            Bitmap image = getBitmapFromUrl(pushMessage.getMediaUrl());
            if(image == null) {
                style = new NotificationCompat.BigTextStyle()
                        .bigText(pushMessage.getMessage());
            } else {
                notificationBuilder.setLargeIcon(image);
                style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon(null)
                        .setSummaryText(pushMessage.getMessage());
            }
            notificationBuilder.setStyle(style);
        }

        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        if (mNotificationManager != null) {
            mNotificationManager.notify(pushMessage.getMessageId(), notification);
        }
    }

    protected void generateCarouselNotification(Context context, Message pushMessage, NotificationCompat.Builder notificationBuilder) {

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
            return -1;
        }
    }

    public int getResourceId(Context context, String resourceName) {
        if(TextUtils.isEmpty(resourceName)) return -1;
        try {
            return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        } catch (Exception e) {
            try {
                return android.R.drawable.class.getField(resourceName).getInt(null);
            } catch (Throwable ignored) {}
            e.printStackTrace();
            return -1;
        }
    }

    public static Bitmap getBitmapFromUrl(String imageUrl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            return BitmapFactory.decodeStream(new URL(imageUrl).openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            logger.Debug("getBitmapFromUrl: "+ e.getMessage());
            return null;
        }
    }
}
