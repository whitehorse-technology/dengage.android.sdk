package com.dengage.sdk;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context; 
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    final String integrationKey = "OLyKr5qIa24Inz2jF5jT_p_l_lvid1JJ8_p_l_sZE4k0cjHNoqZ5rqwYvep9nHSFkulSOwxPSptxfRGc_s_l_NBZQ5GqYP1MrV36NlB3ID1pjd5na_p_l_ti0l_p_l_31qqTU2HOQBMANKbnmJ89";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        DengageManager.setLogStatus(true);
        DengageManager.setConfig(integrationKey, context);

    }
}
