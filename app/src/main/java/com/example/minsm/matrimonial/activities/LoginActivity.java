package com.example.minsm.matrimonial.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    String mLogedinUsername, mLogedinPassword;
    FloatingActionButton buttonLogin;

    private Call loginRequestCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_login:
                loginServiceCall();
        }
    }

    /**
     * used for login service call
     */
    private void loginServiceCall() {

        if(DetectConnection.checkInternetConnection(LoginActivity.this)) {

            JSONObject jsonLoginResquest = new JSONObject();
            try {
                jsonLoginResquest.put(ProjectConstants.EMAIL, mLogedinUsername);
                jsonLoginResquest.put(ProjectConstants.PASSWORD, mLogedinPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient clientLogin = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.LOGIN_URL).newBuilder();

            Request requestLogin = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), jsonLoginResquest.toString()))
                    .build();

            loginRequestCall = clientLogin.newCall(requestLogin);
            loginRequestCall.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()) {

                    } else {
                        try {
                            final JSONObject jsonLogin = new JSONObject(response.body().toString());
                            final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                            final String message = jsonLogin.getString(ProjectConstants.MESSAGE);

                            if(auth) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

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
