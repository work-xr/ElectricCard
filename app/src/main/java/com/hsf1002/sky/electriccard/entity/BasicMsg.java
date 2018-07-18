package com.hsf1002.sky.electriccard.entity;

import android.util.Log;

import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_MOBILE_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_TELECOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_UNICOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.GSM_PHONE_ONCE_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.GSM_PHONE_TOTAL_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.TELECOM_PHONE_ONCE_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.TELECOM_PHONE_TOTAL_DURATION;

/**
 * Created by hefeng on 18-7-17.
 */

// 如果更换不同运营商的SIM卡,则重新统计数据
public class BasicMsg {
    public static final String TAG = "BasicMsg";

    private String name;    // cmcc, unicom or telecom

    private long onceDuration;

    private long totalDuration;

    public static BasicMsg getInstance()
    {
        return Holder.instance;
    }

    private static final class Holder
    {
        private static final BasicMsg instance = new BasicMsg();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration()
    {
        if (name.equals(CHINA_MOBILE_NAME))
        {
            setOnceDuration(GSM_PHONE_ONCE_DURATION);
            setTotalDuration(GSM_PHONE_TOTAL_DURATION);
        }
        else if (name.equals(CHINA_UNICOM_NAME))
        {
            setOnceDuration(GSM_PHONE_ONCE_DURATION);
            setTotalDuration(GSM_PHONE_TOTAL_DURATION);
        }
        else if (name.equals(CHINA_TELECOM_NAME))
        {
            setOnceDuration(TELECOM_PHONE_ONCE_DURATION);
            setTotalDuration(TELECOM_PHONE_TOTAL_DURATION);
        }
        else
        {
            Log.d(TAG, "setName: ");
        }
    }

    public long getOnceDuration() {
        return onceDuration;
    }

    public void setOnceDuration(long onceDuration) {
        this.onceDuration = onceDuration;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }
}
