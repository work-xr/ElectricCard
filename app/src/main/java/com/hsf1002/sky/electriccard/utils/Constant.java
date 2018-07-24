package com.hsf1002.sky.electriccard.utils;

/**
 * Created by hefeng on 18-7-17.
 */

public class Constant {
    /* 时间单位 */
    public static final int PHONE_DRATION_UNIT = 1000;

    /* 定时服务启动时间间隔, 默认一分钟 */
    public static final int SERVICE_STARTUP_INTERVAL = 60 * PHONE_DRATION_UNIT;

    /* GSM手机持续开机SIM卡联网时长 */
    public static final int GSM_PHONE_CONSISTENT_DURATION = 1 * 5* 60 * PHONE_DRATION_UNIT;
    /* GSM手机累积开机SIM卡联网时长 */
    public static final int GSM_PHONE_ACCUMULATED_DURATION = 1 * 5 * 60 * PHONE_DRATION_UNIT;
    /* 电信手机持续开机SIM卡联网时长 */
    public static final int TELECOM_PHONE_CONSISTENT_DURATION = 6 * 60 * 60 * PHONE_DRATION_UNIT;
    /* 电信手机累积开机SIM卡联网时长 */
    public static final int TELECOM_PHONE_ACCUMULATED_DURATION = 6 * 60 * 60 * PHONE_DRATION_UNIT;
    public static final String CONSISTENT_DURATION = "consistent_duration";
    public static final String ACCUMULATED_DURATION = "accumulated_duration";

    public static final String CHINA_MOBILE_NAME = "CMCC";
    public static final String CHINA_UNICOM_NAME = "UNICOM";
    public static final String CHINA_TELECOM_NAME = "TELECOM";

    /* 需要保存到sharepreferences中的状态,运营商是否已经读取 */
    public static final String PROVIDER_NAME_PRESET_STATE = "provider_name_preset";
    public static final String PROVIDER_ACCUMULATED_PRESET_STATE = "provider_accumulated_preset";
    public static final String PROVIDER_CONSISTENT_PRESET_STATE = "provider_consistent_set";

    /* 需要保存到NV中的状态,电子保卡是否已经激活, 为了测试方便, 先保存到sharepreferences  */
    public static final String ELECTRIC_CARD_ACTIVATED_STATE = "electric_card_activated_state";
    /* 需要保存到NV中的状态,电子保卡已经激活后收到的运营商信息的时间, 为了测试方便, 先保存到sharepreferences  */
    public static final String ELECTRIC_CARD_ACTIVATED_DATETIME = "electric_card_activated_datetime";

    public static final String CHINA_MOBILE_SMS_CENTER_PREFIX = "10086";
    public static final String CHINA_UNICOM_SMS_CENTER_PREFIX = "10010";
    public static final String CHINA_TELECOM_SMS_CENTER_PREFIX = "10000";
    public static final String CHINA_TELECOM_SMS_CENTER_2_PREFIX = "10001";

    public static final String SHARED_PREFERENCE_NAME = "electriccard";

    public static final String ELECTRIC_CARD_ACTIVATED_DEFAULT_TIME = "";//"20180101010101";


}
