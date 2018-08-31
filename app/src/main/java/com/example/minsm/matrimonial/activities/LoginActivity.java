package com.example.minsm.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minsm.matrimonial.R;
import com.example.minsm.matrimonial.utils.DetectConnection;
import com.example.minsm.matrimonial.utils.ProjectConstants;
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

    String mLogedinUsername, mLogedinPassword;
    FloatingActionButton buttonLogin;
    TextView signUptextView;
    private SharedPreferences globalSP;

    private Call loginRequestCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        loginEmail = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);

        buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(this);

        signUptextView = findViewById(R.id.sign_up);
        signUptextView.setOnClickListener(this);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_login:
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
                    loginServiceCall();
                }

                break;

            case R.id.sign_up:
                Intent intentSignUp = new Intent(this, SignupActivity.class);
                startActivity(intentSignUp);
                break;
        }
    }

    /**
     * used for login service call
     */
    private void loginServiceCall() {

        if(DetectConnection.checkInternetConnection(LoginActivity.this)) {

            JSONObject jsonLoginResquest = new JSONObject();
            try {
                jsonLoginResquest.put(ProjectConstants.EMAIL, loginEmail.getText().toString().trim());
                jsonLoginResquest.put(ProjectConstants.PASSWORD, loginPassword.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient clientLogin = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.LOGIN_URL).newBuilder();

            String url = urlBuilder.build().toString(); // URL is converted to String

            Request requestLogin = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), jsonLoginResquest.toString()))
                    .build();

            loginRequestCall = clientLogin.newCall(requestLogin);
            loginRequestCall.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    //Log.e("onFailure", "in ", e);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()) {
                    } else {
                        try {
                            String result = response.body().string(); // response is converted to string
                            final JSONObject jsonLogin = new JSONObject(result);

                            //final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                            //final String message = jsonLogin.getString(ProjectConstants.MESSAGE);

                            final JSONObject successObj = jsonLogin.getJSONObject(ProjectConstants.SUCCESS);
                            String token = successObj.getString(ProjectConstants.TOKEN);

                            SharedPreferences.Editor editor = globalSP.edit();
                            editor.putString(ProjectConstants.TOKEN, token);
                            editor.apply();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                            /*if(auth) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }*/

                        } catch (JSONException e) {

                        }
                    }
                }
            });

        } else {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }
}