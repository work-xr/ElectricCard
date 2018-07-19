package com.hsf1002.sky.electriccard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.hsf1002.sky.electriccard.service.ElectricCardService;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.ACTION_SHUTDOWN;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readProviderInfo;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readSimcardActivated;
import static com.hsf1002.sky.electriccard.utils.NVUtils.updateDurationFromReceiver;
import static com.hsf1002.sky.electriccard.utils.NVUtils.writeNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.ProviderUtils.setOperatorInfo;

/**
 * Created by hefeng on 18-7-17.
 */

public class ElectricCardReceiver extends BroadcastReceiver {
    private static final String TAG = "ElectricCardReceiver";
    private static Context mContext = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        mContext = context.getApplicationContext();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Log.d(TAG, "onReceive: boot completed. NVUtils.readSimcardActivated() = " + readSimcardActivated());

            ElectricCardService.setServiceAlarm(context.getApplicationContext(), true/* !NVUtils.readSimcardActivated()*/);
        }

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            Log.d(TAG, "onReceive: CONNECTIVITY_ACTION .");

            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            boolean isSimcardActivated = readSimcardActivated();
            boolean isOperatorSetted = readProviderInfo();

            Log.d(TAG, "onReceive: isSimcardActivated = " + isSimcardActivated + ", isOperatorSetted = " + isOperatorSetted);

            if (isSimcardActivated)
            {
                //return;
            }

            if (!isOperatorSetted)
            {
                setOperatorInfo();
            }

            if (info != null)
            {
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable())
                {
                    if (info.getType() == ConnectivityManager.TYPE_MOBILE)
                    {
                        Log.d(TAG,  "mobile connected...................................................");
                        // 放在这个地方太晚了, service都跑了100ms了
                        long connectedStartTime = readNetworkConnectedTime();

                        if (connectedStartTime == 0)
                        {
                            writeNetworkConnectedTime(System.currentTimeMillis());
                        }
                    }
                } else
                {
                    Log.d(TAG, "mobile disconnected....................................................");
                    if (!readSimcardActivated()) {
                        updateDurationFromReceiver();
                    }
                }
            }
        }
        
        if (action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        {
			Log.d(TAG, "onReceive: SMS_RECEIVED_ACTION .");
			
			Bundle bundle = intent.getExtras();
            SmsMessage msg = null;

            if (null != bundle) {
                Object[] smsObj = (Object[]) bundle.get("pdus");

                for (Object object : smsObj) {
                    msg = SmsMessage.createFromPdu((byte[]) object);
                    Date date = new Date(msg.getTimestampMillis());//时间
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String receiveTime = format.format(date);

                    Log.d(TAG, "onReceive: number:" + msg.getOriginatingAddress()
                            + "   body:" + msg.getDisplayMessageBody() + "  time:"
                            + msg.getTimestampMillis() + ", receiveTime = " + receiveTime);

                    //在这里写自己的逻辑
                    if (msg.getOriginatingAddress().equals("10086")) {
                        //TODO

                    }
                }
            }
		}
/*
        if (action.equals(Intents.DATA_SMS_RECEIVED_ACTION))
        {
            Log.d(TAG, "onReceive: DATA_SMS_RECEIVED_ACTION .");
            Bundle bundle = intent.getExtras();
            SmsMessage msg = null;

            if (null != bundle) {
                Object[] smsObj = (Object[]) bundle.get("pdus");

                for (Object object : smsObj) {
                    msg = SmsMessage.createFromPdu((byte[]) object);
                    Date date = new Date(msg.getTimestampMillis());//时间
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String receiveTime = format.format(date);

                    Log.d(TAG, "onReceive: number:" + msg.getOriginatingAddress()
                            + "   body:" + msg.getDisplayMessageBody() + "  time:"
                            + msg.getTimestampMillis() + ", receiveTime = " + receiveTime);

                    //在这里写自己的逻辑
                    if (msg.getOriginatingAddress().equals("10086")) {
                        //TODO

                    }
                }
            }

        }
*/
        if (action.equals(ACTION_SHUTDOWN))
        {
            Log.d(TAG, "onReceive: ACTION_SHUTDOWN .");
        }

        /*
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

            Log.e(TAG, "wifiState:" + wifiState);

            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
            }
        }*/
    }
}
