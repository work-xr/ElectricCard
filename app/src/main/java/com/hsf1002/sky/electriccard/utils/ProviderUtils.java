package com.hsf1002.sky.electriccard.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hsf1002.sky.electriccard.application.ElectricCardApp;
import com.hsf1002.sky.electriccard.entity.ProviderInfo;

import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_MOBILE_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_TELECOM_NAME;
import static com.hsf1002.sky.electriccard.utils.Constant.CHINA_UNICOM_NAME;
import static com.hsf1002.sky.electriccard.utils.NVUtils.writeProviderInfo;

/**
 * Created by hefeng on 18-7-19.
 */

public class ProviderUtils {
    private static final String TAG = "ProviderUtils";

    /*
* 电信 46003 46005 46011 46012 46050 46051 46052 46053 46054 46055 46056 46057 46058 46059 946003 946005 946011 946012 946050 946051 946052 946053 946054 946055 946056 946057 946058 946059
* 联通 46001 46006 46009 46010 46030 46031 46032 46033 46034 46035 46036 46037 46038 46039 946001 946006 946009 946010 946030 946031 946032 946033 946034 946035 946036 946037 946038 946039
* 移动 46000 46002 46007 46008*/
    public static void setOperatorInfo()
    {
        String providerName = "";
        TelephonyManager telephonyManager = (TelephonyManager) ElectricCardApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
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

            ProviderInfo.getInstance().setName(providerName);
            ProviderInfo.getInstance().setDuration();

            writeProviderInfo(true);
        } else {
            Log.d(TAG, "setOperatorInfo: providerName = null" );
        }
    }
}
