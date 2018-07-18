package com.hsf1002.sky.electriccard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.hsf1002.sky.electriccard.entity.BasicMsg;
import com.hsf1002.sky.electriccard.service.ElectricCardService;
import com.hsf1002.sky.electriccard.utils.NVutils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.ACTION_SHUTDOWN;
import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_MOBILE_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_TELECOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_UNICOM_NAME;

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
            Log.d(TAG, "onReceive: boot completed.");

            ElectricCardService.setServiceAlarm(context.getApplicationContext(), !NVutils.getSimcardActivated());
        }

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d(TAG, "onReceive: CONNECTIVITY_ACTION .");

            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            boolean isSimcardActivated = NVutils.getSimcardActivated();
            boolean isOperatorSetted = NVutils.getOperatorSetting();

            if (isSimcardActivated)
            {
                return;
            }

            if (!isOperatorSetted)
            {
                setOperatorInfo();
            }

            if (info != null) {
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if ( info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.d(TAG,  "mobile connected.");
                        NVutils.setsNetworkConnectedTime(System.currentTimeMillis());
                    }
                } else {
                    Log.d(TAG, "mobile disconnected.");
                    NVutils.updateDurationFromReceiver();
                }
            }
        }

        if (action.equals(SMS_RECEIVED_ACTION))
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
                    System.out.println("number:" + msg.getOriginatingAddress()
                            + "   body:" + msg.getDisplayMessageBody() + "  time:"
                            + msg.getTimestampMillis());

                    //在这里写自己的逻辑
                    if (msg.getOriginatingAddress().equals("10086")) {
                        //TODO

                    }
                }
            }

        }

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

    /*
    * 电信 46003 46005 46011 46012 46050 46051 46052 46053 46054 46055 46056 46057 46058 46059 946003 946005 946011 946012 946050 946051 946052 946053 946054 946055 946056 946057 946058 946059
    * 联通 46001 46006 46009 46010 46030 46031 46032 46033 46034 46035 46036 46037 46038 46039 946001 946006 946009 946010 946030 946031 946032 946033 946034 946035 946036 946037 946038 946039
    * 移动 46000 46002 46007 46008*/
    private void setOperatorInfo()
    {
        String providerName = "";
        TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        Log.d(TAG, "setOperatorInfo: IMSI = " + IMSI);

        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                providerName = CHINA_MOBILE_NAME;
            } else if (IMSI.startsWith("46001")  || IMSI.startsWith("46006")) {
                providerName = CHINA_UNICOM_NAME;
            } else if (IMSI.startsWith("46003")) {
                providerName = CHINA_TELECOM_NAME;
            }
            Log.d(TAG, "setOperatorInfo: providerName = " + providerName);

            BasicMsg.getInstance().setName(providerName);
            BasicMsg.getInstance().setDuration();
        } else {
            Log.d(TAG, "setOperatorInfo: providerName = null" );
        }
    }
}
