package com.softdesign.devintensive.utils;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

public class SnackBarUtils {

    public static void show(CoordinatorLayout coordinatorLayout, final String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
