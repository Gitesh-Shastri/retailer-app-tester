package com.medicento.retailerappmedi.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;

public class MedicentoUtils {

    public static void showVolleyError(VolleyError error) {
        try {
            if (error == null || error.networkResponse == null) {
                Log.d("No Response", "showVolleyError: ");
                return;
            }
            String body;
            try {
                body = new String(error.networkResponse.data, "UTF-8");
                logLargeString(body);
            } catch (UnsupportedEncodingException e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logLargeString(String str) {
        if(str.length() > 3000) {
            Log.e("error_message", str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.e("error_message", str); // continuation
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

    public static boolean isStringEquals(String str, String str_to_compare) {
        return str.toLowerCase().equals(str_to_compare.toLowerCase());
    }

    public static String getDeviceManufacture() {
        try {
            String manufacturer = Build.MANUFACTURER;
            return capitalize(manufacturer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDeviceModel() {
        try {
            String model = Build.MODEL;
            return capitalize(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String capitalize(String s) {
        try {
            if (s == null || s.length() == 0) {
                return "";
            }
            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                return s;
            } else {
                return Character.toUpperCase(first) + s.substring(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Activity activity) {
        try {
            ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
