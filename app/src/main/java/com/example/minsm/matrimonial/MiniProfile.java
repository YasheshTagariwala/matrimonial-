package com.example.minsm.matrimonial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MiniProfile extends AppCompatActivity {

    ImageButton imageButtonShare, imageButtonShare1, imageButtonShare2, imageButtonShare3;
    ImageView imageButtonOption, imageButtonOption1, imageButtonOption2, imageButtonOption3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_profile);
        getSupportActionBar().hide();

        imageButtonShare = findViewById(R.id.imagebutton_share);
        imageButtonShare1 = findViewById(R.id.imagebutton_share1);
        imageButtonShare2 = findViewById(R.id.imagebutton_share2);
        imageButtonShare3 = findViewById(R.id.imagebutton_share3);
        imageButtonOption = findViewById(R.id.imagebutton_option);
        imageButtonOption1 = findViewById(R.id.imagebutton_option1);
        imageButtonOption2 = findViewById(R.id.imagebutton_option2);
        imageButtonOption3 = findViewById(R.id.imagebutton_option3);

        imageButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MiniProfile.this, "Share This", Toast.LENGTH_SHORT).show();
            }
        });

        imageButtonShare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MiniProfile.this, "Share This", Toast.LENGTH_SHORT).show();
            }
        });

        imageButtonShare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MiniProfile.this, "Share This", Toast.LENGTH_SHORT).show();
            }
        });

        imageButtonShare3.setOnClickListener(new View.OnClickListener() {
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

        imageButtonOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FullProfile.class));
            }
        });

        imageButtonOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FullProfile.class));
            }
        });

        imageButtonOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FullProfile.class));
            }
        });
    }
}
