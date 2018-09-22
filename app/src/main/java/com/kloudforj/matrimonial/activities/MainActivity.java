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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.adapters.CardListAdapter;
import com.kloudforj.matrimonial.entities.UserProfile;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mainToolbar;
    private ProgressBar mMainActvityProgressBar;
    private RecyclerView mUsersListRecyclerView;
    private NavigationView mNavigationView;
    private ImageButton imageButtonSearch;
    private Call userListRequestCall;
    private Button buttonProfileName;

    private String token;
    private SharedPreferences globalSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mMainActvityProgressBar = (ProgressBar) findViewById(R.id.pb_main_activity);
        if (mMainActvityProgressBar != null) {
            mMainActvityProgressBar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(MainActivity.this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);

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
                Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                intent.putExtra("user_id",1);
                startActivity(intent);
            }
        });

        mUsersListRecyclerView = findViewById(R.id.rv_user_list);
        mUsersListRecyclerView.setHasFixedSize(true);
        mUsersListRecyclerView.setNestedScrollingEnabled(false);
        mUsersListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mUsersListRecyclerView.setItemViewCacheSize(100);
        mUsersListRecyclerView.setDrawingCacheEnabled(true);
        mUsersListRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mUsersListRecyclerView.setAdapter(new CardListAdapter(this));

        imageButtonSearch = findViewById(R.id.imagebutton_search);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });

        fetchUserList();

        //Log.e("Token : ", token);
    }

    private void fetchUserList() {

        if(DetectConnection.checkInternetConnection(MainActivity.this)) {

            JSONObject jsonObjectRequest = new JSONObject();
            try {
                jsonObjectRequest.put(ProjectConstants.CAST, "");
                jsonObjectRequest.put(ProjectConstants.SUBCASTE1, "");
                jsonObjectRequest.put(ProjectConstants.SUBCASTE2, "");
                jsonObjectRequest.put(ProjectConstants.AGE, "");
                jsonObjectRequest.put(ProjectConstants.LOCATION, "");
                jsonObjectRequest.put(ProjectConstants.NAME, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient clientUserList = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.USERLIST_URL).newBuilder();

            String urlUserList = urlBuilder.build().toString(); // URL is converted to String
            /*Log.e("URL UserList : ", urlUserList);
            Log.e("URL Request : ", jsonLoginResquest.toString());*/

            Request requestUserList = new Request.Builder()
                    .url(urlUserList)
                    .header(ProjectConstants.TOKEN, token)
                    .post(RequestBody.create(MediaType.parse(ProjectConstants.APPLICATION_CHARSET), jsonObjectRequest.toString()))
                    .build();

            userListRequestCall = clientUserList.newCall(requestUserList);
            userListRequestCall.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    //Log.e("onFailure", "in ", e);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()) {
                        enableComponents(getResources().getString(R.string.something_went_wrong));
                        throw new IOException("Unexpected code " + response);
                    } else {

                    }
                }
            });


        } else {
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
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
}
