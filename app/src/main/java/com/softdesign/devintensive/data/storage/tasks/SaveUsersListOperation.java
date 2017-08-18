package com.softdesign.devintensive.data.storage.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redmadrobot.chronos.ChronosOperation;
import com.redmadrobot.chronos.ChronosOperationResult;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Like;
import com.softdesign.devintensive.data.storage.models.LikeDao;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;


public class SaveUsersListOperation extends ChronosOperation<String> {
    private Response<UserListRes> mResponse;

    public SaveUsersListOperation(Response<UserListRes> response) {
        mResponse = response;
    }

    @Nullable
    @Override
    public String run() {
        DataManager dataManager = DataManager.getInstance();
        UserDao userDao = dataManager.getDaoSession().getUserDao();
        LikeDao likeDao = dataManager.getDaoSession().getLikeDao();
        RepositoryDao repositoryDao = dataManager.getDaoSession().getRepositoryDao();
        List<Repository> allRepositories = new ArrayList<>();
        List<Like> allLikes = new ArrayList<>();
        List<User> allUsers = new ArrayList<>();

        for (UserListRes.UserData userRes : mResponse.body().getData()) {
            allRepositories.addAll(getRepoListFromUserRes(userRes));
            allLikes.addAll(getLikeListFromUserRes(userRes));
            allUsers.add(new User(userRes));
        }

        likeDao.deleteAll();
        repositoryDao.insertOrReplaceInTx(allRepositories);
        likeDao.insertOrReplaceInTx(allLikes);
        userDao.insertOrReplaceInTx(allUsers);

        return null;
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }
        return repositories;
    }

    private List<Like> getLikeListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();

        List<Like> likes = new ArrayList<>();
        for (String likedUserId : userData.getProfileValues().getLikesArray()) {
            likes.add(new Like(likedUserId, userId));

        }
        return likes;
    }

    @NonNull
    @Override
    public Class<Result> getResultClass() {
        return Result.class;
    }


    public static final class Result extends ChronosOperationResult<String> {

    }
}
