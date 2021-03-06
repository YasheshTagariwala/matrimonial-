package com.kloudforj.matrimonial.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.kloudforj.matrimonial.R;
import com.kloudforj.matrimonial.activities.UserEditProfileActivity;
import com.kloudforj.matrimonial.utils.CallBackFunction;
import com.kloudforj.matrimonial.utils.DetectConnection;
import com.kloudforj.matrimonial.utils.ProjectConstants;
import com.kloudforj.matrimonial.utils.Tools;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
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

    private String[] items;
    private int[] items_id;

    int numberOfImages;

    public interface OnItemClickListener {
        void onItemClick(View view, String obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridBasic(Context context, int numberOfImages) {
        this.numberOfImages = numberOfImages;
        ctx = context;
    }

    public AdapterGridBasic(Context context, String[] items, int[] items_id) {
        this.items = items;
        this.items_id = items_id;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public View lyt_parent;
        public ImageButton imageButtonTimeline;
        public ImageButton imageButtonRemove;
        public RelativeLayout relativeLayoutDisable;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            imageButtonTimeline = v.findViewById(R.id.timeline_image);
            imageButtonRemove = v.findViewById(R.id.remove_image);
            relativeLayoutDisable = v.findViewById(R.id.rl_igi_disable);
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
            SharedPreferences globalSP;
            globalSP = ctx.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
            final String token = globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING);
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            if (items.length > position && items[position] != null) {
                Tools.displayImageOriginal(ctx, view.imageButtonTimeline, items[position]);
                view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(view, items[position], position);
                        }
                    }
                });
                /** To Display Disable Image **/
