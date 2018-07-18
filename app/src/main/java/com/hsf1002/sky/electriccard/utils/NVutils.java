package com.hsf1002.sky.electriccard.utils;

import android.util.Log;

import com.hsf1002.sky.electriccard.entity.BasicMsg;

import static com.hsf1002.sky.electriccard.utils.Constant.GSM_PHONE_ONCE_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.GSM_PHONE_TOTAL_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.IS_OPERATOR_NAME_SETED;

/**
 * Created by hefeng on 18-7-17.
 */

public class NVutils {

    public static final String TAG = "NVutils";

    /* 一次网络连接成功的时间 */
    private static long sNetworkConnectedStartTime = 0;
    /* 一次网络连接断开的时间 */
    private static long sNetworkConnectedEndTime = 0;
    /* 一次网络连接持续的时间 */
    private static long sNetworkConnectedTimeDuration = 0;
    /* 一次开机网络连接持续的时间 */
    private static long sOnceDuration = 0;

    /*public NVutils getInstance()
    {
        return Holder.instance;
    }

    private static class Holder
    {
        private static final NVutils instance = new NVutils();
    }
*/

    public static long readSimcardTotalOnlineDuration()
    {
        return 0;
    }

    public static void writeSimcardTotalOnlineDuration(long offsetDuration)
    {
        long lastTotalDuration = readSimcardTotalOnlineDuration();
        //lastOnceDuration + lastTotalDuration

        Log.d(TAG, "writeSimcardOnceOnlineDuration: current total duration = " + lastTotalDuration + offsetDuration);
    }

    public static long readSimcardOnceOnlineDuration()
    {
        return 0;
    }

    /* 把每次网络连接持续的时长累加, 成为 本次开机累计的联网时长 */
    public static void writeSimcardOnceOnlineDuration(long offsetDuration)
    {
        long lastOnceDuration = readSimcardOnceOnlineDuration();

        sNetworkConnectedTimeDuration += offsetDuration;

        if (sNetworkConnectedTimeDuration > lastOnceDuration)
        {
            //sNetworkConnectedTimeDuration
        }

        Log.d(TAG, "writeSimcardOnceOnlineDuration: current once duration = " );
    }

    public static boolean getSimcardActivated()
    {
        // 读取NV
        return false;
    }

    public static void setSimcardActivated(boolean activated)
    {

    }

    public static long getsNetworkConnectedTime() {
        return sNetworkConnectedStartTime;
    }

    public static void setsNetworkConnectedTime(long networkConnectedTime) {
        sNetworkConnectedStartTime = networkConnectedTime;
    }

    /* 只有断开网络连接的时候,才写数据, 不做是否激活的判断 */
    public static void updateDurationFromReceiver()
    {
        long curentTime = System.currentTimeMillis();
        long startTime = getsNetworkConnectedTime();

        Log.d(TAG, "updateDurationFromReceiver: curentTime = " + curentTime + ", startTime = " + startTime);
        Log.d(TAG, "updateDurationFromReceiver: once online duration = " + (curentTime - startTime)/1000 + " seconds");

        writeSimcardOnceOnlineDuration(curentTime - startTime);
        writeSimcardTotalOnlineDuration(curentTime - startTime);
    }

    /* service只读, 仅当满足激活条件时才写, 此函数和setsNetworkConnectedTime是同时调用的 */
    public static void updateDurationFromService()
    {
        long curentTime = System.currentTimeMillis();
        long startTime = getsNetworkConnectedTime();    // 每次联网成功都会更新,所以service 只统计一次联网的时长

        Log.d(TAG, "updateDurationFromService: curentTime = " + curentTime + ", startTime = " + startTime);
        Log.d(TAG, "updateDurationFromService: once online duration = " + (curentTime - startTime)/1000 + " seconds");
        long lastOnceDuration = readSimcardOnceOnlineDuration();
        long lastTotalDuration = readSimcardTotalOnlineDuration();
        Log.d(TAG, "updateDurationFromService: preset onceDuration = " + BasicMsg.getInstance().getOnceDuration());
        Log.d(TAG, "updateDurationFromService: preset totalDuration = " + BasicMsg.getInstance().getTotalDuration());
        Log.d(TAG, "updateDurationFromService: lastOnceDuration = " + lastOnceDuration);
        Log.d(TAG, "updateDurationFromService: lastTotalDuration = " + lastTotalDuration);

        if (lastOnceDuration + (curentTime - startTime) > BasicMsg.getInstance().getOnceDuration())
        {
            if (lastTotalDuration + (curentTime - startTime) > BasicMsg.getInstance().getTotalDuration())
            {
                NVutils.writeSimcardOnceOnlineDuration(curentTime - startTime);
                NVutils.writeSimcardTotalOnlineDuration(curentTime - startTime);
            }
        }
    }

    public static boolean getOperatorSetting()
    {
        return IS_OPERATOR_NAME_SETED;
    }

    public static void setOperatorSetting(boolean setted)
    {
        //return IS_OPERATOR_NAME_SETED;
    }
}
