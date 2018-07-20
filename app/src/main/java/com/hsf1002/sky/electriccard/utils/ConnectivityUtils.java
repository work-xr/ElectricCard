package com.hsf1002.sky.electriccard.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hsf1002.sky.electriccard.application.ElectricCardApp;

/**
 * Created by hefeng on 18-7-20.
 */

public class ConnectivityUtils {

    public static boolean isNetworkConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) ElectricCardApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
