package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Response;

public class VerificationActivity extends AppCompatActivity {

    TextView textViewNote,textViewCountDownPhone,textViewCountDownEmail,verifyPhoneCode1,
            verifyEmailCode1,verifyPhoneCode2,verifyEmailCode2,verifyPhoneCode3,verifyEmailCode3,
            verifyPhoneCode4,verifyEmailCode4, verifyPhoneCode5,verifyEmailCode5,verifyPhoneCode6,verifyEmailCode6,
            verifyEmailText,verifyPhoneText;
    LinearLayout linearLayoutPhone,linearLayoutMail;
    AppCompatButton appCompatButtonResendCode,appCompatButtonVerifyContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        this.getActionBar().hide();
        getSupportActionBar().hide();
        textViewNote = findViewById(R.id.text_note);

        linearLayoutPhone = findViewById(R.id.linearlayout_phone);
        linearLayoutMail = findViewById(R.id.linearlayout_email);
        textViewCountDownPhone = findViewById(R.id.tv_countdown_phone);
        textViewCountDownEmail = findViewById(R.id.tv_countdown_email);

        appCompatButtonResendCode = findViewById(R.id.resend_code);
        appCompatButtonVerifyContinue = findViewById(R.id.verify_continue);

        verifyEmailText = findViewById(R.id.verify_email_text);
        verifyPhoneText = findViewById(R.id.verify_phone_text);

        verifyPhoneCode1 = findViewById(R.id.verification_phone_code_1);
        verifyPhoneCode2 = findViewById(R.id.verification_phone_code_2);
        verifyPhoneCode3 = findViewById(R.id.verification_phone_code_3);
        verifyPhoneCode4 = findViewById(R.id.verification_phone_code_4);
        verifyPhoneCode5 = findViewById(R.id.verification_phone_code_5);
        verifyPhoneCode6 = findViewById(R.id.verification_phone_code_6);

        verifyEmailCode1 = findViewById(R.id.verification_email_code_1);
        verifyEmailCode2 = findViewById(R.id.verification_email_code_2);
        verifyEmailCode3 = findViewById(R.id.verification_email_code_3);
        verifyEmailCode4 = findViewById(R.id.verification_email_code_4);
        verifyEmailCode5 = findViewById(R.id.verification_email_code_5);
        verifyEmailCode6 = findViewById(R.id.verification_email_code_6);

        initTimer();

        Bundle extras = getIntent().getExtras();
        final String type = extras.getString("type");
        final String data = extras.getString("data");
        if(type.equals("Phone")){
            linearLayoutPhone.setVisibility(View.VISIBLE);
            linearLayoutMail.setVisibility(View.GONE);
            verifyPhoneText.setText(data);
            textViewNote.setText(getResources().getString(R.string.phone_verify));
        }else{
            linearLayoutPhone.setVisibility(View.GONE);
            linearLayoutMail.setVisibility(View.VISIBLE);
            verifyEmailText.setText(data);
            textViewNote.setText(getResources().getString(R.string.email_verify));
        }

        appCompatButtonVerifyContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder verification_code = new StringBuilder();
                if(type.equals("Phone")){
                    verification_code.append(verifyPhoneCode1.getText().toString().trim());
                    verification_code.append(verifyPhoneCode2.getText().toString().trim());
                    verification_code.append(verifyPhoneCode3.getText().toString().trim());
                    verification_code.append(verifyPhoneCode4.getText().toString().trim());
                    verification_code.append(verifyPhoneCode5.getText().toString().trim());
                    verification_code.append(verifyPhoneCode6.getText().toString().trim());
                }else{
                    verification_code.append(verifyEmailCode1.getText().toString().trim());
                    verification_code.append(verifyEmailCode2.getText().toString().trim());
                    verification_code.append(verifyEmailCode3.getText().toString().trim());
                    verification_code.append(verifyEmailCode4.getText().toString().trim());
                    verification_code.append(verifyEmailCode5.getText().toString().trim());
                    verification_code.append(verifyEmailCode6.getText().toString().trim());
                }
                final JSONObject jsonObjectRequest = new JSONObject();
                try {
                    jsonObjectRequest.put("type", type);
                    jsonObjectRequest.put("verification_code",verification_code.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callVerifyApi(jsonObjectRequest);
            }
        });

        final JSONObject jsonObjectRequest = new JSONObject();
        try {
            jsonObjectRequest.put("type", type);
            jsonObjectRequest.put("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callGenerateApi(jsonObjectRequest);

        appCompatButtonResendCode.setEnabled(false);
        appCompatButtonResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCompatButtonResendCode.setEnabled(false);
                initTimer();
                callGenerateApi(jsonObjectRequest);
            }
        });
    }

    public void callGenerateApi(JSONObject jsonObject){
        SharedPreferences globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        String token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.GENERATE_VERIFICATION_CODE).newBuilder();
        if (DetectConnection.checkInternetConnection(this)) {
            new ProjectConstants.getDataFromServer(jsonObject, new GenerateVerificationCode(), this).execute(urlBuilder.build().toString(), token);
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    public void callVerifyApi(JSONObject jsonObject){
        SharedPreferences globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        String token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.VERIFY_CODE).newBuilder();
        if (DetectConnection.checkInternetConnection(this)) {
            new ProjectConstants.getDataFromServer(jsonObject, new VerifyVerificationCode(), this).execute(urlBuilder.build().toString(), token);
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

    public class GenerateVerificationCode implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
                Log.e("Error",response.body().toString());
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
                                    Toast.makeText(VerificationActivity.this,message,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(VerificationActivity.this,getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerificationActivity.this,getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class VerifyVerificationCode implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
                Log.e("Error",response.toString());
            } else {

                String result = response.body().string(); // response is converted to string
//                Log.e("resp : ", result);

                if (result != null) {
                    try {
                        final JSONObject jsonUserProfile = new JSONObject(result);

                        final Boolean auth = jsonUserProfile.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonUserProfile.getString(ProjectConstants.MESSAGE);
                        final int data = jsonUserProfile.getInt(ProjectConstants.DATA);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (auth) {
                                    Toast.makeText(VerificationActivity.this,message,Toast.LENGTH_SHORT).show();
                                    if(data == 1){
                                        startActivity(new Intent(VerificationActivity.this,UserProfileActivity.class));
                                        finish();
                                    }
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(VerificationActivity.this,getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerificationActivity.this,getResources().getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void initTimer(){
        new CountDownTimer(120000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                StringBuilder time = new StringBuilder();
                time.append((millisUntilFinished / 60000)).append(":").append(millisUntilFinished % 60000 / 1000);
                textViewCountDownPhone.setText(time.toString());
                textViewCountDownEmail.setText(time.toString());
            }

            @Override
            public void onFinish() {
                appCompatButtonResendCode.setEnabled(true);
            }
        }.start();
    }
}
