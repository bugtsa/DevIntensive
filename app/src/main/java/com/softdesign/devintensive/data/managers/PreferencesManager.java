package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    private SharedPreferences mSharedPreferences;

    private static final String[] USER_FIELDS = {ConstantManager.USER_PHONE_KEY, ConstantManager.USER_EMAIL_KEY,
            ConstantManager.USER_VK_KEY, ConstantManager.USER_GIT_KEY, ConstantManager.USER_BIO_KEY};

    private static final String[] USER_VALUES = {
            ConstantManager.USER_RATING_VALUE,
            ConstantManager.USER_CODE_LINES_VALUE,
            ConstantManager.USER_PROJECT_VALUE
    };

    /**
     * Присваивает SharedPreferences в переменную класса
     */
    public PreferencesManager() {
        this.mSharedPreferences = DevIntensiveApplication.getsSharedPreferences();
    }

    /**
     * Сохраняет пользовательскую информацию в SharedPreferences
     *
     * @param userFields List строк полей пользовательской информации
     */
    public void saveUserProfileData(List<String> userFields) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int index = 0; index < USER_FIELDS.length; index++) {
            editor.putString(USER_FIELDS[index], userFields.get(index));
        }
        editor.apply();
    }

    /**
     * Загружает пользовательскую информацию из SharedPreferences в интерфейс приложения
     *
     * @return List строк полей пользовательской информации
     */
    public List<String> loadUserProfileData() {
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_EMAIL_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_VK_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_GIT_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_BIO_KEY, "null"));
        return userFields;
    }

    /**
     * Сохраняет изображение профиля пользователя
     *
     * @param uri изображения профиля пользователя
     */
    public void saveUserPhoto(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    /**
     * Загружает изображение профиля пользователя
     *
     * @return изображение профиля пользователя
     */
    public Uri loadUserPhoto() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY, "android.resource://com.softdesign.devintensive/drawable/user_profile"));
    }

    public void saveUserProfileValues(int[] userValues) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for (int i = 0; i < USER_VALUES.length; i++) {
            editor.putString(USER_VALUES[i], String.valueOf(userValues[i]));
        }
        editor.apply();
    }

    public List<String> loadUserProfileValues() {
        List<String> userValues = new ArrayList<>();
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_RATING_VALUE, "0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_CODE_LINES_VALUE, "0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_PROJECT_VALUE, "0"));

        return userValues;
    }

    public void saveUserAvatar(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserAvatar() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_KEY,
                "android.resource://com.softdesign.devintensive/drawable/avatar_bg"));
    }

    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    public String getAuthToken() {
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY, "");
    }

    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY, userId);
        editor.apply();
    }

    public String getUserId() {
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY, "");
    }

    public void saveUserFullName(String userFullName) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_FULL_NAME_KEY, userFullName);
        editor.apply();
    }

    public String getUserFullName() {
        return mSharedPreferences.getString(ConstantManager.USER_FULL_NAME_KEY, "");
    }

    public String getUserEmail() {
        return mSharedPreferences.getString(ConstantManager.EDIT_MAIL_KEY, "");
    }
}
