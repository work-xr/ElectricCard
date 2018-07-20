package com.hsf1002.sky.electriccard.utils;

import android.util.Log;

import com.hsf1002.sky.electriccard.entity.ProviderInfo;

import static com.hsf1002.sky.electriccard.utils.Constant.ACCUMULATED_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.CONSISTENT_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.ELECTRIC_CARD_ACTIVATED_DATETIME;
import static com.hsf1002.sky.electriccard.utils.Constant.ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME;
import static com.hsf1002.sky.electriccard.utils.Constant.ELECTRIC_CARD_ACTIVATED_STATE;
import static com.hsf1002.sky.electriccard.utils.Constant.PROVIDER_NAME_SET_STATE;

/**
 * Created by hefeng on 18-7-17.
 */

public class NVUtils {

    public static final String TAG = "NVUtils";

    /* 一次网络连接成功的时间 */
    private static long sNetworkConnectedStartTime = 0;
    /* 一次网络连接持续的时间, 累加后变成 一次开机网络连接持续的时间 */
    private static long sNetworkConnectedTimeDuration = 0;


    /* 读取累积联网时长 */
    public static long readSimcardConsistentOnlineDuration() {
        return SharedPreUtils.getInstance().getLong(CONSISTENT_DURATION, 0L);
    }

    /* 写入累积联网时长, 重新开机也会累加 */
    public static void writeSimcardConsistentOnlineDuration(long offsetDuration) {
        long lastTotalDuration = readSimcardConsistentOnlineDuration();

        SharedPreUtils.getInstance().putLong(CONSISTENT_DURATION, lastTotalDuration + offsetDuration);

        Log.d(TAG, "writeSimcardConsistentOnlineDuration: current total duration = " + (lastTotalDuration + offsetDuration)/1000 + " seconds");
    }

    /* 读取持续联网时长 */
    public static long readSimcardAccumulatedOnlineDuration() {
        return SharedPreUtils.getInstance().getLong(ACCUMULATED_DURATION, 0L);
    }

    /* 把每次网络连接持续的时长累加, 成为 本次开机累计的联网时长, 只记录时间最长的那次联网开机 */
    public static void writeSimcardAccumulatedOnlineDuration(long offsetDuration) {
        long lastOnceDuration = readSimcardAccumulatedOnlineDuration();

        sNetworkConnectedTimeDuration += offsetDuration;

        if (sNetworkConnectedTimeDuration > lastOnceDuration) {
            SharedPreUtils.getInstance().putLong(ACCUMULATED_DURATION, sNetworkConnectedTimeDuration);
        }

        Log.d(TAG, "writeSimcardAccumulatedOnlineDuration: current once duration = " + sNetworkConnectedTimeDuration/1000 + " seconds");
    }

    public static boolean readSimcardActivated() {
        return SharedPreUtils.getInstance().getBoolean(ELECTRIC_CARD_ACTIVATED_STATE, false);
    }

    public static void writeSimcardActivated(boolean activated) {
        SharedPreUtils.getInstance().putBoolean(ELECTRIC_CARD_ACTIVATED_STATE, activated);
    }

    public static String readSimcardDateTime() {
        return SharedPreUtils.getInstance().getString(ELECTRIC_CARD_ACTIVATED_DATETIME, ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME);
    }

    public static void writeSimcardDateTime(String datatime) {
        SharedPreUtils.getInstance().putString(ELECTRIC_CARD_ACTIVATED_DATETIME, datatime);
    }

    public static long readNetworkConnectedTime() {
        return sNetworkConnectedStartTime;
    }

    public static void writeNetworkConnectedTime(long networkConnectedTime) {
        sNetworkConnectedStartTime = networkConnectedTime;
    }

    /* 只有断开网络连接的时候,才写数据, 不做是否激活的判断 */
    public static void updateDurationFromReceiver() {
        long currentTime = System.currentTimeMillis();
        long startTime = readNetworkConnectedTime();
        long offset = currentTime - startTime;

        assert (offset > 0);
        Log.d(TAG, "updateDurationFromReceiver: curentTime = " + currentTime + ", startTime = " + startTime);
        Log.d(TAG, "updateDurationFromReceiver: once online duration = " + offset / 1000 + " seconds");

        writeNetworkConnectedTime(currentTime);
        writeSimcardAccumulatedOnlineDuration(offset);
        writeSimcardConsistentOnlineDuration(offset);
    }

    /* service只读, 仅当满足激活条件时才写, 此函数和setsNetworkConnectedTime是同时调用的 */
    public static void updateDurationFromService() {
        long curentTime = System.currentTimeMillis();
        long startTime = readNetworkConnectedTime();    // 每次联网成功都会更新,所以service 只统计截止到当前时刻一次联网的时长
        long offset = curentTime - startTime;

        assert (offset > 0);

        Log.d(TAG, "updateDurationFromService: curentTime = " + curentTime + " , startTime = " + startTime);
        Log.d(TAG, "updateDurationFromService: once online duration = " + offset/1000 + " seconds");
        long lastAccumulatedDuration = readSimcardAccumulatedOnlineDuration();
        long lastConsistentDuration = readSimcardConsistentOnlineDuration();
        Log.d(TAG, "updateDurationFromService: preset onceDuration = " + ProviderInfo.getInstance().getAccumulatedDuration()/1000 + " seconds");
        Log.d(TAG, "updateDurationFromService: preset totalDuration = " + ProviderInfo.getInstance().getConsistentDuration()/1000 + " seconds");
        Log.d(TAG, "updateDurationFromService: lastOnceDuration = " + lastAccumulatedDuration/1000 + " seconds");
        Log.d(TAG, "updateDurationFromService: lastTotalDuration = " + lastConsistentDuration/1000 + " seconds");

        if (lastAccumulatedDuration + offset > ProviderInfo.getInstance().getAccumulatedDuration()) {
            if (lastConsistentDuration + offset > ProviderInfo.getInstance().getConsistentDuration()) {
                writeNetworkConnectedTime(curentTime);
                writeSimcardAccumulatedOnlineDuration(offset);
                writeSimcardConsistentOnlineDuration(offset);
                Log.d(TAG, "updateDurationFromService: write simcard activated flag true........................................................");
                writeSimcardActivated(true);
            }
        }
    }

    /* 是否已经读取过了运营商的信息 */
    public static boolean readProviderInfo() {
        return SharedPreUtils.getInstance().getBoolean(PROVIDER_NAME_SET_STATE, false);
    }

    public static void writeProviderInfo(boolean setted) {
        SharedPreUtils.getInstance().putBoolean(PROVIDER_NAME_SET_STATE, setted);
    }

    public static void clearActivatedState()
    {
        /* 读取运营商信息清空 */
        writeProviderInfo(false);

        /* 开始的联网时间清空 */
        writeNetworkConnectedTime(0L);

        /* 持续联网时间清空 */
        SharedPreUtils.getInstance().putLong(CONSISTENT_DURATION, 0L);

        /* 累积联网时间清空 */
        SharedPreUtils.getInstance().putLong(ACCUMULATED_DURATION, 0L);

        /* 电子保卡激活标志位清空 */
        writeSimcardActivated(false);

        /* 收到的第一条运营商信息的时间清空 */
        writeSimcardDateTime(ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME);
    }
}
