package com.softdesign.devintensive.data.storage.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Передаёт данные о пользователях
 */
public class UserDTO implements Parcelable {

    private String mRemoteId;
    private String mPhoto;
    private String mFullName;
    private String mRait;
    private String mRating;
    private String mCodeLines;
    private String mProjects;
    private String mBio;
    private List<String> mRepositories;
    private List<String> mLikes;

    public UserDTO(User userData) {
        List<String> repoLink = new ArrayList<>();
        List<String> likeIds = new ArrayList<>();

        mRemoteId = userData.getRemoteId();
        mPhoto = userData.getPhoto();
        mFullName = userData.getFullName();
        mRait = String.valueOf(userData.getRait());
        mRating = String.valueOf(userData.getRating());
        mCodeLines = String.valueOf(userData.getCodeLines());
        mProjects = String.valueOf(userData.getProjects());
        mBio = userData.getBio();

        for (Repository gitLink : userData.getRepositories()) {
            repoLink.add(gitLink.getRepositoryName());
        }
        mRepositories = repoLink;

        for (Like like : userData.getLikes()) {
            likeIds.add(like.getLikeUserId());
        }
        mLikes = likeIds;
    }

    protected UserDTO(Parcel in) {
        mRemoteId = in.readString();
        mPhoto = in.readString();
        mFullName = in.readString();
        mRait = in.readString();
        mRating = in.readString();
        mCodeLines = in.readString();
        mProjects = in.readString();
        mBio = in.readString();
        if (in.readByte() == 0x01) {
            mRepositories = new ArrayList<String>();
            in.readList(mRepositories, String.class.getClassLoader());
        } else {
            mRepositories = null;
        }
        if (in.readByte() == 0x01) {
            mLikes = new ArrayList<String>();
            in.readList(mLikes, String.class.getClassLoader());
        } else {
            mLikes = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRemoteId);
        dest.writeString(mPhoto);
        dest.writeString(mFullName);
        dest.writeString(mRait);
        dest.writeString(mRating);
        dest.writeString(mCodeLines);
        dest.writeString(mProjects);
        dest.writeString(mBio);
        if (mRepositories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mRepositories);
        }
        if (mLikes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mLikes);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserDTO> CREATOR = new Parcelable.Creator<UserDTO>() {
        @Override
        public UserDTO createFromParcel(Parcel in) {
            return new UserDTO(in);
        }

        @Override
        public UserDTO[] newArray(int size) {
            return new UserDTO[size];
        }
    };

    public String getRemoteId() { return mRemoteId; }

    public String getPhoto() {
        return mPhoto;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getRait() {return mRait;}

    public String getRating() {
        return mRating;
    }

    public String getCodeLines() {
        return mCodeLines;
    }

    public String getProjects() {
        return mProjects;
    }

    public String getBio() {
        return mBio;
    }

    public List<String> getRepositories() {
        return mRepositories;
    }

    public List<String> getLikes() {
        return mLikes;
    }
}
