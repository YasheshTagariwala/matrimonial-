package com.kloudforj.matrimonial.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.entities.UserProfile;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class UserProfileActivity extends AppCompatActivity {

    private String token;
    private int user_id;
    private SharedPreferences globalSP;
    boolean isSelf = false;
    private Call userDetailsRequestCall;

    private ProgressBar mUserProfileActvityProgressBar;

    FloatingActionButton fabEdit;
    Button buttonSave;
    ImageButton imageButtonSave;
    boolean modeEdit = false;
    RadioButton radioButtonMale, radioButtonFemale;

    LinearLayout linearLayoutAddress, linearLayoutPhone;

    ImageButton imageButtonAddress1, imageButtonAddress2,imageButtonAddress3,imageButtonCancel,imageButtonCalendar;

    TextView textViewFullName, textViewAboutMe, textViewHobby, textViewBirthDate,
            textViewAddress1, textViewAddress2, textViewAddress3, textViewPhone, textViewGender,
            textViewCountry, textViewState, textViewCity, textViewCaste, textViewSubCaste1, textViewSubCaste2;
    EditText editTextFullName, editTextAboutMe, editTextHobby,
            editTextAddress1, editTextAddress2, editTextAddress3, editTextPhone;

    Spinner spinnerGender, spinnerCountry, spinnerState, spinnerCity,
            spinnerCast, spinnerSubCast1, spinnerSubCast2;

    RadioGroup radioGroupSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mUserProfileActvityProgressBar = (ProgressBar) findViewById(R.id.pb_userprofile_activity);
        if (mUserProfileActvityProgressBar != null) {
            mUserProfileActvityProgressBar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(UserProfileActivity.this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("user_id")) {
                user_id = extras.getInt("user_id");
            }
        } else {
            user_id = globalSP.getInt(ProjectConstants.USERID, 0);
            isSelf = true;
        }

        radioButtonMale = findViewById(R.id.radioMale);
        radioButtonFemale = findViewById(R.id.radioFemale);
        radioGroupSex = findViewById(R.id.radioSex);

        linearLayoutAddress = findViewById(R.id.linearlayout_addres);
        linearLayoutPhone = findViewById(R.id.linearlayout_phone);

        imageButtonAddress1 = findViewById(R.id.imagebutton_address_1);
        imageButtonAddress2 = findViewById(R.id.imagebutton_address_2);
        imageButtonAddress3 = findViewById(R.id.imagebutton_address_3);
        imageButtonCancel = findViewById(R.id.imagebutton_Cancel);
        imageButtonCalendar = findViewById(R.id.imagebutton_calendar);
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

//        spinnerGender = findViewById(R.id.spn_gender);
        spinnerCountry = findViewById(R.id.spn_user_country);
        spinnerState = findViewById(R.id.spn_user_state);
        spinnerCity = findViewById(R.id.spn_user_city);
        spinnerCast = findViewById(R.id.spn_user_caste);
        spinnerSubCast1 = findViewById(R.id.spn_user_sub_caste_1);
        spinnerSubCast2 = findViewById(R.id.spn_user_sub_caste_2);
//        spinnerGender.setClickable(false);
        spinnerCountry.setClickable(false);
        spinnerState.setClickable(false);
        spinnerCity.setClickable(false);
        spinnerCast.setClickable(false);
        spinnerSubCast1.setClickable(false);
        spinnerSubCast2.setClickable(false);

        textViewFullName = findViewById(R.id.text_full_name);
        textViewAboutMe = findViewById(R.id.text_about_me);
        textViewHobby = findViewById(R.id.text_hobby);
        textViewBirthDate = findViewById(R.id.text_birth_date);
        textViewAddress1 = findViewById(R.id.text_address_1);
        textViewAddress2 = findViewById(R.id.text_address_2);
        textViewAddress3 = findViewById(R.id.text_address_3);
        textViewPhone = findViewById(R.id.text_phone);
        textViewGender = findViewById(R.id.textview_gender);

        textViewCountry = findViewById(R.id.text_user_country);
        textViewState = findViewById(R.id.text_user_state);
        textViewCity = findViewById(R.id.text_user_city);
        textViewCaste = findViewById(R.id.text_user_caste);
        textViewSubCaste1 = findViewById(R.id.text_user_sub_caste_1);
        textViewSubCaste2 = findViewById(R.id.text_user_sub_caste_2);

        editTextFullName = findViewById(R.id.editText_full_name);
        editTextAboutMe = findViewById(R.id.editText_about_me);
        editTextHobby = findViewById(R.id.editText_hobby);
        editTextAddress1 = findViewById(R.id.editText_address_1);
        editTextAddress2 = findViewById(R.id.editText_address_2);
        editTextAddress3 = findViewById(R.id.editText_address_3);
        editTextPhone = findViewById(R.id.editText_phone);

//        buttonSave = findViewById(R.id.button_profile_save);
//        buttonSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setDataEditable(false);
//            }
//        });

        imageButtonSave = findViewById(R.id.imagebutton_profile_save);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataEditable(false);
            }
        });

        fabEdit = findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataEditable(true);
            }
        });

        if(isSelf){
            fabEdit.setVisibility(View.VISIBLE);
        }else {
            fabEdit.setVisibility(View.GONE);
            linearLayoutAddress.setVisibility(View.GONE);
            linearLayoutPhone.setVisibility(View.GONE);
        }

