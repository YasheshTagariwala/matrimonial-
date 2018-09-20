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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.adapters.CardListAdapter;
import com.kloudforj.matrimonial.utils.ProjectConstants;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mainToolbar;
    private ProgressBar mMainActvityProgressBar;
    private RecyclerView mUsersListRecyclerView;
    private NavigationView mNavigationView;
    private ImageButton imageButtonSearch,imageButtonEdit;

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
        imageButtonEdit = mView.findViewById(R.id.imagebutton_edit);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserProfile.class);
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

        //Log.e("Token : ", token);
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
}
