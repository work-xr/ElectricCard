package com.hsf1002.sky.electriccard.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.hsf1002.sky.electriccard.utils.NVutils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hsf1002.sky.electriccard.utils.Constant.SERVICE_STARTUP_INTERVAL;

/**
 * Created by hefeng on 18-7-17.
 */

public class ElectricCardService extends Service {
    private static final String TAG = "ElectricCardService";
    private static int startServiceInterval = SERVICE_STARTUP_INTERVAL;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        readSimCardOnlineDuration();

        return super.onStartCommand(intent, flags, startId);
    }

    private void readSimCardOnlineDuration()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();

            if (type == ConnectivityManager.TYPE_MOBILE) {
                Log.d(TAG, "readSimCardOnlineDuration: TYPE_MOBILE");

                NVutils.updateDurationFromService();
            }
        }

        if (NVutils.getSimcardActivated())
        {
            Log.d(TAG, "readSimCardOnlineDuration: SimcardActivated.............");

            getSmsFromPhone();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setServiceAlarm(Context context, boolean isOn)
    {
        Intent intent = new Intent(context, ElectricCardService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if  (isOn)
        {
            manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), startServiceInterval, pi);
        }
        else
        {
            manager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context)
    {
        Intent intent = new Intent(context, ElectricCardService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }

    public void stopGpsService()
    {
        stopSelf();
    }

    private void getSmsFromPhone() {
        ContentResolver cr = getContentResolver();
        Uri SMS_INBOX = Uri.parse("content://sms/");
        String[] projection = new String[]{"body"};//"_id", "address", "person",, "date", "type
        String where = " address = '1066321332' AND date >  " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");

        if (null == cur) {
            return;
        }

        if (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));
            //这里我是要获取自己短信服务号码中的验证码~~
            Pattern pattern = Pattern.compile(" [a-zA-Z0-9]{10}");
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                String res = matcher.group().substring(1, 11);
            }
        }

        if (true)
        {
            // write data-time to NV
        }
    }
}
