package com.kloudforj.matrimonial.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.adapters.UserImageSliderAdapter;
import com.kloudforj.matrimonial.entities.UserProfile;
import com.kloudforj.matrimonial.entities.UserProfileImage;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;
import com.kloudforj.matrimonial.utils.Tools;
import com.kloudforj.matrimonial.utils.ViewAnimations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Response;

import static com.kloudforj.matrimonial.utils.Tools.*;

public class UserProfileActivity extends AppCompatActivity {

    private String token;
    private int user_id;
    private SharedPreferences globalSP;
    boolean isSelf = false;
    private Call userDetailsRequestCall;
    private JSONObject userProfile1;

    //========     Added by ellis On date 30-09-2018     ================

    private LinearLayout toggle_basic_info, toggle_education_info, toggle_extra_info, toggle_family_info;
    private ImageView img_toggle_basic_info, img_toggle_extra_info, img_toggle_education_info, img_toggle_family_info, img_toggle_address_info;
    private View basic_info_expanded_view, extra_info_expanded_view, education_info_expanded_view, family_info_expanded_view, toggle_address_info, address_info_expanded_view;
    private NestedScrollView nested_scroll_view;

    //========     Added by ellis On date 30-09-2018     ================
    private LinearLayout layout_dots;
    private ViewPager viewPager;
    private UserImageSliderAdapter userImageSliderAdapter;
    private Runnable runnable = null;
    private Handler handler = new Handler();

    private String[] array_image_product;
    private Integer[] array_image_product_is_verified;

    CardView cardViewPrivateHolder;
    Switch switchPrivate;
//===================================================================

    private ProgressBar mUserProfileActvityProgressBar;

    FloatingActionButton fabEdit;
    Button buttonSave;
    ImageButton imageButtonSave;
    boolean modeEdit = false;

    LinearLayout linearLayoutPhone, linearLayoutEducation;

    ImageButton imageButtonCancel, imageButtonPhoneNumberNotVerified, imageButtonPhoneNumberVerified, imageButtonEmailNotVerified, imageButtonEmailVerified;

    TextView textViewFullName, textViewAboutMe, textViewHobby, textViewBirthDate,
            textViewPhone, textViewGender, textViewEmail,
            textViewCaste, textViewSubCaste1, textViewSubCaste2, textViewMaritalStatus, textViewIncome,
            textViewUserHeight, textViewUserWeight, textViewUserBirthPlace, textViewUserBirthTime,
            textViewUserJob, textViewUserEducation, textViewFatherName, textViewFatherEducation,
            textViewFatherProfession, textViewFatherBirthPlace, textViewMotherName,
            textViewMotherEducation, textViewMotherProfession, textViewMotherBirthPlace, textViewAddress1, textViewAddress2, textViewAddress3, textViewPincode, textviewLocation;

    AlertDialog alertDialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        cardViewPrivateHolder = findViewById(R.id.cv_upa_private_holder);
        switchPrivate = findViewById(R.id.sw_upa_private);

