package com.kloudforj.matrimonial.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.kloudforj.matrimonial.utils.ProjectConstants;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

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

                    //Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor1 = ctx.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor1.moveToFirst();

                    int columnIndex = cursor1.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor1.getString(columnIndex);
                    cursor1.close();
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

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor1 = ctx.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor1.moveToFirst();

                    int columnIndex = cursor1.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor1.getString(columnIndex);
                    cursor1.close();

                    Call requestCall;
                    OkHttpClient client = new OkHttpClient();
                    Request request;
                    SharedPreferences globalSP = ctx.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
                    RequestBody req = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("user_id", String.valueOf(globalSP.getInt(ProjectConstants.USERID, 0)))
                            .addFormDataPart("profile", picturePath, RequestBody.create(MediaType.parse("image/jpg"), new File(picturePath))).build();

                    request = new Request.Builder()
                            .url("http://139.59.90.129/matrimonial/public/index.php/api/v0/user/upload-profile-image")
                            .post(req)
                            .header(ProjectConstants.APITOKEN, globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING))
                            .build();

                    Log.e("Request : ", request.toString());

                    requestCall = client.newCall(request);
                    requestCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Error : ", "123");
                            Log.e("Error : ", e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.e("Resp : ", response.body().string());
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