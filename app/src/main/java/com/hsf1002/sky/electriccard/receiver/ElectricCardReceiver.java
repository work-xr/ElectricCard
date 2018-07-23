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
import com.hsf1002.sky.electriccard.utils.SaveFileUtils;

import static android.content.Intent.ACTION_SHUTDOWN;
import static com.hsf1002.sky.electriccard.entity.ProviderInfo.setProviderInfo;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readProviderNameStatus;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readSimcardActivated;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readSimcardDateTime;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.updateDurationFromReceiver;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.writeNetworkConnectedTime;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.writeProviderNameStatus;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.writeSimcardDateTime;

/**
 * Created by hefeng on 18-7-17.
 */

public class ElectricCardReceiver extends BroadcastReceiver {
    private static final String TAG = "ElectricCardReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean isSimcardActivated = readSimcardActivated();
        boolean isProviderNameRead = readProviderNameStatus();

        Log.d(TAG, "onReceive: isSimcardActivated = " + isSimcardActivated);
        Log.d(TAG, "onReceive: isProviderNameRead = " + isProviderNameRead);

        if (action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Log.d(TAG, "onReceive: ACTION_BOOT_COMPLETED.................................." );
            /* 还要考虑一种拔电池的情况, 该值不为空,说明没有走正常关机流程进行清空, 但是无法防止 updateDurationFromReceiver 没有更新的问题 */
            if (isProviderNameRead)
            {
                writeProviderNameStatus(false);
            }
            ElectricCardService.setServiceAlarm(context.getApplicationContext(), !isSimcardActivated);
        }

        /* 每次断网 CONNECTIVITY_ACTION 会收到两次, 相隔 1s, 而且联网和断网的广播不是成对出现, 联网的广播可能连续出现两次 */
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            Log.d(TAG, "onReceive: CONNECTIVITY_ACTION .");

            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (isSimcardActivated)
            {
                return;
            }

            if (!isProviderNameRead)
            {
                setProviderInfo();
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
                    Log.d(TAG, "mobile disconnected.................................................. sNetworkConnectedStartTime 1 = " + readNetworkConnectedTime());
                    /* service一分钟跑一次, 大概率会在service中停止 */
                    //if (/*readNetworkConnectedTime() != 0*/已经联网开始时间已经在前面写入  /*!readSimcardActivated()*/ 激活条件在上面已经判断)
                    /* 断网广播会连续出现两次 */
                    if (readNetworkConnectedTime() != 0)
                    {
                        Log.d(TAG, "mobile disconnected.................................................. sNetworkConnectedStartTime 2 = " + readNetworkConnectedTime());
                        updateDurationFromReceiver();

                        /* 断网之后, 将联网开始时间初始化为0 */
                        writeNetworkConnectedTime(0L);
                    }
                }
            }
        }
        
        if (action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        {
			Log.d(TAG, "onReceive: SMS_RECEIVED_ACTION .");

			/* 如果电子保卡已经激活, 开始截取短信, 解析是否在自运营商 */
            if (isSimcardActivated && readSimcardDateTime() == null)
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
                            /* 将电子保卡激活标志写入文件 */
                            SaveFileUtils.getInstance().writeElectricCardActivated(readSimcardActivated(), readSimcardDateTime());
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
            Log.d(TAG, "onReceive: ACTION_SHUTDOWN .................................");

            /* 关机的时候要更新持续时长和累积时长, 此处要屏蔽, 会和断网时候重复, 导致统计2次 */
            //if (readNetworkConnectedTime() != 0)
            //{
                //updateDurationFromReceiver();
            //}

            /* 关机的时候,清空读取的运营商信息 */
            writeProviderNameStatus(false);
        }
    }
}
