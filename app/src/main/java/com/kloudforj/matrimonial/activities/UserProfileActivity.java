package com.kloudforj.matrimonial.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.ViewAnimation;
import com.google.gson.Gson;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.adapters.AdapterGridBasic;
import com.kloudforj.matrimonial.adapters.SpacingItemDecoration;
import com.kloudforj.matrimonial.adapters.UserImageSliderAdapter;
import com.kloudforj.matrimonial.entities.UserProfile;
import com.kloudforj.matrimonial.entities.UserProfileImage;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;
import com.kloudforj.matrimonial.utils.Tools;
import com.kloudforj.matrimonial.utils.ViewAnimations;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.kloudforj.matrimonial.utils.Tools.*;
import static com.kloudforj.matrimonial.utils.ViewAnimations.*;

public class UserProfileActivity extends AppCompatActivity {

    private String token;
    private int user_id;
    private SharedPreferences globalSP;
    boolean isSelf = false;
    private Call userDetailsRequestCall;
    private JSONObject userProfile;

    //========     Added by ellis On date 30-09-2018     ================

    private ImageButton img_toggle_basic_info, img_toggle_extra_info, img_toggle_education_info, img_toggle_family_info;
    private View basic_info_expanded_view, extra_info_expanded_view, education_info_expanded_view, family_info_expanded_view;
    private NestedScrollView nested_scroll_view;

//========     Added by ellis On date 30-09-2018     ================
    private LinearLayout layout_dots;
    private ViewPager viewPager;
    private UserImageSliderAdapter userImageSliderAdapter;
    private Runnable runnable = null;
    private Handler handler = new Handler();

    private static int[] array_image_product = {
            R.drawable.profile_image,
            R.drawable.profile_image,
            R.drawable.profile_image,
    };
//===================================================================

    private ProgressBar mUserProfileActvityProgressBar;

    FloatingActionButton fabEdit;
    Button buttonSave;
    ImageButton imageButtonSave;
    boolean modeEdit = false;

    LinearLayout linearLayoutPhone,linearLayoutEducation;

    ImageButton imageButtonCancel,imageButtonCalendar;

    TextView textViewFullName, textViewAboutMe, textViewHobby, textViewBirthDate, textViewVerifyPhone, textViewVerifymail,
            textViewPhone, textViewGender,
            textViewCaste, textViewSubCaste1, textViewSubCaste2,
            textViewUserHeight,textViewUserWeight,textViewUserBirthPlace,textViewUserBirthTime,
            textViewUserJob,textViewUserEducation,textViewFatherName,textViewFatherEducation,
            textViewFatherProfession,textViewFatherBirthPlace,textViewMotherName,
            textViewMotherEducation,textViewMotherProfession,textViewMotherBirthPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mUserProfileActvityProgressBar = (ProgressBar) findViewById(R.id.pb_userprofile_activity);
        if (mUserProfileActvityProgressBar != null) {
            mUserProfileActvityProgressBar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(UserProfileActivity.this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        globalSP = getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("user_id")) {
                user_id = extras.getInt("user_id");
            }
        } else {
            user_id = globalSP.getInt(ProjectConstants.USERID, 0);
            isSelf = true;
        }

        linearLayoutPhone = findViewById(R.id.linearlayout_phone);
        linearLayoutEducation = findViewById(R.id.linearlayout_education);
        imageButtonCancel = findViewById(R.id.imagebutton_Cancel);
        imageButtonCalendar = findViewById(R.id.imagebutton_calendar);

