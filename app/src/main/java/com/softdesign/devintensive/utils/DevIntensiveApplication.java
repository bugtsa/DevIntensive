package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.softdesign.devintensive.data.storage.models.DaoMaster;
import com.softdesign.devintensive.data.storage.models.DaoSession;

import org.greenrobot.greendao.database.Database;

public class DevIntensiveApplication extends Application {

    public static SharedPreferences sSharedPreferences;

    private static Context sContext;

    private static DaoSession sDaoSession;

    public static Context getContext() {
        return sContext;
    }

    /**
     * Создаёт SharedPreferences
     */
    @Override
    public void onCreate(){
        super.onCreate();
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sContext = getApplicationContext();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "devintenseve-db");
        Database db = helper.getWritableDb();
        sDaoSession = new DaoMaster(db).newSession();

        Stetho.initializeWithDefaults(this);
    }

    /**
     * Получает SharedPreferences
     * @return SharedPreferences
     */
    public static SharedPreferences getsSharedPreferences() {
        return sSharedPreferences;
    }

    public static DaoSession getDaoSession() {return sDaoSession;}
}
