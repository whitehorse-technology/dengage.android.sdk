package com.dengage.sdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dengage.sdk.notification.MobileManager;
import com.dengage.sdk.notification.logging.Logger;
import com.dengage.sdk.notification.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    final String appAlias = "com.dengage.dengagepushapp";
    static MobileManager mobileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        mobileManager = MobileManager.createInstance(appAlias, context);
        mobileManager.register();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Logger.Error("FirebaseInstanceId Failed: " + task.getException().getMessage());
                    return;
                }

                String token = task.getResult().getToken();
                Logger.Debug("Current Token: " + token);

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.getExtras() != null)
            MobileManager.getInstance().open(new Message(intent.getExtras()));
    }
}
