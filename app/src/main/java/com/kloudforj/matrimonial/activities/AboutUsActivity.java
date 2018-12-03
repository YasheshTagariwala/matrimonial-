package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudforj.matrimonial.R;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewEmail, textViewPhone1, textViewPhone2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        textViewEmail = findViewById(R.id.tv_email);
        textViewPhone1 = findViewById(R.id.tv_phone_1);
        textViewPhone2 = findViewById(R.id.tv_phone_2);

        textViewEmail.setOnClickListener(this);
        textViewPhone1.setOnClickListener(this);
        textViewPhone2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_email:

                String email = textViewEmail.getText().toString().trim();
                if (!email.equals("")) {
                    Intent shareIntent = new Intent(Intent.ACTION_SENDTO);
                    shareIntent.setType("message/rfc822");
                    shareIntent.setData(Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(shareIntent, "Email Via"));
                }
                break;

            case R.id.tv_phone_1:

                String phone1 = textViewPhone1.getText().toString().trim();
                if(!phone1.equals("")) {
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL);
                    intentPhone.setData(Uri.parse("tel:" + phone1));
                    startActivity(intentPhone);
                }
                break;

            case R.id.tv_phone_2:

                String phone2 = textViewPhone2.getText().toString().trim();
                if(!phone2.equals("")) {
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL);
                    intentPhone.setData(Uri.parse("tel:" + phone2));
                    startActivity(intentPhone);
                }
                break;
        }
    }
}