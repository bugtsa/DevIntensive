package com.softdesign.devintensive.data.storage.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

import java.util.List;

public class LoadUsersListOperation extends ChronosOperation<List<User>> {
    @Nullable
    @Override
    public List<User> run() {
        return DevIntensiveApplication.getDaoSession().queryBuilder(User.class)
                .where(UserDao.Properties.CodeLines.gt(0))
                .orderDesc(UserDao.Properties.Rating)
                .build()
                .list();
    }

    @NonNull
    @Override
    public Class<Result> getResultClass() {
        return Result.class;
    }

    public static final class Result extends ChronosOperationResult<List<User>> {

    }
}
