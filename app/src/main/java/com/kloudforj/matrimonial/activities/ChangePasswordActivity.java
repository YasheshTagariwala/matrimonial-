package com.kloudforj.matrimonial.activities;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.utils.ProjectConstants;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout currentPasswordWrapper, newPasswordWrapper, confirmPasswordWrapper;
    private Button changePasswordButton;
    Boolean changePasswordCheck = false; // A flag is initialized for validations.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPasswordWrapper = findViewById(R.id.currentPasswordWrapper);
        newPasswordWrapper = findViewById(R.id.newPasswordWrapper);
        confirmPasswordWrapper = findViewById(R.id.confirmPasswordWrapper);

        etCurrentPassword = findViewById(R.id.current_password);
        etNewPassword = findViewById(R.id.new_password);
        etConfirmPassword = findViewById(R.id.confirm_password);

        changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(this);

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if(! newPassword.equals(confirmPassword)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_same_password));
                    changePasswordCheck = false;
                } else {
                    confirmPasswordWrapper.setErrorEnabled(false);
                    changePasswordCheck = true;
                }
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if(! newPassword.equals(confirmPassword)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_same_password));
                    changePasswordCheck = false;
                } else {
                    confirmPasswordWrapper.setErrorEnabled(false);
                    changePasswordCheck = true;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.change_password_button:

                if ( etCurrentPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etNewPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING) && etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    currentPasswordWrapper.setError(getResources().getString(R.string.enter_valid_email));
                    newPasswordWrapper.setError(getResources().getString(R.string.enter_valid_phone));
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    changePasswordCheck = false;
                }

                if (etCurrentPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    currentPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    changePasswordCheck = false;
                } else if (etNewPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    newPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    changePasswordCheck = false;
                } else if (etConfirmPassword.getText().toString().equals(ProjectConstants.EMPTY_STRING)) {
                    confirmPasswordWrapper.setError(getResources().getString(R.string.enter_valid_password));
                    changePasswordCheck = false;
                }

                if(changePasswordCheck) {
                    //TODO: Call change password APIs
                }

                break;
        }

    }
}