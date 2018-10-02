package com.kloudforj.matrimonial.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudforj.matrimonial.R;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

public class SearchActivity extends AppCompatActivity {

    ImageButton imageButtonClose;
    Button buttonFind;
    int minAge = 18,maxAge=60;
    RangeSeekBar rangeSeekBar;
    TextView textViewAge;
    int gap = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        textViewAge = findViewById(R.id.textview_Age);

        imageButtonClose = findViewById(R.id.bt_close);
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rangeSeekBar = findViewById(R.id.rangeSeekbar);

        rangeSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textViewAge.setText("Age (" + getSelectedValue((Integer) rangeSeekBar.getSelectedMinValue()) + "-" + getSelectedValue((Integer) rangeSeekBar.getSelectedMaxValue()) + ")");
                return false;
            }
        });


        buttonFind = findViewById(R.id.bt_find);
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SearchActivity.this, "Find / Search", Toast.LENGTH_SHORT).show();
            }
        });

        gap = maxAge - minAge;
    }

    public Integer getSelectedValue(int val){
        return minAge + ((val * gap)/100);
    }
}
