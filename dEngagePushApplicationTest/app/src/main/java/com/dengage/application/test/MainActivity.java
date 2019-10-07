package com.dengage.application.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dengage.sdk.notification.dEngageMobileManager;
import com.dengage.sdk.notification.logging.Logger;
import com.dengage.sdk.notification.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final String appAlias = "com.dengage.application.test";
    private static dEngageMobileManager mobileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Context context = getApplicationContext();
        mobileManager = dEngageMobileManager.createInstance(appAlias, context);
        mobileManager.setAppAlias(appAlias);
        mobileManager.register();
        mobileManager.subscribe();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.e("DenPush", task.getException().getMessage());
                    return;
                }

                String token = task.getResult().getToken();
                Log.d("DenPush", token);
            }
        });

        TextView txtEnvironment = (TextView)findViewById(R.id.txtEnvironment);
        TextView txtAlias = (TextView)findViewById(R.id.txtAlias);
        TextView txtAdvertisingId = (TextView)findViewById(R.id.txtAdvertisingId);
        TextView txtToken = (TextView)findViewById(R.id.txtToken);

        txtEnvironment.setText(mobileManager.getEnvironment());
        txtAlias.setText(mobileManager.subscription.getAppAlias());
        txtAdvertisingId.setText(mobileManager.subscription.getUdid());
        txtToken.setText(mobileManager.subscription.getToken());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        dEngageMobileManager.getInstance().open(new Message(intent.getExtras()));
    }
}
