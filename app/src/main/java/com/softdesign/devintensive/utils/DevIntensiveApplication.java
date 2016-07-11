package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DevIntensiveApplication extends Application {

    public static SharedPreferences sSharedPreferences;

    /**
     * Создаёт SharedPreferences
     */
    @Override
    public void onCreate(){
        super.onCreate();
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Получает SharedPreferences
     * @return SharedPreferences
     */
    public static SharedPreferences getsSharedPreferences() {
        return sSharedPreferences;
    }
}
