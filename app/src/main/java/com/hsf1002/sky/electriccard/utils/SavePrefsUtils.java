package com.hsf1002.sky.electriccard.utils;

import android.util.Log;

import com.hsf1002.sky.electriccard.application.ElectricCardApp;
import com.hsf1002.sky.electriccard.entity.ProviderInfo;
import com.hsf1002.sky.electriccard.service.ElectricCardService;

import static com.hsf1002.sky.electriccard.utils.Constant.ACCUMULATED_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.CONSISTENT_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.ELECTRIC_CARD_ACTIVATED_DATETIME;
import static com.hsf1002.sky.electriccard.utils.Constant.ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME;
import static com.hsf1002.sky.electriccard.utils.Constant.ELECTRIC_CARD_ACTIVATED_STATE;
import static com.hsf1002.sky.electriccard.utils.Constant.PROVIDER_ACCUMULATED_PRESET_STATE;
import static com.hsf1002.sky.electriccard.utils.Constant.PROVIDER_CONSISTENT_PRESET_STATE;
import static com.hsf1002.sky.electriccard.utils.Constant.PROVIDER_NAME_PRESET_STATE;

/**
 * Created by hefeng on 18-7-17.
 */

public class SavePrefsUtils {

    public static final String TAG = "SavePrefsUtils";

    /* 一次网络连接成功的时间 */
    private static long sNetworkConnectedStartTime = 0;
    /* 一次网络连接持续的时间, 累加后变成 一次开机网络连接持续的时间 */
    private static long sNetworkConnectedTimeDuration = 0;


    /* 读取累积联网时长 */
    public static long readSimcardConsistentOnlineDuration() {
        return PrefsUtils.getInstance().getLong(CONSISTENT_DURATION, 0L);
    }

    /* 写入累积联网时长, 重新开机也会累加 */
    public static void writeSimcardConsistentOnlineDuration(long offsetDuration) {
        long lastTotalDuration = readSimcardConsistentOnlineDuration();

        PrefsUtils.getInstance().putLong(CONSISTENT_DURATION, lastTotalDuration + offsetDuration);

        Log.d(TAG, "writeSimcardConsistentOnlineDuration: current consistent duration updated = " + (lastTotalDuration + offsetDuration)/1000 + " seconds");
    }

    /* 读取持续联网时长 */
    public static long readSimcardAccumulatedOnlineDuration() {
        return PrefsUtils.getInstance().getLong(ACCUMULATED_DURATION, 0L);
    }

    /* 把每次网络连接持续的时长累加, 成为 本次开机累计的联网时长, 只记录时间最长的那次联网开机 */
    public static void writeSimcardAccumulatedOnlineDuration(long offsetDuration) {
        long lastOnceDuration = readSimcardAccumulatedOnlineDuration();

        sNetworkConnectedTimeDuration += offsetDuration;

        if (sNetworkConnectedTimeDuration > lastOnceDuration) {
            PrefsUtils.getInstance().putLong(ACCUMULATED_DURATION, sNetworkConnectedTimeDuration);
            Log.d(TAG, "writeSimcardAccumulatedOnlineDuration: current accumulated duration updated = " + sNetworkConnectedTimeDuration/1000 + " seconds");
        }
        else
        {
            Log.d(TAG, "writeSimcardAccumulatedOnlineDuration: current accumulated duration not update, still is = " + sNetworkConnectedTimeDuration/1000 + " seconds");
        }
    }

    public static boolean readSimcardActivated() {
        return PrefsUtils.getInstance().getBoolean(ELECTRIC_CARD_ACTIVATED_STATE, false);
    }

    public static void writeSimcardActivated(boolean activated) {
        PrefsUtils.getInstance().putBoolean(ELECTRIC_CARD_ACTIVATED_STATE, activated);
    }

    public static String readSimcardDateTime() {
        return PrefsUtils.getInstance().getString(ELECTRIC_CARD_ACTIVATED_DATETIME, ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME);
    }

    public static void writeSimcardDateTime(String datatime) {
        PrefsUtils.getInstance().putString(ELECTRIC_CARD_ACTIVATED_DATETIME, datatime);
    }

    public static long readNetworkConnectedTime() {
        return sNetworkConnectedStartTime;
    }

    public static void writeNetworkConnectedTime(long networkConnectedTime) {
        sNetworkConnectedStartTime = networkConnectedTime;
    }

    /* 是否已经读取过了运营商的信息, 每次开机重新读取一次, 关机的时候清空 */
    public static boolean readProviderNameStatus()
    {
        if ("".equals(PrefsUtils.getInstance().getString(PROVIDER_NAME_PRESET_STATE, "")))
        {
            return false;
        }
        return true;
    }

