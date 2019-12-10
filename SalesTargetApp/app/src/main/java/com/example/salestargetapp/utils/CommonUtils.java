package com.example.salestargetapp.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;

public class CommonUtils {

    public static void showVolleyError(VolleyError error) {
        try {
            if (error == null || error.networkResponse == null) {
                Log.e("error_message", "no response: " );
                return;
            }
            String body;
            try {
                body = new String(error.networkResponse.data, "UTF-8");
                Log.e("error_message", "showVolleyError: " + body);
            } catch (UnsupportedEncodingException e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean amIConnect(Activity activity) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
