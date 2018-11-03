package com.kloudforj.matrimonial.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.adapters.AdapterGridBasic;
import com.kloudforj.matrimonial.adapters.SpacingItemDecoration;
import com.kloudforj.matrimonial.entities.UserProfile;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;
import com.kloudforj.matrimonial.utils.Tools;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UserEditProfileActivity extends AppCompatActivity {

    TextView textViewBirthDate;
    //    TextView textViewUserEducation;
    LinearLayout linearLayoutEducationHolder, linearLayoutHobbiesHolder;
    List<View> arrayOfEducationView = new ArrayList<View>();
    List<View> arrayOfHobbyView = new ArrayList<View>();
    AdapterGridBasic mAdapter;

    private RecyclerView recyclerViewUserImage;

    //    EditText editTextHobby;
    EditText editTextFirstName, editTextMiddleName, editTextLastName, editTextAboutMe, editTextEmail,
            editTextAddress1, editTextAddress2, editTextAddress3, editTextPhone,
            editTextUserHeight, editTextUserWeight, editTextUserBirthPlace, editTextUserBirthTime,
            editTextUserJob, editTextFatherName, editTextFatherEducation, editTextFatherProfession,
            editTextFatherBirthPlace, editTextMotherName, editTextMotherEducation, editTextMotherProfession, editTextMotherBirthPlace;

    Spinner spinnerGender, spinnerCountry, spinnerState, spinnerCity,
            spinnerCast, spinnerSubCast1, spinnerSubCast2;

    RadioGroup radioGroupSex;
    RadioButton radioButtonMale, radioButtonFemale;

    private SharedPreferences globalSP;

    ImageButton imageButtonAddress1, imageButtonAddress2, imageButtonAddress3,
            imageButtonCancel, imageButtonCalendar, imageButtonAddEducation, imageButtonAddHobbies, imageButtonSave,
            imageButtonPhoneVerified, imageButtonEmailVerified, imageButtonPhoneNotVerified, imageButtonEmailNotVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        linearLayoutEducationHolder = findViewById(R.id.linearlayout_education_holder);
        linearLayoutHobbiesHolder = findViewById(R.id.linearlayout_hobbies_holder);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);

        recyclerViewUserImage = (RecyclerView) findViewById(R.id.recyclerView_user_image);
        recyclerViewUserImage.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewUserImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUserImage.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 5), true));
        recyclerViewUserImage.setHasFixedSize(true);
        mAdapter = new AdapterGridBasic(this, 3);
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

        textViewBirthDate = findViewById(R.id.text_birth_date);
//        textViewUserEducation = findViewById(R.id.text_user_education);

        editTextFirstName = findViewById(R.id.editText_first_name);
        editTextMiddleName = findViewById(R.id.editText_middle_name);
        editTextLastName = findViewById(R.id.editText_last_name);
        editTextEmail = findViewById(R.id.editText_user_mail);
        editTextAboutMe = findViewById(R.id.editText_about_me);
