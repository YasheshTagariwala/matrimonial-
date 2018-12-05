package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    ImageButton imageButtonClose;
    Button buttonFind;

    private Spinner mSpnBirthYear, mSpnSubCaste1, mSpnSubCaste2, mSpnCaste;
    ArrayList<String> birthYears, castes, subCastes1, subCastes2, country, state, city;
    private Spinner mSpnCountry, mSpnState, mSpnCity;
    private EditText mEtName;
    private int countrySelected, stateSelected, citySelected;

    private SharedPreferences globalSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        String sex = globalSP.getString(ProjectConstants.SEX, ProjectConstants.EMPTY_STRING).trim();

        countrySelected = stateSelected = citySelected = 0;

        mSpnBirthYear = findViewById(R.id.spn_user_birthyear);
        birthYears = new ArrayList<>();
        castes = new ArrayList<>();
        subCastes1 = new ArrayList<>();
        subCastes2 = new ArrayList<>();
        country = new ArrayList<>();
        state = new ArrayList<>();
        city = new ArrayList<>();
        birthYears.add("Select birth year");
        castes.add("Select Caste");
        subCastes1.add("Select Sub Caste 1");
        subCastes2.add("Select Sub Caste 2");
        country.add("Select Country");
        state.add("Select State");
        city.add("Select City");
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

        mSpnCaste = findViewById(R.id.spn_cast);
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
                String location;
                if (citySelected == 0 && stateSelected == 0 && citySelected == 0) {
                    location = "";
                } else if (citySelected > 0 && stateSelected == 0 && citySelected == 0) {
                    location = mSpnCountry.getSelectedItem().toString().trim();
                } else if (citySelected > 0 && stateSelected > 0 && citySelected == 0) {
                    location = mSpnCountry.getSelectedItem().toString().trim() + "/" + mSpnState.getSelectedItem().toString().trim();
                } else {
                    location = mSpnCountry.getSelectedItem().toString().trim() + "/" + mSpnState.getSelectedItem().toString().trim() + "/" + mSpnCity.getSelectedItem().toString().trim();
                }
                Intent intentMain = new Intent(SearchActivity.this, MainActivity.class);
                intentMain.putExtra(ProjectConstants.LOCATION, location);
                if (!mSpnBirthYear.getSelectedItem().toString().equals("Select birth year")) {
                    int byear = Integer.parseInt(mSpnBirthYear.getSelectedItem().toString());
                    intentMain.putExtra(ProjectConstants.BIRTH_YEAR, byear);
                }
                intentMain.putExtra(ProjectConstants.SUBCASTE1, mSpnSubCaste1.getSelectedItemId() == 0 ? "" : mSpnSubCaste1.getSelectedItem().toString().trim());
                intentMain.putExtra(ProjectConstants.CAST, mSpnCaste.getSelectedItemId() == 0 ? "" : mSpnCaste.getSelectedItem().toString().trim());
                intentMain.putExtra(ProjectConstants.SUBCASTE2, mSpnSubCaste2.getSelectedItemId() == 0 ? "" : mSpnSubCaste2.getSelectedItem().toString().trim());
                intentMain.putExtra(ProjectConstants.NAME, mEtName.getText().toString().trim());

                startActivity(intentMain);

                finish();
            }
        });

        getCasteAndSubCaste();
    }

    public void getCasteAndSubCaste() {
        SharedPreferences globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        String token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.GET_CASTE_SUBCASTE).newBuilder();
        if (DetectConnection.checkInternetConnection(this)) {
            new ProjectConstants.getDataFromServer(new JSONObject(), new GetCasteAndSubCaste(), this).execute(urlBuilder.build().toString(), token);
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    public class GetCasteAndSubCaste implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
                Log.e("Error", response.toString());
            } else {

                String result = response.body().string(); // response is converted to string
//                Log.e("resp : ", result);

                if (result != null) {
                    try {
                        final JSONObject jsonUserProfile = new JSONObject(result);

                        final Boolean auth = jsonUserProfile.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonUserProfile.getString(ProjectConstants.MESSAGE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (auth) {
                                    try {
                                        JSONObject jsonObjectData = jsonUserProfile.getJSONObject(ProjectConstants.DATA);
                                        JSONArray casteArray = jsonObjectData.getJSONArray("caste");
                                        JSONArray subCasteArray1 = jsonObjectData.getJSONArray("sub_caste1");
                                        JSONArray subCasteArray2 = jsonObjectData.getJSONArray("sub_caste2");

                                        for (int i = 0; i < casteArray.length(); i++) {
                                            castes.add(casteArray.get(i).toString());
                                        }
                                        for (int i = 0; i < subCasteArray1.length(); i++) {
                                            subCastes1.add(subCasteArray1.get(i).toString());
                                        }
                                        for (int i = 0; i < subCasteArray2.length(); i++) {
                                            subCastes2.add(subCasteArray2.get(i).toString());
                                        }

                                        ArrayAdapter<String> adapterCastes = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_dropdown_item, castes);
                                        mSpnCaste.setAdapter(adapterCastes);
                                        ArrayAdapter<String> adapterSubCastes1 = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_dropdown_item, subCastes1);
                                        mSpnSubCaste1.setAdapter(adapterSubCastes1);
                                        ArrayAdapter<String> adapterSubCastes2 = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_dropdown_item, subCastes2);
                                        mSpnSubCaste2.setAdapter(adapterSubCastes2);

                                        initCountryStateCity();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void initCountryStateCity() {
        loadCountries();
    }


    public void loadCountries() {
        country.clear();
        try {
            JSONArray countryArray = new JSONObject(loadJSONFromAsset("countries.json")).getJSONArray("countries");
            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject countryObject = new JSONObject(countryArray.get(i).toString());
                country.add(countryObject.getString("name"));
            }

            ArrayAdapter<String> adapterCountry = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_dropdown_item, country);
            mSpnCountry.setAdapter(adapterCountry);

            mSpnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        countrySelected = stateSelected = citySelected = 0;
                    } else {
                        loadStates(position);
                        countrySelected = position;
                        stateSelected = 0;
                        citySelected = 0;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadStates(int country) {
        state.clear();
        try {
            JSONArray stateArray = new JSONObject(loadJSONFromAsset("states.json")).getJSONArray("states");
            for (int i = 0; i < stateArray.length(); i++) {
                JSONObject stateObject = new JSONObject(stateArray.get(i).toString());
                try {
                    if (stateObject.getInt("country_id") == country) {
                        state.add(stateObject.getString("name"));
                    }
                }catch (Exception e){
                    Log.e("country_id",stateObject.toString());
                }
            }

            ArrayAdapter<String> adapterState = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_dropdown_item, state);
            mSpnState.setAdapter(adapterState);

            mSpnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadCity(position);
                    stateSelected = position;
                    citySelected = 0;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadCity(int state) {
        city.clear();
        try {
            JSONArray cityArray = new JSONObject(loadJSONFromAsset("cities.json")).getJSONArray("cities");
            for (int i = 0; i < cityArray.length(); i++) {
                JSONObject cityObject = new JSONObject(cityArray.get(i).toString());
                try {
                    if (cityObject.getInt("state_id") == state) {
                        city.add(cityObject.getString("name"));
//                        city.add(i, cityObject.getString("name"));
                    }
                }catch (Exception e){
                    Log.e("state_id",cityObject.toString());
                }
            }

            ArrayAdapter<String> adapterCity = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_spinner_dropdown_item, city);
            mSpnCity.setAdapter(adapterCity);

            mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    citySelected = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
