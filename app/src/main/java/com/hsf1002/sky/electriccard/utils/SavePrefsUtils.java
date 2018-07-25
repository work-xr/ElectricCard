package com.hsf1002.sky.electriccard.utils;

import android.text.TextUtils;
import android.util.Log;

import com.hsf1002.sky.electriccard.application.ElectricCardApp;
import com.hsf1002.sky.electriccard.entity.ResultInfo;
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

    /* 一次网络连接成功的开始时间 */
    private static long sNetworkConnectedStartTime = 0;

    /* 读取持续联网时长 */
    public static long readSimcardConsistentOnlineDuration() {
        return PrefsUtils.getInstance().getLong(CONSISTENT_DURATION, 0L);
    }

    /* 写入持续联网时长, 重新开机也会累加 */
    public static void writeSimcardConsistentOnlineDuration(long offsetDuration) {
        long lastConsistentDuration = readSimcardConsistentOnlineDuration();
        PrefsUtils.getInstance().putLong(CONSISTENT_DURATION, lastConsistentDuration + offsetDuration);
        Log.d(TAG, "writeSimcardConsistentOnlineDuration: current consistent duration updated = " + (lastConsistentDuration + offsetDuration)/1000 + " seconds");
    }

    /* 读取累积联网时长 */
    public static long readSimcardAccumulatedOnlineDuration() {
        return PrefsUtils.getInstance().getLong(ACCUMULATED_DURATION, 0L);
    }

    /* 把每次网络连接持续的时长累加, 成为 本次开机累计的联网时长, 只记录本次开机联网开机, 关机会清空 */
    public static void writeSimcardAccumulatedOnlineDuration(long offsetDuration) {
        long lastAccumulatedDuration = readSimcardAccumulatedOnlineDuration();
        PrefsUtils.getInstance().putLong(ACCUMULATED_DURATION, lastAccumulatedDuration + offsetDuration);
        Log.d(TAG, "writeSimcardAccumulatedOnlineDuration: current accumulated duration updated = " + (lastAccumulatedDuration + offsetDuration)/1000 + " seconds");
    }

    /* 清空累积联网时长 */
    public static void resetSimcardAccumulatedOnlineDuration() {
        PrefsUtils.getInstance().putLong(ACCUMULATED_DURATION, 0L);
    }

    @Deprecated  // 已过期, 通过文件保存, 读取写入
    public static boolean readSimcardActivated() {
        return PrefsUtils.getInstance().getBoolean(ELECTRIC_CARD_ACTIVATED_STATE, false);
    }

    @Deprecated  // 已过期, 通过文件保存, 读取写入
    public static void writeSimcardActivated(boolean activated) {
        PrefsUtils.getInstance().putBoolean(ELECTRIC_CARD_ACTIVATED_STATE, activated);
    }

    @Deprecated  // 已过期, 通过文件保存, 读取写入
    public static String readSimcardDateTime() {
        return PrefsUtils.getInstance().getString(ELECTRIC_CARD_ACTIVATED_DATETIME, ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME);
    }

    @Deprecated  // 已过期, 通过文件保存, 读取写入
    public static void writeSimcardDateTime(String datatime) {
        PrefsUtils.getInstance().putString(ELECTRIC_CARD_ACTIVATED_DATETIME, datatime);
    }

    /* 读取每次断开网络重新联网的开始时间 */
    public static long readNetworkConnectedTime() {
        return sNetworkConnectedStartTime;
    }

    /* 更新每次断开网络重新联网的开始时间 */
    public static void writeNetworkConnectedTime(long networkConnectedTime) {
        sNetworkConnectedStartTime = networkConnectedTime;
    }

    /* 是否已经读取过了运营商的信息, 每次开机重新读取一次, 关机的时候清空 */
    public static String readProviderName()
    {
        return PrefsUtils.getInstance().getString(PROVIDER_NAME_PRESET_STATE, "");
    }

    /* 读取运营商预设累计时长 */
    public static Long readProviderAccumulatedDuration()
    {
        return PrefsUtils.getInstance().getLong(PROVIDER_ACCUMULATED_PRESET_STATE, 0L);
    }

    /* 读取运营商预设持续时长 */
    public static Long readProviderConsistentDuration()
    {
        return PrefsUtils.getInstance().getLong(PROVIDER_CONSISTENT_PRESET_STATE, 0L);
    }

    /* 给运营商 名称, 持续时长和累积时长一个默认值  */
    public static void resetProviderNameDuration()
    {
        PrefsUtils.getInstance().putString(PROVIDER_NAME_PRESET_STATE, "");
        PrefsUtils.getInstance().putLong(PROVIDER_ACCUMULATED_PRESET_STATE, 0L);
        PrefsUtils.getInstance().putLong(PROVIDER_CONSISTENT_PRESET_STATE, 0L);
    }

    /* 只有断开网络连接的时候,才写数据, 不做是否激活的判断 */
    public static void updateDurationFromReceiver() {
        long currentTime = System.currentTimeMillis();
        long startTime = readNetworkConnectedTime();
        long offset = currentTime - startTime;

        Log.d(TAG, "updateDurationFromReceiver: curentTime = " + currentTime + ", startTime = " + startTime);
        Log.d(TAG, "updateDurationFromReceiver: once online duration = " + offset / 1000 + " seconds");

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
        long currentAccumulatedDuration = readSimcardAccumulatedOnlineDuration();
        long ConsistentDuration = readSimcardConsistentOnlineDuration();
        Log.d(TAG, "updateDurationFromService: current AccumulatedDuration = " + currentAccumulatedDuration/1000 + " seconds");
        Log.d(TAG, "updateDurationFromService: current ConsistentDuration = " + ConsistentDuration/1000 + " seconds");

        long presetAccumulatedDuration = readProviderAccumulatedDuration();
        long presetConsistentDuration = readProviderConsistentDuration();

        try {
            if (presetAccumulatedDuration == 0 || presetConsistentDuration == 0) {
                Log.d(TAG, "updateDurationFromService: preset accumulated or consistent duration is 0....................");
                throw new Exception("tell me why ?.............................................................................");
            }
            else
            {
                Log.d(TAG, "updateDurationFromService: presetAccumulatedDuration = " + presetAccumulatedDuration/1000 + " seconds, " + " presetConsistentDuration = " + presetConsistentDuration/1000 + " seconds");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (currentAccumulatedDuration + offset > presetAccumulatedDuration) {
            if (ConsistentDuration + offset > presetConsistentDuration) {
                writeSimcardAccumulatedOnlineDuration(offset);
                writeSimcardConsistentOnlineDuration(offset);
                Log.d(TAG, "updateDurationFromService: write simcard activated flag true..................................");
                writeSimcardActivated(true);
                /* 将电子保卡激活标志写入文件, 此处只写入激活标志, 激活日期还是为空, 在读取短信成功的地方要重新写入, 来更新激活日期 */
                SaveFileUtils.getInstance().writeElectricCardActivated(new ResultInfo(true));
            }
        }
    }

    /* 处理清除电子保卡激活信息 */
    public static void handleClearActivatedState()
    {
        ResultInfo resultInfo = SaveFileUtils.getInstance().readElectricCardActivated();
        boolean isSimcardActivated = resultInfo.getFlag();
        String dateTime = resultInfo.getTime();

        Log.d(TAG, "handleClearActivatedState: isSimcardActivated = " + isSimcardActivated + ", dateTime = " + dateTime);

        /* 电子保卡没有激活 */
        if (!isSimcardActivated)
        {
            Log.d(TAG, "handleClearActivatedState: start clearing activated flag failed Simcard not activated...............");
        }
        /* 电子保卡已经激活, 但是没有收到运营商短信 */
        else if (TextUtils.isEmpty(dateTime))
        {
            Log.d(TAG, "handleClearActivatedState: start clearing activated flag failed Simcard dataTime empty .............");
        }
        else
        {
            Log.d(TAG, "handleClearActivatedState: start clearing activated flag success ...................................");
            /* 初始化所有变量 */
            clearActivatedState();

            /* 开始重新启动定时服务 */
            ElectricCardService.setServiceAlarm(ElectricCardApp.getAppContext(), true);
            Log.d(TAG, "handleClearActivatedState: after clearing activated flag service started............................");
        }
    }

    /* 清空电子保卡激活状态有关的数据 */
    public static void clearActivatedState()
    {
        /* 开始的联网时间清空 */
        writeNetworkConnectedTime(0L);

        /* 持续联网时间清空 */
        PrefsUtils.getInstance().putLong(CONSISTENT_DURATION, 0L);

        /* 累积联网时间清空 */
        PrefsUtils.getInstance().putLong(ACCUMULATED_DURATION, 0L);

        /* 读取到的运营商信息清空 */
        resetProviderNameDuration();

        /* 电子保卡激活标志位清空 */
        writeSimcardActivated(false);

        /* 收到的第一条运营商信息的时间清空 */
        writeSimcardDateTime(ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME);

        /* 保存电子保卡激活标志位的文件删除 */
        SaveFileUtils.getInstance().resetElectricCardActivated();
    }
}
