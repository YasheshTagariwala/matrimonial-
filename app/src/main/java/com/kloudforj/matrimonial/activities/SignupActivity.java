package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText /*etFirstName, etMiddleName, etLastName,*/ etEmail, etPassword, etConfirmPassword;
    private TextInputLayout /*firstNameWrapper, middleNameWrapper, lastNameWrapper,*/ emailWrapper, passwordWrapper, confirmPasswordWrapper;
    private ProgressBar mSignUpActvityProgressBar;
    private Button registerButton;
    private SharedPreferences globalSP;
    Boolean signUpCheck = false; // A flag is initialized for checking Edittext value is empty or not

    private Call signUpRequestCall;
    private String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignUpActvityProgressBar = findViewById(R.id.pb_signup_activity);
        if (mSignUpActvityProgressBar != null) {
            mSignUpActvityProgressBar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(SignupActivity.this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }


        /*firstNameWrapper = findViewById(R.id.firstNameWrapper);
        middleNameWrapper = findViewById(R.id.middleNameWrapper);
        lastNameWrapper = findViewById(R.id.lastNameWrapper);*/
        emailWrapper = findViewById(R.id.emailWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        confirmPasswordWrapper = findViewById(R.id.confirmPasswordWrapper);
        /*etFirstName = findViewById(R.id.firstName);
        etMiddleName = findViewById(R.id.middleName);
        etLastName = findViewById(R.id.lastName);*/
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirmPassword);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if(! password.equals(confirmPassword)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_same_password));
                    signUpCheck = false;
                } else {
                    confirmPasswordWrapper.setErrorEnabled(false);
                    signUpCheck = true;
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

                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if(! password.equals(confirmPassword)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_same_password));
                    signUpCheck = false;
                } else {
                    confirmPasswordWrapper.setErrorEnabled(false);
                    signUpCheck = true;
                }
            }
        });

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.registerButton:

                if (etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)
                        && etPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setError(getResources().getString(R.string.enter_valid_email));
                    passwordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                }

                // validation for edittext is empty
                if (etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
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
                if (!etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setErrorEnabled(false);
                } else if (!etPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    passwordWrapper.setErrorEnabled(false);
                } else if (!etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    confirmPasswordWrapper.setErrorEnabled(false);
                }

                if (signUpCheck) {
                    Log.e("Register : ", "Made a call");
                    JSONObject jsonSignUpRequest = new JSONObject();
                    try {
                        jsonSignUpRequest.put(ProjectConstants.EMAIL, etEmail.getText().toString().trim());
                        jsonSignUpRequest.put(ProjectConstants.PASSWORD, etPassword.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.SIGNUP_URL).newBuilder();
                    if(DetectConnection.checkInternetConnection(this)) {
                        new ProjectConstants.getDataFromServer(jsonSignUpRequest,new RegisterServiceCall(),this).execute(urlBuilder.build().toString());
                    }else{
                        Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("Register : ", "Beep beep. Error!!!");
                }
                break;
        }
    }

    /**
     * used for signup service call
     */
    public class RegisterServiceCall implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
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
                                mSignUpActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                if(auth) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                    SharedPreferences.Editor editor = globalSP.edit();
                                    editor.putString(ProjectConstants.TOKEN, token);
                                    editor.apply();

                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                } else {

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
                mSignUpActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled
                Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
