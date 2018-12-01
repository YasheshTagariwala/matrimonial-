package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.utils.ProjectConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    ImageButton imageButtonClose;
    Button buttonFind;

    private Spinner mSpnBirthYear, mSpnSubCaste1, mSpnSubCaste2;
    private ArrayList<String> listCountry, listState, listCity;
    private HashMap<Integer, String> mapCountry, mapState, mapCity;
    ArrayList<String> birthYears;
    private Spinner mSpnCountry, mSpnState, mSpnCity;
    private EditText mEtName;

    private SharedPreferences globalSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        String sex = globalSP.getString(ProjectConstants.SEX, ProjectConstants.EMPTY_STRING).trim();

        mSpnBirthYear = findViewById(R.id.spn_user_birthyear);
        birthYears = new ArrayList<>();
        birthYears.add("Select birth year");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        int upto = thisYear;

        if (!sex.equals(ProjectConstants.EMPTY_STRING) && sex.equals("M")) {
            upto = thisYear - 18; //2000
        }
        if (!sex.equals(ProjectConstants.EMPTY_STRING) && sex.equals("F")) {
            upto = thisYear - 21; //1997
        }

        for (; upto >= 1975; upto--) {
            birthYears.add(String.valueOf(upto));
        }

        ArrayAdapter<String> adapterBirthYears = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, birthYears);
        mSpnBirthYear.setAdapter(adapterBirthYears);

        mSpnSubCaste1 = findViewById(R.id.spn_sub_cast_1);
        mSpnSubCaste2 = findViewById(R.id.spn_sub_cast_2);
        mSpnCountry = findViewById(R.id.spn_country);
        mSpnState = findViewById(R.id.spn_state);
        mSpnCity = findViewById(R.id.spn_city);
        mEtName = findViewById(R.id.et_name);

        imageButtonClose = findViewById(R.id.bt_close);
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initCountryStateCity();

        buttonFind = findViewById(R.id.bt_find);
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SharedPreferences.Editor editor = globalSP.edit();
                //editor.putString(ProjectConstants.LOCATION, location);
                //editor.putInt(ProjectConstants.BIRTH_YEAR, Integer.parseInt(mSpnBirthYear.getSelectedItem().toString()));
                //editor.putString(ProjectConstants.SUBCASTE1, mSpnSubCaste1.getSelectedItemId() == 0 ? "" : mSpnSubCaste1.getSelectedItem().toString().trim());
                //editor.putString(ProjectConstants.SUBCASTE2, mSpnSubCaste2.getSelectedItemId() == 0 ? "" : mSpnSubCaste2.getSelectedItem().toString().trim());
                //editor.putString(ProjectConstants.NAME, mEtName.getText().toString().trim());
                //editor.apply();

                Toast.makeText(SearchActivity.this, "Find / Search", Toast.LENGTH_SHORT).show();

                String location = mSpnCountry.getSelectedItem().toString().trim() + "/" + mSpnState.getSelectedItem().toString().trim() + "/" + mSpnCity.getSelectedItem().toString().trim();
                Intent intentMain = new Intent(SearchActivity.this, MainActivity.class);
                intentMain.putExtra(ProjectConstants.LOCATION, location);
                if (!mSpnBirthYear.getSelectedItem().toString().equals("Select birth year")) {
                    int byear = Integer.parseInt(mSpnBirthYear.getSelectedItem().toString());
                    intentMain.putExtra(ProjectConstants.BIRTH_YEAR, byear);
                }
                intentMain.putExtra(ProjectConstants.SUBCASTE1, mSpnSubCaste1.getSelectedItemId() == 0 ? "" : mSpnSubCaste1.getSelectedItem().toString().trim());
                intentMain.putExtra(ProjectConstants.SUBCASTE2, mSpnSubCaste2.getSelectedItemId() == 0 ? "" : mSpnSubCaste2.getSelectedItem().toString().trim());
                intentMain.putExtra(ProjectConstants.NAME, mEtName.getText().toString().trim());

                startActivity(intentMain);

                finish();
            }
        });
    }

    public void initCountryStateCity() {

    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getResources().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
