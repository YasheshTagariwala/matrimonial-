package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.adapters.HomeListAdapter;
import com.kloudforj.matrimonial.entities.UserProfile;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mainToolbar;
    private ProgressBar mMainActvityProgressBar;
    private RecyclerView mUsersListRecyclerView;
    private HomeListAdapter mHomeListAdapter;
    private NavigationView mNavigationView;
    private ImageButton imageButtonSearch;
    private Call userListRequestCall, logoutRequestCall;
    private Button buttonProfileName;

    private String location, subcaste1, subcaste2, name;

    private String token;
    private int user_id;
    private SharedPreferences globalSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mMainActvityProgressBar = (ProgressBar) findViewById(R.id.pb_main_activity);
        if (mMainActvityProgressBar != null) {
            mMainActvityProgressBar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(MainActivity.this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);
        user_id = globalSP.getInt(ProjectConstants.USERID, 0);

        location = globalSP.getString(ProjectConstants.LOCATION, ProjectConstants.EMPTY_STRING);
        subcaste1 = globalSP.getString(ProjectConstants.SUBCASTE1, ProjectConstants.EMPTY_STRING);
        subcaste2 = globalSP.getString(ProjectConstants.SUBCASTE2, ProjectConstants.EMPTY_STRING);
        name = globalSP.getString(ProjectConstants.NAME, ProjectConstants.EMPTY_STRING);

        // ActionBar is set on MainActivity
        setToolbar();

        // Drawer Mapping
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            mNavigationView.getMenu().getItem(0).setChecked(true);
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        View mView = mNavigationView.getHeaderView(0);
        buttonProfileName = mView.findViewById(R.id.button_edit);
        buttonProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserProfileActivity.class));
                /*Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                intent.putExtra("user_id",1);
                startActivity(intent);*/
            }
        });

        mUsersListRecyclerView = findViewById(R.id.rv_user_list);
        mUsersListRecyclerView.setHasFixedSize(true);
        mUsersListRecyclerView.setNestedScrollingEnabled(false);
        mUsersListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mUsersListRecyclerView.setItemViewCacheSize(100);
        mUsersListRecyclerView.setDrawingCacheEnabled(true);
        mUsersListRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //mUsersListRecyclerView.setAdapter(new HomeListAdapter(this));

        imageButtonSearch = findViewById(R.id.imagebutton_search);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });

        JSONObject jsonObjectRequest = new JSONObject();
        try {
            jsonObjectRequest.put(ProjectConstants.SEX, "m");

            if(!location.equals(ProjectConstants.EMPTY_STRING)) {
                jsonObjectRequest.put(ProjectConstants.LOCATION, location);
            }
            if(!name.equals(ProjectConstants.EMPTY_STRING)) {
                jsonObjectRequest.put(ProjectConstants.NAME, name);
            }
            if(!subcaste1.equals(ProjectConstants.EMPTY_STRING)) {
                jsonObjectRequest.put(ProjectConstants.SUBCASTE1, subcaste1);
            }if(!subcaste2.equals(ProjectConstants.EMPTY_STRING)) {
                jsonObjectRequest.put(ProjectConstants.SUBCASTE2, subcaste2);
            }

            //TODO: Add age from range seek bar.
                /*jsonObjectRequest.put(ProjectConstants.AGE, "");*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.USERLIST_URL).newBuilder();
        if(DetectConnection.checkInternetConnection(MainActivity.this)) {
            new ProjectConstants.getDataFromServer(jsonObjectRequest,new FetchUserList(),this).execute(urlBuilder.build().toString(),token);
        }else{
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }

        //Log.e("Token : ", token);
    }

    public class FetchUserList implements CallBackFunction{

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if(!response.isSuccessful()) {
                //Log.e("resp : ", response.toString());
                enableComponents(getResources().getString(R.string.something_went_wrong));
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                //Log.e("resp : ", result);

                if(result != null) {

                    try {

                        final JSONObject jsonHome = new JSONObject(result);

                        final Boolean auth = jsonHome.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonHome.getString(ProjectConstants.MESSAGE);
                        final int total_users = jsonHome.getInt(ProjectConstants.TOTAL_USERS);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMainActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                if(auth) {

                                    try {
                                        if(total_users == 0) {
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), total_users+" "+message, Toast.LENGTH_LONG).show();
                                        }

                                        JSONArray jsonArrayData = jsonHome.getJSONArray(ProjectConstants.DATA);
                                        //Log.e("Users : ", jsonArrayData.toString());

                                        if (jsonArrayData.length() > 0) {

                                            ArrayList<UserProfile> userProfiles = new ArrayList<>();

                                            for(int i = 0; i < jsonArrayData.length(); i++) {
                                                JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                                                //Log.e("Profile : ", jsonObject.toString());
                                                userProfiles.add(new Gson().fromJson(jsonObject.toString(), UserProfile.class));
                                            }

                                            mUsersListRecyclerView.setAdapter(new HomeListAdapter(MainActivity.this, userProfiles));
                                        }
                                    } catch (JSONException e) {
                                        enableComponents(getResources().getString(R.string.something_went_wrong));
                                        e.printStackTrace();
                                    }
                                } else {
                                    logoutServiceCall();
                                }
                            }
                        });

                    } catch (JSONException e) {
                        enableComponents(getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    enableComponents(getResources().getString(R.string.something_went_wrong));
                }

            }
        }
    }

    /**
     * Set Action ToolBar as per Material Design Standards
     */
    private void setToolbar() {
        mainToolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        if (mainToolbar != null) {

            mainToolbar.collapseActionView();
            setSupportActionBar(mainToolbar);
            // Get a support ActionBar corresponding to this toolbar
            android.support.v7.app.ActionBar mainActionBar = getSupportActionBar();
            // Enable the Up button
            if (mainActionBar != null) {
                mainActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_settings) {

            Intent setttingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(setttingsActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Toast message and rogressbar invisible
     *
     * @param msg
     */
    private void enableComponents(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMainActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logoutServiceCall() {

        if(DetectConnection.checkInternetConnection(MainActivity.this)) {

            JSONObject jsonLogoutResquest = new JSONObject();
            try {
                jsonLogoutResquest.put(ProjectConstants.ID, user_id);
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
                        enableComponents(getResources().getString(R.string.something_went_wrong));
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
                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            enableComponents(getResources().getString(R.string.something_went_wrong));
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                enableComponents(getResources().getString(R.string.something_went_wrong));
                            }

                        } else {
                            enableComponents(getResources().getString(R.string.something_went_wrong));
                        }

                    }
                }
            });
        }
    }
}
