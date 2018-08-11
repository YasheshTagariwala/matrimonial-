package com.example.minsm.matrimonial.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.minsm.matrimonial.R;
import com.example.minsm.matrimonial.utils.DetectConnection;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton buttonLogin;

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

    private void loginServiceCall() {

        if(DetectConnection.checkInternetConnection(LoginActivity.this)) {

            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        } else {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }
}
