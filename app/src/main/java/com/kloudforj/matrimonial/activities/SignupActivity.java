package com.kloudforj.matrimonial.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    private EditText /*etFirstName, etMiddleName, etLastName,*/ etEmail, etPassword, etConfirmPassword, etPhone;
    private TextInputLayout /*firstNameWrapper, middleNameWrapper, lastNameWrapper,*/ emailWrapper, phoneWrapper, passwordWrapper, confirmPasswordWrapper;
    private ProgressBar mSignUpActvityProgressBar;
    private TextView logintextView;
    private Button registerButton;
    private SharedPreferences globalSP;
    Boolean signUpCheck = false; // A flag is initialized for checking Edittext value is empty or not

    private String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);

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
        phoneWrapper = findViewById(R.id.phoneWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        confirmPasswordWrapper = findViewById(R.id.confirmPasswordWrapper);
        /*etFirstName = findViewById(R.id.firstName);
        etMiddleName = findViewById(R.id.middleName);
        etLastName = findViewById(R.id.lastName);*/
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
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

        logintextView = findViewById(R.id.login);
        logintextView.setOnClickListener(this);
    }

    private void showPrivacyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_signup_policies);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_content)).setMovementMethod(LinkMovementMethod.getInstance());

        TextView tv_privacy = dialog.findViewById(R.id.tv_privacy);
        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPolicy = new Intent(SignupActivity.this, PoliciesActivity.class);
                intentPolicy.putExtra("policy", "privacy");
                startActivity(intentPolicy);
            }
        });

        TextView tv_terms = dialog.findViewById(R.id.tv_terms);
        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPolicy = new Intent(SignupActivity.this, PoliciesActivity.class);
                    intentPolicy.putExtra("policy", "terms");
                startActivity(intentPolicy);
            }
        });

        ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Button Accept Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                makeRegisterCall();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Please accept policies to continue.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.registerButton:

                if ( (etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING) || etPhone.getText().toString().equals(ProjectConstants.EMPTY_STRING ))
                        && etPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setError(getResources().getString(R.string.enter_valid_email));
                    phoneWrapper.setError(getResources().getString(R.string.enter_valid_phone));
                    passwordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                }

                // validation for edittext is empty
                /*if (etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setError(getResources().getString(R.string.enter_valid_email));
                    signUpCheck = false;
                } else*/
                if (etPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    passwordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                } else if (etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signUpCheck = false;
                }

                // validation for edittext is non-empty
                /*if (!etEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setErrorEnabled(false);
                } else*/
                if (!etPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    passwordWrapper.setErrorEnabled(false);
                } else if (!etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    confirmPasswordWrapper.setErrorEnabled(false);
                }

                if (signUpCheck) {
                    showPrivacyDialog();
                }
                break;

            case R.id.login:
                finish();
                break;
        }
    }

    private void makeRegisterCall() {

        JSONObject jsonSignUpRequest = new JSONObject();
        try {
            if(!etEmail.getText().toString().trim().equals("")) {
                jsonSignUpRequest.put(ProjectConstants.EMAIL, etEmail.getText().toString().trim());
            }

            if(!etPhone.getText().toString().trim().equals("")) {
                jsonSignUpRequest.put(ProjectConstants.PHONE, etPhone.getText().toString().trim());
            }

            jsonSignUpRequest.put(ProjectConstants.PASSWORD, etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.SIGNUP_URL).newBuilder();
        if(DetectConnection.checkInternetConnection(this)) {
            new ProjectConstants.getDataFromServer(jsonSignUpRequest, new RegisterServiceCall(),this).execute(urlBuilder.build().toString());
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * used for signup service call
     */
    public class RegisterServiceCall implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if(!response.isSuccessful()) {
                Log.e("data",response.toString());
                enableLoginComponents(getResources().getString(R.string.something_went_wrong));
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                Log.e("Result : ",result);

                if(result != null) {

                    try {

                        final JSONObject jsonSignup = new JSONObject(result);
                        final Boolean auth = jsonSignup.getBoolean(ProjectConstants.AUTH);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                registerButton.setEnabled(true); // Login Button is Enabled
                                mSignUpActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                if(auth) {

                                    String message = null;
                                    try {
                                        message = jsonSignup.getString(ProjectConstants.MESSAGE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    finish();

                                } else {

                                    etPassword.setText(ProjectConstants.EMPTY_STRING);
                                    etConfirmPassword.setText(ProjectConstants.EMPTY_STRING);
                                    passwordWrapper.setErrorEnabled(false);
                                    confirmPasswordWrapper.setErrorEnabled(false);
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
