package com.example.minsm.matrimonial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MiniProfile extends AppCompatActivity {

    ImageButton imageButtonShare,imageButtonOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_profile);
        getSupportActionBar().hide();

        imageButtonShare = (ImageButton) findViewById(R.id.imagebutton_share);
        imageButtonOption = (ImageButton) findViewById(R.id.imagebutton_option);

        imageButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MiniProfile.this, "Share This", Toast.LENGTH_SHORT).show();
            }
        });

        imageButtonOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FullProfile.class));
            }
        });
    }
}
