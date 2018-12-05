package com.kloudforj.matrimonial.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.balysv.materialripple.MaterialRippleLayout;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.entities.UserProfileImage;
import com.kloudforj.matrimonial.utils.Tools;

import java.util.List;

public class UserImageSliderAdapter extends PagerAdapter {

    private Activity act;
    private List<UserProfileImage> items;

    private OnItemClickListener onItemClickListener;
    boolean canEdit = false;

    private interface OnItemClickListener {
        void onItemClick(View view, UserProfileImage obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // constructor
    public UserImageSliderAdapter(Activity activity, List<UserProfileImage> items) {
        this.act = activity;
        this.items = items;

    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    public UserProfileImage getItem(int pos) {
        return items.get(pos);
    }

    public void setItems(List<UserProfileImage> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setCanEdit(boolean canEdit){
        this.canEdit = canEdit;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final UserProfileImage o = items.get(position);
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_slider_image, container, false);

        RelativeLayout relativeLayoutMain = v.findViewById(R.id.relative_main);

        final ImageView image = v.findViewById(R.id.image);
        RelativeLayout relativeLayout = v.findViewById(R.id.relative_editable);
        if(canEdit){
            RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)relativeLayout.getLayoutParams();
            relativeParams.setMargins(10,10,10,10);
            relativeLayoutMain.setLayoutParams(relativeParams);
            relativeLayout.setVisibility(View.VISIBLE);
        }else{
            relativeLayout.setVisibility(View.GONE);
        }
        MaterialRippleLayout lyt_parent = v.findViewById(R.id.lyt_parent);
        Tools.displayImageOriginal(act, image, o.name);
        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, o);
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(act);
                View imgEntryView = inflater.inflate(R.layout.full_screen_image, null);
                final Dialog dialog=new Dialog(act,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                ImageView img = imgEntryView.findViewById(R.id.full_screen_image);
                img.setImageDrawable(image.getDrawable());
                dialog.setContentView(imgEntryView);
                dialog.show();
                imgEntryView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramView) {
                        dialog.cancel();
                    }
                });
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
