package com.softdesign.devintensive.data.storage.models;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig repositoryDaoConfig;

    private final UserDao userDao;
    private final RepositoryDao repositoryDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        repositoryDaoConfig = daoConfigMap.get(RepositoryDao.class).clone();
        repositoryDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        repositoryDao = new RepositoryDao(repositoryDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(Repository.class, repositoryDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        repositoryDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public RepositoryDao getRepositoryDao() {
        return repositoryDao;
    }

}
