package com.softdesign.devintensive.utils;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.softdesign.devintensive.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtils {

    private List<TextInputLayout> mUserInfoTil;

    private List<EditText> mUserInfoViews;

    private Context context;

    private String errorMessage;

    private Window window;

    public ValidatorUtils(List<EditText> mUserInfoViews, List<TextInputLayout> mUserInfoTil, Context context, Window window) {
        this.mUserInfoViews = mUserInfoViews;
        this.mUserInfoTil = mUserInfoTil;

        this.context = context;
        this.window = window;
    }

    /**
     * Регулярное выражение телефонного номера
     */
    private final Pattern PHONE_NUMBER = Pattern.compile(
            "[+][0-9]{11,20}");

    /**
     * Регулярное выражение адресса электронной почты
     */
    private final Pattern EMAIL_ADDRESS = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{3,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{1,64}" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{1,25}"
    );

    /**
     * Проверяет адресс электронной почты по регулярному выражению
     *
     * @param email адресс электронной почты
     * @return true если валидный email, false - нет
     */
    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Проверяет телефонный номер по регулярному выражению
     *
     * @param phone телефонный номер
     * @return rue если валидный phone, false - нет
     */
    private boolean checkPhone(String phone) {
        return PHONE_NUMBER.matcher(phone).matches();
    }

    /**
     * Очищает TextInputLayout от выведенной ошибки
     *
     * @param indexFieldAtUserInfoViews индекс TextInputLayout
     */
    private void clearErrorAtTextInputLayout(int indexFieldAtUserInfoViews) {
        mUserInfoTil.get(indexFieldAtUserInfoViews).setError(null);
        mUserInfoTil.get(indexFieldAtUserInfoViews).setErrorEnabled(false);
    }

    /**
     * Отображает ошибку в TextInputLayout
     *
     * @param indexFieldAtUserInfoViews индекс TextInputLayout
     */
    private void showErrorAtField(int indexFieldAtUserInfoViews) {
        mUserInfoTil.get(indexFieldAtUserInfoViews).setErrorEnabled(true);
        mUserInfoTil.get(indexFieldAtUserInfoViews).setError(errorMessage);
        requestFocus(mUserInfoViews.get(indexFieldAtUserInfoViews));

    }

    /**
     * Проверяет на валидность элемент экрана для ввода текста
     *
     * @param indexFieldAtUserInfoViews индекс элемента экрана
     */
    public void isValidate(int indexFieldAtUserInfoViews) {
        String stringField = mUserInfoViews.get(indexFieldAtUserInfoViews).getText().toString().trim();
        boolean isValidateField = false;
        if (stringField.isEmpty()) {
            showErrorAtField(indexFieldAtUserInfoViews);
        }

        switch (indexFieldAtUserInfoViews) {
            case ConstantManager.USER_PHONE_INDEX_EDIT_TEXT:
                if (isValidPhoneNumber(stringField)) {
                    isValidateField = true;
                }
                errorMessage = context.getString(R.string.err_msg_phone);
                break;
            case ConstantManager.USER_EMAIL_INDEX_EDIT_TEXT:
                if (isValidEmail(stringField)) {
                    isValidateField = true;
                }
                errorMessage = context.getString(R.string.err_msg_email);
                break;
            case ConstantManager.USER_VK_INDEX_EDIT_TEXT:
                if (isValidUrl(stringField)) {
                    isValidateField = true;
                }
                errorMessage = context.getString(R.string.err_msg_vk);
                break;
            case ConstantManager.USER_GIT_INDEX_EDIT_TEXT:
                if (isValidUrl(stringField)) {
                    isValidateField = true;
                }
                errorMessage = context.getString(R.string.err_msg_git);
                break;
        }

        if (!isValidateField) {
            showErrorAtField(indexFieldAtUserInfoViews);
        } else {
            clearErrorAtTextInputLayout(indexFieldAtUserInfoViews);
        }
    }

    /**
     * Проверяет адресс электронной почты на валидность
     *
     * @param email адресс электронной почты
     * @return true если валиден, false - нет
     */
    private boolean isValidEmail(String email) {
        boolean isValidEmail = false;
        if (checkEmail(email)) {
            isValidEmail = true;
        } else {
            isValidEmail = false;
        }
        return isValidEmail;
    }

    /**
     * Проверяет телефонный номер на валидность
     *
     * @param phone введённый телефонный номен
     * @return true если валиден, false - нет
     */
    private boolean isValidPhoneNumber(String phone) {
        boolean isValidPhoneNumber = false;
        if (phone.isEmpty()) {
            isValidPhoneNumber = false;
        } else {
            if (checkPhone(phone)) {
                isValidPhoneNumber = true;
            } else {
                isValidPhoneNumber = false;
            }
        }
        return isValidPhoneNumber;
    }

    /**
     * Проверяет url на валидность
     *
     * @param url введённая строка адреса
     * @return true если валиден, false - нет
     */
    private boolean isValidUrl(String url) {
        boolean isValidUrl = false;
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        if (m.matches()) {
            isValidUrl = true;
        } else {
            isValidUrl = false;
        }
        return isValidUrl;
    }

    /**
     * Переводит фокус на элемент экрана
     *
     * @param view элемент экрана
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
