package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kloudforj.matrimonial.R;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etFirstName, etMiddleName, etLastName, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout firstNameWrapper, middleNameWrapper, lastNameWrapper, emailWrapper, passwordWrapper, confirmPasswordWrapper;
    private ProgressBar signUpProgress;
    private Button registerButton;
    private SharedPreferences globalSP;

    private Call signUpRequestCall;
    private String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpProgress = findViewById(R.id.signUpProgress);

        firstNameWrapper = findViewById(R.id.firstNameWrapper);
        middleNameWrapper = findViewById(R.id.middleNameWrapper);
        lastNameWrapper = findViewById(R.id.lastNameWrapper);
        emailWrapper = findViewById(R.id.emailWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        confirmPasswordWrapper = findViewById(R.id.confirmPasswordWrapper);
        etFirstName = findViewById(R.id.firstName);
        etMiddleName = findViewById(R.id.middleName);
        etLastName = findViewById(R.id.lastName);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirmPassword);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.registerButton:

                Boolean signUpCheck = true; // A flag is initialized for checking Edittext value is empty or not

                if (etFirstName.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etLastName.getText().toString().equals(ProjectConstants.EMPTY_STRING)
                        && etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)
                        && etPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    firstNameWrapper.setError(getResources().getString(R.string.enter_valid_fname));
                    lastNameWrapper.setError(getResources().getString(R.string.enter_valid_lname));
                    emailWrapper.setError(getResources().getString(R.string.enter_valid_email));
                    passwordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                }

                // validation for edittext is empty
                if (etFirstName.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    firstNameWrapper.setError(getResources().getString(R.string.enter_valid_email));
                    signUpCheck = false;
                } else if (etLastName.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    lastNameWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                } else if (etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                } else if (etPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    passwordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                } else if (etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                }

                // validation for edittext is non-empty
                if (!etFirstName.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    firstNameWrapper.setErrorEnabled(false);
                } else if (!etLastName.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    lastNameWrapper.setErrorEnabled(false);
                } else if (!etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setErrorEnabled(false);
                } else if (!etPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    passwordWrapper.setErrorEnabled(false);
                } else if (!etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    confirmPasswordWrapper.setErrorEnabled(false);
                }

                if (signUpCheck) {
                    Log.e("Register : ", "Made a call");
                    //registerServiceCall();
                }
                break;
        }
    }

    /**
     * used for signup service call
     */
    private void registerServiceCall() {

        if(DetectConnection.checkInternetConnection(SignupActivity.this)) {

            JSONObject jsonSignupResquest = new JSONObject();
            try {
                jsonSignupResquest.put(ProjectConstants.FNAME, etFirstName.getText().toString().trim());
                jsonSignupResquest.put(ProjectConstants.MNAME, etMiddleName.getText().toString().trim());
                jsonSignupResquest.put(ProjectConstants.LNAME, etLastName.getText().toString().trim());
                jsonSignupResquest.put(ProjectConstants.EMAIL, etEmail.getText().toString().trim());
                jsonSignupResquest.put(ProjectConstants.PASSWORD, etPassword.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient clientLogin = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.SIGNUP_URL).newBuilder();

            String url = urlBuilder.build().toString(); // URL is converted to String
            //Log.e("URL Login : ", url);

            registerButton.setEnabled(false); // Login Button is Disabled
            signUpProgress.setVisibility(View.VISIBLE); // ProgressBar is Enabled

            Request requestSignup = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), jsonSignupResquest.toString()))
                    .build();

            signUpRequestCall = clientLogin.newCall(requestSignup);
            signUpRequestCall.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("onFailure", "in ", e);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()) {
                        enableLoginComponents(getResources().getString(R.string.something_went_wrong));
                        throw new IOException("Unexpected code " + response);
                    } else {

                        String result = response.body().string(); // response is converted to string

                        if(result != null) {

                            try {

                                final JSONObject jsonLogin = new JSONObject(result);

                                final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                                final String message = jsonLogin.getString(ProjectConstants.MESSAGE);
                                final String token = jsonLogin.getString(ProjectConstants.TOKEN);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        registerButton.setEnabled(true); // Login Button is Enabled
                                        signUpProgress.setVisibility(View.GONE); // ProgressBar is Disabled

                                        SharedPreferences.Editor editor = globalSP.edit();
                                        editor.putString(ProjectConstants.TOKEN, token);
                                        editor.apply();

                                        if(auth) {
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            //etEmail.setText(ProjectConstants.EMPTY_STRING);
                                            etPassword.setText(ProjectConstants.EMPTY_STRING);
                                            etConfirmPassword.setText(ProjectConstants.EMPTY_STRING);
                                            passwordWrapper.setErrorEnabled(false);
                                            confirmPasswordWrapper.setErrorEnabled(false);
                                            Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                enableLoginComponents(getResources().getString(R.string.something_went_wrong));
                            }
                        } else {
                            enableLoginComponents(getResources().getString(R.string.something_went_wrong));
                        }
                    }
                }
            });

        } else {
            Toast.makeText(SignupActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Enables login button and progressbar invisible
     *
     * @param msg
     */
    private void enableLoginComponents(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registerButton.setEnabled(true); // Login Button is Enabled
                signUpProgress.setVisibility(View.GONE); // ProgressBar is Disabled
                Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
