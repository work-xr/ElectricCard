package com.hsf1002.sky.electriccard.application;

import android.app.Application;
import android.net.Uri;

import com.hsf1002.sky.electriccard.observer.SmsObserver;

/**
 * Created by hefeng on 18-7-17.
 */

public class EletricCardApp extends Application {
    private Uri SMS_INBOX = Uri.parse("content://sms/");

    @Override
    public void onCreate() {
        super.onCreate();

        getContentResolver().registerContentObserver(SMS_INBOX, true, SmsObserver.getInstance());
    }
}
