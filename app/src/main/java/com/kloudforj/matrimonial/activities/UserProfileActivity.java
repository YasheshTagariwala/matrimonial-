package com.kloudforj.matrimonial.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.Gson;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.entities.UserProfile;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;
import com.kloudforj.matrimonial.utils.Tools;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import com.kloudforj.matrimonial.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private String token;
    private int user_id;
    private SharedPreferences globalSP;
    boolean isSelf = false;
    private Call userDetailsRequestCall;

//========     Added by ellis On date 30-09-2018     ================
    private LinearLayout layout_dots;
    private ViewPager viewPager;
    private AdapterImageSlider adapterImageSlider;
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
    RadioButton radioButtonMale, radioButtonFemale;

    LinearLayout linearLayoutPhone,linearLayoutEducation;
    CardView cardViewAddress;

    ImageButton imageButtonAddress1, imageButtonAddress2,imageButtonAddress3,imageButtonCancel,imageButtonCalendar, imageButtonAddEducation;

    TextView textViewFullName, textViewAboutMe, textViewHobby, textViewBirthDate,
            textViewAddress1, textViewAddress2, textViewAddress3, textViewPhone, textViewGender,
            textViewCountry, textViewState, textViewCity, textViewCaste, textViewSubCaste1, textViewSubCaste2,
            textViewUserHeight,textViewUserWeight,textViewUserBirthPlace,textViewUserBirthTime,
            textViewUserJob,textViewUserEducation,textViewFatherName,textViewFatherEducation,
            textViewFatherProfession,textViewFatherBirthPlace,textViewMotherName,
            textViewMotherEducation,textViewMotherProfession,textViewMotherBirthPlace;

    EditText editTextFullName, editTextAboutMe, editTextHobby,
            editTextAddress1, editTextAddress2, editTextAddress3, editTextPhone,
            editTextUserHeight,editTextUserWeight,editTextUserBirthPlace,editTextUserBirthTime,
            editTextUserJob,editTextFatherName,editTextFatherEducation,editTextFatherProfession,
            editTextFatherBirthPlace,editTextMotherName,editTextMotherEducation,editTextMotherProfession,editTextMotherBirthPlace;

    Spinner spinnerGender, spinnerCountry, spinnerState, spinnerCity,
            spinnerCast, spinnerSubCast1, spinnerSubCast2;

    RadioGroup radioGroupSex;

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

        radioButtonMale = findViewById(R.id.radioMale);
        radioButtonFemale = findViewById(R.id.radioFemale);
        radioGroupSex = findViewById(R.id.radioSex);

        linearLayoutPhone = findViewById(R.id.linearlayout_phone);
        linearLayoutEducation = findViewById(R.id.linearlayout_education);
        cardViewAddress = findViewById(R.id.cardview_address);

        imageButtonAddress1 = findViewById(R.id.imagebutton_address_1);
        imageButtonAddress2 = findViewById(R.id.imagebutton_address_2);
        imageButtonAddress3 = findViewById(R.id.imagebutton_address_3);
        imageButtonCancel = findViewById(R.id.imagebutton_Cancel);
        imageButtonCalendar = findViewById(R.id.imagebutton_calendar);
        imageButtonAddEducation = findViewById(R.id.imagebutton_add_education);
        imageButtonAddEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEducationAdd();
            }
        });
        imageButtonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

//        spinnerGender = findViewById(R.id.spn_gender);
        spinnerCountry = findViewById(R.id.spn_user_country);
        spinnerState = findViewById(R.id.spn_user_state);
        spinnerCity = findViewById(R.id.spn_user_city);
        spinnerCast = findViewById(R.id.spn_user_caste);
        spinnerSubCast1 = findViewById(R.id.spn_user_sub_caste_1);
        spinnerSubCast2 = findViewById(R.id.spn_user_sub_caste_2);
