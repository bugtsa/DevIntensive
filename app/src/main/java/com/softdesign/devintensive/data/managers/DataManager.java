package com.softdesign.devintensive.data.managers;

public class DataManager {

    private static DataManager INSTANCE = null;

    public PreferencesManager mPreferencesManager;

    public DataManager() {
        mPreferencesManager = new PreferencesManager();
    }

    /**
     * Создаёт экземпляр класса
     * @return singleton класса
     */
    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    /**
     * Получает SharedPreferences
     * @return SharedPreferences
     */
    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }
}
