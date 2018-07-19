package com.hsf1002.sky.electriccard.entity;

import android.util.Log;

import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_MOBILE_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_TELECOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_UNICOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.GSM_PHONE_ACCUMULATED_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.GSM_PHONE_CONSISTENT_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.TELECOM_PHONE_ACCUMULATED_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.TELECOM_PHONE_CONSISTENT_DURATION;


/**
 * Created by hefeng on 18-7-17.
 */

// 如果更换不同运营商的SIM卡,则重新统计数据
public class ProviderInfo {
    public static final String TAG = "ProviderInfo";

    private String name;    // cmcc, unicom or telecom

    private long accumulatedDuration;

    private long consistentDuration;

    public static ProviderInfo getInstance()
    {
        return Holder.instance;
    }

    private static final class Holder
    {
        private static final ProviderInfo instance = new ProviderInfo();
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
            setAccumulatedDuration(GSM_PHONE_ACCUMULATED_DURATION);
            setConsistentDuration(GSM_PHONE_CONSISTENT_DURATION);
        }
        else if (name.equals(CHINA_UNICOM_NAME))
        {
            setAccumulatedDuration(GSM_PHONE_ACCUMULATED_DURATION);
            setConsistentDuration(GSM_PHONE_CONSISTENT_DURATION);
        }
        else if (name.equals(CHINA_TELECOM_NAME))
        {
            setAccumulatedDuration(TELECOM_PHONE_ACCUMULATED_DURATION);
            setConsistentDuration(TELECOM_PHONE_CONSISTENT_DURATION);
        }
        else
        {
        }
        Log.d(TAG, "set duration success : " + toString());
    }

    public long getAccumulatedDuration() {
        return accumulatedDuration;
    }

    public void setAccumulatedDuration(long accumulatedDuration) {
        this.accumulatedDuration = accumulatedDuration;
    }

    public long getConsistentDuration() {
        return consistentDuration;
    }

    public void setConsistentDuration(long consistentDuration) {
        this.consistentDuration = consistentDuration;
    }

    @Override
    public String toString() {
        return "ProviderInfo{" +
                "name='" + name + '\'' +
                ", accumulatedDuration=" + accumulatedDuration +
                ", consistentDuration=" + consistentDuration +
                '}';
    }
}
