package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword, etForgetOpt;
    private TextInputLayout currentPasswordWrapper, newPasswordWrapper, confirmPasswordWrapper, forgetPasswordWrapper;
    private Button changePasswordButton;
    Boolean changePasswordCheck = false; // A flag is initialized for validations.
    private ImageButton imageButtonCancel;
    private String token;
    private SharedPreferences globalSP;
    private boolean isFromForget = false;
    private String userName = ProjectConstants.EMPTY_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPasswordWrapper = findViewById(R.id.currentPasswordWrapper);
        newPasswordWrapper = findViewById(R.id.newPasswordWrapper);
        confirmPasswordWrapper = findViewById(R.id.confirmPasswordWrapper);
        forgetPasswordWrapper = findViewById(R.id.forgetPasswordWrapper);

        etCurrentPassword = findViewById(R.id.current_password);
        etForgetOpt = findViewById(R.id.forget_opt);
        etNewPassword = findViewById(R.id.new_password);
        etConfirmPassword = findViewById(R.id.confirm_password);

        changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(this);

        imageButtonCancel = findViewById(R.id.imagebutton_cpa_cancel);
        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("is_forget")) {
            isFromForget = true;
            userName = intent.getStringExtra("user_name");
        }
        long ts = ProjectConstants.getTimeStamp(this);
        final long FIVE_MINUTES = 1000 * 60 * 5; //5 minutes in milliseconds
        long current_ts = System.currentTimeMillis();

        if (isFromForget) {
            forgetPasswordWrapper.setVisibility(View.VISIBLE);
            currentPasswordWrapper.setVisibility(View.GONE);
            final JSONObject jsonObjectRequest = new JSONObject();
            try {
                if (userName.contains("@")) {
                    jsonObjectRequest.put("type", "Email");
                } else {
                    jsonObjectRequest.put("type", "Phone");
                }
                jsonObjectRequest.put("email", userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.e("DATA", jsonObjectRequest.toString());

            if(current_ts - ts > FIVE_MINUTES) {
                //Log.e("1 : ", "Generate Code called.");
                ProjectConstants.setTimeStamp(ChangePasswordActivity.this);
                HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.GENERATE_FORGET_VERIFICATION_CODE).newBuilder();
                if (DetectConnection.checkInternetConnection(ChangePasswordActivity.this)) {
                    new ProjectConstants.getDataFromServer(jsonObjectRequest, new GenerateVerificationCode(), this).execute(urlBuilder.build().toString());
                } else {
                    Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            forgetPasswordWrapper.setVisibility(View.GONE);
            currentPasswordWrapper.setVisibility(View.VISIBLE);
        }


        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (!newPassword.equals(confirmPassword)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_same_password));
                    changePasswordCheck = false;
                } else {
                    confirmPasswordWrapper.setErrorEnabled(false);
                    changePasswordCheck = true;
                }
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (!newPassword.equals(confirmPassword)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_same_password));
                    changePasswordCheck = false;
                } else {
                    confirmPasswordWrapper.setErrorEnabled(false);
                    changePasswordCheck = true;
                }
            }
        });
    }

    public class GenerateVerificationCode implements CallBackFunction {

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
                                    Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.change_password_button:

                if (isFromForget) {
                    if (etForgetOpt.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etNewPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                        forgetPasswordWrapper.setError(getResources().getString(R.string.enter_valid_email));
                        newPasswordWrapper.setError(getResources().getString(R.string.enter_valid_phone));
                        confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                        changePasswordCheck = false;
                    }
                } else {
                    if (etCurrentPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etNewPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                        currentPasswordWrapper.setError(getResources().getString(R.string.enter_valid_email));
                        newPasswordWrapper.setError(getResources().getString(R.string.enter_valid_phone));
                        confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                        changePasswordCheck = false;
                    }
                }

                if (isFromForget) {
                    if (etForgetOpt.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                        forgetPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                        changePasswordCheck = false;
                    } else if (etNewPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                        newPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                        changePasswordCheck = false;
                    } else if (etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                        confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                        changePasswordCheck = false;
                    }
                } else {
                    if (etCurrentPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                        currentPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                        changePasswordCheck = false;
                    } else if (etNewPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                        newPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                        changePasswordCheck = false;
                    } else if (etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                        confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                        changePasswordCheck = false;
                    }
                }

                if (changePasswordCheck) {
                    if (isFromForget) {
                        JSONObject jsonObjectRequest = new JSONObject();
                        try {
                            jsonObjectRequest.put(ProjectConstants.EMAIL, userName);
                            jsonObjectRequest.put("verification_code", etForgetOpt.getText().toString());
                            jsonObjectRequest.put(ProjectConstants.PASSWORD, etNewPassword.getText().toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
                        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.FORGET_PASSWORD).newBuilder();
                        if (DetectConnection.checkInternetConnection(this)) {
                            new ProjectConstants.getDataFromServer(jsonObjectRequest, new ChangePasswordResponse(), this).execute(urlBuilder.build().toString());
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        JSONObject jsonObjectRequest = new JSONObject();
                        try {
                            jsonObjectRequest.put(ProjectConstants.OLD_PASSWORD, etCurrentPassword.getText().toString());
                            jsonObjectRequest.put(ProjectConstants.NEW_PASSWORD, etNewPassword.getText().toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
                        token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);
                        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.CHANGE_PASSWORD).newBuilder();
                        if (DetectConnection.checkInternetConnection(this)) {
                            new ProjectConstants.getDataFromServer(jsonObjectRequest, new ChangePasswordResponse(), this).execute(urlBuilder.build().toString(), token);
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    private void enableComponents(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ChangePasswordResponse implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
                Log.e("response", response.toString());
                enableComponents(getResources().getString(R.string.something_went_wrong));
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                //Log.e("resp : ", result);

                if (result != null) {

                    try {

                        final JSONObject jsonHome = new JSONObject(result);

                        final Boolean auth = jsonHome.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonHome.getString(ProjectConstants.MESSAGE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                if (auth) {
                                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
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
    }
}