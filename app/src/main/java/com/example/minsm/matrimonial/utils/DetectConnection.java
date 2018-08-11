package com.example.minsm.matrimonial.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;
import com.example.minsm.matrimonial.R;

public class DetectConnection {
    public static boolean checkInternetConnection(Context context) {
        if (context != null) {
            ConnectivityManager con_manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            return con_manager.getActiveNetworkInfo() != null
                    && con_manager.getActiveNetworkInfo().isAvailable()
                    && con_manager.getActiveNetworkInfo().isConnected();
        }
        return true;
    }

    public static void somethingWentWrongToast(Context context) {

        Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
    }
}