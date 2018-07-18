package com.hsf1002.sky.electriccard.utils;

/**
 * Created by hefeng on 18-7-17.
 */

public class Constant {
    /* 需要保存到NV中的状态,运营商是否已经读取 */
    public static boolean IS_OPERATOR_NAME_SETED = false;
    /* 需要保存到NV中的状态,电子保卡是否已经激活 */
    public static boolean IS_ELECTRIC_CARD_ACTIVATED = false;

    /* 定时服务启动时间间隔, 默认一分钟 */
    public static final int SERVICE_STARTUP_INTERVAL = 1000;
    /* 时间单位 */
    public static final int PHONE_DRATION_UNIT = 1000;
    /* GSM手机持续开机SIM卡联网时长 */
    public static final int GSM_PHONE_TOTAL_DURATION = 24 * 60 * 60 * PHONE_DRATION_UNIT;
    /* GSM手机一次开机SIM卡联网时长 */
    public static final int GSM_PHONE_ONCE_DURATION = 6 * 60 * 60 * PHONE_DRATION_UNIT;
    /* 电信手机持续开机SIM卡联网时长 */
    public static final int TELECOM_PHONE_TOTAL_DURATION = 6 * 60 * 60 * PHONE_DRATION_UNIT;
    /* 电信手机一次开机SIM卡联网时长 */
    public static final int TELECOM_PHONE_ONCE_DURATION = 6 * 60 * 60 * PHONE_DRATION_UNIT;

    public static final String CHINA_MOBILE_NAME = "CMCC";
    public static final String CHINA_UNICOM_NAME = "UNICOM";
    public static final String CHINA_TELECOM_NAME = "TELECOM";

    public static final String CHINA_MOBILE_SMS_CENTER_PREFIX = "10086";
    public static final String CHINA_UNICOM_SMS_CENTER_PREFIX = "10010";
    public static final String CHINA_TELECOM_SMS_CENTER_PREFIX = "10000";
    public static final String CHINA_TELECOM_SMS_CENTER_2_PREFIX = "10001";
}