//        spinnerGender.setClickable(false);
        spinnerCountry.setClickable(false);
        spinnerState.setClickable(false);
        spinnerCity.setClickable(false);
        spinnerCast.setClickable(false);
        spinnerSubCast1.setClickable(false);
        spinnerSubCast2.setClickable(false);

        textViewFullName = findViewById(R.id.text_full_name);
        textViewAboutMe = findViewById(R.id.text_about_me);
        textViewHobby = findViewById(R.id.text_hobby);
        textViewBirthDate = findViewById(R.id.text_birth_date);
        textViewAddress1 = findViewById(R.id.text_address_1);
        textViewAddress2 = findViewById(R.id.text_address_2);
        textViewAddress3 = findViewById(R.id.text_address_3);
        textViewPhone = findViewById(R.id.text_phone);
        textViewGender = findViewById(R.id.textview_gender);

        textViewCountry = findViewById(R.id.text_user_country);
        textViewState = findViewById(R.id.text_user_state);
        textViewCity = findViewById(R.id.text_user_city);
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


        editTextFullName = findViewById(R.id.editText_full_name);
        editTextAboutMe = findViewById(R.id.editText_about_me);
        editTextHobby = findViewById(R.id.editText_hobby);
        editTextAddress1 = findViewById(R.id.editText_address_1);
        editTextAddress2 = findViewById(R.id.editText_address_2);
        editTextAddress3 = findViewById(R.id.editText_address_3);
        editTextPhone = findViewById(R.id.editText_phone);

        editTextUserHeight = findViewById(R.id.editText_user_height);
        editTextUserWeight = findViewById(R.id.editText_user_weight);
        editTextUserBirthPlace = findViewById(R.id.editText_user_birth_place);
        editTextUserBirthTime = findViewById(R.id.editText_user_birth_time);
        editTextUserJob = findViewById(R.id.editText_user_job);
        editTextFatherName = findViewById(R.id.editText_father_name);
        editTextFatherEducation = findViewById(R.id.editText_father_education);
        editTextFatherProfession = findViewById(R.id.editText_father_profession);
        editTextFatherBirthPlace = findViewById(R.id.editText_father_birth_place);
        editTextMotherName = findViewById(R.id.editText_mother_name);
        editTextMotherEducation = findViewById(R.id.editText_mother_education);
        editTextMotherProfession = findViewById(R.id.editText_mother_profession);
        editTextMotherBirthPlace = findViewById(R.id.editText_mother_birth_place);

//        buttonSave = findViewById(R.id.button_profile_save);
//        buttonSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setDataEditable(false);
//            }
//        });

        imageButtonSave = findViewById(R.id.imagebutton_profile_save);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
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

        if(isSelf) {
            fabEdit.setVisibility(View.VISIBLE);
        } else {
            fabEdit.setVisibility(View.GONE);
            cardViewAddress.setVisibility(View.GONE);
            linearLayoutPhone.setVisibility(View.GONE);
        }

