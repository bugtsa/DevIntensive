package com.softdesign.devintensive.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    /**
     * Вывод всплывающего сообщения об ошибке
     * @param message сообщение
     * @param context контекст
     */
    public static void showError(final String message, final Context context) {
        getToast(message, context).show();
    }

    /**
     * Вывод короткого всплывающего сообщения
     * @param message сообщение
     * @param context контекст
     */
    public static void showShortMessage(String message, Context context) {
        getToast(message, context, Toast.LENGTH_SHORT).show();
    }

    /**
     * Получает всплывающее сообщение
     * @param message сообщение
     * @param context контектс
     * @return всплывающее сообщение
     */
    private static Toast getToast(String message, Context context) {
        return getToast(message, context, Toast.LENGTH_LONG);
    }

    /**
     * Получает всплывающее сообщение
     * @param message сообщение
     * @param context контекст
     * @param length длина показа сообщения
     * @return всплывающее сообщение
     */
    private static Toast getToast(String message, Context context, int length) {
        return Toast.makeText(context, message, length);
    }
}
