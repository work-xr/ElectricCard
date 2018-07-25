package com.hsf1002.sky.electriccard.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.electriccard.entity.ResultInfo;
import com.hsf1002.sky.electriccard.receiver.ElectricCardReceiver;
import com.hsf1002.sky.electriccard.utils.ConnectivityUtils;
import com.hsf1002.sky.electriccard.utils.SaveFileUtils;

import static com.hsf1002.sky.electriccard.entity.ProviderInfo.setProviderInfo;
import static com.hsf1002.sky.electriccard.utils.Constant.SERVICE_STARTUP_INTERVAL;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readProviderName;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.updateDurationFromService;

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
            String providerName = readProviderName();
            long connectedStartTime = readNetworkConnectedTime();

            if (TextUtils.isEmpty(providerName))
            {
                setProviderInfo();
            }

            Log.d(TAG, "readSimCardOnlineDuration: connectedStartTime = " + connectedStartTime);

            /* 开机第一次connectedStartTime = 0, 断网后再次重置为 0, 只在联网的时候初始化这个值 */
            if (connectedStartTime == 0)
            {
                return;
            }

            /* readSimcardActivatedFromFile must be call again*/
            ResultInfo resultInfo = SaveFileUtils.getInstance().readElectricCardActivated();
            boolean isSimcardActivated = resultInfo.getFlag();
            if (!isSimcardActivated)
            {
                Log.d(TAG, "readSimCardOnlineDuration: readSimcardActivatedFromFile = false, update duration from service again ");
                updateDurationFromService();
            }
            else
            {
                Log.d(TAG, "readSimCardOnlineDuration: readSimcardActivatedFromFile = true..................................... ");
            }
        }
        else
        {
            Log.d(TAG, "readSimCardOnlineDuration: network does not connect.................................................... ");
        }
        /* 如果已经置激活标志位, 则开始读取短信内容, 不在这里读了,因为短信时间不好获取, 在广播那里截取判断 */
        /*if (readSimcardActivatedFromFile())
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
            Log.d(TAG, "setServiceAlarm: turn on start repeating service.........................");
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
}
