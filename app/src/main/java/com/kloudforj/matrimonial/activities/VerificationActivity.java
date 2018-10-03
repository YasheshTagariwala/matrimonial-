package com.kloudforj.matrimonial.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudforj.matrimonial.R;

public class VerificationActivity extends AppCompatActivity {

    TextView textViewNote;
    LinearLayout linearLayoutPhone,linearLayoutMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        this.getActionBar().hide();
        getSupportActionBar().hide();
        textViewNote = findViewById(R.id.text_note);

        linearLayoutPhone = findViewById(R.id.linearlayout_phone);
        linearLayoutMail = findViewById(R.id.linearlayout_email);

        Bundle extras = getIntent().getExtras();
        String type = extras.getString("type");
        if(type.equals("phone")){
            linearLayoutPhone.setVisibility(View.VISIBLE);
            linearLayoutMail.setVisibility(View.GONE);
            textViewNote.setText("You will get SMS with a confirmation code to this number.");
        }else{
            linearLayoutPhone.setVisibility(View.GONE);
            linearLayoutMail.setVisibility(View.VISIBLE);
            textViewNote.setText("You will get Mail with a confirmation code to this Email ID.");
        }
    }
}
