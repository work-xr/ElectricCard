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

import com.hsf1002.sky.electriccard.entity.ProviderInfo;
import com.hsf1002.sky.electriccard.service.ElectricCardService;
import com.hsf1002.sky.electriccard.utils.DateTimeUtils;
import com.hsf1002.sky.electriccard.utils.NVUtils;

import static android.content.Intent.ACTION_SHUTDOWN;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readProviderInfo;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readSimcardActivated;
import static com.hsf1002.sky.electriccard.utils.NVUtils.readSimcardDateTime;
import static com.hsf1002.sky.electriccard.utils.NVUtils.updateDurationFromReceiver;
import static com.hsf1002.sky.electriccard.utils.NVUtils.writeNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.NVUtils.writeSimcardDateTime;
import static com.hsf1002.sky.electriccard.utils.ProviderUtils.setOperatorInfo;

/**
 * Created by hefeng on 18-7-17.
 */

public class ElectricCardReceiver extends BroadcastReceiver {
    private static final String TAG = "ElectricCardReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Log.d(TAG, "onReceive: boot completed. NVUtils.readSimcardActivated() = " + readSimcardActivated());

            ElectricCardService.setServiceAlarm(context.getApplicationContext(), !readSimcardActivated());
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
                return;
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
                        Log.d(TAG,  "mobile connected................................................... startTime = " + readNetworkConnectedTime());
                        /* 放在这个地方太晚了, service都跑了100ms了, 如果初始时间不是0, 说明已经在service中调用过了 */
                        writeNetworkConnectedTime(System.currentTimeMillis());
                    }
                }
                else
                {
                    Log.d(TAG, "mobile disconnected.................................................. sNetworkConnectedStartTime = " + readNetworkConnectedTime());
                    /* service一分钟跑一次, 大概率会在service中停止 */
                    //if (/*readNetworkConnectedTime() != 0*/已经联网开始时间已经在前面写入  /*!readSimcardActivated()*/ 激活条件在上面已经判断)
                    {
                        updateDurationFromReceiver();
                    }
                }
            }
        }
        
        if (action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        {
			Log.d(TAG, "onReceive: SMS_RECEIVED_ACTION .");

			/* 如果电子保卡已经激活, 开始截取短信, 解析是否在自运营商 */
            if (readSimcardActivated() && readSimcardDateTime() == null)
            {
                Bundle bundle = intent.getExtras();
                SmsMessage msg = null;

                if (null != bundle) {
                    Object[] smsObj = (Object[]) bundle.get("pdus");

                    for (Object object : smsObj) {
                        msg = SmsMessage.createFromPdu((byte[]) object);
                        String address = msg.getOriginatingAddress();
                        String body = msg.getMessageBody();
                        String center = msg.getServiceCenterAddress();
                        String receiveTime = DateTimeUtils.getFormatCurrentTime();

                        Log.d(TAG, "onReceive: address = " +  address);
                        Log.d(TAG, "onReceive: body = " + body);
                        Log.d(TAG, "onReceive: center = " + center);
                        Log.d(TAG, "onReceive: receiveTime = " + receiveTime);

                        if (ProviderInfo.getInstance().isFromProviderSmsCenter(address)) {
                            Log.d(TAG, "onReceive: get the provider msg success.........................................................");
                            /* 保存运营商信息的时间 */
                            writeSimcardDateTime(receiveTime);
                            /* 停止定时服务 */
                            ElectricCardService.setServiceAlarm(context.getApplicationContext(), false);
                            break;
                        }
                    }
                }
            }
            else
            {
                Log.d(TAG, "onReceive: get a msg, but the electric card has not activated .................................................");
            }
		}
/*
        if (action.equals(Intents.DATA_SMS_RECEIVED_ACTION))
        {
            Log.d(TAG, "onReceive: DATA_SMS_RECEIVED_ACTION .");
        }
*/
        /* 先接收到关机广播, 再接收到断网广播, 差了大约3s */
        if (action.equals(ACTION_SHUTDOWN))
        {
            Log.d(TAG, "onReceive: ACTION_SHUTDOWN .  startTime = " + readNetworkConnectedTime());

            /* 关机的时候要更新持续时长和累积时长 */
            if (readNetworkConnectedTime() != 0)
            {
                updateDurationFromReceiver();
            }
        }
    }
}
