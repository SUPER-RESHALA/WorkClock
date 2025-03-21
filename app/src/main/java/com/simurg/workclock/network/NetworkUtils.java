package com.simurg.workclock.network;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.simurg.workclock.log.FileLogger;

public class NetworkUtils {

    public static boolean isNetworkConnected(Context context) {
        FileLogger.log("isNetworkConnected", "NetworkCheck called");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            // Получаем информацию о подключении
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}