package com.hsf1002.sky.electriccard.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.hsf1002.sky.electriccard.entity.ProviderInfo;
import com.hsf1002.sky.electriccard.receiver.ElectricCardReceiver;
import com.hsf1002.sky.electriccard.utils.ConnectivityUtils;

import static com.hsf1002.sky.electriccard.entity.ProviderInfo.setProviderInfo;
import static com.hsf1002.sky.electriccard.utils.Constant.SERVICE_STARTUP_INTERVAL;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readProviderNameStatus;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readSimcardActivated;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.updateDurationFromService;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.writeNetworkConnectedTime;

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
        if (ConnectivityUtils.isNetworkConnected()) {
            boolean isProviderNameRead = readProviderNameStatus();
            long connectedStartTime = readNetworkConnectedTime();

            if (!isProviderNameRead)
            {
                setProviderInfo();
            }

            Log.d(TAG, "readSimCardOnlineDuration: connectedStartTime = " + connectedStartTime);

            /* 开机第一次connectedStartTime = 0, 断网后再次重置为 0 */
            if (connectedStartTime == 0)
            {
                writeNetworkConnectedTime(System.currentTimeMillis());
            }

            /* readSimcardActivated must be call again*/
            if (!readSimcardActivated())
            {
                Log.d(TAG, "readSimCardOnlineDuration: readSimcardActivated = false, update duration from service again ");
                updateDurationFromService();
            }
            else
            {
                Log.d(TAG, "readSimCardOnlineDuration: readSimcardActivated = true..................................... ");
            }
        }
        /* 如果已经置激活标志位, 则开始读取短信内容, 不在这里读了,因为短信时间不好获取, 在广播那里判断 */
        /*if (readSimcardActivated())
        {
            getSmsFromPhone();
        }*/
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
            Log.d(TAG, "setServiceAlarm: turn on start repeating service........................");
            manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), startServiceInterval, pi);
        }
        else
        {
            Log.d(TAG, "setServiceAlarm: turn off stopped alarm service..........................");
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

    @Deprecated
    private void getSmsFromPhone() {
        //ContentResolver cr = getContentResolver();
        //Uri SMS_INBOX = Uri.parse("content://sms/");
        //String[] projection = new String[]{"body"};//"_id", "address", "person",, "date", "type
        //String where = "date >  " + (System.currentTimeMillis() - 1 * 1 * 60 * 60 * 1000);     // 查询最近一小时的信息
        Cursor cursor = null;// //cr.query(SMS_INBOX, projection, where, null, "date desc");

        try {
            cursor = getContentResolver().query(
                    Uri.parse("content://sms/inbox"),
                    new String[]{"_id", "address", "body", "date", "service_center"},
                    null, null, "date desc");
            if (cursor != null) {
                String address;
                String body;
                String date;
                String service_center;

                while (cursor.moveToNext()) {
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    body = cursor.getString(cursor.getColumnIndex("body"));
                    date = cursor.getString(cursor.getColumnIndex("date"));
                    service_center = cursor.getString(cursor.getColumnIndex("service_center"));

                    Log.d(TAG, "getSmsFromPhone: address = " + address);
                    Log.d(TAG, "getSmsFromPhone: body = " + body);
                    Log.d(TAG, "getSmsFromPhone: date = " + date);
                    Log.d(TAG, "getSmsFromPhone: service_center = " + service_center);

                    long dateInteger = Integer.valueOf(date);
                    long activatedRealTime = 0;//readElectricCardActivatedRealTime();

                    Log.d(TAG, "getSmsFromPhone: dateInteger = " + dateInteger + ", activatedRealTime = " + activatedRealTime);

                    if (ProviderInfo.getInstance().isFromProviderSmsCenter(address))
                    {
                        if ( dateInteger> activatedRealTime)
                        {
                            long offsetSeconds = (dateInteger - activatedRealTime)/1000;

                            Log.d(TAG, "getSmsFromPhone: get the provider sms success...........................................offsetSeconds = " + offsetSeconds);
                        }
                        else
                        {
                            Log.d(TAG, "getSmsFromPhone: get the provider sms failed...........................................");
                        }
                    }
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
    }
}
