package com.hsf1002.sky.electriccard.observer;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hefeng on 18-7-18.
 */

public class SmsObserver extends ContentObserver{

    private Uri SMS_INBOX = Uri.parse("content://sms/");

    public static Handler smsHandler = new Handler() {
        //这里可以进行回调的操作
        //TODO
    };

    private SmsObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        //每当有新短信到来时，使用我们获取短消息的方法
        //getSmsFromPhone();
    }


    private static class Holder
    {
        private static final SmsObserver instance = new SmsObserver(smsHandler);
    }

    public static SmsObserver getInstance()
    {
        return Holder.instance;
    }
}
