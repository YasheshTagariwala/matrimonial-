package com.kloudforj.matrimonial.activities;

import android.app.DatePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.kloudforj.matrimonial.R;

import java.util.Calendar;

public class UserProfileActivity extends AppCompatActivity {

    String user_id = "";
    boolean isSelf = false;
    FloatingActionButton fabEdit;
    Button buttonSave;
    ImageButton imageButtonSave;
    boolean modeEdit = false;
    RadioButton radioButtonMale,radioButtonFemale;

    LinearLayout linearLayoutAddress,linearLayoutPhone;

    ImageButton imageButtonAddress1,imageButtonAddress2,imageButtonAddress3,imageButtonCancel,imageButtonCalendar;

    TextView textViewFullName,textViewAboutMe,textViewHobby,textViewBirthDate,
            textViewAddress1,textViewAddress2,textViewAddress3,textViewPhone, textViewGender,
            textViewCountry,textViewState,textViewCity,textViewCast,textViewSubCast1,textViewSubCast2;
    EditText editTextFullName,editTextAboutMe,editTextHobby,
            editTextAddress1,editTextAddress2,editTextAddress3,editTextPhone;

    Spinner spinnerGender,spinnerCountry,spinnerState,spinnerCity,
            spinnerCast,spinnerSubCast1,spinnerSubCast2;

    RadioGroup radioGroupSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("user_id")) {
                user_id = extras.getString("user_id");
                isSelf = true;
            }
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
        spinnerCast = findViewById(R.id.spn_user_cast);
        spinnerSubCast1 = findViewById(R.id.spn_user_sub_cast_1);
        spinnerSubCast2 = findViewById(R.id.spn_user_sub_cast_2);
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
        textViewCast = findViewById(R.id.text_user_cast);
        textViewSubCast1 = findViewById(R.id.text_user_sub_cast_1);
        textViewSubCast2 = findViewById(R.id.text_user_sub_cast_2);

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
            textViewCast.setVisibility(View.GONE);
            textViewSubCast1.setVisibility(View.GONE);
            textViewSubCast2.setVisibility(View.GONE);

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
        }else {
            textViewCountry.setVisibility(View.VISIBLE);
            textViewState.setVisibility(View.VISIBLE);
            textViewCity.setVisibility(View.VISIBLE);
            textViewCast.setVisibility(View.VISIBLE);
            textViewSubCast1.setVisibility(View.VISIBLE);
            textViewSubCast2.setVisibility(View.VISIBLE);

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
}
