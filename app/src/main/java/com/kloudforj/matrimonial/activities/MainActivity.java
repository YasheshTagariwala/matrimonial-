package com.kloudforj.matrimonial.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.adapters.HomeListAdapter;
import com.kloudforj.matrimonial.adapters.ViewPagerAdapter;
import com.kloudforj.matrimonial.entities.UserProfile;
import com.kloudforj.matrimonial.fragments.FavouriteListFragment;
import com.kloudforj.matrimonial.fragments.UserListFragment;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.GlideApp;
import com.kloudforj.matrimonial.utils.ProjectConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mainToolbar;
    private ProgressBar mMainActvityProgressBar;
    private RecyclerView mUsersListRecyclerView;
    private HomeListAdapter mHomeListAdapter;
    private NavigationView mNavigationView;
    private ImageButton imageButtonSearch;
    private Call userListRequestCall, logoutRequestCall;
    private Button buttonProfileName;
    private ImageView imgProfile;
    private UserListFragment userListFragment;
    private FavouriteListFragment favouriteListFragment;

    private String location, subcaste1, subcaste2, name, user_name, image_name;
    private String age;

    private String token;
    private int user_id;
    private SharedPreferences globalSP;

    private TabLayout tabLayout;
    private ViewPager viewPager;

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
        user_name = globalSP.getString(ProjectConstants.USER_NAME, ProjectConstants.EMPTY_STRING);
        image_name = globalSP.getString(ProjectConstants.BASE_IMAGE, ProjectConstants.EMPTY_STRING);
        age = globalSP.getString(ProjectConstants.AGE, ProjectConstants.EMPTY_STRING);

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
        buttonProfileName = mView.findViewById(R.id.button_profile_name);
        buttonProfileName.setText(user_name);
        buttonProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserProfileActivity.class));
            }
        });
        imgProfile = mView.findViewById(R.id.imageview_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserProfileActivity.class));
            }
        });

        RequestOptions ro = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter();

        GlideUrl url = new GlideUrl(ProjectConstants.BASE_URL + ProjectConstants.IMAGE_GET_URL + "/" + image_name,
                new LazyHeaders.Builder().addHeader(ProjectConstants.APITOKEN, token).build());
        GlideApp.with(MainActivity.this).load(url).apply(ro)
                /*.crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)*/
                .into(imgProfile);

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
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                //startActivity(new Intent(MainActivity.this,SearchActivity.class));
                //finish();
            }
        });

        JSONObject jsonObjectRequest = new JSONObject();
        try {
            jsonObjectRequest.put(ProjectConstants.SEX, "F");

//            if(!location.equals(ProjectConstants.EMPTY_STRING)) {
//                jsonObjectRequest.put(ProjectConstants.LOCATION, location);
//            }
            if(!name.equals(ProjectConstants.EMPTY_STRING)) {
                jsonObjectRequest.put(ProjectConstants.NAME, name);
            }
            if(!subcaste1.equals(ProjectConstants.EMPTY_STRING)) {
                jsonObjectRequest.put(ProjectConstants.SUBCASTE1, subcaste1);
            }
            if(!subcaste2.equals(ProjectConstants.EMPTY_STRING)) {
                jsonObjectRequest.put(ProjectConstants.SUBCASTE2, subcaste2);
            }
            if(!age.equals(ProjectConstants.EMPTY_STRING)) {
                jsonObjectRequest.put(ProjectConstants.AGE, age);
            }

            //TODO: Add age from range seek bar.
                /*jsonObjectRequest.put(ProjectConstants.AGE, "");*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!globalSP.getBoolean(ProjectConstants.USER_PROFILE,false)) {
            startActivity(new Intent(MainActivity.this, UserEditProfileActivity.class));
            finish();
        } else {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.USERLIST_URL).newBuilder();
            if ( DetectConnection.checkInternetConnection(MainActivity.this) ) {
                new ProjectConstants.getDataFromServer(jsonObjectRequest,new FetchUserList(),this).execute(urlBuilder.build().toString(),token);
            } else {
                Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        Snackbar snackbar = Snackbar
                .make(drawer, "TEST", Snackbar.LENGTH_INDEFINITE)
                .setAction("Contact Us", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        snackbar.show();

        //Log.e("Token : ", token);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        userListFragment = new UserListFragment();
        favouriteListFragment = new FavouriteListFragment();
        adapter.addFragment(userListFragment, "");
        adapter.addFragment(favouriteListFragment, "");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_user_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite_white_24dp);
    }

    public class FetchUserList implements CallBackFunction{

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if(!response.isSuccessful()) {
//                Log.e("resp : ", response.toString());
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
                                            ArrayList<UserProfile> userProfilesWithFavorites = new ArrayList<>();
                                            Map<Integer,Boolean> is_favorite = new HashMap<>();
                                            for(int i = 0; i < jsonArrayData.length(); i++) {
                                                JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                                                //Log.e("Profile : ", jsonObject.toString());
                                                userProfiles.add(new Gson().fromJson(jsonObject.toString(), UserProfile.class));
                                                if(jsonObject.getBoolean("is_favorite")){
                                                    is_favorite.put(jsonObject.getJSONObject("profile").getInt("user_id"),true);
                                                    userProfilesWithFavorites.add(new Gson().fromJson(jsonObject.toString(), UserProfile.class));
                                                }
                                            }
                                            userListFragment.mUsersListRecyclerView.setAdapter(new HomeListAdapter(MainActivity.this, userProfiles,false,is_favorite));
                                            favouriteListFragment.mUsersListRecyclerView.setAdapter(new HomeListAdapter(MainActivity.this, userProfilesWithFavorites,true,is_favorite));
//                                            mUsersListRecyclerView.setAdapter();
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

    public void updateData(boolean is_removed){
        if(is_removed){

        }else{

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
     * Toast message and progressbar invisible
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
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
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

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