//        textViewBirthDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isSelf && modeEdit)
//                    openDatePicjker();
//            }
//        });

        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataEditable(false);
            }
        });

        fetchUserDetails();
    }

    public void setDataEditable(boolean canEdit) {
//        spinnerGender.setClickable(canEdit);
        spinnerCountry.setClickable(canEdit);
        spinnerState.setClickable(canEdit);
        spinnerCity.setClickable(canEdit);
        spinnerCast.setClickable(canEdit);
        spinnerSubCast1.setClickable(canEdit);
        spinnerSubCast2.setClickable(canEdit);
        modeEdit = canEdit;
        if (canEdit){
            textViewCountry.setVisibility(View.GONE);
            textViewState.setVisibility(View.GONE);
            textViewCity.setVisibility(View.GONE);
            textViewCaste.setVisibility(View.GONE);
            textViewSubCaste1.setVisibility(View.GONE);
            textViewSubCaste2.setVisibility(View.GONE);

            spinnerCountry.setVisibility(View.VISIBLE);
            spinnerState.setVisibility(View.VISIBLE);
            spinnerCity.setVisibility(View.VISIBLE);
            spinnerCast.setVisibility(View.VISIBLE);
            spinnerSubCast1.setVisibility(View.VISIBLE);
            spinnerSubCast2.setVisibility(View.VISIBLE);

            imageButtonCancel.setVisibility(View.VISIBLE);
            radioButtonMale.setClickable(true);
            radioButtonFemale.setClickable(true);
            imageButtonCalendar.setVisibility(View.VISIBLE);

            imageButtonAddress1.setBackgroundResource(R.drawable.ic_edit_location_white_24dp);
            imageButtonAddress2.setBackgroundResource(R.drawable.ic_edit_location_white_24dp);
            imageButtonAddress3.setBackgroundResource(R.drawable.ic_edit_location_white_24dp);

            fabEdit.setVisibility(View.GONE);
            imageButtonSave.setVisibility(View.VISIBLE);
//            buttonSave.setVisibility(View.VISIBLE);

            textViewFullName.setVisibility(View.GONE);
            editTextFullName.setVisibility(View.VISIBLE);

            textViewAboutMe.setVisibility(View.GONE);
            editTextAboutMe.setVisibility(View.VISIBLE);

            textViewHobby.setVisibility(View.GONE);
            editTextHobby.setVisibility(View.VISIBLE);

            textViewAddress1.setVisibility(View.GONE);
            editTextAddress1.setVisibility(View.VISIBLE);

            textViewAddress2.setVisibility(View.GONE);
            editTextAddress2.setVisibility(View.VISIBLE);

            textViewAddress3.setVisibility(View.GONE);
            editTextAddress3.setVisibility(View.VISIBLE);

            textViewPhone.setVisibility(View.GONE);
            editTextPhone.setVisibility(View.VISIBLE);

            textViewGender.setVisibility(View.GONE);
            radioGroupSex.setVisibility(View.VISIBLE);
        } else {
            textViewCountry.setVisibility(View.VISIBLE);
            textViewState.setVisibility(View.VISIBLE);
            textViewCity.setVisibility(View.VISIBLE);
            textViewCaste.setVisibility(View.VISIBLE);
            textViewSubCaste1.setVisibility(View.VISIBLE);
            textViewSubCaste1.setVisibility(View.VISIBLE);

            spinnerCountry.setVisibility(View.GONE);
            spinnerState.setVisibility(View.GONE);
            spinnerCity.setVisibility(View.GONE);
            spinnerCast.setVisibility(View.GONE);
            spinnerSubCast1.setVisibility(View.GONE);
            spinnerSubCast2.setVisibility(View.GONE);

            imageButtonCancel.setVisibility(View.GONE);
            radioButtonMale.setClickable(false);
            radioButtonFemale.setClickable(false);
            imageButtonCalendar.setVisibility(View.GONE);

            imageButtonAddress1.setBackgroundResource(R.drawable.ic_place_white_24dp);
            imageButtonAddress2.setBackgroundResource(R.drawable.ic_place_white_24dp);
            imageButtonAddress3.setBackgroundResource(R.drawable.ic_place_white_24dp);

            fabEdit.setVisibility(View.VISIBLE);
            imageButtonSave.setVisibility(View.GONE);
//            buttonSave.setVisibility(View.GONE);

            textViewFullName.setText(editTextFullName.getText());
            textViewFullName.setVisibility(View.VISIBLE);
            editTextFullName.setVisibility(View.GONE);

            textViewAboutMe.setText(editTextAboutMe.getText());
            textViewAboutMe.setVisibility(View.VISIBLE);
            editTextAboutMe.setVisibility(View.GONE);

            textViewHobby.setText(editTextHobby.getText());
            textViewHobby.setVisibility(View.VISIBLE);
            editTextHobby.setVisibility(View.GONE);

            textViewAddress1.setText(editTextAddress1.getText());
            textViewAddress1.setVisibility(View.VISIBLE);
            editTextAddress1.setVisibility(View.GONE);

            textViewAddress2.setText(editTextAddress2.getText());
            textViewAddress2.setVisibility(View.VISIBLE);
            editTextAddress2.setVisibility(View.GONE);

            textViewAddress3.setText(editTextAddress3.getText());
            textViewAddress3.setVisibility(View.VISIBLE);
            editTextAddress3.setVisibility(View.GONE);

            textViewPhone.setText(editTextPhone.getText());
            textViewPhone.setVisibility(View.VISIBLE);
            editTextPhone.setVisibility(View.GONE);

            textViewGender.setVisibility(View.VISIBLE);
            radioGroupSex.setVisibility(View.GONE);
        }
    }

    public void openDatePicker(){
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(UserProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox

                        if (year < mYear)
                            view.updateDate(mYear,mMonth,mDay);

                        if (monthOfYear < mMonth && year == mYear)
                            view.updateDate(mYear,mMonth,mDay);

                        if (dayOfMonth < mDay && year == mYear && monthOfYear == mMonth)
                            view.updateDate(mYear,mMonth,mDay);

                        textViewBirthDate.setText(String.valueOf(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));

                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();
    }

    public void onBackPressed() {
        if (modeEdit) {
            setDataEditable(false);
        }else{
            super.onBackPressed();
            return;
        }
    }

    private void fetchUserDetails() {

        if(DetectConnection.checkInternetConnection(UserProfileActivity.this)) {

            OkHttpClient clientUserDetails = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.USER_PROFILE_URL + ProjectConstants.SLASH + user_id).newBuilder();

            String urlUserDetails = urlBuilder.build().toString(); // URL is converted to String
            /*Log.e("URL UserList : ", urlUserDetails);*/

            final Request requestUserDetails = new Request.Builder()
                    .url(urlUserDetails)
                    .header(ProjectConstants.APITOKEN, token)
                    .build();

            userDetailsRequestCall = clientUserDetails.newCall(requestUserDetails);
            userDetailsRequestCall.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    //Log.e("onFailure", "in ", e);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()) {
                        enableComponents(getResources().getString(R.string.something_went_wrong));
                        throw new IOException("Unexpected code " + response);
                    } else {

                        String result = response.body().string(); // response is converted to string
                        Log.e("resp : ", result);

                        if(result != null) {

                            try {

                                final JSONObject jsonUserProfile = new JSONObject(result);

                                final Boolean auth = jsonUserProfile.getBoolean(ProjectConstants.AUTH);
                                final String message = jsonUserProfile.getString(ProjectConstants.MESSAGE);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mUserProfileActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                        if(auth) {
                                            try {
                                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                                JSONObject jsonObjectData = jsonUserProfile.getJSONObject(ProjectConstants.DATA);
                                                JSONObject jsonObjectUserProfile = jsonObjectData.getJSONObject(ProjectConstants.USER_PROFILE);
                                                /*JSONArray jsonObjectUserEducation = jsonObjectData.getJSONArray(ProjectConstants.USER_EDUCATION);
                                                JSONArray jsonObjectUserHobby = jsonObjectData.getJSONArray(ProjectConstants.USER_HOBBY);
                                                JSONObject jsonObjectUserFamily = jsonObjectData.getJSONObject(ProjectConstants.USER_FAMILY);
                                                JSONObject jsonObjectUserExtra = jsonObjectData.getJSONObject(ProjectConstants.USER_EXTRA);*/

                                                Gson gson = new Gson();
                                                UserProfile userProfile = gson.fromJson(jsonObjectUserProfile.toString(), UserProfile.class);

                                                textViewFullName.setText(String.valueOf(userProfile.getFirst_name()+" "+userProfile.getMiddle_name()+" "+userProfile.getLast_name()));
                                                textViewBirthDate.setText(userProfile.getDate_of_birth());
                                                textViewGender.setText((userProfile.getSex().toLowerCase().equals("m")?"Male":"Female"));
                                                textViewCountry.setText(userProfile.getCountry());
                                                textViewState.setText(userProfile.getState());
                                                textViewCity.setText(userProfile.getCity());
                                                textViewCaste.setText(userProfile.getCaste());
                                                textViewSubCaste1.setText(userProfile.getSub_caste1());
                                                textViewSubCaste2.setText(userProfile.getSub_caste2());

                                            } catch (JSONException e) {
                                                enableComponents(getResources().getString(R.string.something_went_wrong));
                                                e.printStackTrace();
                                            }

                                        } else {

                                        }

                                    }
                                });


                            } catch (JSONException e) {
                                enableComponents(getResources().getString(R.string.something_went_wrong));
                            }
                        } else {
                            enableComponents(getResources().getString(R.string.something_went_wrong));
                        }
                    }

                }
            });

        }
    }

    /**
     * Toast message and rogressbar invisible
     *
     * @param msg
     */
    private void enableComponents(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUserProfileActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled
                Toast.makeText(UserProfileActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
