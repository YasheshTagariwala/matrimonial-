package com.kloudforj.matrimonial.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        ImageView image = v.findViewById(R.id.image);
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