//                view.relativeLayoutDisable.setVisibility(View.VISIBLE);
            } else {
                view.imageButtonTimeline.setImageResource(R.drawable.add_image_icon);
                view.imageButtonRemove.setVisibility(View.GONE);
                view.imageButtonTimeline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainHolder = view;
                        ((UserEditProfileActivity) ctx).requestPermission();
                    }
                });
            }
            view.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject jsonObjectRequest = new JSONObject();
                    try {
                        jsonObjectRequest.put(ProjectConstants.IMAGE_ID, items_id[position]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.DELETE_IMAGE_URL).newBuilder();
                    if (DetectConnection.checkInternetConnection(ctx)) {
                        new ProjectConstants.getDataFromServer(jsonObjectRequest, new DeleteProfilePic(), ctx).execute(urlBuilder.build().toString(), token);
                    } else {
                        Toast.makeText(ctx, ctx.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                    view.imageButtonTimeline.setImageResource(R.drawable.add_image_icon);
                    view.imageButtonRemove.setVisibility(View.GONE);
                    view.imageButtonTimeline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mainHolder = view;
                            ((UserEditProfileActivity) ctx).requestPermission();
                        }
                    });
                }
            });
        }
    }

    public class DeleteProfilePic implements CallBackFunction {

        @Override
        public void getResponseFromServer(Response response) throws IOException {
            if (!response.isSuccessful()) {
                //Log.e("resp1 : ", response.toString());
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, ctx.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
                throw new IOException("Unexpected code " + response);
            } else {

                String result = response.body().string(); // response is converted to string
                //Log.e("resp : ", result);

                if (result != null) {

                    try {

                        final JSONObject jsonHome = new JSONObject(result);

                        final Boolean auth = jsonHome.getBoolean(ProjectConstants.AUTH);
                        final String message = jsonHome.getString(ProjectConstants.MESSAGE);

                        ((Activity) ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (auth) {
                                    Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        ((Activity) ctx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, ctx.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    ((Activity) ctx).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ctx, ctx.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    public void getImageData() {
//        Log.e("where", "getImageData");
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

    private void getImageFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) ctx).startActivityForResult(pickPhoto, 1);
    }

    private void getImageFromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((Activity) ctx).startActivityForResult(takePicture, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bitmap image = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    mainHolder.imageButtonTimeline.setImageBitmap(image);
                    mainHolder.imageButtonRemove.setVisibility(View.VISIBLE);
                    mainHolder.imageButtonTimeline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });

                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"CelebrationMatchMakers");
                    if(!mediaStorageDir.exists()){
                        if(!mediaStorageDir.mkdirs()){
                            Log.e("Error","Cannot Create Directory");
                        }
                    }

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(mediaFile);
                        image.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LayoutInflater inflater = LayoutInflater.from(ctx);
                    final View alertImageUpload = inflater.inflate(R.layout.layout_loading, null);
                    ProgressBar mImageUploadProgressbar = alertImageUpload.findViewById(R.id.pb_loading);
                    if (mImageUploadProgressbar != null) {
                        mImageUploadProgressbar.getIndeterminateDrawable().setColorFilter(
                                ContextCompat.getColor(ctx, R.color.colorAccent),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                    }

                    final AlertDialog.Builder builderInageUpload = new AlertDialog.Builder(ctx);
                    builderInageUpload.setTitle("Uploading");
                    builderInageUpload.setView(alertImageUpload);
                    builderInageUpload.setCancelable(false);

                    final AlertDialog alertDialogImageUpload = builderInageUpload.create();
                    alertDialogImageUpload.show();

                    Call requestCall;
                    OkHttpClient client = new OkHttpClient();
                    Request request;
                    SharedPreferences globalSP = ctx.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
                    RequestBody req = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("user_id", String.valueOf(globalSP.getInt(ProjectConstants.USERID, 0)))
                            .addFormDataPart("profile", mediaFile.getPath(), RequestBody.create(MediaType.parse("image/jpg"), mediaFile)).build();

                    request = new Request.Builder()
                            .url(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.IMAGE_UPLOAD_URL)
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
                            alertDialogImageUpload.dismiss();
                            ((Activity) ctx).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ctx, "Error Uploading Image", Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String result = response.body().string();
                                Log.e("Response : ", result);
                                final JSONObject jsonLogin = new JSONObject(result);
                                final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                                final String message = jsonLogin.getString(ProjectConstants.MESSAGE);
                                if (auth) {
                                    ((Activity) ctx).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialogImageUpload.dismiss();
                                            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
                                            SharedPreferences globalSP;
                                            globalSP = ctx.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = globalSP.edit();
                                            try {
                                                editor.putString(ProjectConstants.BASE_IMAGE, jsonLogin.getString(ProjectConstants.BASE_IMAGE));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            editor.apply();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                alertDialogImageUpload.dismiss();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;

            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    mainHolder.imageButtonTimeline.setImageURI(selectedImage);
                    mainHolder.imageButtonRemove.setVisibility(View.VISIBLE);
                    mainHolder.imageButtonTimeline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor1 = ctx.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor1.moveToFirst();

                    int columnIndex = cursor1.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor1.getString(columnIndex);
                    cursor1.close();

                    LayoutInflater inflater = LayoutInflater.from(ctx);
                    final View alertImageUpload = inflater.inflate(R.layout.layout_loading, null);
                    ProgressBar mImageUploadProgressbar = alertImageUpload.findViewById(R.id.pb_loading);
                    if (mImageUploadProgressbar != null) {
                        mImageUploadProgressbar.getIndeterminateDrawable().setColorFilter(
                                ContextCompat.getColor(ctx, R.color.colorAccent),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                    }

                    final AlertDialog.Builder builderInageUpload = new AlertDialog.Builder(ctx);
                    builderInageUpload.setTitle("Uploading");
                    builderInageUpload.setView(alertImageUpload);
                    builderInageUpload.setCancelable(false);

                    final AlertDialog alertDialogImageUpload = builderInageUpload.create();
                    alertDialogImageUpload.show();

                    Call requestCall;
                    OkHttpClient client = new OkHttpClient();
                    Request request;
                    SharedPreferences globalSP = ctx.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
                    RequestBody req = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("user_id", String.valueOf(globalSP.getInt(ProjectConstants.USERID, 0)))
                            .addFormDataPart("profile", picturePath, RequestBody.create(MediaType.parse("image/jpg"), new File(picturePath))).build();

                    request = new Request.Builder()
                            .url(ProjectConstants.BASE_URL + ProjectConstants.VERSION_0 + ProjectConstants.USER + ProjectConstants.IMAGE_UPLOAD_URL)
                            .post(req)
                            .header(ProjectConstants.APITOKEN, globalSP.getString(ProjectConstants.TOKEN, ProjectConstants.EMPTY_STRING))
                            .build();

                    //Log.e("Request : ", request.toString());

                    requestCall = client.newCall(request);
                    requestCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            /*Log.e("Error : ", "123");
                            Log.e("Error : ", e.getMessage());*/
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String result = response.body().string();
                                //Log.e("Response : ", result);
                                final JSONObject jsonLogin = new JSONObject(result);
                                final Boolean auth = jsonLogin.getBoolean(ProjectConstants.AUTH);
                                final String message = jsonLogin.getString(ProjectConstants.MESSAGE);
                                if (auth) {
                                    ((Activity) ctx).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialogImageUpload.dismiss();
                                            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
                                            SharedPreferences globalSP;
                                            globalSP = ctx.getSharedPreferences(ProjectConstants.PROJECTBASEPREFERENCE, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = globalSP.edit();
                                            try {
                                                editor.putString(ProjectConstants.BASE_IMAGE, jsonLogin.getString(ProjectConstants.BASE_IMAGE));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            editor.apply();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}