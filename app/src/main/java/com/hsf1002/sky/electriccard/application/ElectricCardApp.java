package com.hsf1002.sky.electriccard.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by hefeng on 18-7-17.
 */

public class ElectricCardApp extends Application {
    private static final String TAG = "ElectricCardApp";
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: ");

        sContext = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return sContext;
    }
}