    /* */
    public static void writeProviderNameStatus(boolean status)
    {
        if (status) {
            PrefsUtils.getInstance().putString(PROVIDER_NAME_PRESET_STATE, ProviderInfo.getInstance().getName());
            PrefsUtils.getInstance().putLong(PROVIDER_ACCUMULATED_PRESET_STATE, ProviderInfo.getInstance().getAccumulatedDuration());
            PrefsUtils.getInstance().putLong(PROVIDER_CONSISTENT_PRESET_STATE, ProviderInfo.getInstance().getConsistentDuration());
        }
        else
        {
            PrefsUtils.getInstance().putString(PROVIDER_NAME_PRESET_STATE, "");
            PrefsUtils.getInstance().putLong(PROVIDER_ACCUMULATED_PRESET_STATE, 0L);
            PrefsUtils.getInstance().putLong(PROVIDER_CONSISTENT_PRESET_STATE, 0L);
        }
    }

    /* 只有断开网络连接的时候,才写数据, 不做是否激活的判断 */
    public static void updateDurationFromReceiver() {
        long currentTime = System.currentTimeMillis();
        long startTime = readNetworkConnectedTime();
        long offset = currentTime - startTime;

        Log.d(TAG, "updateDurationFromReceiver: curentTime = " + currentTime + ", startTime = " + startTime);
        Log.d(TAG, "updateDurationFromReceiver: once online duration = " + offset / 1000 + " seconds");

        //writeNetworkConnectedTime(currentTime);       // no necessary
        writeSimcardAccumulatedOnlineDuration(offset);
        writeSimcardConsistentOnlineDuration(offset);
    }

    /* service只读, 仅当满足激活条件时才写, 此函数和setsNetworkConnectedTime是同时调用的 */
    public static void updateDurationFromService() {
        long curentTime = System.currentTimeMillis();
        /* 每次联网成功都会更新,所以service 只统计截止到当前时刻一次联网的时长 */
        long startTime = readNetworkConnectedTime();
        long offset = curentTime - startTime;

        Log.d(TAG, "updateDurationFromService: curentTime = " + curentTime + " , startTime = " + startTime);
        Log.d(TAG, "updateDurationFromService: once online duration = " + offset/1000 + " seconds");
        long lastAccumulatedDuration = readSimcardAccumulatedOnlineDuration();
        long lastConsistentDuration = readSimcardConsistentOnlineDuration();
        Log.d(TAG, "updateDurationFromService: lastAccumulatedDuration = " + lastAccumulatedDuration/1000 + " seconds");
        Log.d(TAG, "updateDurationFromService: lastConsistentDuration = " + lastConsistentDuration/1000 + " seconds");

        long presetAccumulatedDuration = ProviderInfo.getInstance().getAccumulatedDuration();
        long presetConsistentDuration = ProviderInfo.getInstance().getConsistentDuration();

        try {
            if (presetAccumulatedDuration == 0 || presetConsistentDuration == 0) {
                Log.d(TAG, "updateDurationFromService: preset accumulated duration is 0...................................................");
                throw new Exception("preset accumulated duration is 0 ..........................");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (lastAccumulatedDuration + offset > presetAccumulatedDuration) {
            if (lastConsistentDuration + offset > presetConsistentDuration) {
                //writeNetworkConnectedTime(curentTime);   // no necessary
                writeSimcardAccumulatedOnlineDuration(offset);
                writeSimcardConsistentOnlineDuration(offset);
                Log.d(TAG, "updateDurationFromService: write simcard activated flag true........................................................");
                writeSimcardActivated(true);

                /* 将电子保卡激活标志写入文件 */
                SaveFileUtils.getInstance().writeElectricCardActivated(readSimcardActivated(), readSimcardDateTime());
            }
        }
    }

    public static void handleClearActivatedState()
    {
        boolean isSimcardActivated = readSimcardActivated();
        String simcardActivatedTimeStr = readSimcardDateTime();

        Log.d(TAG, "handleClearActivatedState: isSimcardActivated = " + isSimcardActivated + ", simcardActivatedTimeStr = " + simcardActivatedTimeStr);
        
        if ( isSimcardActivated && !simcardActivatedTimeStr.equals(""))
        {
            Log.d(TAG, "handleClearActivatedState: start clearing activated flag...........................................");
            clearActivatedState();

            /* 开始重新启动定时服务 */
            ElectricCardService.setServiceAlarm(ElectricCardApp.getAppContext(), true);
        }
    }

    public static void clearActivatedState()
    {
        /* 读取到的运营商信息清空 */
        writeProviderNameStatus(false);

        /* 开始的联网时间清空 */
        writeNetworkConnectedTime(0L);

        /* 持续联网时间清空 */
        PrefsUtils.getInstance().putLong(CONSISTENT_DURATION, 0L);

        /* 累积联网时间清空 */
        PrefsUtils.getInstance().putLong(ACCUMULATED_DURATION, 0L);

        /* 电子保卡激活标志位清空 */
        writeSimcardActivated(false);

        /* 收到的第一条运营商信息的时间清空 */
        writeSimcardDateTime(ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME);
    }
}
