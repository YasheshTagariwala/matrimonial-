package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences globalSP;
    private int id;

    private TextView tvLogout;
    private Call logoutRequestCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        id = globalSP.getInt(ProjectConstants.USERID, 0);

        tvLogout = findViewById(R.id.tv_logout);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutServiceCall();
            }
        });
    }

    private void logoutServiceCall() {

        if(DetectConnection.checkInternetConnection(SettingsActivity.this)) {

            JSONObject jsonLogoutResquest = new JSONObject();
            try {
                jsonLogoutResquest.put(ProjectConstants.ID, id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient clientlogout = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.LOGOUT_URL).newBuilder();

            String urlLogout = urlBuilder.build().toString(); // URL is converted to String
            /*Log.e("URL Logout : ", urlLogout);
            Log.e("URL Request : ", jsonLogoutResquest.toString());*/

            Request requestLogout = new Request.Builder()
                    .url(urlLogout)
                    .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), jsonLogoutResquest.toString()))
                    .build();

            logoutRequestCall = clientlogout.newCall(requestLogout);
            logoutRequestCall.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()) {
                        Log.e("1 : ", response.toString());
                        enableLoginComponents(getResources().getString(R.string.something_went_wrong));
                        throw new IOException("Unexpected code " + response);
                    } else {

                        String result = response.body().string(); // response is converted to string

                        if(result != null) {

                            try {

                                final JSONObject jsonLogin = new JSONObject(result);

                                final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                                final String message = jsonLogin.getString(ProjectConstants.MESSAGE);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //mLoginActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                        if(auth) {

                                            SharedPreferences.Editor editor = globalSP.edit();
                                            editor.clear();
                                            editor.apply();

                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            enableLoginComponents(getResources().getString(R.string.something_went_wrong));
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
                //TODO: Settings design
                /*loginButton.setEnabled(true); // Login Button is Enabled
                mLoginActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled*/
                Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
