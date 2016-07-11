package com.softdesign.devintensive.utils;

import android.util.Log;

public class LogUtils {
    public static final String TAG = "DevIntensive";
    private static boolean isDebug = true;

    /**
     * Выводит debug сообщение в LogCat
     * @param message сообщение
     */
    public static void d(String message) {
        if (isDebug) {
            Log.d(TAG, message);
        }
    }

    /**
     * Выводит debug сообщение в LogCat
     * @param tag тэг сообщения
     * @param message сообщение
     */
    public static void d(String tag, String message) {
        if (isDebug) {
            Log.d(tag, message);
        }
    }
}
