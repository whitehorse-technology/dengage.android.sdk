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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import com.dengage.sdk.models.ActionButton;
import com.dengage.sdk.models.CarouselItem;
import com.dengage.sdk.models.Event;
import com.dengage.sdk.models.Message;
import com.dengage.sdk.models.ModelBase;
import com.dengage.sdk.models.NotificationType;
import com.dengage.sdk.models.Open;
import com.dengage.sdk.models.Subscription;
import com.dengage.sdk.models.TransactionalOpen;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
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
        if(extras == null) return;

        onRenderStart(context, intent);
    }

    private  void launchActivity(Context context, Intent intent, String uri) {
        Class<? extends Activity> cls = getActivity(context, intent);
        if(cls == null) return;

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

    private void onRenderStart(final Context context, final Intent intent) {
        logger.Verbose("onRenderStart method is called.");
        final Message message = new Message(intent.getExtras());
        if(message.getNotificationType() == NotificationType.CAROUSEL) {
            if (message.getCarouselContent() != null && message.getCarouselContent().length > 0) {
                ImageDownloader basicDownloader = new ImageDownloader(context, message.getCarouselContent(),
                        new ImageDownloader.OnDownloadsCompletedListener() {
                            @Override
                            public void onComplete(CarouselItem[] items) {
                                message.setCarouselContent(items);
                                intent.putExtra("RAW_DATA", message.toJson());
                                onCarouselRender(context, intent, message);
                            }
                        });
                basicDownloader.startAllDownloads();
            }
        } else if(message.getNotificationType() == NotificationType.RICH) {
            onRender(context, intent, message);
        } else {
            onRender(context, intent, message);
        }
    }

    protected void onRender(Context context, Intent intent, Message message) {
        Bundle extras = intent.getExtras();

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
            NotificationChannel notificationChannel = getNotificationChannel();
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
                .setSmallIcon(getSmallIconId(context));

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
                Intent buttonIntent = new Intent(Constants.PUSH_ACTION_CLICK_EVENT);
                buttonIntent.putExtra("id", button.getId());
                buttonIntent.putExtra("targetUrl", button.getTargetUrl());
                buttonIntent.putExtra("RAW_DATA", message.toJson());
                buttonIntent.setPackage(packageName);
                PendingIntent btnPendingIntent = PendingIntent.getBroadcast(context, requestCode, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                int icon = getResourceId(context, button.getIcon());
                notificationBuilder.addAction(icon, button.getText(), btnPendingIntent);
            }
        }

        if(message.getNotificationType() == NotificationType.RICH) {
            NotificationCompat.Style style;
            Bitmap image = getBitmapFromUrl(message.getMediaUrl());
            if (image != null) {
                notificationBuilder.setLargeIcon(image);
                style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(image);
                notificationBuilder.setStyle(style);
            }
        }

        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
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
            if(!TextUtils.isEmpty(rawJson))
                message = Message.fromJson(rawJson);

            uri = extras.getString("targetUrl");

            DengageEvent.getInstance(context, uri, message.getDengageCampId(), message.getDengageSendId());

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

        if(extras != null) {
            String rawJson = extras.getString("RAW_DATA");
            if(!TextUtils.isEmpty(rawJson))
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
            if(!TextUtils.isEmpty(rawJson))
                message = Message.fromJson(rawJson);

            String id = extras.getString("id", "");
            manager.sendOpenEvent(id, "", message);

            uri = extras.getString("targetUrl");

            DengageEvent.getInstance(context, uri, message.getDengageCampId(), message.getDengageSendId());
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
        if(!TextUtils.isEmpty(rawJson))
            message = Message.fromJson(rawJson);

        logger.Debug("Current Item Index: "+ current);
        if(current  > -1) {
            CarouselItem item = message.getCarouselContent()[current];
            uri = item.getTargetUrl();
            logger.Debug("Current URI: "+ uri);
        }

        if(navigation.equals("")) {

            DengageEvent.getInstance(context, uri, message.getDengageCampId(), message.getDengageSendId());

            manager.sendOpenEvent("", id, new Message(extras));

            clearNotification(context, message);
            launchActivity(context, intent, uri);

        } else if(navigation.equals("left")) {
            onCarouselReRender(context, intent, message);
        } else if(navigation.equals("right")) {
            onCarouselReRender(context, intent, message);
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

    protected void clearNotification(Context context, Message message) {
        logger.Verbose("Clearing notification ID: "+ message.getMessageId());
        logger.Verbose("Clearing notification TAG: "+ message.getMessageSource());

        if(message.getCarouselContent() != null && message.getCarouselContent().length > 0) {
            for (CarouselItem item : message.getCarouselContent()) {
                Utils.removeFileFromStorage(item.getMediaFileLocation(), item.getMediaFileName());
            }
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager != null) {
            manager.cancel(message.getMessageSource(), message.getMessageId());
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    protected NotificationChannel getNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(Constants.CHANNEL_DESCRIPTION);
        return channel;
    }

    @TargetApi(Build.VERSION_CODES.O)
    protected void createNotificationChannel(Context context, NotificationChannel notificationChannel) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.createNotificationChannel(notificationChannel);
        }
    }

    protected int getSmallIconId(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            logger.Verbose("Application icon: "+ applicationInfo.icon);
            return applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            logger.Verbose("Application Icon Not Found");
            return -1;
        }
    }

    public int getResourceId(Context context, String resourceName) {
        if(TextUtils.isEmpty(resourceName)) return 0;
        if(Utils.isInteger(resourceName)) return 0;

        try {
            int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            return resourceId;
        } catch (Exception e) {
            try {
                int defaultResourceId = android.R.drawable.class.getField(resourceName).getInt(null);
                return defaultResourceId;
            } catch (Throwable ignored) {}
            e.printStackTrace();
            return 0;
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
