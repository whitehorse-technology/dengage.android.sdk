package com.dengage.sdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.dengage.sdk.notification.dEngageMobileManager;
import com.dengage.sdk.notification.logging.Logger;
import com.dengage.sdk.notification.models.CustomParam;
import com.dengage.sdk.notification.models.Event;
import com.dengage.sdk.notification.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    final String integrationKey = "Wt8AlFzBHxdI_s_l_XKDB9zOxBjOVAIeNOUq_p_l_xh4rrkDE80uQzSEGxQ6taNs9ytXAuVD8QKcwKn_s_l_w1U4qepg_s_l_mEB6ESLvLewJye_s_l_22HGSws6pw9tRrpDhbevlBBukcMBX_s_l__p_l_0";
    static dEngageMobileManager mobileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        mobileManager = dEngageMobileManager.createInstance(integrationKey, context);
        mobileManager.register();

        Log.d("DenPush", mobileManager.getSubscriptionJson());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.getExtras() != null)
            dEngageMobileManager.getInstance().open(new Message(intent.getExtras()));
    }
}
