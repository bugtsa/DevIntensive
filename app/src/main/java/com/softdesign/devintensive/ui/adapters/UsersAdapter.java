package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.LogUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private static final String TAG = ConstantManager.TAG_PREFIX + UsersAdapter.class.getSimpleName();
    private Context mContext;
    private List<User> mUsers;
    private List<String> mUsersRes;
    private UserViewHolder.CustomClickListener mCustomClickListener;

    public UsersAdapter(List<User> users, Context context, UserViewHolder.CustomClickListener customClickListener) {
        mUsers = users;
        mUsersRes = new ArrayList<>();
        mContext = context;
        mCustomClickListener = customClickListener;
    }

    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    /**
     * Bind данных
     *
     * @param holder   элемент
     * @param position позиция
     */
    @Override
    public void onBindViewHolder(final UsersAdapter.UserViewHolder holder, int position) {
        final User user = mUsers.get(position);
        final String userPhoto;
        if (user.getPhoto().isEmpty()) {
            userPhoto = "null";
            LogUtils.d(TAG, "onBindViewHolder: user with name " + user.getFullName() + "has empty photo");
        } else {
            userPhoto = user.getPhoto();
        }

        DataManager.getInstance().getPicasso()
                .load(userPhoto)
                .error(holder.mBugtsa)
                .placeholder(holder.mBugtsa)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.userPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        LogUtils.d(TAG, " load from cache");
                    }

                    @Override
                    public void onError() {
                        DataManager.getInstance().getPicasso()
                                .load(userPhoto)
                                .error(holder.mBugtsa)
                                .placeholder(holder.mBugtsa)
                                .fit()
                                .centerCrop()
                                .into(holder.userPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        LogUtils.d(TAG, " Could not fetch image");
                                    }
                                });
                    }
                });

        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));

        if (user.getBio() == null || user.getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        } else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getBio());
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected AspectRatioImageView userPhoto;
        protected TextView mFullName, mRating, mCodeLines, mProjects, mBio;
        protected Button mShowMore;
        protected Drawable mBugtsa;
        protected ImageView mLike;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            mListener = customClickListener;

            userPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo_iv);
            mFullName = (TextView) itemView.findViewById(R.id.user_full_name_tv);
            mRating = (TextView) itemView.findViewById(R.id.user_rating_tv);
            mCodeLines = (TextView) itemView.findViewById(R.id.user_lines_code_tv);
            mProjects = (TextView) itemView.findViewById(R.id.user_projects_tv);
            mBio = (TextView) itemView.findViewById(R.id.user_bio_tv);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);
            mLike = (ImageView) itemView.findViewById(R.id.like_btn);

            mBugtsa = userPhoto.getContext().getResources().getDrawable(R.drawable.user_bg);

            mShowMore.setOnClickListener(this);
            mLike.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        /**
         * Слушает собития прикосания к кнопке в RecyclerView
         */
        public interface CustomClickListener {
            void onUserItemClickListener(int position);
        }
    }
}
