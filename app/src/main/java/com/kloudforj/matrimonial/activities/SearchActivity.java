package com.kloudforj.matrimonial.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kloudforj.matrimonial.R;

public class SearchActivity extends AppCompatActivity {

    ImageButton imageButtonClose;
    Button buttonFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imageButtonClose = findViewById(R.id.bt_close);
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonFind = findViewById(R.id.bt_find);
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SearchActivity.this, "Find / Search", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
