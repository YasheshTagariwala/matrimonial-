package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText loginEmail, loginPassword;
    private TextInputLayout emailWrapper, passwordWrapper;
    private ProgressBar mLoginActvityProgressBar;
    private Button loginButton;
    private TextView signUptextView;
    private Call loginRequestCall;

    private String token;
    private SharedPreferences globalSP;

    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginActvityProgressBar = findViewById(R.id.pb_login_activity);
        if (mLoginActvityProgressBar != null) {
            mLoginActvityProgressBar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(LoginActivity.this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        emailWrapper = findViewById(R.id.usernameWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        loginEmail = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        signUptextView = findViewById(R.id.sign_up);
        signUptextView.setOnClickListener(this);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);

        if(!token.trim().equals(ProjectConstants.EMPTY_STRING)) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    /**
     * used for login service call
     */
    public class LoginServiceCall implements CallBackFunction{

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if(!response.isSuccessful()) {
                        /*Log.e("1 : ", response.toString());
                        Log.e("2 Code : ", ""+response.code());
                        Log.e("3 Message : ", response.message());*/
                enableLoginComponents(getResources().getString(R.string.something_went_wrong));
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                //Log.e("Response : ", result);

                if(result != null) {

                    try {

                        final JSONObject jsonLogin = new JSONObject(result);

                        final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonLogin.getString(ProjectConstants.MESSAGE);
                        final Boolean profile = jsonLogin.getBoolean(ProjectConstants.USER_PROFILE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginButton.setEnabled(true); // Login Button is Enabled
                                mLoginActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                if(auth) {
                                    try {
                                        token = jsonLogin.getString(ProjectConstants.TOKEN);
                                        final int userid = jsonLogin.getInt(ProjectConstants.USERID);

                                        SharedPreferences.Editor editor = globalSP.edit();
                                        editor.putString(ProjectConstants.TOKEN, token);
                                        editor.putInt(ProjectConstants.USERID, userid);

                                        editor.putString(ProjectConstants.LOCATION, "");
                                        editor.putString(ProjectConstants.SUBCASTE1, "");
                                        editor.putString(ProjectConstants.SUBCASTE2, "");
                                        editor.putString(ProjectConstants.NAME, "");
                                        editor.putString(ProjectConstants.EMAIL, loginEmail.getText().toString());

                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        editor.putBoolean(ProjectConstants.USER_PROFILE, profile);
                                        editor.apply();
                                        if(profile){
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        }else{
                                            startActivity(new Intent(LoginActivity.this, UserEditProfileActivity.class));
                                        }
                                        finish();
                                    }
                                    catch (JSONException e) {
                                        enableLoginComponents(getResources().getString(R.string.something_went_wrong));
                                        e.printStackTrace();
                                    }
                                } else {
                                    SharedPreferences.Editor editor = globalSP.edit();
                                    editor.clear();
                                    editor.apply();

                                    loginEmail.setText(ProjectConstants.EMPTY_STRING);
                                    loginPassword.setText(ProjectConstants.EMPTY_STRING);
                                    passwordWrapper.setErrorEnabled(false);
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loginButton:
                Boolean signInCheck = true; // A flag is initialized for checking Edittext value is empty or not

                if (loginEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING) && loginPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setError(getResources().getString(R.string.enter_valid_email));
                    passwordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signInCheck = false;
                }

                // validation for edittext is empty
                if (loginEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setError(getResources().getString(R.string.enter_valid_email));
                    signInCheck = false;
                } else if (loginPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    passwordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    signInCheck = false;
                }
                // validation for edittext is non-empty
                if (!loginEmail.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    emailWrapper.setErrorEnabled(false);
                } else if (!loginPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    passwordWrapper.setErrorEnabled(false);
                }

                if (signInCheck) {
                    JSONObject jsonLoginRequest = new JSONObject();
                    try {
                        jsonLoginRequest.put(ProjectConstants.EMAIL, loginEmail.getText().toString().trim());
                        jsonLoginRequest.put(ProjectConstants.PASSWORD, loginPassword.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.LOGIN_URL).newBuilder();
                    if(DetectConnection.checkInternetConnection(LoginActivity.this)) {
                        new ProjectConstants.getDataFromServer(jsonLoginRequest,new LoginServiceCall(),this).execute(urlBuilder.build().toString());
                    }else{
                        Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case R.id.sign_up:
                Intent intentSignUp = new Intent(this, SignupActivity.class);
                startActivity(intentSignUp);
                break;
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
                loginButton.setEnabled(true); // Login Button is Enabled
                mLoginActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}