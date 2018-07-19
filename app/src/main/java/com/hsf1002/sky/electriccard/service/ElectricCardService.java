package com.hsf1002.sky.electriccard.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hsf1002.sky.electriccard.entity.ProviderInfo;
import com.hsf1002.sky.electriccard.receiver.ElectricCardReceiver;
import com.hsf1002.sky.electriccard.utils.NVUtils;

import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_MOBILE_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_TELECOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_UNICOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.SERVICE_STARTUP_INTERVAL;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readProviderInfo;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readSimcardActivated;
import static com.hsf1002.sky.electriccard.utils.NVUtils.updateDurationFromService;
import static com.hsf1002.sky.electriccard.utils.NVUtils.writeNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.ProviderUtils.setOperatorInfo;

/**
 * Created by hefeng on 18-7-17.
 */

public class ElectricCardService extends Service {
    private static final String TAG = "ElectricCardService";
    private static int startServiceInterval = SERVICE_STARTUP_INTERVAL;

    private ElectricCardReceiver electricCardReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: ");

        electricCardReceiver = new ElectricCardReceiver();
        IntentFilter filter = new IntentFilter();

        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(electricCardReceiver, filter);
    }

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
                boolean isOperatorSetted = readProviderInfo();
                long connectedStartTime = readNetworkConnectedTime();

                if (!isOperatorSetted)
                {
                    setOperatorInfo();
                }

                if (connectedStartTime == 0)
                {
                    writeNetworkConnectedTime(System.currentTimeMillis());
                }

                /* readSimcardActivated must be call again*/
                if (!readSimcardActivated())
                {
                    updateDurationFromService();
                }
            }
        }

        /* 如果已经置激活标志位, 则开始读取短信内容 */
        if (readSimcardActivated())
        {
            Log.d(TAG, "readSimCardOnlineDuration: SimcardActivated.............");

            getSmsFromPhone();
        }
    }

    private void getSmsFromPhone() {
        //ContentResolver cr = getContentResolver();
        //Uri SMS_INBOX = Uri.parse("content://sms/");
        //String[] projection = new String[]{"body"};//"_id", "address", "person",, "date", "type
        //String where = "date >  " + (System.currentTimeMillis() - 1 * 1 * 60 * 60 * 1000);     // 查询最近一小时的信息
        Cursor cursor = null;// //cr.query(SMS_INBOX, projection, where, null, "date desc");

        try {
            cursor = getContentResolver().query(
                    Uri.parse("content://sms"),
                    new String[]{"_id", "address", "body", "date"},
                    null, null, "date desc");
            if (cursor != null) {
                String address;
                String body;
                String date;
                while (cursor.moveToNext()) {
                    address = cursor.getString(cursor.getColumnIndex("address"));// 在这里获取短信信息
                    body = cursor.getString(cursor.getColumnIndex("body"));// 在这里获取短信信息
                    date = cursor.getString(cursor.getColumnIndex("date"));// 在这里获取短信信息

                    Log.d(TAG, "getSmsFromPhone: address = " + address);
                    Log.d(TAG, "getSmsFromPhone: body = " + body);
                    Log.d(TAG, "getSmsFromPhone: date = " + date);
                    //-----------------写自己的逻辑
                }
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "getSmsFromPhone: cursor = null");
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }


        if (true)
        {
            // write data-time to NV
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
        Log.d(TAG, "setServiceAlarm: isOn = " + isOn);

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

    public void stopService()
    {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(electricCardReceiver);

        super.onDestroy();
    }
}
