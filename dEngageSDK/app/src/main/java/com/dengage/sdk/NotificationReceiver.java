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
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.dengage.sdk.cache.Prefs;
import com.dengage.sdk.models.ActionButton;
import com.dengage.sdk.models.CarouselItem;
import com.dengage.sdk.models.Message;
import com.dengage.sdk.models.NotificationType;
import com.dengage.sdk.push.NotificationNavigationDeciderActivity;

import java.net.URL;
import java.util.List;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

    private static Logger logger = Logger.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.Verbose("onReceive method is called.");
        String intentAction = intent.getAction();
        if (intentAction != null) {
            switch (intentAction) {
                case Constants.PUSH_RECEIVE_EVENT:
                    onPushReceive(context, intent);
                    break;
                case Constants.PUSH_OPEN_EVENT:
                    onPushOpen(context, intent);
                    break;
                case Constants.PUSH_DELETE_EVENT:
                    onPushDismiss(context, intent);
                    break;
                case Constants.PUSH_ACTION_CLICK_EVENT:
                    onActionClick(context, intent);
                    break;
                case Constants.PUSH_ITEM_CLICK_EVENT:
                    onItemClick(context, intent);
                    break;
            }
        }
    }

    protected void onPushReceive(Context context, Intent intent) {
        logger.Verbose("onPushReceive method is called.");
        Bundle extras = intent.getExtras();
        if (extras == null) return;

        onRenderStart(context, intent);
    }

    public static void launchActivity(Context context, @Nullable Intent intent, String uri) {
        Class<? extends Activity> cls = getActivity(context);
        if (cls == null) return;

        Intent activityIntent;
        if (uri != null && !TextUtils.isEmpty(uri)) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        } else {
            activityIntent = new Intent(context, cls);
        }

        if (intent != null && intent.getExtras() != null) {
            activityIntent.putExtras(intent.getExtras());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                startActivities(context, cls, activityIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(activityIntent);
        }
    }

    public static void launchActivityForInApp(Context context, @Nullable Intent intent, String uri) {
        Class<? extends Activity> cls = getActivity(context);
        if (cls == null) return;

        Intent activityIntent;
        if (uri != null && !TextUtils.isEmpty(uri)) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        } else {
            activityIntent = new Intent(context, cls);
        }

        if (intent != null && intent.getExtras() != null) {
            activityIntent.putExtras(intent.getExtras());
        }


        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(activityIntent);

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void startActivities(Context context, Class<? extends Activity> cls, Intent activityIntent) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(activityIntent);
        stackBuilder.startActivities();
    }

    private void onRenderStart(final Context context, final Intent intent) {
        logger.Verbose("onRenderStart method is called.");
        final Message message = new Message(intent.getExtras());
        if (message.getNotificationType() == NotificationType.CAROUSEL) {
            logger.Verbose("This is a carousel notification");
            new CarouselDownloader(context, message.getCarouselContent(),
                    new CarouselDownloader.OnDownloadsCompletedListener() {
                        @Override
                        public void onComplete(CarouselItem[] items) {
                            message.setCarouselContent(items);
                            intent.putExtra("RAW_DATA", message.toJson());
                            onCarouselRender(context, intent, message);
                        }
                    }).start();
        } else if (message.getNotificationType() == NotificationType.RICH) {
            logger.Verbose("This is a rich notification");
            new ImageDownloader(message.getMediaUrl(), new ImageDownloader.OnImageLoaderListener() {
                @Override
                public void onError(ImageDownloader.ImageError error) {
                    logger.Error("Image Download Error: " + error.getMessage());
                }

                @Override
                public void onComplete(Bitmap bitmap) {
                    logger.Verbose("Image downloaded.");
                    NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context, intent, message);
                    onRichNotificationRender(context, intent, message, bitmap, notificationBuilder);
                }
            }).start();
        } else {
            logger.Verbose("This is a text notification");
            NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context, intent, message);
            onTextNotificationRender(context, intent, message, notificationBuilder);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(
            Context context,
            Intent intent,
            Message message
    ) {
        Bundle extras = intent.getExtras();

        Random random = new Random();
        int contentIntentRequestCode = random.nextInt();
        int deleteIntentRequestCode = random.nextInt();

        final String packageName = context.getPackageName();
        Intent contentIntent = getContentIntent(extras, packageName);
        Intent deleteIntent = getDeleteIntent(extras, packageName);

        PendingIntent pContentIntent = getPendingIntent(context, contentIntentRequestCode, contentIntent);
        PendingIntent pDeleteIntent = getDeletePendingIntent(context, deleteIntentRequestCode, deleteIntent);

        Uri soundUri = Utils.getSound(context, message.getSound());
        // generate new channel id for different sounds
        NotificationChannel channel = null;
        String channelId = Constants.CHANNEL_ID;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (channel == null) {
                channel = new NotificationChannel(
                        channelId,
                        Constants.CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(soundUri, audioAttributes);

                notificationManager.createNotificationChannel(channel);
            } else {
                channelId = channel.getId();
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);

        notificationBuilder
                .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                .setContentIntent(pContentIntent)
                .setDeleteIntent(pDeleteIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(getSmallIconId(context));

        int notificationSmallIconColorId = getSmallIconColorId(context);

        if (notificationSmallIconColorId > 0) {
            notificationBuilder.setColor(ContextCompat.getColor(context, notificationSmallIconColorId));
        }


        if (!TextUtils.isEmpty(message.getTitle())) {
            notificationBuilder.setContentTitle(message.getTitle());
        }

        if (!TextUtils.isEmpty(message.getSubText())) {
            notificationBuilder.setSubText(message.getSubText());
        }

        if (!TextUtils.isEmpty(message.getMessage())) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getMessage()));
            notificationBuilder.setContentText(message.getMessage());
        }

        notificationBuilder.setSound(soundUri);

        if (message.getBadgeCount() > 0) {
            notificationBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
            notificationBuilder.setNumber(message.getBadgeCount());
        }

        if (message.getActionButtons() != null && message.getActionButtons().length > 0) {
            for (ActionButton button : message.getActionButtons()) {
                int requestCode = random.nextInt();
                Intent buttonIntent = new Intent(Constants.PUSH_ACTION_CLICK_EVENT);
                buttonIntent.putExtra("id", button.getId());
                buttonIntent.putExtra("targetUrl", button.getTargetUrl());
                buttonIntent.putExtra("RAW_DATA", message.toJson());
                buttonIntent.setPackage(packageName);
                PendingIntent btnPendingIntent = getPendingIntent(context, requestCode, buttonIntent);
                int icon = getResourceId(context, button.getIcon());
                notificationBuilder.addAction(icon, button.getText(), btnPendingIntent);
            }
        }
        return notificationBuilder;
    }

    protected void onRichNotificationRender(Context context, Intent intent, Message message, Bitmap bitmap, NotificationCompat.Builder notificationBuilder) {

        NotificationCompat.Style style;
        if (bitmap != null) {
            notificationBuilder.setLargeIcon(bitmap);
            style = new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap);
            notificationBuilder.setStyle(style);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        if (manager != null) {
            manager.notify(message.getMessageSource(), message.getMessageId(), notification);
        }
    }

    protected void onTextNotificationRender(Context context, Intent intent, Message message, NotificationCompat.Builder notificationBuilder) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        if (manager != null) {
            manager.notify(message.getMessageSource(), message.getMessageId(), notification);
        }
    }

    protected void onCarouselRender(Context context, Intent intent, Message message) {

    }

    protected void onCarouselReRender(Context context, Intent intent, Message message) {

    }

    protected void onPushOpen(Context context, Intent intent) {
        logger.Verbose("onPushOpen method is called.");

        Bundle extras = intent.getExtras();
        Message message = new Message(extras);

        DengageManager manager = DengageManager.getInstance(context);

        String uri = null;

        if (extras != null) {

            String rawJson = extras.getString("RAW_DATA");
            if (!TextUtils.isEmpty(rawJson))
                message = Message.fromJson(rawJson);

            uri = extras.getString("targetUrl");

            manager.sendOpenEvent("", "", message);

        } else {
            logger.Error("No extra data for open.");
        }

        clearNotification(context, message);

        launchActivity(context, intent, uri);
    }

    protected void onPushDismiss(Context context, Intent intent) {
        logger.Verbose("onPushDismiss method is called.");

        Bundle extras = intent.getExtras();
        Message message = new Message(extras);

        if (extras != null) {
            String rawJson = extras.getString("RAW_DATA");
            if (!TextUtils.isEmpty(rawJson))
                message = Message.fromJson(rawJson);
        }

        clearNotification(context, message);
    }

    protected void onActionClick(Context context, Intent intent) {
        logger.Verbose("onActionClick method is called.");

        Bundle extras = intent.getExtras();
        Message message = new Message(extras);

        DengageManager manager = DengageManager.getInstance(context);

        String uri = null;

        if (extras != null) {
            String rawJson = extras.getString("RAW_DATA");
            if (!TextUtils.isEmpty(rawJson))
                message = Message.fromJson(rawJson);

            String id = extras.getString("id", "");
            manager.sendOpenEvent(id, "", message);

            uri = extras.getString("targetUrl");

        } else {
            logger.Debug("No extra data for action.");
        }

        clearNotification(context, message);

        launchActivity(context, intent, uri);
    }

    protected void onItemClick(Context context, Intent intent) {
        logger.Verbose("onItemClick method is called.");

        DengageManager manager = DengageManager.getInstance(context);

        String navigation = "";
        String uri = null;
        String rawJson = "";
        String id = "";
        int current = -1;
        Bundle extras = intent.getExtras();

        if (extras != null) {
            id = extras.getString("id", "");
            navigation = extras.getString("navigation");
            uri = extras.getString("targetUrl");
            current = extras.getInt("current");
            rawJson = extras.getString("RAW_DATA");
        } else {
            logger.Debug("No extra data for action.");
        }

        Message message = new Message(intent.getExtras());
        if (!TextUtils.isEmpty(rawJson))
            message = Message.fromJson(rawJson);

        logger.Debug("Current Item Index: " + current);
        if (current > -1) {
            CarouselItem item = message.getCarouselContent()[current];
            uri = item.getTargetUrl();
            logger.Debug("Current URI: " + uri);
        }

        if (navigation.equals("")) {

            manager.sendOpenEvent("", id, new Message(extras));

            clearNotification(context, message);
            launchActivity(context, intent, uri);

        } else if (navigation.equals("left")) {
            onCarouselReRender(context, intent, message);
        } else if (navigation.equals("right")) {
            onCarouselReRender(context, intent, message);
        }
    }

    public static Class<? extends Activity> getActivity(Context context) {
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
        Intent contentIntent = new Intent(Constants.PUSH_OPEN_EVENT);
        contentIntent.putExtras(extras);
        contentIntent.setPackage(packageName);
        return contentIntent;
    }

    protected Intent getDeleteIntent(Bundle extras, String packageName) {
        Intent contentIntent = new Intent(Constants.PUSH_DELETE_EVENT);
        contentIntent.putExtras(extras);
        contentIntent.setPackage(packageName);
        return contentIntent;
    }

    protected Intent getItemClickIntent(Bundle extras, String packageName) {
        Intent itemIntent = new Intent(Constants.PUSH_ITEM_CLICK_EVENT);
        itemIntent.putExtras(extras);
        itemIntent.putExtra("navigation", "");
        itemIntent.setPackage(packageName);
        return itemIntent;
    }

    protected Intent getLeftItemIntent(Bundle extras, String packageName) {
        Intent itemIntent = new Intent(Constants.PUSH_ITEM_CLICK_EVENT);
        itemIntent.putExtras(extras);
        itemIntent.putExtra("navigation", "left");
        itemIntent.setPackage(packageName);
        return itemIntent;
    }

    protected Intent getRightItemIntent(Bundle extras, String packageName) {
        Intent itemIntent = new Intent(Constants.PUSH_ITEM_CLICK_EVENT);
        itemIntent.putExtras(extras);
        itemIntent.putExtra("navigation", "right");
        itemIntent.setPackage(packageName);
        return itemIntent;
    }

    public static void clearNotification(Context context, Message message) {
        logger.Verbose("Clearing notification ID: " + message.getMessageId());
        logger.Verbose("Clearing notification TAG: " + message.getMessageSource());

        if (message.getCarouselContent() != null && message.getCarouselContent().length > 0) {
            for (CarouselItem item : message.getCarouselContent()) {
                Utils.removeFileFromStorage(item.getMediaFileLocation(), item.getMediaFileName());
            }
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(message.getMessageSource(), message.getMessageId());
        }
    }

    protected int getSmallIconId(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String smallIcon = Utils.getMetaData(context, "den_push_small_icon");
            if (!TextUtils.isEmpty(smallIcon)) {
                int appIconId = getResourceId(context, smallIcon);
                logger.Verbose("Application icon: " + smallIcon);
                return appIconId;
            } else {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                logger.Verbose("Application icon: " + applicationInfo.icon);
                return applicationInfo.icon;
            }
        } catch (PackageManager.NameNotFoundException e) {
            logger.Verbose("Application Icon Not Found");
            return -1;
        }
    }

    protected int getSmallIconColorId(Context context) {
        String smallIcon = Utils.getMetaData(context, "den_push_small_icon_color");
        if (!TextUtils.isEmpty(smallIcon)) {
            int appIconColorId = getColorResourceId(context, smallIcon);
            logger.Verbose("Application icon: " + smallIcon);
            return appIconColorId;
        } else {
            return -1; // in case metadata not provided in AndroidManifest
        }
    }

    public int getColorResourceId(Context context, String resourceName) {
        if (TextUtils.isEmpty(resourceName)) return 0;
        if (Utils.isInteger(resourceName)) return 0;

        try {
            int resourceId = context.getResources().getIdentifier(resourceName, "color", context.getPackageName());
            return resourceId;
        } catch (Exception e) {
            try {
                int defaultResourceId = android.R.drawable.class.getField(resourceName).getInt(null);
                return defaultResourceId;
            } catch (Throwable ignored) {
            }
            e.printStackTrace();
            return 0;
        }
    }

    public int getResourceId(Context context, String resourceName) {
        if (TextUtils.isEmpty(resourceName)) return 0;
        if (Utils.isInteger(resourceName)) return 0;

        try {
            int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            return resourceId;
        } catch (Exception e) {
            try {
                int defaultResourceId = android.R.drawable.class.getField(resourceName).getInt(null);
                return defaultResourceId;
            } catch (Throwable ignored) {
            }
            e.printStackTrace();
            return 0;
        }
    }

    public PendingIntent getPendingIntent(Context context, int requestCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            Bundle extras = intent.getExtras();
            final String packageName = context.getPackageName();
            intent = new Intent(context, NotificationNavigationDeciderActivity.class);
            intent.putExtras(extras);
            intent.setPackage(packageName);
            if (intent.getExtras() != null) {
                intent.putExtras(intent.getExtras());
            }
            stackBuilder.addNextIntentWithParentStack(intent);
            return stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private PendingIntent getDeletePendingIntent(Context context, int requestCode, Intent intent) {

        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public static Bitmap getBitmapFromUrl(String imageUrl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            return BitmapFactory.decodeStream(new URL(imageUrl).openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            logger.Debug("getBitmapFromUrl: " + e.getMessage());
            return null;
        }
    }
}
