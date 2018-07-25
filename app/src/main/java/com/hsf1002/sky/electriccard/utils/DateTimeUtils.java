package com.hsf1002.sky.electriccard.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hefeng on 18-7-19.
 */

public class DateTimeUtils {
    private static final String TAG = "DateTimeUtils";

    public static String getFormatCurrentTime()
    {
        Date date= new Date();
        SimpleDateFormat smft=new SimpleDateFormat("yyyyMMddHHmmss");  // 20160426132222
        String nowTimeString = smft.format(date.getTime());

        return nowTimeString;
    }

    @Deprecated
    public static String getCurrentDateTime()
    {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH)+1;

        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        int minute = calendar.get(Calendar.MINUTE);

        int second = calendar.get(Calendar.SECOND);

        Log.d(TAG, "getCurrentDateTime: " + year +  month + day + hour + month + minute + second);

        return String.valueOf(year +  month + day + hour + month + minute + second);

        //return year + ":" + month + ":" + day + ":" + hour + ":" + month + ":" + minute + ":" + second;
    }

    @Deprecated
    public static String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }

    @Deprecated
    public static String convert(long t) {
        int s = (int) (t % 60);
        int m = (int) ((t / 60) % 60);
        int h = (int) ((t / 3600));

        return h + ":" + pad(m) + ":" + pad(s);
    }

    @Deprecated
    private class TimeDetail {
        int day;
        int hour;
        int minute;
        int second;
    }

    @Deprecated
    private class DateTimeDetail {
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;
    }

    private static final int oneMinuteSeconds = 60;
    private static final int oneHourSeconds = 60 * 60;
    private static final int oneDaySeconds = 24 * 60 * 60;

    //秒转换为天，时，分，秒
    @Deprecated
    TimeDetail getTimeDetailByInt(int time) {
        TimeDetail detail = new TimeDetail();

        detail.day = (int) (time / oneDaySeconds);
        time -= detail.day * oneDaySeconds;

        detail.hour = (int) (time / oneHourSeconds);
        time -= detail.hour * oneHourSeconds;

        detail.minute = (int) (time / oneMinuteSeconds);
        detail.second = time - detail.minute * oneMinuteSeconds;

        return detail;
    }

    //秒转换为年，月，日，时，分，秒
    @Deprecated
    DateTimeDetail getCurrentSystemDateTime(int time) {
        TimeDetail timeDetail = getTimeDetailByInt(time);
        DateTimeDetail dateTimeDetail = new DateTimeDetail();
        dateTimeDetail.year = 1970;
        dateTimeDetail.month = 1;
        dateTimeDetail.day = 1;
        dateTimeDetail.hour = timeDetail.hour + 8;//时区导致
        dateTimeDetail.minute = timeDetail.minute;
        dateTimeDetail.second = timeDetail.second;

        int daysLeft = timeDetail.day;
        int daysOfYear;
        while (daysLeft > 0) {
            if ((((dateTimeDetail.year % 4) == 0) && (dateTimeDetail.year % 100) != 0) || (dateTimeDetail.year % 400 == 0)) {
                daysOfYear = 366;
            } else {
                daysOfYear = 365;
            }

            if (daysLeft < daysOfYear) {
                int daysOfMonth = 0;

                while (daysLeft > 0) {
                    switch (dateTimeDetail.month) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 12:
                            daysOfMonth = 31;
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            daysOfMonth = 30;
                            break;
                        case 2:
                            daysOfMonth = (daysOfYear == 365) ? 28 : 29;
                        default:
                            break;
                    }

                    if (daysLeft < daysOfMonth) {
                        dateTimeDetail.day = daysLeft + 1;
                        daysLeft = 0;
                    } else {
                        daysLeft -= daysOfMonth;
                        dateTimeDetail.month++;
                    }
                }
            } else {
                daysLeft -= daysOfYear;
                dateTimeDetail.year++;
            }
        }

        return dateTimeDetail;
    }
}