        LayoutInflater inflater = LayoutInflater.from(UserProfileActivity.this);
        View alertLoading = inflater.inflate(R.layout.layout_loading, null);
        ProgressBar mLoadingProgressbar = alertLoading.findViewById(R.id.pb_loading);
        if (mLoadingProgressbar != null) {
            mLoadingProgressbar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(UserProfileActivity.this, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        AlertDialog.Builder builderLoading = new AlertDialog.Builder(UserProfileActivity.this);
        builderLoading.setTitle("Loading");
        builderLoading.setView(alertLoading);
        builderLoading.setCancelable(true);

        alertDialogLoading = builderLoading.create();

        alertDialogLoading.dismiss();

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

        imageButtonPhoneNumberNotVerified = findViewById(R.id.view_phone_number_not_verified);
        imageButtonPhoneNumberVerified = findViewById(R.id.view_phone_number_verified);
        imageButtonEmailNotVerified = findViewById(R.id.view_email_not_verified);
        imageButtonEmailVerified = findViewById(R.id.view_email_verified);

        imageButtonPhoneNumberNotVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyActivity("Phone");
            }
        });

        imageButtonEmailNotVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyActivity("Email");
            }
        });

        textViewFullName = findViewById(R.id.text_full_name);
        textViewAboutMe = findViewById(R.id.text_about_me);
        textViewHobby = findViewById(R.id.text_hobby);
        textViewBirthDate = findViewById(R.id.text_birth_date);
        textViewPhone = findViewById(R.id.text_phone);
        textViewGender = findViewById(R.id.textview_gender);
        textViewMaritalStatus = findViewById(R.id.text_marital_status);
        textViewIncome = findViewById(R.id.text_user_income);
        textViewEmail = findViewById(R.id.text_user_mail);

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

        textViewAddress1 = findViewById(R.id.text_address1);
        textViewAddress2 = findViewById(R.id.text_address2);
        textViewAddress3 = findViewById(R.id.text_address3);
        textViewPincode = findViewById(R.id.text_pincode);
        textviewLocation = findViewById(R.id.text_location);

        fabEdit = findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userProfile1 != null) {

                    alertDialogLoading.show();

                    Intent editProfile = new Intent(UserProfileActivity.this, UserEditProfileActivity.class);
                    editProfile.putExtra("userProfile", userProfile1.toString());
                    startActivity(editProfile);

                } else {
                    Toast.makeText(UserProfileActivity.this, UserProfileActivity.this.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
            }
        });

        if (isSelf) {
            fabEdit.setVisibility(View.VISIBLE);
            imageButtonPhoneNumberVerified.setVisibility(View.GONE);
            imageButtonPhoneNumberNotVerified.setVisibility(View.VISIBLE);
            imageButtonEmailVerified.setVisibility(View.GONE);
            imageButtonEmailNotVerified.setVisibility(View.VISIBLE);
            cardViewPrivateHolder.setVisibility(View.VISIBLE);
        } else {
            fabEdit.setVisibility(View.GONE);
            imageButtonPhoneNumberVerified.setVisibility(View.GONE);
            imageButtonPhoneNumberNotVerified.setVisibility(View.GONE);
            imageButtonEmailVerified.setVisibility(View.GONE);
            imageButtonEmailNotVerified.setVisibility(View.GONE);
            cardViewPrivateHolder.setVisibility(View.GONE);
//            linearLayoutPhone.setVisibility(View.GONE);
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.USER_PROFILE_URL + ProjectConstants.SLASH + user_id).newBuilder();
        if (DetectConnection.checkInternetConnection(this)) {
            new ProjectConstants.getDataFromServer(new JSONObject(), new FetchUserDetails(), this).execute(urlBuilder.build().toString(), token);
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }

        initComponent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        alertDialogLoading.dismiss();
    }

    public void verifyActivity(String type) {
        boolean is_empty = false;
        String data;
        if (type.equals("Phone")) {
            data = textViewPhone.getText().toString().trim();
            if (textViewPhone.getText().toString().trim().equals("-") || textViewPhone.getText().toString().trim().equals("")) {
                is_empty = true;
            }
        } else {
            data = textViewEmail.getText().toString().trim();
            if (textViewEmail.getText().toString().trim().equals("-") || textViewEmail.getText().toString().trim().equals("")) {
                is_empty = true;
            }
        }
        if (is_empty) {
            Toast.makeText(UserProfileActivity.this, type + " Is Not Valid", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(UserProfileActivity.this, VerificationActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("data", data);
            startActivity(intent);
        }
    }

    //========     Added by ellis On date 30-09-2018     ================
    private void initComponent() {
        layout_dots = findViewById(R.id.layout_dots);
        viewPager = findViewById(R.id.pager);
        userImageSliderAdapter = new UserImageSliderAdapter(this, new ArrayList<UserProfileImage>());

        toggle_basic_info = findViewById(R.id.toggle_basic_info);
        img_toggle_basic_info = findViewById(R.id.img_toggle_basic_info);
        basic_info_expanded_view = findViewById(R.id.basic_info_expanded_view);
        toggle_basic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionInfo(img_toggle_basic_info);
            }
        });

        toggle_extra_info = findViewById(R.id.toggle_extra_info);
        img_toggle_extra_info = findViewById(R.id.img_toggle_extra_info);
        extra_info_expanded_view = findViewById(R.id.extra_info_expanded_view);
        toggle_extra_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionExtraInfo(img_toggle_extra_info);
            }
        });

        toggle_education_info = findViewById(R.id.toggle_education_info);
        img_toggle_education_info = findViewById(R.id.img_toggle_education_info);
        education_info_expanded_view = findViewById(R.id.education_info_expanded_view);
        toggle_education_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionEducationInfo(img_toggle_education_info);
            }
        });

        toggle_family_info = findViewById(R.id.toggle_family_info);
        img_toggle_family_info = findViewById(R.id.img_toggle_family_info);
        family_info_expanded_view = findViewById(R.id.family_info_expanded_view);
        toggle_family_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionFamilyInfo(img_toggle_family_info);
            }
        });

        toggle_address_info = findViewById(R.id.toggle_address_info);
        img_toggle_address_info = findViewById(R.id.img_toggle_address_info);
        address_info_expanded_view = findViewById(R.id.address_info_expanded_view);
        toggle_address_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionaddressInfo(img_toggle_address_info);
            }
        });

        nested_scroll_view = findViewById(R.id.nested_scroll_view);

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

    private void toggleSectionaddressInfo(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimations.expand(address_info_expanded_view, new ViewAnimations.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, address_info_expanded_view);
                }
            });
        } else {
            ViewAnimations.collapse(address_info_expanded_view);
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
            if (!response.isSuccessful()) {
                enableComponents(getResources().getString(R.string.something_went_wrong));
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
//                Log.e("resp : ", result);

                if (result != null) {
                    try {
                        final JSONObject jsonUserProfile = new JSONObject(result);

                        final Boolean auth = jsonUserProfile.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonUserProfile.getString(ProjectConstants.MESSAGE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mUserProfileActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                if (auth) {
                                    try {
//                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                        JSONObject jsonObjectData = jsonUserProfile.getJSONObject(ProjectConstants.DATA);
                                        //Log.e("1 : ", jsonObjectData.toString());
                                        userProfile1 = jsonUserProfile.getJSONObject(ProjectConstants.DATA);

                                        Gson gson = new Gson();
                                        UserProfile userProfile = gson.fromJson(jsonObjectData.toString(), UserProfile.class);

                                        int is_private = userProfile.getProfile().getIs_private();
                                        switchPrivate.setChecked(is_private != 0);
                                        switchPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                JSONObject jsonObjectRequest = new JSONObject();
                                                try {
                                                    jsonObjectRequest.put(ProjectConstants.USERID, user_id);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_1 + ProjectConstants.USER + ProjectConstants.USER_PROFILE_PRIVACY_TOGGLE).newBuilder();
                                                if (DetectConnection.checkInternetConnection(UserProfileActivity.this)) {
                                                    new ProjectConstants.getDataFromServer(jsonObjectRequest, new PrivateUserDetails(), UserProfileActivity.this).execute(urlBuilder.build().toString(), token);
                                                } else {
                                                    Toast.makeText(UserProfileActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        textViewFullName.setText(String.valueOf(userProfile.getProfile().getFirst_name() + " " + userProfile.getProfile().getMiddle_name() + " " + userProfile.getProfile().getLast_name()));
                                        textViewBirthDate.setText(userProfile.getProfile().getDate_of_birth());
                                        textViewGender.setText((userProfile.getProfile().getSex().toLowerCase().equals("m") ? "Male" : "Female"));
                                        textViewPhone.setText(userProfile.getPhone_number() == null || userProfile.getPhone_number().equals("null") ? "-" : userProfile.getPhone_number());
                                        textViewEmail.setText(userProfile.getEmail() == null || userProfile.getEmail().equals("null") ? "-" : userProfile.getEmail());
                                        String martial_status = userProfile.getProfile().getMarital_status();
                                        textViewMaritalStatus.setText(martial_status);
                                        if (martial_status.trim().equals("Divorced")) {
                                            if (Build.VERSION.SDK_INT < 23) {
                                                textViewMaritalStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                                            } else {
                                                textViewMaritalStatus.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                                            }

                                        }

                                        if (isSelf) {
                                            if (userProfile.getProfile().getPhone_number_verified() == 0) {
                                                imageButtonPhoneNumberVerified.setVisibility(View.GONE);
                                                imageButtonPhoneNumberNotVerified.setVisibility(View.VISIBLE);
                                            } else {
                                                imageButtonPhoneNumberVerified.setVisibility(View.VISIBLE);
                                                imageButtonPhoneNumberNotVerified.setVisibility(View.GONE);
                                            }
                                            if (userProfile.getProfile().getEmail_verified() == 0) {
                                                imageButtonEmailVerified.setVisibility(View.GONE);
                                                imageButtonEmailNotVerified.setVisibility(View.VISIBLE);
                                            } else {
                                                imageButtonEmailVerified.setVisibility(View.VISIBLE);
                                                imageButtonEmailNotVerified.setVisibility(View.GONE);
                                            }
                                        } else {
                                            imageButtonPhoneNumberVerified.setVisibility(View.GONE);
                                            imageButtonPhoneNumberNotVerified.setVisibility(View.GONE);
                                            imageButtonEmailVerified.setVisibility(View.GONE);
                                            imageButtonEmailNotVerified.setVisibility(View.GONE);
                                        }

                                        textViewCaste.setText(userProfile.getProfile().getCaste());
                                        textViewSubCaste1.setText(userProfile.getProfile().getSub_caste1());
                                        textViewSubCaste2.setText(userProfile.getProfile().getSub_caste2());
                                        textViewIncome.setText(userProfile.getExtra().getIncome());

                                        textViewUserHeight.setText(userProfile.getExtra().getHeight());
                                        textViewUserWeight.setText(userProfile.getExtra().getWeight());
                                        textViewUserBirthPlace.setText(userProfile.getExtra().getBirth_place());
                                        textViewUserBirthTime.setText(userProfile.getExtra().getBirth_time());
                                        textViewUserJob.setText(userProfile.getExtra().getCurrent_job());
                                        textViewAboutMe.setText(userProfile.getExtra().getAbout_me());

                                        StringBuilder education = new StringBuilder();
                                        for (int i = 0; i < userProfile.getEducation().size(); i++) {
                                            education.append(userProfile.getEducation().get(i)).append(System.getProperty("line.separator"));
                                        }
                                        textViewUserEducation.setText(education.toString());

                                        StringBuilder hobbies = new StringBuilder();
                                        for (int i = 0; i < userProfile.getHobbies().size(); i++) {
                                            hobbies.append(userProfile.getHobbies().get(i)).append(System.getProperty("line.separator"));
                                        }
                                        textViewHobby.setText(hobbies.toString());

                                        array_image_product = new String[userProfile.getImages().length];
                                        array_image_product_is_verified = new Integer[userProfile.getImages().length];
                                        int length = 0;

                                        for (int i = 0; i < userProfile.getImages().length; i++) {
                                            if (!isSelf) {
                                                if (userProfile.getImages()[i].getIs_verified() == 1) {
                                                    length = length + 1;
                                                    array_image_product[i] = userProfile.getImages()[i].getImage_path();
                                                    array_image_product_is_verified[i] = userProfile.getImages()[i].getIs_verified();
                                                }
                                            } else {
                                                array_image_product[i] = userProfile.getImages()[i].getImage_path();
                                                array_image_product_is_verified[i] = userProfile.getImages()[i].getIs_verified();
                                            }
                                        }

                                        if (!isSelf) {
                                            List<String> temp1 = new ArrayList<>();
                                            List<Integer> temp2 = new ArrayList<>();
                                            for (int i = 0; i < array_image_product.length; i++) {
                                                if (array_image_product[i] != null) {
                                                    temp1.add(array_image_product[i]);
                                                    temp2.add(array_image_product_is_verified[i]);
                                                }
                                            }
                                            array_image_product = temp1.toArray(new String[temp1.size()]);
                                            array_image_product_is_verified = temp2.toArray(new Integer[temp2.size()]);
                                        }

                                        textViewAddress1.setText(userProfile.getProfile().getAddress1());
                                        textViewAddress2.setText(userProfile.getProfile().getAddress2());
                                        textViewAddress3.setText(userProfile.getProfile().getAddress3());
                                        textViewPincode.setText(userProfile.getProfile().getPincode());
                                        textviewLocation.setText(userProfile.getProfile().getCountry() + ", " + userProfile.getProfile().getState() + ", " + userProfile.getProfile().getCity());

                                        textViewFatherName.setText(userProfile.getFamily().getFather_name());
                                        textViewFatherEducation.setText(userProfile.getFamily().getFather_education());
                                        textViewFatherProfession.setText(userProfile.getFamily().getFather_profession());
                                        textViewFatherBirthPlace.setText(userProfile.getFamily().getFather_birth_place());
                                        textViewMotherName.setText(userProfile.getFamily().getMother_name());
                                        textViewMotherEducation.setText(userProfile.getFamily().getMother_education());
                                        textViewMotherProfession.setText(userProfile.getFamily().getMother_profession());
                                        textViewMotherBirthPlace.setText(userProfile.getFamily().getMother_birth_place());

                                        List<UserProfileImage> items = new ArrayList<>();
                                        for (int i = 0; i < array_image_product.length; i++) {
                                            UserProfileImage obj = new UserProfileImage();
                                            obj.name = array_image_product[i];
                                            obj.is_verified = array_image_product_is_verified[i];
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

    public void showEducationAdd() {
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

    public class PrivateUserDetails implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
                //Log.e("resp : ", response.toString());
                enableComponents(getResources().getString(R.string.something_went_wrong));
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                //Log.e("resp : ", result);

                if (result != null) {
                    try {
                        final JSONObject jsonUserProfile = new JSONObject(result);

                        final Boolean auth = jsonUserProfile.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonUserProfile.getString(ProjectConstants.MESSAGE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mUserProfileActvityProgressBar.setVisibility(View.GONE); // ProgressBar is Disabled

                                if (auth) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
}
