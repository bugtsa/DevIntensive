package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.utils.DevIntensiveApplication;
import com.squareup.picasso.Picasso;

import retrofit2.Call;

public class DataManager {

    private static DataManager INSTANCE = null;

    private RestService mRestService;

    private Context mContext;

    private PreferencesManager mPreferencesManager;

    private Picasso mPicasso;

    private static DaoSession mDaoSession;

    public DataManager() {
        mPreferencesManager = new PreferencesManager();
        mContext = DevIntensiveApplication.getContext();
        mRestService = ServiceGenerator.createService(RestService.class);
        mPicasso = new PicassoCache(mContext).getPicassoInstance();
        mDaoSession = DevIntensiveApplication.getDaoSession();
    }

    /**
     * Создаёт экземпляр класса
     *
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
     *
     * @return SharedPreferences
     */
    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Context getContext() {
        return mContext;
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

    //region ============Network=================

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }

    //end region

    //region ============Network=================

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    //end region
}