//        editTextHobby = findViewById(R.id.editText_hobby);
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
                if (validateData()) {
                    Log.e("1", "1");
                    JSONObject jsonUserProfileRequest = null;
                    try {
                        UserProfile userProfile = new UserProfile();

                        //Profile
                        UserProfile.Profile profile = userProfile.new Profile();
                        profile.setFirst_name(editTextFirstName.getText().toString().trim());
                        profile.setMiddle_name(editTextMiddleName.getText().toString().trim());
                        profile.setLast_name(editTextLastName.getText().toString().trim());
                        if (radioButtonMale.isChecked()) {
                            profile.setSex("M");
                        } else {
                            profile.setSex("F");
                        }
                        profile.setDate_of_birth(textViewBirthDate.getText().toString().trim());
                        profile.setPhone_number(editTextPhone.getText().toString().trim());
                        profile.setCaste(spinnerCast.getSelectedItem().toString().trim());
                        profile.setSub_caste1(spinnerSubCast1.getSelectedItem().toString().trim());
                        profile.setSub_caste2(spinnerSubCast2.getSelectedItem().toString().trim());
                        userProfile.setProfile(profile);
                        //userProfile.getProfile().setFirst_name(editTextFirstName.getText().toString().trim());
                        //userProfile.getProfile().setMiddle_name(editTextMiddleName.getText().toString().trim());
                        //userProfile.getProfile().setLast_name(editTextLastName.getText().toString().trim());

                        //Extra
                        UserProfile.Extra extra = userProfile.new Extra();
                        extra.setHeight(editTextUserHeight.getText().toString().trim());
                        extra.setWeight(editTextUserWeight.getText().toString().trim());
                        extra.setBirth_place(editTextUserBirthPlace.getText().toString().trim());
                        extra.setBirth_time(editTextUserBirthTime.getText().toString().trim());
                        extra.setCurrent_job(editTextUserJob.getText().toString().trim());
                        extra.setAbout_me(editTextAboutMe.getText().toString().trim());
                        userProfile.setExtra(extra);

                        //Education
                        //UserProfile.Education education = userProfile.new Education(); //getEducation();
                        int i = 0; List<String> tempedu = new ArrayList<>();
                        for (View educations : arrayOfEducationView) {
                            TextView text = educations.findViewById(R.id.text_main_content);
                            tempedu.add(text.getText().toString().trim());
                            i = i + 1;
                        }
                        userProfile.setEducation(tempedu);

                        //Hobbies
                        //UserProfile.Hobby hobbies = userProfile.new Hobby(); //getHobbies();
                        int j = 0; List<String> temphobby = new ArrayList<>();
                        for (View hobbie : arrayOfHobbyView) {
                            TextView text = hobbie.findViewById(R.id.text_main_content);
                            temphobby.add(text.getText().toString().trim());
                            j = j + 1;
                        }
                        userProfile.setHobby(temphobby);

                        //Families
                        UserProfile.Family family = userProfile.new Family();
                        family.setFather_name(editTextFatherName.getText().toString().trim());
                        family.setFather_education(editTextFatherEducation.getText().toString().trim());
                        family.setFather_profession(editTextFatherProfession.getText().toString().trim());
                        family.setFather_birth_place(editTextFatherBirthPlace.getText().toString().trim());
                        family.setMother_name(editTextMotherName.getText().toString().trim());
                        family.setMother_education(editTextMotherEducation.getText().toString().trim());
                        family.setMother_profession(editTextMotherProfession.getText().toString().trim());
                        family.setMother_birth_place(editTextMotherBirthPlace.getText().toString().trim());
                        userProfile.setFamily(family);

                        jsonUserProfileRequest = new JSONObject(new Gson().toJson(userProfile));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.UPDATE_PROFILE_URL).newBuilder();
                    if (DetectConnection.checkInternetConnection(UserEditProfileActivity.this)) {
                        Log.e("2", "2");
                        new ProjectConstants.getDataFromServer(jsonUserProfileRequest, new UpdateProfileCall(), UserEditProfileActivity.this).execute(urlBuilder.build().toString());
                        Log.e("3", "3");
                    } else {
                        Toast.makeText(UserEditProfileActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        imageButtonCancel = findViewById(R.id.imagebutton_Cancel);
        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserEditProfileActivity.this, UserProfileActivity.class));
            }
        });

        imageButtonAddEducation = findViewById(R.id.imagebutton_add_education);
        imageButtonAddEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEducationAdd();
            }
        });

        imageButtonAddHobbies = findViewById(R.id.imagebutton_add_hobbies);
        imageButtonAddHobbies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHobbiesAdd();
            }
        });

        imageButtonCalendar = findViewById(R.id.imagebutton_calendar);
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        imageButtonPhoneNotVerified = findViewById(R.id.phone_number_not_verified);
        imageButtonPhoneVerified = findViewById(R.id.phone_number_verified);
        imageButtonEmailVerified = findViewById(R.id.email_verified);
        imageButtonEmailNotVerified = findViewById(R.id.email_not_verified);

        imageButtonPhoneNotVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserEditProfileActivity.this, VerificationActivity.class);
                intent.putExtra("type", "phone");
                startActivity(intent);
            }
        });

        imageButtonEmailNotVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserEditProfileActivity.this, VerificationActivity.class);
                intent.putExtra("type", "mail");
                startActivity(intent);
            }
        });

        if (!globalSP.getBoolean(ProjectConstants.USER_PROFILE, false)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.profile_compulsory), Toast.LENGTH_LONG).show();
            imageButtonCancel.setVisibility(View.GONE);
        }

        if (getIntent().hasExtra("userProfile"))  {
            if(getIntent().getExtras().getString("userProfile") != null) {
                Gson gson = new Gson();
                UserProfile userProfile = gson.fromJson(getIntent().getExtras().getString("userProfile"), UserProfile.class);
                editTextFirstName.setText(userProfile.getProfile().getFirst_name());
                editTextMiddleName.setText(userProfile.getProfile().getMiddle_name());
                editTextLastName.setText(userProfile.getProfile().getLast_name());
                if (userProfile.getProfile().getSex().toLowerCase().equals("m")) {
                    radioButtonMale.setChecked(true);
                    radioButtonFemale.setChecked(false);
                } else {
                    radioButtonMale.setChecked(false);
                    radioButtonFemale.setChecked(true);
                }
                textViewBirthDate.setText(userProfile.getProfile().getDate_of_birth());
                editTextPhone.setText(userProfile.getProfile().getPhone_number());
                editTextEmail.setText(globalSP.getString(ProjectConstants.EMAIL, ""));
                if (userProfile.getProfile().getPhone_number_verified() == 1) {
                    imageButtonPhoneVerified.setVisibility(View.VISIBLE);
                    imageButtonPhoneNotVerified.setVisibility(View.GONE);
                } else {
                    imageButtonPhoneVerified.setVisibility(View.GONE);
                    imageButtonPhoneNotVerified.setVisibility(View.VISIBLE);
                }

                if (userProfile.getProfile().getEmail_verified() == 1) {
                    imageButtonEmailVerified.setVisibility(View.VISIBLE);
                    imageButtonEmailNotVerified.setVisibility(View.GONE);
                } else {
                    imageButtonEmailVerified.setVisibility(View.GONE);
                    imageButtonEmailNotVerified.setVisibility(View.VISIBLE);
                }

                editTextUserHeight.setText(userProfile.getExtra().getHeight());
                editTextUserWeight.setText(userProfile.getExtra().getWeight());
                editTextUserBirthPlace.setText(userProfile.getExtra().getBirth_place());
                editTextUserBirthTime.setText(userProfile.getExtra().getBirth_time());
                editTextUserJob.setText(userProfile.getExtra().getCurrent_job());
                editTextAboutMe.setText(userProfile.getExtra().getAbout_me());

                /*for (UserProfile.Education education : userProfile.getEducation()) {
                    addCell(education.getEducation(), true);
                }

                for (UserProfile.Hobbies hobbies : userProfile.getHobbies()) {
                    addCell(hobbies.getHobby(), false);
                }*/

                editTextFatherName.setText(userProfile.getFamily().getFather_name());
                editTextFatherEducation.setText(userProfile.getFamily().getFather_education());
                editTextFatherProfession.setText(userProfile.getFamily().getFather_profession());
                editTextFatherBirthPlace.setText(userProfile.getFamily().getFather_birth_place());
                editTextMotherName.setText(userProfile.getFamily().getMother_name());
                editTextMotherEducation.setText(userProfile.getFamily().getMother_education());
                editTextMotherProfession.setText(userProfile.getFamily().getMother_profession());
                editTextMotherBirthPlace.setText(userProfile.getFamily().getMother_birth_place());
            }
        }

    }

    public class UpdateProfileCall implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                String result = response.body().string(); // response is converted to string
                Log.e("Response : ", result);

                if (result != null) {

                    try {

                        final JSONObject jsonLogin = new JSONObject(result);

                        final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonLogin.getString(ProjectConstants.MESSAGE);
                        if (auth) {
                            Toast.makeText(UserEditProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UserEditProfileActivity.this, UserProfileActivity.class));
                            finish();
                        }

                    } catch (JSONException e) {
                        Log.e("error", e.getMessage());
                    }
                } else {
                    Log.e("error", "error");
                }
            }
        }
    }

    private boolean validateData() {
        String toValidate = editTextFirstName.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.first_name_empty));
        }

        toValidate = editTextMiddleName.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.middle_name_empty));
        }

        toValidate = editTextLastName.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.last_name_empty));
        }

        toValidate = textViewBirthDate.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.birth_date_empty));
        } else {
            if (radioButtonMale.isChecked()) {
                if (validateAge(toValidate) < 21) {
                    return showError(getResources().getString(R.string.birth_date_male));
                }
            } else {
                if (validateAge(toValidate) < 18) {
                    return showError(getResources().getString(R.string.birth_date_female));
                }
            }
        }

        toValidate = editTextPhone.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.phone_number_empty));
        }

        toValidate = editTextEmail.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.email_empty));
        }

        toValidate = editTextUserHeight.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.height_empty));
        }

        toValidate = editTextUserWeight.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.weight_empty));
        }

        toValidate = editTextUserBirthPlace.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.user_birth_place_empty));
        }

        toValidate = editTextUserBirthTime.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.user_birth_time_empty));
        }

        toValidate = getEducations();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.user_education_empty));
        }

        toValidate = getHobbies();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.user_hobby_empty));
        }

        toValidate = editTextFatherName.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.user_father_empty));
        }

        toValidate = editTextFatherProfession.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.user_father_profession_empty));
        }

        toValidate = editTextMotherName.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.user_mother_empty));
        }

        toValidate = editTextMotherProfession.getText().toString();
        if (toValidate.trim().equals("") || toValidate.isEmpty()) {
            return showError(getResources().getString(R.string.user_mother_profession_empty));
        }

        return true;
    }

    private boolean showError(String message) {
        Toast.makeText(UserEditProfileActivity.this, message, Toast.LENGTH_LONG).show();
        return false;
    }

    private int validateAge(String dob) {
        int year = Integer.parseInt(dob.split("-")[2]);
        int month = Integer.parseInt(dob.split("-")[1]);
        int day = Integer.parseInt(dob.split("-")[0]);

        final Calendar birthDay = Calendar.getInstance();
        birthDay.set(year, month, day);
        final Calendar current = Calendar.getInstance();
        int age = current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        if (birthDay.get(Calendar.MONTH) > current.get(Calendar.MONTH) || (birthDay.get(Calendar.MONTH) == current.get(Calendar.MONTH) && birthDay.get(Calendar.DATE) > current.get(Calendar.DATE)))
            age--;
        return age;
    }

    public void openDatePicker() {
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
                            view.updateDate(mYear, mMonth, mDay);

                        if (monthOfYear < mMonth && year == mYear)
                            view.updateDate(mYear, mMonth, mDay);

                        if (dayOfMonth < mDay && year == mYear && monthOfYear == mMonth)
                            view.updateDate(mYear, mMonth, mDay);

                        textViewBirthDate.setText(String.valueOf(year + "-" + ((monthOfYear + 1) > 9 ? '0' + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + dayOfMonth));

                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        if (!textViewBirthDate.getText().toString().trim().equals("") || !textViewBirthDate.getText().toString().isEmpty()) {
            dpd.updateDate(Integer.parseInt(textViewBirthDate.getText().toString().split("-")[0]), Integer.parseInt(textViewBirthDate.getText().toString().split("-")[1]), Integer.parseInt(textViewBirthDate.getText().toString().split("-")[2]));
        }
        dpd.show();
    }

    public void showEducationAdd() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Education"); //Set Alert dialog title here
        alert.setMessage("Here You Can Add new Education"); //Message here

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String srt = input.getEditableText().toString();
                addCell(srt, true);
//                textViewUserEducation.setText(textViewUserEducation.getText().toString() + "\n" + srt);
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

    public void showHobbiesAdd() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Hobby"); //Set Alert dialog title here
        alert.setMessage("Here You Can Add new Hobby"); //Message here

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String srt = input.getEditableText().toString();
                addCell(srt, false);
//                textViewUserEducation.setText(textViewUserEducation.getText().toString() + "\n" + srt);
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

    public void removeEducation(View v) {
        linearLayoutEducationHolder.removeView(v);
        arrayOfEducationView.remove(arrayOfEducationView.indexOf(v));
    }

    public void removeHobby(View v) {
        linearLayoutHobbiesHolder.removeView(v);
        arrayOfHobbyView.remove(arrayOfHobbyView.indexOf(v));
    }

    public String getHobbies() {
        String strHobby = "";
        for (View v : arrayOfHobbyView) {
            TextView text = v.findViewById(R.id.text_main_content);
            strHobby += "," + text.getText().toString();
        }
        return strHobby.length() > 1 ? strHobby.substring(1, strHobby.length()) : "";
    }

    public String getEducations() {
        String strEducation = "";
        for (View v : arrayOfHobbyView) {
            TextView text = v.findViewById(R.id.text_main_content);
            strEducation += "," + text.getText().toString();
        }
        return strEducation.length() > 1 ? strEducation.substring(1, strEducation.length()) : "";
    }

    public void addCell(String content, boolean isEducation) {
        final View view = getLayoutInflater().inflate(R.layout.element_row_cell, null);
        TextView textViewTemp = view.findViewById(R.id.text_main_content);
        ImageButton imageButtonRemove = view.findViewById(R.id.imagebutton_remove_content);
        textViewTemp.setText(content);
        if (isEducation) {
            imageButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeEducation(view);
                }
            });
            linearLayoutEducationHolder.addView(view);
            arrayOfEducationView.add(view);
        } else {
            imageButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeHobby(view);
                }
            });
            linearLayoutHobbiesHolder.addView(view);
            arrayOfHobbyView.add(view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        mAdapter.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }
}