//        textViewBirthDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isSelf && modeEdit)
//                    openDatePicjker();
//            }
//        });

        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataEditable(false);
            }
        });

        fetchUserDetails();

        initComponent();

    }

    //========     Added by ellis On date 30-09-2018     ================
    private void initComponent() {
        layout_dots = findViewById(R.id.layout_dots);
        viewPager = findViewById(R.id.pager);
        adapterImageSlider = new AdapterImageSlider(this, new ArrayList<Image>());

        List<com.kloudforj.matrimonial.model.Image> items = new ArrayList<>();
        for (int i : array_image_product) {
            com.kloudforj.matrimonial.model.Image obj = new com.kloudforj.matrimonial.model.Image();
            obj.image = i;
            obj.imageDrw = getResources().getDrawable(obj.image);
            items.add(obj);
        }

        adapterImageSlider.setItems(items);
        viewPager.setAdapter(adapterImageSlider);

        // displaying selected image first
        viewPager.setCurrentItem(0);
        addBottomDots(layout_dots, adapterImageSlider.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                addBottomDots(layout_dots, adapterImageSlider.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        startAutoSlider(adapterImageSlider.getCount());
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

    private static class AdapterImageSlider extends PagerAdapter {

        private Activity act;
        private List<Image> items;

        private OnItemClickListener onItemClickListener;

        private interface OnItemClickListener {
            void onItemClick(View view, Image obj);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        // constructor
        private AdapterImageSlider(Activity activity, List<Image> items) {
            this.act = activity;
            this.items = items;
        }

        @Override
        public int getCount() {
            return this.items.size();
        }

        public Image getItem(int pos) {
            return items.get(pos);
        }

        public void setItems(List<Image> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Image o = items.get(position);
            LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_slider_image, container, false);

            ImageView image = v.findViewById(R.id.image);
            MaterialRippleLayout lyt_parent = v.findViewById(R.id.lyt_parent);
            Tools.displayImageOriginal(act, image, o.image);
            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, o);
                    }
                }
            });

            (container).addView(v);

            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((RelativeLayout) object);

        }

    }
    //===================================================================

    public void setDataEditable(boolean canEdit) {
//        spinnerGender.setClickable(canEdit);
        spinnerCountry.setClickable(canEdit);
        spinnerState.setClickable(canEdit);
        spinnerCity.setClickable(canEdit);
        spinnerCast.setClickable(canEdit);
        spinnerSubCast1.setClickable(canEdit);
        spinnerSubCast2.setClickable(canEdit);
        modeEdit = canEdit;

        if (canEdit) {

            textViewCountry.setVisibility(View.GONE);
            textViewState.setVisibility(View.GONE);
            textViewCity.setVisibility(View.GONE);
            textViewCaste.setVisibility(View.GONE);
            textViewSubCaste1.setVisibility(View.GONE);
            textViewSubCaste2.setVisibility(View.GONE);

            spinnerCountry.setVisibility(View.VISIBLE);
            spinnerState.setVisibility(View.VISIBLE);
            spinnerCity.setVisibility(View.VISIBLE);
            spinnerCast.setVisibility(View.VISIBLE);
            spinnerSubCast1.setVisibility(View.VISIBLE);
            spinnerSubCast2.setVisibility(View.VISIBLE);

            imageButtonCancel.setVisibility(View.VISIBLE);
            imageButtonAddEducation.setVisibility(View.VISIBLE);
            radioButtonMale.setClickable(true);
            radioButtonFemale.setClickable(true);
            imageButtonCalendar.setVisibility(View.VISIBLE);

            imageButtonAddress1.setBackgroundResource(R.drawable.ic_edit_location_white_24dp);
            imageButtonAddress2.setBackgroundResource(R.drawable.ic_edit_location_white_24dp);
            imageButtonAddress3.setBackgroundResource(R.drawable.ic_edit_location_white_24dp);

            fabEdit.setVisibility(View.GONE);
            imageButtonSave.setVisibility(View.VISIBLE);
//            buttonSave.setVisibility(View.VISIBLE);

            textViewFullName.setVisibility(View.GONE);
            editTextFullName.setVisibility(View.VISIBLE);

            textViewAboutMe.setVisibility(View.GONE);
            editTextAboutMe.setVisibility(View.VISIBLE);

            textViewHobby.setVisibility(View.GONE);
            editTextHobby.setVisibility(View.VISIBLE);

            textViewAddress1.setVisibility(View.GONE);
            editTextAddress1.setVisibility(View.VISIBLE);

            textViewAddress2.setVisibility(View.GONE);
            editTextAddress2.setVisibility(View.VISIBLE);

            textViewAddress3.setVisibility(View.GONE);
            editTextAddress3.setVisibility(View.VISIBLE);

            textViewPhone.setVisibility(View.GONE);
            editTextPhone.setVisibility(View.VISIBLE);

            textViewGender.setVisibility(View.GONE);
            radioGroupSex.setVisibility(View.VISIBLE);

            textViewUserHeight.setVisibility(View.GONE);
            editTextUserHeight.setVisibility(View.VISIBLE);

            textViewUserWeight.setVisibility(View.GONE);
            editTextUserWeight.setVisibility(View.VISIBLE);

            textViewUserBirthPlace.setVisibility(View.GONE);
            editTextUserBirthPlace.setVisibility(View.VISIBLE);

            textViewUserBirthTime.setVisibility(View.GONE);
            editTextUserBirthTime.setVisibility(View.VISIBLE);

            textViewUserJob.setVisibility(View.GONE);
            editTextUserJob.setVisibility(View.VISIBLE);

            textViewFatherName.setVisibility(View.GONE);
            editTextFatherName.setVisibility(View.VISIBLE);

            textViewFatherEducation.setVisibility(View.GONE);
            editTextFatherEducation.setVisibility(View.VISIBLE);

            textViewFatherProfession.setVisibility(View.GONE);
            editTextFatherProfession.setVisibility(View.VISIBLE);

            textViewFatherBirthPlace.setVisibility(View.GONE);
            editTextFatherBirthPlace.setVisibility(View.VISIBLE);

            textViewMotherName.setVisibility(View.GONE);
            editTextMotherName.setVisibility(View.VISIBLE);

            textViewMotherEducation.setVisibility(View.GONE);
            editTextMotherEducation.setVisibility(View.VISIBLE);

            textViewMotherProfession.setVisibility(View.GONE);
            editTextMotherProfession.setVisibility(View.VISIBLE);

            textViewMotherBirthPlace.setVisibility(View.GONE);
            editTextMotherBirthPlace.setVisibility(View.VISIBLE);
        } else {
            textViewCountry.setVisibility(View.VISIBLE);
            textViewState.setVisibility(View.VISIBLE);
            textViewCity.setVisibility(View.VISIBLE);
            textViewCaste.setVisibility(View.VISIBLE);
            textViewSubCaste1.setVisibility(View.VISIBLE);
            textViewSubCaste1.setVisibility(View.VISIBLE);

            spinnerCountry.setVisibility(View.GONE);
            spinnerState.setVisibility(View.GONE);
            spinnerCity.setVisibility(View.GONE);
            spinnerCast.setVisibility(View.GONE);
            spinnerSubCast1.setVisibility(View.GONE);
            spinnerSubCast2.setVisibility(View.GONE);

            imageButtonCancel.setVisibility(View.GONE);
            imageButtonAddEducation.setVisibility(View.GONE);
            radioButtonMale.setClickable(false);
            radioButtonFemale.setClickable(false);
            imageButtonCalendar.setVisibility(View.GONE);

            imageButtonAddress1.setBackgroundResource(R.drawable.ic_place_white_24dp);
            imageButtonAddress2.setBackgroundResource(R.drawable.ic_place_white_24dp);
            imageButtonAddress3.setBackgroundResource(R.drawable.ic_place_white_24dp);

            fabEdit.setVisibility(View.VISIBLE);
            imageButtonSave.setVisibility(View.GONE);
//            buttonSave.setVisibility(View.GONE);

            textViewFullName.setText(editTextFullName.getText());
            textViewFullName.setVisibility(View.VISIBLE);
            editTextFullName.setVisibility(View.GONE);

            textViewAboutMe.setText(editTextAboutMe.getText());
            textViewAboutMe.setVisibility(View.VISIBLE);
            editTextAboutMe.setVisibility(View.GONE);

            textViewHobby.setText(editTextHobby.getText());
            textViewHobby.setVisibility(View.VISIBLE);
            editTextHobby.setVisibility(View.GONE);

            textViewAddress1.setText(editTextAddress1.getText());
            textViewAddress1.setVisibility(View.VISIBLE);
            editTextAddress1.setVisibility(View.GONE);

            textViewAddress2.setText(editTextAddress2.getText());
            textViewAddress2.setVisibility(View.VISIBLE);
            editTextAddress2.setVisibility(View.GONE);

            textViewAddress3.setText(editTextAddress3.getText());
            textViewAddress3.setVisibility(View.VISIBLE);
            editTextAddress3.setVisibility(View.GONE);

            textViewPhone.setText(editTextPhone.getText());
            textViewPhone.setVisibility(View.VISIBLE);
            editTextPhone.setVisibility(View.GONE);

            textViewGender.setVisibility(View.VISIBLE);
            radioGroupSex.setVisibility(View.GONE);

            textViewUserHeight.setVisibility(View.VISIBLE);
            editTextUserHeight.setVisibility(View.GONE);

            textViewUserWeight.setVisibility(View.VISIBLE);
            editTextUserWeight.setVisibility(View.GONE);

            textViewUserBirthPlace.setVisibility(View.VISIBLE);
            editTextUserBirthPlace.setVisibility(View.GONE);

            textViewUserBirthTime.setVisibility(View.VISIBLE);
            editTextUserBirthTime.setVisibility(View.GONE);

            textViewUserJob.setVisibility(View.VISIBLE);
            editTextUserJob.setVisibility(View.GONE);

            textViewFatherName.setVisibility(View.VISIBLE);
            editTextFatherName.setVisibility(View.GONE);

            textViewFatherEducation.setVisibility(View.VISIBLE);
            editTextFatherEducation.setVisibility(View.GONE);

            textViewFatherProfession.setVisibility(View.VISIBLE);
            editTextFatherProfession.setVisibility(View.GONE);

            textViewFatherBirthPlace.setVisibility(View.VISIBLE);
            editTextFatherBirthPlace.setVisibility(View.GONE);

            textViewMotherName.setVisibility(View.VISIBLE);
            editTextMotherName.setVisibility(View.GONE);

            textViewMotherEducation.setVisibility(View.VISIBLE);
            editTextMotherEducation.setVisibility(View.GONE);

            textViewMotherProfession.setVisibility(View.VISIBLE);
            editTextMotherProfession.setVisibility(View.GONE);

            textViewMotherBirthPlace.setVisibility(View.VISIBLE);
            editTextMotherBirthPlace.setVisibility(View.GONE);
        }
    }

    public void openDatePicker(){
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(UserProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox

                        if (year < mYear)
                            view.updateDate(mYear,mMonth,mDay);

                        if (monthOfYear < mMonth && year == mYear)
                            view.updateDate(mYear,mMonth,mDay);

                        if (dayOfMonth < mDay && year == mYear && monthOfYear == mMonth)
                            view.updateDate(mYear,mMonth,mDay);

                        textViewBirthDate.setText(String.valueOf(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));

                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();
    }

    public void onBackPressed() {
        if (modeEdit) {
            setDataEditable(false);
        }else{
            super.onBackPressed();
            return;
        }
    }

    private void fetchUserDetails() {

        if(DetectConnection.checkInternetConnection(UserProfileActivity.this)) {

            OkHttpClient clientUserDetails = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.USER_PROFILE_URL + ProjectConstants.SLASH + user_id).newBuilder();

            String urlUserDetails = urlBuilder.build().toString(); // URL is converted to String
            /*Log.e("URL UserList : ", urlUserDetails);*/

            final Request requestUserDetails = new Request.Builder()
                    .url(urlUserDetails)
                    .header(ProjectConstants.APITOKEN, token)
                    .build();

            userDetailsRequestCall = clientUserDetails.newCall(requestUserDetails);
            userDetailsRequestCall.enqueue(new Callback() {
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

                                                Gson gson = new Gson();
                                                UserProfile userProfile = gson.fromJson(jsonObjectData.toString(), UserProfile.class);

                                                textViewFullName.setText(String.valueOf(userProfile.getProfile().getFirst_name()+" "+userProfile.getProfile().getMiddle_name()+" "+userProfile.getProfile().getLast_name()));
                                                textViewBirthDate.setText(userProfile.getProfile().getDate_of_birth());
                                                textViewGender.setText((userProfile.getProfile().getSex().toLowerCase().equals("m")?"Male":"Female"));
                                                textViewPhone.setText(userProfile.getProfile().getPhone_number());

                                                textViewCaste.setText(userProfile.getProfile().getCaste());
                                                textViewSubCaste1.setText(userProfile.getProfile().getSub_caste1());
                                                textViewSubCaste2.setText(userProfile.getProfile().getSub_caste2());

                                                textViewAboutMe.setText(userProfile.getExtra().getAbout_me());

                                                textViewAddress1.setText(userProfile.getProfile().getAddress1());
                                                textViewAddress2.setText(userProfile.getProfile().getAddress2());
                                                textViewAddress3.setText(userProfile.getProfile().getAddress3());

                                                textViewCountry.setText(userProfile.getProfile().getCountry());
                                                textViewState.setText(userProfile.getProfile().getState());
                                                textViewCity.setText(userProfile.getProfile().getCity());

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
            });

        }
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