        textViewVerifyPhone = findViewById(R.id.text_verify_phone);
        textViewVerifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this,VerificationActivity.class);
                intent.putExtra("type","phone");
                startActivity(intent);
            }
        });
        textViewVerifymail = findViewById(R.id.text_verify_mail);
        textViewVerifymail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this,VerificationActivity.class);
                intent.putExtra("type","mail");
                startActivity(intent);
            }
        });

        textViewFullName = findViewById(R.id.text_full_name);
        textViewAboutMe = findViewById(R.id.text_about_me);
        textViewHobby = findViewById(R.id.text_hobby);
        textViewBirthDate = findViewById(R.id.text_birth_date);
        textViewPhone = findViewById(R.id.text_phone);
        textViewGender = findViewById(R.id.textview_gender);

        textViewCaste = findViewById(R.id.text_user_caste);
        textViewSubCaste1 = findViewById(R.id.text_user_sub_caste_1);
        textViewSubCaste2 = findViewById(R.id.text_user_sub_caste_2);

        textViewUserHeight = findViewById(R.id.text_user_height);
        textViewUserWeight = findViewById(R.id.text_user_weight);
        textViewUserBirthPlace = findViewById(R.id.text_user_birth_place);
        textViewUserBirthTime = findViewById(R.id.text_user_birth_time);
        textViewUserJob = findViewById(R.id.text_user_job);
        textViewUserEducation = findViewById(R.id.text_user_education);
        textViewFatherName = findViewById(R.id.text_father_name);
        textViewFatherEducation = findViewById(R.id.text_father_education);
        textViewFatherProfession = findViewById(R.id.text_father_profession);
        textViewFatherBirthPlace = findViewById(R.id.text_father_birth_place);
        textViewMotherName = findViewById(R.id.text_mother_name);
        textViewMotherEducation = findViewById(R.id.text_mother_education);
        textViewMotherProfession = findViewById(R.id.text_mother_profession);
        textViewMotherBirthPlace = findViewById(R.id.text_mother_birth_place);

        fabEdit = findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(UserProfileActivity.this, UserEditProfileActivity.class);
                editProfile.putExtra("userProfile",userProfile.toString());
                startActivity(editProfile);
            }
        });

        if(isSelf) {
            fabEdit.setVisibility(View.VISIBLE);
            textViewVerifymail.setVisibility(View.VISIBLE);
            textViewVerifyPhone.setVisibility(View.VISIBLE);
        } else {
            fabEdit.setVisibility(View.GONE);
            linearLayoutPhone.setVisibility(View.GONE);
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.USER_PROFILE_URL + ProjectConstants.SLASH + user_id).newBuilder();
        if(DetectConnection.checkInternetConnection(this)) {
            new ProjectConstants.getDataFromServer(new JSONObject(),new FetchUserDetails(),this).execute(urlBuilder.build().toString(),token);
        }else{
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }

        initComponent();

    }

    //========     Added by ellis On date 30-09-2018     ================
    private void initComponent() {
        layout_dots = findViewById(R.id.layout_dots);
        viewPager = findViewById(R.id.pager);
        userImageSliderAdapter = new UserImageSliderAdapter(this, new ArrayList<UserProfileImage>());

        img_toggle_basic_info = findViewById(R.id.img_toggle_basic_info);
        basic_info_expanded_view = findViewById(R.id.basic_info_expanded_view);
        img_toggle_basic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionInfo(img_toggle_basic_info);
            }
        });

        img_toggle_extra_info = findViewById(R.id.img_toggle_extra_info);
        extra_info_expanded_view = findViewById(R.id.extra_info_expanded_view);
        img_toggle_extra_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionExtraInfo(img_toggle_extra_info);
            }
        });

        img_toggle_education_info = findViewById(R.id.img_toggle_education_info);
        education_info_expanded_view = findViewById(R.id.education_info_expanded_view);
        img_toggle_education_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionEducationInfo(img_toggle_education_info);
            }
        });

        img_toggle_family_info = findViewById(R.id.img_toggle_family_info);
        family_info_expanded_view = findViewById(R.id.family_info_expanded_view);
        img_toggle_family_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionFamilyInfo(img_toggle_family_info);
            }
        });

        nested_scroll_view = findViewById(R.id.nested_scroll_view);



        List<UserProfileImage> items = new ArrayList<>();
        for (int i : array_image_product) {
            UserProfileImage obj = new UserProfileImage();
            obj.image = i;
            obj.imageDrw = getResources().getDrawable(obj.image);
            items.add(obj);
        }

        userImageSliderAdapter.setItems(items);
        viewPager.setAdapter(userImageSliderAdapter);

        // displaying selected image first
        viewPager.setCurrentItem(0);
        addBottomDots(layout_dots, userImageSliderAdapter.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                addBottomDots(layout_dots, userImageSliderAdapter.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

//        startAutoSlider(userImageSliderAdapter.getCount());
    }

    private void toggleSectionInfo(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimations.expand(basic_info_expanded_view, new ViewAnimations.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, basic_info_expanded_view);

                }
            });
        } else {
            ViewAnimations.collapse(basic_info_expanded_view);
        }
    }

    private void toggleSectionExtraInfo(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimations.expand(extra_info_expanded_view, new ViewAnimations.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, extra_info_expanded_view);
                }
            });
        } else {
            ViewAnimations.collapse(extra_info_expanded_view);
        }
    }

    private void toggleSectionEducationInfo(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimations.expand(education_info_expanded_view, new ViewAnimations.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, education_info_expanded_view);
                }
            });
        } else {
            ViewAnimations.collapse(education_info_expanded_view);
        }
    }

    private void toggleSectionFamilyInfo(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimations.expand(family_info_expanded_view, new ViewAnimations.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, family_info_expanded_view);
                }
            });
        } else {
            ViewAnimations.collapse(family_info_expanded_view);
        }
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(ContextCompat.getColor(this, R.color.overlay_dark_10), PorterDuff.Mode.SRC_ATOP);
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current].setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryLight), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void startAutoSlider(final int count) {
        runnable = new Runnable() {
            @Override
            public void run() {
                int pos = viewPager.getCurrentItem();
                pos = pos + 1;
                if (pos >= count) pos = 0;
                viewPager.setCurrentItem(pos);
                handler.postDelayed(runnable, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    //===================================================================

    public class FetchUserDetails implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if(!response.isSuccessful()) {
                enableComponents(getResources().getString(R.string.something_went_wrong));
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                //Log.e("resp : ", result);

                if(result != null) {
                    try {
                        final JSONObject jsonUserProfile = new JSONObject(result);

                        final Boolean auth = jsonUserProfile.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonUserProfile.getString(ProjectConstants.MESSAGE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mUserProfileActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                if(auth) {
                                    try {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                        JSONObject jsonObjectData = jsonUserProfile.getJSONObject(ProjectConstants.DATA);
                                        userProfile = jsonUserProfile.getJSONObject(ProjectConstants.DATA);

                                        Gson gson = new Gson();
                                        UserProfile userProfile = gson.fromJson(jsonObjectData.toString(), UserProfile.class);

                                        textViewFullName.setText(String.valueOf(userProfile.getProfile().getFirst_name()+" "+userProfile.getProfile().getMiddle_name()+" "+userProfile.getProfile().getLast_name()));
                                        textViewBirthDate.setText(userProfile.getProfile().getDate_of_birth());
                                        textViewGender.setText((userProfile.getProfile().getSex().toLowerCase().equals("m")?"Male":"Female"));
                                        textViewPhone.setText(userProfile.getProfile().getPhone_number());

                                        textViewCaste.setText(userProfile.getProfile().getCaste());
                                        textViewSubCaste1.setText(userProfile.getProfile().getSub_caste1());
                                        textViewSubCaste2.setText(userProfile.getProfile().getSub_caste2());

                                        textViewUserHeight.setText(userProfile.getExtra().getHeight());
                                        textViewUserWeight.setText(userProfile.getExtra().getWeight());
                                        textViewUserBirthPlace.setText(userProfile.getExtra().getBirth_place());
                                        textViewUserBirthTime.setText(userProfile.getExtra().getBirth_time());
                                        textViewUserJob.setText(userProfile.getExtra().getCurrent_job());
                                        textViewAboutMe.setText(userProfile.getExtra().getAbout_me());

//                                        textViewAddress1.setText(userProfile.getProfile().getAddress1());
//                                        textViewAddress2.setText(userProfile.getProfile().getAddress2());
//                                        textViewAddress3.setText(userProfile.getProfile().getAddress3());
//                                        textViewCountry.setText(userProfile.getProfile().getCountry());
//                                        textViewState.setText(userProfile.getProfile().getState());
//                                        textViewCity.setText(userProfile.getProfile().getCity());

                                                /*textViewFatherName.setText(userProfile.getFamilyDetails().getFather_name());
                                                textViewFatherEducation.setText(userProfile.getFamilyDetails().getFather_education());
                                                textViewFatherProfession.setText(userProfile.getFamilyDetails().getFather_profession());
                                                textViewFatherBirthPlace.setText(userProfile.getFamilyDetails().getFather_birth_place());
                                                textViewMotherName.setText(userProfile.getFamilyDetails().getMother_name());
                                                textViewMotherEducation.setText(userProfile.getFamilyDetails().getMother_education());
                                                textViewMotherProfession.setText(userProfile.getFamilyDetails().getMother_profession());
                                                textViewMotherBirthPlace.setText(userProfile.getFamilyDetails().getMother_birth_place());*/

                                    } catch (JSONException e) {
                                        enableComponents(getResources().getString(R.string.something_went_wrong));
                                        e.printStackTrace();
                                    }
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
     * Toast message and progressbar invisible
     *
     * @param msg
     */
    private void enableComponents(final String msg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUserProfileActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled
                Toast.makeText(UserProfileActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showEducationAdd(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Education"); //Set Alert dialog title here
        alert.setMessage("Here You Can Add new Education"); //Message here

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String srt = input.getEditableText().toString();
                textViewUserEducation.setText(textViewUserEducation.getText().toString() + "\n" + srt);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
}
