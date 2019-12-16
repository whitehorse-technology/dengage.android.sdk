package com.dengage.sdk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class DenActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.INSTANCE.Verbose("Called DenActivity onCreate.");
        Intent intent = getIntent();
        ProcessMessage(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.INSTANCE.Verbose("Called DenActivity onNewIntent.");
        ProcessMessage(intent);
        finish();
    }

    private void ProcessMessage(Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null) {
            DengageManager.sendOpenEvent(new Message(intent.getExtras()));
        }

        PackageManager packageManager = this.getPackageManager();

        Intent launchIntent = packageManager.getLaunchIntentForPackage(this.getPackageName());

        if (launchIntent != null) {
            if (getIntent().getExtras() != null) {
                launchIntent.putExtras(getIntent().getExtras());
            }
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(launchIntent);
        }

        //PackageManager packageManager = this.getPackageManager();

        //Intent resultIntent = packageManager.getLaunchIntentForPackage(this.getPackageName());

        //ComponentName componentName = denIntent.getComponent();

        // Intent notificationIntent = Intent.makeRestartActivityTask(componentName)
    }
}