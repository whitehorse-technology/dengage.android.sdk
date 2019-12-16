package com.dengage.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class DenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            DengageManager.sendOpenEvent(new Message(intent.getExtras()));
        }

        PackageManager packageManager = context.getPackageManager();

        Intent launchIntent = packageManager.getLaunchIntentForPackage(context.getPackageName());

        if (launchIntent != null) {
            if (intent.getExtras() != null) {
                launchIntent.putExtras(intent.getExtras());
            }
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }

        //PackageManager packageManager = this.getPackageManager();

        //Intent resultIntent = packageManager.getLaunchIntentForPackage(this.getPackageName());

        //ComponentName componentName = denIntent.getComponent();

        // Intent notificationIntent = Intent.makeRestartActivityTask(componentName);

        //if (getIntent().getExtras() != null) {
        //resultIntent.putExtras(getIntent().getExtras());
        //}
    }


}
