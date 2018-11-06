package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.utils.ProjectConstants;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

public class SearchActivity extends AppCompatActivity {

    ImageButton imageButtonClose;
    Button buttonFind;
    int minAge = 18,maxAge=60;
    RangeSeekBar rangeSeekBar;
    TextView textViewAge;
    int gap = 0;

    private Spinner mSpnSubCaste1, mSpnSubCaste2;
    private Spinner mSpnCountry, mSpnState, mSpnCity;
    private EditText mEtName;

    private SharedPreferences globalSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSpnSubCaste1 = findViewById(R.id.spn_sub_cast_1);
        mSpnSubCaste2 = findViewById(R.id.spn_sub_cast_2);
        mSpnCountry = findViewById(R.id.spn_country);
        mSpnState = findViewById(R.id.spn_state);
        mSpnCity = findViewById(R.id.spn_city);
        mEtName = findViewById(R.id.et_name);

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
                textViewAge.setText(getSelectedValue((Integer) rangeSeekBar.getSelectedMinValue()) + "-" + getSelectedValue((Integer) rangeSeekBar.getSelectedMaxValue()));
                return false;
            }
        });


        buttonFind = findViewById(R.id.bt_find);
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
                SharedPreferences.Editor editor = globalSP.edit();
                //editor.putInt(ProjectConstants.AGE, userid);
                String location = mSpnCountry.getSelectedItem().toString().trim()+"/"+mSpnState.getSelectedItem().toString().trim()+"/"+mSpnCity.getSelectedItem().toString().trim();

                editor.putString(ProjectConstants.LOCATION, location);
                if(textViewAge.getText().toString().trim().equals("")){
                    editor.putString(ProjectConstants.AGE, "0-24");
                }else{
                    editor.putString(ProjectConstants.AGE, textViewAge.getText().toString().trim());
                }
                editor.putString(ProjectConstants.SUBCASTE1, mSpnSubCaste1.getSelectedItemId() == 0 ? "" : mSpnSubCaste1.getSelectedItem().toString().trim());
                editor.putString(ProjectConstants.SUBCASTE2, mSpnSubCaste2.getSelectedItemId() == 0 ? "" : mSpnSubCaste2.getSelectedItem().toString().trim());
                editor.putString(ProjectConstants.NAME, mEtName.getText().toString().trim());
                editor.apply();

                Toast.makeText(SearchActivity.this, "Find / Search", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                finish();
            }
        });

        gap = maxAge - minAge;
    }

    public Integer getSelectedValue(int val){
        return minAge + ((val * gap)/100);
    }
}
