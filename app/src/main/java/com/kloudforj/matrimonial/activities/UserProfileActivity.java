package com.kloudforj.matrimonial.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kloudforj.matrimonial.R;

public class UserProfileActivity extends AppCompatActivity {

    String user_id = "";
    boolean isSelf = false;
    FloatingActionButton fabEdit;
    Button buttonSave;

    TextView textViewFullName,textViewAboutMe,textViewHobby;
    EditText editTextFullName,editTextAboutMe,editTextHobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("user_id")) {
                user_id = extras.getString("user_id");
                isSelf = true;
            }
        }

        textViewFullName = findViewById(R.id.text_full_name);
        textViewAboutMe = findViewById(R.id.text_about_me);
        textViewHobby = findViewById(R.id.text_hobby);

        editTextFullName = findViewById(R.id.editText_full_name);
        editTextAboutMe = findViewById(R.id.editText_about_me);
        editTextHobby = findViewById(R.id.editText_hobby);

        buttonSave = findViewById(R.id.button_profile_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataEditable(false);
            }
        });

        fabEdit = findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataEditable(true);
            }
        });

        if(isSelf){
            fabEdit.setVisibility(View.VISIBLE);
        }else {
            fabEdit.setVisibility(View.GONE);
        }
    }

    public void setDataEditable(boolean canEdit){
        if (canEdit){
            fabEdit.setVisibility(View.GONE);
            buttonSave.setVisibility(View.VISIBLE);

            textViewFullName.setVisibility(View.GONE);
            editTextFullName.setVisibility(View.VISIBLE);

            textViewAboutMe.setVisibility(View.GONE);
            editTextAboutMe.setVisibility(View.VISIBLE);

            textViewHobby.setVisibility(View.GONE);
            editTextHobby.setVisibility(View.VISIBLE);
        }else {
            fabEdit.setVisibility(View.VISIBLE);
            buttonSave.setVisibility(View.GONE);

            textViewFullName.setText(editTextFullName.getText());
            textViewFullName.setVisibility(View.VISIBLE);
            editTextFullName.setVisibility(View.GONE);

            textViewAboutMe.setText(editTextAboutMe.getText());
            textViewAboutMe.setVisibility(View.VISIBLE);
            editTextAboutMe.setVisibility(View.GONE);

            textViewHobby.setText(editTextHobby.getText());
            textViewHobby.setVisibility(View.VISIBLE);
            editTextHobby.setVisibility(View.GONE);
        }
    }
}
