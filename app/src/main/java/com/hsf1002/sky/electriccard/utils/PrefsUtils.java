package com.hsf1002.sky.electriccard.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hsf1002.sky.electriccard.application.ElectricCardApp;

/**
 * Created by hefeng on 18-7-18.
 */

public class PrefsUtils {
    //private static final String SHARED_NAME = SHARED_PREFERENCE_NAME;
    private static PrefsUtils sInstance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public PrefsUtils() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ElectricCardApp.getAppContext());
        editor = sharedPreferences.edit();
    }

    public static PrefsUtils getInstance()
    {
        if (sInstance == null)
        {
            synchronized (PrefsUtils.class)
            {
                if (sInstance == null)
                {
                    sInstance = new PrefsUtils();
                }
            }
        }

        return  sInstance;
    }

    private void sharedPreRemove(String key) {
        editor.remove(key).apply();
    }

    public int getInt(String key, int value)
    {
        return sharedPreferences.getInt(key, value);
    }

    public void putInt(String key, int value)
    {
        editor.putInt(key, value);
        editor.apply();
    }

    public long getLong(String key, long value)
    {
        return sharedPreferences.getLong(key, value);
    }

    /* 持续时长和累积时长要同时写入, 用apply会出现偶尔无法写入的情况 */
    public void putLong(String key, long value)
    {
        boolean result;
        editor.putLong(key, value);
        //editor.apply();
        result = editor.commit();
        Log.d("electriccard", "putLong result = " + result);
    }

    public Boolean getBoolean(String key, Boolean value)
    {
        return sharedPreferences.getBoolean(key, value);
    }

    public void putBoolean(String key, Boolean value)
    {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getString(String key, String value)
    {
        return sharedPreferences.getString(key, value);
    }

    public void putString(String key, String value)
    {
        editor.putString(key, value);
        editor.apply();
    }
}
