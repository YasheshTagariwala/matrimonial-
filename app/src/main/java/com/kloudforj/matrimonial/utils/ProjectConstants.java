package com.kloudforj.matrimonial.utils;

import android.content.Context;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    public static final String USER_EDUCATION = "user_education";
    public static final String USER_HOBBY = "user_hobby";
    public static final String USER_FAMILY = "user_family";
    public static final String USER_EXTRA = "user_extra";
    public static final String USERID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String PHONE = "phone_number";
    public static final String BASE_IMAGE = "user_image";
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

    /*Logout constants*/
    public static final String LOGOUT_URL = "/logout";

    /* Common Api Call Function */
    public static class getDataFromServer {
        JSONObject values;
        CallBackFunction callBackFunction;
        WeakReference<Context> context;

        public getDataFromServer(JSONObject values,CallBackFunction callBackFunction,Context context){
            this.values = values;
            this.callBackFunction = callBackFunction;
            this.context = new WeakReference<>(context);
        }

        public void execute(String url,String token){
            Call requestCall;
            OkHttpClient client = new OkHttpClient();
            Request request;
            if(this.values.length() > 0){
                request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), this.values.toString()))
                        .header(ProjectConstants.APITOKEN, token)
                        .build();
            }else{
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
                    callBackFunction.getResponseFromServer(response);
                }
            });
        }

        public void execute(String url){
            Call requestCall;
            OkHttpClient client = new OkHttpClient();
            Request request;
            if(this.values.length() > 0){
                request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), this.values.toString()))
                        .build();
            }else{
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
}