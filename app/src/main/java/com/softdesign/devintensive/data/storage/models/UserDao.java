package com.softdesign.devintensive.data.storage.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USERS".
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "USERS";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property RemoteId = new Property(1, String.class, "remoteId", false, "REMOTE_ID");
        public final static Property Photo = new Property(2, String.class, "photo", false, "PHOTO");
        public final static Property FullName = new Property(3, String.class, "fullName", false, "FULL_NAME");
        public final static Property SearchName = new Property(4, String.class, "searchName", false, "SEARCH_NAME");
        public final static Property Rait = new Property(5, int.class, "rait", false, "RAIT");
        public final static Property Rating = new Property(6, int.class, "rating", false, "RATING");
        public final static Property CodeLines = new Property(7, int.class, "codeLines", false, "CODE_LINES");
        public final static Property Projects = new Property(8, int.class, "projects", false, "PROJECTS");
        public final static Property Bio = new Property(9, String.class, "bio", false, "BIO");
    };

    private DaoSession daoSession;


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USERS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"REMOTE_ID\" TEXT NOT NULL UNIQUE ," + // 1: remoteId
                "\"PHOTO\" TEXT," + // 2: photo
                "\"FULL_NAME\" TEXT NOT NULL UNIQUE ," + // 3: fullName
                "\"SEARCH_NAME\" TEXT NOT NULL UNIQUE ," + // 4: searchName
                "\"RAIT\" INTEGER NOT NULL ," + // 5: rait
                "\"RATING\" INTEGER NOT NULL ," + // 6: rating
                "\"CODE_LINES\" INTEGER NOT NULL ," + // 7: codeLines
                "\"PROJECTS\" INTEGER NOT NULL ," + // 8: projects
                "\"BIO\" TEXT);"); // 9: bio
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USERS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getRemoteId());
 
        String photo = entity.getPhoto();
        if (photo != null) {
            stmt.bindString(3, photo);
        }
        stmt.bindString(4, entity.getFullName());
        stmt.bindString(5, entity.getSearchName());
        stmt.bindLong(6, entity.getRait());
        stmt.bindLong(7, entity.getRating());
        stmt.bindLong(8, entity.getCodeLines());
        stmt.bindLong(9, entity.getProjects());
 
        String bio = entity.getBio();
        if (bio != null) {
            stmt.bindString(10, bio);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getRemoteId());
 
        String photo = entity.getPhoto();
        if (photo != null) {
            stmt.bindString(3, photo);
        }
        stmt.bindString(4, entity.getFullName());
        stmt.bindString(5, entity.getSearchName());
        stmt.bindLong(6, entity.getRait());
        stmt.bindLong(7, entity.getRating());
        stmt.bindLong(8, entity.getCodeLines());
        stmt.bindLong(9, entity.getProjects());
 
        String bio = entity.getBio();
        if (bio != null) {
            stmt.bindString(10, bio);
        }
    }

    @Override
    protected final void attachEntity(User entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // remoteId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // photo
            cursor.getString(offset + 3), // fullName
            cursor.getString(offset + 4), // searchName
            cursor.getInt(offset + 5), // rait
            cursor.getInt(offset + 6), // rating
            cursor.getInt(offset + 7), // codeLines
            cursor.getInt(offset + 8), // projects
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // bio
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRemoteId(cursor.getString(offset + 1));
        entity.setPhoto(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFullName(cursor.getString(offset + 3));
        entity.setSearchName(cursor.getString(offset + 4));
        entity.setRait(cursor.getInt(offset + 5));
        entity.setRating(cursor.getInt(offset + 6));
        entity.setCodeLines(cursor.getInt(offset + 7));
        entity.setProjects(cursor.getInt(offset + 8));
        entity.setBio(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(User entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
