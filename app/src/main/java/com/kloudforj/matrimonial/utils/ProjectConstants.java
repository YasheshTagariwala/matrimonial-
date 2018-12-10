package com.kloudforj.matrimonial.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.activities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProjectConstants {

    /*System wide constants*/
    public static final String APPLICATION_CHARSET = "application/json; charset=utf-8";
    public static final String AUTH = "auth";
    public static final String MESSAGE = "msg";
    public static final String TOKEN = "token";
    public static final String APITOKEN = "apitoken";
    public static final String EMPTY_STRING = "";
    public static final String SLASH = "/";
    public static final String TOTAL_USERS = "total_users_found";

    public static final String DATA = "data";
    public static final String USER_PROFILE = "profile";
    public static final String ADMIN_VERIFIED = "admin_verified";
    public static final String PHONE_VERIFIED = "phone_verified";
    public static final String EMAIL_VERIFIED = "email_verified";
    public static final String USERID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String PHONE = "phone_number";
    public static final String BASE_IMAGE = "user_image";
    public static final String OLD_PASSWORD = "old_password";
    public static final String NEW_PASSWORD = "new_password";
    public static final String ID = "id";


    public static final String BOOKMARKID = "bookmark_id";
    public static final String IMAGE_ID = "image_id";

    //  public static final String BASE_URL = "http://metrimonial.it/api";
//    public static final String BASE_URL = "http://139.59.90.129/matrimonial/public/index.php/api";
    public static final String BASE_URL = "http://139.59.68.146/matrimonial/public/index.php/api";
    public static final String VERSION_0 = "/v0";
    public static final String VERSION_1 = "/v1";

    public static final String USER = "/user";

    /*Shared SharedPreferences used in application*/
    public static final String PROJECTBASEPREFERENCE = "projectbasepreference";

    /*Signup Activity constants*/
    public static final String FNAME = "fname";
    public static final String MNAME = "mname";
    public static final String LNAME = "lname";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    public static final String SIGNUP_URL = "/register";
    public static final String CHANGE_PASSWORD = "/change-password";


    /*Login Activity constants*/
    public static final String LOGIN_URL = "/login";


    /*User profile constants*/
    public static final String SEX = "sex";
    public static final String CAST = "cast";
    public static final String SUBCASTE1 = "subCaste1";
    public static final String SUBCASTE2 = "subCaste2";
    public static final String AGE = "age";                 // 26-30
    public static final String BIRTH_YEAR = "year";
    public static final String LOCATION = "location";       // india/gujarat/surat
    //    public static final String NAME = "/user-list";
    public static final String NAME = "name";

    public static final String USER_PROFILE_URL = "/profile";
    public static final String UPDATE_PROFILE_URL = "/update-profile";
    public static final String IMAGE_UPLOAD_URL = "/upload-profile-image";
    public static final String DELETE_IMAGE_URL = "/delete-profile-image";
    public static final String IMAGE_GET_URL = "/user-image";
    public static final String USERLIST_URL = "/home";
    public static final String ADD_TO_FAVORITES_URL = "/add-to-favorites";
    public static final String REMOVE_FAVORITES_URL = "/delete-favorites";
    public static final String GENERATE_VERIFICATION_CODE = "/generate-verification-code";
    public static final String VERIFY_CODE = "/verify-code";
    public static final String GET_CASTE_SUBCASTE = "/get-caste-subcaste-list";

    /*Logout constants*/
    public static final String LOGOUT_URL = "/logout";

    /* Common Api Call Function */
    public static class getDataFromServer {
        JSONObject values;
        CallBackFunction callBackFunction;
        WeakReference<Context> context;
        Context context1;

        public getDataFromServer(JSONObject values, CallBackFunction callBackFunction, Context context) {
            this.values = values;
            this.callBackFunction = callBackFunction;
            this.context = new WeakReference<>(context);
            this.context1 = context;
        }

        public void execute(String url, String token) {

            LayoutInflater inflater = LayoutInflater.from(context1);
            View alertLoadind = inflater.inflate(R.layout.layout_loading, null);
            ProgressBar mLoadingProgressbar = alertLoadind.findViewById(R.id.pb_loading);
            if (mLoadingProgressbar != null) {
                mLoadingProgressbar.getIndeterminateDrawable().setColorFilter(
                        ContextCompat.getColor(context1, R.color.colorAccent),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            }

            AlertDialog.Builder builderLoading = new AlertDialog.Builder(context1);
            builderLoading.setTitle("Loading");
            builderLoading.setView(alertLoadind);
            builderLoading.setCancelable(false);

            final AlertDialog alertDialogLoading = builderLoading.create();
            alertDialogLoading.show();

            Call requestCall;
            OkHttpClient client = new OkHttpClient();
            Request request;
            if (this.values.length() > 0) {
                request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), this.values.toString()))
                        .header(ProjectConstants.APITOKEN, token)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .header(ProjectConstants.APITOKEN, token)
                        .build();
            }

            requestCall = client.newCall(request);
            requestCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    alertDialogLoading.dismiss();
                    callBackFunction.getResponseFromServer(response);
                }
            });
        }

        public void execute(String url) {
            Call requestCall;
            OkHttpClient client = new OkHttpClient();
            Request request;
            if (this.values.length() > 0) {
                request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), this.values.toString()))
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .build();
            }

            requestCall = client.newCall(request);
            requestCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callBackFunction.getResponseFromServer(response);
                }
            });
        }
    }

    public static void logoutServiceCall(final Context context) {
        Call logoutRequestCall;
        final SharedPreferences globalSP;
        globalSP = context.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        int user_id = globalSP.getInt(ProjectConstants.USERID, 0);
        int apitoken = globalSP.getInt(ProjectConstants.APITOKEN, 0);
        if (DetectConnection.checkInternetConnection(context)) {

            JSONObject jsonLogoutResquest = new JSONObject();
            try {
                jsonLogoutResquest.put(ProjectConstants.ID, user_id);
//                jsonLogoutResquest.put(ProjectConstants.APITOKEN, apitoken);
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
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e("1 : ", response.toString());
                        enableComponents(context);
                        throw new IOException("Unexpected code " + response);
                    } else {

                        String result = response.body().string(); // response is converted to string

                        if (result != null) {

                            try {

                                final JSONObject jsonLogin = new JSONObject(result);

                                final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                                final String message = jsonLogin.getString(ProjectConstants.MESSAGE);

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //mLoginActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                        if (auth) {
                                            SharedPreferences.Editor editor = globalSP.edit();
                                            editor.clear();
                                            editor.apply();

                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            context.startActivity(intent);
                                            ((Activity) context).finish();
                                        } else {
                                            enableComponents(context);
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                enableComponents(context);
                            }

                        } else {
                            enableComponents(context);
                        }

                    }
                }
            });
        }
    }

    public static void enableComponents(final Context context) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }
}