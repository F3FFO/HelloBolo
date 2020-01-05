package com.f3ffo.hellobolo.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.NotNull;

public class CheckInternet {

    public static boolean isNetworkAvailable(@NotNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}