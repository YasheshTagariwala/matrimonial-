package com.example.minsm.matrimonial.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.minsm.matrimonial.R;
import com.example.minsm.matrimonial.utils.ProjectConstants;

public class MainActivity extends AppCompatActivity {

    private String token;
    private SharedPreferences globalSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);

        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();

    }
}
