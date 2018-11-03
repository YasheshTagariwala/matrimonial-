package com.kloudforj.matrimonial.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AdapterGridBasic extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private OriginalViewHolder mainHolder;



    int numberOfImages;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridBasic(Context context, int numberOfImages) {
        this.numberOfImages = numberOfImages;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public View lyt_parent;
        public ImageButton imageButtonTimeline;
        public ImageButton imageButtonRemove;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            imageButtonTimeline = v.findViewById(R.id.timeline_image);
            imageButtonRemove = v.findViewById(R.id.remove_image);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_image, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
//            Tools.displayImageOriginal(ctx, view.image, items.get(position));
//            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (mOnItemClickListener != null) {
//                        mOnItemClickListener.onItemClick(view, items.get(position), position);
//                    }
//                }
//            });
            view.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.imageButtonTimeline.setImageResource(R.drawable.add_image_icon);
                    view.imageButtonRemove.setVisibility(View.GONE);
                    view.imageButtonTimeline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mainHolder = view;
                            getImageData();
                        }
                    });
                }
            });
            if(position == 2){
                view.imageButtonTimeline.setImageResource(R.drawable.add_image_icon);
                view.imageButtonRemove.setVisibility(View.GONE);
                view.imageButtonTimeline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainHolder = view;
                        getImageData();
                    }
                });
            }
        }
    }

    private void getImageData(){
        Log.e("where","getImageData");
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Get New Image");

        builder.setPositiveButton("From Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getImageFromGallery();
            }
        });

        builder.setNegativeButton("From Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getImageFromCamera();
            }
        });

        builder.create();
        builder.show();
    }

    private void getImageFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity)ctx).startActivityForResult(pickPhoto , 1);
    }

    private void getImageFromCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((Activity)ctx).startActivityForResult(takePicture, 0);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mainHolder.imageButtonTimeline.setImageURI(selectedImage);
                    mainHolder.imageButtonRemove.setVisibility(View.VISIBLE);
                    mainHolder.imageButtonTimeline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    mainHolder.imageButtonTimeline.setImageURI(selectedImage);
                    mainHolder.imageButtonRemove.setVisibility(View.VISIBLE);
                    mainHolder.imageButtonTimeline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return numberOfImages;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}