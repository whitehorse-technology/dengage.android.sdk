package com.dengage.sdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.dengage.sdk.notification.MobileManager;
import com.dengage.sdk.notification.logging.Logger;
import com.dengage.sdk.notification.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static java.security.AccessController.getContext;

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

        TextView appAliasText = (TextView) findViewById(R.id.appAliasText);
        appAliasText.setText(appAlias);

        TextView tokenText = (TextView) findViewById(R.id.tokenText);
        tokenText.setText(MobileManager.getInstance().subscription.getToken());

        TextView advertisinIdText = (TextView) findViewById(R.id.advertisingIdText);
        advertisinIdText.setText(MobileManager.getInstance().subscription.getUdid());


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.getExtras() != null)
            MobileManager.getInstance().open(new Message(intent.getExtras()));
    }
}
