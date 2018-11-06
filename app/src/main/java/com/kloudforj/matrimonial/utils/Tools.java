package com.kloudforj.matrimonial.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

import static android.content.Context.MODE_PRIVATE;

public class Tools {

    public static void displayImageOriginal(Context ctx, ImageView img, @DrawableRes int drawable) {
        try {
            Glide.with(ctx).load(drawable)
                    /*.crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)*/
                    .into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayImageOriginal(Context ctx, ImageView img, String image_name) {
        try {
            SharedPreferences globalSP;
            globalSP = ctx.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
            String token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);

            RequestOptions ro = new RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .override(256, 140)
                    .fitCenter();

            GlideUrl url = new GlideUrl(ProjectConstants.BASE_URL + ProjectConstants.IMAGE_GET_URL + "/" + image_name,
                    new LazyHeaders.Builder().addHeader(ProjectConstants.APITOKEN, token).build());
            GlideApp.with(ctx).load(url).apply(ro)
                    /*.crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)*/
                    .into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static void nestedScrollTo(final NestedScrollView nested, final View targetView) {
        nested.post(new Runnable() {
            @Override
            public void run() {
                nested.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    public static boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }
}
