package com.softdesign.devintensive.utils;

import android.util.Log;

public class LogUtils {
    public static final String TAG = "DevIntensive";
    private static boolean isDebug = true;

    public static void d(String message) {
        if (isDebug) {
            Log.d(TAG, message);
        }
    }

    public static void d(String tag, String message) {
        if (isDebug) {
            Log.d(tag, message);
        }
    }
}
