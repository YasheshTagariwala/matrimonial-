package com.kloudforj.matrimonial.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.adapters.AdapterGridBasic;
import com.kloudforj.matrimonial.adapters.SpacingItemDecoration;
import com.kloudforj.matrimonial.utils.Tools;

import java.util.Calendar;

public class UserEditProfileActivity extends AppCompatActivity {

    TextView textViewBirthDate,textViewVerifyPhone, textViewVerifymail, textViewUserEducation;

    private RecyclerView recyclerViewUserImage;

    EditText editTextFullName, editTextAboutMe, editTextHobby,
            editTextAddress1, editTextAddress2, editTextAddress3, editTextPhone,
            editTextUserHeight,editTextUserWeight,editTextUserBirthPlace,editTextUserBirthTime,
            editTextUserJob,editTextFatherName,editTextFatherEducation,editTextFatherProfession,
            editTextFatherBirthPlace,editTextMotherName,editTextMotherEducation,editTextMotherProfession,editTextMotherBirthPlace;

    Spinner spinnerGender, spinnerCountry, spinnerState, spinnerCity,
            spinnerCast, spinnerSubCast1, spinnerSubCast2;

    RadioGroup radioGroupSex;
    RadioButton radioButtonMale, radioButtonFemale;

    ImageButton imageButtonAddress1, imageButtonAddress2,imageButtonAddress3,imageButtonCancel,imageButtonCalendar, imageButtonAddEducation, imageButtonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        recyclerViewUserImage = (RecyclerView) findViewById(R.id.recyclerView_user_image);
        recyclerViewUserImage.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewUserImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUserImage.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 5), true));
        recyclerViewUserImage.setHasFixedSize(true);
        AdapterGridBasic mAdapter = new AdapterGridBasic(this, 3);
        recyclerViewUserImage.setAdapter(mAdapter);

        radioButtonMale = findViewById(R.id.radioMale);
        radioButtonFemale = findViewById(R.id.radioFemale);
        radioGroupSex = findViewById(R.id.radioSex);

        spinnerCountry = findViewById(R.id.spn_user_country);
        spinnerState = findViewById(R.id.spn_user_state);
        spinnerCity = findViewById(R.id.spn_user_city);
        spinnerCast = findViewById(R.id.spn_user_caste);
        spinnerSubCast1 = findViewById(R.id.spn_user_sub_caste_1);
        spinnerSubCast2 = findViewById(R.id.spn_user_sub_caste_2);

        textViewVerifyPhone = findViewById(R.id.text_verify_phone);
        textViewVerifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserEditProfileActivity.this,VerificationActivity.class);
                intent.putExtra("type","phone");
                startActivity(intent);
            }
        });
        textViewVerifymail = findViewById(R.id.text_verify_mail);
        textViewVerifymail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserEditProfileActivity.this,VerificationActivity.class);
                intent.putExtra("type","mail");
                startActivity(intent);
            }
        });

        textViewBirthDate = findViewById(R.id.text_birth_date);
        textViewUserEducation = findViewById(R.id.text_user_education);

        editTextFullName = findViewById(R.id.editText_full_name);
        editTextAboutMe = findViewById(R.id.editText_about_me);
        editTextHobby = findViewById(R.id.editText_hobby);
        editTextAddress1 = findViewById(R.id.editText_address_1);
        editTextAddress2 = findViewById(R.id.editText_address_2);
        editTextAddress3 = findViewById(R.id.editText_address_3);
        editTextPhone = findViewById(R.id.editText_phone);

        editTextUserHeight = findViewById(R.id.editText_user_height);
        editTextUserWeight = findViewById(R.id.editText_user_weight);
        editTextUserBirthPlace = findViewById(R.id.editText_user_birth_place);
        editTextUserBirthTime = findViewById(R.id.editText_user_birth_time);
        editTextUserJob = findViewById(R.id.editText_user_job);
        editTextFatherName = findViewById(R.id.editText_father_name);
        editTextFatherEducation = findViewById(R.id.editText_father_education);
        editTextFatherProfession = findViewById(R.id.editText_father_profession);
        editTextFatherBirthPlace = findViewById(R.id.editText_father_birth_place);
        editTextMotherName = findViewById(R.id.editText_mother_name);
        editTextMotherEducation = findViewById(R.id.editText_mother_education);
        editTextMotherProfession = findViewById(R.id.editText_mother_profession);
        editTextMotherBirthPlace = findViewById(R.id.editText_mother_birth_place);

        imageButtonSave = findViewById(R.id.imagebutton_profile_save);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setDataEditable(false);
            }
        });

        imageButtonCancel = findViewById(R.id.imagebutton_Cancel);
        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setDataEditable(false);
            }
        });

        imageButtonAddEducation = findViewById(R.id.imagebutton_add_education);
        imageButtonAddEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEducationAdd();
            }
        });

        imageButtonCalendar = findViewById(R.id.imagebutton_calendar);
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
    }

    public void openDatePicker(){
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(UserEditProfileActivity.this,
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

    public void showEducationAdd(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Education"); //Set Alert dialog title here
        alert.setMessage("Here You Can Add new Education"); //Message here

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String srt = input.getEditableText().toString();
                textViewUserEducation.setText(textViewUserEducation.getText().toString() + "\n" + srt);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
}
