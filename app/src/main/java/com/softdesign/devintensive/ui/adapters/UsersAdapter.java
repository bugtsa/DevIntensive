package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.Like;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.ui.activities.interfaces.CustomClickListener;
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
//    private List<User> mFilteredUsers;
//    private UserFilter mUserFilter;

    private CustomClickListener mCustomClickListener;

    public UsersAdapter(List<User> users, Context context, CustomClickListener customClickListener) {
        mContext = context;
        mUsers = users;
//        mFilteredUsers = new ArrayList<>();
//        mFilteredUsers.addAll(users);
//        mUserFilter = new UserFilter(UsersAdapter.this);
//        mUsersRes = new ArrayList<>();
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
        holder.mRait.setText(String.valueOf(user.getRait()));
        holder.mCodeLines.setText(String.valueOf(user.getCodeLines()));
        holder.mProjects.setText(String.valueOf(user.getProjects()));

        if (user.getBio() == null || user.getBio().isEmpty()) {
            holder.mBio.setVisibility(View.GONE);
        } else {
            holder.mBio.setVisibility(View.VISIBLE);
            holder.mBio.setText(user.getBio());
        }

        holder.mLikeQuantity.setText(String.valueOf(user.getRating() - user.getRait()));

        if (holder.isUserLiked(user)) {
            holder.mLike.setImageResource(R.drawable.ic_favorite_accent_24dp);
        } else {
            holder.mLike.setImageResource(R.drawable.ic_favorite_border_accent_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected AspectRatioImageView userPhoto;
        protected TextView mFullName, mRait, mLikeQuantity, mCodeLines, mProjects, mBio;
        protected Button mShowMore;
        protected Drawable mBugtsa;
        protected ImageView mLike;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);
            mListener = customClickListener;

            userPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo_iv);
            mFullName = (TextView) itemView.findViewById(R.id.user_full_name_tv);
            mRait = (TextView) itemView.findViewById(R.id.user_rait_tv);
            mCodeLines = (TextView) itemView.findViewById(R.id.user_lines_code_tv);
            mProjects = (TextView) itemView.findViewById(R.id.user_projects_tv);
            mBio = (TextView) itemView.findViewById(R.id.user_bio_tv);
            mShowMore = (Button) itemView.findViewById(R.id.more_info_btn);
            mLike = (ImageView) itemView.findViewById(R.id.like_btn_iv);
            mLikeQuantity =(TextView) itemView.findViewById(R.id.like_quantity);

            mBugtsa = userPhoto.getContext().getResources().getDrawable(R.drawable.user_bg);

            mShowMore.setOnClickListener(this);
            mLike.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener == null) {
                return;
            }

//            int position = mUsers.indexOf(mFilteredUsers.get(getAdapterPosition()));
            int position = mUsers.indexOf(mUsers.get(getAdapterPosition()));
            switch (v.getId()) {
                case R.id.more_info_btn:
                    mListener.onUserItemClickListener(ConstantManager.START_PROFILE_ACTIVITY_KEY, position);
                    break;
                case R.id.like_btn_iv:
                    String action;
//                    if(isUserLiked(mFilteredUsers.get(getAdapterPosition()))) {
                    if(isUserLiked(mUsers.get(getAdapterPosition()))) {
                        action = ConstantManager.UNLIKE_USER_KEY;
                    } else {
                        action = ConstantManager.LIKE_USER_KEY;
                    }
                    mListener.onUserItemClickListener(action, position);
                    break;
            }
        }

        private boolean isUserLiked(User user) {
            List<Like> likes = user.getLikes();
            String userId = DataManager.getInstance().getPreferencesManager().getUserId();

            for (Like like : likes) {
                if (like.getLikeUserId().equals(userId)) {
                    return true;
                }
            }
            return false;
        }
    }

//    public class UserFilter extends Filter {
//        private UsersAdapter mAdapter;
//
//        public UserFilter(UsersAdapter adapter) {
//            super();
//            mAdapter = adapter;
//        }
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            mFilteredUsers.clear();
//            FilterResults results = new FilterResults();
//            if (constraint.length() == 0) {
//                mFilteredUsers.addAll(mUsers);
//            } else {
//                String filterString = constraint.toString().toLowerCase().trim();
//                if (filterString.contains(" ")) {
//                    int spaceIndex = filterString.indexOf(" ");
//                    String filterPattern1 = filterString.substring(0, spaceIndex - 1);
//                    String filterPattern2 = filterString.substring(spaceIndex + 1, filterString.length());
//                    for (User user : mUsers) {
//                        String userName = user.getFullName().toLowerCase();
//                        if (userName.contains(filterPattern1)
//                                && userName.contains(filterPattern2)) {
//                            mFilteredUsers.add(user);
//                        }
//                    }
//                } else {
//                    for (User user : mUsers) {
//                        if (user.getFullName().toLowerCase().contains(filterString)) {
//                            mFilteredUsers.add(user);
//                        }
//                    }
//                }
//            }
//            results.values = mFilteredUsers;
//            results.count = mFilteredUsers.size();
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
//            mAdapter.notifyDataSetChanged();
//        }
//    }
}
