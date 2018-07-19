package com.hsf1002.sky.electriccard.application;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.hsf1002.sky.electriccard.observer.SmsObserver;

/**
 * Created by hefeng on 18-7-17.
 */

public class ElectricCardApp extends Application {
    private static final String TAG = "ElectricCardApp";
    private static Context sContext;
    //private Uri SMS_INBOX = Uri.parse("content://sms/");

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: ");

        sContext = getApplicationContext();
        //getContentResolver().registerContentObserver(SMS_INBOX, true, SmsObserver.getInstance());
    }

    public static Context getAppContext()
    {
        return sContext;
    }
}
