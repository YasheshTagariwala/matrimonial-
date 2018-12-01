package com.kloudforj.matrimonial.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kloudforj.matrimonial.R;

public class AboutUsActivity extends AppCompatActivity {

    TextView textViewPhone1, textViewPhone2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        textViewPhone1 = findViewById(R.id.tv_phone_1);
        textViewPhone2 = findViewById(R.id.tv_phone_2);
    }
}