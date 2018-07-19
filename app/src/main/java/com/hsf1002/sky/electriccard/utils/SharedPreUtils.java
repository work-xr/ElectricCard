package com.hsf1002.sky.electriccard.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hsf1002.sky.electriccard.application.ElectricCardApp;

/**
 * Created by hefeng on 18-7-18.
 */

public class SharedPreUtils {
    //private static final String SHARED_NAME = SHARED_PREFERENCE_NAME;
    private static SharedPreUtils sInstance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public SharedPreUtils() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ElectricCardApp.getAppContext());
        editor = sharedPreferences.edit();
    }

    public static SharedPreUtils getInstance()
    {
        if (sInstance == null)
        {
            synchronized (SharedPreUtils.class)
            {
                if (sInstance == null)
                {
                    sInstance = new SharedPreUtils();
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

    public void putLong(String key, long value)
    {
        editor.putLong(key, value);
        editor.apply();
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
