package com.hsf1002.sky.electriccard.entity;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hsf1002.sky.electriccard.application.ElectricCardApp;
import com.hsf1002.sky.electriccard.utils.PrefsUtils;

import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_MOBILE_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_MOBILE_SMS_CENTER_PREFIX;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_TELECOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_TELECOM_SMS_CENTER_2_PREFIX;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_TELECOM_SMS_CENTER_PREFIX;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_UNICOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_UNICOM_SMS_CENTER_PREFIX;
import static com.hsf1002.sky.electriccard.utils.Constant.GSM_PHONE_ACCUMULATED_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.GSM_PHONE_CONSISTENT_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.PROVIDER_ACCUMULATED_PRESET_STATE;
import static com.hsf1002.sky.electriccard.utils.Constant.PROVIDER_CONSISTENT_PRESET_STATE;
import static com.hsf1002.sky.electriccard.utils.Constant.PROVIDER_NAME_PRESET_STATE;
import static com.hsf1002.sky.electriccard.utils.Constant.TELECOM_PHONE_ACCUMULATED_DURATION;
import static com.hsf1002.sky.electriccard.utils.Constant.TELECOM_PHONE_CONSISTENT_DURATION;
import static com.hsf1002.sky.electriccard.utils.SavePrefsUtils.readProviderName;


/**
 * Created by hefeng on 18-7-17.
 */

// 如果更换不同运营商的SIM卡,则重新统计数据
public class ProviderInfo {
    public static final String TAG = "ProviderInfo";

    /*private String name;    // cmcc, unicom or telecom

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

    public boolean isFromProviderSmsCenter(String address)
    {
        boolean result = false;

        if (name.equals(CHINA_MOBILE_NAME))
        {
            if (address.contains(CHINA_MOBILE_SMS_CENTER_PREFIX))
            {
                result = true;
            }
        }
        else if (name.equals(CHINA_UNICOM_NAME))
        {
            if (address.contains(CHINA_UNICOM_SMS_CENTER_PREFIX))
            {
                result = true;
            }
        }
        else if (name.equals(CHINA_TELECOM_NAME))
        {
            if (address.contains(CHINA_TELECOM_SMS_CENTER_PREFIX) || address.contains(CHINA_TELECOM_SMS_CENTER_2_PREFIX))
            {
                result = true;
            }
        }

        return result;
    }
    */

    /*
* 电信 46003 46005 46011 46012 46050 46051 46052 46053 46054 46055 46056 46057 46058 46059 946003 946005 946011 946012 946050 946051 946052 946053 946054 946055 946056 946057 946058 946059
* 联通 46001 46006 46009 46010 46030 46031 46032 46033 46034 46035 46036 46037 46038 46039 946001 946006 946009 946010 946030 946031 946032 946033 946034 946035 946036 946037 946038 946039
* 移动 46000 46002 46007 46008*/
    public static void setProviderInfo()
    {
        String providerName = "";
        TelephonyManager telephonyManager = (TelephonyManager) ElectricCardApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        Log.d(TAG, "setProviderInfo: IMSI = " + IMSI);

        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                providerName = CHINA_MOBILE_NAME;
            } else if (IMSI.startsWith("46001")  || IMSI.startsWith("46006")) {
                providerName = CHINA_UNICOM_NAME;
            } else if (IMSI.startsWith("46003")) {
                providerName = CHINA_TELECOM_NAME;
            }
            Log.d(TAG, "setProviderInfo: providerName = " + providerName);

            //ProviderInfo.getInstance().setName(providerName);
            setNameDuration(providerName);

            //resetProviderNameDuration(true);
        } else {
            Log.d(TAG, "setProviderInfo: providerName = null" );
        }
    }

    private static void setNameDuration(String name)
    {
        if (name.equals(CHINA_MOBILE_NAME))
        {
            PrefsUtils.getInstance().putString(PROVIDER_NAME_PRESET_STATE, CHINA_MOBILE_NAME);
            PrefsUtils.getInstance().putLong(PROVIDER_ACCUMULATED_PRESET_STATE, GSM_PHONE_ACCUMULATED_DURATION);
            PrefsUtils.getInstance().putLong(PROVIDER_CONSISTENT_PRESET_STATE, GSM_PHONE_CONSISTENT_DURATION);
        }
        else if (name.equals(CHINA_UNICOM_NAME))
        {
            PrefsUtils.getInstance().putString(PROVIDER_NAME_PRESET_STATE, CHINA_UNICOM_NAME);
            PrefsUtils.getInstance().putLong(PROVIDER_ACCUMULATED_PRESET_STATE, GSM_PHONE_ACCUMULATED_DURATION);
            PrefsUtils.getInstance().putLong(PROVIDER_CONSISTENT_PRESET_STATE, GSM_PHONE_CONSISTENT_DURATION);
        }
        else if (name.equals(CHINA_TELECOM_NAME))
        {
            PrefsUtils.getInstance().putString(PROVIDER_NAME_PRESET_STATE, CHINA_TELECOM_NAME);
            PrefsUtils.getInstance().putLong(PROVIDER_ACCUMULATED_PRESET_STATE, TELECOM_PHONE_ACCUMULATED_DURATION);
            PrefsUtils.getInstance().putLong(PROVIDER_CONSISTENT_PRESET_STATE, TELECOM_PHONE_CONSISTENT_DURATION);
        }
        else
        {
        }
        Log.d(TAG, "set provider name and duration success ......................................... " );
    }

    public static boolean isFromProviderSmsCenter(String address)
    {
        boolean result = false;
        String name = readProviderName();

        if (name.equals(CHINA_MOBILE_NAME))
        {
            if (address.contains(CHINA_MOBILE_SMS_CENTER_PREFIX))
            {
                result = true;
            }
        }
        else if (name.equals(CHINA_UNICOM_NAME))
        {
            if (address.contains(CHINA_UNICOM_SMS_CENTER_PREFIX))
            {
                result = true;
            }
        }
        else if (name.equals(CHINA_TELECOM_NAME))
        {
            if (address.contains(CHINA_TELECOM_SMS_CENTER_PREFIX) || address.contains(CHINA_TELECOM_SMS_CENTER_2_PREFIX))
            {
                result = true;
            }
        }

        return result;
    }
}
