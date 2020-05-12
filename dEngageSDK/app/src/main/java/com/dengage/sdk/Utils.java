package com.dengage.sdk;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class Utils {

    private static String installationID = null;

    public static int getScreenWith(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        if(manager!=null) {
            manager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        } else {
            return -1;
        }
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        if(manager!=null) {
            manager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        } else {
            return -1;
        }
    }

    public static String getAppLanguage(){
        return Locale.getDefault().getDisplayLanguage();
    }

    public static String getSystemLanguage() {
        return Resources.getSystem().getConfiguration().locale.getLanguage();
    }

    public static int getTimezoneId() {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        return tz.getRawOffset();
    }

    public static String getSdkVersion() {
        return com.dengage.sdk.BuildConfig.VERSION_NAME;
    }

    public static String getOsVersion() {
        return "Android " + Build.VERSION.RELEASE;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getDeviceUniqueId() {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        String serial;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial";
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    synchronized static String getDeviceId(Context context) {
        if (installationID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.DEN_DEVICE_UNIQUE_ID, Context.MODE_PRIVATE);
            installationID = sharedPrefs.getString(Constants.DEN_DEVICE_UNIQUE_ID, null);
                if (installationID == null) {
                    installationID = UUID.randomUUID().toString();
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(Constants.DEN_DEVICE_UNIQUE_ID, installationID);
                    editor.apply();
                }
        }
        return installationID;
    }

    static void saveSubscription(Context context, String value) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(Constants.SUBSCRIPTION_KEY, value);
        spEditor.apply();
    }

    static boolean hasSubscription(Context context) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        return sp.contains(Constants.SUBSCRIPTION_KEY);
    }

    static String getSubscription(Context context) {
        String appName = context.getPackageName();
        SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
        return sp.getString(Constants.SUBSCRIPTION_KEY, "");
    }

    static String appVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception ignored) {
        }
        return null;
    }

    static String osVersion() {
        return Build.VERSION.RELEASE;
    }

    static String osType() {
        return "Android";
    }

    static String carrier(Context context) {
        String carrier = "";
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            assert manager != null;
            manager.getNetworkOperator();
        } catch (Exception ignored) {
        }
        return carrier;
    }

    static String local(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    static String deviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    static String getUserAgent(Context context) {
        return Utils.getAppLabel(context, "An Android App") + "/"+ Utils.appVersion(context) + " "+ Build.MANUFACTURER +"/"+ Build.MODEL +" "+ System.getProperty("http.agent") +" Mobile/"+ Build.ID +"";
    }

    static String deviceType() {
        return android.os.Build.MANUFACTURER + " : " + android.os.Build.MODEL;
    }

    static String getAppLabel(Context pContext, String defaultText) {
        PackageManager lPackageManager = pContext.getPackageManager();
        ApplicationInfo lApplicationInfo = null;
        try {
            lApplicationInfo = lPackageManager.getApplicationInfo(pContext.getApplicationInfo().packageName, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return (String) (lApplicationInfo != null ? lPackageManager.getApplicationLabel(lApplicationInfo) : defaultText);
    }

    public static Uri getSound(Context context, String sound) {
        int id = context.getResources().getIdentifier(sound, "raw", context.getPackageName());
        if (id != 0) {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + id);
        }else{
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int getAppIconResourceId(Context context) {
        int appIconResId = -1;
        String packageName = context.getPackageName();
        final PackageManager pm = context.getPackageManager();
        final ApplicationInfo applicationInfo;
        try {
            applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            appIconResId = applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            //do nothing here
        }
        return appIconResId;
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static String saveBitmapToInternalStorage(Context context, Bitmap bitmapImage, String fileName) {
        boolean fileSaved = false;
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, fileName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fileSaved = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileSaved)
            return directory.getAbsolutePath();
        else
            return null;
    }

    public static Bitmap loadImageFromStorage(String path, String fileName) {
        Bitmap b = null;

        try {
            File f = new File(path, fileName + ".jpg");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    public static int calculateInSampleSize(final int width, final int height, int reqWidth, int reqHeight) {

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean isInteger(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }
}