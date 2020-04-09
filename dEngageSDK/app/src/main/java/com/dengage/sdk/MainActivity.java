package com.dengage.sdk;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context; 
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        final DengageManager manager = DengageManager .getInstance(context)
                .setLogStatus(true)
                .setIntegrationKey("YOUR_INTEGRATION_KEY")
                .useCloudSubscription(false)
                .init();
    }
}
