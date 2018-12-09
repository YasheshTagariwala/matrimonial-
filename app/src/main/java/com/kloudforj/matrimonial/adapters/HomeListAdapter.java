package com.kloudforj.matrimonial.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.activities.MainActivity;
import com.kloudforj.matrimonial.activities.UserProfileActivity;
import com.kloudforj.matrimonial.entities.UserProfile;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.GlideApp;
import com.kloudforj.matrimonial.utils.ProjectConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    public Context context;
    private List<UserProfile> userProfileList;
    private String TAG = "HomeListAdapter";
    private int count = 0;
    private boolean isDummy = false;
    private boolean isFavorite = false;
    private Map<Integer, Boolean> is_favorite;
    private SharedPreferences globalSP;
    public String token;

    public HomeListAdapter(Context context, List<UserProfile> userProfileList, boolean isFavorite, Map<Integer, Boolean> is_favorite) {
        this.context = context;
        this.userProfileList = userProfileList;
        this.isFavorite = isFavorite;
        this.is_favorite = is_favorite;
        globalSP = context.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
        token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);
    }

    public HomeListAdapter(Context context, int count) {
        this.context = context;
        this.count = count;
        this.isDummy = true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserProfile items = userProfileList.get(position);
        holder.tvUserName.setText(String.valueOf(items.getProfile().getFirst_name() + " " + items.getProfile().getMiddle_name() + " " + items.getProfile().getLast_name()));
        holder.tvUserCaste.setText(items.getProfile().getCaste());
        holder.tvUserAge.setText(String.valueOf(items.getProfile().getAge()+" Years"));
        holder.tvUserId.setText("CM" + String.valueOf(items.getProfile().getUser_id()));

        holder.tvUserBirthPlace.setText(items.getExtra().getBirth_place());
        String martial_status = items.getProfile().getMarital_status();
        if(martial_status.trim().equals("Divorced")) {
            if(Build.VERSION.SDK_INT < 23) {
                holder.tvUserMaritalStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.tvUserMaritalStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }else{
            if(Build.VERSION.SDK_INT < 23) {
                holder.tvUserMaritalStatus.setTextColor(context.getResources().getColor(R.color.grey_40));
            } else {
                holder.tvUserMaritalStatus.setTextColor(ContextCompat.getColor(context, R.color.grey_40));
            }
        }
        holder.tvUserMaritalStatus.setText(martial_status);
        holder.tvUserHeight.setText(items.getExtra().getHeight());
        if(items.getEducation().size() > 0){
            holder.tvUserEducation.setText(items.getEducation().get(0));
        }else{
            holder.tvUserEducation.setText("-");
        }

        if(items.getImages().length > 0){
            RequestOptions ro = new RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter();

            GlideUrl url = new GlideUrl(ProjectConstants.BASE_URL + ProjectConstants.IMAGE_GET_URL + "/" + items.getImages()[0].getImage_path(),
                    new LazyHeaders.Builder().addHeader(ProjectConstants.APITOKEN, token).build());
            GlideApp.with(context).load(url).apply(ro)
                    .into(holder.userImage);
        } else {
            if(items.getProfile().getSex().toLowerCase().equals("m")){
                holder.userImage.setImageResource(R.drawable.default_male);
            }else{
                holder.userImage.setImageResource(R.drawable.default_female);
            }
        }

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intentUserProfile = new Intent(context, UserProfileActivity.class);
                intentUserProfile.putExtra(ProjectConstants.USERID, items.getId());
                context.startActivity(intentUserProfile);

                //context.startActivity(new Intent(context,UserProfileActivity.class));
            }
        });
        if (!isFavorite) {
            SharedPreferences globalSP;
            globalSP = context.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
            final String token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);
            if (is_favorite.containsKey(items.getId())) {
                holder.imageButtonFavouritePersonLike.setVisibility(View.VISIBLE);
                holder.imageButtonFavouritePerson.setVisibility(View.GONE);
            }else{
                holder.imageButtonFavouritePersonLike.setVisibility(View.GONE);
                holder.imageButtonFavouritePerson.setVisibility(View.VISIBLE);
            }
            holder.imageButtonFavouritePerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jsonObjectRequest = new JSONObject();
                    try {
                        jsonObjectRequest.put(ProjectConstants.BOOKMARKID, items.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.ADD_TO_FAVORITES_URL).newBuilder();
                    if (DetectConnection.checkInternetConnection(context)) {
                        new ProjectConstants.getDataFromServer(jsonObjectRequest, new AddToFavorite(items.getId()), context).execute(urlBuilder.build().toString(), token);
                        holder.imageButtonFavouritePerson.setVisibility(View.GONE);
                        holder.imageButtonFavouritePersonLike.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.imageButtonFavouritePersonLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jsonObjectRequest = new JSONObject();
                    try {
                        jsonObjectRequest.put(ProjectConstants.BOOKMARKID, items.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.REMOVE_FAVORITES_URL).newBuilder();
                    if (DetectConnection.checkInternetConnection(context)) {
                        new ProjectConstants.getDataFromServer(jsonObjectRequest, new RemoveFromFavorite(items.getId()), context).execute(urlBuilder.build().toString(), token);
                        holder.imageButtonFavouritePerson.setVisibility(View.VISIBLE);
                        holder.imageButtonFavouritePersonLike.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            holder.imageButtonFavouritePersonLike.setVisibility(View.VISIBLE);
            holder.imageButtonFavouritePersonLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jsonObjectRequest = new JSONObject();
                    try {
                        jsonObjectRequest.put(ProjectConstants.BOOKMARKID, items.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.REMOVE_FAVORITES_URL).newBuilder();
                    if (DetectConnection.checkInternetConnection(context)) {
                        new ProjectConstants.getDataFromServer(jsonObjectRequest, new RemoveFromFavorite(items.getId()), context).execute(urlBuilder.build().toString(), token);
                        holder.imageButtonFavouritePerson.setVisibility(View.VISIBLE);
                        holder.imageButtonFavouritePersonLike.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            holder.imageButtonFavouritePerson.setVisibility(View.GONE);
        }
    }

    public class AddToFavorite implements CallBackFunction {

        int bookmark_id;

        public AddToFavorite(int bookmark_id) {
            this.bookmark_id = bookmark_id;
        }

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
//                Log.e("resp : ", response.toString());
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                //Log.e("resp : ", result);

                if (result != null) {

                    try {

                        final JSONObject jsonHome = new JSONObject(result);

                        final Boolean auth = jsonHome.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonHome.getString(ProjectConstants.MESSAGE);

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (auth) {
                                    is_favorite.put(bookmark_id, true);
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    ((MainActivity) context).updateData(false,bookmark_id);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        }
    }

    public class RemoveFromFavorite implements CallBackFunction {

        int bookmark_id;

        public RemoveFromFavorite(int bookmark_id) {
            this.bookmark_id = bookmark_id;
        }

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
//                Log.e("resp : ", response.toString());
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                //Log.e("resp : ", result);

                if (result != null) {

                    try {

                        final JSONObject jsonHome = new JSONObject(result);

                        final Boolean auth = jsonHome.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonHome.getString(ProjectConstants.MESSAGE);

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (auth) {
                                    is_favorite.remove(bookmark_id);
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    ((MainActivity) context).updateData(true,bookmark_id);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return isDummy ? count : userProfileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvUserName, tvUserCaste, tvUserAge, tvUserBirthPlace, tvUserMaritalStatus, tvUserHeight, tvUserEducation,tvUserId;
        ImageButton imageButtonFavouritePerson, imageButtonFavouritePersonLike;
        ImageView userImage;

        View view;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            tvUserName = view.findViewById(R.id.tv_user_name);
            tvUserCaste = view.findViewById(R.id.tv_user_caste);
            tvUserAge = view.findViewById(R.id.tv_user_age);
            tvUserId = view.findViewById(R.id.tv_user_id);
            tvUserBirthPlace = view.findViewById(R.id.tv_user_birth_place);
            tvUserMaritalStatus = view.findViewById(R.id.tv_user_marital_status);
            tvUserHeight = view.findViewById(R.id.tv_user_height);
            tvUserEducation = view.findViewById(R.id.tv_user_education);

            imageButtonFavouritePerson = view.findViewById(R.id.favourite_person);
            imageButtonFavouritePersonLike = view.findViewById(R.id.favourite_person_like);

            userImage = view.findViewById(R.id.image_user);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }

        public View getView() {
            return view;
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

}
