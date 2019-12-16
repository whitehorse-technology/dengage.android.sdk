package com.dengage.sdk;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context; 
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    final String integrationKey = "Wt8AlFzBHxdI_s_l_XKDB9zOxBjOVAIeNOUq_p_l_xh4rrkDE80uQzSEGxQ6taNs9ytXAuVD8QKcwKn_s_l_w1U4qepg_s_l_mEB6ESLvLewJye_s_l_22HGSws6pw9tRrpDhbevlBBukcMBX_s_l__p_l_0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        DengageManager.setLogStatus(true);
        DengageManager.setConfig(integrationKey, context);

    }
}
