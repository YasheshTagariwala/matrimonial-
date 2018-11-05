package com.kloudforj.matrimonial.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.activities.UserProfileActivity;
import com.kloudforj.matrimonial.entities.UserProfile;
import com.kloudforj.matrimonial.utils.ProjectConstants;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {

    public Context context;
    private List<UserProfile> userProfileList;
    private String TAG = "HomeListAdapter";
    private int count = 0;
    private boolean isDummy = false;

    public HomeListAdapter(Context context, List<UserProfile> userProfileList) {
        this.context = context;
        this.userProfileList = userProfileList;
    }

    public HomeListAdapter(Context context,int count){
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
        if(!isDummy) {
            final UserProfile items = userProfileList.get(position);
            holder.tvUserName.setText(String.valueOf(items.getProfile().getFirst_name() + " " + items.getProfile().getMiddle_name() + " " + items.getProfile().getLast_name()));
            holder.tvUserCaste.setText(items.getProfile().getCaste());
            holder.tvUserAge.setText(String.valueOf(items.getProfile().getAge()));

            holder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intentUserProfile = new Intent(context, UserProfileActivity.class);
                    intentUserProfile.putExtra(ProjectConstants.USERID, items.getId());
                    context.startActivity(intentUserProfile);

                    //context.startActivity(new Intent(context,UserProfileActivity.class));
                }
            });
        }else{
            holder.tvUserName.setText("They H Name");
            holder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Toast.makeText(context, "Dummy Data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        holder.imageButtonFavouritePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageButtonFavouritePerson.setVisibility(View.GONE);
                holder.imageButtonFavouritePersonLike.setVisibility(View.VISIBLE);
            }
        });

        holder.imageButtonFavouritePersonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageButtonFavouritePerson.setVisibility(View.VISIBLE);
                holder.imageButtonFavouritePersonLike.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return isDummy ? count : userProfileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvUserName, tvUserCaste, tvUserAge;
        ImageButton imageButtonFavouritePerson,imageButtonFavouritePersonLike;

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
            imageButtonFavouritePerson = view.findViewById(R.id.favourite_person);
            imageButtonFavouritePersonLike = view.findViewById(R.id.favourite_person_like);
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
